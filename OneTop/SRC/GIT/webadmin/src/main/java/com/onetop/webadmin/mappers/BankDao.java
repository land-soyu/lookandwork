package com.onetop.webadmin.mappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.onetop.webadmin.controllers.BankingController;
import com.onetop.webadmin.models.Counts;
import com.onetop.webadmin.models.bank.BankCoinRequest;
import com.onetop.webadmin.models.bank.DepositInfo;
import com.onetop.webadmin.models.bank.SystemOption;
import com.onetop.webadmin.models.bank.WithdrawRequest;
import com.onetop.webadmin.models.member.WebMember;
import com.onetop.webadmin.util.Utils;

@Repository
public class BankDao {
    private static final Logger log = LoggerFactory.getLogger(BankingController.class);

    @Autowired
    @Qualifier("namedParameterWebAdminJdbcTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public BankCoinRequest GetBankCoinRequest(
            String od_id
    ){
        BankCoinRequest resultSet = new BankCoinRequest();
        try{
            if(od_id != null && od_id.length() > 0){

                String sql = "";
                sql += "SELECT A.*, C.mb_status_update_dt, C.mb_reg_dt, C.mb_id, D.address, D.reg_dt, D.txid, E.amount as extra_amount, E.reason, E.mg_id, E.is_handwriting " +
                        "FROM (" +
                        " SELECT * FROM coinmarketing.member_buy " +
                        ") AS A " +
                        " LEFT JOIN coinmarketing.member AS C ON A.mb_no = C.mb_no " +
                        " LEFT JOIN coinmarketing.member_buy_transactions AS D ON D.buy_idx = A.idx " +
                        " LEFT JOIN coinmarketing.member_buy_extra AS E ON E.buy_idx = A.idx " +
                        "  %s";

                MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
                List<String> conditionList = new ArrayList<>();

                conditionList.add(" A.idx = :idx ");
                mapSqlParameterSource.addValue("idx", od_id);

                String where = Utils.GetWhereString(conditionList);

                sql = String.format(sql, where);

                List<BankCoinRequest> list = namedParameterJdbcTemplate.query(sql,
                        mapSqlParameterSource,
                        new BeanPropertyRowMapper<>(BankCoinRequest.class));

                for(BankCoinRequest request : list){
                    if(request.getMb_email_confirm_dt() != null){
                        request.setCert_cnt(request.getCert_cnt() + 1);
                    }

                    if(request.getMb_hp_confirm_dt() != null){
                        request.setCert_cnt(request.getCert_cnt() + 1);
                    }

                    if(request.getOtp_reg_dt() != null){
                        request.setCert_cnt(request.getCert_cnt() + 1);
                    }

                    if(request.getMb_idcard_status() != null && request.getMb_idcard_status().equals("Y")){
                        request.setCert_cnt(request.getCert_cnt() + 1);
                    }
                }

                if (list.size() > 0) {
                    resultSet = list.get(0);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return resultSet;
    }
    public HashMap<String, Object> GetAllCoinRequest(
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
        int rowCntPerPage,
        String deposit_memo,
        String withdraw_memo,
        boolean isExcel
){
    HashMap<String, Object> resultSet = new HashMap<>();
    try{
        if(action != null && action.length() > 0 && (action.equals("deposit") || action.equals("withdraw"))){

            String sql = "";

            // 엑셀다운로드일 때
            if (isExcel) {

                sql += "/* Excel Download */ ";
            }

            sql += "SELECT A.*, C.mb_status_update_dt, C.mb_reg_dt, C.mb_id FROM " +
                    "(" +
                    " SELECT * FROM coinmarketing.member_buy " +
                    "  %s" +
                    "  ORDER BY %s %s " +
                    "  LIMIT :currentPage, :rowCntPerPage " +
                    " " +
                    ") AS A " +
                    " LEFT JOIN coinmarketing.member AS C ON A.mb_no = C.mb_no " +
                    "  %s" +
                    " ORDER BY %s %s ";

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

            String countSql = "SELECT 1 AS total, " +
                    "(SELECT COUNT(A.idx) FROM coinmarketing.member_buy AS A LEFT JOIN coinmarketing.member AS B ON A.mb_no = B.mb_no %s) AS search";


            List<String> conditionList = new ArrayList<>();

            if (!action.equals("deposit")) {
                conditionList.add("`action`= :action ");
                // depositKrw가 포함되어 있으면 소액, 고액을 나눠야 하기때문에 추가
                if (action.indexOf("depositkrw") != -1) {

                    String[] actionTemp = action.split("_");
                    mapSqlParameterSource.addValue("action", actionTemp[0]);
                } else {

                    mapSqlParameterSource.addValue("action", action);
                }
            }

            mapSqlParameterSource.addValue("currentPage", (currentPage - 1) * rowCntPerPage);
            mapSqlParameterSource.addValue("rowCntPerPage", rowCntPerPage);

            if (req_dt_from != null && req_dt_from.length() > 0) {
                conditionList.add(" create_dt >= :req_dt_from ");
                mapSqlParameterSource.addValue("req_dt_from", req_dt_from);
            }

            if (req_dt_to != null && req_dt_to.length() > 0) {
                conditionList.add(" create_dt < :req_dt_to ");
                mapSqlParameterSource.addValue("req_dt_to", Utils.GetDateStringAddDays(req_dt_to, "yyyy.MM.dd", 1));
            }

            if( !"ALL".equals(coin_name) ){
                conditionList.add(" coin_name IN (:coin_name) ");
                mapSqlParameterSource.addValue("coin_name", coin_name);
            }

            if(txid != null && txid.length() > 0){
                conditionList.add(" txid LIKE CONCAT(:txid, '%')" );
                mapSqlParameterSource.addValue("txid", txid);
            }

            if(deposit_memo != null && deposit_memo.length() > 0){
                conditionList.add(" deposit_memo LIKE CONCAT(:deposit_memo, '%')" );
                mapSqlParameterSource.addValue("deposit_memo", deposit_memo);
            }

            if(withdraw_memo != null && withdraw_memo.length() > 0){
                conditionList.add(" withdraw_memo LIKE CONCAT(:withdraw_memo, '%')" );
                mapSqlParameterSource.addValue("withdraw_memo", withdraw_memo);
            }

            if(od_request_address != null && od_request_address.length() > 0){
                conditionList.add(" od_request_address LIKE CONCAT(:od_request_address, '%')" );
                mapSqlParameterSource.addValue("od_request_address", od_request_address);
            }

            if(status != null && status.length() > 0 && !status.equals("ALL")){
                conditionList.add(" status = :status" );
                mapSqlParameterSource.addValue("status", status);
            }

            if(action.equals("depositkrw_small")) {

                conditionList.add(" od_request_coin < 1000000" );
            }

            if(action.equals("depositkrw_high")) {

                conditionList.add(" od_request_coin >= 1000000" );
            }

            String where = Utils.GetWhereString(conditionList);

            List<String> conditionList_ = new ArrayList<>();

            if (mb_id != null && mb_id.length() > 0) {
                conditionList_.add(" mb_id LIKE CONCAT(:mb_id,'%') ");
                mapSqlParameterSource.addValue("mb_id", mb_id);
            }

            String searchwhere = Utils.GetWhereString(conditionList_);

            sql = String.format(sql, where, sortName, sortOrderBy, searchwhere, sortName, sortOrderBy);
            log.error("sql [ "+sql+" ]");
            log.error("where [ "+where+" ]");

            if (mb_id != null && mb_id.length() > 0) {
                conditionList.add(" mb_id LIKE CONCAT(:mb_id,'%') ");
                mapSqlParameterSource.addValue("mb_id", mb_id);
            }
            where = Utils.GetWhereString(conditionList);

            countSql = String.format(countSql, where);
            log.error("countSql [ "+countSql+" ]");

            log.error("mapSqlParameterSource [ "+mapSqlParameterSource.toString()+" ]");
            List<BankCoinRequest> list = namedParameterJdbcTemplate.query(sql,
                    mapSqlParameterSource,
                    new BeanPropertyRowMapper<>(BankCoinRequest.class));

            for(BankCoinRequest request : list){
                if(request.getMb_email_confirm_dt() != null){
                    request.setCert_cnt(request.getCert_cnt() + 1);
                }

                if(request.getMb_hp_confirm_dt() != null){
                    request.setCert_cnt(request.getCert_cnt() + 1);
                }

                if(request.getOtp_reg_dt() != null){
                    request.setCert_cnt(request.getCert_cnt() + 1);
                }

                if(request.getMb_idcard_status() != null && request.getMb_idcard_status().equals("Y")){
                    request.setCert_cnt(request.getCert_cnt() + 1);
                }
            }


            Counts counts = namedParameterJdbcTemplate.queryForObject(countSql,
                    mapSqlParameterSource,
                    new BeanPropertyRowMapper<>(Counts.class));

            resultSet.put("list", list);
            resultSet.put("counts", counts);
        }
        else{
            resultSet.put("list", new ArrayList<WebMember>());
            resultSet.put("counts", new Counts());
        }
    }
    catch (Exception e){
        e.printStackTrace();
        resultSet.put("list", new ArrayList<WebMember>());
        resultSet.put("counts", new Counts());
    }

    return resultSet;
}

    public BankCoinRequest GetCoinRequest(String req_id, String mb_id){
        BankCoinRequest bankCoinRequest = null;

        try{

            String sql = "SELECT A.*, B.mb_status_update_dt, B.mb_reg_dt, B.mb_use_language FROM ( " +
                    "  SELECT * FROM coinmarketing.member_request_order WHERE od_id = :req_id AND mb_id  = :mb_id " +
                    ")  AS A " +
                    "LEFT JOIN coinmarketing.member AS B ON A.mb_no = B.mb_no";
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("req_id", req_id);
            mapSqlParameterSource.addValue("mb_id", mb_id);

            bankCoinRequest = namedParameterJdbcTemplate.queryForObject(sql, mapSqlParameterSource, new BeanPropertyRowMapper<>(BankCoinRequest.class));

            if(bankCoinRequest.getMb_email_confirm_dt() != null){
                bankCoinRequest.setCert_cnt(bankCoinRequest.getCert_cnt() + 1);
            }

            if(bankCoinRequest.getOtp_reg_dt() != null){
                bankCoinRequest.setCert_cnt(bankCoinRequest.getCert_cnt() + 1);
            }
        }
        catch(EmptyResultDataAccessException e){

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return bankCoinRequest;
    }

    public Boolean SetHoldCoinRequest(String od_id, String reason){
        try{
            String sql = "UPDATE coinmarketing.member_request_order SET `status` = 'HOLD', reject = :reject, admin_confirm_dt = NOW() WHERE od_id = :od_id AND `action`='withdraw'";
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("reject", reason);
            mapSqlParameterSource.addValue("od_id", od_id);

            namedParameterJdbcTemplate.update(sql, mapSqlParameterSource);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 입금 처리 bank_transaction 테이블 status값에 따른 is_point_pay_yn, deposit_confirm_yn값 변경
     * @param req_id
     * @param status
     * @return
     * @throws Exception
     */
    public int updateBankTransaction(int req_id, String status) throws Exception {

        String query = "UPDATE coinmarketing.bank_transactions";

        if (status.equals("REJECT")) {

            query += " SET is_point_pay_yn = 'N'";
        } else if (status.equals("REQ")) {

            query += " SET deposit_confirm_yn = 'Y'";
        }

        query += " WHERE req_id = :req_id";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("req_id", req_id);

        return namedParameterJdbcTemplate.update(query, parameterSource);
    }


    /**
     * 환불 처리시 포인트 반영여부가 미반영인 경우 상태값을 'CANCEL' 로 바꿔준다.
     * @param req_id
     * @return
     */
    public int updateRequestOrderStatusCancel(int req_id) {

        String query = "UPDATE coinmarketing.member_request_order SET `status` = 'CANCEL',  admin_confirm_dt = NOW() WHERE req_id = :req_id ";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("req_id", req_id);

        return namedParameterJdbcTemplate.update(query, parameterSource);
    }




    public HashMap<String, Object> GetAllCoinRequest_(
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
            int rowCntPerPage,
            String deposit_memo,
            String withdraw_memo,
            boolean isExcel
    ){
        HashMap<String, Object> resultSet = new HashMap<>();
        try{
            if(action != null && action.length() > 0 && (action.equals("wait") || action.equals("ALL") || action.equals("refund"))){

                String sql = "";

                // 엑셀다운로드일 때
                if (isExcel) {

                    sql += "/* Excel Download */ ";
                }

                // sql += "SELECT A.*, C.mb_status_update_dt, C.mb_reg_dt FROM" +
                //         "(" +
                //         " SELECT od_id FROM coinmarketing.member_request_order " +
                //         "  %s" +
                //         "  ORDER BY %s %s " +
                //         "  LIMIT :currentPage, :rowCntPerPage " +
                //         " " +
                //         ") AS B LEFT JOIN coinmarketing.member_request_order AS A ON B.od_id = A.od_id " +
                //         " LEFT JOIN coinmarketing.member AS C ON A.mb_no = C.mb_no " +
                //         " ORDER BY %s %s ";
                sql += "SELECT * FROM ( SELECT @ROWNUM := @ROWNUM +1 AS rownum,T.* FROM (" +
                        "SELECT A.*, B.mb_id, C.id as approve_id " +
                        "FROM coinmarketing.member_withdraw AS A " +
                        "LEFT JOIN coinmarketing.member AS B " +
                        "  ON A.mb_no = B.mb_no " +
                        "LEFT JOIN coinmarketing.admin_member AS C " +
                        "  ON A.withdraw_batch_id = C.no " +
                        " %s " +
                        "ORDER BY %s %s " +
                        ") T, (SELECT @ROWNUM := 0 ) AS TMP ORDER BY %s %s) SUB ORDER BY SUB.ROWNUM DESC " +
                        "LIMIT :currentPage, :rowCntPerPage ";

                MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

                String countSql = "SELECT 1 AS total, " +
                        "(SELECT COUNT(A.idx) FROM coinmarketing.member_withdraw AS A LEFT JOIN coinmarketing.member AS B ON A.mb_no = B.mb_no %s) AS search";


                List<String> conditionList = new ArrayList<>();

                if (action.equals("ALL")) {
                    if(status != null && status.length() > 0 && !status.equals("ALL")){
                        conditionList.add(" status = :status" );
                        mapSqlParameterSource.addValue("status", status);
                    } else {
                        conditionList.add("`status` IN ('complete', 'pending', 'reject') ");
                    }
                } else {
                    conditionList.add("`status`= :status ");

                    // depositKrw가 포함되어 있으면 소액, 고액을 나눠야 하기때문에 추가
                    if (action.indexOf("depositkrw") != -1) {
    
                        String[] actionTemp = action.split("_");
                        mapSqlParameterSource.addValue("status", actionTemp[0]);
                    } else {
                        mapSqlParameterSource.addValue("status", action);
                    }
                }

                mapSqlParameterSource.addValue("currentPage", (currentPage - 1) * rowCntPerPage);
                mapSqlParameterSource.addValue("rowCntPerPage", rowCntPerPage);

                if (mb_id != null && mb_id.length() > 0) {
                    conditionList.add(" mb_id LIKE CONCAT(:mb_id,'%') ");
                    mapSqlParameterSource.addValue("mb_id", mb_id);
                }

                if (action.equals("wait")) {
                    // if (req_dt_from != null && req_dt_from.length() > 0) {
                    //     conditionList.add(" update_dt >= :req_dt_from ");
                    //     mapSqlParameterSource.addValue("req_dt_from", req_dt_from);
                    // }
    
                    // if (req_dt_to != null && req_dt_to.length() > 0) {
                    //     conditionList.add(" update_dt < :req_dt_to ");
                    //     mapSqlParameterSource.addValue("req_dt_to", Utils.GetDateStringAddDays(req_dt_to, "yyyy.MM.dd", 1));
                    // }
                } else {
                    if (req_dt_from != null && req_dt_from.length() > 0) {
                        conditionList.add(" create_dt >= :req_dt_from ");
                        mapSqlParameterSource.addValue("req_dt_from", req_dt_from);
                    }
    
                    if (req_dt_to != null && req_dt_to.length() > 0) {
                        conditionList.add(" create_dt < :req_dt_to ");
                        mapSqlParameterSource.addValue("req_dt_to", Utils.GetDateStringAddDays(req_dt_to, "yyyy.MM.dd", 1));
                    }
                }

                if( !"ALL".equals(coin_name) ){
                    conditionList.add(" coin_name IN (:coin_name) ");
                    mapSqlParameterSource.addValue("coin_name", coin_name);
                }

                if(txid != null && txid.length() > 0){
                    conditionList.add(" txid LIKE CONCAT(:txid, '%')" );
                    mapSqlParameterSource.addValue("txid", txid);
                }

                if(deposit_memo != null && deposit_memo.length() > 0){
                    conditionList.add(" deposit_memo LIKE CONCAT(:deposit_memo, '%')" );
                    mapSqlParameterSource.addValue("deposit_memo", deposit_memo);
                }

                if(withdraw_memo != null && withdraw_memo.length() > 0){
                    conditionList.add(" withdraw_memo LIKE CONCAT(:withdraw_memo, '%')" );
                    mapSqlParameterSource.addValue("withdraw_memo", withdraw_memo);
                }

                if(od_request_address != null && od_request_address.length() > 0){
                    conditionList.add(" withdraw_address LIKE CONCAT(:od_request_address, '%')" );
                    mapSqlParameterSource.addValue("od_request_address", od_request_address);
                }

                if(action.equals("depositkrw_small")) {

                    conditionList.add(" od_request_coin < 1000000" );
                }

                if(action.equals("depositkrw_high")) {

                    conditionList.add(" od_request_coin >= 1000000" );
                }

                String where = Utils.GetWhereString(conditionList);


                System.out.println("sql [ "+sql+" ]");
                if (sortOrderBy.equals("ASC")) {
                    sql = String.format(sql, where, sortName, sortOrderBy, sortName, new String("DESC"));
                } else {
                    sql = String.format(sql, where, sortName, sortOrderBy, sortName, new String("ASC"));
                }
                System.out.println("sql [ "+sql+" ]");
                System.out.println("where [ "+where+" ]");
                countSql = String.format(countSql, where);
                System.out.println("countSql [ "+countSql+" ]");

                System.out.println("mapSqlParameterSource [ "+mapSqlParameterSource.toString()+" ]");
                List<WithdrawRequest> list = namedParameterJdbcTemplate.query(sql,
                        mapSqlParameterSource,
                        new BeanPropertyRowMapper<>(WithdrawRequest.class));

                for(WithdrawRequest request : list){
                    // if(request.getMb_email_confirm_dt() != null){
                    //     request.setCert_cnt(request.getCert_cnt() + 1);
                    // }

                    // if(request.getMb_hp_confirm_dt() != null){
                    //     request.setCert_cnt(request.getCert_cnt() + 1);
                    // }

                    // if(request.getOtp_reg_dt() != null){
                    //     request.setCert_cnt(request.getCert_cnt() + 1);
                    // }

                    // if(request.getMb_idcard_status() != null && request.getMb_idcard_status().equals("Y")){
                    //     request.setCert_cnt(request.getCert_cnt() + 1);
                    // }
                }


                Counts counts = namedParameterJdbcTemplate.queryForObject(countSql,
                        mapSqlParameterSource,
                        new BeanPropertyRowMapper<>(Counts.class));

                resultSet.put("list", list);
                resultSet.put("counts", counts);
            }
            else{
                resultSet.put("list", new ArrayList<WebMember>());
                resultSet.put("counts", new Counts());
            }
        }
        catch (Exception e){
            e.printStackTrace();
            resultSet.put("list", new ArrayList<WebMember>());
            resultSet.put("counts", new Counts());
        }

        return resultSet;
    }
    public HashMap<String, Object> GetAllCoinRequest_(
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
            int rowCntPerPage,
            String deposit_memo,
            String withdraw_memo,
            boolean isExcel,
            String search_value
    ){
        HashMap<String, Object> resultSet = new HashMap<>();
        try{
            if(action != null && action.length() > 0 && (action.equals("wait") || action.equals("complete"))){

                String sql = "";

                // 엑셀다운로드일 때
                sql += "SELECT A.*, B.mb_id " +
                        "FROM coinmarketing.member_withdraw AS A " +
                        "LEFT JOIN coinmarketing.member AS B " +
                        "  ON A.mb_no = B.mb_no " +
                        " %s " +
                        "ORDER BY %s %s ";

                MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

                String countSql = "SELECT 1 AS total, " +
                        "(SELECT COUNT(idx) FROM coinmarketing.member_withdraw %s) AS search";

                List<String> conditionList = new ArrayList<>();
                conditionList.add(" `idx` IN ("+search_value+") ");
                String where = Utils.GetWhereString(conditionList);

                System.out.println("sql [ "+sql+" ]");
                sql = String.format(sql, where, sortName, sortOrderBy);
                System.out.println("sql [ "+sql+" ]");
                System.out.println("where [ "+where+" ]");
                countSql = String.format(countSql, where);
                System.out.println("countSql [ "+countSql+" ]");

                System.out.println("mapSqlParameterSource [ "+mapSqlParameterSource.toString()+" ]");
                List<WithdrawRequest> list = namedParameterJdbcTemplate.query(sql,
                        mapSqlParameterSource,
                        new BeanPropertyRowMapper<>(WithdrawRequest.class));

                for(WithdrawRequest request : list){
                    // if(request.getMb_email_confirm_dt() != null){
                    //     request.setCert_cnt(request.getCert_cnt() + 1);
                    // }

                    // if(request.getMb_hp_confirm_dt() != null){
                    //     request.setCert_cnt(request.getCert_cnt() + 1);
                    // }

                    // if(request.getOtp_reg_dt() != null){
                    //     request.setCert_cnt(request.getCert_cnt() + 1);
                    // }

                    // if(request.getMb_idcard_status() != null && request.getMb_idcard_status().equals("Y")){
                    //     request.setCert_cnt(request.getCert_cnt() + 1);
                    // }
                }


                Counts counts = namedParameterJdbcTemplate.queryForObject(countSql,
                        mapSqlParameterSource,
                        new BeanPropertyRowMapper<>(Counts.class));

                resultSet.put("list", list);
                resultSet.put("counts", counts);
            }
            else{
                resultSet.put("list", new ArrayList<WebMember>());
                resultSet.put("counts", new Counts());
            }
        }
        catch (Exception e){
            e.printStackTrace();
            resultSet.put("list", new ArrayList<WebMember>());
            resultSet.put("counts", new Counts());
        }

        return resultSet;
    }

    public HashMap<String, Object> GetOldCoinRequest_(
            int mb_no
    ){
        HashMap<String, Object> resultSet = new HashMap<>();
        try{
            if(mb_no != -1 && mb_no > 0 ){

                String sql = "";

                sql += "SELECT A.*, B.mb_id " +
                        "FROM coinmarketing.member_withdraw AS A " +
                        "LEFT JOIN coinmarketing.member AS B " +
                        "  ON A.mb_no = B.mb_no " +
                        " %s " +
                        "ORDER BY %s %s " ;

                MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

                List<String> conditionList = new ArrayList<>();

                conditionList.add("`status`= 'complete' ");
                conditionList.add(" A.mb_no = "+mb_no);

                String where = Utils.GetWhereString(conditionList);

                System.out.println("sql [ "+sql+" ]");
                sql = String.format(sql, where, "update_dt", "DESC");
                System.out.println("sql [ "+sql+" ]");
                System.out.println("where [ "+where+" ]");

                System.out.println("mapSqlParameterSource [ "+mapSqlParameterSource.toString()+" ]");
                List<WithdrawRequest> list = namedParameterJdbcTemplate.query(sql,
                        mapSqlParameterSource,
                        new BeanPropertyRowMapper<>(WithdrawRequest.class));

                resultSet.put("list", list);
            }
            else{
                resultSet.put("list", new ArrayList<WebMember>());
            }
        }
        catch (Exception e){
            e.printStackTrace();
            resultSet.put("list", new ArrayList<WebMember>());
        }

        return resultSet;
    }



    
    /**
     * 승인 출금
     * @param idx
     * @return
     */
    public Boolean updateApprove(int idx, String mg_id) {
        try{
            String query = "UPDATE coinmarketing.member_withdraw SET status = 'pending', update_dt = NOW(), mg_id = :mg_id WHERE `idx` = :idx ";

            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
            parameterSource.addValue("mg_id", mg_id);            
            parameterSource.addValue("idx", idx);            

            namedParameterJdbcTemplate.update(query, parameterSource);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 일괄 승인 출금
     * @param idx
     * @param txid
     * @param mg_id
     * @return
     */
    public Boolean updateAllApprove(String idx, String mg_id) {
        try{
            String insertquery = "INSERT INTO coinmarketing.system_withdraw_batch_process (mg_id, regist_dt) " +
                " VALUES (:mg_id, now())";

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("mg_id", mg_id);
            
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(insertquery, mapSqlParameterSource, holder, new String[] {"GENERATED_ID" });
            int batchid = holder.getKey().intValue();

            String query = "UPDATE coinmarketing.member_withdraw SET status = 'pending', update_dt = NOW(), withdraw_batch_id = :batchid, mg_id = :mg_id WHERE `idx` IN ("+idx+") ";

            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
            parameterSource.addValue("idx", idx);            
            parameterSource.addValue("batchid", batchid);
            parameterSource.addValue("mg_id", mg_id);

            namedParameterJdbcTemplate.update(query, parameterSource);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 개별 거래소 출금
     * @param idx
     * @return
     */
    public Boolean updateExchange(String idx, String mg_id) {
        try{
            String query = "UPDATE coinmarketing.member_withdraw SET status = :status, update_dt = NOW(), withdraw_exchange = 1, mg_id = :mg_id WHERE `idx` = :idx ";

            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
            parameterSource.addValue("status", "complete");
            parameterSource.addValue("mg_id", mg_id);
            parameterSource.addValue("idx", idx);
            namedParameterJdbcTemplate.update(query, parameterSource);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 일괄 거래소 출금
     * @param idx
     * @return
     */
    public Boolean updateAllExchange(String idx, String mg_id) {
        try{
            log.error("[updateAllExchange idx : "+idx);
            log.error("[updateAllExchange mg_id : "+mg_id);
            String insertquery = "INSERT INTO coinmarketing.system_withdraw_batch_process (mg_id, regist_dt) " +
                " VALUES (:mg_id, now())";

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("mg_id", mg_id);

            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(insertquery, mapSqlParameterSource, holder, new String[] {"GENERATED_ID" });
            int batchid = holder.getKey().intValue();

            String query = "UPDATE coinmarketing.member_withdraw SET status = :status, update_dt = NOW(), withdraw_batch_id = :batchid, withdraw_exchange = 1, mg_id = :mg_id WHERE `idx` IN ("+idx+") ";

            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
            parameterSource.addValue("status", "complete");
            parameterSource.addValue("batchid", batchid);
            parameterSource.addValue("mg_id", mg_id);
            namedParameterJdbcTemplate.update(query, parameterSource);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 개별 출금 거절
     * @param idx
     * @param reject
     * @return
     */
    public Boolean updateReject(String idx, String reject, Double total_value, String mg_id) {
        try{
            String query = "UPDATE coinmarketing.member_withdraw SET status = :status, update_dt = NOW(), reject = :reject, mg_id = :mg_id WHERE `idx` = :idx ";

            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
            parameterSource.addValue("status", "reject");
            parameterSource.addValue("reject", reject);
            parameterSource.addValue("idx", idx);
            parameterSource.addValue("mg_id", mg_id);

            namedParameterJdbcTemplate.update(query, parameterSource);

            String query_ = "UPDATE coinmarketing.member SET withdraw_bonus = withdraw_bonus + :total_value  WHERE mb_no = :mb_no ";

            MapSqlParameterSource parameterSource_ = new MapSqlParameterSource();
            parameterSource_.addValue("total_value", total_value);
            parameterSource_.addValue("mb_no", idx);

            namedParameterJdbcTemplate.update(query_, parameterSource_);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 일괄 출금 거절
     * @param idx
     * @param reject
     * @return
     */
    public Boolean updateAllReject(String idx, String reject, Double total_value, String mg_id) {
        try{
            String query = "UPDATE coinmarketing.member_withdraw SET status = :status, update_dt = NOW(), reject = :reject, mg_id = :mg_id  WHERE `idx` IN ("+idx+") ";

            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
            parameterSource.addValue("status", "reject");
            parameterSource.addValue("reject", reject);
            parameterSource.addValue("mg_id", mg_id);

            namedParameterJdbcTemplate.update(query, parameterSource);

            String query_ = "UPDATE coinmarketing.member SET withdraw_bonus = withdraw_bonus + :total_value  WHERE mb_no IN ("+idx+") ";

            MapSqlParameterSource parameterSource_ = new MapSqlParameterSource();
            parameterSource_.addValue("total_value", total_value);

            namedParameterJdbcTemplate.update(query_, parameterSource_);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public Boolean checkApprove(String idx){
        try{
            if(idx != null && idx.length() > 0 ){

                String countSql = "SELECT 1 AS total, " +
                        "(SELECT COUNT(idx) FROM coinmarketing.member_withdraw %s) AS search";

                MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

                List<String> conditionList = new ArrayList<>();

                conditionList.add("`status` IN ('complete', 'pending') ");
                conditionList.add("`idx` IN ("+idx+") ");

                String where = Utils.GetWhereString(conditionList);


                System.out.println("checkApprove where [ "+where+" ]");
                countSql = String.format(countSql, where);
                System.out.println("checkApprove countSql [ "+countSql+" ]");

                Counts counts = namedParameterJdbcTemplate.queryForObject(countSql,
                        mapSqlParameterSource,
                        new BeanPropertyRowMapper<>(Counts.class));

                if (counts.getSearch() < 1) {
                    return false;
                }
            } else{
                return false;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 일괄 출금 거절
     * @param idx
     * @param od_receipt_coin
     * @param buy_point
     * @return
     */
    public Boolean updateWithdrawBuy(String idx, String od_receipt_coin, String buy_point) {
        try{
            String query = "UPDATE coinmarketing.member_buy SET amount = :amount, coin_amount = :coin_amount  WHERE `idx` IN ("+idx+") ";

            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
            String[] amount = od_receipt_coin.split(" ");
            parameterSource.addValue("amount", amount[0].replaceAll(",", ""));
            parameterSource.addValue("coin_amount", buy_point);

            namedParameterJdbcTemplate.update(query, parameterSource);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 매출 처리
     * @param idx
     * @param action_type
     * @param return_amount
     * @param reason
     * @return
     */
    public Boolean updateWithdrawBuy(int idx, String mg_id, String coin_name, String mb_no, String address, String action_type, String return_amount, String reason) {
        try{
            String query = "";
            MapSqlParameterSource parameterSource = new MapSqlParameterSource();

            switch (action_type) {
                case "ACCEPT":
                    String sql = "SELECT * FROM coinmarketing.system_option WHERE `option` = 'BONUS_EXPIRE_DAY'; ";
                    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();        
                    List<SystemOption> list = namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, new BeanPropertyRowMapper<>(SystemOption.class));

                    query = "UPDATE coinmarketing.member_buy SET status = :status, is_complete = 'Y', bonus_expire_dt = DATE_ADD(NOW(), INTERVAL "+((int)list.get(0).getValue())+" DAY), complete_dt = NOW() WHERE `idx` IN ("+idx+") ";
                    parameterSource.addValue("status", "complete");
                    namedParameterJdbcTemplate.update(query, parameterSource);

                    break;
                case "CANCEL":
                    query = "UPDATE coinmarketing.member_buy SET status = :status, complete_dt = NOW() WHERE `idx` IN ("+idx+") ";
                    parameterSource.addValue("status", "cancel");
                    namedParameterJdbcTemplate.update(query, parameterSource);
                    break;
                case "RETURN":
                    query = "UPDATE coinmarketing.member_buy SET status = :status, complete_dt = NOW() WHERE `idx` IN ("+idx+") ";
                    parameterSource.addValue("status", "refund");
                    namedParameterJdbcTemplate.update(query, parameterSource);

                    String insertquery = "INSERT INTO coinmarketing.member_withdraw (mb_no, status, quote_amount, coin_name, withdraw_address, return_amount, return_reason, return_mg_id ) " +
                    " VALUES (:mb_no, :status, :return_amount, :coin_name, :address, :return_amount, :return_reason, :return_mg_id)";
    
                    MapSqlParameterSource mapSqlParameterSource_ = new MapSqlParameterSource();
                    mapSqlParameterSource_.addValue("mb_no", mb_no);
                    mapSqlParameterSource_.addValue("status", "refund");
                    mapSqlParameterSource_.addValue("coin_name", coin_name);
                    mapSqlParameterSource_.addValue("address", address);
                    mapSqlParameterSource_.addValue("return_amount", return_amount);
                    mapSqlParameterSource_.addValue("return_reason", reason);
                    mapSqlParameterSource_.addValue("return_mg_id", mg_id);
                    
                    int batchid = namedParameterJdbcTemplate.update(insertquery, mapSqlParameterSource_);
                    break;
            }

            String insertquery_default = "INSERT INTO coinmarketing.member_buy_extra (buy_idx, amount, reason, mg_id, is_handwriting) " +
            " VALUES (:buy_idx, :amount, :reason, :mg_id, 'Y')";

            MapSqlParameterSource mapSqlParameterSource_default = new MapSqlParameterSource();
            mapSqlParameterSource_default.addValue("buy_idx", idx);
            if (return_amount.equals("")) {
                mapSqlParameterSource_default.addValue("amount", 0);
            } else {
                mapSqlParameterSource_default.addValue("amount", return_amount);
            }
            mapSqlParameterSource_default.addValue("reason", reason);
            mapSqlParameterSource_default.addValue("mg_id", mg_id);
            
            int batchid_default = namedParameterJdbcTemplate.update(insertquery_default, mapSqlParameterSource_default);

            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 관리자 출금요청 상세 페이지
     * @param idx
     * @return
     */
    public WithdrawRequest GetWithdrawRequest(
            int idx
    ){
        WithdrawRequest resultSet;
        try{
            if(idx != -1 && idx > 0 ){

                String sql = "SELECT A.*, B.mb_id " +
                        "FROM coinmarketing.member_withdraw AS A " +
                        "LEFT JOIN coinmarketing.member AS B " +
                        "  ON A.mb_no = B.mb_no " +
                        " %s " +
                        "ORDER BY %s %s " ;

                MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

                List<String> conditionList = new ArrayList<>();

                conditionList.add(" A.idx = "+idx);

                String where = Utils.GetWhereString(conditionList);

                System.out.println("sql [ "+sql+" ]");
                sql = String.format(sql, where, "update_dt", "DESC");
                System.out.println("sql [ "+sql+" ]");
                System.out.println("where [ "+where+" ]");

                System.out.println("mapSqlParameterSource [ "+mapSqlParameterSource.toString()+" ]");
                List<WithdrawRequest> list = namedParameterJdbcTemplate.query(sql,
                        mapSqlParameterSource,
                        new BeanPropertyRowMapper<>(WithdrawRequest.class));

                if (list.size() > 0) {
                    resultSet = list.get(0);
                } else {
                    resultSet = new WithdrawRequest();;
                }
            }
            else{
                resultSet = new WithdrawRequest();;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            resultSet = new WithdrawRequest();;
        }

        return resultSet;
    }


    /**
     * 매출현황 수기등록
     * @param member_id
     * @param member_no
     * @param insert_date
     * @param asset_type
     * @param txid
     * @param buy_point
     * @param reason
     * @param mg_id
     * @return
     */
    public boolean insertHandWriting(String member_id, String member_no, String insert_date, String asset_type, String txid, String buy_point, String reason, String mg_id) {
        try{
            String getSql = "SELECT * FROM coinmarketing.system_option WHERE `option` = 'BONUS_EXPIRE_DAY'; ";            
            List<SystemOption> optionList = namedParameterJdbcTemplate.query(getSql, new MapSqlParameterSource(), new BeanPropertyRowMapper<>(SystemOption.class));

            String insertquery = "INSERT INTO coinmarketing.member_buy (mb_no, is_complete, status, amount, create_dt, complete_dt, coin_name, buy_expire_dt, bonus_expire_dt) " +
            " VALUES (:mb_no, 'Y', :status, :amount, :create_dt, :create_dt, '', DATE_ADD(:create_dt, INTERVAL 2 HOUR), DATE_ADD(:create_dt, INTERVAL "+((int)optionList.get(0).getValue())+" DAY))";

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("mb_no", member_no);
            mapSqlParameterSource.addValue("status", "complete");
            mapSqlParameterSource.addValue("amount", buy_point);
            mapSqlParameterSource.addValue("create_dt", insert_date);
            
            namedParameterJdbcTemplate.update(insertquery, mapSqlParameterSource);


            String sql = "SELECT * FROM coinmarketing.member_buy WHERE mb_no = :mb_no AND status = :status AND amount = :amount AND create_dt = :create_dt AND is_complete = 'N'; ";
            MapSqlParameterSource mapSqlParameterSource_select = new MapSqlParameterSource();        
            mapSqlParameterSource_select.addValue("mb_no", member_no);
            mapSqlParameterSource.addValue("status", "complete");
            mapSqlParameterSource.addValue("amount", buy_point);
            mapSqlParameterSource.addValue("create_dt", insert_date);
            List<BankCoinRequest> list = namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, new BeanPropertyRowMapper<>(BankCoinRequest.class));

            int batchid = 0;
            if (list.size() > 0) {
                batchid = list.get(0).getIdx();
            }

            String insertquery_ = "INSERT INTO coinmarketing.member_buy_extra (buy_idx, amount, reason, mg_id, is_handwriting) " +
            " VALUES (:buy_idx, :amount, :reason, :mg_id, 'Y')";

            MapSqlParameterSource mapSqlParameterSource_ = new MapSqlParameterSource();
            mapSqlParameterSource_.addValue("buy_idx", batchid);
            mapSqlParameterSource_.addValue("amount", buy_point);
            mapSqlParameterSource_.addValue("reason", reason);
            mapSqlParameterSource_.addValue("mg_id", mg_id);
            
            namedParameterJdbcTemplate.update(insertquery_, mapSqlParameterSource_);

            String insertquery_extra = "INSERT INTO coinmarketing.member_buy_transactions (buy_idx, mb_no, coin_name, type, address, amount, reg_dt) " +
            " VALUES (:buy_idx, :mb_no, '', 'deposit', :address, :amount, NOW())";

            MapSqlParameterSource mapSqlParameterSource_extra = new MapSqlParameterSource();
            mapSqlParameterSource_extra.addValue("buy_idx", batchid);
            mapSqlParameterSource_extra.addValue("mb_no", member_no);
            mapSqlParameterSource_extra.addValue("address", txid);
            mapSqlParameterSource_extra.addValue("amount", buy_point);
            mapSqlParameterSource_extra.addValue("create_dt", insert_date);
            
            namedParameterJdbcTemplate.update(insertquery_extra, mapSqlParameterSource_extra);

            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }







    public List<DepositInfo> GetDepositInfo(
        String od_id
    ){
        List<DepositInfo> resultSet = new ArrayList<>();
    try{
        if(od_id != null && od_id.length() > 0){

            String sql = "SELECT address, reg_dt, txid, amount FROM coinmarketing.member_buy_transactions %s";

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            List<String> conditionList = new ArrayList<>();

            conditionList.add(" buy_idx = :buy_idx ");
            mapSqlParameterSource.addValue("buy_idx", od_id);

            String where = Utils.GetWhereString(conditionList);

            sql = String.format(sql, where);

            resultSet = namedParameterJdbcTemplate.query(sql,
                    mapSqlParameterSource,
                    new BeanPropertyRowMapper<>(DepositInfo.class));
        }
    }
    catch (Exception e){
        e.printStackTrace();
    }

    return resultSet;
}

}