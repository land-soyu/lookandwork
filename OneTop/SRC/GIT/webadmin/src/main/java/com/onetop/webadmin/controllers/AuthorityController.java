package com.onetop.webadmin.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.onetop.webadmin.mappers.AuthorityDao;
import com.onetop.webadmin.models.RSA;
import com.onetop.webadmin.models.authority.AccessRole;
import com.onetop.webadmin.models.authority.AdminLog;
import com.onetop.webadmin.models.authority.AdminMember;
import com.onetop.webadmin.models.authority.AdminMemberAuth;
import com.onetop.webadmin.responses.authority.AdminLogResponse;
import com.onetop.webadmin.responses.authority.AdminMemberResponse;
import com.onetop.webadmin.services.AuthorityService;
import com.onetop.webadmin.services.excel.ExcelConstant;
import com.onetop.webadmin.services.excel.ExcelXlsxStreamingView;
import com.onetop.webadmin.util.MessageUtil;
import com.onetop.webadmin.util.PasswordUtil;
import com.onetop.webadmin.util.RSAUtil;
import com.onetop.webadmin.util.Utils;

import java.security.PrivateKey;
import java.util.*;

@Controller
public class AuthorityController {

    private static final Logger log = LoggerFactory.getLogger(AuthorityController.class);

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private RSAUtil rsaUtil;

    @Autowired
    AuthorityDao authorityDao;

    /**
     * 접근권한 관리 페이지
     * @param session
     * @param model
     * @param search_type
     * @param search_content
     * @param search_listCount
     * @param page
     * @return
     */
    @RequestMapping(value = "auth1", method = RequestMethod.GET)
    public String authority1(
            HttpSession session,
            Model model,
            @RequestParam(value = "search_type", defaultValue = "all", required = false) String search_type,
            @RequestParam(value = "search_content", defaultValue = "", required = false) String search_content,
            @RequestParam(value = "search_listCount", defaultValue = "20", required = false) int search_listCount,
            @RequestParam(value = "page", defaultValue="1", required=false) int page
    ){

        AdminMemberResponse adminMemberResponse = null;

        // 관리자리스트를 가지고 온다.
        try {

            adminMemberResponse = authorityService.adminMemberList(search_type, search_content, page, search_listCount);
        } catch (Exception e) {

            log.error("Fail to get admin member list, msg = " + e.getMessage());
        }

        model.addAttribute("member_proc", (AdminMember) session.getAttribute("member"));
        model.addAttribute("adminMemberResponse", adminMemberResponse);
        model.addAttribute("page", page);
        model.addAttribute("search_type", search_type);
        model.addAttribute("search_content", search_content);
        model.addAttribute("search_listCount", search_listCount);
        return "authority/auth1";
    }

    /**
     * 접근권한 관리 (등록/수정) 페이지
     * @param session
     * @param model
     * @param no
     * @return
     */
    @RequestMapping(value = "auth1_mod", method = RequestMethod.GET)
    public String authority1_mod(
            HttpSession session,
            Model model,
            @RequestParam(value = "no", defaultValue = "0", required = false) int no
    ){

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

        AdminMember adminMember = new AdminMember();
        List<AccessRole> accessRoleList = null;

        // 0이면 사용자 등록
        if (no != 0) {

            try {

                adminMember = authorityService.getAdminMemberByNo(no);
            } catch (Exception e) {

                log.error("Fail to get admin member, msg = " + e.getMessage());
            }
        }

        // 권한 정보 가져오기
        try {

            accessRoleList = authorityService.accessRoleList();
        } catch (Exception e) {

            log.error("Fail to get access role list, msg = " + e.getMessage());
        }

        model.addAttribute("member_proc", (AdminMember) session.getAttribute("member"));
        model.addAttribute("adminMember", adminMember);
        model.addAttribute("accessRoleList", accessRoleList);
        return "authority/auth1_mod";
    }

