package com.onetop.webadmin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.onetop.webadmin.models.RSA;
import com.onetop.webadmin.models.adjustment.MemberBalanceInfo;
import com.onetop.webadmin.models.authority.AdminMember;
import com.onetop.webadmin.models.bank.BankCoinRequest;
import com.onetop.webadmin.models.bank.BankWithdrawProcRequest;
import com.onetop.webadmin.models.bank.DepositInfo;
import com.onetop.webadmin.models.bank.WithdrawRequest;
import com.onetop.webadmin.responses.bank.BankCoinRequestListResponse;
import com.onetop.webadmin.services.AdjustmentService;
import com.onetop.webadmin.services.AuthorityService;
import com.onetop.webadmin.services.BankService;
import com.onetop.webadmin.services.excel.ExcelConstant;
import com.onetop.webadmin.services.excel.ExcelXlsxStreamingView;
import com.onetop.webadmin.util.MessageUtil;
import com.onetop.webadmin.util.PasswordUtil;
import com.onetop.webadmin.util.RSAUtil;
import com.onetop.webadmin.util.Utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.expression.Numbers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.util.*;

@Controller
public class BankingController {

    private static final Logger log = LoggerFactory.getLogger(BankingController.class);

    @Autowired
    private BankService bankService;

    @Autowired
    private AdjustmentService adjustmentService;

    @Autowired
    private RSAUtil rsaUtil;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private Environment env;

    @RequestMapping("bank{pageNo}")
    public String banking(
            @PathVariable String pageNo,
            HttpSession session,
            HttpServletRequest request,
            Model model,
            @RequestParam(value = "sortName", defaultValue = "") String sortName,
            @RequestParam(value = "sortOrderBy", defaultValue = "") String sortOrderBy,
            @RequestParam(value = "search_listCount", defaultValue = "100", required = false) int search_listCount,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(value = "searchValue", defaultValue = "") String searchValue
    ) {

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        model.addAttribute("member_proc", adminMember);

        switch (Integer.parseInt(pageNo)) {
            case 1:
                bank("deposit", session, model, request, sortName, sortOrderBy, page, false, search_listCount);
                break;
            case 2:
                bank("withdraw", session, model, request, sortName, sortOrderBy, page, false, search_listCount);
                break;
            case 31:
                if(searchValue.equals("")) {
                    log.error("searchValue is empty");
                    bank_("wait", session, model, request, sortName, sortOrderBy, page, false, search_listCount);
                } else {
                    log.error("searchValue is not empty");
                    bank_("wait", session, model, request, sortName, sortOrderBy, page, false, search_listCount, searchValue);
                }
                break;
            case 33:
                bank_("refund", session, model, request, sortName, sortOrderBy, page, false, search_listCount);
                break;
            case 41:
            log.error("41 sortName : "+sortName);
                if (sortName == null || sortOrderBy == null || sortName.equals("") || sortOrderBy.equals("")) {
                    sortName = "update_dt";
                    sortOrderBy = "DESC";
                }
    
                bank_("ALL", session, model, request, sortName, sortOrderBy, page, false, search_listCount);
                break;
            default:
                break;
        }

        model.addAttribute("page", page);
        model.addAttribute("search_listCount", search_listCount);
        model.addAttribute("searchValue", searchValue);

        return "banking/bank" + pageNo;
    }


    @RequestMapping("bank1_mod")
    private Object bank1_mod(
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        model.addAttribute("member_proc", adminMember);

        String od_id = request.getParameter("od_id");

        log.error("od_id : "+od_id);

        // @TODO: 매출요청 조회
        BankCoinRequest response = null;
        response = bankService.GetBankCoinRequest(
            od_id
        );
        model.addAttribute("bankCoinRequest", response);

        // @TODO: 입금정보조회 조회
        List<DepositInfo> response_deposit = null;
        response_deposit = bankService.GetDepositInfo(
            od_id
        );
        model.addAttribute("depositInfo", response_deposit);

        
        // 테스트 데이터 추가
        // BankCoinRequest row;
        // row = new BankCoinRequest(); 
        // row.setOd_id(100);
        // row.setCoin_name("도지코인");
        // row.setReg_dt(new Date(System.currentTimeMillis() - 90000000));
        // row.setKST_reg_dt(row.getReg_dt());
        // row.setMb_id("userid");
        // row.setOd_receipt_coin(new BigDecimal(12.345678));
        // row.setConfirm_cnt(3);
        // row.setStatus("REQ");
        // row.setConfirm_dt(new Date(System.currentTimeMillis()));
        // model.addAttribute("bankCoinRequest", row);

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

        return "banking/bank1_mod";
    }

    /**
     * 매출 처리
     */
    @RequestMapping(value = "bank1_mod_proc")
    @ResponseBody
    public String bank1_mod_proc(
            HttpSession session,
            HttpServletRequest request,
            @RequestParam(value = "idx", defaultValue = "1") int idx,
            @RequestParam(value = "mg_id", defaultValue = "") String mg_id,
            @RequestParam(value = "coin_name", defaultValue = "") String coin_name,
            @RequestParam(value = "mb_no", defaultValue = "") String mb_no,
            @RequestParam(value = "address", defaultValue = "") String address,
            @RequestParam(value = "action_type", defaultValue = "") String action_type,
            @RequestParam(value = "return_amount", defaultValue = "") String return_amount,
            @RequestParam(value = "reason", defaultValue = "") String reason)
    {
        String result = "fail";
        try {
        	log.info("AJAX REQUEST :: bank1_mod_proc");
        	log.info("idx=" + idx);
        	log.info("action_type=" + action_type);
        	log.info("return_amount=" + return_amount);
        	log.info("reason=" + reason);
        	
            // @todo: 매출처리
            if (bankService.updateWithdrawBuy(idx, mg_id, coin_name, mb_no, address, action_type, return_amount, reason)) {
                result = "success";
            };
        	
            result = "success";
        } catch (Exception e) {
            log.error("Fail to Request Action, msg = " + e.getMessage());
        }

        return result;
    }

    @RequestMapping("bank33_mod")
    private Object bank33_mod(
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        model.addAttribute("member_proc", adminMember);

        String idx = request.getParameter("idx");

        log.error("idx : "+idx);

        // // @TODO: 매출요청 조회
        WithdrawRequest response = null;
        response = bankService.GetWithdrawRequest(
            idx
        );
        model.addAttribute("withdrawRequest", response);
        


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

        return "banking/bank33_mod";
    }


