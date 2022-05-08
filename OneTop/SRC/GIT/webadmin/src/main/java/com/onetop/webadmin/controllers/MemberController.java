package com.onetop.webadmin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.onetop.webadmin.mappers.MemberDao;
import com.onetop.webadmin.models.CoinInfo;
import com.onetop.webadmin.models.RSA;
import com.onetop.webadmin.models.adjustment.*;
import com.onetop.webadmin.models.authority.AdminMember;
import com.onetop.webadmin.models.member.*;
import com.onetop.webadmin.responses.member.*;
import com.onetop.webadmin.services.*;
import com.onetop.webadmin.services.excel.ExcelConstant;
import com.onetop.webadmin.services.excel.ExcelXlsxStreamingView;
import com.onetop.webadmin.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.expression.Numbers;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class MemberController {
    @Autowired
    WebMemberListService webMemberListService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private AdjustmentService adjustmentService;

    @Autowired
    private RSAUtil rsaUtil;

    @Autowired
    private Environment env;

    @Autowired
    MemberDao memberDao;

    @Autowired
    AuthEmailWriteService authEmailWriteService;


    private static final Logger log = LoggerFactory.getLogger(MemberController.class);


    @RequestMapping("mem{pageNo}")
    public String member(
            @PathVariable String pageNo,
            HttpSession session,
            HttpServletRequest request,
            Model model,
            @RequestParam(value = "sortName", defaultValue = "") String sortName,
            @RequestParam(value = "sortOrderBy", defaultValue = "") String sortOrderBy,
            @RequestParam(value = "search_listCount", defaultValue = "10", required = false) int search_listCount,
            @RequestParam(defaultValue = "1") int page
    ) {

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        model.addAttribute("member_proc", adminMember);

        switch (Integer.parseInt(pageNo)) {
            case 1:
                mem1(session, model, request, sortName, sortOrderBy, page, false, search_listCount);
                break;
            case 2:
                mem2(session, model, request, page, false, search_listCount);
                break;
            case 3:
                mem3(session, model, request);
                break;
            case 4:
                mem4(session, model, request);
                break;
            case 5:
                mem5(session, model, request, sortName, sortOrderBy, page, false, search_listCount);
                break;
            case 6:
                mem6(session, model, request, sortName, sortOrderBy, page, false, search_listCount);
                break;
            case 8:
                mem8(session, model, request, page, false, search_listCount);
                break;

            case 17://회원 로그인 정보
                mem17(session, model, request, page, search_listCount);
                break;
            case 18://스냅샷 리스트
                mem18(session, model, request, page, search_listCount);
                break;
            default:
                break;
        }

        model.addAttribute("page", page);
        model.addAttribute("search_listCount", search_listCount);

        return "member/mem" + pageNo;
    }

    /**
     * 회원검색 페이지
     */
    private void mem1(HttpSession session, Model model, HttpServletRequest request, String sortName, String sortOrderBy, int page, boolean isExcel, int search_listCount) {

        if (sortName == null || sortOrderBy == null || sortName.equals("") || sortOrderBy.equals("")) {

            sortName = "mb_reg_dt";
            sortOrderBy = "DESC";
        }

        request.setAttribute("sortName", sortName);
        request.setAttribute("sortOrderBy", sortOrderBy);

        String mb_id = request.getParameter("mb_id");
        model.addAttribute("mb_id", mb_id);

        String mb_referrer = request.getParameter("mb_referrer");
        model.addAttribute("mb_referrer", mb_referrer);

        ArrayList<String> stateList = new ArrayList<>();

        String state = request.getParameter("state");
        log.error("member serarch state : "+state);
        model.addAttribute("state", state);

        if (state != null && state.length() > 0) {
            stateList.add(state);
        }

        if (stateList.size() <= 0) {
            for (int i = 1; i <= 8; i++) {
                model.addAttribute("state", true);
            }
        }

        ArrayList<String> rankList = new ArrayList<>();

        for (int i = 1; i <= 6; i++) {

            String cert = request.getParameter("rank" + i);
            model.addAttribute("rank" + i, cert);

            if (cert != null && cert.length() > 0) {

                rankList.add(cert);
            }
        }

        if (rankList.size() <= 0) {
            model.addAttribute("rank1", true);
            rankList.add("S0");
            model.addAttribute("rank2", true);
            rankList.add("S1");
            model.addAttribute("rank3", true);
            rankList.add("S2");
            model.addAttribute("rank4", true);
            rankList.add("S3");
            model.addAttribute("rank5", true);
            rankList.add("S4");
            model.addAttribute("rank6", true);
            rankList.add("S5");
        }

        String reg_dt = request.getParameter("reg_dt");

        if (reg_dt == null || reg_dt.length() <= 0) {

            Date now = new Date();

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -3);

            Date date = new Date(cal.getTimeInMillis());
            reg_dt = String.format("%s - %s", Utils.GetDateStringFromFormat(cal.getTime(), "yyyy.MM.dd"),
                    Utils.GetDateStringFromFormat(now, "yyyy.MM.dd"));
        }

        String[] reg_dts = reg_dt.split("-");
        request.setAttribute("reg_dt", reg_dt);

        String mb_reg_dt_from = null;
        String mb_reg_dt_to = null;

        if (reg_dts.length > 0) {

            mb_reg_dt_from = reg_dts[0].trim();
            mb_reg_dt_to = reg_dts.length >= 2 ? reg_dts[1].trim() : reg_dts[0].trim();
        }

        String last_login_dt = request.getParameter("last_login_dt");

        String mb_last_login_dt_from = null;
        String mb_last_login_dt_to = null;

        request.setAttribute("last_login_dt", null);

        if (last_login_dt != null && last_login_dt.length() > 0) {

            String[] last_login_dts = last_login_dt.split("-");
            mb_last_login_dt_from = last_login_dts.length > 0 ? last_login_dts[0].trim() : null;
            mb_last_login_dt_to = last_login_dts.length > 1 ? last_login_dts[1].trim() : null;
            request.setAttribute("last_login_dt", last_login_dt);
        }

        if (isExcel) {
            WebMemberListResponse responseExcel = webMemberListService.GetWebMemberList(mb_id, mb_referrer, mb_reg_dt_from, mb_reg_dt_to, mb_last_login_dt_from, mb_last_login_dt_to, stateList, rankList, sortName, sortOrderBy, true, page, search_listCount);
            request.setAttribute("responseExcel", responseExcel);
            request.setAttribute("excelFileName", String.join("_", mb_reg_dt_from, mb_reg_dt_to));
        } else {

            WebMemberListResponse response = webMemberListService.GetWebMemberList(mb_id, mb_referrer, mb_reg_dt_from, mb_reg_dt_to, mb_last_login_dt_from, mb_last_login_dt_to, stateList, rankList, sortName, sortOrderBy, false, page, search_listCount);
            request.setAttribute("memberBalanceInfoSyncDate", adjustmentService.GetMbBalanceInfoSyncDate());
            request.setAttribute("response", response);
        }
    }

    @RequestMapping(value = "mem1/excelDown")
    public Object mem1ExcelDown(
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse httpServletResponse,
            Model model,
            @RequestParam(value = "sortName", defaultValue = "") String sortName,
            @RequestParam(value = "sortOrderBy", defaultValue = "") String sortOrderBy,
            @RequestParam(defaultValue = "1") int page
    ) {

        mem1(session, model, request, sortName, sortOrderBy, page, true, 20);
        Map<String, Object> map = new HashMap<>();

        //SET FILE NAME
        map.put(ExcelConstant.FILE_NAME, request.getAttribute("excelFileName") + "/security_list");

        MessageUtil messageUtil = new MessageUtil();
        String no = messageUtil.getMessage(session, "number_");
        String mb_id = messageUtil.getMessage(session, "mem1.table.thead.th.mb_id");
        String mb_email = messageUtil.getMessage(session, "mem1.table.thead.th.cert.email");
        String referrer = messageUtil.getMessage(session, "mem1.mod.sub.Referrer");

        String total_purchase = messageUtil.getMessage(session, "mem1.table.thead.th.total_purchase");
        String total_apply_bonus = messageUtil.getMessage(session, "mem1.table.thead.th.total_apply_bonus");

        String status = messageUtil.getMessage(session, "status");
        String reg_dt = messageUtil.getMessage(session, "bank1.user_search.join_date");

        //SET HEADER
        map.put(ExcelConstant.HEAD,
                new ArrayList() {
                    {
                        add(Arrays.asList(
                                no, mb_id, mb_email, referrer, total_purchase, total_apply_bonus, status, reg_dt

                        ));
                    }
                }
        );

        //SET CONTENT
        WebMemberListResponse response = (WebMemberListResponse) request.getAttribute("responseExcel");
        List<List<String>> excelBody = new ArrayList<>();
        for (WebMember member : response.getWebMemberList()) {
            List<String> tempList = new ArrayList<>();
            tempList.add(String.valueOf(member.getMb_no()));
            tempList.add(member.getMb_id());
            tempList.add(member.getMb_email());
            tempList.add(member.getMb_referer());


            if (member.getBuy_total_amount() != null) {
                double totalAsset = member.getBuy_total_amount().doubleValue();
                Numbers numbers = new Numbers(Locale.getDefault());
                String totalAssetText = numbers.formatDecimal(totalAsset, 1, "COMMA", 0, "POINT");

                tempList.add(String.valueOf(totalAssetText));
            } else {
                Numbers numbers = new Numbers(Locale.getDefault());
                String totalAssetText = numbers.formatDecimal(0, 1, "COMMA", 0, "POINT");
                tempList.add(String.valueOf(totalAssetText));
            }
            if (member.getApply_bonus() != null) {
                double totalAsset = member.getApply_bonus().doubleValue();
                Numbers numbers = new Numbers(Locale.getDefault());
                String totalAssetText = numbers.formatDecimal(totalAsset, 1, "COMMA", 0, "POINT");

                tempList.add(String.valueOf(totalAssetText));
            } else {
                Numbers numbers = new Numbers(Locale.getDefault());
                String totalAssetText = numbers.formatDecimal(0, 1, "COMMA", 0, "POINT");
                tempList.add(String.valueOf(totalAssetText));
            }

            tempList.add(GetResourceText(session, messageUtil, "status.", member.getMb_status()));
            if (member.getMb_reg_dt() != null) {
                tempList.add(Utils.GetDateStringFromFormat(member.getMb_reg_dt(), "yyyy-MM-dd HH:mm:ss"));
            } else {
                tempList.add("");
            }

            excelBody.add(tempList);
        }
        map.put(ExcelConstant.BODY, excelBody);

        return new ExcelXlsxStreamingView(map);
    }

    /**
     * 보안인증 페이지
     */
    private void mem2(HttpSession session, Model model, HttpServletRequest request, int page, Boolean isExcel, int search_listCount) {

        String mb_id = request.getParameter("mb_id");
        model.addAttribute("mb_id", mb_id);

        ArrayList<String> stateList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String state = request.getParameter("state" + i);
            model.addAttribute("state" + i, state);
            if (state != null && state.length() > 0) {
                stateList.add(state);
            }
        }

        if (stateList.size() <= 0) {
            for (int i = 0; i < 4; i++) {
                model.addAttribute("state" + i, true);
            }
        }

        ArrayList<String> certList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String cert = request.getParameter("cert" + i);
            model.addAttribute("cert" + i, cert);
            if (cert != null && cert.length() > 0) {
                certList.add(cert);
            }
        }

        if (certList.size() <= 0) {
            for (int i = 1; i <= 5; i++) {
                model.addAttribute("cert" + i, true);
            }
        }

        String request_dt = request.getParameter("request_dt");

        if (request_dt == null || request_dt.length() <= 0) {
            Date now = new Date();

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -3);

            request_dt = String.format("%s - %s", Utils.GetDateStringFromFormat(cal.getTime(), "yyyy.MM.dd"),
                    Utils.GetDateStringFromFormat(now, "yyyy.MM.dd"));
        }
        String[] request_dts = request_dt.split("-");
        request.setAttribute("request_dt", request_dt);

        String request_dt_from = null;
        String request_dt_to = null;
        if (request_dts.length > 0) {
            request_dt_from = request_dts[0].trim();
            request_dt_to = request_dts.length >= 2 ? request_dts[1].trim() : request_dts[0].trim();
        }


        String confirm_dt = request.getParameter("confirm_dt");
        request.setAttribute("confirm_dt", confirm_dt);

        String confirm_dt_from = null;
        String confirm_dt_to = null;

        if (confirm_dt != null && confirm_dt.length() > 0) {
            String[] proct_dts = confirm_dt.split("-");
            confirm_dt_from = proct_dts.length > 0 ? proct_dts[0].trim() : null;
            confirm_dt_to = proct_dts.length > 1 ? proct_dts[1].trim() : null;
            request.setAttribute("last_login_dt", confirm_dt);
        }

        WebMemberListResponse webMemberListExcelResponse = new WebMemberListResponse();
        WebMemberListResponse response = new WebMemberListResponse();
        if (isExcel) {
            try {
                webMemberListExcelResponse = webMemberListService.GetWebMemberCertList(mb_id, request_dt_from, request_dt_to, confirm_dt_from, confirm_dt_to, stateList, certList, true, page, search_listCount);
            } catch (Exception e) {

                e.printStackTrace();
            }
        } else {
            try {
                response = webMemberListService.GetWebMemberCertList(mb_id, request_dt_from, request_dt_to, confirm_dt_from, confirm_dt_to, stateList, certList, false, page, search_listCount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        request.setAttribute("excelFileName", String.join("_", request_dt_from, request_dt_to));
        request.setAttribute("response", response);
        request.setAttribute("responseExcel", webMemberListExcelResponse);
    }

    @RequestMapping(value = "mem2/excelDown")
    public Object mem2ExcelDown(
            HttpSession session,
            HttpServletRequest request,
            Model model,
            @RequestParam(defaultValue = "1") int page
    ) {
        mem2(session, model, request, page, true, 20);
        Map<String, Object> map = new HashMap<>();

        //SET FILE NAME
        map.put(ExcelConstant.FILE_NAME, request.getAttribute("excelFileName") + "/member_security_log");

        MessageUtil messageUtil = new MessageUtil();
        String no = messageUtil.getMessage(session, "number");
        String request_dt = messageUtil.getMessage(session, "mem2.table.thead.th.request_dt");
        String mb_id = messageUtil.getMessage(session, "mem2.table.thead.th.mb_id");
        String cert = messageUtil.getMessage(session, "security.authentication");
        String country = messageUtil.getMessage(session, "mem2.table.thead.th.country");
        String status = messageUtil.getMessage(session, "status");
        String processing_id = messageUtil.getMessage(session, "processing.id");
        String processing_dt = messageUtil.getMessage(session, "processing.date");

        //SET HEADER
        map.put(ExcelConstant.HEAD,
                new ArrayList() {
                    {
                        add(Arrays.asList(
                                no, request_dt, mb_id, cert, country, status, processing_id, processing_dt
                                )
                        );
                    }
                }
        );

        //SET CONTENT
        WebMemberListResponse response = (WebMemberListResponse) request.getAttribute("responseExcel");
        List<List<String>> excelBody = new ArrayList<>();
        for (WebMemberSecurityLog member : response.getWebMemberSecurityList()) {
            List<String> tempList = new ArrayList<>();
            tempList.add(String.valueOf(member.getLog_no()));
            tempList.add(member.getReg_dt().toString());
            tempList.add(member.getMb_id());
            tempList.add(String.valueOf(member.getSecurity_type()));
            if (member.getCountry_code() != null) {
                tempList.add(messageUtil.getMessage(session, "country.name." + member.getCountry_code()));
            } else {
                tempList.add("");
            }
            tempList.add(messageUtil.getMessage(session, "mem2.table.thead.th.status." + member.getState()));
            tempList.add(member.getConfirm_id());
            tempList.add(member.getConfirm_dt().toString());

            excelBody.add(tempList);
        }
        map.put(ExcelConstant.BODY, excelBody);

        return new ExcelXlsxStreamingView(map);
    }

    /**
     * 신분인증요청 가져오기
     *
     * @return
     */
    @RequestMapping(value = "mem2/idcard_request")
    @ResponseBody
    public Map<String, Object> getMemberIdCardRequest(HttpSession session, HttpServletRequest request)
            throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        String mb_id = request.getParameter("mb_id");
        String log_no = request.getParameter("log_no");

        MessageUtil messageUtil = new MessageUtil();

        Map<String, Object> tempMap = webMemberListService.GetWebMemberIdCardInfo(log_no, mb_id);

        if (tempMap.get("country_code") != null) {

            String key = tempMap.get("country_code").toString();
            tempMap.put("country_name", messageUtil.getMessage(session, "country.name." + key));
        }

        return tempMap;
    }

    /**
     * 회원 상세정보 인증 초기화
     *
     * @return
     */
    @RequestMapping(value = "mem2/clear_security")
    @ResponseBody
    public String clearSecurityLog(HttpSession session, HttpServletRequest request) {

        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        if (adminMember != null) {

            if (webMemberListService.ClearWebMemberCertInfo(adminMember, request).equals("success")) {

                String security_type = request.getParameter("security_type");

                if (security_type.equals("4")) security_type = "otp";
                else if (security_type.equals("3")) security_type = "id card";
                else if (security_type.equals("2")) security_type = "account";
                else if (security_type.equals("1")) security_type = "sms";
                else if (security_type.equals("0")) security_type = "email";
                else security_type = "unknown type : " + security_type;

                authorityService.insertAdminLog(request,
                        321,
                        String.format("clear certification mb_no : %s, mb_id : %s, type : %s",
                                request.getParameter("mb_no") != null ? request.getParameter("mb_no") : "null",
                                request.getParameter("mb_id") != null ? request.getParameter("mb_id") : "null",
                                security_type
                        ),
                        adminMember.getId()
                );

                return "success";
            }
        }

        return "fail";
    }

    /**
     * 신분인증 처리
     *
     * @return
     */
    @RequestMapping(value = "mem2/confirm_request")
    @ResponseBody
    public Map<String, Object> UpdateIdCardRequest(
            HttpServletRequest req,
            HttpSession session
    ) {

        Map<String, Object> resultMap = new HashMap<>();
        try {
            AdminMember adminMember = (AdminMember) session.getAttribute("member");

            if (adminMember != null) {
                int result = webMemberListService.UpdateWebMemberIdCardRequest(req);
                resultMap.put("resultCnt", result);

                if (result > 0) {
                    final String state = req.getParameter("state");

                    String address = env.getProperty("webadmin.mailsender.addr");
                    log.info("address : [" + address + "]");

                    // EMAIL 발송의 성공/실패는 어드민에서 알 필요 없음.
                    new Thread(() -> {
                        if (state != null && (state.equals("OK") || state.equals("REJECT"))) {
                            HashMap<String, String> response = Utils.SendHttpRequest(
                                    String.format("%s%s", address, "/ems/admin/confirm-idcard"),
                                    new JSONObject() {
                                        {
                                            put("mbId", req.getParameter("mb_id"));
                                            put("logNo", req.getParameter("log_no"));
                                            put("sendType", Integer.parseInt(req.getParameter("sendType")));
                                            put("smsMessage", req.getParameter("confirm_msg"));
                                        }
                                    },
                                    session.getAttribute("language").toString()
                            );

                            if (response.get("responseCode").equals("success")) {
                                try {
                                    log.info("success : " + response.get("resultData"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    log.info("fail : " + response.get("resultData"));
                                }
                            } else {
                                log.info("fail : " + response.get("resultData"));
                            }
                        }
                    }).start();

                    authorityService.insertAdminLog(req,
                            321,
                            String.format("id card update request, id = %s, state = %s",
                                    req.getParameter("mb_id") != null ? req.getParameter("mb_id") : "null",
                                    state != null ? state : "null"),
                            adminMember.getId()
                    );
                }

            } else {
                resultMap.put("resultCnt", -1);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return resultMap;
    }

    /**
     * 신분인증 여권번호 중복체크
     *
     * @return
     */
    @RequestMapping(value = "mem2/check_idcardno")
    @ResponseBody
    public int checkIdcardNo(@RequestParam(value = "idcardNo") String idcardNo) {

        int result = 0;

        try {

            result = webMemberListService.CheckWebMemberIdcardNoExist(idcardNo);
        } catch (Exception e) {

            log.error("Fail to check member idcardNo, msg = " + e.getMessage());
        }

        return result;
    }


    /**
     * 관리자 전체 지급/회수 페이지
     */
    private void mem3(HttpSession session, Model model, HttpServletRequest request) {

        List<CoinInfo> coinInfoList = null;

        try {
            coinInfoList = memberService.getCoinList();
            model.addAttribute("coinInfoList", coinInfoList);
        } catch (Exception e) {

            log.error("Fail to get member vip, msg = " + e.getMessage());
        }

        // 기존 RSA privatekey가 존재하면 제거
        PrivateKey privateKey = (PrivateKey) session.getAttribute("RSA_PRIVATE_KEY");
        if (privateKey != null) {

            session.removeAttribute("RSA_PRIVATE_KEY");
        }

        // 새로운 키 생성
        RSA rsa = rsaUtil.createRSA();
        model.addAttribute("modulus", rsa.getModulus());
        model.addAttribute("exponent", rsa.getExponent());
        session.setAttribute("RSA_PRIVATE_KEY", rsa.getPrivateKey());
    }



    /**
     * 관리자 개별 지급/회수 페이지
     */
    private void mem4(HttpSession session, Model model, HttpServletRequest request) {

        List<CoinInfo> coinInfoList = null;

        try {
            coinInfoList = memberService.getCoinList();
            model.addAttribute("coinInfoList", coinInfoList);
        } catch (Exception e) {

            log.error("Fail to get member vip, msg = " + e.getMessage());
        }

        // 기존 RSA privatekey가 존재하면 제거
        PrivateKey privateKey = (PrivateKey) session.getAttribute("RSA_PRIVATE_KEY");
        if (privateKey != null) {

            session.removeAttribute("RSA_PRIVATE_KEY");
        }

        // 새로운 키 생성
        RSA rsa = rsaUtil.createRSA();
        model.addAttribute("modulus", rsa.getModulus());
        model.addAttribute("exponent", rsa.getExponent());
        session.setAttribute("RSA_PRIVATE_KEY", rsa.getPrivateKey());
    }

    /**
     * 관리자 지급/회수 아이디 검색(탈퇴회원 제외)
     *
     * @return
     */
    @RequestMapping(value = "ajax/search/id")
    @ResponseBody
    public List<Map<String, Object>> ajaxSearchMember(@RequestParam(value = "member_id", defaultValue = "") String member_id) {

        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        Map<String, Object> resultMap = null;

        List<WebMember> webMemberList = null;
        List<MemberBalanceInfo> memberBalanceInfoList = null;
        List<CoinNowPrice> coinNowPriceList = null;
        BigDecimal totalPrice = BigDecimal.ZERO;

        try {
            webMemberList = webMemberListService.GetWebSeacherMemberList(member_id);

            for (int i = 0; i < webMemberList.size(); i++) {
                resultMap = new HashMap<String, Object>();
                totalPrice = BigDecimal.ZERO;

                WebMember member = webMemberList.get(i);
                resultMap.put("mb_no", member.getMb_no());
                resultMap.put("mb_id", member.getMb_id());
                
                /*
                // 코인들의 현재가격을 가져와서 맵에 넣어준다.
                Map<String, BigDecimal> coinNowPriceMap = new HashMap<String, BigDecimal>();
                coinNowPriceList = adjustmentService.GetAllCoinNowPrice();

                for (CoinNowPrice coinNowPrice : coinNowPriceList) {
                    coinNowPriceMap.put(coinNowPrice.getCoin_name(), coinNowPrice.getUsd_price());
                }                
                
                // 회원의 코인 정보들을 가져옴
                memberBalanceInfoList = adjustmentService.GetMemberBalanceInfo(member.getMb_no());

                for (MemberBalanceInfo memberBalanceInfo : memberBalanceInfoList) {
                    totalPrice = totalPrice.add(coinNowPriceMap.get(memberBalanceInfo.getCoin_name()).multiply(memberBalanceInfo.getAvail()));
                }
                */

                //resultMap.put("totalPrice", totalPrice);
                resultMap.put("totalPrice", member.getWithdraw_bonus());
                resultList.add(resultMap);
            }
        } catch (Exception e) {
            log.error("Fail to get member info list, msg = " + e.getMessage());
        }

        return resultList;
    }


    /**
     * 관리자 지급/회수 전체 회원 수 가져오기(탈퇴회원 제외)
     */
    @RequestMapping(value = "ajax/member/count")
    @ResponseBody
    public int ajaxMemberCount() {
        int result = 0;

        try {
            result = memberService.getNotDeleteMemberCount();
        } catch (Exception e) {
            log.error("Fail to get member count, msg = " + e.getMessage());
        }

        return result;
    }


    /**
     * 관리자 전체 지급/회수, CSV파일 읽어오기(탈퇴회원 제외)
     */
    @ResponseBody
    @RequestMapping(value = "ajax/read/csv")
    public Map<String, Object> ajaxReadCsv(
            @RequestParam(value = "uploadCsv") MultipartFile uploadCsv
    ) {
        HashMap<String, Object> resultMap = new HashMap<>();
        HashMap<String, String> select_member = new HashMap<>();

        double coin_total_amount = 0.0;

        try {
            HashMap<String, String> csvList = memberService.csvFileRead(uploadCsv);

            // 계정 존재하는지 체크 존재하지 않을때 마다 +1 씩
            int notExist = 0;

            for (HashMap.Entry<String, String> memberList : csvList.entrySet()) {
                if (webMemberListService.GetWebMember(memberList.getKey()) == null || webMemberListService.GetWebMember(memberList.getKey()).getMb_status().equals("DEL")) {
                    notExist++;
                }else{
                    select_member.put(memberList.getKey(), String.valueOf(Math.abs(Double.valueOf(memberList.getValue()))));
                    coin_total_amount+= Double.valueOf(select_member.get(memberList.getKey()));
                }
            }

            int total = csvList.size();
            int exist = total - notExist;

            resultMap.put("total", total);
            resultMap.put("exist", exist);
            resultMap.put("coin_total_amount", coin_total_amount);
            resultMap.put("select_member", JSONValue.toJSONString(select_member));
        } catch (Exception e) {
            log.error("Fail to csv read, msg = " + e.getMessage());
        }

        return resultMap;
    }


    /**
     * 관리자 전체 지급/회수 처리
     */
    @RequestMapping(value = "ajax/mem3/approval/proc")
    @ResponseBody
    public String ajaxMem3Approval(
            HttpSession session,
            HttpServletRequest request,
            @RequestParam(value = "pw", defaultValue = "") String pw,
            @RequestParam(value = "select_member", defaultValue = "") String select_member,
            @RequestParam(value = "content_type", defaultValue = "") int content_type,
            @RequestParam(value = "content", defaultValue = "") String content,
            @RequestParam(value = "pr_type", defaultValue = "") int pr_type,
            @RequestParam(value = "coin_name", defaultValue = "") String coin_name,
            @RequestParam(value = "coin_quantity", defaultValue = "") BigDecimal coin_quantity
    ) {

        String result = "fail";

        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        MessageUtil messageUtil = new MessageUtil();

        // 아이디로 관리자 정보를 가져옴 세션에는 비밀번호가 포함되어있지 않기때문에
        try {
            adminMember = authorityService.getAdminMemberById(adminMember.getId());
        } catch (Exception e) {
            log.error("Fail to get adminMember, id = " + adminMember.getId() + ", msg = " + e.getMessage());
        }

        // 입력된 비밀번호를 암호화하여 저장된 비밀번호와 같은지 체크
        PrivateKey privateKey = (PrivateKey) session.getAttribute("RSA_PRIVATE_KEY");
        if (privateKey == null) {
            return result;
        }

        try {
            String encPassword = rsaUtil.getDecryptPassword(privateKey, pw);

            // 관리자 비밀번호 체크
            if (!PasswordUtil.matchesPassword(adminMember.getPassword(), encPassword, adminMember.getId())) {
                result = messageUtil.getMessage(session, "password.vaild.message");
                return result;
            }

            HashMap<String, String> hashMap = new HashMap<>();
            int seletMemberCount = 0;
            if("all".equals(select_member)) {
                seletMemberCount = memberService.getMemberCount();
            } else {
                hashMap = new ObjectMapper().readValue(select_member, HashMap.class);
                seletMemberCount = hashMap.size();
            }

            // admin_payment_repayment 테이블에 등록후 index값으로 해당 사용자들 테이블에 추가
            PaymentRepayment paymentRepayment = new PaymentRepayment();
            paymentRepayment.setContent_type(content_type);
            paymentRepayment.setContent(content);
            paymentRepayment.setPr_type(pr_type);
            paymentRepayment.setCoin_name(coin_name);
            paymentRepayment.setCoin_quantity(coin_quantity);
            paymentRepayment.setComplete_count(0);
            paymentRepayment.setTotal_count(seletMemberCount);
            paymentRepayment.setAdmin_id(adminMember.getId());
            paymentRepayment.setIs_payed("I");

            int insertIndex = memberService.insertAdminPaymentRepayment(paymentRepayment);

            if("all".equals(select_member)) {
                List<String> idList = memberService.getNotDeleteMemberIdList();

                for (String id : idList) {
                    PaymentRepaymentMemberInfo paymentRepaymentMemberInfo = new PaymentRepaymentMemberInfo();
                    paymentRepaymentMemberInfo.setPr_no(insertIndex);
                    paymentRepaymentMemberInfo.setState("REQ");
                    paymentRepaymentMemberInfo.setCoin_quantity(coin_quantity);
                    paymentRepaymentMemberInfo.setMb_id(id);

                    memberService.insertAdminPaymentRepaymentMemberInfo(paymentRepaymentMemberInfo);
                }
            } else {
                hashMap.entrySet().forEach(entry->{
                    PaymentRepaymentMemberInfo paymentRepaymentMemberInfo = new PaymentRepaymentMemberInfo();
                    paymentRepaymentMemberInfo.setPr_no(insertIndex);
                    paymentRepaymentMemberInfo.setState("REQ");
                    paymentRepaymentMemberInfo.setMb_id(entry.getKey());
                    paymentRepaymentMemberInfo.setCoin_quantity(new BigDecimal(entry.getValue()));

                    try {
                        memberService.insertAdminPaymentRepaymentMemberInfo(paymentRepaymentMemberInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            memberService.updateAdminPaymentRepayment(insertIndex);

            result = "success";

            String log = "";

            if (pr_type == 0) log += "Admin Repayment request, (";
            else if (pr_type == 1) log += "Admin Payment request, (";

//            log += "coin_name = " + coin_name + ", coin_quantity = " + coin_quantity + ", content_type = " + content_type + ", content = " + content;

            authorityService.insertAdminLog(request, 341, log, adminMember.getId());
            adjustmentService.insertSyncHistory("admin_payment_repayment");
        } catch (Exception e) {
            log.error("Fail to decrypt password, otp, id = " + adminMember.getId() + ", msg = " + e.getMessage());
            e.printStackTrace();
            result = "not valid session. please refresh page";
        }

        log.info(result);
        return result;
    }


    /**
     * 관리자 개별 지급/회수 처리
     */
    @RequestMapping(value = "ajax/mem4/approval/proc")
    @ResponseBody
    public String ajaxMem4Approval(
            HttpSession session,
            HttpServletRequest request,
            @RequestParam(value = "pw", defaultValue = "") String pw,
            @RequestParam(value = "search_id", defaultValue = "") String search_id,
            @RequestParam(value = "content_type", defaultValue = "") int content_type,
            @RequestParam(value = "content", defaultValue = "") String content,
            @RequestParam(value = "pr_type", defaultValue = "") int pr_type,
            @RequestParam(value = "coin_name", defaultValue = "") String coin_name,
            @RequestParam(value = "coin_quantity", defaultValue = "") BigDecimal coin_quantity
    ) {

        String result = "fail";

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        MessageUtil messageUtil = new MessageUtil();

        // 아이디로 관리자 정보를 가져옴 세션에는 비밀번호가 포함되어있지 않기때문에
        try {
            adminMember = authorityService.getAdminMemberById(adminMember.getId());
        } catch (Exception e) {
            log.error("Fail to get adminMember, id = " + adminMember.getId() + ", msg = " + e.getMessage());
        }


        // 입력된 비밀번호를 암호화하여 저장된 비밀번호와 같은지 체크
        PrivateKey privateKey = (PrivateKey) session.getAttribute("RSA_PRIVATE_KEY");
        if (privateKey == null) {
            return result;
        }

        try {
            String encPassword = rsaUtil.getDecryptPassword(privateKey, pw);

            // 관리자 비밀번호 체크
            if (!PasswordUtil.matchesPassword(adminMember.getPassword(), encPassword, adminMember.getId())) {
                result = messageUtil.getMessage(session, "password.vaild.message");
                return result;
            }

            // admin_payment_repayment 테이블에 등록후 index값으로 해당 사용자들 테이블에 추가
            PaymentRepayment paymentRepayment = new PaymentRepayment();
            paymentRepayment.setContent_type(content_type);
            paymentRepayment.setContent(content);
            paymentRepayment.setPr_type(pr_type);
            paymentRepayment.setCoin_name(coin_name);
            paymentRepayment.setCoin_quantity(coin_quantity);
            paymentRepayment.setComplete_count(0);
            paymentRepayment.setTotal_count(1);
            paymentRepayment.setAdmin_id(adminMember.getId());
            paymentRepayment.setIs_payed("I");

            int insertIndex = memberService.insertAdminPaymentRepayment(paymentRepayment);


            PaymentRepaymentMemberInfo paymentRepaymentMemberInfo = new PaymentRepaymentMemberInfo();
            paymentRepaymentMemberInfo.setPr_no(insertIndex);
            paymentRepaymentMemberInfo.setState("REQ");
            paymentRepaymentMemberInfo.setMb_id(search_id);
            paymentRepaymentMemberInfo.setCoin_quantity(coin_quantity);

            memberService.insertAdminPaymentRepaymentMemberInfo(paymentRepaymentMemberInfo);
            memberService.updateAdminPaymentRepayment(insertIndex);

            result = "success";

            String log = "";

            if (pr_type == 0) log += "Admin Repayment request, (";
            else if (pr_type == 1) log += "Admin Payment request, (";

            log += "coin_name = " + coin_name + ", coin_quantity = " + coin_quantity + ", content_type = " + content_type + ", content = " + content;

            authorityService.insertAdminLog(request, 341, log, adminMember.getId());
            adjustmentService.insertSyncHistory("admin_payment_repayment");
        } catch (Exception e) {
            log.error("Fail to decrypt password, otp, id = " + adminMember.getId() + ", msg = " + e.getMessage());
            e.printStackTrace();
            result = "not valid session. please refresh page";
        }

//        log.info(result);
        return result;
    }




    /**
     * 회원자산 log 페이지
     */
    private void mem5(HttpSession session, Model model, HttpServletRequest request, String sortName, String sortOrderBy, int page, boolean isExcel, int search_listCount) {
        if (sortName == null || sortOrderBy == null || sortName.equals("") || sortOrderBy.equals("")) {
            sortName = "mb_reg_dt";
            sortOrderBy = "DESC";
        }
        request.setAttribute("sortName", sortName);
        request.setAttribute("sortOrderBy", sortOrderBy);

        String mb_id = request.getParameter("mb_id");
        model.addAttribute("mb_id", mb_id);

        String change_dt = request.getParameter("change_dt");

        if (change_dt == null || change_dt.length() <= 0) {
            Date now = new Date();
            change_dt = String.format("%s - %s", Utils.GetDateStringFromFormat(Utils.GetDateAddMonths(now, -1), "yyyy.MM.dd"),
                    Utils.GetDateStringFromFormat(now, "yyyy.MM.dd"));
        }
        String[] change_dts = change_dt.split("-");
        request.setAttribute("change_dt", change_dt);

        String change_dt_from = null;
        String change_dt_to = null;
        if (change_dts.length > 0) {
            change_dt_from = change_dts[0].trim();
            change_dt_to = change_dts.length >= 2 ? change_dts[1].trim() : change_dts[0].trim();
        }

        String action = request.getParameter("action");
        if (action == null || action.length() <= 0) {
            action = "ALL";
        }
        model.addAttribute("action", action);


        String coin = request.getParameter("coin");
        if (coin == null || coin.length() <= 0) {
            coin = "ALL";
        }
        model.addAttribute("coin", coin);

        String market = request.getParameter("market");
        if (market == null || market.length() <= 0) {
            market = "ALL";
        }
        model.addAttribute("market", market);

        String asset = request.getParameter("asset");
        if (asset == null || asset.length() <= 0) {
            asset = "ALL";
        }
        model.addAttribute("asset", asset);


        if (isExcel) {
            request.setAttribute("response", adjustmentService.GetMemberBalanceLogExcel(change_dt_from, change_dt_to, mb_id, action, coin, market, asset, isExcel));
        } else {
            model.addAttribute("response", adjustmentService.GetMemberBalanceLog(change_dt_from, change_dt_to, mb_id, action, coin, market, asset, page, search_listCount, isExcel));
            request.setAttribute("memberBalanceLogSyncDate", adjustmentService.GetMbBalanceLogSyncDate());
        }

        log.info("Mem5 Page");
    }

    @RequestMapping(value = "mem5/excelDown")
    public Object mem5ExcelDown(
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse httpServletResponse,
            Model model,
            @RequestParam(value = "sortName", defaultValue = "") String sortName,
            @RequestParam(value = "sortOrderBy", defaultValue = "") String sortOrderBy,
            @RequestParam(defaultValue = "1") int page
    ) {
        mem5(session, model, request, sortName, sortOrderBy, page, true, 20);
        Map<String, Object> map = new HashMap<>();

        String change_dt = request.getParameter("change_dt");

        change_dt = change_dt.replace("-", "_").replace(" ", "").replace(".", "");

        //SET FILE NAME
        map.put(ExcelConstant.FILE_NAME, change_dt + "_member_balance_log");

        MessageUtil messageUtil = new MessageUtil();
        String od_no = messageUtil.getMessage(session, "mem5.table.thead.th.od_no");
        String mb_id = messageUtil.getMessage(session, "member.id");
        String type = messageUtil.getMessage(session, "type");
        String coin_market = messageUtil.getMessage(session, "coin.market");
        String sign_price = messageUtil.getMessage(session, "price");
        String sign_amount = messageUtil.getMessage(session, "amount");
        String sign_balance = messageUtil.getMessage(session, "mem5.table.thead.th.sign_balance");
        String sign_fee = messageUtil.getMessage(session, "fee");
        String sign_adj_balance = messageUtil.getMessage(session, "mem5.table.thead.th.sign_adj_balance");
        String coin_asset = messageUtil.getMessage(session, "mem5.table.thead.th.coin_asset");
        String change_date = messageUtil.getMessage(session, "mem5.table.thead.th.change_date");
        String ip_addr = messageUtil.getMessage(session, "ip");
        String memo = messageUtil.getMessage(session, "remarks");

        //SET HEADER
        map.put(ExcelConstant.HEAD,
                new ArrayList() {
                    {
                        add(Arrays.asList(
                                od_no,
                                mb_id,
                                type,
                                coin_market,
                                sign_price, sign_price,
                                sign_amount, sign_amount,
                                sign_balance, sign_balance,
                                sign_fee, sign_fee,
                                sign_adj_balance, sign_adj_balance,
                                coin_asset, coin_asset,
                                change_date,
                                ip_addr,
                                memo
                                )
                        );
                    }
                }
        );

        /*
        //SET CONTENT
        MemberBalanceLogResponse response = (MemberBalanceLogResponse) request.getAttribute("response");
        List<List<String>> excelBody = new ArrayList<>();
        if (response != null) {
            for (MemberBalanceLog memberBalanceLog : response.getMemberBalanceLogList()) {
                List<String> tempList = new ArrayList<>();

                tempList.add(String.valueOf(memberBalanceLog.getOd_id()));
                tempList.add(memberBalanceLog.getMb_id());
                tempList.add(messageUtil.getMessage(session, "mem5.table.thead.th.type." + memberBalanceLog.getAction()));
                switch (memberBalanceLog.getAction()) {
                    case "bid":
                        tempList.add(memberBalanceLog.getMarket_name());
                        break;
                    case "ask":
                        tempList.add(memberBalanceLog.getMarket_name());
                        break;
                    default:
                        tempList.add("-");
                }

                if (memberBalanceLog.getSign_price() != null) {
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(memberBalanceLog.getSign_price(), 1, "COMMA", 4, "POINT")));
                } else {
                    tempList.add("");
                }
                switch (memberBalanceLog.getAction()) {
                    case "bid":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[1]);
                        break;
                    case "ask":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[1]);
                        break;
                    default:
                        tempList.add("-");
                }

                if (memberBalanceLog.getSign_amount() != null) {
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(memberBalanceLog.getSign_amount(), 1, "COMMA", 4, "POINT")));
                } else {
                    tempList.add("");
                }
                switch (memberBalanceLog.getAction()) {
                    case "bid":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[0]);
                        break;
                    case "ask":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[0]);
                        break;
                    default:
                        tempList.add(memberBalanceLog.getCoin_name() != null ? memberBalanceLog.getCoin_name() : "-");
                }

                if (memberBalanceLog.getSign_balance() != null) {
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(memberBalanceLog.getSign_balance(), 1, "COMMA", 4, "POINT")));
                } else {
                    tempList.add("");
                }
                switch (memberBalanceLog.getAction()) {
                    case "bid":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[1]);
                        break;
                    case "ask":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[1]);
                        break;
                    default:
                        tempList.add("-");
                }

                if (memberBalanceLog.getFee() != null) {
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(memberBalanceLog.getFee(), 1, "COMMA", 4, "POINT")));
                } else {
                    tempList.add("");
                }
                switch (memberBalanceLog.getAction()) {
                    case "bid":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[0]);
                        break;
                    case "ask":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[1]);
                        break;
                    default:
                        tempList.add(memberBalanceLog.getCoin_name() != null ? memberBalanceLog.getCoin_name() : "-");
                }


                if (memberBalanceLog.getAdjust_balance() != null) {
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(memberBalanceLog.getAdjust_balance(), 1, "COMMA", 4, "POINT")));
                } else {
                    tempList.add("");
                }
                switch (memberBalanceLog.getAction()) {
                    case "bid":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[0]);
                        break;
                    case "ask":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[1]);
                        break;
                    default:
                        tempList.add(memberBalanceLog.getCoin_name() != null ? memberBalanceLog.getCoin_name() : "-");
                }

                if (memberBalanceLog.getCoin_balance() != null) {
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(memberBalanceLog.getCoin_balance(), 1, "COMMA", 4, "POINT")));
                } else {
                    tempList.add("");
                }
                switch (memberBalanceLog.getAction()) {
                    case "bid":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[0]);
                        break;
                    case "ask":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[1]);
                        break;
                    default:
                        tempList.add(memberBalanceLog.getCoin_name() != null ? memberBalanceLog.getCoin_name() : "-");
                }

                if (memberBalanceLog.getChange_date() != null) {
                    tempList.add(memberBalanceLog.getChange_date().toString());
                } else {
                    tempList.add("");
                }

                if (memberBalanceLog.getIp_addr() != null) {
                    tempList.add(memberBalanceLog.getIp_addr());
                } else {
                    tempList.add("");
                }

                if (memberBalanceLog.getMemo() != null) {
                    tempList.add(memberBalanceLog.getMemo());
                } else {
                    tempList.add("");
                }

                excelBody.add(tempList);
            }
        }        

        map.put(ExcelConstant.BODY, excelBody);
        */

        return new ExcelXlsxStreamingView(map);
    }

    /**
     * 관리자 지급/회수 Log 페이지
     */
    private void mem6(HttpSession session, Model model, HttpServletRequest request, String sortName, String sortOrderBy, int page, boolean isExcel, int search_listCount) {

        if (sortName == null || sortOrderBy == null || sortName.equals("") || sortOrderBy.equals("")) {

            sortName = "reg_dt";
            sortOrderBy = "DESC";
        }

        log.info("Mem6 Page");

        String search_id = request.getParameter("search_id") == null ? "" : request.getParameter("search_id");
        String search_admin_id = request.getParameter("search_admin_id") == null ? "" : request.getParameter("search_admin_id");
        String search_coin = request.getParameter("search_coin") == null ? "all" : request.getParameter("search_coin");
        String search_pr_type = request.getParameter("search_pr_type") == null ? "all" : request.getParameter("search_pr_type");
        String search_content_type = request.getParameter("search_content_type") == null ? "all" : request.getParameter("search_content_type");
        String search_reg_dt = request.getParameter("search_reg_dt") == null ? "" : request.getParameter("search_reg_dt");

        // 등록일
        if (search_reg_dt == null || search_reg_dt.length() <= 0) {

            Date now = new Date();
            search_reg_dt = String.format("%s - %s", Utils.GetDateStringFromFormat(Utils.GetDateAddMonths(now, -1), "yyyy.MM.dd"), Utils.GetDateStringFromFormat(now, "yyyy.MM.dd"));
        }

        PaymentRepaymentResponse paymentRepaymentResponse = null;
        PaymentRepaymentResponse paymentRepaymentExcelResponse = null;

        String[] reg_dts = search_reg_dt.split("-");

        try {

            if (isExcel) {
                // 엑셀용
                paymentRepaymentExcelResponse = memberService.getPaymentRepaymentList(search_id, reg_dts[0], reg_dts[1], search_admin_id, search_coin, search_pr_type, search_content_type, sortName, sortOrderBy, true, page, search_listCount);

                // 엑셀용
                request.setAttribute("response", paymentRepaymentExcelResponse);
                request.setAttribute("excelFileName", String.join("_", reg_dts[0].trim(), reg_dts[1].trim()));
            } else {
                paymentRepaymentResponse = memberService.getPaymentRepaymentList(search_id, reg_dts[0], reg_dts[1], search_admin_id, search_coin, search_pr_type, search_content_type, sortName, sortOrderBy, false, page, search_listCount);
                model.addAttribute("paymentRepaymentResponse", paymentRepaymentResponse);
            }

        } catch (Exception e) {

            log.error("Fail to get payment/repayment log, msg = " + e.getMessage());
        }

        List<CoinInfo> coinInfoList = null;

        try {

            coinInfoList = memberService.getCoinList();
            model.addAttribute("coinInfoList", coinInfoList);
        } catch (Exception e) {

            log.error("Fail to get coin list, msg = " + e.getMessage());
        }

        request.setAttribute("adminPaymentRepaymentSyncDate", adjustmentService.GetAdminPaymentRepaymentSyncDate());
        model.addAttribute("search_id", search_id);
        model.addAttribute("search_admin_id", search_admin_id);
        model.addAttribute("search_coin", search_coin);
        model.addAttribute("search_pr_type", search_pr_type);
        model.addAttribute("search_content_type", search_content_type);
        model.addAttribute("search_reg_dt", search_reg_dt);
        model.addAttribute("sortName", sortName);
        model.addAttribute("sortOrderBy", sortOrderBy);
    }

    /**
     * 관리자 지급/회수 상세화면
     *
     * @return
     */
    @RequestMapping("mem6_mod")
    public String mem6_mod(
            HttpSession session,
            Model model,
            @RequestParam(value = "no") int no,
            @RequestParam(defaultValue = "1") int page
    ) {

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        model.addAttribute("member_proc", adminMember);

        PaymentRepaymentMemberInfoResponse paymentRepaymentMemberInfoResponse = null;

        try {

            paymentRepaymentMemberInfoResponse = memberService.getPaymentRepaymentMemberInfoList(no, false, page);
        } catch (Exception e) {

            log.error("Fail to get paymentRepaymentMemberInfo list, msg = " + e.getMessage());
        }

        PaymentRepayment paymentRepayment = null;

        try {

            paymentRepayment = memberService.getPaymentRepaymentByNo(no);
        } catch (Exception e) {

            log.error("Fail to get payment/repayment, msg = " + e.getMessage());
        }

        model.addAttribute("adminPaymentRepaymentMemberInfoSyncDate", adjustmentService.GetAdminPaymentRepaymentMemberInfoSyncDate());
        model.addAttribute("paymentRepaymentMemberInfoResponse", paymentRepaymentMemberInfoResponse);
        model.addAttribute("paymentRepayment", paymentRepayment);
        model.addAttribute("page", page);
        return "member/mem6_mod";
    }

    @RequestMapping(value = "mem6/excelDown")
    public Object mem6ExcelDown(
            HttpSession session,
            HttpServletRequest request,
            Model model,
            @RequestParam(value = "sortName", defaultValue = "") String sortName,
            @RequestParam(value = "sortOrderBy", defaultValue = "") String sortOrderBy,
            @RequestParam(defaultValue = "1") int page
    ) {

        mem6(session, model, request, sortName, sortOrderBy, page, true, 20);
        Map<String, Object> map = new HashMap<>();

        //SET FILE NAME
        map.put(ExcelConstant.FILE_NAME, request.getAttribute("excelFileName") + "/admin_payment_repayment");

        MessageUtil messageUtil = new MessageUtil();
        String no = messageUtil.getMessage(session, "number");
        String reg_dt = messageUtil.getMessage(session, "date");
        String content_type = messageUtil.getMessage(session, "tally");
        String pr_type = messageUtil.getMessage(session, "type");
        String coin_name = messageUtil.getMessage(session, "coin");
        String coin_quantity = messageUtil.getMessage(session, "mem6.table.th.coin_quantity");
        String complete_count = messageUtil.getMessage(session, "mem6.table.th.complete_count");
        String admin_id = messageUtil.getMessage(session, "admin.id");
        String mb_id = messageUtil.getMessage(session, "i18n.user.id");
        String content = messageUtil.getMessage(session, "mem6.table.th.content");

        String pr_type_0 = messageUtil.getMessage(session, "payments");
        String pr_type_1 = messageUtil.getMessage(session, "recovery");

        String type_0 = messageUtil.getMessage(session, "cost");
        String type_1 = messageUtil.getMessage(session, "sales");
        String type_2 = messageUtil.getMessage(session, "etc");
        String type_3 = messageUtil.getMessage(session, "mem6.table.th.type.3");

        //SET HEADER
        map.put(ExcelConstant.HEAD,
                new ArrayList() {
                    {
                        add(Arrays.asList(
                                no, reg_dt, content_type, pr_type, coin_name, coin_quantity, complete_count, admin_id, mb_id, content
                                )
                        );
                    }
                }
        );

        //SET CONTENT
        PaymentRepaymentResponse response = (PaymentRepaymentResponse) request.getAttribute("response");
        List<List<Object>> excelBody = new ArrayList<>();
        for (PaymentRepayment paymentRepayment : response.getPaymentRepaymentList()) {

            List<Object> tempList = new ArrayList<>();
            tempList.add(String.valueOf(paymentRepayment.getNo()));
            tempList.add(paymentRepayment.getReg_dt().toString());

            if (paymentRepayment.getContent_type() == 0) tempList.add(type_0);
            else if (paymentRepayment.getContent_type() == 1) tempList.add(type_1);
            else if (paymentRepayment.getContent_type() == 2) tempList.add(type_2);
            else if (paymentRepayment.getContent_type() == 3) tempList.add(type_3);

            if (paymentRepayment.getPr_type() == 0) tempList.add(pr_type_0);
            else if (paymentRepayment.getPr_type() == 1) tempList.add(pr_type_1);

            tempList.add(paymentRepayment.getCoin_name());
            tempList.add(paymentRepayment.getCoin_quantity().toString());
            tempList.add(paymentRepayment.getComplete_count() + "/" + paymentRepayment.getTotal_count());
            tempList.add(paymentRepayment.getAdmin_id());
            if(paymentRepayment.getTotal_count()>1){
                tempList.add(paymentRepayment.getMb_id()+"...");
            }else{
                tempList.add(paymentRepayment.getMb_id());
            }
            tempList.add(paymentRepayment.getContent());

            excelBody.add(tempList);
        }
        map.put(ExcelConstant.BODY, excelBody);

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        authorityService.insertAdminLog(request, 361, "Download excel admin payment/repayment log list", adminMember.getId());

        return new ExcelXlsxStreamingView(map);
    }

    @RequestMapping(value = "mem6_mod/excelDown")
    public Object mem6_modExcelDown(
            HttpSession session,
            HttpServletRequest request,
            @RequestParam(value = "no") int no,
            @RequestParam(value = "reg_dt") String reg_dt,
            @RequestParam(defaultValue = "1") int page
    ) {

        Map<String, Object> map = new HashMap<>();

        //SET FILE NAME
        map.put(ExcelConstant.FILE_NAME, reg_dt + "/admin_payment_repayment_member_info");

        MessageUtil messageUtil = new MessageUtil();
        String mb_id = messageUtil.getMessage(session, "mem6.table.th.id");
        String state = messageUtil.getMessage(session, "status");
        String content = messageUtil.getMessage(session, "mem6.mod.table.th.content");

        String state_req = messageUtil.getMessage(session, "waiting");
        String state_ok = messageUtil.getMessage(session, "status.complete");
        String state_err = messageUtil.getMessage(session, "mem6.mod.state.err");

        //SET HEADER
        map.put(ExcelConstant.HEAD,
                new ArrayList() {
                    {
                        add(Arrays.asList(
                                mb_id, state, content
                                )
                        );
                    }
                }
        );

        PaymentRepaymentMemberInfoResponse response = null;

        try {

            response = memberService.getPaymentRepaymentMemberInfoList(no, true, page);
        } catch (Exception e) {

            log.error("Fail to get payment/repayment member info log list, msg = " + e.getMessage());
        }

        //SET CONTENT
        List<List<String>> excelBody = new ArrayList<>();
        for (PaymentRepaymentMemberInfo paymentRepaymentMemberInfo : response.getPaymentRepaymentMemberInfoList()) {

            List<String> tempList = new ArrayList<>();
            tempList.add(paymentRepaymentMemberInfo.getMb_id());

            if (paymentRepaymentMemberInfo.getState().equals("REQ")) tempList.add(state_req);
            else if (paymentRepaymentMemberInfo.getState().equals("OK")) tempList.add(state_ok);
            else if (paymentRepaymentMemberInfo.getState().equals("ERR")) tempList.add(state_err);

            tempList.add(paymentRepaymentMemberInfo.getContent());

            excelBody.add(tempList);
        }

        map.put(ExcelConstant.BODY, excelBody);

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        authorityService.insertAdminLog(request, 362, "Download excel payment/repayment member info log list", adminMember.getId());
        return new ExcelXlsxStreamingView(map);
    }


    /** 비밀번호, 지갑비밀번호 이메일 재발송 요청 **/
    @RequestMapping("reset_password_email_resend")
    private @ResponseBody HashMap<String, String> reset_password_email_resend(HttpSession session, @RequestBody ResetMbPasswordRequest data) {
        HashMap<String, String> resultSet = new HashMap<>();

        try {
            // 세션에 관리자 정보가 없으면 요청 불허
            if (session.getAttribute("member") == null) {
                resultSet.put("resultCode", "-10");
                resultSet.put("resultMsg", "session is not valid");
                return resultSet;
            }

            AdminMember adminMember = (AdminMember) session.getAttribute("member");
            if (adminMember.getRole() == 1) {
                resultSet.put("resultCode", "-11");
                resultSet.put("resultMsg", "no access permission");
                return resultSet;
            }

            if (data.getMb_id() == null || data.getMb_id().length() <= 0) {
                resultSet.put("resultCode", "-12");
                resultSet.put("resultMsg", "not valid parameter");
                return resultSet;
            }

            /*
            int limitMinute = 1440;

            MemberEmailAuth memberEmailAuthList = memberDao.getMemberEmailAuth(data.getMb_id(), 2, limitMinute);

            if(ObjectUtils.isEmpty(memberEmailAuthList)==false) {
                int result = memberDao.setMemberEmailAuth(memberEmailAuthList.getAuth_key(), 0);

                if(result==1) {
                    resultSet.put("resultCode", "1");
                    resultSet.put("resultMsg", "You requested to resend");
                }else{
                    resultSet.put("resultCode", "-1");
                    resultSet.put("resultMsg", "Email resend failed");
                }
            }else{
                resultSet.put("resultCode", "-2");
                resultSet.put("resultMsg", "No email registered within "+limitMinute/60+" hours");
            }
            */

        } catch (Exception e) {
            e.printStackTrace();
            resultSet.put("resultCode", "-4");
            resultSet.put("resultMsg", "fail to rest request");
        }

        return resultSet;
    }


    /**
     * 비밀번호 초기화 요청
     *
     * @return
     */
    @RequestMapping("reset_password_request_proc")
    private @ResponseBody
    HashMap<String, String> reset_password_request_proc(HttpSession session, Model model, HttpServletRequest request,
                                                        @RequestBody ResetMbPasswordRequest data) {
        HashMap<String, String> resultSet = new HashMap<>();

        try {
            // 세션에 관리자 정보가 없으면 요청 불허
            if (session.getAttribute("member") == null) {
                resultSet.put("resultCode", "-10");
                resultSet.put("resultMsg", "session is not valid");
                return resultSet;
            }

            AdminMember adminMember = (AdminMember) session.getAttribute("member");
            if (adminMember.getRole() == 1) {
                resultSet.put("resultCode", "-11");
                resultSet.put("resultMsg", "no access permission");
                return resultSet;
            }

            if (data == null || data.getMb_id() == null || data.getMb_id().length() <= 0) {
                resultSet.put("resultCode", "-12");
                resultSet.put("resultMsg", "not valid parameter");
                return resultSet;
            }


            boolean result = authEmailWriteService.sendPasswordReset(data.getMb_id());
            if( !result ) {
                resultSet.put("resultCode", "-1");
                resultSet.put("resultMsg", "fail to reset");
            } else {
                resultSet.put("resultCode", "0000");
            }
            return resultSet;

        } catch (Exception e) {
            e.printStackTrace();
            resultSet.put("resultCode", "-4");
            resultSet.put("resultMsg", "fail to rest request");
        }
        return resultSet;
    }

    /**
     * 복구코드 발송 요청
     *
     * @return
     */
    @RequestMapping("send_recovery_code_request_proc")
    private @ResponseBody
    HashMap<String, String> send_recovery_code_request_proc(
            HttpSession session,
            @RequestBody ResetMbPasswordRequest data) {
        HashMap<String, String> resultSet = new HashMap<>();

        try {
            // 세션에 관리자 정보가 없으면 요청 불허
            if (session.getAttribute("member") == null) {
                resultSet.put("resultCode", "-10");
                resultSet.put("resultMsg", "session is not valid");
                return resultSet;
            }

            AdminMember adminMember = (AdminMember) session.getAttribute("member");
            if (adminMember.getRole() == 1) {
                resultSet.put("resultCode", "-11");
                resultSet.put("resultMsg", "no access permission");
                return resultSet;
            }

            if (data == null || data.getMb_id() == null || data.getMb_id().length() <= 0) {
                resultSet.put("resultCode", "-12");
                resultSet.put("resultMsg", "not valid parameter");
                return resultSet;
            }
            String address = env.getProperty("webadmin.mailsender.addr");
            log.info("address : [" + address + "]");

            HashMap<String, String> response = Utils.SendHttpRequest(
                    String.format("%s%s", address, "/ems/user/request-recoverycode"),
                    new JSONObject() {
                        {
                            put("mbId", data.getMb_id());
                            put("language", session.getAttribute("language").toString());
                        }
                    },
                    session.getAttribute("language").toString()
            );

            if (response.get("responseCode").equals("success")) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    TypeFactory factory = TypeFactory.defaultInstance();
                    MapType type = factory.constructMapType(HashMap.class, String.class, String.class);
                    resultSet = mapper.readValue(response.get("resultData"), type);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("fail : " + response.get("resultData"));

                    resultSet.put("resultCode", "-2");
                    resultSet.put("resultMsg", "fail to rest request");
                }
            } else {
                resultSet.put("resultCode", "-1");
                resultSet.put("resultMsg", "fail to rest request");
                log.info("fail : " + response.get("resultData"));
            }
            return resultSet;

        } catch (Exception e) {
            e.printStackTrace();
            resultSet.put("resultCode", "-4");
            resultSet.put("resultMsg", "fail to rest request");
        }
        return resultSet;
    }

    /**
     * 회원탍퇴 처리
     *
     * @return
     */
    @RequestMapping(value = "send_delete_member_request_proc")
    @ResponseBody
    public String send_delete_member_request_proc(
            HttpSession session,
            HttpServletRequest request,
            @RequestParam(value = "otpPw", defaultValue = "") String otpPw,
            @RequestParam(value = "pw", defaultValue = "") String pw,
            @RequestParam(value = "mb_id", defaultValue = "") String mb_id
    ) {

        String result = "fail";

        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        MessageUtil messageUtil = new MessageUtil();

        // 아이디로 관리자 정보를 가져옴 세션에는 비밀번호가 포함되어있지 않기때문에
        try {

            adminMember = authorityService.getAdminMemberById(adminMember.getId());
        } catch (Exception e) {

            log.error("Fail to get adminMember, id = " + adminMember.getId() + ", msg = " + e.getMessage());
        }

        // 입력된 비밀번호를 암호화하여 저장된 비밀번호와 같은지 체크
        PrivateKey privateKey = (PrivateKey) session.getAttribute("RSA_PRIVATE_KEY");

        if (privateKey == null) return result;

        try {

            String encPassword = rsaUtil.getDecryptPassword(privateKey, pw);
            // 관리자 비밀번호 체크
            if (!PasswordUtil.matchesPassword(adminMember.getPassword(), encPassword, adminMember.getId())) {

                result = messageUtil.getMessage(session, "password.vaild.message");
                return result;
            }

            String decOtpPw = rsaUtil.getDecryptPassword(privateKey, otpPw);
            String key = Utils.padRight(decOtpPw, '0', 16);

            log.info("decOtpPw : [" + decOtpPw + "]");
            log.info("key : [" + key + "]");

            JSONObject requestObject = new JSONObject();
            requestObject.put("senderId", adminMember.getOtp_key());
            requestObject.put("encData", AES256Cipher.AesEncodeKey(new JSONObject().toJSONString(), key, key));
            requestObject.put("mbId", mb_id);
            requestObject.put("adminId", adminMember.getId());

            String address = env.getProperty("webadmin.balancenoti.addr");
            log.info("address : [" + address + "]");

            HashMap<String, String> response = Utils.SendHttpRequest(
                    address + "/admin/member-withdrawal",
                    requestObject,
                    session.getAttribute("language").toString()
            );

            if (response.get("responseCode").equals("success")) {

                try {

                    ObjectMapper mapper = new ObjectMapper();
                    TypeFactory factory = TypeFactory.defaultInstance();
                    MapType type = factory.constructMapType(HashMap.class, String.class, String.class);
                    HashMap<String, String> resultSet = mapper.readValue(response.get("resultData"), type);

                    if (resultSet.get("resultCode") != null && resultSet.get("resultCode").equals("0000")) {

                        // OTP 코드 성공
                        result = "success";
                        String log = "Delete Member, mb_id = " + mb_id;
                        authorityService.insertAdminLog(request, 363, log, adminMember.getId());
                    } else {

                        // OTP Fail
                        result = String.format("[%s] %s", resultSet.get("resultCode"), resultSet.get("resultMsg"));
                    }
                } catch (Exception e) {

                    result = "fail to request rest api";
                    log.info("fail : " + response.get("resultData"));
                }
            } else {

                log.info("fail : " + response.get("resultData"));
                result = "fail to request rest api";
            }
        } catch (Exception e) {

            log.error("Fail to decrypt password, otp, id = " + adminMember.getId() + ", msg = " + e.getMessage());
            result = "not valid session. please refresh page";
        }

        log.info(result);
        return result;
    }

    /**
     * 블랙 컨슈머 등록
     *
     * @return
     */
    @RequestMapping(value = "mem8/blackconsumer/add")
    @ResponseBody
    public String mem8_blackconsumer_add(
            HttpSession session,
            HttpServletRequest request,
            @RequestParam(value = "mb_id", defaultValue = "") String mb_id,
            @RequestParam(value = "type", defaultValue = "") String type,
            @RequestParam(value = "type_memo", defaultValue = "") String type_memo,
            @RequestParam(value = "mb_blk_type", defaultValue = "") String mb_blk_type,
            @RequestParam(value = "end_dt", defaultValue = "") String end_dt
    ) {

        String result = "fail";

        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

        try {

            BlackConsumer blackConsumer = new BlackConsumer();
            blackConsumer.setMb_id(mb_id);
            blackConsumer.setType(Integer.parseInt(type));
            blackConsumer.setType_memo(type_memo);
            blackConsumer.setMb_blk_type(mb_blk_type);
            blackConsumer.setEnd_dt(sdf.parse(end_dt));
            blackConsumer.setReg_id(adminMember.getId());
            blackConsumer.setState(0);

            memberService.insertBlackConsumer(blackConsumer);
            result = "success";
        } catch (Exception e) {

            log.error("Fail to add black consumer, msg = " + e.getMessage());
        }

        return result;
    }

    /**
     * 블랙 컨슈머 해제
     *
     * @return
     */
    @RequestMapping(value = "mem8/blackconsumer/release")
    @ResponseBody
    public String mem8_blackconsumer_release(
            HttpSession session,
            HttpServletRequest request,
            @RequestParam(value = "mb_id", defaultValue = "") String mb_id,
            @RequestParam(value = "memo", defaultValue = "") String memo
    ) {

        String result = "fail";

        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        try {

            BlackConsumer blackConsumer = memberService.getBlackConsumerById(mb_id);
            blackConsumer.setMemo(memo);
            blackConsumer.setState(1);
            blackConsumer.setRel_id(adminMember.getId());

            memberService.releaseBlackConsumer(blackConsumer);
            result = "success";
        } catch (Exception e) {

            log.error("Fail to release black consumer, msg = " + e.getMessage());
        }

        return result;
    }

    /**
     * 블랙 컨슈머 페이지
     */
    private void mem8(HttpSession session, Model model, HttpServletRequest request, int page, boolean isExcel, int search_listCount) {

        log.info("Mem8 Page");

        String search_id = request.getParameter("search_id") == null ? "" : request.getParameter("search_id");
        String search_reg_dt = request.getParameter("search_reg_dt") == null ? "" : request.getParameter("search_reg_dt");
        String search_state = request.getParameter("search_state") == null ? "all" : request.getParameter("search_state");

        // 등록일
        if (search_reg_dt == null || search_reg_dt.length() <= 0) {

            Date now = new Date();
            search_reg_dt = String.format("%s - %s", Utils.GetDateStringFromFormat(Utils.GetDateAddMonths(now, -1), "yyyy.MM.dd"), Utils.GetDateStringFromFormat(now, "yyyy.MM.dd"));
        }

        BlackConsumerResponse blackConsumerResponse = null;

        String[] reg_dts = search_reg_dt.split("-");

        try {

            if (isExcel) {

                // 엑셀용
                blackConsumerResponse = memberService.blackConsumerList(search_state, reg_dts[0], reg_dts[1], search_id, true, page, search_listCount);
                request.setAttribute("response", blackConsumerResponse);
                request.setAttribute("excelFileName", String.join("_", reg_dts[0].trim(), reg_dts[1].trim()));
            } else {

                blackConsumerResponse = memberService.blackConsumerList(search_state, reg_dts[0], reg_dts[1], search_id, false, page, search_listCount);
                model.addAttribute("blackConsumerResponse", blackConsumerResponse);
            }
        } catch (Exception e) {

            log.error("Fail to get black consumer list log, msg = " + e.getMessage());
        }

        model.addAttribute("search_id", search_id);
        model.addAttribute("search_reg_dt", search_reg_dt);
        model.addAttribute("search_state", search_state);
    }

    @RequestMapping(value = "mem8/excelDown")
    public Object mem8ExcelDown(
            HttpSession session,
            HttpServletRequest request,
            Model model,
            @RequestParam(defaultValue = "1") int page
    ) {

        mem8(session, model, request, page, true, 20);
        Map<String, Object> map = new HashMap<>();

        //SET FILE NAME
        map.put(ExcelConstant.FILE_NAME, request.getAttribute("excelFileName") + "/member_blackconsumer_history");

        MessageUtil messageUtil = new MessageUtil();
        String no = messageUtil.getMessage(session, "number");
        String mb_id = messageUtil.getMessage(session, "member.id");
        String state = messageUtil.getMessage(session, "status");
        String type = messageUtil.getMessage(session, "mem4.content");
        String mb_blk_type = messageUtil.getMessage(session, "mem1.mod.sub.stop.memo");
        String reg_dt = messageUtil.getMessage(session, "sys7.start.dt");
        String end_dt = messageUtil.getMessage(session, "sys7.end.dt");
        String reg_id = messageUtil.getMessage(session, "mem1.mod.sub.process.id");
        String rel_id = messageUtil.getMessage(session, "mem1.mod.sub.release.id");
        String memo = messageUtil.getMessage(session, "mem1.mod.sub.release.memo");

        String state_0 = messageUtil.getMessage(session, "mem1.mod.blackconsumer.state.0");
        String state_1 = messageUtil.getMessage(session, "mem1.mod.blackconsumer.state.1");

        String type_0 = messageUtil.getMessage(session, "mem1.mod.blackconsumer.type.0");
        String type_1 = messageUtil.getMessage(session, "mem1.mod.blackconsumer.type.1");
        String type_2 = messageUtil.getMessage(session, "mem1.mod.blackconsumer.type.2");

        //SET HEADER
        map.put(ExcelConstant.HEAD,
                new ArrayList() {
                    {
                        add(Arrays.asList(
                                no, mb_id, state, type, mb_blk_type, reg_dt, end_dt, reg_id, rel_id, memo
                                )
                        );
                    }
                }
        );

        //SET CONTENT
        BlackConsumerResponse response = (BlackConsumerResponse) request.getAttribute("response");
        List<List<Object>> excelBody = new ArrayList<>();
        for (BlackConsumer blackConsumer : response.getBlackConsumerList()) {

            List<Object> tempList = new ArrayList<>();
            tempList.add(String.valueOf(blackConsumer.getNo()));
            tempList.add(blackConsumer.getMb_id());

            if (blackConsumer.getState() == 0) tempList.add(state_0);
            else if (blackConsumer.getState() == 1) tempList.add(state_1);

            if (blackConsumer.getType() == 0) tempList.add(type_0 + "(" + blackConsumer.getType_memo() + ")");
            else if (blackConsumer.getType() == 1) tempList.add(type_1 + "(" + blackConsumer.getType_memo() + ")");
            else if (blackConsumer.getType() == 2) tempList.add(type_2 + "(" + blackConsumer.getType_memo() + ")");

            tempList.add(blackConsumer.getMb_blk_type());
            tempList.add(blackConsumer.getReg_dt().toString());
            tempList.add(blackConsumer.getEnd_dt().toString());
            tempList.add(blackConsumer.getReg_id());
            tempList.add(blackConsumer.getRel_id());
            tempList.add(blackConsumer.getMemo());

            excelBody.add(tempList);
        }
        map.put(ExcelConstant.BODY, excelBody);

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        authorityService.insertAdminLog(request, 381, "Download excel admin blackconsumer log list", adminMember.getId());

        return new ExcelXlsxStreamingView(map);
    }


    /**
     * 회원 로그인 정보
     *
     * @param session
     * @param model
     * @param request
     * @param page
     * @param search_listCount
     */
    private void mem17(
            HttpSession session,
            Model model,
            HttpServletRequest request,
            int page,
            int search_listCount
    ) {

        log.info("[페이지 이동] => 로그인 히스토리 리스트");


        String mb_id = request.getParameter("mb_id") == null ? "" : request.getParameter("mb_id");
        String login_reg_ip = request.getParameter("login_reg_ip") == null ? "" : request.getParameter("login_reg_ip");
        String login_yn = request.getParameter("login_yn") == null ? "" : request.getParameter("login_yn");
        String search_reg_dt = request.getParameter("search_reg_dt") == null ? "" : request.getParameter("search_reg_dt");

        // 등록일
        if (search_reg_dt == null || search_reg_dt.length() <= 0) {
            Date now = new Date();
            search_reg_dt = String.format("%s - %s", Utils.GetDateStringFromFormat(Utils.GetDateAddMonths(now, -1), "yyyy.MM.dd"), Utils.GetDateStringFromFormat(now, "yyyy.MM.dd"));
        }
        String[] reg_dts = search_reg_dt.split("-");

        MemberLoginHisResponse memberLoginHisResponse = null;

        try {
            memberLoginHisResponse = memberService.memberLoginHisList(reg_dts[0], reg_dts[1], mb_id, login_reg_ip, login_yn, page, search_listCount);
            model.addAttribute("memberLoginHisResponse", memberLoginHisResponse);
        } catch (Exception e) {
            log.error("Fail to get login history List, msg = " + e.getMessage());
        }


        model.addAttribute("mb_id", mb_id);
        model.addAttribute("login_reg_ip", login_reg_ip);
        model.addAttribute("login_yn", login_yn);
        model.addAttribute("search_reg_dt", search_reg_dt);
    }


    /**
     * snap shot 정보
     *
     * @param session
     * @param model
     * @param request
     * @param page
     * @param search_listCount
     */
    private void mem18(
            HttpSession session,
            Model model,
            HttpServletRequest request,
            int page,
            int search_listCount
    ) {

        log.info("[페이지 이동] => 스냅샷 정보");
        ExtraPaySnapshotResponse extraPaySnapshotResponse = new ExtraPaySnapshotResponse();

        try {
            extraPaySnapshotResponse = adjustmentService.selectExtraPaySnapshotInfoList(page, search_listCount);
        } catch (Exception e) {
            log.error("Fail to get balance log snapshot time list, msg = " + e.getMessage());
        }

        model.addAttribute("extraPaySnapshotResponse", extraPaySnapshotResponse);
    }

    @RequestMapping(value = "mem1_mod/excelDown")
    public Object mem1ModExcelDown(
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse httpServletResponse,
            Model model,
            @RequestParam(value = "mb_id") String mb_id
    ) {

        Map<String, Object> map = new HashMap<>();

        //SET FILE NAME
        map.put(ExcelConstant.FILE_NAME, "member_balance_list_" + mb_id);

        MessageUtil messageUtil = new MessageUtil();
        String coin = messageUtil.getMessage(session, "coin");
        String coin_avail = messageUtil.getMessage(session, "bank2.mod.table.thead.th.coin_avail");
        String coin_in_use = messageUtil.getMessage(session, "bank2.mod.table.thead.th.coin_in_use");
        String coin_in_withdraw = messageUtil.getMessage(session, "bank2.mod.table.thead.th.coin_in_withdraw");
        String sum = messageUtil.getMessage(session, "sum");

        String exc_coin_total = messageUtil.getMessage(session, "bank2.mod.table.thead.th.exc_coin_total");
        String exc_coin_total_krw = messageUtil.getMessage(session, "bank2.mod.table.thead.th.exc_coin_total_krw");

        //SET HEADER
        map.put(ExcelConstant.HEAD,
                new ArrayList() {
                    {
                        add(Arrays.asList(
                                coin, coin_avail, coin_in_use, coin_in_withdraw, sum

                        ));
                    }
                }
        );

        List<MemberBalanceInfo> memberBalanceInfoList = adjustmentService.GetMemberBalanceInfo(0);

        MemberBalanceInfo totalBalanceInfo = new MemberBalanceInfo();
        for (MemberBalanceInfo memberBalanceInfo : memberBalanceInfoList) {

            totalBalanceInfo.setExc_avail(totalBalanceInfo.getExc_avail().add(memberBalanceInfo.getExc_avail()));
            totalBalanceInfo.setExc_in_withdraw(totalBalanceInfo.getExc_in_withdraw().add(memberBalanceInfo.getExc_in_withdraw()));
        }

        List<List<String>> excelBody = new ArrayList<>();

        for (MemberBalanceInfo memberBalanceInfo : memberBalanceInfoList) {

            List<String> tempList = new ArrayList<>();
            tempList.add(String.valueOf(memberBalanceInfo.getCoin_name()));
            tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(memberBalanceInfo.getAvail(), 1, "COMMA", 4, "POINT")));
            tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(memberBalanceInfo.getIn_withdraw(), 1, "COMMA", 4, "POINT")));

            excelBody.add(tempList);
        }

        List<String> lastTempList = new ArrayList<>();

        lastTempList.add(exc_coin_total_krw);
        lastTempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(totalBalanceInfo.getExc_avail(), 1, "COMMA", 0, "POINT")));
        lastTempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(totalBalanceInfo.getExc_in_withdraw(), 1, "COMMA", 0, "POINT")));
        excelBody.add(lastTempList);

        map.put(ExcelConstant.BODY, excelBody);

        return new ExcelXlsxStreamingView(map);
    }

    /**
     * 언어팩 가져오기
     *
     * @return
     */
    private String GetResourceText(HttpSession session, MessageUtil messageUtil, String src, String join) {

        try {

            return messageUtil.getMessage(session, src + join);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return join;
    }


    /**
     * snapshot 등록
     *
     * @param request
     * @param date
     * @param hour
     * @param minutes
     * @return
     */
    @RequestMapping(value = "ajax/mem18/proc")
    @ResponseBody
    public String ajaxMon7Proc(
            HttpServletRequest request,
            @RequestParam(value = "date") String date,
            @RequestParam(value = "hour") String hour,
            @RequestParam(value = "minutes") String minutes
    ) {
        String resultMsg = "error";
        try {
            String from = date + " " + hour + ":" + minutes + ":00";
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

            Date setDate = transFormat.parse(from);

            adjustmentService.insertExtraPaySnapshotInfo(setDate);
            resultMsg = "success";
        } catch (ParseException e) {
            log.error("Fail to  add snapshotTime detail, msg = " + e.getMessage());
        }
        return resultMsg;
    }

    /**
     * snapshot 지급 등록
     *
     * @param request
     * @param date
     * @param hour
     * @param minutes
     * @return
     */
    @RequestMapping(value = "ajax/mem18/payment")
    @ResponseBody
    public String ajaxMem18Payment(
            HttpServletRequest request,
            @RequestParam(value = "idxPayment") int idx,
            @RequestParam(value = "datePayment") String date,
            @RequestParam(value = "hourPayment") String hour,
            @RequestParam(value = "minutesPayment") String minutes
    ) {
        String resultMsg = "error";
        try {
            String from = date + " " + hour + ":" + minutes + ":00";
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

            Date setDate = transFormat.parse(from);

            adjustmentService.updateExtraPaySnapshotPaymentDate(idx, setDate);
            resultMsg = "success";
        } catch (ParseException e) {
            log.error("Fail to  add snapshotTime detail, msg = " + e.getMessage());
        }
        return resultMsg;
    }

    /**
     * snapshot 지급 취소
     *
     * @param snapshotIdx
     * @return
     */
    @RequestMapping(value = "ajax/mem18/cancel")
    @ResponseBody
    public String ajaxMem18Cancel(
            HttpServletRequest request,
            @RequestParam(value = "snapshotIdx") int snapshotIdx
    ) {
        String resultMsg = "error";
        if( adjustmentService.updateExtraPaySnapshotPaymentCancel(snapshotIdx) == 1 )
            resultMsg = "success";
        return resultMsg;
    }

    @RequestMapping(value = "mem18/excelDown")
    public Object mem18ExcelDown(
            HttpSession session,
            HttpServletRequest request,
            Model model,
            @RequestParam(value = "snapshotIdx") int snapshotIdx
    ) {
        Map<String, Object> map = new HashMap<>();

        //SET FILE NAME
        map.put(ExcelConstant.FILE_NAME, "idx_" + snapshotIdx + "_snapshot_history_list");

        String snapshot_idx = "스냅샷 번호";
        String mb_no = "유저번호";
        String mb_id = "아이디";        
        String purcahse = "구매포인트";                
        String r_t = "토탈보너스";
        String r_daily = "데일리보너스";
        String r_recommend = "후원보너스";
        String r_matching = "매칭보너스";
        String r_team = "팀보너스";
        String r_donate = "기부금";        
        String is_apply = "지급 여부";
        String up_dt = "적용 날짜";
        //SET HEADER
        map.put(ExcelConstant.HEAD,
                new ArrayList() {
                    {
                        add(Arrays.asList(
                                snapshot_idx, mb_no, mb_id, purcahse, r_t, r_daily, r_recommend, r_matching, r_team, r_donate, is_apply, up_dt
                                )
                        );
                    }
                }
        );

        //SET CONTENT
        List<ExtraPaySnapshotHistory> extraPaySnapshotHistoryList = adjustmentService.getExtraPaySnapshotHistoryList(snapshotIdx);
        List<List<Object>> excelBody = new ArrayList<>();

        for (ExtraPaySnapshotHistory extraPaySnapshotHistory : extraPaySnapshotHistoryList) {
            List<Object> tempList = new ArrayList<>();
            tempList.add(extraPaySnapshotHistory.getSnapshot_idx().toString());           
            tempList.add(extraPaySnapshotHistory.getMb_no().toString());
            tempList.add(extraPaySnapshotHistory.getMb_id());            
            String textPurchase = String.valueOf(extraPaySnapshotHistory.getPurchase());
            String textTotal = String.valueOf(extraPaySnapshotHistory.getTotal_bonus());
            String textDaily = String.valueOf(extraPaySnapshotHistory.getDaily_bonus());
            String textRecomd = String.valueOf(extraPaySnapshotHistory.getRecomd_bonus());
            String textMatching = String.valueOf(extraPaySnapshotHistory.getMatching_bonus());
            String textTeam = String.valueOf(extraPaySnapshotHistory.getTeam_bonus());
            String textDonation = String.valueOf(extraPaySnapshotHistory.getDonation_amount());            
            tempList.add(textPurchase);
            tempList.add(textTotal);
            tempList.add(textDaily);
            tempList.add(textRecomd);
            tempList.add(textMatching);
            tempList.add(textTeam);
            tempList.add(textDonation);
            tempList.add(extraPaySnapshotHistory.getIs_apply());
            tempList.add(Utils.GetDateStringFromFormat(extraPaySnapshotHistory.getUp_dt(), "yyyy-MM-dd HH:mm:ss"));

            excelBody.add(tempList);
        }
        map.put(ExcelConstant.BODY, excelBody);

        return new ExcelXlsxStreamingView(map);
    }


    /**
     * 수당 상한액 설정
     */
    @RequestMapping("change_extrapay_limit_proc")
    @ResponseBody
    private String change_extrapay_limit_request_proc(
            @RequestParam(value = "mb_no") int mb_no,
            @RequestParam(value = "extrapay_limit") int extrapay_limit
    ) {
        String result = "fail";
        try {
            memberService.modifyMemberExtrapayLimit(mb_no, extrapay_limit);
            result = "success";
        } catch (Exception e) {
            log.error("Fail to update member change_extrapay_limit, msg = " + e.getMessage());
        }
        return result;
    }


    /**
     * 출금가능금액 설정
     */
    @RequestMapping("change_withdraw_limit_proc")
    @ResponseBody
    private String change_withdraw_limit_request_proc(
            @RequestParam(value = "mb_no") int mb_no,
            @RequestParam(value = "withdraw_limit") int withdraw_limit
    ) {
        String result = "fail";
        try {
            memberService.modifyMemberWithdrawLimit(mb_no, withdraw_limit);
            result = "success";
        } catch (Exception e) {
            log.error("Fail to update member change_withdraw_limit, msg = " + e.getMessage());
        }
        return result;
    }


    /**
     * 회원 이메일 변경
     **/
    @RequestMapping("change_email_proc")
    @ResponseBody
    private String change_email_proc(
            @RequestParam(value = "mb_no") int mb_no,
            @RequestParam(value = "mb_email") String mb_email
    ) {
        String result = "fail";
        try {
            memberService.updateMemberEmail(mb_no, mb_email);
            result = "success";
        } catch (Exception e) {
            log.error("Fail to update member email Address, msg = " + e.getMessage());
        }
        return result;
    }


    /**
     * 인증 이메일 등록
     **/
    @RequestMapping("auth_email_save")
    @ResponseBody
    private String auth_email_save(
            @RequestParam(value = "mb_id") String mb_id
    ) {
        String result = "fail";
        try {
            authEmailWriteService.authEmailWrite(mb_id, 1);
            result = "success";
        } catch (Exception e) {
            log.error("Fail Auth email, msg = " + e.getMessage());
        }
        return result;
    }



    @RequestMapping("member_sub_tree")
    @ResponseBody
    private List<MemberSubDownTree> member_sub_tree(
            @RequestParam(value = "mb_position") int mb_position,
            @RequestParam(value = "sort_info") String  sort_info
    ) {
        return  memberDao.getMemberSubDownTree(mb_position, sort_info, "LST");
    }


    @RequestMapping(value = "mem1/membertreeexcelDown")
    public Object memberTreeExcelDown(
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse httpServletResponse,
            Model model,
            @RequestParam(value = "sortName", defaultValue = "") String sortName,
            @RequestParam(value = "sortOrderBy", defaultValue = "") String sortOrderBy,
            @RequestParam(defaultValue = "1") int page
    ) {

        System.out.println(sortName);

        mem1(session, model, request, sortName, sortOrderBy, page, true, 20);
        Map<String, Object> map = new HashMap<>();

        //SET FILE NAME
        map.put(ExcelConstant.FILE_NAME, request.getAttribute("excelFileName") + "/memberTreeExcelDown");

        MessageUtil messageUtil = new MessageUtil();
        String mb_position = messageUtil.getMessage(session, "mb.position");
        String mb_no = messageUtil.getMessage(session, "keyValue");
        String mb_parent_id = messageUtil.getMessage(session, "parent.id");
        String mb_id = messageUtil.getMessage(session, "login.text.id");
        String coin_name = messageUtil.getMessage(session, "coin");
        String avail = messageUtil.getMessage(session, "possession.amount");
        String basic_pay = messageUtil.getMessage(session, "basicextrapay");
        String sponsor_pay = messageUtil.getMessage(session, "sponsorextrapay");
        String encourage_pay = messageUtil.getMessage(session, "encourageextrapay");
        String rank_pay = messageUtil.getMessage(session, "rankextrapay");
        String apply_pay = messageUtil.getMessage(session, "mem1.mod.apply_pay");

        //SET HEADER
        map.put(ExcelConstant.HEAD,
                new ArrayList() {
                    {
                        add(Arrays.asList(
                                mb_position, mb_no, mb_parent_id, mb_id, coin_name, avail, basic_pay, sponsor_pay, encourage_pay, rank_pay, apply_pay

                        ));
                    }
                }
        );

        //SET CONTENT
        WebMemberListResponse response = (WebMemberListResponse) request.getAttribute("responseExcel");
        List<List<String>> excelBody = new ArrayList<>();
        int mbp=0;
        for (WebMemberBalance webMemberBalance : memberDao.getMemberTreeExcel(sortName)) {
            if(mbp==0) {
                mbp=webMemberBalance.getMb_position();
            }
            List<String> tempList = new ArrayList<>();
            tempList.add(String.valueOf(webMemberBalance.getMb_position()-mbp));
            tempList.add(String.valueOf(webMemberBalance.getMb_no()));
            tempList.add(String.valueOf(webMemberBalance.getMb_parent_id()));
            tempList.add(webMemberBalance.getMb_id());
            tempList.add(webMemberBalance.getCoin_name());
            tempList.add(String.valueOf(webMemberBalance.getAvail()));
            tempList.add(String.valueOf(webMemberBalance.getBasic_pay()));
            tempList.add(String.valueOf(webMemberBalance.getSponsor_pay()));
            tempList.add(String.valueOf(webMemberBalance.getEncourage_pay()));
            tempList.add(String.valueOf(webMemberBalance.getRank_pay()));
            tempList.add(String.valueOf(webMemberBalance.getApply_pay()));
            excelBody.add(tempList);
        }
        map.put(ExcelConstant.BODY, excelBody);

        return new ExcelXlsxStreamingView(map);
    }


    @RequestMapping("member_full_tree")
    @ResponseBody
    public JSONObject getMemberListService(
            @RequestParam(value = "sortName", defaultValue = "") String sortName,
            @RequestParam(value = "pdata", defaultValue = "") int pdata) {

        ArrayList<WebMemberBalance> webMemberBalance = (ArrayList)memberDao.getMemberTreeView(sortName, pdata);

        int max_step = 100;
        int step_pre = 0;
        int step_now = 0;

        ArrayList<JSONObject> mainObjectArrayList = new ArrayList<>(max_step);
        ArrayList<JSONArray> jArrayList = new ArrayList<>(max_step);

        for(int i=0;i<max_step;i++){
            mainObjectArrayList.add(i, new JSONObject());
        }

        for(int i=0;i<max_step;i++){
            jArrayList.add(i, new JSONArray());
        }

        for(WebMemberBalance item : webMemberBalance){
            step_now = item.getMb_position()-pdata;

            if (step_now==0) {
                setJsonObjectArrayList(mainObjectArrayList, step_now, item);
            }else if(step_now>0) {
                if(step_now>step_pre) {
                    setJsonObjectArrayList(mainObjectArrayList, step_now, item);
                }else if(step_now==step_pre) {
                    jArrayList.get(step_now).add(mainObjectArrayList.get(step_pre).clone());
                    mainObjectArrayList.set(step_pre, new JSONObject());
                    setJsonObjectArrayList(mainObjectArrayList, step_now, item);
                }else if(step_now<step_pre) {
                    for(int i=0;i<step_pre-step_now;i++){
                        jArrayList.get(step_pre-i).add(mainObjectArrayList.get(step_pre-i).clone());
                        mainObjectArrayList.set(step_pre-i, new JSONObject());
                        mainObjectArrayList.get(step_pre-(i+1)).put("children", jArrayList.get(step_pre-i).clone());
                        jArrayList.set(step_pre-i, new JSONArray());
                    }
                    jArrayList.get(step_now).add(mainObjectArrayList.get(step_now).clone());
                    mainObjectArrayList.set(step_now, new JSONObject());
                    setJsonObjectArrayList(mainObjectArrayList, step_now, item);
                }
            }
            step_pre = step_now;
        }

        for(int i=step_pre;i>0;i--){
            jArrayList.get(i).add(mainObjectArrayList.get(i).clone());
            mainObjectArrayList.set(i, new JSONObject());
            mainObjectArrayList.get(i-1).put("children", jArrayList.get(i).clone());
            jArrayList.set(i, new JSONArray());
        }

        return mainObjectArrayList.get(0);
    }

    public ArrayList<JSONObject> setJsonObjectArrayList(ArrayList<JSONObject> mainObjectArrayList, int step_now, WebMemberBalance item) {
        mainObjectArrayList.get(step_now).put("name",item.getMb_id());
        mainObjectArrayList.get(step_now).put("title",((item.getAvail()==null))?("0.0000 LST"):(item.getAvail()+" LST"));
        mainObjectArrayList.get(step_now).put("R","");
        mainObjectArrayList.get(step_now).put("P","");
        return mainObjectArrayList;
    }









        /**
     * 회원 아이디로 검색 요청
     * @param member_id 검색할 아이디
     */
    @RequestMapping(value = "ajax/dummy/search/id")
    @ResponseBody
    public List<Map<String, Object>> ajaxSearchMemberID(@RequestParam(value = "member_id", defaultValue = "") String member_id) {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        Map<String, Object> resultMap = null;

        List<WebMember> webMemberList = null;

        try {
            webMemberList = webMemberListService.GetWebSeacherMemberList(member_id);

            for (int i = 0; i < webMemberList.size(); i++) {
                resultMap = new HashMap<String, Object>();

                WebMember member = webMemberList.get(i);
                resultMap.put("mb_no", member.getMb_no());
                resultMap.put("mb_id", member.getMb_id());
                resultMap.put("mb_name", member.getMb_name());
                resultMap.put("mb_reg_dt", member.getMb_reg_dt());
                
                resultList.add(resultMap);
            }
        } catch (Exception e) {
            log.error("Fail to get member info list, msg = " + e.getMessage());
        }

        return resultList;
    }    
    /**
     * 회원 번호로 검색 요청
     * @param member_no 검색할 번호
     */
    @RequestMapping(value = "ajax/dummy/search/no")
    @ResponseBody
    public List<Map<String, Object>> ajaxSearchMemberNo(@RequestParam(value = "member_no", defaultValue = "") String member_no) {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        Map<String, Object> resultMap = null;

        List<WebMember> webMemberList = null;

        try {
            webMemberList = webMemberListService.GetWebSeacherMemberListByMB_NO(member_no);

            for (int i = 0; i < webMemberList.size(); i++) {
                resultMap = new HashMap<String, Object>();

                WebMember member = webMemberList.get(i);
                resultMap.put("mb_no", member.getMb_no());
                resultMap.put("mb_id", member.getMb_id());
                resultMap.put("mb_name", member.getMb_name());
                resultMap.put("mb_reg_dt", member.getMb_reg_dt());
                
                resultList.add(resultMap);
            }
        } catch (Exception e) {
            log.error("Fail to get member info list, msg = " + e.getMessage());
        }

        return resultList;
    }
}