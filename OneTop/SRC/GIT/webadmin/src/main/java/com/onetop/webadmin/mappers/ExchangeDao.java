package com.onetop.webadmin.mappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.onetop.webadmin.controllers.BankingController;
import com.onetop.webadmin.models.Counts;
import com.onetop.webadmin.models.adjustment.MemberBalanceLog;
import com.onetop.webadmin.models.exchange.OrderRequestHistory;
import com.onetop.webadmin.models.member.WebMember;
import com.onetop.webadmin.util.Utils;

@Repository
public class ExchangeDao {
    private static final Logger log = LoggerFactory.getLogger(BankingController.class);

    @Autowired
    @Qualifier("namedParameterWebAdminJdbcTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public OrderRequestHistory GetOrderHistory(String mb_id, String ord_no){
        OrderRequestHistory orderRequestHistory = new OrderRequestHistory();
        try{
            String sql = "SELECT A.*, (SELECT IFNULL(SUM(sign_amount), 0) FROM adjustment.mb_balance_log WHERE od_id = B.ord_no) AS sign_amount FROM (" +
                    "SELECT ord_no " +
                    "  FROM adjustment.mb_order_history  " +
                    "  WHERE mb_id = :mb_id AND ord_no = :ord_no " +
                    ") AS B LEFT JOIN adjustment.mb_order_history AS A ON A.ord_no = B.ord_no";

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("mb_id", mb_id);
            mapSqlParameterSource.addValue("ord_no", ord_no);

            orderRequestHistory = namedParameterJdbcTemplate.queryForObject(sql, mapSqlParameterSource, new BeanPropertyRowMapper<>(OrderRequestHistory.class));
        }catch (Exception e){
            e.printStackTrace();
        }
        return orderRequestHistory;
    }


    public HashMap<String, Object> GetAllOrderRequestHistory(
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
            int rowCntPerPage,
            boolean isExcel
    ){
        HashMap<String, Object> resultSet = new HashMap<>();
        try{

            String sql = "";

            // 엑셀다운로드일 때
            if (isExcel) {

                sql += "/* Excel Download */ ";
            }

            sql += "SELECT *, (SELECT IFNULL(SUM(sign_amount), 0) FROM adjustment.mb_balance_log WHERE od_id = ord_no) AS sign_amount " +
                    "FROM adjustment.mb_order_history " +
                    "%s " +
                    "ORDER BY %s %s LIMIT :currentPage, :rowCntPerPage";

            String countSql = "SELECT 1 AS total, " +
                    "(SELECT COUNT(ord_no) FROM  adjustment.mb_order_history %s) AS search";

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

            List<String> conditionList = new ArrayList<>();

            if (mb_id != null && mb_id.length() > 0) {
                conditionList.add(" mb_id LIKE CONCAT(:mb_id,'%') ");
                mapSqlParameterSource.addValue("mb_id", mb_id);
            }

            if (req_dt_from != null && req_dt_from.length() > 0) {
                conditionList.add(" ord_date >= :req_dt_from ");
                mapSqlParameterSource.addValue("req_dt_from", req_dt_from);
            }

            if (req_dt_to != null && req_dt_to.length() > 0) {
                conditionList.add(" ord_date < :req_dt_to ");
                mapSqlParameterSource.addValue("req_dt_to", Utils.GetDateStringAddDays(req_dt_to, "yyyy.MM.dd", 1));
            }

            if( coin_flag != null && coin_flag.length() > 0 && !coin_flag.equals("ALL") ){
                conditionList.add(" market_name LIKE CONCAT(:coin_flag, '%') ");
                mapSqlParameterSource.addValue("coin_flag", coin_flag);
            }

            if( market_flag != null && market_flag.length() > 0 && !market_flag.equals("ALL")){
                conditionList.add(" market_name LIKE CONCAT('%', :market_flag) ");
                mapSqlParameterSource.addValue("market_flag", market_flag);
            }

            if(action != null && action.length() > 0 && !action.equals("ALL")){
                conditionList.add(" `action` = :action");
                mapSqlParameterSource.addValue("action", action);
            }

            if(od_no != null && od_no.length() > 0){
                conditionList.add(" ord_no = :od_no");
                mapSqlParameterSource.addValue("od_no", od_no);
            }

            if(status != null && status.length() > 0){
                if(status.equals("CANCEL")) {
                    conditionList.add(" is_cancel = 1 ");
                }
                else if(status.equals("OK")){
                    conditionList.add(" is_cancel = 0 ");
                }
            }

            if( search_channel != null && search_channel.length() > 0 && !search_channel.equals("ALL") ){
                conditionList.add(" channel = :search_channel");
                mapSqlParameterSource.addValue("search_channel", search_channel);
            }

            if(search_ip != null && search_ip.length() > 0){
                conditionList.add(" req_ip_addr = :search_ip");
                mapSqlParameterSource.addValue("search_ip", search_ip);
            }

            mapSqlParameterSource.addValue("currentPage",  (currentPage - 1) * rowCntPerPage);
            mapSqlParameterSource.addValue("rowCntPerPage",  rowCntPerPage);

            String where = Utils.GetWhereString(conditionList);

            sql = String.format(sql, where, sortName, sortOrderBy);
            System.out.println(sql);
            countSql = String.format(countSql, where);

            List<OrderRequestHistory> list = namedParameterJdbcTemplate.query(sql,
                    mapSqlParameterSource,
                    new BeanPropertyRowMapper<>(OrderRequestHistory.class));

            Counts counts = namedParameterJdbcTemplate.queryForObject(countSql,
                    mapSqlParameterSource,
                    new BeanPropertyRowMapper<>(Counts.class));


            resultSet.put("list", list);
            resultSet.put("counts", counts);

        }
        catch (Exception e){
            e.printStackTrace();
            resultSet.put("list", new ArrayList<WebMember>());
            resultSet.put("counts", new Counts());
        }
        return resultSet;
    }


