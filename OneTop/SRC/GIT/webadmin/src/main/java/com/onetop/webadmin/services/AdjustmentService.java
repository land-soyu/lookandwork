package com.onetop.webadmin.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import com.onetop.webadmin.mappers.AdjustmentDao;
import com.onetop.webadmin.mappers.CoinDao;
import com.onetop.webadmin.models.Counts;
import com.onetop.webadmin.models.MarketInfo;
import com.onetop.webadmin.models.adjustment.*;
import com.onetop.webadmin.models.monitoring.CoinInvest;
import com.onetop.webadmin.responses.PageNaviResponse;
import com.onetop.webadmin.responses.member.ExtraPaySnapshotResponse;
import com.onetop.webadmin.responses.member.MemberBalanceLogResponse;
import com.onetop.webadmin.util.Utils;

@Service
public class AdjustmentService extends PageService {


    private static final Logger log = LoggerFactory.getLogger(AttachmentService.class);

    @Autowired
    private AdjustmentDao adjustmentDao;

    @Autowired
    private CoinDao coinDao;



    public List<MemberBalanceInfo> GetMemberBalanceInfo(int mb_id) {
        return adjustmentDao.GetMemberBalanceInfo(mb_id);
    }


    public List<CoinNowPrice> GetAllCoinNowPrice() {
        return adjustmentDao.GetAllCoinNowPrice();
    }


    public Date GetMbBalanceInfoSyncDate() {
        return adjustmentDao.GetSyncHistory("mb_balance_info");
    }


    public Date GetMbBalanceLogSyncDate() {
        return adjustmentDao.GetSyncHistory("mb_balance_log");
    }


    public Date GetAdminPaymentRepaymentMemberInfoSyncDate() {
        return adjustmentDao.GetSyncHistory("admin_payment_repayment_member_info");
    }


    public Date GetAdminPaymentRepaymentSyncDate() {
        return adjustmentDao.GetSyncHistory("admin_payment_repayment");
    }


    public MemberBalanceLogResponse GetMemberBalanceLog(
            String changeDateFrom,
            String changeDateTo,
            String mb_id,
            String action,
            String coin,
            String market,
            String asset_coin,
            int currentPage,
            int search_listCount,
            boolean isExcel
    ) {
        HashMap<String, Object> resultSet = adjustmentDao.GetMemberBalanceLog(changeDateFrom, changeDateTo, mb_id, action, coin, market, asset_coin, currentPage > 0 ? currentPage : 1, currentPage > 0 ? search_listCount : SIZE_PER_PAGE_DETAIL, isExcel);

        MemberBalanceLogResponse response = new MemberBalanceLogResponse();
        Counts counts = (Counts) resultSet.get("counts");
        response.setCounts(counts);
        response.setMemberBalanceLogList((List<MemberBalanceLog>) resultSet.get("list"));
        //response.setCoinList(GetAdjCoinListCointainsCoinInfo("mb_balance_log"));
        //response.setAssetCoinList(GetAdjCoinListContainsCoinInfoWithMarketBase("mb_balance_log"));
        //response.setMarketList(coinDao.GetMarketNameList());
        PageNaviResponse naviResponse = getPageNavi(counts.getSearch(), currentPage, search_listCount);
        response.setPageNaviResponse(naviResponse);
        return response;
    }
    public MemberBalanceLogResponse GetMemberBalanceLog_no(
            String changeDateFrom,
            String changeDateTo,
            String mb_no,
            String action,
            String coin,
            String market,
            String asset_coin,
            int currentPage,
            int search_listCount,
            boolean isExcel
    ) {
        HashMap<String, Object> resultSet = adjustmentDao.GetMemberBalanceLog_no(changeDateFrom, changeDateTo, mb_no, action, coin, market, asset_coin, currentPage > 0 ? currentPage : 1, currentPage > 0 ? search_listCount : SIZE_PER_PAGE_DETAIL, isExcel);

        MemberBalanceLogResponse response = new MemberBalanceLogResponse();
        Counts counts = (Counts) resultSet.get("counts");
        response.setCounts(counts);
        response.setMemberBalanceLogList((List<MemberBalanceLog>) resultSet.get("list"));
        //response.setCoinList(GetAdjCoinListCointainsCoinInfo("mb_balance_log"));
        //response.setAssetCoinList(GetAdjCoinListContainsCoinInfoWithMarketBase("mb_balance_log"));
        //response.setMarketList(coinDao.GetMarketNameList());
        PageNaviResponse naviResponse = getPageNavi(counts.getSearch(), currentPage, search_listCount);
        response.setPageNaviResponse(naviResponse);
        return response;
    }


