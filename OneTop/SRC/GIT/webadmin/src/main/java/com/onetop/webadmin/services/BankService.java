package com.onetop.webadmin.services;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.onetop.webadmin.mappers.BankDao;
import com.onetop.webadmin.mappers.CoinDao;
import com.onetop.webadmin.models.Counts;
import com.onetop.webadmin.models.bank.BankCoinRequest;
import com.onetop.webadmin.models.bank.DepositInfo;
import com.onetop.webadmin.models.bank.WithdrawRequest;
import com.onetop.webadmin.responses.PageNaviResponse;
import com.onetop.webadmin.responses.bank.BankCoinRequestListResponse;

@Service
public class BankService  extends PageService {

    private static final Logger log = LoggerFactory.getLogger(WebMemberListService.class);

    @Autowired
    private BankDao bankDao;
    @Autowired
    private CoinDao coinDao;

    @Value("${octet.url}")
    private String OCTET_URL;

    @Value("${octet.auth}")
    private String OCTET_AUTH;

    @Value("${octet.passphrase}")
    private String OCTET_PASSPHRASE;

    @Value("${octet.privateKey}")
    private String OCTET_PRIVATEKEY;



    public BankCoinRequest GetCoinRequest(String req_id, String mb_id){
        return bankDao.GetCoinRequest(req_id, mb_id);
    }


    public BankCoinRequestListResponse GetBankCoinRequestListExcel(
            String action,
            String mb_id,
            String req_dt_from, String req_dt_to,
            String coin_name,
            String txid,
            String od_request_address,
            String status,
            String sortName,
            String sortOrderBy,
            String deposti_memo,
            String withdraw_memo,
            boolean isExcel
    ){
        BankCoinRequestListResponse response = new BankCoinRequestListResponse();
        try{
            HashMap<String, Object> resultSet = bankDao.GetAllCoinRequest(
                    action,
                    mb_id,
                    req_dt_from, req_dt_to,
                    coin_name,
                    txid,
                    od_request_address,
                    status,
                    sortName,
                    sortOrderBy,
                    1,
                    Integer.MAX_VALUE,
                    deposti_memo,
                    withdraw_memo,
                    isExcel
            );
            response.setBankCoinRequestList((List<BankCoinRequest>)resultSet.get("list"));
        }
        catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return response;
    }


    public BankCoinRequest GetBankCoinRequest(
            String od_id
        ){
        BankCoinRequest response = new BankCoinRequest();
        try{
            response = bankDao.GetBankCoinRequest(
                od_id
            );
        }
        catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return response;
    }
    public BankCoinRequestListResponse GetBankCoinRequestList(
        String action,
        String mb_id,
        String req_dt_from, String req_dt_to,
        String coin_name,
        String txid,
        String od_request_address,
        String status,
        String sortName,
        String sortOrderBy,
        int currentPage,
        int search_listCount,
        String deposit_memo,
        String withdraw_memo,
        boolean isExcel
){
    BankCoinRequestListResponse response = new BankCoinRequestListResponse();
    try{
        HashMap<String, Object> resultSet = bankDao.GetAllCoinRequest(
                action,
                mb_id,
                req_dt_from, req_dt_to,
                coin_name,
                txid,
                od_request_address,
                status,
                sortName,
                sortOrderBy,
                currentPage,
                search_listCount,
                deposit_memo,
                withdraw_memo,
                isExcel
        );

        Counts counts = (Counts)resultSet.get("counts");
        response.setCounts(counts);

        response.setCoinList(coinDao.GetCoinList());

        PageNaviResponse naviResponse = getPageNavi(counts.getSearch(), currentPage, search_listCount);
        response.setPageNaviResponse(naviResponse);
        response.setBankCoinRequestList((List<BankCoinRequest>)resultSet.get("list"));
    }
    catch (Exception e){
        e.printStackTrace();
        log.error(e.getMessage());
    }
    return response;
}


    public Boolean SetHoldCoinRequest(String req_id, String reason){
        return bankDao.SetHoldCoinRequest(req_id, reason);
    }


