package com.onetop.webadmin.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.onetop.webadmin.mappers.AdjustmentDao;
import com.onetop.webadmin.mappers.SystemDao;
import com.onetop.webadmin.models.adjustment.CoinNowPrice;
import com.onetop.webadmin.models.authority.AdminLog;
import com.onetop.webadmin.models.authority.AdminMember;
import com.onetop.webadmin.models.system.*;
import com.onetop.webadmin.responses.authority.AdminLogResponse;
import com.onetop.webadmin.services.AuthorityService;
import com.onetop.webadmin.services.SystemService;
import com.onetop.webadmin.services.excel.ExcelConstant;
import com.onetop.webadmin.services.excel.ExcelXlsxStreamingView;
import com.onetop.webadmin.util.MessageUtil;
import com.onetop.webadmin.util.Utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

@Controller
public class SystemController {

    @Autowired
    SystemDao systemDao;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    SystemService systemService;

    @Autowired
    AdjustmentDao adjustmentDao;

    private static final Logger log = LoggerFactory.getLogger(SystemController.class);


    @Value("${server.folder.banner-upload}")
    private String uploadFolder;



    @RequestMapping("/basicextrapay")
    public ModelAndView basicExtraPay(HttpSession session, @RequestParam(value="mode", defaultValue = "read") String mode)
    {
        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        ModelAndView mv = new ModelAndView();

        mv.addObject("member_proc", adminMember);
        mv.addObject("mode", (mode==null)?"read":mode);
        mv.addObject("basicExtraPay", systemDao.getBasicExtraPay());
        mv.setViewName("system/basicExtraPay");

        return mv;
    }