    /**
     * 접근권한 관리 추가
     * @param request
     * @param session
     * @param name
     * @param id
     * @param password
     * @param phone_number
     * @param role
     * @param otp_key
     * @return
     */
    @RequestMapping(value = "ajax/auth1_mod/add", method = RequestMethod.POST)
    public @ResponseBody
    String authority1_mod_add(
            HttpServletRequest request,
            HttpSession session,
            @RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "password", required = true) String password,
            @RequestParam(value = "phone_number", defaultValue = "", required = false) String phone_number,
            @RequestParam(value = "role", required = true) int role,
            @RequestParam(value = "otp_key", defaultValue = "", required = false) String otp_key
    ){

        String result = "fail";

        // 접속한 관리자 세션정보
        AdminMember loginAdmin = (AdminMember) session.getAttribute("member");

        AdminMember adminMember = new AdminMember();
        adminMember.setName(name);
        adminMember.setId(id);
        adminMember.setPhone_number(phone_number);
        adminMember.setRole(role);
        adminMember.setSecurity_proposal("ZA001ZA111ZB001ZB101ZB111ZB121ZB201ZB211ZB221ZB301ZB311ZB321ZB331ZB341ZB411ZB511ZC001ZC111ZC201ZC211ZC221ZC231ZC301ZC311ZC321ZC401ZC411ZC421ZD001ZD101ZD111ZD201ZD211ZE001ZE111ZE211ZF001ZF111ZF211ZF311ZD311ZD321ZD411");
        adminMember.setIs_password_init("Y");
        adminMember.setOtp_key(otp_key == "0" ? "" : otp_key);

        // 입력된 비밀번호를 복호화하여 저장
        PrivateKey privateKey = (PrivateKey) session.getAttribute("RSA_PRIVATE_KEY");
        if (privateKey == null) return result;

        try {
            String decPassword = rsaUtil.getDecryptPassword(privateKey, password);
            adminMember.setPassword(PasswordUtil.createUserPassword(id, decPassword));
        } catch (Exception e) {

            log.error("Fail to decrypt param, msg = " + e.getMessage());
        }

        try {

            int no = authorityService.insertAdminMember(adminMember);
            authorityService.insertAdminLog(request, 711, "Add admin member, no=" + no + ", id=" + id, loginAdmin.getId());
            result = "success";

            // 세션에 저장된 개인키 제거
            session.removeAttribute("RSA_PRIVATE_KEY");
        } catch (Exception e) {

            log.error("Fail to add adminMember, msg = " + e.getMessage());
        }

        return result;
    }

    /**
     * @brief       접근권한 관리 수정\n
     * @details     \n
     * @param       no              관리자 번호\n
     * @param       name            관리자 이름\n
     * @param       password        관리자 비밀번호\n
     * @param       phone_number    관리자 전화번호\n
     * @param       role            관리자 권한\n
     * @param       otp_key         관리자 OTP key\n
     */
    @RequestMapping(value = "ajax/auth1_mod/modify", method = RequestMethod.POST)
    public @ResponseBody String authority1_mod_modify(
            HttpServletRequest request,
            HttpSession session,
            @RequestParam(value = "no", required = true) int no,
            @RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "password", defaultValue = "", required = false) String password,
            @RequestParam(value = "phone_number", defaultValue = "", required = false) String phone_number,
            @RequestParam(value = "role", required = true) int role,
            @RequestParam(value = "otp_key", defaultValue = "", required = false) String otp_key
    ){

        String result = "fail";
        String logTemp = "";
        String logContent = "Modify admin member, no=" + no + ", ";

        // 접속한 관리자 세션정보
        AdminMember loginAdmin = (AdminMember) session.getAttribute("member");

        AdminMember adminMember = null;

        // 번호로 관리자 정보를 가지고 온다.
        try {

            adminMember = authorityService.getAdminMemberByNo(no);
        } catch (Exception e) {

            log.error("Fail to get adminMember, msg = " + e.getMessage());
        }

        if (!adminMember.getName().equals(name)) {

            adminMember.setName(name);
            logTemp += !logTemp.equals("") ? ", name = " + name : "name = " + name;
        }

        if (!adminMember.getPhone_number().equals(phone_number)) {

            adminMember.setPhone_number(phone_number);
            logTemp += !logTemp.equals("") ? ", phone_number = " + phone_number : "phone_number = " + phone_number;
        }

        if (adminMember.getRole() != role) {

            adminMember.setRole(role);
            logTemp += !logTemp.equals("") ? ", role = " + role : "role = " + role;
        }

        if (adminMember.getOtp_key() == null || !adminMember.getOtp_key().equals(otp_key)) {

            String setOtpKey = otp_key.equals("0") ? "" : otp_key;
            adminMember.setOtp_key(setOtpKey);
            logTemp += !logTemp.equals("") ? ", otp_key = " + setOtpKey : "otp_key = " + setOtpKey;
        }

        // 입력된 password가 있으면 암호화 해서 저장해야 함
        if (!password.equals("")) {

            // 입력된 password를 복호화하여 저장
            PrivateKey privateKey = (PrivateKey) session.getAttribute("RSA_PRIVATE_KEY");
            if (privateKey == null) {

                return result;
            }

            try {

                String decPassword = rsaUtil.getDecryptPassword(privateKey, password);
                adminMember.setPassword(PasswordUtil.createUserPassword(adminMember.getId(), decPassword));
            } catch (Exception e) {

                log.error("Fail to decrypt password, msg = " + e.getMessage());
            }

            // 관리자가 비밀번호를 변경해줄 경우 로그인시 첫화면에서 비밀번호 변경 페이지를 보여줘야하기 때문에 Y로 변경
            adminMember.setIs_password_init("Y");
            logTemp += !logTemp.equals("") ? ", is_password_init = Y, password change" : "is_password_init = Y, password change";
        }

        try {

            authorityService.modifyAdminMember(adminMember);

            if (!logTemp.equals("")) {

                logContent += logTemp;
                authorityService.insertAdminLog(request, 712, logContent, loginAdmin.getId());
            }

            result = "success";

            // 세션에 저장된 개인키 제거
            session.removeAttribute("RSA_PRIVATE_KEY");
        } catch (Exception e) {

            log.error("Fail to modify adminMember, msg = " + e.getMessage());
        }

        return result;
    }

    /**
     * @brief       접근권한 관리 삭제\n
     * @details     \n
     * @param       checkNo    관리자 번호들\n
     */
    @RequestMapping(value = "ajax/auth1_mod/delete", method = RequestMethod.POST)
    public @ResponseBody String auth1_delete(
            HttpServletRequest request,
            HttpSession session,
            @RequestParam(value = "checkNo", required = true) String checkNo
    ){

        String result = "fail";

        // 접속한 관리자 세션정보
        AdminMember loginAdmin = (AdminMember) session.getAttribute("member");

        String[] tempNo = checkNo.split(",");

        try {

            for (String no : tempNo) {

                AdminMember adminMember = authorityService.getAdminMemberByNo(Integer.parseInt(no));

                authorityService.deleteAdminMember(no);
                authorityService.insertAdminLog(request, 713, "Delete admin member, no=" + no + ", id=" + adminMember.getId() , loginAdmin.getId());
            }

            result = "success";
        } catch (Exception e) {

            log.error("Fail to delete adminMember, msg = " + e.getMessage());
        }

        return result;
    }

    /**
     * @brief       접근권한 관리 ID 체크\n
     * @details     \n
     * @param       id    관리자 ID\n
     */
    @RequestMapping(value = "ajax/auth1_mod/idcheck", method = RequestMethod.POST)
    public @ResponseBody int auth1_id_check(
            @RequestParam(value = "id", required = true) String id
    ){

        int result = 0;

        try {

            result = authorityService.checkId(id);
        } catch (Exception e) {

            log.error("Fail to check id, msg = " + e.getMessage());
        }

        return result;
    }

    /**
     * @brief       관리자 Log 페이지\n
     * @details     \n
     * @param       search_reg_dt    검색날짜\n
     * @param       search_id        검색아이디\n
     * @param       search_ip        검색아이피\n
     * @param       search_type      검색타입\n
     * @param       page             페이지\n
     */
    @RequestMapping(value = "auth2", method = RequestMethod.GET)
    public String authority2(
            HttpSession session,
            Model model,
            @RequestParam(value = "search_reg_dt", defaultValue = "", required = false) String search_reg_dt,
            @RequestParam(value = "search_id", defaultValue = "", required = false) String search_id,
            @RequestParam(value = "search_ip", defaultValue = "", required = false) String search_ip,
            @RequestParam(value = "search_type", defaultValue = "all", required = false) String search_type,
            @RequestParam(value = "search_listCount", defaultValue = "20", required = false) int search_listCount,
            @RequestParam(value = "page", defaultValue="1", required=false) int page
    ){

        // 등록일
        if (search_reg_dt == null || search_reg_dt.length() <= 0) {

            Date now = new Date();
            search_reg_dt = String.format("%s - %s", Utils.GetDateStringFromFormat(Utils.GetDateAddMonths(now, -1), "yyyy.MM.dd"), Utils.GetDateStringFromFormat(now, "yyyy.MM.dd"));
        }

        AdminLogResponse adminLogResponse = null;

        try {

            String[] reg_dts = search_reg_dt.split("-");
            adminLogResponse = authorityService.adminLogList(reg_dts[0], reg_dts[1], search_type, search_id, search_ip, false, page, search_listCount);
        } catch (Exception e) {

            log.error("Fail to get adminLogList, msg = " + e.getMessage());
        }

        model.addAttribute("member_proc", (AdminMember) session.getAttribute("member"));
        model.addAttribute("adminLogResponse", adminLogResponse);
        model.addAttribute("search_reg_dt", search_reg_dt);
        model.addAttribute("search_id", search_id);
        model.addAttribute("search_ip", search_ip);
        model.addAttribute("search_type", search_type);
        model.addAttribute("search_listCount", search_listCount);
        model.addAttribute("page", page);
        return "authority/auth2";
    }

    /* 시스템 변경 log excel 다운로드 */
    @RequestMapping(value="auth2/excelDown")
    public Object auth2ExcelDown(
            HttpSession session,
            HttpServletRequest request,
            @RequestParam(value = "search_reg_dt", defaultValue = "", required = false) String search_reg_dt,
            @RequestParam(value = "search_id", defaultValue = "", required = false) String search_id,
            @RequestParam(value = "search_ip", defaultValue = "", required = false) String search_ip,
            @RequestParam(value = "search_type", defaultValue = "all", required = false) String search_type,
            @RequestParam(value = "page", defaultValue="1", required=false) int page
    ){

        Map<String, Object> map = new HashMap<>();

        String[] reg_dts = search_reg_dt.split("-");
        //SET FILE NAME
        map.put(ExcelConstant.FILE_NAME, String.join("_", reg_dts[0].trim(), reg_dts[1].trim()) + "/admin_log_list");

        MessageUtil messageUtil = new MessageUtil();
        String no = messageUtil.getMessage(session, "auth2.table.thead.th.no");
        String type = messageUtil.getMessage(session, "auth2.table.tbody.th.type");
        String content = messageUtil.getMessage(session, "auth2.table.thead.th.content");
        String id = messageUtil.getMessage(session, "auth2.table.thead.th.id");
        String reg_dt = messageUtil.getMessage(session, "auth2.table.tbody.th.reg_dt");
        String ip = messageUtil.getMessage(session, "ip");

        String type_1 = messageUtil.getMessage(session, "login");
        String type_2 = messageUtil.getMessage(session, "auth2.type.2");
        String type_3 = messageUtil.getMessage(session, "modify.information");
        String type_171 = messageUtil.getMessage(session, "auth2.type.171");
        String type_211 = messageUtil.getMessage(session, "auth2.type.211");
        String type_221 = messageUtil.getMessage(session, "auth2.type.221");
        String type_231 = messageUtil.getMessage(session, "auth2.type.231");
        String type_232 = messageUtil.getMessage(session, "auth2.type.232");
        String type_241 = messageUtil.getMessage(session, "auth2.type.241");
        String type_261 = messageUtil.getMessage(session, "auth2.type.261");
        String type_321 = messageUtil.getMessage(session, "auth2.type.321");
        String type_331 = messageUtil.getMessage(session, "auth2.type.331");
        String type_332 = messageUtil.getMessage(session, "auth2.type.332");
        String type_333 = messageUtil.getMessage(session, "auth2.type.333");
        String type_341 = messageUtil.getMessage(session, "auth2.type.341");
        String type_361 = messageUtil.getMessage(session, "auth2.type.361");
        String type_362 = messageUtil.getMessage(session, "auth2.type.362");
        String type_363 = messageUtil.getMessage(session, "auth2.type.363");
        String type_381 = messageUtil.getMessage(session, "auth2.type.381");
        String type_3101 = messageUtil.getMessage(session, "auth2.type.3101");
        String type_3111 = messageUtil.getMessage(session, "auth2.type.3111");
        String type_3121 = messageUtil.getMessage(session, "auth2.type.3121");
        String type_3141 = messageUtil.getMessage(session, "auth2.type.3141");
        String type_3151 = messageUtil.getMessage(session, "auth2.type.3151");
        String type_3161 = messageUtil.getMessage(session, "auth2.type.3161");
        String type_521 = messageUtil.getMessage(session, "auth2.type.521");
        String type_541 = messageUtil.getMessage(session, "auth2.type.541");
        String type_591 = messageUtil.getMessage(session, "auth2.type.591");
        String type_611 = messageUtil.getMessage(session, "auth2.type.611");
        String type_612 = messageUtil.getMessage(session, "auth2.type.612");
        String type_613 = messageUtil.getMessage(session, "auth2.type.613");
        String type_621 = messageUtil.getMessage(session, "auth2.type.621");
        String type_622 = messageUtil.getMessage(session, "auth2.type.622");
        String type_623 = messageUtil.getMessage(session, "auth2.type.623");
        String type_631 = messageUtil.getMessage(session, "auth2.type.631");
        String type_632 = messageUtil.getMessage(session, "auth2.type.632");
        String type_633 = messageUtil.getMessage(session, "auth2.type.633");
        String type_641 = messageUtil.getMessage(session, "auth2.type.641");
        String type_642 = messageUtil.getMessage(session, "auth2.type.642");
        String type_643 = messageUtil.getMessage(session, "auth2.type.643");
        String type_644 = messageUtil.getMessage(session, "auth2.type.644");
        String type_651 = messageUtil.getMessage(session, "auth2.type.651");
        String type_652 = messageUtil.getMessage(session, "auth2.type.652");
        String type_653 = messageUtil.getMessage(session, "auth2.type.653");
        String type_654 = messageUtil.getMessage(session, "auth2.type.654");
        String type_711 = messageUtil.getMessage(session, "auth2.type.711");
        String type_712 = messageUtil.getMessage(session, "auth2.type.712");
        String type_713 = messageUtil.getMessage(session, "auth2.type.713");

        //SET HEADER
        map.put(ExcelConstant.HEAD,
                new ArrayList(){
                    {
                        add(Arrays.asList(
                                no, type, content, id, reg_dt, ip
                                )
                        );
                    }
                }
        );

        //SET CONTENT
        AdminLogResponse response = null;

        try {

            response = authorityService.adminLogList(reg_dts[0], reg_dts[1], search_type, search_id, search_ip, true, page, 20);
        } catch (Exception e) {

            log.error("Fail to get adminLogList, msg = " + e.getMessage());
        }

        List<List<String>> excelBody = new ArrayList<>();
        for(AdminLog adminLog : response.getAdminLogList()) {
            List<String> tempList = new ArrayList<>();
            tempList.add(String.valueOf(adminLog.getNo()));

            if (adminLog.getType() == 1) tempList.add(type_1);
            else if (adminLog.getType() == 2) tempList.add(type_2);
            else if (adminLog.getType() == 3) tempList.add(type_3);
            else if (adminLog.getType() == 171) tempList.add(type_171);
            else if (adminLog.getType() == 211) tempList.add(type_211);
            else if (adminLog.getType() == 221) tempList.add(type_221);
            else if (adminLog.getType() == 231) tempList.add(type_231);
            else if (adminLog.getType() == 232) tempList.add(type_232);
            else if (adminLog.getType() == 241) tempList.add(type_241);
            else if (adminLog.getType() == 261) tempList.add(type_261);
            else if (adminLog.getType() == 321) tempList.add(type_321);
            else if (adminLog.getType() == 331) tempList.add(type_331);
            else if (adminLog.getType() == 332) tempList.add(type_332);
            else if (adminLog.getType() == 333) tempList.add(type_333);
            else if (adminLog.getType() == 341) tempList.add(type_341);
            else if (adminLog.getType() == 361) tempList.add(type_361);
            else if (adminLog.getType() == 362) tempList.add(type_362);
            else if (adminLog.getType() == 363) tempList.add(type_363);
            else if (adminLog.getType() == 381) tempList.add(type_381);
            else if (adminLog.getType() == 3101) tempList.add(type_3101);
            else if (adminLog.getType() == 3111) tempList.add(type_3111);
            else if (adminLog.getType() == 3121) tempList.add(type_3121);
            else if (adminLog.getType() == 3141) tempList.add(type_3141);
            else if (adminLog.getType() == 3151) tempList.add(type_3151);
            else if (adminLog.getType() == 3161) tempList.add(type_3161);
            else if (adminLog.getType() == 521) tempList.add(type_521);
            else if (adminLog.getType() == 541) tempList.add(type_541);
            else if (adminLog.getType() == 591) tempList.add(type_591);
            else if (adminLog.getType() == 611) tempList.add(type_611);
            else if (adminLog.getType() == 612) tempList.add(type_612);
            else if (adminLog.getType() == 613) tempList.add(type_613);
            else if (adminLog.getType() == 621) tempList.add(type_621);
            else if (adminLog.getType() == 622) tempList.add(type_622);
            else if (adminLog.getType() == 623) tempList.add(type_623);
            else if (adminLog.getType() == 631) tempList.add(type_631);
            else if (adminLog.getType() == 632) tempList.add(type_632);
            else if (adminLog.getType() == 633) tempList.add(type_633);
            else if (adminLog.getType() == 641) tempList.add(type_641);
            else if (adminLog.getType() == 642) tempList.add(type_642);
            else if (adminLog.getType() == 643) tempList.add(type_643);
            else if (adminLog.getType() == 644) tempList.add(type_644);
            else if (adminLog.getType() == 651) tempList.add(type_651);
            else if (adminLog.getType() == 652) tempList.add(type_652);
            else if (adminLog.getType() == 653) tempList.add(type_653);
            else if (adminLog.getType() == 654) tempList.add(type_654);
            else if (adminLog.getType() == 711) tempList.add(type_711);
            else if (adminLog.getType() == 712) tempList.add(type_712);
            else if (adminLog.getType() == 713) tempList.add(type_713);
            else tempList.add("");

            tempList.add(adminLog.getContent());
            tempList.add(adminLog.getId());
            tempList.add(adminLog.getReg_dt().toString());
            tempList.add(adminLog.getIp());

            excelBody.add(tempList);
        }
        map.put(ExcelConstant.BODY, excelBody);

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        authorityService.insertAdminLog(request, 261, "Download excel system change log list", adminMember.getId());

        return new ExcelXlsxStreamingView(map);
    }




    @RequestMapping(value = "auth3", method = RequestMethod.GET)
    public String authority3(
            HttpSession session,
            Model model,
            @RequestParam(value = "search_type", defaultValue = "all", required = false) String search_type,
            @RequestParam(value = "search_content", defaultValue = "", required = false) String search_content,
            @RequestParam(value = "search_listCount", defaultValue = "20", required = false) int search_listCount,
            @RequestParam(value = "page", defaultValue="1", required=false) int page
    ){
        AdminMemberResponse adminMemberResponse = null;

        // 관리자리스트를 가지고 온다.
        try {
            adminMemberResponse = authorityService.adminMemberList(search_type, search_content, page, search_listCount);
        } catch (Exception e) {
            log.error("Fail to get admin member list, msg = " + e.getMessage());
        }

        model.addAttribute("member_proc", (AdminMember) session.getAttribute("member"));
        model.addAttribute("adminMemberResponse", adminMemberResponse);
        return "authority/auth3";
    }

    @ResponseBody
    @RequestMapping(value = "auth3/edit", method = RequestMethod.POST)
    public String authority3edit(AdminMemberAuth adminMemberAuth) {
        adminMemberAuth.getAdminid().forEach(id -> {
            int search = id.indexOf("!!!");
            authorityDao.changeAuthAdmin(id.substring(0, search), id.substring(search+3));
        });

        return "ok";
    }
}