package com.onetop.webadmin.controllers;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.onetop.webadmin.models.CoinInfo;
import com.onetop.webadmin.models.Counts;
import com.onetop.webadmin.models.RSA;
import com.onetop.webadmin.models.adjustment.MemberBalanceInfo;
import com.onetop.webadmin.models.authority.AdminMember;
import com.onetop.webadmin.models.bank.BankCoinRequest;
import com.onetop.webadmin.models.bank.WithdrawRequest;
import com.onetop.webadmin.models.member.WebMember;
import com.onetop.webadmin.responses.bank.BankCoinRequestListResponse;
import com.onetop.webadmin.services.BankService;
import com.onetop.webadmin.util.RSAUtil;
import com.onetop.webadmin.util.Utils;

@Controller
/**
 * 테스트용 더미 컨트롤러
 */
public class DummyController {
    private static final Logger log = LoggerFactory.getLogger(DummyController.class);
    @Autowired
    private BankService bankService;
    @Autowired
    private RSAUtil rsaUtil;
    
    @RequestMapping(value = "bank1_dummy")
    public String bank1(
            HttpSession session,
            HttpServletRequest request,
            Model model,
            @RequestParam(value = "sortName", defaultValue = "") String sortName,
            @RequestParam(value = "sortOrderBy", defaultValue = "") String sortOrderBy,
            @RequestParam(value = "search_listCount", defaultValue = "100", required = false) int search_listCount,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(value = "searchValue", defaultValue = "") String searchValue
    )
    {
        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        model.addAttribute("member_proc", adminMember);
        
        bank("deposit", session, model, request, sortName, sortOrderBy, page, false, search_listCount);

        model.addAttribute("page", page);
        model.addAttribute("search_listCount", search_listCount);
        model.addAttribute("searchValue", searchValue);
        
        // 테스트 데이터 추가
        List<BankCoinRequest> list = new ArrayList<BankCoinRequest>();
        BankCoinRequest row;
        
        row = new BankCoinRequest(); 
        row.setOd_id(100);
        row.setCoin_name("도지코인");
        row.setReg_dt(new Date(System.currentTimeMillis() - 90000000));
        row.setKST_reg_dt(row.getReg_dt());
        row.setMb_id("userid");
        row.setOd_receipt_coin(new BigDecimal(12.345678));
        row.setConfirm_cnt(3);
        row.setStatus("REQ");
        row.setConfirm_dt(new Date(System.currentTimeMillis()));
        list.add(row);
        
        row = new BankCoinRequest(); 
        row.setOd_id(101);
        row.setCoin_name("비트코인");
        row.setReg_dt(new Date(System.currentTimeMillis() - 130000000));
        row.setKST_reg_dt(row.getReg_dt());
        row.setMb_id("testuser");
        row.setOd_receipt_coin(new BigDecimal(4.78));
        row.setStatus("wait");
        row.setConfirm_dt(null);
        list.add(row);
        
        row = new BankCoinRequest(); 
        row.setOd_id(105);
        row.setCoin_name("비트코인");
        row.setReg_dt(new Date(System.currentTimeMillis() - 130000000));
        row.setKST_reg_dt(row.getReg_dt());
        row.setMb_id("testuser");
        row.setOd_receipt_coin(new BigDecimal(12.345678));
        row.setConfirm_cnt(3);
        row.setStatus("wait");
        row.setConfirm_dt(null);
        list.add(row);
        
        Counts count = new Counts();
        count.setTotal(list.size());
        count.setSearch(list.size());
        
        BankCoinRequestListResponse response = (BankCoinRequestListResponse) request.getAttribute("response");
        response.setBankCoinRequestList(list);
        response.setCounts(count);
        request.setAttribute("response", response);

        return "banking/bank1";
    }
    

