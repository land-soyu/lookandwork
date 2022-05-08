package com.onetop.webadmin.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.onetop.webadmin.mappers.AdjustmentDao;
import com.onetop.webadmin.mappers.CoinDao;
import com.onetop.webadmin.mappers.ExchangeDao;
import com.onetop.webadmin.models.Counts;
import com.onetop.webadmin.models.adjustment.MemberBalanceLog;
import com.onetop.webadmin.models.exchange.OrderRequestHistory;
import com.onetop.webadmin.responses.PageNaviResponse;
import com.onetop.webadmin.responses.exchange.OrderRequestHistoryListResponse;
import com.onetop.webadmin.responses.exchange.SignHistoryListResponse;

@Service
public class ExchangeService  extends PageService {

    private static final Logger log = LoggerFactory.getLogger(WebMemberListService.class);


    @Autowired
    private ExchangeDao exchangeDao;
    @Autowired
    private CoinDao coinDao;
    @Autowired
    private AdjustmentDao adjustmentDao;



    public OrderRequestHistoryListResponse GetAllOrderRequestHistoryExcel(
            String mb_id,
            String req_dt_from, String req_dt_to,
            String coin_flag,
            String market_flag,
            String action,
            String od_no,
            String status,
            String search_channel,
            String search_ip,
            String sortName,
            String sortOrderBy,
            boolean isExcel
    ){
        OrderRequestHistoryListResponse response = new OrderRequestHistoryListResponse();
        try{

            HashMap<String, Object> resultSet = exchangeDao.GetAllOrderRequestHistory(mb_id, req_dt_from, req_dt_to, coin_flag, market_flag, action, od_no, status, search_channel, search_ip, sortName, sortOrderBy, 1, Integer.MAX_VALUE, isExcel);
            response.setCoinList(GetAdjCoinListCointainsCoinInfo("mb_order_history"));
            response.setMarketList(coinDao.GetMarketNameList());

            response.setOrderRequestHistoryList((List<OrderRequestHistory>)resultSet.get("list"));

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    public OrderRequestHistoryListResponse GetAllOrderRequestHistory(
            String mb_id,
            String req_dt_from, String req_dt_to,
            String coin_flag,
            String market_flag,
            String action,
            String od_no,
            String status,
            String search_channel,
            String search_ip,
            String sortName,
            String sortOrderBy,
            int currentPage,
            int search_listCount,
            boolean isExcel
    ){
        OrderRequestHistoryListResponse response = new OrderRequestHistoryListResponse();
        try{
            HashMap<String, Object> resultSet = exchangeDao.GetAllOrderRequestHistory(mb_id, req_dt_from, req_dt_to, coin_flag, market_flag, action, od_no, status, search_channel, search_ip, sortName, sortOrderBy, currentPage, search_listCount, isExcel);

            Counts counts = (Counts)resultSet.get("counts");
            response.setCounts(counts);

            response.setCoinList(GetAdjCoinListCointainsCoinInfo("mb_order_history"));
            response.setMarketList(coinDao.GetMarketNameList());

            PageNaviResponse naviResponse = getPageNavi(counts.getSearch(), currentPage, search_listCount);
            response.setPageNaviResponse(naviResponse);
            response.setOrderRequestHistoryList((List<OrderRequestHistory>)resultSet.get("list"));

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return response;

    }


    public SignHistoryListResponse GetAllSignHistoryExcel(
            String mb_id,
            String req_dt_from, String req_dt_to,
            String coin_flag,
            String market_flag,
            String action,
            String od_no,
            String sign_no,
            String search_channel,
            String search_ip,
            String sortName,
            String sortOrderBy,
            boolean isExcel
    ){
        SignHistoryListResponse response = new SignHistoryListResponse();
        try{

            HashMap<String, Object> resultSet = exchangeDao.GetAllSignHistory(mb_id, req_dt_from, req_dt_to, coin_flag, market_flag, action, od_no, sign_no, search_channel, search_ip, sortName, sortOrderBy, 1, Integer.MAX_VALUE, isExcel);
            response.setSignHistoryList((List<MemberBalanceLog>)resultSet.get("list"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }


    public SignHistoryListResponse GetAllSignHistory(
            String mb_id,
            String req_dt_from, String req_dt_to,
            String coin_flag,
            String market_flag,
            String action,
            String od_no,
            String sign_no,
            String search_channel,
            String search_ip,
            String sortName,
            String sortOrderBy,
            int currentPage,
            int search_listCount,
            boolean isExcel
    ){
        SignHistoryListResponse response = new SignHistoryListResponse();
        try{


            HashMap<String, Object> resultSet = exchangeDao.GetAllSignHistory(mb_id, req_dt_from, req_dt_to, coin_flag, market_flag, action, od_no, sign_no, search_channel, search_ip, sortName, sortOrderBy, currentPage, search_listCount, isExcel);

            Counts counts = (Counts)resultSet.get("counts");
            response.setCounts(counts);

            //response.setCoinList(coinDao.GetCoinList());
            //response.setMarketList(coinDao.GetMarketNameList());

            response.setCoinList(GetAdjCoinListCointainsCoinInfo("mb_balance_log_exc"));
            response.setMarketList(coinDao.GetMarketNameList());

            PageNaviResponse naviResponse = getPageNavi(counts.getSearch(), currentPage, search_listCount);
            response.setPageNaviResponse(naviResponse);
            response.setSignHistoryList((List<MemberBalanceLog>)resultSet.get("list"));

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return response;

    }


    public OrderRequestHistory GetOrderHistory(String mb_id, String ord_no){
        return exchangeDao.GetOrderHistory(mb_id, ord_no);
    }


    public List<MemberBalanceLog> GetSignHistory(String mb_id, String ord_no){
        return exchangeDao.GetSignHistory(mb_id, ord_no);
    }


    public List<String> GetAdjCoinListCointainsCoinInfo(String tableName){
        List<String> adjCoinList = adjustmentDao.GetAdjCoinList(tableName);
        List<String> marketCoinList = coinDao.GetMarketCoinList();

        for(String marketCoin : marketCoinList){
            if(adjCoinList.indexOf(marketCoin) < 0){
                adjCoinList.add(marketCoin);
            }
        }

        Collections.sort(adjCoinList);
        return adjCoinList;
    }
}