    @RequestMapping(value = "/basicextrapay/add", method = RequestMethod.POST)
    public @ResponseBody
    String addExtraPay(@RequestBody ArrayList<BasicExtraPay> basicExtraPays, HttpSession session, HttpServletRequest request)
    {
        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        String logContent = "BasicExtraPay Update";

        if (basicExtraPays == null) return "fail";

        try {
            // 기존 기본 수당
            List<BasicExtraPay> oldExtraPay = systemDao.getBasicExtraPay();

            systemService.updateBasicextraPay(basicExtraPays);


            for (BasicExtraPay item : basicExtraPays) {

                String logTemp = "";
                String logStartTemp = ", [" + item.getIdx() + "] ";

                // 일출금 설정 정보가 변경되었으면
                if (oldExtraPay.get(item.getIdx()-1).getMinimum().compareTo(item.getMinimum()) != 0)
                    logTemp += ",minimum = " + oldExtraPay.get(item.getIdx()-1).getMinimum() +"->" + item.getMinimum();
                if (oldExtraPay.get(item.getIdx()-1).getMaximum().compareTo(item.getMaximum()) != 0)
                    logTemp += ",maximum = " + oldExtraPay.get(item.getIdx()-1).getMaximum() +"->" + item.getMaximum();
                if (oldExtraPay.get(item.getIdx()-1).getPaymentrate().compareTo(item.getPaymentrate()) != 0)
                    logTemp += ",paymentrate = " + oldExtraPay.get(item.getIdx()-1).getPaymentrate() +"->"+ item.getPaymentrate();

                if (!logTemp.equals("")) logContent += logStartTemp + logTemp;
            }

            // 변경된 사항이 있으면 로그를 남긴다.
            if (!logContent.equals("BasicExtraPay Update")) authorityService.insertAdminLog(request, 271, logContent, adminMember.getId());
            return "success";
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return "error";
    }



    @RequestMapping("/sponsorextrapay")
    public ModelAndView sponsorExtraPay( HttpSession session, @RequestParam(value="mode", defaultValue = "read") String mode)
    {
        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        ModelAndView mv = new ModelAndView();

        mv.addObject("member_proc", adminMember);
        mv.addObject("mode", (mode==null)?"read":mode);
        mv.addObject("sponsorExtraPay", systemDao.getSponsorExtraPay());
        mv.setViewName("system/sponsorExtraPay");

        return mv;
    }

    @RequestMapping(value = "/sponsorextrapay/add", method = RequestMethod.POST)
    public @ResponseBody String addSponsorExtraPay(@RequestBody ArrayList<SponsorExtraPay> sponsorExtraPays, HttpSession session, HttpServletRequest request)
    {
        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        String logContent = "SponsorExtraPay Update";

        if (sponsorExtraPays == null) return "fail";

        try {
            // 기존 기본 수당
            List<SponsorExtraPay> oldExtraPay = systemDao.getSponsorExtraPay();

            systemService.updateSponsorExtraPay(sponsorExtraPays);

            for (SponsorExtraPay item : sponsorExtraPays) {

                String logTemp = "";
                String logStartTemp = ", [" + item.getIdx() + "] ";

                if (oldExtraPay.get(item.getIdx()-1).getPaymentrate().compareTo(item.getPaymentrate()) != 0)
                    logTemp += ",paymentrate = " + oldExtraPay.get(item.getIdx()-1).getPaymentrate() +"->"+ item.getPaymentrate();

                if (!logTemp.equals("")) logContent += logStartTemp + logTemp;
            }

            // 변경된 사항이 있으면 로그를 남긴다.
            if (!logContent.equals("SponsorExtraPay Update")) authorityService.insertAdminLog(request, 272, logContent, adminMember.getId());
            return "success";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "error";
    }



    @RequestMapping("/encourageextrapay")
    public ModelAndView encourageExtraPay( HttpSession session, @RequestParam(value="mode", defaultValue = "read") String mode)
    {
        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        ModelAndView mv = new ModelAndView();

        mv.addObject("member_proc", adminMember);
        mv.addObject("mode", (mode==null)?"read":mode);
        mv.addObject("encourageExtraPay", systemDao.getEncourageExtraPay());
        mv.setViewName("system/encourageExtraPay");

        return mv;
    }

    @RequestMapping(value = "/encourageextrapay/add", method = RequestMethod.POST)
    public @ResponseBody String addEncourageExtraPay(@RequestBody ArrayList<EncourageExtraPay> encourageExtraPay, HttpSession session, HttpServletRequest request)
    {
        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        String logContent = "EncourageExtraPay Update";

        if (encourageExtraPay == null) return "fail";

        try {
            // 기존 기본 수당
            List<EncourageExtraPay> oldExtraPay = systemDao.getEncourageExtraPay();

            systemService.updateEncourageExtraPay(encourageExtraPay);

            for (EncourageExtraPay item : encourageExtraPay) {

                String logTemp = "";
                String logStartTemp = ", [" + item.getIdx() + "] ";

                if (oldExtraPay.get(item.getIdx()-1).getPaymentrate().compareTo(item.getPaymentrate()) != 0)
                    logTemp += ",paymentrate = " + oldExtraPay.get(item.getIdx()-1).getPaymentrate() +"->"+ item.getPaymentrate();

                if (!logTemp.equals("")) logContent += logStartTemp + logTemp;
            }

            // 변경된 사항이 있으면 로그를 남긴다.
            if (!logContent.equals("EncourageExtraPay Update")) authorityService.insertAdminLog(request, 273, logContent, adminMember.getId());
            return "success";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "error";
    }



    @RequestMapping("/rankextrapay")
    public ModelAndView rankExtraPay( HttpSession session, @RequestParam(value="mode", defaultValue = "read") String mode)
    {
        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        ModelAndView mv = new ModelAndView();

        mv.addObject("member_proc", adminMember);
        mv.addObject("mode", (mode==null)?"read":mode);
        mv.addObject("rankExtraPay", systemDao.getRankExtraPay());
        mv.setViewName("system/rankExtraPay");

        return mv;
    }

    @RequestMapping(value = "/rankextrapay/add", method = RequestMethod.POST)
    public @ResponseBody String addRankExtraPay(@RequestBody ArrayList<RankExtraPay> rankExtraPay, HttpSession session, HttpServletRequest request)
    {
        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        String logContent = "RankExtraPay Update";

        if (rankExtraPay == null) return "fail";

        try {
            // 기존 기본 수당
            List<RankExtraPay> oldExtraPay = systemDao.getRankExtraPay();

            systemService.updateRankExtraPay(rankExtraPay);

            for (RankExtraPay item : rankExtraPay) {

                String logTemp = "";
                String logStartTemp = ", [" + item.getIdx() + "] ";

                if (oldExtraPay.get(item.getIdx()-1).getInvesttoltal().compareTo(item.getInvesttoltal()) != 0)
                    logTemp += ",investtotal = " + oldExtraPay.get(item.getIdx()-1).getInvesttoltal() +"->"+ item.getInvesttoltal();

                //if (oldExtraPay.get(item.getIdx()-1).getStep1_count().compareTo(item.getStep1_count()) != 0)
                //  logTemp += ",step1_count = " + oldExtraPay.get(item.getIdx()-1).getStep1_count() +"->"+ item.getStep1_count();

                if (oldExtraPay.get(item.getIdx()-1).getFive_high_count().compareTo(item.getFive_high_count()) != 0)
                    logTemp += ",five_high_count = " + oldExtraPay.get(item.getIdx()-1).getFive_high_count() +"->"+ item.getFive_high_count();

                if (oldExtraPay.get(item.getIdx()-1).getPaymentrate().compareTo(item.getPaymentrate()) != 0)
                    logTemp += ",paymentrate = " + oldExtraPay.get(item.getIdx()-1).getPaymentrate() +"->"+ item.getPaymentrate();

                if (!logTemp.equals("")) logContent += logStartTemp + logTemp;
            }

            // 변경된 사항이 있으면 로그를 남긴다.
            if (!logContent.equals("RankExtraPay Update")) authorityService.insertAdminLog(request, 274, logContent, adminMember.getId());
            return "success";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "error";
    }



    @RequestMapping("sys{pageNo}")
    public String system(
            @PathVariable String pageNo,
            HttpSession session,
            HttpServletRequest request,
            Model model,
            @RequestParam(value="mode", defaultValue = "read") String mode,
            @RequestParam(value = "search_listCount", defaultValue = "20", required = false) int search_listCount
    ){
        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        model.addAttribute("member_proc", adminMember);

        switch (Integer.parseInt(pageNo)) {
            case 1:
                sys1(request, session, model, mode);
                break;
            case 2:
                sys2(request, session, model, mode);
                break;
            case 3:
                sys3(request, session, model, mode);
                break;
            case 6:
                sys6(request, session, model, search_listCount);
                break;
           case 7:
            //    sys7(request, session, model, mode);
               break;
            case 9:
                sys9(request, session, model, mode);
                break;
            default:
                break;
        }

        return "system/sys" + pageNo;
    }

    /* 거래수수료 페이지 */
    private void sys1(HttpServletRequest request, HttpSession session, Model model, String mode) {

        if (mode == null) {

            model.addAttribute("mode", "read");
        } else {

            model.addAttribute("mode", mode);
        }

        model.addAttribute("systemFee", systemDao.GetSystemFee());
        request.setAttribute("systemFee", systemDao.GetSystemFee());
    }

    /* 거래수수료 import */
    @RequestMapping("sys1_proc")
    public @ResponseBody String sys1_proc(
            @RequestBody SystemFee systemFeeRequest,
            HttpSession session,
            HttpServletRequest request,
            Model model,
            @RequestParam(value="mode", defaultValue = "read") String mode
    ){

        sys1(request, session, model, mode);

        String logContent = "Update system fee";
        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        if (systemFeeRequest == null) return "fail";

        try {

            systemDao.UpdateSystemFee(systemFeeRequest);

            // 기존 거래 수수료
            List<SystemFee> oldOrderFee = (List<SystemFee>) request.getAttribute("systemFee");

            for (SystemFee item : oldOrderFee) {
                // 수수료가 변경되었으면
                if (!item.getInvest_fee().equals(systemFeeRequest.getInvest_fee()) ) logContent += ", invest_fee = " + systemFeeRequest.getInvest_fee();
                if (!item.getRecover_max30().equals(systemFeeRequest.getRecover_max30()) ) logContent += ", recover_max30_fee = " + systemFeeRequest.getRecover_max30();
                if (!item.getRecover_min30().equals(systemFeeRequest.getRecover_min30()) ) logContent += ", recover_min30_fee = " + systemFeeRequest.getRecover_min30();
            }

            // 변경된 사항이 있으면 로그를 남긴다.
            if (!logContent.equals("Update system fee")) authorityService.insertAdminLog(request, 211, logContent, adminMember.getId());

            return "success";
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return "error";
    }

    /* 투자 비율 설정 페이지 */
    private void sys2(HttpServletRequest request, HttpSession session, Model model, String mode){

        if (mode == null) {
            model.addAttribute("mode", "read");
        } else {
            model.addAttribute("mode", mode);
        }

        model.addAttribute("investSetting", systemDao.GetInvestSetting());
        request.setAttribute("investSetting", systemDao.GetInvestSetting());

        model.addAttribute("priceSetting", systemDao.GetPriceSetting());
        request.setAttribute("priceSetting", systemDao.GetPriceSetting());

        model.addAttribute("priceSettingLSR", systemDao.GetPriceSettingLSR());
        request.setAttribute("priceSettingLSR", systemDao.GetPriceSettingLSR());

        model.addAttribute("nowPriceETH", systemDao.GetNowPriceETH());
        model.addAttribute("nowPriceBTC", systemDao.GetNowPriceBTC());
    }


    /* 투자 비율 import */
    @RequestMapping("sys2_proc")
    public @ResponseBody String sys2proc(
            HttpSession session,
            HttpServletRequest request,
            Model model,
            @RequestBody SystemPriceInvestSetting systemPriceInvestSetting,
            @RequestParam(value="mode", defaultValue = "read") String mode
    ) {
        sys2(request, session, model, mode);

        String logContent = "Update Invest Auto And Value";
        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        if (systemPriceInvestSetting == null) return "fail";

        try {
            systemDao.UpdateInvestSettingAuto(systemPriceInvestSetting.getInvest());
            systemDao.UpdatePriceSettingAuto(systemPriceInvestSetting.getPrice());

            CoinNowPrice coinNowPrice = new CoinNowPrice();
            coinNowPrice.setCoin_name("LST");
            coinNowPrice.setKrw_price(new BigDecimal(String.format("%.4f", systemDao.GetNowPriceBTC().getKrw_price().divide(systemDao.GetNowPriceBTC().getUsd_price(), MathContext.DECIMAL32).multiply(systemPriceInvestSetting.getPrice().get(2).getManual_value(),MathContext.DECIMAL32))));
            coinNowPrice.setUsd_price(systemPriceInvestSetting.getPrice().get(2).getManual_value());    // index 2 - LST

            adjustmentDao.defaultCoinNowPriceUpdate(coinNowPrice);


            //CoinNowPrice coinNowPrice = new CoinNowPrice();
            coinNowPrice.setCoin_name("LSR");
            coinNowPrice.setKrw_price(new BigDecimal(String.format("%.4f", systemDao.GetNowPriceBTC().getKrw_price().divide(systemDao.GetNowPriceBTC().getUsd_price(), MathContext.DECIMAL32).multiply(systemPriceInvestSetting.getPrice().get(3).getManual_value(),MathContext.DECIMAL32))));
            coinNowPrice.setUsd_price(systemPriceInvestSetting.getPrice().get(3).getManual_value());    // index 3 - LSR

            adjustmentDao.defaultCoinNowPriceUpdate(coinNowPrice);

            List<SystemInvestSetting> tempInvestSetting = (List<SystemInvestSetting>) request.getAttribute("investSetting");

            SystemPriceSetting tempPriceSetting = (SystemPriceSetting) request.getAttribute("priceSetting");

            HashMap<Integer, SystemPriceSetting> tempPrice = new HashMap<Integer, SystemPriceSetting>();
            for (SystemPriceSetting item : systemPriceInvestSetting.getPrice()) {
                tempPrice.put(item.getIdx(), item);
            }


            String logTemp = "";
            String logStartTemp = ", [" + tempPriceSetting.getCoin_name() + "] ";
            if( tempPriceSetting.getRatio_auto() != tempPrice.get(tempPriceSetting.getIdx()).getRatio_auto())
                logTemp += ",ratio_auto="+ tempPriceSetting.getRatio_auto()+ "->" + tempPrice.get(tempPriceSetting.getIdx()).getRatio_auto();
            if( !tempPriceSetting.getManual_value().equals(tempPrice.get(tempPriceSetting.getIdx()).getManual_value()))
                logTemp += ",manual_value="+ tempPriceSetting.getManual_value()+ "->" + tempPrice.get(tempPriceSetting.getIdx()).getManual_value();

            if (!logTemp.equals("")) logContent += logStartTemp + logTemp;


            for (SystemInvestSetting setting : tempInvestSetting) {
                if (!setting.getReinvest_amount().equals(systemPriceInvestSetting.getInvest().getReinvest_amount()))
                    logContent += ",reinvest_amount="+ setting.getReinvest_amount()+ "->" + systemPriceInvestSetting.getInvest().getReinvest_amount();
                if (!setting.getInvest_min().equals(systemPriceInvestSetting.getInvest().getInvest_min()))
                    logContent += ",invest_min ="+ setting.getInvest_min()+ "->"  + systemPriceInvestSetting.getInvest().getInvest_min();
                if (!setting.getExtrapay_rate().equals(systemPriceInvestSetting.getInvest().getExtrapay_rate()))
                    logContent += ",extrapay_rate="+ setting.getExtrapay_rate()+ "->" + systemPriceInvestSetting.getInvest().getExtrapay_rate();
                if (!setting.getTarget_invest_rate().equals(systemPriceInvestSetting.getInvest().getTarget_invest_rate()))
                    logContent += ",target_invest_rate="+ setting.getTarget_invest_rate()+ "->" + systemPriceInvestSetting.getInvest().getTarget_invest_rate();
            }
            // 변경된 사항이 있으면 로그를 남긴다.
            if (!logContent.equals("Update Invest Auto And Value")) authorityService.insertAdminLog(request, 221, logContent, adminMember.getId());

            return "success";
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return "error";
    }

    /* 출금 수수료 페이지 */
    private void sys3(HttpServletRequest request, HttpSession session, Model model, String mode){

        if (mode == null) {

            model.addAttribute("mode", "read");
        } else {

            model.addAttribute("mode", mode);
        }

        model.addAttribute("withdrawFee", systemDao.GetWithdrawFee());
        request.setAttribute("withdrawFee", systemDao.GetWithdrawFee());
    }

    /* 출금 수수료 import */
    @RequestMapping("sys3_proc")
    public @ResponseBody String sys3proc(
            @RequestBody List<SystemWithdrawFee> withdrwaFeeRequest,
            HttpSession session,
            HttpServletRequest request,
            Model model,
            @RequestParam(value="mode", defaultValue = "read") String mode
    ){

        sys3(request, session, model, mode);

        String logContent = "Update withdraw fee";
        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        if (withdrwaFeeRequest == null) return "fail";

        try {

            systemDao.UpdateWithdrawFee(withdrwaFeeRequest);

            // 기존 입출금 수수료
            HashMap<String, SystemWithdrawFee> tempFee = new HashMap<String, SystemWithdrawFee>();
            List<SystemWithdrawFee> oldBankFee = (List<SystemWithdrawFee>) request.getAttribute("withdrawFee");

            for (SystemWithdrawFee fee : oldBankFee) {

                tempFee.put(fee.getCoinname(), fee);
            }

            for (SystemWithdrawFee item : withdrwaFeeRequest) {

                String logTemp = "";
                String logStartTemp = ", [" + item.getCoinname() + "] ";

                // 수수료 정보가 변경되었으면
                if (tempFee.get(item.getCoinname()).getMax30().compareTo(item.getMax30()) != 0)
                    logTemp += "  max30_fee = " + item.getMax30();
                if (tempFee.get(item.getCoinname()).getMin30().compareTo(item.getMin30()) != 0)
                    logTemp += "  min30_fee = " + item.getMin30();

                if (!logTemp.equals("")) logContent += logStartTemp + logTemp;
            }

            // 변경된 사항이 있으면 로그를 남긴다.
            if (!logContent.equals("Update withdraw fee")) authorityService.insertAdminLog(request, 231, logContent, adminMember.getId());
            return "success";
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return "error";
    }


    /* 시스템 변경 log 페이지 */
    private void sys6(HttpServletRequest request, HttpSession session, Model model, int search_listCount) {

        String search_type = request.getParameter("search_type") == null ? "all" : request.getParameter("search_type");
        String search_reg_dt = request.getParameter("search_reg_dt") == null ? "" : request.getParameter("search_reg_dt");
        String search_id = request.getParameter("search_id") == null ? "" : request.getParameter("search_id");
        int page = request.getParameter("page") == null ? 1 : Integer.parseInt(request.getParameter("page"));

        // 등록일
        if (search_reg_dt == null || search_reg_dt.length() <= 0) {

            Date now = new Date();
            search_reg_dt = String.format("%s - %s", Utils.GetDateStringFromFormat(Utils.GetDateAddMonths(now, -1), "yyyy.MM.dd"), Utils.GetDateStringFromFormat(now, "yyyy.MM.dd"));
        }

        AdminLogResponse systemLogResponse = null;
        AdminLogResponse systemLogExcelResponse = null;

        String[] reg_dts = search_reg_dt.split("-");

        try {

            systemLogResponse = authorityService.systemLogList(reg_dts[0], reg_dts[1], search_type, search_id, false, page, search_listCount);

            // 엑셀용
            systemLogExcelResponse = authorityService.systemLogList(reg_dts[0], reg_dts[1], search_type, search_id, true, page, 20);
        } catch (Exception e) {

            log.error("Fail to get adminLogList, msg = " + e.getMessage());
        }

        // 엑셀용
        request.setAttribute("excelFileName", String.join("_", reg_dts[0].trim(), reg_dts[1].trim()));
        request.setAttribute("response", systemLogExcelResponse);

        model.addAttribute("systemLogResponse", systemLogResponse);
        model.addAttribute("search_type", search_type);
        model.addAttribute("search_id", search_id);
        model.addAttribute("search_reg_dt", search_reg_dt);
        model.addAttribute("page", page);
        model.addAttribute("search_listCount", search_listCount);
    }

    /* 시스템 변경 log excel 다운로드 */
    @RequestMapping(value="sys6/excelDown")
    public Object sys6ExcelDown(
            HttpSession session,
            HttpServletRequest request,
            Model model
    ){

        sys6(request, session, model, 20);
        Map<String, Object> map = new HashMap<>();

        //SET FILE NAME
        map.put(ExcelConstant.FILE_NAME, request.getAttribute("excelFileName") + "/system_log");

        MessageUtil messageUtil = new MessageUtil();
        String no = messageUtil.getMessage(session, "sys6.table.th.no");
        String type = messageUtil.getMessage(session, "type");
        String content = messageUtil.getMessage(session, "sys6.table.th.content");
        String id = messageUtil.getMessage(session, "sys6.table.th.id");
        String reg_dt = messageUtil.getMessage(session, "sys6.table.th.reg_dt");
        String ip = messageUtil.getMessage(session, "ip");

        String type_211 = messageUtil.getMessage(session, "trade.fee");
        String type_221 = messageUtil.getMessage(session, "sys6.type.221");
        String type_231 = messageUtil.getMessage(session, "sys6.type.231");
        String type_232 = messageUtil.getMessage(session, "sys6.type.232");
        String type_241 = messageUtil.getMessage(session, "withdrawal.limit");

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
        AdminLogResponse response = (AdminLogResponse) request.getAttribute("response");
        List<List<String>> excelBody = new ArrayList<>();
        for(AdminLog systemLog : response.getAdminLogList()) {
            List<String> tempList = new ArrayList<>();
            tempList.add(String.valueOf(systemLog.getNo()));

            if (systemLog.getType() == 211) tempList.add(type_211);
            else if (systemLog.getType() == 221) tempList.add(type_221);
            else if (systemLog.getType() == 231) tempList.add(type_231);
            else if (systemLog.getType() == 232) tempList.add(type_232);
            else if (systemLog.getType() == 241) tempList.add(type_241);
            else  tempList.add("");

            tempList.add(systemLog.getContent());
            tempList.add(systemLog.getId());
            tempList.add(systemLog.getReg_dt().toString());
            tempList.add(systemLog.getIp());

            excelBody.add(tempList);
        }
        map.put(ExcelConstant.BODY, excelBody);

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        authorityService.insertAdminLog(request, 261, "Download excel system change log list", adminMember.getId());

        return new ExcelXlsxStreamingView(map);
    }


    /* 입출금 수수료 페이지 */
    private void sys9(HttpServletRequest request, HttpSession session, Model model, String mode){

        if (mode == null) {

            model.addAttribute("mode", "read");
        } else {

            model.addAttribute("mode", mode);
        }

        model.addAttribute("bankingUse", systemDao.GetBankingUse());
        request.setAttribute("bankingUse", systemDao.GetBankingUse());
    }

    /* 입출금 설정 import */
    @RequestMapping("sys9_proc")
    public @ResponseBody String sys9proc(
            @RequestBody List<BankingUse> bankingUseRequest,
            HttpSession session,
            HttpServletRequest request,
            Model model,
            @RequestParam(value="mode", defaultValue = "read") String mode
    ){

        sys9(request, session, model, mode);

        String logContent = "Update banking Use";
        AdminMember adminMember = (AdminMember) session.getAttribute("member");

        if (bankingUseRequest == null) return "fail";

        try {

            systemDao.UpdateBankingUse(bankingUseRequest);

            // 기존 입출금 설정
            HashMap<String, BankingUse> tempUse = new HashMap<String, BankingUse>();
            List<BankingUse> oldBankUse = (List<BankingUse>) request.getAttribute("bankingUse");

            for (BankingUse use : oldBankUse) {

                tempUse.put(use.getCoin_name(), use);
            }

            for (BankingUse item : bankingUseRequest) {

                String logTemp = "";
                String logStartTemp = ", [" + item.getCoin_name() + "] ";

                // 일출금 설정 정보가 변경되었으면
                if (tempUse.get(item.getCoin_name()).getBanking_deposit_use().compareTo(item.getBanking_deposit_use()) != 0)
                    logTemp += "banking_deposit_use = " + item.getBanking_deposit_use();
                if (tempUse.get(item.getCoin_name()).getBanking_withdraw_use().compareTo(item.getBanking_withdraw_use()) != 0)
                    logTemp += !logTemp.equals("") ? ", banking_withdraw_use = " + item.getBanking_withdraw_use() : "banking_withdraw_use = " + item.getBanking_withdraw_use();
                if (tempUse.get(item.getCoin_name()).getBanking_list_use().compareTo(item.getBanking_list_use()) != 0)
                    logTemp += !logTemp.equals("") ? ", banking_list_use = " + item.getBanking_list_use() : "banking_list_use = " + item.getBanking_list_use();
                if (tempUse.get(item.getCoin_name()).getBanking_invest_use().compareTo(item.getBanking_invest_use()) != 0)
                    logTemp += !logTemp.equals("") ? ", Banking_invest_use = " + item.getBanking_invest_use() : "banking_invest_use = " + item.getBanking_invest_use();
                if (tempUse.get(item.getCoin_name()).getBanking_recovery_use().compareTo(item.getBanking_recovery_use()) != 0)
                    logTemp += !logTemp.equals("") ? ", banking_recovery_use = " + item.getBanking_recovery_use() : "banking_recovery_use = " + item.getBanking_recovery_use();

                if (!logTemp.equals("")) logContent += logStartTemp + logTemp;
            }

            // 변경된 사항이 있으면 로그를 남긴다.
            if (!logContent.equals("Update banking use")) authorityService.insertAdminLog(request, 232, logContent, adminMember.getId());
            return "success";
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return "error";
    }
}