    public MemberBalanceLogResponse GetMemberBalanceLogExcel(
            String changeDateFrom,
            String changeDateTo,
            String mb_id,
            String action,
            String coin,
            String market,
            String asset_coin,
            boolean isExcel
    ) {
        HashMap<String, Object> resultSet = adjustmentDao.GetMemberBalanceLog(changeDateFrom, changeDateTo, mb_id, action, coin, market, asset_coin, 1, Integer.MAX_VALUE, isExcel);

        MemberBalanceLogResponse response = new MemberBalanceLogResponse();
        response.setMemberBalanceLogList((List<MemberBalanceLog>) resultSet.get("list"));
        response.setCoinList(GetAdjCoinList("mb_balance_log"));
        response.setMarketList(coinDao.GetMarketNameList());
        return response;
    }


    public List<String> GetAdjCoinList(String tableName) {
        return adjustmentDao.GetAdjCoinList(tableName);
    }


    public List<String> GetAdjCoinListCointainsCoinInfo(String tableName) {
        List<String> adjCoinList = adjustmentDao.GetAdjCoinList(tableName);
        List<String> marketCoinList = coinDao.GetMarketCoinList();

        for (String marketCoin : marketCoinList) {
            if (adjCoinList.indexOf(marketCoin) < 0) {
                adjCoinList.add(marketCoin);
            }
        }

        Collections.sort(adjCoinList);
        return adjCoinList;
    }


    public List<String> GetAdjCoinListContainsCoinInfoWithMarketBase(String tableName) {
        List<String> adjCoinList = GetAdjCoinListCointainsCoinInfo(tableName);

        List<MarketInfo> marketInfoList = coinDao.GetMarketNameList();
        for (MarketInfo marketInfo : marketInfoList) {
            if (!adjCoinList.contains(marketInfo.getMarket())) {
                adjCoinList.add(marketInfo.getMarket());
            }

        }
        return adjCoinList;
    }


    /**
     * @brief 동기화 후 sync_history에 테이블명 넣어준다.\n
     * @details \n
     */
    public int insertSyncHistory(String tableName) {

        return adjustmentDao.insertSyncHistory(tableName);
    }


    /**
     * snapshot 정보 조회
     *
     * @param page
     * @param search_listCount
     * @return
     * @throws Exception
     */
    public ExtraPaySnapshotResponse selectExtraPaySnapshotInfoList(
            int page,
            int search_listCount
    ) throws Exception {
        ExtraPaySnapshotResponse extraPaySnapshotResponse = new ExtraPaySnapshotResponse();
        List<ExtraPaySnapshotInfo> extraPaySnapshotInfoList = new ArrayList<>();

        int total_count = adjustmentDao.countExtraPaySnapshotInfoList();
        extraPaySnapshotResponse.setTotal_mbBalanceInfoSnapshot_count(total_count);

        PageNaviResponse pageNaviResponse = getPageNavi(total_count, page, search_listCount);
        extraPaySnapshotResponse.setPageNaviResponse(pageNaviResponse);

        if (total_count > 0) {

            int startIndex = (page - 1) * search_listCount;
            extraPaySnapshotInfoList = adjustmentDao.selectExtraPaySnapshotInfoList(startIndex, search_listCount);
        }

        extraPaySnapshotResponse.setExtraPaySnapshotInfoList(extraPaySnapshotInfoList);

        return extraPaySnapshotResponse;
    }


    /**
     * snapshot 정보 등록
     * @param reserveTime
     * @return
     */
    public int insertExtraPaySnapshotInfo(Date reserveTime) {
        return adjustmentDao.insertExtraPaySnapshotInfo(reserveTime);
    }


    /**
     * snapshot 지급 날짜 등록
     * @param reserveTime
     * @return
     */
    public int updateExtraPaySnapshotPaymentDate(int idx, Date reserveTime) {
        return adjustmentDao.updateExtraPaySnapshotPaymentDate(idx, reserveTime);
    }


    /**
     * snapshot 지급 취소
     * @param idx
     * @return
     */
    public int updateExtraPaySnapshotPaymentCancel(int idx) {
        return adjustmentDao.updateExtraPaySnapshotPaymentCancel(idx);
    }


    public List<ExtraPaySnapshotHistory> getExtraPaySnapshotHistoryList(int snapshotIdx) {

        List<ExtraPaySnapshotHistory> extraPaySnapshotHistory = new ArrayList<>();

        try {

            extraPaySnapshotHistory = adjustmentDao.getExtraPaySnapshotHistoryList(snapshotIdx);
        } catch (Exception e) {

            log.error("Fail to get extrapay_snapshot_history list, msg = " + e.getMessage());
            e.printStackTrace();
        }
        return extraPaySnapshotHistory;
    }


