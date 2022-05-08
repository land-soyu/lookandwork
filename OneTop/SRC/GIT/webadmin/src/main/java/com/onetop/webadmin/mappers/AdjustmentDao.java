package com.onetop.webadmin.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.onetop.webadmin.config.ServerConfigs;
import com.onetop.webadmin.models.Counts;
import com.onetop.webadmin.models.StaticDataManager;
import com.onetop.webadmin.models.adjustment.*;
import com.onetop.webadmin.models.monitoring.CoinInvest;
import com.onetop.webadmin.responses.monitoring.MonitoringCoinInfo;
import com.onetop.webadmin.util.Utils;

@Repository
public class AdjustmentDao {
    @Autowired
    @Qualifier("namedParameterWebAdminJdbcTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<MemberBalanceInfo> GetMemberBalanceInfo(int mb_no) {
        List<MemberBalanceInfo> memberBalanceInfoList = new ArrayList<>();

        try {
            String sql = "SELECT * FROM coinmarketing.member_balance WHERE mb_no = :mb_no";
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("mb_no", mb_no);
            memberBalanceInfoList = namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, new BeanPropertyRowMapper<>(MemberBalanceInfo.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return memberBalanceInfoList;
    }


    public List<CoinNowPrice> GetAllCoinNowPrice() {
        List<CoinNowPrice> coinNowPriceList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM coinmarketing.now_price";
            coinNowPriceList = namedParameterJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CoinNowPrice.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return coinNowPriceList;
    }


    public Date GetSyncHistory(String table_name) {
        String sql = "SELECT end_dt FROM adjustment.sync_history WHERE `table_name` = :table_name AND fail_msg IS NULL ORDER BY end_dt DESC LIMIT 1";
        try {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("table_name", table_name);
            List<Date> syncList = namedParameterJdbcTemplate.queryForList(sql, mapSqlParameterSource, Date.class);
            return syncList.size() > 0 ? syncList.get(0) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }


    public HashMap<String, Object> GetMemberBalanceLog(
            String changeDateFrom,
            String changeDateTo,
            String mb_id,
            String action,
            String coin,
            String market,
            String asset_coin,
            int currentPage,
            int rowCntPerPage,
            boolean isExcel
    ) {
        HashMap<String, Object> resultSet = new HashMap<>();
        resultSet.put("memberBalanceLogList", new ArrayList<String>());
        try {

            String sql = "";

            // 엑셀다운로드일 때
            if (isExcel) {

                sql += "/* Excel Download */ ";
            }

            sql +=
                    " SELECT A.*, B.admin_id, B.content, C.mb_id  " +
                            " FROM adjustment.mb_balance_log  AS A " +
                            " LEFT JOIN coinmarketing.admin_payment_repayment AS B " +
                            "   ON A.standard_idx = B.no " +
                            " LEFT JOIN coinmarketing.member AS C " +
                            "   ON C.mb_no = A.mb_no " +
                            " %s " +
                            " ORDER BY up_dt DESC " +
                            " LIMIT :page, :count ";

            String countSql = "SELECT 1 AS total, " +
                    "(SELECT COUNT(*) FROM adjustment.mb_balance_log %s) AS search";

            List<String> conditionList = new ArrayList<>();

            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
            parameterSource.addValue("page", (currentPage - 1) * rowCntPerPage);
            parameterSource.addValue("count", rowCntPerPage);

            if (changeDateFrom != null && changeDateFrom.length() > 0) {
                conditionList.add("up_dt >= :changeDateFrom ");
                parameterSource.addValue("changeDateFrom", changeDateFrom);
            }

            if (changeDateTo != null && changeDateTo.length() > 0) {
                conditionList.add("up_dt < :changeDateTo ");
                parameterSource.addValue("changeDateTo", Utils.GetDateStringAddDays(changeDateTo, "yyyy.MM.dd", 1));
            }

            if (mb_id != null && mb_id.length() > 0) {
//                conditionList.add(" mb_id LIKE CONCAT(:mb_id, '%') ");
                conditionList.add("mb_id = :mb_id");
                parameterSource.addValue("mb_id", mb_id);
            }

            if (action != null && action.length() > 0 && !action.equals("ALL")) {
                if (action.equals("admin") || action.equals("system")) {
                    conditionList.add("action LIKE CONCAT(:action, '%') ");
                    parameterSource.addValue("action", action);
                } else {
                    conditionList.add("type = :action ");
                    parameterSource.addValue("action", action);
                }
            } else {
                conditionList.add("type IN ('admin_pay', 'admin_retrieve', 'bonus')");
            }

            // if (coin != null && coin.length() > 0 && !coin.equals("ALL")) {
            //     conditionList.add("(SUBSTRING_INDEX(market_name, '/', 1) = :coin OR coin_name = :coin) ");
            //     parameterSource.addValue("coin", coin);
            // }

            // if (market != null && market.length() > 0 && !market.equals("ALL")) {
            //     conditionList.add("(SUBSTRING_INDEX(market_name, '/', -1) = :market) ");
            //     parameterSource.addValue("market", market);
            // }

            // if (asset_coin != null && asset_coin.length() > 0 && !asset_coin.equals("ALL")) {
            //     conditionList.add("((`action` = 'bid' AND SUBSTRING_INDEX(market_name, '/', 1) = :asset_coin) " +
            //             "   OR " +
            //             "   (`action` = 'ask' AND SUBSTRING_INDEX(market_name, '/', -1) = :asset_coin) " +
            //             "   OR " +
            //             "   (coin_name = :asset_coin))");
            //     parameterSource.addValue("asset_coin", asset_coin);
            // }

            String where = Utils.GetWhereString(conditionList);

            sql = String.format(sql, where);
            countSql = String.format(countSql, where);

            List<MemberBalanceLog> list = namedParameterJdbcTemplate.query(sql, parameterSource, new BeanPropertyRowMapper<>(MemberBalanceLog.class));
            Counts counts = namedParameterJdbcTemplate.queryForObject(countSql, parameterSource, new BeanPropertyRowMapper<>(Counts.class));

            resultSet.put("list", list);
            resultSet.put("counts", counts);

        } catch (EmptyResultDataAccessException ignore) {

        } catch (Exception e) {
            e.printStackTrace();
            ;
        }

        return resultSet;

    }
    public HashMap<String, Object> GetMemberBalanceLog_no(
            String changeDateFrom,
            String changeDateTo,
            String mb_no,
            String action,
            String coin,
            String market,
            String asset_coin,
            int currentPage,
            int rowCntPerPage,
            boolean isExcel
    ) {
        HashMap<String, Object> resultSet = new HashMap<>();
        resultSet.put("memberBalanceLogList", new ArrayList<String>());
        try {

            String sql = "";

            // 엑셀다운로드일 때
            if (isExcel) {

                sql += "/* Excel Download */ ";
            }

            sql +=
                    " SELECT A.*, B.admin_id, B.content, C.mb_id  " +
                            " FROM adjustment.mb_balance_log  AS A " +
                            " LEFT JOIN coinmarketing.admin_payment_repayment AS B " +
                            "   ON A.standard_idx = B.no " +
                            " LEFT JOIN coinmarketing.member AS C " +
                            "   ON C.mb_no = A.mb_no " +
                            " %s " +
                            " ORDER BY up_dt DESC " +
                            " LIMIT :page, :count ";

            String countSql = "SELECT 1 AS total, " +
                    "(SELECT COUNT(*) FROM adjustment.mb_balance_log AS A %s) AS search";

            List<String> conditionList = new ArrayList<>();

            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
            parameterSource.addValue("page", (currentPage - 1) * rowCntPerPage);
            parameterSource.addValue("count", rowCntPerPage);

            if (changeDateFrom != null && changeDateFrom.length() > 0) {
                conditionList.add("up_dt >= :changeDateFrom ");
                parameterSource.addValue("changeDateFrom", changeDateFrom);
            }

            if (changeDateTo != null && changeDateTo.length() > 0) {
                conditionList.add("up_dt < :changeDateTo ");
                parameterSource.addValue("changeDateTo", Utils.GetDateStringAddDays(changeDateTo, "yyyy.MM.dd", 1));
            }

            if (mb_no != null && mb_no.length() > 0) {
//                conditionList.add(" mb_id LIKE CONCAT(:mb_id, '%') ");
                conditionList.add("A.mb_no = :mb_no");
                parameterSource.addValue("mb_no", mb_no);
            }

            if (action != null && action.length() > 0 && !action.equals("ALL")) {
                if (action.equals("admin") || action.equals("system")) {
                    conditionList.add("action LIKE CONCAT(:action, '%') ");
                    parameterSource.addValue("action", action);
                } else {
                    conditionList.add("type = :action ");
                    parameterSource.addValue("action", action);
                }
            } else {
                conditionList.add("type IN ('admin_pay', 'admin_retrieve', 'bonus')");
            }

            // if (coin != null && coin.length() > 0 && !coin.equals("ALL")) {
            //     conditionList.add("(SUBSTRING_INDEX(market_name, '/', 1) = :coin OR coin_name = :coin) ");
            //     parameterSource.addValue("coin", coin);
            // }

            // if (market != null && market.length() > 0 && !market.equals("ALL")) {
            //     conditionList.add("(SUBSTRING_INDEX(market_name, '/', -1) = :market) ");
            //     parameterSource.addValue("market", market);
            // }

            // if (asset_coin != null && asset_coin.length() > 0 && !asset_coin.equals("ALL")) {
            //     conditionList.add("((`action` = 'bid' AND SUBSTRING_INDEX(market_name, '/', 1) = :asset_coin) " +
            //             "   OR " +
            //             "   (`action` = 'ask' AND SUBSTRING_INDEX(market_name, '/', -1) = :asset_coin) " +
            //             "   OR " +
            //             "   (coin_name = :asset_coin))");
            //     parameterSource.addValue("asset_coin", asset_coin);
            // }

            String where = Utils.GetWhereString(conditionList);

            sql = String.format(sql, where);
            countSql = String.format(countSql, where);

            List<MemberBalanceLog> list = namedParameterJdbcTemplate.query(sql, parameterSource, new BeanPropertyRowMapper<>(MemberBalanceLog.class));
            Counts counts = namedParameterJdbcTemplate.queryForObject(countSql, parameterSource, new BeanPropertyRowMapper<>(Counts.class));

            resultSet.put("list", list);
            resultSet.put("counts", counts);

        } catch (EmptyResultDataAccessException ignore) {

        } catch (Exception e) {
            e.printStackTrace();
            ;
        }

        return resultSet;

    }

    private static StaticDataManager staticDataManager = new StaticDataManager();

    public List<String> GetAdjCoinList(String tableName) {
        List<String> adjCoinList = new ArrayList<>();
        try {
            String staticDataTag = "GetAdjCoinList_" + tableName;
            adjCoinList = (List<String>) staticDataManager.GetStaticData(staticDataTag, ServerConfigs.STATIC_DATA_VALID_SECS);
            if (adjCoinList == null) {
                String sql = null;
                switch (tableName) {
                    case "mb_balance_log":
                        // 체결 내역을 포함한 코인 리스트 추출 필요
                        sql = String.format("SELECT SUBSTRING_INDEX(a.market_name, '/', 1) as coin FROM (" +
                                " SELECT market_name, `action` FROM adjustment.%s GROUP BY market_name, `action` " +
                                " ) a " +
                                "WHERE `action` = 'bid' OR `action` = 'ask' GROUP BY coin " +
                                "UNION SELECT coin_name as coin FROM adjustment.%s WHERE coin_name IS NOT NULL GROUP BY coin", tableName, tableName);
                        break;
                    case "mb_balance_log_exc":
                        // 체결 내역을 제외하고는 코인 리스트를 추출할 필요가 없음
                        sql = String.format("SELECT SUBSTRING_INDEX(a.market_name, '/', 1) as coin FROM (" +
                                " SELECT market_name, `action` FROM adjustment.%s GROUP BY market_name, `action`" +
                                ") a " +
                                " WHERE `action` = 'bid' OR `action` = 'ask' GROUP BY coin ", tableName.replace("_exc", ""));
                        break;
                    case "mb_order_history":
                        sql = String.format("SELECT SUBSTRING_INDEX(a.market_name, '/', 1) AS coin " +
                                "from ( " +
                                "    select market_name " +
                                "    from adjustment.%s " +
                                "    group by market_name) a " +
                                "group by coin;", tableName);
                        break;
                    case "member_balance_history":
                        sql = String.format("SELECT coin_name FROM adjustment.%s GROUP BY coin_name", tableName);
                        break;
                    default:
                        break;
                }

                adjCoinList = namedParameterJdbcTemplate.queryForList(sql, new MapSqlParameterSource(), String.class);

                staticDataManager.UpdateStaticData(staticDataTag, adjCoinList);
            }

        } catch (EmptyResultDataAccessException ignored) {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return adjCoinList;
    }


    /**
     * @brief 동기화 후 sync_history에 테이블명 넣어준다.\n
     * @details \n
     */
    public int insertSyncHistory(String tableName) {

        String query = "INSERT INTO adjustment.sync_history (table_name, end_dt) VALUES (:tableName, now())";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("tableName", tableName);
        return namedParameterJdbcTemplate.update(query, mapSqlParameterSource);
    }


    public List<ExtraPaySnapshotHistory> getExtraPaySnapshotHistoryList(int snapshot_idx){
        String query = "SELECT * FROM adjustment.extrapay_snapshot_history WHERE snapshot_idx = :snapshot_idx";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("snapshot_idx", snapshot_idx);
        return namedParameterJdbcTemplate.query(query, mapSqlParameterSource, new BeanPropertyRowMapper<>(ExtraPaySnapshotHistory.class));
    }


    /**
     * 스냅샷 리스트
     */
    public List<ExtraPaySnapshotInfo> selectExtraPaySnapshotInfoList(int start, int size) throws Exception {
        String query = "SELECT * FROM adjustment.extrapay_snapshot_info WHERE snapshot_idx > 0";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

        query += " ORDER BY snapshot_idx desc LIMIT :start, :size";
        mapSqlParameterSource.addValue("start", start);
        mapSqlParameterSource.addValue("size", size);
        return namedParameterJdbcTemplate.query(query, mapSqlParameterSource, new BeanPropertyRowMapper<>(ExtraPaySnapshotInfo.class));
    }

    /**
     * 스냅샷 리스트 카운트
     */
    public int countExtraPaySnapshotInfoList() throws Exception {
        String query = "SELECT COUNT(snapshot_idx) FROM adjustment.extrapay_snapshot_info WHERE snapshot_idx > 0";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, Integer.class);
    }

    /**
     * 스냅샷 등록
     * @param reserve_time
     * @return
     */
    public int insertExtraPaySnapshotInfo(Date reserve_time) {
        String query = "INSERT INTO adjustment.extrapay_snapshot_info (reserve_time) " +
                "VALUES (:reserve_time)";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("reserve_time", reserve_time);

        return namedParameterJdbcTemplate.update(query, mapSqlParameterSource);
    }

    /**
     * 스냅샷 지급 날짜
     * @param reserve_time
     * @return
     */
    public int updateExtraPaySnapshotPaymentDate(int idx, Date reserve_time) {
        String query = "UPDATE adjustment.extrapay_snapshot_info SET pay_date =:reserve_time WHERE snapshot_idx=:idx AND pay_date is NULL";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("idx", idx);
        mapSqlParameterSource.addValue("reserve_time", reserve_time);

        return namedParameterJdbcTemplate.update(query, mapSqlParameterSource);
    }

    /**
     * 스냅샷 지급 취소
     * @param idx
     * @return
     */
    public int updateExtraPaySnapshotPaymentCancel(int idx) {
        String query = "UPDATE adjustment.extrapay_snapshot_info SET pay_date = NULL WHERE snapshot_idx=:idx AND pay_date is NOT NULL";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("idx", idx);

        return namedParameterJdbcTemplate.update(query, mapSqlParameterSource);
    }

    public List<CoinInvest> getInvestCoinInfo() {

        String query = "SELECT (COALESCE(SUM(IF(ACTION='invest', sign_amount, 0)),0)-COALESCE(SUM(IF(ACTION='recovery', adjust_balance, 0)),0)) AS invest, market_name AS coinName"
                + " FROM adjustment.mb_balance_log WHERE ACTION='invest' OR ACTION='recovery' GROUP BY market_name";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        return namedParameterJdbcTemplate.query(query, mapSqlParameterSource, new BeanPropertyRowMapper<>(CoinInvest.class));
    }


    // 전일 당일 입금/출금/투자 현황
    // invest sign_amount
    // deposit sign_balance
    // withdraw adjust_balance
    public List<MonitoringCoinInfo> getMonitoringCoinInfo() {
        String sQuery = "SELECT COUNT(idx) AS proceed, ACTION, coin_name, market_name, SUM(sign_amount) AS sum_sign_amount, SUM(sign_balance) AS sum_sign_balance, SUM(adjust_balance) AS sum_adjust_balance, LEFT(change_date,10) AS cdate FROM adjustment.mb_balance_log WHERE (ACTION='deposit' OR ACTION='withdraw' OR ACTION='invest') AND change_date>:yesterday AND change_date<:today GROUP BY cdate, coin_name, market_name, ACTION";

        Date date = new Date();
        String today = Utils.GetDateStringFromFormat(Utils.GetDateAddDays(date, +1), "yyyy.MM.dd");
        String yesterday = Utils.GetDateStringFromFormat(Utils.GetDateAddDays(date, -1), "yyyy.MM.dd");
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("today", today);
        mapSqlParameterSource.addValue("yesterday", yesterday);
        return namedParameterJdbcTemplate.query(sQuery, mapSqlParameterSource, new BeanPropertyRowMapper<>(MonitoringCoinInfo.class));
    }


    //코인별 미처리 데이터
    public List<UntreatedData> getUntreatedDataList() {
        String sQuery = "SELECT A.coin_name, COALESCE(B.status_count,0) AS untreated_count, COALESCE(B.sum_od_request_coin,0) as coinvalue_count FROM coinmarketing.coin A LEFT OUTER JOIN (SELECT coin_name, COUNT(STATUS) AS status_count, SUM(od_request_coin) AS sum_od_request_coin FROM coinmarketing.member_request_order WHERE (ACTION='withdraw' AND STATUS='REQ') GROUP BY coin_name) B ON A.coin_name=B.coin_name";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        return namedParameterJdbcTemplate.query(sQuery, mapSqlParameterSource, new BeanPropertyRowMapper<>(UntreatedData.class));
    }


    //기준 코인 시세 업데이트
    public int defaultCoinNowPriceUpdate(CoinNowPrice coinNowPrice) {
        String query = "UPDATE coinmarketing.now_price SET krw_price =:krw_price, usd_price =:usd_price WHERE coin_name=:coin_name";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("krw_price", coinNowPrice.getKrw_price());
        mapSqlParameterSource.addValue("usd_price", coinNowPrice.getUsd_price());
        mapSqlParameterSource.addValue("coin_name", coinNowPrice.getCoin_name());

        return namedParameterJdbcTemplate.update(query, mapSqlParameterSource);
    }
}