    public BankCoinRequestListResponse GetBankCoinRequestList_(
            String action,
            String mb_id,
            String req_dt_from, String req_dt_to,
            String coin_name,
            String txid,
            String od_request_address,
            String status,
            String sortName,
            String sortOrderBy,
            int currentPage,
            int search_listCount,
            String deposit_memo,
            String withdraw_memo,
            boolean isExcel
    ){
        BankCoinRequestListResponse response = new BankCoinRequestListResponse();
        try{
            HashMap<String, Object> resultSet = bankDao.GetAllCoinRequest_(
                    action,
                    mb_id,
                    req_dt_from, req_dt_to,
                    coin_name,
                    txid,
                    od_request_address,
                    status,
                    sortName,
                    sortOrderBy,
                    currentPage,
                    search_listCount,
                    deposit_memo,
                    withdraw_memo,
                    isExcel
            );

            Counts counts = (Counts)resultSet.get("counts");
            response.setCounts(counts);

            response.setCoinList(coinDao.GetCoinList());

            PageNaviResponse naviResponse = getPageNavi(counts.getSearch(), currentPage, search_listCount);
            response.setPageNaviResponse(naviResponse);
            response.setWithdrawRequestList((List<WithdrawRequest>)resultSet.get("list"));
        }
        catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return response;
    }
    public BankCoinRequestListResponse GetBankCoinRequestList_(
            String action,
            String mb_id,
            String req_dt_from, String req_dt_to,
            String coin_name,
            String txid,
            String od_request_address,
            String status,
            String sortName,
            String sortOrderBy,
            int currentPage,
            int search_listCount,
            String deposit_memo,
            String withdraw_memo,
            boolean isExcel,
            String search_value
    ){
        BankCoinRequestListResponse response = new BankCoinRequestListResponse();
        try{
            HashMap<String, Object> resultSet = bankDao.GetAllCoinRequest_(
                    action,
                    mb_id,
                    req_dt_from, req_dt_to,
                    coin_name,
                    txid,
                    od_request_address,
                    status,
                    sortName,
                    sortOrderBy,
                    currentPage,
                    search_listCount,
                    deposit_memo,
                    withdraw_memo,
                    isExcel,
                    search_value
            );

            Counts counts = (Counts)resultSet.get("counts");
            response.setCounts(counts);

            response.setCoinList(coinDao.GetCoinList());

            PageNaviResponse naviResponse = getPageNavi(counts.getSearch(), currentPage, search_listCount);
            response.setPageNaviResponse(naviResponse);
            response.setWithdrawRequestList((List<WithdrawRequest>)resultSet.get("list"));
        }
        catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return response;
    }

    public BankCoinRequestListResponse GetBankCoinRequestListExcel_(
        String action,
        String mb_id,
        String req_dt_from, String req_dt_to,
        String coin_name,
        String txid,
        String od_request_address,
        String status,
        String sortName,
        String sortOrderBy,
        String deposti_memo,
        String withdraw_memo,
        boolean isExcel
){
    BankCoinRequestListResponse response = new BankCoinRequestListResponse();
    try{
        HashMap<String, Object> resultSet = bankDao.GetAllCoinRequest_(
                action,
                mb_id,
                req_dt_from, req_dt_to,
                coin_name,
                txid,
                od_request_address,
                status,
                sortName,
                sortOrderBy,
                1,
                Integer.MAX_VALUE,
                deposti_memo,
                withdraw_memo,
                isExcel
        );
        response.setWithdrawRequestList((List<WithdrawRequest>)resultSet.get("list"));
    }
    catch (Exception e){
        e.printStackTrace();
        log.error(e.getMessage());
    }
    return response;
}



    public BankCoinRequestListResponse searchOldData(int mb_id) {
        BankCoinRequestListResponse response = new BankCoinRequestListResponse();
        try{
            HashMap<String, Object> resultSet = bankDao.GetOldCoinRequest_(
                    mb_id
            );
            response.setOldWithdrawRequestList((List<WithdrawRequest>)resultSet.get("list"));
        }
        catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return response;
    }