    public List<CoinInvest> getInvestCoinInfo() {
        return adjustmentDao.getInvestCoinInfo();
    }


    public Map<String, Map<String,String>> getMonitoringCoinInfo() {
        Map<String,String> defaultMap = new HashMap<>();
        defaultMap.put("yesterdaydeposit","0");
        defaultMap.put("yesterdaydepositcount","0");
        defaultMap.put("yesterdaywithdraw","0");
        defaultMap.put("yesterdaywithdrawcount","0");
        defaultMap.put("todaydeposit","0");
        defaultMap.put("todaydepositcount","0");
        defaultMap.put("todaywithdraw","0");
        defaultMap.put("todaywithdrawcount","0");
        defaultMap.put("untreatedcount","0");
        defaultMap.put("untreated","0");
        defaultMap.put("yesterdayinvest","0");
        defaultMap.put("yesterdayinvestcount","0");
        defaultMap.put("todayinvest","0");
        defaultMap.put("todayinvestcount","0");

        Map<String, Map<String,String>> map = new HashMap<>();
        map.put("LST", new HashMap(defaultMap));
        map.put("BTC", new HashMap(defaultMap));
        map.put("ETH", new HashMap(defaultMap));
        map.put("LSR", new HashMap(defaultMap));

        Date date = new Date();
        String today = Utils.GetDateStringFromFormat(Utils.GetDateAddDays(date, 0), "yyyy-MM-dd");
        String yesterday = Utils.GetDateStringFromFormat(Utils.GetDateAddDays(date, -1), "yyyy-MM-dd");

        adjustmentDao.getMonitoringCoinInfo().forEach(monitoringCoinInfo -> {
            if(yesterday.equals(monitoringCoinInfo.getCdate())) {
                switch (monitoringCoinInfo.getAction()) {
                    case "deposit":
                        map.get(monitoringCoinInfo.getCoin_name()).put("yesterdaydeposit", monitoringCoinInfo.getSum_sign_balance().toString());
                        map.get(monitoringCoinInfo.getCoin_name()).put("yesterdaydepositcount", Integer.toString(monitoringCoinInfo.getProceed()));
                        break;
                    case "withdraw":
                        map.get(monitoringCoinInfo.getCoin_name()).put("yesterdaywithdraw", monitoringCoinInfo.getSum_adjust_balance().toString());
                        map.get(monitoringCoinInfo.getCoin_name()).put("yesterdaywithdrawcount", Integer.toString(monitoringCoinInfo.getProceed()));
                        break;
                    case "invest":
                        map.get(monitoringCoinInfo.getMarket_name()).put("yesterdayinvest", monitoringCoinInfo.getSum_sign_amount().toString());
                        map.get(monitoringCoinInfo.getMarket_name()).put("yesterdayinvestcount", Integer.toString(monitoringCoinInfo.getProceed()));
                        break;
                }
            } else if(today.equals(monitoringCoinInfo.getCdate())) {
                switch (monitoringCoinInfo.getAction()) {
                    case "deposit":
                        map.get(monitoringCoinInfo.getCoin_name()).put("todaydeposit", monitoringCoinInfo.getSum_sign_balance().toString());
                        map.get(monitoringCoinInfo.getCoin_name()).put("todaydepositcount", Integer.toString(monitoringCoinInfo.getProceed()));
                        break;
                    case "withdraw":
                        map.get(monitoringCoinInfo.getCoin_name()).put("todaywithdraw", monitoringCoinInfo.getSum_adjust_balance().toString());
                        map.get(monitoringCoinInfo.getCoin_name()).put("todaywithdrawcount", Integer.toString(monitoringCoinInfo.getProceed()));
                        break;
                    case "invest":
                        map.get(monitoringCoinInfo.getMarket_name()).put("todayinvest", monitoringCoinInfo.getSum_sign_amount().toString());
                        map.get(monitoringCoinInfo.getMarket_name()).put("todayinvestcount", Integer.toString(monitoringCoinInfo.getProceed()));
                        break;
                }
            }
        });

        adjustmentDao.getUntreatedDataList().forEach(untreatedData -> {
            map.get(untreatedData.getCoin_name()).put("untreatedcount", Integer.toString(untreatedData.getUntreated_count()));
            map.get(untreatedData.getCoin_name()).put("untreated", untreatedData.getCoinvalue_count().toString());
        });

        return map;
    }
}