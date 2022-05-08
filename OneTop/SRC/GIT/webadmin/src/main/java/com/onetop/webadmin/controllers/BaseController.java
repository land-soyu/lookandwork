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

import com.onetop.webadmin.mappers.MemberDao;
import com.onetop.webadmin.models.RSA;
import com.onetop.webadmin.models.adjustment.MemberBalanceInfo;
import com.onetop.webadmin.models.authority.AdminMember;
import com.onetop.webadmin.models.member.MemberDownTree;
import com.onetop.webadmin.models.member.MemberWalletAddress;
import com.onetop.webadmin.models.member.ReferrerIdUpdate;
import com.onetop.webadmin.models.member.WebMember;
import com.onetop.webadmin.models.member.WebMemberMainReferer;
import com.onetop.webadmin.models.member.WebMemberMainSponsor;
import com.onetop.webadmin.responses.board.QnaResponse;
import com.onetop.webadmin.responses.member.BlackConsumerResponse;
import com.onetop.webadmin.services.*;
import com.onetop.webadmin.util.PasswordUtil;
import com.onetop.webadmin.util.RSAUtil;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BaseController {

    private static final Logger log = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private WebMemberListService webMemberListService;

    @Autowired
    private AdjustmentService adjustmentService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private RSAUtil rsaUtil;

    @Autowired
    private MemberDao memberDao;

    /**
     * @brief      메인 페이지\n
     * @details    \n
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String main(
            HttpSession session,
            HttpServletRequest request
    ) {

        // 세션에 관리자 정보가 없으면 로그인 페이지로 이동
        if (session.getAttribute("member") == null) {

            return "redirect:login?lang=ko";
        }

        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        // 초기비밀번호 설정을 하지않았으면 초기 비밀번호 변경하는 페이지로 이동
        if (adminMember.getIs_password_init().equals("Y")) {

            return "redirect:admin_mod?state=first";
        }
        // role = 3 (운영팀원)의 경우에는 모니터링을 볼 수 없기 때문에 시스템 관리 페이지로 이동
        else if (adminMember.getRole() == 3 || adminMember.getRole() == 4) {

            return "redirect:sys1";
        }

        // return "redirect:dashboard";
        return "redirect:bank1";
    }

    /**
     * @brief              로그인 페이지\n
     * @details            \n
     * @param      lang    언어(기본값은 설정할것에 따라 변경)\n
     */
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login(
            HttpSession session,
            Model model,
            @RequestParam(value = "lang", defaultValue = "", required = false) String lang
    ) {

        log.info("Login Page");

        // 세션에 관리자 정보가 존재하면 메인 페이지로 이동
        if (session.getAttribute("member") != null) {
            return "redirect:/";
        }

        if (lang.equals("")) {
            return "redirect:login?lang=ko";
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
        session.setAttribute("language", lang);
        model.addAttribute("lang", lang);
        return "login";
    }

    /**
     * @brief                 ajax 로그인 처리\n
     * @details               \n
     * @param     id          아아디\n
     * @param     password    비밀번호\n
     */
    @RequestMapping(value = "ajax/login_proc", method = RequestMethod.POST)
    public @ResponseBody
    String ajax_login_proc(
            HttpServletRequest request,
            HttpSession session,
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "password", required = true) String password
    ) {
        String result = "fail";

        AdminMember adminMember = null;

        // 아이디로 관리자 정보를 가져옴
        try {
            adminMember = authorityService.getAdminMemberById(id);
        } catch (Exception e) {
            log.error("Fail to get adminMember, id = " + id + ", msg = " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 계정이 존재하지 않을경우 로그인페이지로 이동
            if (adminMember == null) {
                return result;
            }
        }

        // 입력된 비밀번호를 암호화하여 저장된 비밀번호와 같은지 체크
        PrivateKey privateKey = (PrivateKey) session.getAttribute("RSA_PRIVATE_KEY");
        if (privateKey == null) {
            return result;
        }

        try {
            String encPassword = rsaUtil.getDecryptPassword(privateKey, password);
            if (PasswordUtil.matchesPassword(adminMember.getPassword(), encPassword, id)) {
                // 세션에 비밀번호 값을넣지 않는다.
                adminMember.setPassword("");
                session.setAttribute("member", adminMember);
                System.out.println("member - " + session.getAttribute("member"));
                result = "success";

                log.info("Success to login, id = " + id);
                log.info("Success to login, Is_password_init = " + adminMember.getIs_password_init());
                authorityService.insertAdminLog(request, 1, "", adminMember.getId());
            } else {
                return result;
            }
        } catch (Exception e) {
            log.error("Fail to decrypt password, id = " + id + ", msg = " + e.getMessage());
        }

        // 세션에 저장된 개인키 제거
        session.removeAttribute("RSA_PRIVATE_KEY");

        return result;
    }

    /**
     * @brief     로그아웃 처리\n
     * @details   \n
     */
    @RequestMapping("logout_proc")
    public String logout_proc(
            HttpServletRequest request,
            HttpSession session
    ) {
        // 세션에 관리자 정보가 없으면 로그인 페이지로 이동
        if (session.getAttribute("member") == null) {
            return "redirect:login";
        }

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        authorityService.insertAdminLog(request, 2, "", adminMember.getId());

        // 세션에 관리자 정보를 제거
        session.removeAttribute("member");

        return "redirect:login";
    }


    /**
     * 추천인 ID 검색
     **/
    @ResponseBody
    @RequestMapping(value = "referrerid_search", method = RequestMethod.POST)
    public String referrerId_search(
            HttpSession session,
            @RequestParam(value = "mb_id", required = true) String mb_id
    ) {
        // 세션에 관리자 정보가 없으면 로그인 페이지로 이동
        if (session.getAttribute("member") == null) {
            return "redirect:login";
        }

        WebMember webMember_self = memberDao.GetWebMember(mb_id);

        if(webMember_self==null) {
            return "no";
        }else{
            return "ok";
        }
    }


    /**
     * 추천인 ID 변경
     **/
    /*
    @ResponseBody
    @RequestMapping(value = "referrerid_proc", method = RequestMethod.POST)
    public String referrerId_proc(
            HttpServletRequest request,
            HttpSession session,
            @RequestParam(value = "mb_id", required = true) String mb_id,
            @RequestParam(value = "mb_parent_id", required = true) String mb_parent_id
    ) {
        // 세션에 관리자 정보가 없으면 로그인 페이지로 이동
        if (session.getAttribute("member") == null) {
            return "redirect:login";
        }

        WebMember webMember_self = memberDao.GetWebMember(mb_id);
        WebMember webMember_parent = memberDao.GetWebMember(mb_parent_id);

        ReferrerIdUpdate referrerIdUpdate = new ReferrerIdUpdate();

        referrerIdUpdate.setMb_id(mb_id);
        referrerIdUpdate.setMb_parent_id(webMember_parent.getMb_no());
        referrerIdUpdate.setPosition_self(webMember_self.getMb_position());
        referrerIdUpdate.setPosition_parent(webMember_parent.getMb_position());
        referrerIdUpdate.setMb_sort_info_self(webMember_self.getMb_sort_info());
        referrerIdUpdate.setMb_sort_info_target(webMember_self.getMb_sort_info().substring(0, webMember_self.getMb_sort_info().length()-6));
        referrerIdUpdate.setMb_sort_info_change(webMember_parent.getMb_sort_info());

        String result = "fail";
        try {
            memberService.updateReferrerId(referrerIdUpdate);
            result = "success";
        } catch (Exception e) {
            log.error("Fail Auth email, msg = " + e.getMessage());
        }
        return result;
    }
    */


    /**
     * 회원 상세 정보
     **/
    @RequestMapping("member_detail")
    public String member_detail(
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {
        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        model.addAttribute("member_proc", adminMember);

        String mb_id = request.getParameter("mb_id");

        WebMember memberInfo = null;

        if(adminMember.getRole() != 1){
            try {
                memberInfo = webMemberListService.GetWebMember(mb_id);
            } catch (Exception e) {
                log.error("Fail to get member info, msg = " + e.getMessage());
            }

            model.addAttribute("memberInfo", memberInfo);
            System.out.println(memberInfo.getMb_level());

            List<MemberBalanceInfo> memberBalanceInfoList = adjustmentService.GetMemberBalanceInfo(memberInfo.getMb_no());
            request.setAttribute("memberBalanceInfo", memberBalanceInfoList);

            MemberBalanceInfo totalBalanceInfo = new MemberBalanceInfo();
            for(MemberBalanceInfo memberBalanceInfo : memberBalanceInfoList){
                totalBalanceInfo.setExc_avail(totalBalanceInfo.getExc_avail().add(memberBalanceInfo.getExc_avail()));
                totalBalanceInfo.setExc_in_withdraw(totalBalanceInfo.getExc_in_withdraw().add(memberBalanceInfo.getExc_in_withdraw()));
            }
            request.setAttribute("memberBalanceInfoTotal", totalBalanceInfo);

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
            request.setAttribute("memberBalanceInfoSyncDate", adjustmentService.GetMbBalanceInfoSyncDate());
        } else {
            model.addAttribute("memberInfo", null);
        }

        //List<MemberDownTree> memberDownTree = memberDao.getMemberDownTree(memberInfo.getMb_sort_info(), memberInfo.getMb_position());

        // member_id로 문의/답변 가져오기
        QnaResponse qnaResponse = null;
        try {
            qnaResponse = boardService.qnaList("all","","", mb_id, "", 0, 0, "", 1, 20);
        } catch (Exception e) {
            log.error("Fail to get member qna list. msg = " + e.getMessage());
        }

        List<MemberWalletAddress> memberWalletAddressList = new ArrayList<MemberWalletAddress>();
        try {
            memberWalletAddressList = memberService.getMemberWalletAddressList(mb_id);
        } catch (Exception e) {
            log.error("Fail to get member wallet address list, msg = " + e.getMessage());
        }

        BlackConsumerResponse blackConsumerResponse = null;
        try {
            blackConsumerResponse = memberService.blackConsumerList("all","","", mb_id, false, 1, 20);
        } catch (Exception e) {
            log.error("Fail to get blackconsumer list. msg = " + e.getMessage());
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

        model.addAttribute("memberWalletAddressList", memberWalletAddressList);
        model.addAttribute("qnaResponse", qnaResponse);
        model.addAttribute("blackConsumerResponse", blackConsumerResponse);
        //model.addAttribute("memberDownTree", memberDownTree);

        return "member/mem1_mod";
    }
    /**
     * 회원 상세 정보
     **/
    @RequestMapping("member_detail_no")
    public String member_detail_no(
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {
        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        model.addAttribute("member_proc", adminMember);

        String mb_id = request.getParameter("mb_id");
        String mb_no = request.getParameter("mb_no");

        WebMember memberInfo = null;

        if(adminMember.getRole() != 1){
            try {
                memberInfo = webMemberListService.GetWebMember_no(mb_no);
            } catch (Exception e) {
                log.error("Fail to get member info, msg = " + e.getMessage());
            }

            model.addAttribute("memberInfo", memberInfo);
            System.out.println(memberInfo.getMb_level());

            List<MemberBalanceInfo> memberBalanceInfoList = adjustmentService.GetMemberBalanceInfo(memberInfo.getMb_no());
            request.setAttribute("memberBalanceInfo", memberBalanceInfoList);

            MemberBalanceInfo totalBalanceInfo = new MemberBalanceInfo();
            for(MemberBalanceInfo memberBalanceInfo : memberBalanceInfoList){
                totalBalanceInfo.setExc_avail(totalBalanceInfo.getExc_avail().add(memberBalanceInfo.getExc_avail()));
                totalBalanceInfo.setExc_in_withdraw(totalBalanceInfo.getExc_in_withdraw().add(memberBalanceInfo.getExc_in_withdraw()));
            }
            request.setAttribute("memberBalanceInfoTotal", totalBalanceInfo);

            request.setAttribute("memberBalanceLogResponse", adjustmentService.GetMemberBalanceLog_no(
                    null,
                    null,
                    mb_no,
                    null,
                    null,
                    null,
                    null,
                    -1,
                    20,
                    false));

            request.setAttribute("memberBalanceLogSyncDate", adjustmentService.GetMbBalanceLogSyncDate());
            request.setAttribute("memberBalanceInfoSyncDate", adjustmentService.GetMbBalanceInfoSyncDate());
        } else {
            model.addAttribute("memberInfo", null);
        }

        //List<MemberDownTree> memberDownTree = memberDao.getMemberDownTree(memberInfo.getMb_sort_info(), memberInfo.getMb_position());

        // member_id로 문의/답변 가져오기
        QnaResponse qnaResponse = null;
        try {
            qnaResponse = boardService.qnaList("all","","", mb_id, "", 0, 0, "", 1, 20);
        } catch (Exception e) {
            log.error("Fail to get member qna list. msg = " + e.getMessage());
        }

        List<MemberWalletAddress> memberWalletAddressList = new ArrayList<MemberWalletAddress>();
        try {
            memberWalletAddressList = memberService.getMemberWalletAddressList(mb_id);
        } catch (Exception e) {
            log.error("Fail to get member wallet address list, msg = " + e.getMessage());
        }

        BlackConsumerResponse blackConsumerResponse = null;
        try {
            blackConsumerResponse = memberService.blackConsumerList("all","","", mb_id, false, 1, 20);
        } catch (Exception e) {
            log.error("Fail to get blackconsumer list. msg = " + e.getMessage());
        }


        WebMemberMainSponsor mainSponsorL = null;
        try {
            mainSponsorL = memberDao.getWebMemeberMainSponser(mb_id, "L");
        } catch (Exception e) {
            log.error("Fail to get WebMemberMainSponsor LEFT. msg = " + e.getMessage());
        }
        model.addAttribute("mainSponsorL", mainSponsorL);
        WebMemberMainSponsor mainSponsorR = null;
        try {
            mainSponsorR = memberDao.getWebMemeberMainSponser(mb_id, "R");
        } catch (Exception e) {
            log.error("Fail to get WebMemberMainSponsor LEFT. msg = " + e.getMessage());
        }
        model.addAttribute("mainSponsorR", mainSponsorR);
        WebMemberMainReferer mainReferer = null;
        try {
            mainReferer = memberDao.getWebMemeberMainReferer(mb_no);
        } catch (Exception e) {
            log.error("Fail to get WebMemberMainSponsor LEFT. msg = " + e.getMessage());
        }
        model.addAttribute("mainReferer", mainReferer);



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

        model.addAttribute("memberWalletAddressList", memberWalletAddressList);
        model.addAttribute("qnaResponse", qnaResponse);
        model.addAttribute("blackConsumerResponse", blackConsumerResponse);
        //model.addAttribute("memberDownTree", memberDownTree);

        return "member/mem1_mod";
    }



    /**
     * @brief                 관리자 정보 수정 화면(처음 비밀번호 변경안했을때도 보여줄 화면)\n
     * @details               \n
     * @param      state      로그인시에 첫 비밀번호 변경 페이지인지 체크하기 위한 값\n
     */
    @RequestMapping(value = "admin_mod", method = RequestMethod.GET)
    public String admin_mod(
            HttpSession session,
            Model model,
            @RequestParam(value = "state", defaultValue = "", required = false) String state
    ) {

        log.info("Admin Member Modify Page");

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        model.addAttribute("member_proc", adminMember);

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

        model.addAttribute("state", state);
        return "authority/auth1_mod2";
    }

    /**
     * @brief                    기존 비밀번호 체크\n
     * @details                  \n
     * @param     old_password   기존 비밀번호\n
     */
    @RequestMapping(value = "ajax/check_old_password", method = RequestMethod.POST)
    public @ResponseBody String check_old_password(
            HttpSession session,
            @RequestParam(value = "old_password", required = true) String old_password
    ) {

        String result = "fail";

        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        // 번호로 관리자 정보를 가지고 온다.
        try {

            adminMember = authorityService.getAdminMemberByNo(adminMember.getNo());
        } catch (Exception e) {

            log.error("Fail to get adminMember, msg = " + e.getMessage());
        }

        // 입력한 비밀번호를 암호화 처리하여 기존 비밀번호와 비교한다.
        String encPassword = old_password;

        if (PasswordUtil.matchesPassword(adminMember.getPassword(), encPassword, adminMember.getId())) {

            result = "success";
        }

        return result;
    }

    /**
     * @brief                    개인 정보 수정 처리\n
     * @details                  \n
     * @param     phone_number   관리자 전화번호\n
     * @param     password       변경할 비밀번호\n
     */
    @RequestMapping(value = "ajax/admin_mod/modify", method = RequestMethod.POST)
    public @ResponseBody String admin_mod_proc(
            HttpServletRequest request,
            HttpSession session,
            @RequestParam(value = "phone_number", defaultValue = "", required = false) String phone_number,
            @RequestParam(value = "password", defaultValue = "", required = false) String password
    ) {

        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        String result = "fail";
        String logTemp = "";
        String logContent = "Modify my info, no=" + adminMember.getNo() + ", ";

        // 번호로 관리자 정보를 가지고 온다.
        try {

            adminMember = authorityService.getAdminMemberByNo(adminMember.getNo());
        } catch (Exception e) {

            log.error("Fail to get adminMember, msg = " + e.getMessage());
        }

        if (!adminMember.getPhone_number().equals(phone_number)) {

            adminMember.setPhone_number(phone_number);
            logTemp += !logTemp.equals("") ? ", phone_number = " + phone_number : "phone_number = " + phone_number;
        }

        // 비밀번호가 넘어왔을 경우에만
        if (!password.equals("")) {

            // 입력된 비밀번호를 복호화하여 저장
            PrivateKey privateKey = (PrivateKey) session.getAttribute("RSA_PRIVATE_KEY");
            if (privateKey == null) return result;

            try {

                String decPassword = rsaUtil.getDecryptPassword(privateKey, password);
                adminMember.setPassword(PasswordUtil.createUserPassword(adminMember.getId(), decPassword));
            } catch (Exception e) {

                log.error("Fail to decrypt password, msg = " + e.getMessage());
            }

            adminMember.setIs_password_init("N");
            logTemp += !logTemp.equals("") ? ", is_password_init = N, password change" : "is_password_init = N, password chagne";
        }

        try {

            authorityService.modifyAdminMember(adminMember);
            // 변경된 관리자 정보로 세션값 변경
            session.setAttribute("member", adminMember);

            if (!logTemp.equals("")) {

                logContent += logTemp;
                authorityService.insertAdminLog(request, 3, logContent, adminMember.getId());
            }

            result = "success";
        } catch (Exception e) {

            log.error("Fail to modify admin member, msg = " + e.getMessage());
        }

        if (result.equals("success")) {

            // 세션에 저장된 개인키 제거
            session.removeAttribute("RSA_PRIVATE_KEY");
        }

        return result;
    }
}