    public Boolean withdrawApprove(int idx, String addr, String coin_name, String amount, String mg_id){
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        RestTemplate restTemplate = new RestTemplate(factory);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", OCTET_AUTH);

        long datetime = System.currentTimeMillis();
        JSONObject jsonObject = new JSONObject();

        log.error("addr : "+addr);
        log.error("amount : "+amount);

        jsonObject.put("reqId", Integer.toString(idx));
        jsonObject.put("privateKey", OCTET_PRIVATEKEY);
        jsonObject.put("passphrase", OCTET_PASSPHRASE);
        jsonObject.put("memo", Integer.toString(idx));
        jsonObject.put("to", addr);
        jsonObject.put("amount", amount);
        HttpEntity<String> entity = new HttpEntity<String>(jsonObject.toString(), headers);
        String answer = restTemplate.postForObject(OCTET_URL+coin_name+"/transfer", entity, String.class);

        if (answer.contains("transaction_id")) {
            return bankDao.updateApprove(idx, mg_id);
        } else {
            return false;
        }
    }
    public Boolean withdrawAllApprove(String idx, String addr, String coin_name, String amount, String mg_id){           
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        RestTemplate restTemplate = new RestTemplate(factory);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", OCTET_AUTH);

        //idx의 맨 앞에 있는 걸로 보냄
        String[] splited = idx.split(",");

        long datetime = System.currentTimeMillis();
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("reqId", splited[0]);
        jsonObject.put("privateKey", OCTET_PRIVATEKEY);
        jsonObject.put("passphrase", OCTET_PASSPHRASE);
        jsonObject.put("memo", splited[0]);
        jsonObject.put("to", addr);
        jsonObject.put("amount", amount);
        HttpEntity<String> entity = new HttpEntity<String>(jsonObject.toString(), headers);
        String answer = restTemplate.postForObject(OCTET_URL+coin_name+"/transfer", entity, String.class);

        if (answer.contains("transaction_id")) {            
            return bankDao.updateAllApprove(idx, mg_id);
        } else {
            return false;
        }
    }
    public Boolean updateExchange(String idx, String mg_id){
        return bankDao.updateExchange(idx, mg_id);
    }
    public Boolean updateAllExchange(String idx, String mg_id){
        return bankDao.updateAllExchange(idx, mg_id);
    }
    public Boolean updateReject(String idx, String reject, Double total_value, String mg_id){
        return bankDao.updateReject(idx, reject, total_value, mg_id);
    }
    public Boolean updateAllReject(String idx, String reject, Double total_value, String mg_id){
        return bankDao.updateAllReject(idx, reject, total_value, mg_id);
    }

    public Boolean checkApprove(String idx){
        return bankDao.checkApprove(idx);
    }

    public Boolean updateWithdrawBuy(String idx, String od_receipt_coin, String buy_point) {
        return bankDao.updateWithdrawBuy(idx, od_receipt_coin, buy_point);
    }
    public Boolean updateWithdrawBuy(int idx, String mg_id, String coin_name, String mb_no, String address, String action_type, String return_amount, String reason) {
        return bankDao.updateWithdrawBuy(idx, mg_id, coin_name, mb_no, address, action_type, return_amount, reason);
    }

    public WithdrawRequest GetWithdrawRequest(String idx) {
        return bankDao.GetWithdrawRequest(Integer.parseInt(idx));
    }


    public boolean insertHandWriting(String member_id, String member_no, String insert_date, String asset_type, String txid, String buy_point, String reason, String mg_id) {
        return bankDao.insertHandWriting(member_id, member_no, insert_date, asset_type, txid, buy_point, reason, mg_id);
    }



    public List<DepositInfo> GetDepositInfo(
        String od_id
    ){
        List<DepositInfo> response = new ArrayList<>();
        try{
            response = bankDao.GetDepositInfo(
                od_id
            );
        }
        catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return response;
    }

}