    @RequestMapping("bank2_mod")
    private Object bank2_mod(
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        model.addAttribute("member_proc", adminMember);

        String mb_id = request.getParameter("mb_id");
        String req_id = request.getParameter("req_id");

        if (mb_id != null && mb_id.length() > 0 &&
                req_id != null && req_id.length() > 0) {
            BankCoinRequest coinReq = bankService.GetCoinRequest(req_id, mb_id);
            request.setAttribute("bankCoinRequest", coinReq);
            List<MemberBalanceInfo> memberBalanceInfoList = adjustmentService.GetMemberBalanceInfo(coinReq.getMb_no());
            request.setAttribute("memberBalanceInfo", memberBalanceInfoList);

            MemberBalanceInfo totalBalanceInfo = new MemberBalanceInfo();
            for (MemberBalanceInfo memberBalanceInfo : memberBalanceInfoList) {
                totalBalanceInfo.setExc_avail(totalBalanceInfo.getExc_avail().add(memberBalanceInfo.getExc_avail()));
                totalBalanceInfo.setExc_in_withdraw(totalBalanceInfo.getExc_in_withdraw().add(memberBalanceInfo.getExc_in_withdraw()));
            }
            request.setAttribute("memberBalanceInfoTotal", totalBalanceInfo);

            request.setAttribute("memberBalanceInfoSyncDate", adjustmentService.GetMbBalanceInfoSyncDate());


            request.setAttribute("memberBalanceLogResponse", adjustmentService.GetMemberBalanceLog(
                    null,
                    null,
                    mb_id,
                    null,
                    null,
                    null,
                    null,
                    -1,
                    20,
                    false));

            request.setAttribute("memberBalanceLogSyncDate", adjustmentService.GetMbBalanceLogSyncDate());
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

        return "banking/bank2_mod";
    }

    @RequestMapping("bank2_mod_proc")
    private @ResponseBody
    HashMap<String, String> bank2_mod_proc(HttpSession session, Model model, HttpServletRequest request, @RequestBody BankWithdrawProcRequest data) {
        HashMap<String, String> resultSet = new HashMap<>();

        try {
            // 세션에 관리자 정보가 없으면 요청 불허
            if (session.getAttribute("member") == null) {
                resultSet.put("resultCode", "-10");
                resultSet.put("resultMsg", "session is not valid");
                return resultSet;
            }

            AdminMember loginMember = (AdminMember) session.getAttribute("member");
            // 세션에 비밀번호 정보는 포함되어있지않기 때문에 해당 no값으로 관리자 정보를 가지고온다.
            AdminMember adminMember = authorityService.getAdminMemberByNo(loginMember.getNo());

            if (adminMember != null) {
                authorityService.insertAdminLog(request,
                        521,
                        String.format("bank2 withdraw update request, od_id = %s, state = %s ",
                                data.getOd_id() != null ? data.getOd_id() : "null",
                                data.getHandling() != null ? data.getHandling() : "null"
                        ),
                        adminMember.getId()
                );

                if (adminMember.getRole() == 1) {
                    resultSet.put("resultCode", "-11");
                    resultSet.put("resultMsg", "no access permission");
                    return resultSet;
                }

                if (data == null) {
                    resultSet.put("resultCode", "-12");
                    resultSet.put("resultMsg", "request data cannot be null");
                    return resultSet;
                }

                if (data.getOd_id() == null || data.getOd_id().length() <= 0) {
                    resultSet.put("resultCode", "-13");
                    resultSet.put("resultMsg", "not enough params");
                    return resultSet;
                }

                HashMap<String, String> response = null;
                String address = env.getProperty("webadmin.balancenoti.addr");
                log.info("address : [" + address + "]");

                switch (data.getHandling()) {
                    case "OK":
                        if (data.getAdmin_pw() == null || data.getAdmin_pw().length() <= 0) {
                            resultSet.put("resultCode", "-21");
                            resultSet.put("resultMsg", "admin password is not valid");
                            return resultSet;
                        }

                        // 입력된 password를 복호화하여 저장
                        PrivateKey privateKey = (PrivateKey) session.getAttribute("RSA_PRIVATE_KEY");
                        if (privateKey == null) {

                            resultSet.put("resultCode", "-12");
                            resultSet.put("resultMsg", "request data cannot be null");
                            return resultSet;
                        }

                        // 세션에 저장된 개인키 제거
                        //session.removeAttribute("RSA_PRIVATE_KEY");

                        String decAdminPw = rsaUtil.getDecryptPassword(privateKey, data.getAdmin_pw());
                        //String decOtpPw = rsaUtil.getDecryptPassword(privateKey, data.getOtp_pw());
                        //String decWithdrawPw = rsaUtil.getDecryptPassword(privateKey, data.getWithdraw_pw());

                        if (!PasswordUtil.matchesPassword(adminMember.getPassword(), decAdminPw, adminMember.getId())) {

                            resultSet.put("resultCode", "-21");
                            resultSet.put("resultMsg", "admin password is not valid");
                            return resultSet;
                        }

                        //log.info("decWithdrawPw : [" + decWithdrawPw + "]");
                        //log.info("decWithdrawPw : [" + decWithdrawPw + "]");
                        //log.info("decOtpPw : [" + decOtpPw + "]");

                        /*
                        String key = Utils.padRight(decOtpPw, '0', 16);

                        log.info("secretKey length : " + key.length());
                        log.info("key : [" + key + "]");

                        JSONObject reqData = new JSONObject() {
                            {
                                put("pass", decWithdrawPw);
                                put("odId", Integer.parseInt(data.getOd_id()));
                            }
                        };
                        */

                        JSONObject requestObject = new JSONObject();
                        //requestObject.put("senderId", adminMember.getOtp_key());
                        requestObject.put("odId", Integer.parseInt(data.getOd_id()));
                        //requestObject.put("encData", AES256Cipher.AesEncodeKey(reqData.toJSONString(), key, key));

                        //log.info(reqData.toJSONString());
                        log.info(requestObject.toJSONString());

                        response = Utils.SendHttpRequest(
                                address + "/admin/withdraw-confirm",
                                requestObject,
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
                                resultSet.put("resultMsg", "fail to request rest api");
                            }
                        } else {
                            resultSet.put("resultCode", "-1");
                            resultSet.put("resultMsg", "fail to request rest api");
                            log.info("fail : " + response.get("resultData"));
                        }
                        return resultSet;

                    case "HOLD":
                        if (data.getReason() == null || data.getReason().length() <= 0) {
                            resultSet.put("resultCode", "-20");
                            resultSet.put("resultMsg", "not enough params");
                            return resultSet;
                        }
                        if (bankService.SetHoldCoinRequest(data.getOd_id(), data.getReason())) {
                            resultSet.put("resultCode", "0000");
                            resultSet.put("resultMsg", "");
                            return resultSet;
                        } else {
                            resultSet.put("resultCode", "-30");
                            resultSet.put("resultMsg", "update error");
                            return resultSet;
                        }
                    case "REJECT":
                        if (data.getReason() == null || data.getReason().length() <= 0) {
                            resultSet.put("resultCode", "-22");
                            resultSet.put("resultMsg", "not enough params");
                            return resultSet;
                        }

                        response = Utils.SendHttpRequest(
                                address + "/admin/withdraw-cancel",
                                new JSONObject() {
                                    {
                                        put("odId", data.getOd_id());
                                        put("isAdmin", true);
                                        put("adminId", adminMember.getId());
                                        put("reject", data.getReason());
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

                                // 임시 코드임.
                            /*
                            if(resultSet.get("resultCode") == null && resultSet.get("resultMsg") == null){
                                resultSet.put("resultCode", "0000");
                                resultSet.put("resultMsg", "success");
                            }
                            */
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
                    default:
                        resultSet.put("resultCode", "-3");
                        resultSet.put("resultMsg", "fail to rest request");
                }
            } else {
                resultSet.put("resultCode", "-4");
                resultSet.put("resultMsg", "fail to rest request");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resultSet.put("resultCode", "-4");
            resultSet.put("resultMsg", "fail to rest request");
        }
        return resultSet;
    }

    @RequestMapping(value = "bank1/excelDown")
    public Object bank1ExcelDown(
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse httpServletResponse,
            Model model,
            @RequestParam(value = "sortName", defaultValue = "") String sortName,
            @RequestParam(value = "sortOrderBy", defaultValue = "") String sortOrderBy,
            @RequestParam(value = "search_listCount", defaultValue = "100", required = false) int search_listCount,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(value = "searchValue", defaultValue = "") String searchValue
    ) {
        bank("deposit", session, model, request, sortName, sortOrderBy, page, true, search_listCount);
        // bank("deposit", session, model, request, sortName, sortOrderBy, page, true, 20);

        Map<String, Object> map = new HashMap<>();

        String req_date = request.getParameter("req_dt");
        req_date = req_date.replace("-", "_").replace(" ", "").replace(".", "");

        //SET FILE NAME
        map.put(ExcelConstant.FILE_NAME, req_date + "_deposit_request");


        MessageUtil messageUtil = new MessageUtil();
        String no = messageUtil.getMessage(session, "number_");
        String req_dt = messageUtil.getMessage(session, "request.date");
        String mb_id = messageUtil.getMessage(session, "member.id");
        String coin = messageUtil.getMessage(session, "coin");
        String amount = messageUtil.getMessage(session, "amount");
        String point = messageUtil.getMessage(session, "bank1.status.point");
        String deposit = messageUtil.getMessage(session, "bank1.status.deposit.amount");
        String status = messageUtil.getMessage(session, "status");
        String confirm_dt = messageUtil.getMessage(session, "completion.date");

        //SET HEADER
        map.put(ExcelConstant.HEAD,
                new ArrayList() {
                    {
                        add(Arrays.asList(
                                no,
                                req_dt,
                                mb_id,
                                coin,
                                amount,
                                point,
                                deposit,
                                status,
                                confirm_dt
                                )
                        );
                    }
                }
        );

        //SET CONTENT
        BankCoinRequestListResponse response = (BankCoinRequestListResponse) request.getAttribute("response");
        List<List<String>> excelBody = new ArrayList<>();

        if (response != null) {
            for (BankCoinRequest bankCoinRequest : response.getBankCoinRequestList()) {
                List<String> tempList = new ArrayList<>();

                tempList.add(String.valueOf(bankCoinRequest.getIdx()));
                if (bankCoinRequest.getCreate_dt() != null) {
                    tempList.add(bankCoinRequest.getCreate_dt().toString());
                } else {
                    tempList.add("");
                }
                tempList.add(bankCoinRequest.getMb_id());
                tempList.add(bankCoinRequest.getCoin_name());

                if (bankCoinRequest.getCoin_amount() != null) {
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(bankCoinRequest.getCoin_amount(), 1, "COMMA", 4, "POINT")));
                } else {
                    tempList.add("");
                }
                tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(bankCoinRequest.getAmount(), 1, "COMMA", 4, "POINT")));
                if (bankCoinRequest.getDeposit_amount() != null) {
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(bankCoinRequest.getDeposit_amount(), 1, "COMMA", 4, "POINT")));
                } else {
                    tempList.add("");
                }
                tempList.add(messageUtil.getMessage(session, "bank1.status." + bankCoinRequest.getStatus()));
                if (bankCoinRequest.getComplete_dt() != null) {
                    tempList.add(bankCoinRequest.getComplete_dt().toString());
                } else {
                    tempList.add("");
                }

                excelBody.add(tempList);
            }
        }

        map.put(ExcelConstant.BODY, excelBody);

        return new ExcelXlsxStreamingView(map);
    }


    @RequestMapping(value = "bank2/excelDown")
    public Object bank2ExcelDown(
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse httpServletResponse,
            Model model,
            @RequestParam(value = "sortName", defaultValue = "") String sortName,
            @RequestParam(value = "sortOrderBy", defaultValue = "") String sortOrderBy,
            @RequestParam(defaultValue = "1") int page
    ) {
        bank("withdraw", session, model, request, sortName, sortOrderBy, page, true, 20);

        Map<String, Object> map = new HashMap<>();

        String req_date = request.getParameter("req_dt");
        req_date = req_date.replace("-", "_").replace(" ", "").replace(".", "");

        //SET FILE NAME
        map.put(ExcelConstant.FILE_NAME, req_date + "_withdraw_request");

        MessageUtil messageUtil = new MessageUtil();
        String no = messageUtil.getMessage(session, "number");
        String txid = messageUtil.getMessage(session, "tx.id");
        String dest_tag = messageUtil.getMessage(session, "mem1.wallet.destination");
        String mb_id = messageUtil.getMessage(session, "member.id");
        String cert = messageUtil.getMessage(session, "security.authentication");
        String coin = messageUtil.getMessage(session, "coin");
        String amount = messageUtil.getMessage(session, "bank2.table.thead.th.amount");
        String fee = messageUtil.getMessage(session, "bank2.table.thead.th.fee");
        String adjbalance = messageUtil.getMessage(session, "amount.settlement.fee");
        String od_request_address = messageUtil.getMessage(session, "bank2.table.thead.th.od_request_address");
        String status = messageUtil.getMessage(session, "status");
        String req_dt = messageUtil.getMessage(session, "request.date");
        String confirm_dt = messageUtil.getMessage(session, "completion.date");
        String admin_id = messageUtil.getMessage(session, "bank2.table.thead.th.admin_id");

        //SET HEADER

        map.put(ExcelConstant.HEAD,
                new ArrayList() {
                    {
                        add(Arrays.asList(
                                no,
                                txid,
                                mb_id,
                                cert,
                                coin,
                                amount,
                                fee,
                                adjbalance,
                                od_request_address,
                                dest_tag,
                                status,
                                req_dt,
                                confirm_dt,
                                admin_id
                                )
                        );
                    }
                }
        );

        //SET CONTENT
        BankCoinRequestListResponse response = (BankCoinRequestListResponse) request.getAttribute("response");
        List<List<String>> excelBody = new ArrayList<>();

        if (response != null) {
            for (BankCoinRequest bankCoinRequest : response.getBankCoinRequestList()) {
                List<String> tempList = new ArrayList<>();

                tempList.add(String.valueOf(bankCoinRequest.getOd_id()));
                tempList.add(bankCoinRequest.getTxid());
                tempList.add(bankCoinRequest.getMb_id());
                tempList.add(String.valueOf(bankCoinRequest.getCert_cnt()));
                tempList.add(bankCoinRequest.getCoin_name());

                if (bankCoinRequest.getOd_request_coin() != null) {
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(bankCoinRequest.getOd_request_coin(), 1, "COMMA", 4, "POINT")));
                } else {
                    tempList.add("");
                }

                if (bankCoinRequest.getOd_request_fee() != null) {
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(bankCoinRequest.getOd_request_fee(), 1, "COMMA", 4, "POINT")));
                } else {
                    tempList.add("");
                }

                if (bankCoinRequest.getOd_receipt_coin() != null) {
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(bankCoinRequest.getOd_receipt_coin(), 1, "COMMA", 4, "POINT")));
                } else {
                    tempList.add("");
                }

                tempList.add(bankCoinRequest.getOd_request_address());
                if (bankCoinRequest.getWithdraw_memo() != null) {
                    tempList.add(bankCoinRequest.getWithdraw_memo());
                } else {
                    tempList.add("");
                }


                tempList.add(messageUtil.getMessage(session, "bank2.table.thead.th.status." + bankCoinRequest.getStatus()));

                if (bankCoinRequest.getReg_dt() != null) {
                    tempList.add(bankCoinRequest.getReg_dt().toString());
                } else {
                    tempList.add("");
                }

                if (bankCoinRequest.getConfirm_dt() != null) {
                    tempList.add(bankCoinRequest.getConfirm_dt().toString());
                } else {
                    tempList.add("");
                }

                tempList.add(bankCoinRequest.getAdmin_id());

                excelBody.add(tempList);
            }
        }

        map.put(ExcelConstant.BODY, excelBody);

        return new ExcelXlsxStreamingView(map);
    }
    @RequestMapping(value = "bank31/excelDown")
    public Object bank31ExcelDown(
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse httpServletResponse,
            Model model,
            @RequestParam(value = "sortName", defaultValue = "") String sortName,
            @RequestParam(value = "sortOrderBy", defaultValue = "") String sortOrderBy,
            @RequestParam(value = "search_listCount", defaultValue = "100", required = false) int search_listCount,
            @RequestParam(defaultValue = "1") int page
    ) {
        // bank("wait", session, model, request, sortName, sortOrderBy, page, true, 20);
        bank_("wait", session, model, request, sortName, sortOrderBy, page, true, search_listCount);

        Map<String, Object> map = new HashMap<>();

        String req_date = request.getParameter("req_dt");
        req_date = req_date.replace("-", "_").replace(" ", "").replace(".", "");

        //SET FILE NAME
        map.put(ExcelConstant.FILE_NAME, req_date + "_withdraw_request");

        MessageUtil messageUtil = new MessageUtil();
        String no = messageUtil.getMessage(session, "number_");
        String req_dt = messageUtil.getMessage(session, "bank31.table.request_date");
        String mb_id = messageUtil.getMessage(session, "member.id");
        String coin = messageUtil.getMessage(session, "bank31.table.coin.withdraw");
        String amount = messageUtil.getMessage(session, "bank31.table.thead.th.amount");
        String od_request_address = messageUtil.getMessage(session, "bank31.table.thead.th.od_request_address");

        //SET HEADER

        map.put(ExcelConstant.HEAD,
                new ArrayList() {
                    {
                        add(Arrays.asList(
                                no,
                                req_dt,
                                mb_id,
                                coin,
                                amount,
                                od_request_address
                                )
                        );
                    }
                }
        );

        //SET CONTENT
        BankCoinRequestListResponse response = (BankCoinRequestListResponse) request.getAttribute("response");
        List<List<String>> excelBody = new ArrayList<>();

        if (response != null) {
            // for (BankCoinRequest bankCoinRequest : response.getWithdrawRequestList()) {
            for (WithdrawRequest withdrawRequest : response.getWithdrawRequestList()) {
                List<String> tempList = new ArrayList<>();

                tempList.add(String.valueOf(withdrawRequest.getIdx()));

                if (withdrawRequest.getCreate_dt() != null) {
                    tempList.add(withdrawRequest.getCreate_dt().toString());
                } else {
                    tempList.add("");
                }

                tempList.add(withdrawRequest.getMb_id());

                tempList.add(String.valueOf(withdrawRequest.getCoin_name()));

                if (withdrawRequest.getWithdraw_amount() != null) {
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(withdrawRequest.getWithdraw_amount(), 1, "COMMA", 4, "POINT"))+" "+String.valueOf(withdrawRequest.getCoin_name()));
                } else {
                    tempList.add("");
                }

                tempList.add(withdrawRequest.getWithdraw_address());

                excelBody.add(tempList);
            }
        }

        map.put(ExcelConstant.BODY, excelBody);

        return new ExcelXlsxStreamingView(map);
    }
    @RequestMapping(value = "bank31/excelDownAll")
    public Object bank31ExcelDownAll(
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse httpServletResponse,
            Model model,
            @RequestParam(value = "sortName", defaultValue = "") String sortName,
            @RequestParam(value = "sortOrderBy", defaultValue = "") String sortOrderBy,
            @RequestParam(value = "search_listCount", defaultValue = "100", required = false) int search_listCount,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(value = "searchValue", defaultValue = "") String searchValue
    ) {
        searchValue = searchValue.replace(" ", ", ");
        bank_("wait", session, model, request, sortName, sortOrderBy, page, true, search_listCount, searchValue);

        Map<String, Object> map = new HashMap<>();

        //SET FILE NAME
        map.put(ExcelConstant.FILE_NAME, "all_withdraw_request");

        MessageUtil messageUtil = new MessageUtil();
        String no = messageUtil.getMessage(session, "number_");
        String req_dt = messageUtil.getMessage(session, "bank31.table.request_date");
        String mb_id = messageUtil.getMessage(session, "member.id");
        String amount = messageUtil.getMessage(session, "bank31.table.thead.th.amount");
        String use_point = messageUtil.getMessage(session, "bank31.content2.table.withdraw.use_point");
        String fee = messageUtil.getMessage(session, "fee");
        String sys2_rate = messageUtil.getMessage(session, "sys2.rate");

        //SET HEADER

        map.put(ExcelConstant.HEAD,
                new ArrayList() {
                    {
                        add(Arrays.asList(
                                no,
                                req_dt,
                                mb_id,
                                amount,
                                use_point,
                                fee,
                                sys2_rate
                                )
                        );
                    }
                }
        );

        //SET CONTENT
        BankCoinRequestListResponse response = (BankCoinRequestListResponse) request.getAttribute("response");
        List<List<String>> excelBody = new ArrayList<>();

        if (response != null) {
            // for (BankCoinRequest bankCoinRequest : response.getWithdrawRequestList()) {
            for (WithdrawRequest withdrawRequest : response.getWithdrawRequestList()) {
                List<String> tempList = new ArrayList<>();

                tempList.add(String.valueOf(withdrawRequest.getIdx()));
                if (withdrawRequest.getCreate_dt() != null) {
                    tempList.add(withdrawRequest.getCreate_dt().toString());
                } else {
                    tempList.add("");
                }
                tempList.add(withdrawRequest.getMb_id());
                if (withdrawRequest.getWithdraw_amount() != null) {
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(withdrawRequest.getWithdraw_amount(), 1, "COMMA", 4, "POINT"))+" "+String.valueOf(withdrawRequest.getCoin_name()));
                } else {
                    tempList.add("");
                }
                tempList.add(withdrawRequest.getAmount()+" P");
                tempList.add(withdrawRequest.getFee()+"");
                tempList.add("1$ = "+withdrawRequest.getQuote_amount()+withdrawRequest.getCoin_name());

                excelBody.add(tempList);
            }
        }

        map.put(ExcelConstant.BODY, excelBody);

        return new ExcelXlsxStreamingView(map);
    }
    @RequestMapping(value = "bank41/excelDown")
    public Object bank41ExcelDown(
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse httpServletResponse,
            Model model,
            @RequestParam(value = "sortName", defaultValue = "") String sortName,
            @RequestParam(value = "sortOrderBy", defaultValue = "") String sortOrderBy,
            @RequestParam(value = "search_listCount", defaultValue = "100", required = false) int search_listCount,
            @RequestParam(defaultValue = "1") int page
    ) {
        if (sortName == null || sortOrderBy == null || sortName.equals("") || sortOrderBy.equals("")) {
            sortName = "update_dt";
            sortOrderBy = "DESC";
        }

        // bank("withdraw", session, model, request, sortName, sortOrderBy, page, true, 20);
        bank_("ALL", session, model, request, sortName, sortOrderBy, page, true, search_listCount);

        Map<String, Object> map = new HashMap<>();

        String req_date = request.getParameter("req_dt");
        req_date = req_date.replace("-", "_").replace(" ", "").replace(".", "");

        //SET FILE NAME
        map.put(ExcelConstant.FILE_NAME, req_date + "_withdraw_request");

        MessageUtil messageUtil = new MessageUtil();
        String no = messageUtil.getMessage(session, "number_");
        String confirm_dt = messageUtil.getMessage(session, "completion.date");
        String mb_id = messageUtil.getMessage(session, "member.id");
        String od_request_address = messageUtil.getMessage(session, "bank41.table.thead.th.od_request_address");
        String txid = messageUtil.getMessage(session, "tx.id");
        String amount = messageUtil.getMessage(session, "bank41.table.thead.th.amount");
        String status = messageUtil.getMessage(session, "status");
        String approval = messageUtil.getMessage(session, "bank41.approval");
        String method = messageUtil.getMessage(session, "bank41.search.withdraw.method");
        String exchange = messageUtil.getMessage(session, "bank41.search.exchange.withdraw");

        //SET HEADER

        map.put(ExcelConstant.HEAD,
                new ArrayList() {
                    {
                        add(Arrays.asList(
                                no,
                                confirm_dt,
                                mb_id,
                                od_request_address,
                                txid,
                                amount,
                                status,
                                approval,
                                method,
                                exchange
                                )
                        );
                    }
                }
        );

        //SET CONTENT
        BankCoinRequestListResponse response = (BankCoinRequestListResponse) request.getAttribute("response");
        List<List<String>> excelBody = new ArrayList<>();

        if (response != null) {
            log.error("response.getCoinList() : "+response.getCoinList().size());
            log.error("response.getWithdrawRequestList() : "+response.getWithdrawRequestList().size());
            log.error("response.getOldWithdrawRequestList() : "+response.getOldWithdrawRequestList().size());
            // for (BankCoinRequest bankCoinRequest : response.getBankCoinRequestList()) {
            for (WithdrawRequest withdrawRequest : response.getWithdrawRequestList()) {
                List<String> tempList = new ArrayList<>();

                tempList.add(String.valueOf(withdrawRequest.getIdx()));

                if (withdrawRequest.getUpdate_dt() != null) {
                    tempList.add(withdrawRequest.getUpdate_dt().toString());
                } else {
                    tempList.add("");
                }

                tempList.add(withdrawRequest.getMb_id());

                tempList.add(withdrawRequest.getWithdraw_address());

                tempList.add(withdrawRequest.getTxid());

                if (withdrawRequest.getWithdraw_amount() != null) {
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(withdrawRequest.getWithdraw_amount(), 1, "COMMA", 4, "POINT"))+ " "+withdrawRequest.getCoin_name());
                } else {
                    tempList.add("");
                }

                tempList.add(withdrawRequest.getStatus());

                if (withdrawRequest.getStatus().equals("reject")) {
                    log.error("withdrawRequest.getStatus() [true] : "+withdrawRequest.getStatus());
                    tempList.add("X");
                    tempList.add("-");
                    tempList.add("-");
                } else {
                    log.error("withdrawRequest.getStatus() [false] : "+withdrawRequest.getStatus());
                    tempList.add("O");
                    if ( withdrawRequest.getWithdraw_batch_id() > 0 ) {
                        tempList.add("O");
                    } else {
                        tempList.add("X");
                    }
                    if ( withdrawRequest.getWithdraw_exchange() > 0 ) {
                        tempList.add("O");
                    } else {
                        tempList.add("X");
                    }
                    tempList.add("");
                }

                excelBody.add(tempList);
            }
        }

        map.put(ExcelConstant.BODY, excelBody);

        return new ExcelXlsxStreamingView(map);
    }

    private void bank(String action, HttpSession session, Model model, HttpServletRequest request, String sortName, String sortOrderBy, int page, boolean isExcel, int search_listCount) {
        if (sortName == null || sortOrderBy == null || sortName.equals("") || sortOrderBy.equals("")) {
            sortName = "create_dt";
            sortOrderBy = "DESC";
        }

        request.setAttribute("sortName", sortName);
        request.setAttribute("sortOrderBy", sortOrderBy);

        String mb_id = request.getParameter("mb_id");
        model.addAttribute("mb_id", mb_id);

        String req_dt = request.getParameter("req_dt");

        if (req_dt == null || req_dt.length() <= 0) {
            Date now = new Date();
            req_dt = String.format("%s - %s", Utils.GetDateStringFromFormat(Utils.GetDateAddMonths(now, -1), "yyyy.MM.dd"),
                    Utils.GetDateStringFromFormat(now, "yyyy.MM.dd"));
        }
        String[] req_dts = req_dt.split("-");
        request.setAttribute("req_dt", req_dt);

        String req_dt_from = null;
        String req_dt_to = null;
        if (req_dts.length > 0) {
            req_dt_from = req_dts[0].trim();
            req_dt_to = req_dts.length >= 2 ? req_dts[1].trim() : req_dts[0].trim();
        }

        String txid = request.getParameter("txid");
        model.addAttribute("txid", txid);
        String deposit_memo = request.getParameter("deposit_memo");
        model.addAttribute("deposit_memo", deposit_memo);
        String withdraw_memo = request.getParameter("withdraw_memo");
        model.addAttribute("withdraw_memo", withdraw_memo);
        String od_request_address = request.getParameter("od_request_address");
        model.addAttribute("od_request_address", od_request_address);

        String status = request.getParameter("status");
        if (status == null || status.length() <= 0) {
            status = "ALL";
        }
        model.addAttribute("status", status);

        String coin_name = request.getParameter("coin_name");
        if (coin_name == null) {
            coin_name = "ALL";
        }
        model.addAttribute("coin_name", coin_name);

//        List<String> coinNoList = new ArrayList<>();
//        List<CoinInfo> coinInfoList = coinDao.GetCoinList();
//        for (CoinInfo coin : coinInfoList) {
//            String coin_no = request.getParameter("coin_" + coin.getCoin_no());
//            if (coin_no != null && coin_no.length() > 0) {
//                coinNoList.add(String.valueOf(coin_no));
//                model.addAttribute("coin_" + coin_no, true);
//            } else {
//                model.addAttribute("coin_" + coin_no, false);
//            }
//        }

        BankCoinRequestListResponse response = null;
        if (isExcel) {
            response = bankService.GetBankCoinRequestListExcel(
                    action,
                    mb_id,
                    req_dt_from, req_dt_to,
                    coin_name,
                    txid,
                    od_request_address,
                    status,
                    sortName,
                    sortOrderBy,
                    deposit_memo,
                    withdraw_memo,
                    isExcel
            );
        } else {
            response = bankService.GetBankCoinRequestList(
                    action,
                    mb_id,
                    req_dt_from, req_dt_to,
                    coin_name,
                    txid,
                    od_request_address,
                    status,
                    sortName,
                    sortOrderBy,
                    page,
                    search_listCount,
                    deposit_memo,
                    withdraw_memo,
                    isExcel
            );
        }

        request.setAttribute("response", response);
    }
    private void bank_(String action, HttpSession session, Model model, HttpServletRequest request, String sortName, String sortOrderBy, int page, boolean isExcel, int search_listCount) {
        if (sortName == null || sortOrderBy == null || sortName.equals("") || sortOrderBy.equals("")) {
            sortName = "create_dt";
            sortOrderBy = "DESC";
        }

        request.setAttribute("sortName", sortName);
        request.setAttribute("sortOrderBy", sortOrderBy);

        String mb_id = request.getParameter("mb_id");
        model.addAttribute("mb_id", mb_id);

        String req_dt = request.getParameter("req_dt");

        if (req_dt == null || req_dt.length() <= 0) {
            Date now = new Date();
            if (action.equals("ALL")) {
                req_dt = String.format("%s - %s", Utils.GetDateStringFromFormat(Utils.GetDateAddDays(now, -3), "yyyy.MM.dd"), Utils.GetDateStringFromFormat(now, "yyyy.MM.dd"));
            } else {
                req_dt = String.format("%s - %s", Utils.GetDateStringFromFormat(Utils.GetDateAddDays(now, -2), "yyyy.MM.dd"), Utils.GetDateStringFromFormat(now, "yyyy.MM.dd"));
            }
        }
        String[] req_dts = req_dt.split("-");
        request.setAttribute("req_dt", req_dt);

        String req_dt_from = null;
        String req_dt_to = null;
        if (req_dts.length > 0) {
            req_dt_from = req_dts[0].trim();
            req_dt_to = req_dts.length >= 2 ? req_dts[1].trim() : req_dts[0].trim();
        }

        String txid = request.getParameter("txid");
        model.addAttribute("txid", txid);
        String deposit_memo = request.getParameter("deposit_memo");
        model.addAttribute("deposit_memo", deposit_memo);
        String withdraw_memo = request.getParameter("withdraw_memo");
        model.addAttribute("withdraw_memo", withdraw_memo);
        String od_request_address = request.getParameter("od_request_address");
        model.addAttribute("od_request_address", od_request_address);

        String status = request.getParameter("status");
        if (status == null || status.length() <= 0) {
            status = "ALL";
        }
        model.addAttribute("status", status);

        String coin_name = request.getParameter("coin_name");
        if (coin_name == null) {
            coin_name = "ALL";
        }
        model.addAttribute("coin_name", coin_name);

//        List<String> coinNoList = new ArrayList<>();
//        List<CoinInfo> coinInfoList = coinDao.GetCoinList();
//        for (CoinInfo coin : coinInfoList) {
//            String coin_no = request.getParameter("coin_" + coin.getCoin_no());
//            if (coin_no != null && coin_no.length() > 0) {
//                coinNoList.add(String.valueOf(coin_no));
//                model.addAttribute("coin_" + coin_no, true);
//            } else {
//                model.addAttribute("coin_" + coin_no, false);
//            }
//        }

        BankCoinRequestListResponse response = null;
        if (isExcel) {
            response = bankService.GetBankCoinRequestListExcel_(
                    action,
                    mb_id,
                    req_dt_from, req_dt_to,
                    coin_name,
                    txid,
                    od_request_address,
                    status,
                    sortName,
                    sortOrderBy,
                    deposit_memo,
                    withdraw_memo,
                    isExcel
            );
        } else {
            response = bankService.GetBankCoinRequestList_(
                    action,
                    mb_id,
                    req_dt_from, req_dt_to,
                    coin_name,
                    txid,
                    od_request_address,
                    status,
                    sortName,
                    sortOrderBy,
                    page,
                    search_listCount,
                    deposit_memo,
                    withdraw_memo,
                    isExcel
            );
        }

        request.setAttribute("response", response);
    }
    private void bank_(String action, HttpSession session, Model model, HttpServletRequest request, String sortName, String sortOrderBy, int page, boolean isExcel, int search_listCount, String search_value) {
        if (sortName == null || sortOrderBy == null || sortName.equals("") || sortOrderBy.equals("")) {
            sortName = "create_dt";
            sortOrderBy = "DESC";
        }

        request.setAttribute("sortName", sortName);
        request.setAttribute("sortOrderBy", sortOrderBy);

        String mb_id = request.getParameter("mb_id");
        model.addAttribute("mb_id", mb_id);

        String req_dt = request.getParameter("req_dt");

        if (req_dt == null || req_dt.length() <= 0) {
            Date now = new Date();
            if (action.equals("complete")) {
                req_dt = String.format("%s - %s", Utils.GetDateStringFromFormat(Utils.GetDateAddDays(now, -3), "yyyy.MM.dd"), Utils.GetDateStringFromFormat(now, "yyyy.MM.dd"));
            } else {
                req_dt = String.format("%s - %s", Utils.GetDateStringFromFormat(Utils.GetDateAddDays(now, -2), "yyyy.MM.dd"), Utils.GetDateStringFromFormat(now, "yyyy.MM.dd"));
            }
        }
        String[] req_dts = req_dt.split("-");
        request.setAttribute("req_dt", req_dt);

        String req_dt_from = null;
        String req_dt_to = null;
        if (req_dts.length > 0) {
            req_dt_from = req_dts[0].trim();
            req_dt_to = req_dts.length >= 2 ? req_dts[1].trim() : req_dts[0].trim();
        }

        String txid = request.getParameter("txid");
        model.addAttribute("txid", txid);
        String deposit_memo = request.getParameter("deposit_memo");
        model.addAttribute("deposit_memo", deposit_memo);
        String withdraw_memo = request.getParameter("withdraw_memo");
        model.addAttribute("withdraw_memo", withdraw_memo);
        String od_request_address = request.getParameter("od_request_address");
        model.addAttribute("od_request_address", od_request_address);

        String status = request.getParameter("status");
        if (status == null || status.length() <= 0) {
            status = "ALL";
        }
        model.addAttribute("status", status);

        String coin_name = request.getParameter("coin_name");
        if (coin_name == null) {
            coin_name = "ALL";
        }
        model.addAttribute("coin_name", coin_name);

        BankCoinRequestListResponse response = null;
        response = bankService.GetBankCoinRequestList_(
                action,
                mb_id,
                req_dt_from, req_dt_to,
                coin_name,
                txid,
                od_request_address,
                status,
                sortName,
                sortOrderBy,
                page,
                search_listCount,
                deposit_memo,
                withdraw_memo,
                isExcel,
                search_value
        );

        request.setAttribute("response", response);
        BigDecimal all_amount = new BigDecimal("0");
        for (WithdrawRequest re : response.getWithdrawRequestList()) {
            all_amount = all_amount.add(re.getWithdraw_amount());
        }
        request.setAttribute("all_amount", all_amount);
        BigDecimal all_fee = new BigDecimal("0");
        for (WithdrawRequest re : response.getWithdrawRequestList()) {
            all_fee = all_fee.add(re.getFee());
        }
        request.setAttribute("all_fee", all_fee);
    }

    // /**
    //  * 입출금정보 메일 발송
    //  *
    //  * @param session
    //  * @param req_id
    //  * @param mb_id
    //  * @return
    //  */
    // @RequestMapping("bank_mailsend_proc")
    // private @ResponseBody
    // String bank_mailsend_proc(
    //         HttpSession session,
    //         @RequestParam(value = "req_id") String req_id,
    //         @RequestParam(value = "mb_id") String mb_id
    // ) {

    //     String result = "fail";

    //     try {

    //         BankCoinRequest bankCoinRequest = bankService.GetCoinRequest(req_id, mb_id);

    //         String address = env.getProperty("webadmin.mailsender.addr");
    //         log.info("address : [" + address + "]");

    //         String receiveAddress = env.getProperty("webadmin.mailsender.txid.addr");
    //         log.info("receiveAddress : [" + receiveAddress + "]");

    //         // EMAIL 발송의 성공/실패는 어드민에서 알 필요 없음.
    //         new Thread(() -> {

    //             HashMap<String, String> response = Utils.SendHttpRequest(
    //                     String.format("%s%s", address, "/ems/admin/send-txid"),
    //                     new JSONObject() {
    //                         {
    //                             put("action", bankCoinRequest.getAction());
    //                             put("receiveEmailAddr", receiveAddress);
    //                             put("mbId", bankCoinRequest.getMb_id());
    //                             put("txid", bankCoinRequest.getTxid());
    //                             put("coin", bankCoinRequest.getCoin_name());
    //                             put("amount", bankCoinRequest.getOd_receipt_coin());
    //                             put("address", bankCoinRequest.getOd_request_address());
    //                             put("memo", bankCoinRequest.getDeposit_memo() != null ? bankCoinRequest.getDeposit_memo() : bankCoinRequest.getWithdraw_memo() != null ? bankCoinRequest.getWithdraw_memo() : "");
    //                         }
    //                     },
    //                     session.getAttribute("language").toString()
    //             );

    //             if (response.get("responseCode").equals("success")) {
    //                 try {
    //                     System.out.println("success : " + response.get("resultData"));
    //                 } catch (Exception e) {
    //                     e.printStackTrace();
    //                     System.out.println("fail : " + response.get("resultData"));
    //                 }
    //             } else {
    //                 System.out.println("fail : " + response.get("resultData"));
    //             }
    //         }).start();

    //         result = "success";
    //     } catch (Exception e) {

    //         log.error("Fail to send mail coin request , msg = " + e.getMessage());
    //     }
    //     return result;
    // }




    /**
     * 승인 항목 확인
     *
     * @param session
     * @param idx
     * @return
     */
    @RequestMapping("withdraw_check_approve_proc")
    private @ResponseBody
    boolean withdraw_check_approve_proc(
            HttpSession session,
            @RequestParam(value = "idx") String idx
    ) {
        try {
            if (bankService.checkApprove(idx)) {
                return true;
            };
        } catch (Exception e) {
            log.error("Fail to withdraw_check_approve_proc , msg = " + e.getMessage());
        }
        return false;
    }

    /**
     * 기존 출금 내역 확인
     *
     * @param session
     * @param req_id
     * @param mb_id
     * @return
     */
    @RequestMapping("withdraw_old_data_proc")
    private @ResponseBody
    BankCoinRequestListResponse withdraw_old_data_proc(
            HttpSession session,
            HttpServletRequest request,
            @RequestParam(value = "mb_no") int mb_no
    ) {
        BankCoinRequestListResponse response = bankService.searchOldData(mb_no);
        return response;
    }

    /**
     * 개별 승인 요청
     *
     * @param session
     * @param mb_id
     * @param request_address
     * @param amount
     * @return
     */
    @RequestMapping("withdraw_approve_proc")
    private @ResponseBody
    String withdraw_approve_proc(
            HttpSession session,
            @RequestParam(value = "idx") int idx,
            @RequestParam(value = "request_address") String request_address,
            @RequestParam(value = "coin_name") String coin_name,
            @RequestParam(value = "amount") String amount,
            @RequestParam(value = "mg_id") String mg_id
    ) {
        String result = "fail";

        try {
            if (bankService.withdrawApprove(idx, request_address, coin_name, amount, mg_id)) {
                result = "success";
            };
        } catch (Exception e) {

            log.error("Fail to send withdraw_approve_proc , msg = " + e.getMessage());
            String errormsg = e.getMessage();
            try {
                JSONParser parser = new JSONParser();
                log.error("Fail to send withdraw_approve_proc , errormsg = " + errormsg.substring(errormsg.indexOf("["), errormsg.length()-2));

                Object obj = parser.parse(errormsg.substring(errormsg.indexOf("[")+1, errormsg.length()-1));
                JSONObject json = (JSONObject) obj;
                result = (String) json.get("message");
            } catch (ParseException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                log.error("Fail to send withdraw_approve_proc , ParseException = " + e.getMessage());
            }
        }
        log.error("Fail to send withdraw_approve_proc , result = " + result);
        return result;
    }
    /**
     * 일괄 승인 요청
     *
     * @param session
     * @param mb_id
     * @param request_address
     * @param amount
     * @param mg_id
     * @return
     */
    @RequestMapping("withdraw_approve_all_proc")
    private @ResponseBody
    String withdraw_approve_all_proc(
            HttpSession session,
            @RequestParam(value = "idx") String idx,
            @RequestParam(value = "request_address") String request_address,
            @RequestParam(value = "coin_name") String coin_name,
            @RequestParam(value = "amount") String amount,
            @RequestParam(value = "mg_id") String mg_id
    ) {
        String result = "fail";

        try {
            if (bankService.withdrawAllApprove(idx, request_address, coin_name, amount, mg_id)) {
                result = "success";
            };
        } catch (Exception e) {

            log.error("Fail to send withdraw_approve_all_proc , msg = " + e.getMessage());
        }
        return result;
    }

    /**
     * 개별 거래소 출금 요청
     *
     * @param session
     * @param mb_id
     * @return
     */
    @RequestMapping("withdraw_exchange_proc")
    private @ResponseBody
    String withdraw_exchange_proc(
            HttpSession session,
            @RequestParam(value = "mb_id") String mb_id,
            @RequestParam(value = "mg_id") String mg_id
    ) {
        String result = "fail";
        try {
            if (bankService.updateExchange(mb_id, mg_id)) {
                result = "success";
            };
        } catch (Exception e) {

            log.error("Fail to send withdraw_exchange_proc , msg = " + e.getMessage());
        }
        return result;
    }
    /**
     * 일괄 거래소 출금 요청
     *
     * @param session
     * @param mb_id
     * @param mg_id
     * @return
     */
    @RequestMapping("withdraw_exchange_all_proc")
    private @ResponseBody
    String withdraw_exchange_all_proc(
            HttpSession session,
            @RequestParam(value = "mb_id") String mb_id,
            @RequestParam(value = "mg_id") String mg_id
    ) {
        String result = "fail";
        try {
            if (bankService.updateAllExchange(mb_id, mg_id)) {
                result = "success";
            };
        } catch (Exception e) {

            log.error("Fail to send withdraw_exchange_all_proc , msg = " + e.getMessage());
        }
        return result;
    }

    /**
     * 개별 출금요청 거절
     *
     * @param session
     * @param mb_id
     * @param reject_reason
     * @return
     */
    @RequestMapping("withdraw_reject_proc")
    private @ResponseBody
    String withdraw_reject_proc(
            HttpSession session,
            @RequestParam(value = "mb_id") String mb_id,
            @RequestParam(value = "reject_reason") String reject_reason,
            @RequestParam(value = "total_value") Double total_value,
            @RequestParam(value = "mg_id") String mg_id
    ) {

        String result = "fail";

        try {

            if (bankService.updateReject(mb_id, reject_reason, total_value, mg_id)) {
                result = "success";
            };
        } catch (Exception e) {

            log.error("Fail to send withdraw_reject_proc , msg = " + e.getMessage());
        }
        return result;
    }
    /**
     * 일괄 출금요청 거절
     *
     * @param session
     * @param mb_id
     * @param reject_reason
     * @return
     */
    @RequestMapping("withdraw_reject_all_proc")
    private @ResponseBody
    String withdraw_reject_all_proc(
            HttpSession session,
            @RequestParam(value = "mb_id") String mb_id,
            @RequestParam(value = "reject_reason") String reject_reason,
            @RequestParam(value = "total_value") Double total_value,
            @RequestParam(value = "mg_id") String mg_id
    ) {
        String result = "fail";
        try {
            if (bankService.updateAllReject(mb_id, reject_reason, total_value, mg_id)) {
                result = "success";
            };
        } catch (Exception e) {
            log.error("Fail to send withdraw_reject_proc , msg = " + e.getMessage());
        }
        return result;
    }


    /**
     * 매출요청 금액 수정
     */
    @RequestMapping(value = "ajax/bank1/update")
    @ResponseBody
    public String ajaxBank1New(
            HttpSession session,
            HttpServletRequest request,
            @RequestParam(value = "idx", defaultValue = "") String idx,
            @RequestParam(value = "od_receipt_coin", defaultValue = "") String od_receipt_coin,
            @RequestParam(value = "buy_point", defaultValue = "") String buy_point
    ) {
        log.info("AJAX REQUEST :: ajax/bank1/update");
        log.info("idx=" + idx);
        log.info("od_receipt_coin=" + od_receipt_coin);
        log.info("buy_point=" + buy_point);

        String result = "fail";
        try {
            // @todo: 매출요청 수정 처리
            if (bankService.updateWithdrawBuy(idx, od_receipt_coin, buy_point)) {
                result = "success";
            };
        } catch (Exception e) {
            log.error("Fail to Request Update, msg = " + e.getMessage());
        }

        return result;
    }




    /**
     * 매출수기등록 처리
     */
    @RequestMapping(value = "ajax/bank1/new")
    @ResponseBody
    public String ajaxBank1New(
            HttpSession session,
            HttpServletRequest request,
            @RequestParam(value = "member_id", defaultValue = "") String member_id,
            @RequestParam(value = "member_no", defaultValue = "") String member_no,
            @RequestParam(value = "insert_date", defaultValue = "") String insert_date,
            @RequestParam(value = "asset_type", defaultValue = "") String asset_type,
            @RequestParam(value = "txid", defaultValue = "") String txid,
            @RequestParam(value = "buy_point", defaultValue = "") String buy_point,
            @RequestParam(value = "reason", defaultValue = "") String reason,
            @RequestParam(value = "mg_id", defaultValue = "") String mg_id
            ) {
    	
        String result = "fail";

        try {
        	log.info("AJAX REQUEST :: ajax/bank1/new");
        	log.info("member_id=" + member_id);
        	log.info("member_no=" + member_no);
        	log.info("insert_date=" + insert_date);
        	log.info("asset_type=" + asset_type);
        	log.info("txid=" + txid);
        	log.info("buy_point=" + buy_point);
        	log.info("reason=" + reason);
        	log.info("mg_id=" + mg_id);
        	
            // @todo: 매출수기등록 처리
            // 1. 사용자 페이지 추가

            // 2. 관리자 페이지 추가
            if (bankService.insertHandWriting(member_id, member_no, insert_date, asset_type, txid, buy_point, reason, mg_id)) {
                result = "success";
            };


            // 3. 관리자 페이지 관리자 지금/회수 페이지 추가
        } catch (Exception e) {
            log.error("Fail to Request Insert, msg = " + e.getMessage());
        }

        return result;
    }
}