    private void bank(String action, HttpSession session, Model model, HttpServletRequest request, String sortName, String sortOrderBy, int page, boolean isExcel, int search_listCount) {
        if (sortName == null || sortOrderBy == null || sortName.equals("") || sortOrderBy.equals("")) {
            sortName = "reg_dt";
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

    /**
     * 매출요청 금액 수정
     */
    @RequestMapping(value = "ajax/dummy/bank1/update")
    @ResponseBody
    public String ajaxBank1New(
            HttpSession session,
            HttpServletRequest request,
            @RequestParam(value = "idx", defaultValue = "") String idx,
            @RequestParam(value = "od_receipt_coin", defaultValue = "") String od_receipt_coin,
            @RequestParam(value = "buy_point", defaultValue = "") String buy_point) {
    	
        String result = "fail";

        try {
        	log.info("AJAX REQUEST :: ajax/bank1/update");
        	log.info("idx=" + idx);
        	log.info("od_receipt_coin=" + od_receipt_coin);
        	log.info("buy_point=" + buy_point);
        	
            // @todo: 매출요청 수정 처리
        	
            result = "success";
        } catch (Exception e) {
            log.error("Fail to Request Update, msg = " + e.getMessage());
        }

        return result;
    }
    
    @RequestMapping("bank1_mod_dummy")
    private Object bank1_mod(
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        model.addAttribute("member_proc", adminMember);

        String od_id = request.getParameter("od_id");
        
        // @TODO: 매출요청 조회
        
        // 테스트 데이터 추가
        BankCoinRequest row;
        row = new BankCoinRequest(); 
        row.setOd_id(100);
        row.setCoin_name("도지코인");
        row.setReg_dt(new Date(System.currentTimeMillis() - 90000000));
        row.setKST_reg_dt(row.getReg_dt());
        row.setMb_id("userid");
        row.setOd_receipt_coin(new BigDecimal(12.345678));
        row.setConfirm_cnt(3);
        row.setStatus("REQ");
        row.setConfirm_dt(new Date(System.currentTimeMillis()));
        model.addAttribute("bankCoinRequest", row);

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
    @RequestMapping(value = "bank1_mod_proc_dummy")
    @ResponseBody
    public String bank1_mod_proc(
            HttpSession session,
            HttpServletRequest request,
            @RequestParam(value = "action_type", defaultValue = "") String action_type,
            @RequestParam(value = "return_amount", defaultValue = "") String return_amount,
            @RequestParam(value = "reason", defaultValue = "") String reason) {
    	
        String result = "fail";

        try {
        	log.info("AJAX REQUEST :: ajax/bank1/update");
        	log.info("action_type=" + action_type);
        	log.info("return_amount=" + return_amount);
        	log.info("reason=" + reason);
        	
            // @todo: 매출처리
        	
            result = "success";
        } catch (Exception e) {
            log.error("Fail to Request Action, msg = " + e.getMessage());
        }

        return result;
    }
    
    /**
     * 관리자 출금 요청 목록
     */
    @RequestMapping(value = "bank33_dummy")
    public String bank33(
            HttpSession session,
            HttpServletRequest request,
            Model model,
            @RequestParam(value = "sortName", defaultValue = "") String sortName,
            @RequestParam(value = "sortOrderBy", defaultValue = "") String sortOrderBy,
            @RequestParam(value = "search_listCount", defaultValue = "100", required = false) int search_listCount,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(value = "searchValue", defaultValue = "") String searchValue
    )
    {
        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        model.addAttribute("member_proc", adminMember);
        
        bank("wait", session, model, request, sortName, sortOrderBy, page, false, search_listCount);

        model.addAttribute("page", page);
        model.addAttribute("search_listCount", search_listCount);
        model.addAttribute("searchValue", searchValue);

        return "banking/bank33";
    }

    /**
     * 관리자 출금 요청 상세
     */
    @RequestMapping("bank33_mod_dummy")
    private Object bank33_mod(
            HttpSession session,
            HttpServletRequest request,
            Model model
    ) {

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        model.addAttribute("member_proc", adminMember);

        String idx = request.getParameter("idx");
        
        // @TODO: 관리자 출금 요청 조회
        
        // 테스트 데이터 추가
        WithdrawRequest row;
        row = new WithdrawRequest(); 
        row.setIdx(100);
        row.setCoin_name("도지코인");
        row.setMb_no(2);
        row.setMb_id("userid");
        row.setStatus("REQ");
        model.addAttribute("withdrawRequest", row);

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

    /**
     * 관리자 출금 요청 처리
     */
    @RequestMapping(value = "bank33_mod_proc_dummy")
    @ResponseBody
    public String bank33_mod_proc(
            HttpSession session,
            HttpServletRequest request,
            @RequestParam(value = "mode", defaultValue = "") String mode,
            @RequestParam(value = "return_wallet", defaultValue = "") String return_wallet,
            @RequestParam(value = "withdraw_wallet", defaultValue = "") String withdraw_wallet,
            @RequestParam(value = "reason", defaultValue = "") String reason) {
    	
        String result = "fail";

        try {
        	log.info("AJAX REQUEST :: ajax/bank1/update");
        	log.info("mode=" + mode);
        	log.info("return_wallet=" + return_wallet);
        	log.info("withdraw_wallet=" + withdraw_wallet);
        	log.info("reason=" + reason);
        	
            // @todo: mode에 따른 출금요청 처리
        	
            result = "success";
        } catch (Exception e) {
            log.error("Fail to Request Action, msg = " + e.getMessage());
        }

        return result;
    }
   
}
