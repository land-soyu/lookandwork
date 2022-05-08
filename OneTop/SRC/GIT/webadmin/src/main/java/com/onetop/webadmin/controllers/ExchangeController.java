package com.onetop.webadmin.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.expression.Numbers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.onetop.webadmin.models.adjustment.MemberBalanceLog;
import com.onetop.webadmin.models.authority.AdminMember;
import com.onetop.webadmin.models.exchange.OrderRequestHistory;
import com.onetop.webadmin.models.exchange.SignDetailRequest;
import com.onetop.webadmin.responses.exchange.OrderRequestHistoryListResponse;
import com.onetop.webadmin.responses.exchange.SignHistoryListResponse;
import com.onetop.webadmin.services.ExchangeService;
import com.onetop.webadmin.services.excel.ExcelConstant;
import com.onetop.webadmin.services.excel.ExcelXlsxStreamingView;
import com.onetop.webadmin.util.MessageUtil;
import com.onetop.webadmin.util.Utils;

import java.math.BigDecimal;
import java.util.*;

@Controller
public class ExchangeController {
    private static final Logger log = LoggerFactory.getLogger(ExchangeController.class);

    @Autowired
    ExchangeService exchangeService;

    @RequestMapping("exc{pageNo:[0-9]}")
    public String exchange(
            @PathVariable String pageNo,
            HttpSession session,
            HttpServletRequest request,
            Model model,
            @RequestParam(value="sortName", defaultValue = "") String sortName,
            @RequestParam(value="sortOrderBy", defaultValue = "") String sortOrderBy,
            @RequestParam(value = "search_listCount", defaultValue = "20", required = false) int search_listCount,
            @RequestParam(defaultValue = "1") int page
    ){

        AdminMember adminMember = (AdminMember) session.getAttribute("member");
        model.addAttribute("member_proc", adminMember);

        switch (Integer.parseInt(pageNo)) {
            case 1:
                exc1(session, model, request, sortName, sortOrderBy, page, false, search_listCount);
                break;
            case 2:
                exc2(session, model, request, sortName, sortOrderBy, page, false, search_listCount);
                break;
            default:
                break;
        }

        model.addAttribute("page", page);
        model.addAttribute("search_listCount", search_listCount);
        return "exchange/exc" + pageNo;
    }