    public HashMap<String, Object> GetAllSignHistory(
            String mb_id,
            String sign_dt_from, String sign_dt_to,
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
            int rowCntPerPage,
            boolean isExcel
    ){
        HashMap<String, Object> resultSet = new HashMap<>();
        try{
            String sql = "";

            // 엑셀다운로드일 때
            if (isExcel) {

                sql += "/* Excel Download */ ";
            }

            sql += " SELECT * FROM adjustment.mb_balance_log  " +
                    " %s " +
                    "  ORDER BY %s %s LIMIT :currentPage, :rowCntPerPage " +
                    "";

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("currentPage", (currentPage - 1) * rowCntPerPage);
            mapSqlParameterSource.addValue("rowCntPerPage", rowCntPerPage);

            String countSql = "SELECT 1 AS total, " +
                    "(SELECT COUNT(*) FROM  adjustment.mb_balance_log %s) AS search";

            List<String> conditionList = new ArrayList<>();

            if (sign_dt_from != null && sign_dt_from.length() > 0) {
                conditionList.add(" change_date >= :sign_dt_from ");
                mapSqlParameterSource.addValue("sign_dt_from", sign_dt_from);
            }

            if (sign_dt_to != null && sign_dt_to.length() > 0) {
                conditionList.add(" change_date < :sign_dt_to ");
                mapSqlParameterSource.addValue("sign_dt_to", Utils.GetDateStringAddDays(sign_dt_to, "yyyy.MM.dd", 1));
            }


            if(action != null && action.length() > 0 && !action.equals("ALL")){
                conditionList.add(" `action` = :action");
                mapSqlParameterSource.addValue("action", action);
            }
            else{
                conditionList.add(" (`action`='ask' OR `action`='bid') ");
            }


            if (mb_id != null && mb_id.length() > 0) {
                conditionList.add(" mb_id LIKE CONCAT(:mb_id, '%') ");
                mapSqlParameterSource.addValue("mb_id", mb_id);
            }


            if( coin_flag != null && coin_flag.length() > 0 && !coin_flag.equals("ALL") ){
                conditionList.add("  (SUBSTRING_INDEX(market_name, '/', 1) = :coin_flag) ");
                mapSqlParameterSource.addValue("coin_flag", coin_flag);
            }

            if( market_flag != null && market_flag.length() > 0 && !market_flag.equals("ALL")){
                conditionList.add(" (SUBSTRING_INDEX(market_name, '/', -1) = :market_flag) ");
                mapSqlParameterSource.addValue("market_flag", market_flag);
            }


            if(od_no != null && od_no.length() > 0){
                conditionList.add(" od_id = :od_no ");
                mapSqlParameterSource.addValue("od_no", od_no);
            }

            if(sign_no != null && sign_no.length() > 0){
                conditionList.add(" sign_no = :sign_no ");
                mapSqlParameterSource.addValue("sign_no", sign_no);
            }

            if( search_channel != null && search_channel.length() > 0 && !search_channel.equals("ALL") ){
                conditionList.add(" channel = :search_channel");
                mapSqlParameterSource.addValue("search_channel", search_channel);
            }

            if(search_ip != null && search_ip.length() > 0){
                conditionList.add(" ip_addr = :search_ip");
                mapSqlParameterSource.addValue("search_ip", search_ip);
            }

            String where = Utils.GetWhereString(conditionList);

            sql = String.format(sql, where, sortName, sortOrderBy, sortName, sortOrderBy);

            countSql = String.format(countSql, where);

            List<MemberBalanceLog> list = namedParameterJdbcTemplate.query(sql,
                    mapSqlParameterSource,
                    new BeanPropertyRowMapper<>(MemberBalanceLog.class));

            Counts counts = namedParameterJdbcTemplate.queryForObject(countSql,
                    mapSqlParameterSource,
                    new BeanPropertyRowMapper<>(Counts.class));


            resultSet.put("list", list);
            resultSet.put("counts", counts);

        }
        catch (Exception e){
            e.printStackTrace();
            resultSet.put("list", new ArrayList<WebMember>());
            resultSet.put("counts", new Counts());
        }
        return resultSet;
    }


    public List<MemberBalanceLog> GetSignHistory(String mb_id, String ord_no){
        List<MemberBalanceLog> signHistoryList = new ArrayList<>();
        try{
            String sql = " SELECT * FROM adjustment.mb_balance_log  " +
                    " WHERE (`action`='ask' OR `action`='bid') AND mb_id = :mb_id AND od_id = :ord_no " +
                    "";

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("mb_id", mb_id);
            mapSqlParameterSource.addValue("ord_no", ord_no);

            return namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, new BeanPropertyRowMapper<>(MemberBalanceLog.class));

        }
        catch (EmptyResultDataAccessException e){

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return signHistoryList;
    }
}