    @RequestMapping(value = "exc1/excelDown")
    public Object exc1ExcelDown(
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse httpServletResponse,
            Model model,
            @RequestParam(value = "sortName", defaultValue = "") String sortName,
            @RequestParam(value = "sortOrderBy", defaultValue = "") String sortOrderBy,
            @RequestParam(defaultValue = "1") int page
    ) {

        // 엑셀다운로드 테스트용
        httpServletResponse.setHeader("Set-Cookie", "fileDownload=true; path=/");

        exc1(session, model, request, sortName, sortOrderBy, page, true, 20);
        Map<String, Object> map = new HashMap<>();


        String req_date = request.getParameter("req_dt");
        req_date = req_date.replace("-", "_").replace(" ", "").replace(".", "");

        //SET FILE NAME
        map.put(ExcelConstant.FILE_NAME, req_date + "_order_request_history");

        MessageUtil messageUtil = new MessageUtil();
        String od_no = messageUtil.getMessage(session, "order.number");
        String type = messageUtil.getMessage(session, "type");
        String coin_market = messageUtil.getMessage(session, "coin.market");
        String mb_id = messageUtil.getMessage(session, "member.id");

        String od_price = messageUtil.getMessage(session, "exc1.table.thead.th.od_price");
        String od_amount = messageUtil.getMessage(session, "exc1.table.thead.th.od_amount");
        String od_total = messageUtil.getMessage(session, "exc1.table.thead.th.od_total");
        String od_signed_amount = messageUtil.getMessage(session, "amount.clamping");
        String status = messageUtil.getMessage(session, "status");
        String req_dt = messageUtil.getMessage(session, "request.date");
        String ip_addr = messageUtil.getMessage(session, "ip");

        //SET HEADER
        map.put(ExcelConstant.HEAD,
                new ArrayList(){
                    {
                        add(Arrays.asList(
                                od_no,
                                type,
                                coin_market,
                                mb_id,
                                od_price, od_price,
                                od_amount, od_amount,
                                od_total, od_total,
                                od_signed_amount, od_signed_amount,
                                status,
                                req_dt,
                                ip_addr
                                )
                        );
                    }
                }
        );

        //SET CONTENT
        OrderRequestHistoryListResponse response = (OrderRequestHistoryListResponse) request.getAttribute("response");
        List<List<String>> excelBody = new ArrayList<>();
        if(response != null){
            for (OrderRequestHistory orderRequestHistory : response.getOrderRequestHistoryList()) {
                List<String> tempList = new ArrayList<>();

                tempList.add(String.valueOf(orderRequestHistory.getOrd_no()));
                tempList.add(messageUtil.getMessage(session, "exc1.table.thead.th.type."+ orderRequestHistory.getAction()));
                tempList.add(orderRequestHistory.getMarket_name());
                tempList.add(orderRequestHistory.getMb_id());

                if(orderRequestHistory.getOrd_price() != null){
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(orderRequestHistory.getOrd_price(), 1, "COMMA", 4, "POINT")));
                }
                else{
                    tempList.add("");
                }
                tempList.add(orderRequestHistory.getMarket_name().split("/")[1]);

                if(orderRequestHistory.getOrd_amount() != null){
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(orderRequestHistory.getOrd_amount(), 1, "COMMA", 4, "POINT")));
                }
                else{
                    tempList.add("");
                }
                tempList.add(orderRequestHistory.getMarket_name().split("/")[0]);

                if(orderRequestHistory.getBalance() != null){
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(orderRequestHistory.getBalance(), 1, "COMMA", 4, "POINT")));
                }
                else{
                    tempList.add("");
                }
                tempList.add(orderRequestHistory.getMarket_name().split("/")[1]);

                if(orderRequestHistory.getSign_amount() != null){
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(orderRequestHistory.getSign_amount(), 1, "COMMA", 4, "POINT")));
                }
                else{
                    tempList.add("");
                }

                if(orderRequestHistory.getIs_cancel()){
                    tempList.add(messageUtil.getMessage(session, "cancel"));
                }
                else{
                    if(orderRequestHistory.getSign_amount().compareTo(BigDecimal.ZERO) == 0){
                        tempList.add(messageUtil.getMessage(session, "request"));
                    }
                    else if(orderRequestHistory.getOrd_amount().subtract(orderRequestHistory.getSign_amount()).compareTo(BigDecimal.ZERO) > 0){
                        tempList.add(messageUtil.getMessage(session, "status.inprogress"));
                    }
                    else{
                        tempList.add(messageUtil.getMessage(session, "status.complete"));
                    }

                }

                if (orderRequestHistory.getOrd_date() != null) {
                    tempList.add(orderRequestHistory.getOrd_date().toString());
                } else {
                    tempList.add("");
                }

                if (orderRequestHistory.getReq_ip_addr() != null) {
                    tempList.add(orderRequestHistory.getReq_ip_addr());
                } else {
                    tempList.add("");
                }

                excelBody.add(tempList);
            }
        }

        map.put(ExcelConstant.BODY, excelBody);

        return new ExcelXlsxStreamingView(map);
    }

//    @RequestMapping(value = "exc1/excelDown")
//    @ResponseBody
//    public void exc1ExcelDown(
//        HttpSession session,
//        HttpServletRequest request,
//        HttpServletResponse response,
//        Model model,
//        @RequestParam(value = "sortName", defaultValue = "") String sortName,
//        @RequestParam(value = "sortOrderBy", defaultValue = "") String sortOrderBy,
//        @RequestParam(defaultValue = "1") int page
//    ) {
//
//        exc1(session, model, request, sortName, sortOrderBy, page, true, 20);
//
//        String req_date = request.getParameter("req_dt");
//        req_date = req_date.replace("-", "_").replace(" ", "").replace(".", "");
//
//        //SET FILE NAME
//        MessageUtil messageUtil = new MessageUtil();
//        String od_no = messageUtil.getMessage(session, "order.number");
//        String type = messageUtil.getMessage(session, "type");
//        String coin_market = messageUtil.getMessage(session, "coin.market");
//        String mb_id = messageUtil.getMessage(session, "member.id");
//        String od_price = messageUtil.getMessage(session, "exc1.table.thead.th.od_price");
//        String od_amount = messageUtil.getMessage(session, "exc1.table.thead.th.od_amount");
//        String od_total = messageUtil.getMessage(session, "exc1.table.thead.th.od_total");
//        String od_signed_amount = messageUtil.getMessage(session, "amount.clamping");
//        String status = messageUtil.getMessage(session, "status");
//        String req_dt = messageUtil.getMessage(session, "request.date");
//        String ip_addr = messageUtil.getMessage(session, "ip");
//
//        String[] columns = {
//                od_no,
//                type,
//                coin_market,
//                mb_id,
//                od_price,
//                od_amount,
//                od_total,
//                od_signed_amount,
//                status,
//                req_dt,
//                ip_addr
//        };
//
//        //SET CONTENT
//        OrderRequestHistoryListResponse orderRequestHistoryListResponse = (OrderRequestHistoryListResponse) request.getAttribute("response");
//        SXSSFWorkbook wb = (new ExportResponseExcel()).exportExcel(columns, orderRequestHistoryListResponse.getOrderRequestHistoryList());
//
//        try {
//
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            wb.write(byteArrayOutputStream);
//            byte[] outArray = byteArrayOutputStream.toByteArray();
//
//            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//            response.setContentLength(outArray.length);
//            response.setHeader("Expires:", "0");
//            response.setHeader("Content-Disposition", "attachment; filename=" + req_date + "_order_request_history.xlsx");
//
//            OutputStream outputStream = response.getOutputStream();
//            outputStream.write(outArray);
//            outputStream.flush();
//            wb.dispose();
//            wb.close();
//        } catch (Exception e) {
//
//            log.error("Fail to download excel order_request_history, msg = " + e.getMessage());
//        }
//    }


    private void exc1(HttpSession session, Model model, HttpServletRequest request, String sortName, String sortOrderBy, int page, boolean isExcel, int search_listCount) {
        if (sortName == null || sortOrderBy == null || sortName.equals("") || sortOrderBy.equals("")) {
            sortName = "ord_date";
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
        if(req_dts.length > 0){
            req_dt_from = req_dts[0].trim();
            req_dt_to = req_dts.length >= 2 ? req_dts[1].trim(): req_dts[0].trim();
        }

        String action = request.getParameter("action");
        if(action == null || action.length() <= 0){
            action = "ALL";
        }
        model.addAttribute("action", action);

        String status = request.getParameter("status");
        if(status == null || status.length() <= 0){
            status = "ALL";
        }
        model.addAttribute("status", status);

        String od_no = request.getParameter("od_no");
        model.addAttribute("od_no", od_no);

        String coin_flag = request.getParameter("coin_flag");
        if(coin_flag == null || coin_flag.length() <= 0){
            coin_flag = "ALL";
        }
        model.addAttribute("coin_flag", coin_flag);

        String market_flag = request.getParameter("market_flag");
        if(market_flag == null || market_flag.length() <= 0){
            market_flag = "ALL";
        }
        model.addAttribute("market_flag", market_flag);

        String search_channel = request.getParameter("search_channel");
        if(search_channel == null || search_channel.length() <= 0){
            search_channel = "ALL";
        }
        model.addAttribute("search_channel", search_channel);

        String search_ip = request.getParameter("search_ip");
        if(search_ip == null || search_ip.length() <= 0){
            search_ip = "";
        }
        model.addAttribute("search_ip", search_ip);

        OrderRequestHistoryListResponse response = null;
        if(isExcel){
            response =  exchangeService.GetAllOrderRequestHistoryExcel(
                    mb_id,
                    req_dt_from,
                    req_dt_to,
                    coin_flag,
                    market_flag,
                    action,
                    od_no,
                    status,
                    search_channel,
                    search_ip,
                    sortName,
                    sortOrderBy,
                    isExcel
            );
        }
        else{
            response =  exchangeService.GetAllOrderRequestHistory(
                    mb_id,
                    req_dt_from,
                    req_dt_to,
                    coin_flag,
                    market_flag,
                    action,
                    od_no,
                    status,
                    search_channel,
                    search_ip,
                    sortName,
                    sortOrderBy,
                    page,
                    search_listCount,
                    isExcel
            );
        }

        request.setAttribute("response", response);

    }





    @RequestMapping(value = "exc2/excelDown")
    public Object exc2ExcelDown(
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse httpServletResponse,
            Model model,
            @RequestParam(value = "sortName", defaultValue = "") String sortName,
            @RequestParam(value = "sortOrderBy", defaultValue = "") String sortOrderBy,
            @RequestParam(defaultValue = "1") int page
    ) {
        exc2(session, model, request, sortName, sortOrderBy, page, true, 20);
        Map<String, Object> map = new HashMap<>();

        String sign_date = request.getParameter("sign_dt");
        sign_date = sign_date.replace("-", "_").replace(" ", "").replace(".", "");

        //SET FILE NAME
        map.put(ExcelConstant.FILE_NAME, sign_date + "_sign_history");

        MessageUtil messageUtil = new MessageUtil();
        String sign_no = messageUtil.getMessage(session, "contract.number");
        String od_no = messageUtil.getMessage(session, "order.number");
        String mb_id = messageUtil.getMessage(session, "member.id");
        String coin_market = messageUtil.getMessage(session, "coin.market");
        String type = messageUtil.getMessage(session, "type");

        String sign_price = messageUtil.getMessage(session, "price");

        String sign_amount = messageUtil.getMessage(session, "amount.clamping");

        String sign_total = messageUtil.getMessage(session, "total.fastening");

        String sign_fee = messageUtil.getMessage(session, "fee");

        String sign_adjustment = messageUtil.getMessage(session, "amount.settlement.fee");

        String sign_dt = messageUtil.getMessage(session, "exc2.table.thead.th.sign_dt");
        String ip_addr = "IP";

        /*
        //SET HEADER
        map.put(ExcelConstant.HEAD,
                new ArrayList(){
                    {
                        add(Arrays.asList(
                                sign_no,
                                od_no,
                                mb_id,
                                coin_market,
                                type,
                                sign_price,
                                sign_price,
                                sign_amount,
                                sign_amount,
                                sign_total,
                                sign_total,
                                sign_fee,
                                sign_fee,
                                sign_adjustment,
                                sign_adjustment,
                                sign_dt,
                                ip_addr
                                )
                        );
                    }
                }
        );

        //SET CONTENT
        SignHistoryListResponse response = (SignHistoryListResponse) request.getAttribute("response");
        List<List<String>> excelBody = new ArrayList<>();
        if(response != null){
            for (MemberBalanceLog memberBalanceLog : response.getSignHistoryList()) {
                List<String> tempList = new ArrayList<>();

                tempList.add(memberBalanceLog.getSign_no());
                tempList.add(String.valueOf(memberBalanceLog.getOd_id()));
                tempList.add(memberBalanceLog.getMb_id());
                switch(memberBalanceLog.getAction()){
                    case "bid":
                        tempList.add(memberBalanceLog.getMarket_name());
                        break;
                    case "ask":
                        tempList.add(memberBalanceLog.getMarket_name());
                        break;
                    default:
                        tempList.add("-");
                }
                tempList.add(messageUtil.getMessage(session, "mem5.table.thead.th.type."+ memberBalanceLog.getAction()));

                if(memberBalanceLog.getSign_price() != null){
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(memberBalanceLog.getSign_price(), 1, "COMMA", 4, "POINT")));
                }
                else{
                    tempList.add("");
                }
                switch(memberBalanceLog.getAction()){
                    case "bid":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[1]);
                        break;
                    case "ask":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[1]);
                        break;
                    default:
                        tempList.add("-");
                }

                if(memberBalanceLog.getSign_amount() != null){
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(memberBalanceLog.getSign_amount(), 1, "COMMA", 4, "POINT")));
                }
                else{
                    tempList.add("");
                }
                switch(memberBalanceLog.getAction()){
                    case "bid":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[0]);
                        break;
                    case "ask":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[0]);
                        break;
                    default:
                        tempList.add("-");
                }

                if(memberBalanceLog.getSign_balance() != null){
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(memberBalanceLog.getSign_balance(), 1, "COMMA", 4, "POINT")));
                }
                else{
                    tempList.add("");
                }
                switch(memberBalanceLog.getAction()){
                    case "bid":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[1]);
                        break;
                    case "ask":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[1]);
                        break;
                    default:
                        tempList.add("-");
                }

                if(memberBalanceLog.getFee() != null){
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(memberBalanceLog.getFee(), 1, "COMMA", 4, "POINT")));
                }
                else{
                    tempList.add("");
                }
                switch(memberBalanceLog.getAction()){
                    case "bid":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[0]);
                        break;
                    case "ask":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[1]);
                        break;
                    default:
                        tempList.add("-");
                }


                if(memberBalanceLog.getAdjust_balance() != null){
                    tempList.add(String.valueOf(new Numbers(Locale.getDefault()).formatDecimal(memberBalanceLog.getAdjust_balance(), 1, "COMMA", 4, "POINT")));
                }
                else{
                    tempList.add("");
                }
                switch(memberBalanceLog.getAction()){
                    case "bid":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[0]);
                        break;
                    case "ask":
                        tempList.add(memberBalanceLog.getMarket_name().split("/")[1]);
                        break;
                    default:
                        tempList.add("-");
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

                excelBody.add(tempList);
            }
        }

        map.put(ExcelConstant.BODY, excelBody);
        */

        return new ExcelXlsxStreamingView(map);
    }


    private void exc2(HttpSession session, Model model, HttpServletRequest request, String sortName, String sortOrderBy, int page, boolean isExcel, int search_listCount) {
        if (sortName == null || sortOrderBy == null || sortName.equals("") || sortOrderBy.equals("")) {
            sortName = "idx";
            sortOrderBy = "DESC";
        }

        request.setAttribute("sortName", sortName);
        request.setAttribute("sortOrderBy", sortOrderBy);

        String mb_id = request.getParameter("mb_id");
        model.addAttribute("mb_id", mb_id);

        String sign_dt = request.getParameter("sign_dt");

        if (sign_dt == null || sign_dt.length() <= 0) {
            Date now = new Date();
            sign_dt = String.format("%s - %s", Utils.GetDateStringFromFormat(Utils.GetDateAddMonths(now, -1), "yyyy.MM.dd"),
                    Utils.GetDateStringFromFormat(now, "yyyy.MM.dd"));
        }
        String[] sign_dts = sign_dt.split("-");
        request.setAttribute("sign_dt", sign_dt);

        String sign_dt_from = null;
        String sign_dt_to = null;
        if(sign_dts.length > 0){
            sign_dt_from = sign_dts[0].trim();
            sign_dt_to = sign_dts.length >= 2 ? sign_dts[1].trim(): sign_dts[0].trim();
        }

        String action = request.getParameter("action");
        if(action == null || action.length() <= 0){
            action = "ALL";
        }
        model.addAttribute("action", action);

        String od_no = request.getParameter("od_no");
        model.addAttribute("od_no", od_no);

        String sign_no = request.getParameter("sign_no");
        model.addAttribute("sign_no", sign_no);

        String coin_flag = request.getParameter("coin_flag");
        if(coin_flag == null || coin_flag.length() <= 0){
            coin_flag = "ALL";
        }
        model.addAttribute("coin_flag", coin_flag);

        String market_flag = request.getParameter("market_flag");
        if(market_flag == null || market_flag.length() <= 0){
            market_flag = "ALL";
        }
        model.addAttribute("market_flag", market_flag);

        String search_channel = request.getParameter("search_channel");
        if(search_channel == null || search_channel.length() <= 0){
            search_channel = "ALL";
        }
        model.addAttribute("search_channel", search_channel);

        String search_ip = request.getParameter("search_ip");
        if(search_ip == null || search_ip.length() <= 0){
            search_ip = "";
        }
        model.addAttribute("search_ip", search_ip);

        SignHistoryListResponse response = null;
        if(isExcel){
            response =  exchangeService.GetAllSignHistoryExcel(
                    mb_id,
                    sign_dt_from,
                    sign_dt_to,
                    coin_flag,
                    market_flag,
                    action,
                    od_no,
                    sign_no,
                    search_channel,
                    search_ip,
                    sortName,
                    sortOrderBy,
                    isExcel
            );
        }
        else{
            response =  exchangeService.GetAllSignHistory(
                    mb_id,
                    sign_dt_from,
                    sign_dt_to,
                    coin_flag,
                    market_flag,
                    action,
                    od_no,
                    sign_no,
                    search_channel,
                    search_ip,
                    sortName,
                    sortOrderBy,
                    page,
                    search_listCount,
                    isExcel
            );
        }

        request.setAttribute("response", response);
    }

    @RequestMapping("exc1_sign_detail")
    private @ResponseBody
    Object exc1_sign_detail(HttpSession session, Model model, HttpServletRequest request, @RequestBody SignDetailRequest data){
        try {
            // 세션에 관리자 정보가 없으면 요청 불허
            if (session.getAttribute("member") == null) {
                return "fail";
            }

            AdminMember adminMember = (AdminMember) session.getAttribute("member");
            if (adminMember.getRole() == 1) return "fail";

            if (data == null) return "fail";
            if (data.getMb_id() == null || data.getMb_id().length() <= 0) return "fail";
            if (data.getOrd_no() == null || data.getOrd_no().length() <= 0) return "fail";

            HashMap<String, Object> resultSet = new HashMap<>();
            resultSet.put("orderHistory", exchangeService.GetOrderHistory(data.getMb_id(), data.getOrd_no()));
            resultSet.put("signHistory",exchangeService.GetSignHistory(data.getMb_id(), data.getOrd_no()));
            return resultSet;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return "error";
    }
}