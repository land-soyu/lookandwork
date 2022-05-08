package com.onetop.webadmin.mappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.StoredProcedureQuery;
import javax.servlet.http.HttpServletRequest;

import com.onetop.webadmin.controllers.MemberController;
import com.onetop.webadmin.models.Counts;
import com.onetop.webadmin.models.adjustment.MemberEmailAuth;
import com.onetop.webadmin.models.authority.AdminMember;
import com.onetop.webadmin.models.member.*;
import com.onetop.webadmin.models.monitoring.CoinInvest;
import com.onetop.webadmin.responses.monitoring.MonitoringResponse;
import com.onetop.webadmin.util.Utils;

import java.util.*;

@Repository
public class MemberDao {
    private static final Logger log = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    @Qualifier("namedParameterWebAdminJdbcTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    private SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(new JdbcTemplate());


    public List<WebMember> GetWebSearchMemberList(String mb_id) {
        try {
            String sql = "SELECT MB.*, HIS.mb_last_login_dt, IFNULL(0, HIS.web_visit_cnt), IFNULL(0, HIS.app_visit_cnt) FROM (" +
                    "    SELECT * FROM coinmarketing.member WHERE mb_id LIKE CONCAT(:mb_id, '%') AND (mb_status = 'OK' OR mb_status = 'REST')" +
                    ") MB LEFT JOIN coinmarketing.member_login_detail HIS ON MB.mb_no = HIS.mb_no";

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("mb_id", mb_id);
            return namedParameterJdbcTemplate.query(
                    sql,
                    mapSqlParameterSource,
                    new BeanPropertyRowMapper<>(WebMember.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<WebMember> GetWebSeacherMemberListByMB_NO(String mb_no) {
        try {
            String sql = "SELECT MB.*, HIS.mb_last_login_dt, IFNULL(0, HIS.web_visit_cnt), IFNULL(0, HIS.app_visit_cnt) FROM (" +
                    "    SELECT * FROM coinmarketing.member WHERE mb_no LIKE CONCAT(:mb_no, '%') AND (mb_status = 'OK' OR mb_status = 'REST')" +
                    ") MB LEFT JOIN coinmarketing.member_login_detail HIS ON MB.mb_no = HIS.mb_no";

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("mb_no", mb_no);
            return namedParameterJdbcTemplate.query(
                    sql,
                    mapSqlParameterSource,
                    new BeanPropertyRowMapper<>(WebMember.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public WebMember GetWebMember(String mb_id) {
        try {
            String sql = "SELECT MB.*, HIS.mb_last_login_dt, PARENT.mb_id AS parent_id, IFNULL(0, HIS.web_visit_cnt), IFNULL(0, HIS.app_visit_cnt) FROM (" +
                    "    SELECT * FROM coinmarketing.member WHERE mb_id = :mb_id" +
                    ") MB LEFT JOIN coinmarketing.member_login_detail HIS ON MB.mb_no = HIS.mb_no" +
                    " LEFT JOIN coinmarketing.member PARENT ON MB.mb_referer = PARENT.mb_id";

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("mb_id", mb_id);
            return namedParameterJdbcTemplate.queryForObject(
                    sql,
                    mapSqlParameterSource,
                    new BeanPropertyRowMapper<>(WebMember.class));
        } catch (EmptyResultDataAccessException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public WebMember GetWebMember_no(String mb_no) {
        try {
            String sql = "SELECT "+
                    " (select sum(amount) from coinmarketing.member_withdraw WITHDRAW  where WITHDRAW.mb_no = :mb_no and (WITHDRAW.status = 'complete' or (WITHDRAW.status = 'refund' and WITHDRAW.fee > 0))) as amount_sum_withdraw, "+
                    " (select count(amount)  from coinmarketing.member_withdraw WITHDRAW  where WITHDRAW.mb_no = :mb_no and (WITHDRAW.status = 'complete' or (WITHDRAW.status = 'refund' and WITHDRAW.fee > 0))) as count_withdraw,"+
                    " (select sum(amount) from coinmarketing.member_buy BUY  where BUY.mb_no = :mb_no and BUY.is_complete = 'Y' and BUY.status = 'complete') as amount_sum_buy,"+
                    " (select count(amount)  from coinmarketing.member_buy BUY  where BUY.mb_no = :mb_no and BUY.is_complete = 'Y' and BUY.status = 'complete') as count_buy,"+
                    " MB.*, HIS.mb_last_login_dt, PARENT.mb_id AS parent_id, IFNULL(0, HIS.web_visit_cnt), IFNULL(0, HIS.app_visit_cnt) FROM (" +
                    "    SELECT * FROM coinmarketing.member WHERE mb_no = :mb_no" +
                    ") MB LEFT JOIN coinmarketing.member_login_detail HIS ON MB.mb_no = HIS.mb_no" +
                    " LEFT JOIN coinmarketing.member PARENT ON MB.mb_referer = PARENT.mb_id"+
                    "";

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("mb_no", mb_no);
            return namedParameterJdbcTemplate.queryForObject(
                    sql,
                    mapSqlParameterSource,
                    new BeanPropertyRowMapper<>(WebMember.class));
        } catch (EmptyResultDataAccessException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String, Object> GetAllWebMemberList(String mb_id, String mb_referrer,
                                                       String mb_reg_dt_from, String mb_reg_dt_to,
                                                       String mb_last_login_dt_from, String mb_last_login_dt_to,
                                                       List<String> stateList,
                                                       List<String> rankList,
                                                       String sortName,
                                                       String sortOrderBy,
                                                       boolean excel,
                                                       int currentPage,
                                                       int rowCntPerPage
    ) {
        HashMap<String, Object> resultSet = new HashMap<>();

        try {
            // covering index 를 사용하여 검색 후 JOIN을 하면 정렬 보장이 안되므로 결과값을 다시 정렬함

            String sql = "";

            if (!excel) {
                sql = "SELECT MB.*, HIS.mb_last_login_dt, IFNULL(0, HIS.web_visit_cnt), IFNULL(0, HIS.app_visit_cnt), "+
                        // "(SELECT COUNT(*) FROM coinmarketing.member WHERE mb_sort_info LIKE CONCAT(MB.mb_sort_info, '%%')) AS total_referrer, "+
                        "(SELECT mb_id FROM coinmarketing.member WHERE mb_no=MB.mb_referer) AS parent_id, "+
                        "(SELECT SUM(amount) FROM coinmarketing.member_buy WHERE mb_no=MB.mb_no AND is_complete='Y' AND status = 'complete') AS buy_total_amount "+
                        " FROM ( " +
                        " SELECT A.* FROM ( " +
                        "   SELECT mb_id  FROM coinmarketing.member   " +
                        "   %s   ";
                if( mb_referrer != null && mb_referrer.length() > 0 )
                    sql = sql + "  ) AS B JOIN coinmarketing.member A ON A .mb_referer = B.mb_id ";
                else
                    sql = sql + "  ) AS B JOIN coinmarketing.member A ON A.mb_id = B.mb_id ";

                sql = sql +") AS MB LEFT JOIN coinmarketing.member_login_detail AS HIS ON MB.mb_no = HIS.mb_no %s" +
                        "  ORDER BY %s %s LIMIT :currentPage, :rowCntPerPage";
            } else {
                sql = "/* Excel Download */  SELECT MB.*, HIS.mb_last_login_dt, IFNULL(0, HIS.web_visit_cnt), IFNULL(0, HIS.app_visit_cnt), "+
                        // "(SELECT COUNT(*) FROM coinmarketing.member WHERE mb_sort_info LIKE CONCAT(MB.mb_sort_info, '%%')) AS total_referrer, "+
                        "(SELECT mb_id FROM coinmarketing.member WHERE mb_no=MB.mb_referer) AS parent_id, "+
                        "(SELECT SUM(amount) FROM coinmarketing.member_buy WHERE mb_no=MB.mb_no AND is_complete='Y' AND status = 'complete') AS buy_total_amount "+
                        " FROM ( " +
                        " SELECT A.* FROM ( " +
                        "   SELECT mb_id  FROM coinmarketing.member   " +
                        "   %s   ";
                if( mb_referrer != null && mb_referrer.length() > 0 )
                    sql = sql + "  ) AS B JOIN coinmarketing.member A ON A .mb_referer = B.mb_id ";
                else
                    sql = sql + "  ) AS B JOIN coinmarketing.member A ON A.mb_id = B.mb_id ";

                sql = sql + ") AS MB LEFT JOIN coinmarketing.member_login_detail AS HIS ON MB.mb_no = HIS.mb_no %s " +
                        "  ORDER BY %s %s";
            }

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("currentPage", (currentPage - 1) * rowCntPerPage);
            mapSqlParameterSource.addValue("rowCntPerPage", rowCntPerPage);

            String countSql = "SELECT 1 AS total, " +
                    "(SELECT COUNT(MB.mb_no) FROM (   " +
                    "                          SELECT A.mb_no FROM (  " +
                    "                            SELECT mb_no  FROM coinmarketing.member   " +
                    "                            %s " +
                    "                           ) AS B JOIN coinmarketing.member A ON A.mb_no = B.mb_no  " +
                    "                         ) AS MB LEFT JOIN coinmarketing.member_login_detail AS HIS ON MB.mb_no = HIS.mb_no %s " +
                    ") AS search";

            List<String> conditionList = new ArrayList<>();

            List<String> lastLoginConditionList = new ArrayList<>();

            if (mb_id != null && mb_id.length() > 0 && (mb_referrer.length() == 0)) {
                conditionList.add(" mb_id LIKE CONCAT(:mb_id,'%') ");
                mapSqlParameterSource.addValue("mb_id", mb_id);
            }

            if (mb_referrer != null && mb_referrer.length() > 0) {
                conditionList.add(" mb_id LIKE CONCAT(:mb_referrer,'%') ");
                mapSqlParameterSource.addValue("mb_referrer", mb_referrer);
            }

            if (mb_reg_dt_from != null && mb_reg_dt_from.length() > 0) {
                conditionList.add(" mb_reg_dt >= :mb_reg_dt_from ");
                mapSqlParameterSource.addValue("mb_reg_dt_from", mb_reg_dt_from);
            }

            if (mb_reg_dt_to != null && mb_reg_dt_to.length() > 0) {
                conditionList.add(" mb_reg_dt < :mb_reg_dt_to ");
                mapSqlParameterSource.addValue("mb_reg_dt_to", Utils.GetDateStringAddDays(mb_reg_dt_to, "yyyy.MM.dd", 1));
            }

            if (mb_last_login_dt_from != null && mb_last_login_dt_from.length() > 0) {
                lastLoginConditionList.add(" HIS.mb_last_login_dt >= :mb_last_login_dt_from ");
                mapSqlParameterSource.addValue("mb_last_login_dt_from", mb_last_login_dt_from);
            }

            if (mb_last_login_dt_to != null && mb_last_login_dt_to.length() > 0) {
                lastLoginConditionList.add(" HIS.mb_last_login_dt < :mb_last_login_dt_to ");
                mapSqlParameterSource.addValue("mb_last_login_dt_to", Utils.GetDateStringAddDays(mb_last_login_dt_to, "yyyy.MM.dd", 1));
            }

            if (stateList.size() > 0) {
                String stateCondition = "";
                String tempStateCondition = "";
                for (int i = 0; i < stateList.size(); i++) {
                    if (!stateList.get(i).equals("ALL")) {
                        tempStateCondition = " (mb_status = :mb_status" + stateList.get(i) + ") ";
                        mapSqlParameterSource.addValue("mb_status" + stateList.get(i), stateList.get(i));
    
                        if (stateCondition.length() == 0) {
                            stateCondition = tempStateCondition;
                        } else {
                            stateCondition += " OR " + tempStateCondition;
                        }
                        }
                }

                if (stateCondition.length() > 0) {
                    conditionList.add(" (" + stateCondition + ") ");
                }
            }

            // if (rankList.size() > 0) {
            //     String rankCondition = "";
            //     String tempRankCondition = "";
            //     for (int i = 0; i < rankList.size(); i++) {
            //         tempRankCondition = " (mb_rank = :mb_rank" + rankList.get(i) + ") ";
            //         mapSqlParameterSource.addValue("mb_rank" + rankList.get(i), rankList.get(i));

            //         if (rankCondition.length() == 0) {
            //             rankCondition = tempRankCondition;
            //         } else {
            //             rankCondition += " OR " + tempRankCondition;
            //         }
            //     }
            //     if (rankCondition.length() > 0) {
            //         conditionList.add(" (" + rankCondition + ") ");
            //     }
            // }

            String where = Utils.GetWhereString(conditionList);

            String lastLoginWhere = Utils.GetWhereString(lastLoginConditionList);

            sql = String.format(sql, where, lastLoginWhere, sortName, sortOrderBy);
            countSql = String.format(countSql, where, lastLoginWhere);


            List<WebMember> list = null;

            if (!excel) {
                list = namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, new BeanPropertyRowMapper<>(WebMember.class));
            } else {
                list = namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, new BeanPropertyRowMapper<>(WebMember.class));
            }

            Counts counts = namedParameterJdbcTemplate.queryForObject(countSql, mapSqlParameterSource, new BeanPropertyRowMapper<>(Counts.class));

            List<String> memberList = new ArrayList<>();

            for (WebMember member : list) {
                if (member.getMb_email_confirm_dt() != null) {
                    member.setCert_cnt(member.getCert_cnt() + 1);
                }

                if (member.getOtp_reg_dt() != null) {
                    member.setCert_cnt(member.getCert_cnt() + 1);
                }
                memberList.add("\"" + member.getMb_no() + "\"");
            }


            if (memberList.size() > 0) {

                String sqlBalance = String.format("SELECT C.*,  " +
                        " ( " +
                        "  SELECT SUM(member_balance.avail * IFNULL(coinmarketing.now_price.usd_price, 1)) AS exchanged " +
                        "   FROM coinmarketing.member_balance " +
                        "   LEFT JOIN coinmarketing.now_price ON coinmarketing.now_price.coin_name = member_balance.coin_name" +
                        "   WHERE mb_no = C.mb_no " +
                        " ) AS exchanged  " +
                        " FROM( " +
                        "  SELECT mb_no " +
                        "  FROM coinmarketing.member_balance " +
                        "  WHERE mb_no IN (%s) " +
                        "  GROUP BY mb_no " +
                        ") AS C", String.join(",", memberList));

                List<WebMemberTotalExchangedBalance> balanceList = namedParameterJdbcTemplate.query(sqlBalance, new MapSqlParameterSource(), new BeanPropertyRowMapper<>(WebMemberTotalExchangedBalance.class));

                for (WebMemberTotalExchangedBalance balance : balanceList) {
                    for (int i = 0; i < list.size(); i++) {
                        WebMember member = list.get(i);
                        if (member.getMb_no() == balance.getMb_no()) {
                            member.setMb_total_asset(balance.getExchanged());
                            list.set(i, member);
                            break;
                        }
                    }
                }
            }

            resultSet.put("list", list);
            resultSet.put("counts", counts);
        } catch (Exception ex) {
            ex.printStackTrace();
            resultSet.put("list", new ArrayList<WebMember>());
            resultSet.put("counts", new Counts());
        }

        return resultSet;
    }

    /**
     * 회원관리 > 보안인증
     *
     * @return
     */
    public HashMap<String, Object> GetAllWebMemberCertList(String mb_id,
                                                           String request_dt_from, String request_dt_to,
                                                           String proct_dt_from, String proct_dt_to,
                                                           List<String> stateList,
                                                           List<String> certList,
                                                           boolean excel,
                                                           int currentPage,
                                                           int rowCntPerPage
    ) {
        HashMap<String, Object> resultSet = new HashMap<>();

        try {

            String sql = "";

            // 엑셀다운로드일 때
            if (excel) {
                sql += "/* Excel Download */ ";
            }

            sql += "SELECT @ROWNUM:=@ROWNUM + 1 as rowNum,  a.* FROM (SELECT @ROWNUM:=0) as tmp, (SELECT log_no FROM coinmarketing.member_security_log %s) AS b JOIN coinmarketing.member_security_log a ON a.log_no = b.log_no ORDER BY a.log_no DESC";
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

            if (!excel) {
                sql += " LIMIT :currentPage, :rowCntPerPage";

                mapSqlParameterSource.addValue("currentPage", (currentPage - 1) * rowCntPerPage);
                mapSqlParameterSource.addValue("rowCntPerPage", rowCntPerPage);
            }

            String countSql = "SELECT 1 AS total, (SELECT COUNT(mb_no) FROM coinmarketing.member_security_log %s) AS search";

            List<String> conditionList = new ArrayList<>();

            if (mb_id != null && mb_id.length() > 0) {
                conditionList.add(" mb_id LIKE CONCAT(:mb_id,'%') ");
                mapSqlParameterSource.addValue("mb_id", mb_id);
            }

            if (request_dt_from != null && request_dt_from.length() > 0) {
                conditionList.add(" reg_dt >= :request_dt_from ");
                mapSqlParameterSource.addValue("request_dt_from", request_dt_from);
            }

            if (request_dt_to != null && request_dt_to.length() > 0) {
                conditionList.add(" reg_dt < :request_dt_to ");
                mapSqlParameterSource.addValue("request_dt_to", Utils.GetDateStringAddDays(request_dt_to, "yyyy.MM.dd", 1));
            }

            if (proct_dt_from != null && proct_dt_from.length() > 0) {
                conditionList.add(" confirm_dt >= :proct_dt_from ");
                mapSqlParameterSource.addValue("proct_dt_from", proct_dt_from);
            }

            if (proct_dt_to != null && proct_dt_to.length() > 0) {
                conditionList.add(" confirm_dt < :proct_dt_to ");
                mapSqlParameterSource.addValue("proct_dt_to", Utils.GetDateStringAddDays(proct_dt_to, "yyyy.MM.dd", 1));
            }

            if (stateList.size() > 0) {
                String stateCondition = "state in (";
                String tempStateCondition = "";

                for (int i = 0; i < stateList.size(); i++) {

                    if (i + 1 == stateList.size()) {
                        tempStateCondition += "'" + stateList.get(i) + "'";
                    } else {
                        tempStateCondition += "'" + stateList.get(i) + "',";
                    }
                }
                stateCondition += tempStateCondition + ")";

                if (stateCondition.length() > 0) {
                    conditionList.add(" (" + stateCondition + ") ");
                }
            }

            if (certList.size() > 0) {
                String certCondition = "security_type IN (:security_type)";
                ArrayList<String> certCondList = new ArrayList<>();
                for (int i = 0; i < certList.size(); i++) {

                    if (certList.get(i).equals("email")) certCondList.add("0");
                    else if (certList.get(i).equals("sms")) certCondList.add("1");
                    else if (certList.get(i).equals("account")) certCondList.add("2");
                    else if (certList.get(i).equals("id_card")) certCondList.add("3");
                    else if (certList.get(i).equals("otp")) certCondList.add("4");
                }

                if (certCondList.size() > 0) {
                    mapSqlParameterSource.addValue("security_type", certCondList);
                    conditionList.add(certCondition);
                }
            }

            String where = Utils.GetWhereString(conditionList);

            sql = String.format(sql, where);
            countSql = String.format(countSql, where);

            //log.info(sql);

            List<WebMemberSecurityLog> list = null;

            if (!excel) {
                list = namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, new BeanPropertyRowMapper<>(WebMemberSecurityLog.class));
            } else {
                list = namedParameterJdbcTemplate.query(sql, mapSqlParameterSource, new BeanPropertyRowMapper<>(WebMemberSecurityLog.class));
            }

            Counts counts = namedParameterJdbcTemplate.queryForObject(countSql, mapSqlParameterSource, new BeanPropertyRowMapper<>(Counts.class));

            resultSet.put("list", list);
            resultSet.put("counts", counts);
        } catch (Exception ex) {
            ex.printStackTrace();
            resultSet.put("list", new ArrayList<WebMemberSecurityLog>());
            resultSet.put("counts", new Counts());
        }

        return resultSet;
    }


    public Map<String, Object> GetWebMemberIdCardInfo(String log_no, String mb_id) {
        List<String> conditionList = new ArrayList<>();

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

        if (log_no != null && log_no.length() > 0) {
            conditionList.add(" A.log_no = :log_no");
            mapSqlParameterSource.addValue("log_no", log_no);
        }

        if (mb_id != null && mb_id.length() > 0) {
            conditionList.add(" A.mb_id = :mb_id");
            mapSqlParameterSource.addValue("mb_id", mb_id);
        }

        if (conditionList.size() > 0) {
            conditionList.add(" A.security_type = 3 ");

            String where = Utils.GetWhereString(conditionList);

            String query = String.format("SELECT A.*, B.mb_use_language FROM coinmarketing.member_security_log A JOIN coinmarketing.member B ON A.mb_no = B.mb_no %s ORDER BY log_no DESC limit 1", where);
            log.info(query);
            return namedParameterJdbcTemplate.queryForMap(query, mapSqlParameterSource);
        }

        return new HashMap<String, Object>();
    }


    @Transactional(rollbackFor = Exception.class)
    public int UpdateWebMemberIdCardRequest(HttpServletRequest req) throws Exception {
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getRemoteAddr();
        }
        String log_no = req.getParameter("log_no");
        String state = req.getParameter("state");
        String confirm_id = req.getParameter("confirm_id");
        String confirm_msg = req.getParameter("confirm_msg");
        String status = state.equals("OK") ? "Y" : state;
        String mb_no = req.getParameter("mb_no");
        String country_code = req.getParameter("country_code");
        String mb_idcard_type = req.getParameter("mb_idcard_type");

        String query = "UPDATE coinmarketing.member_security_log SET state = :state, confirm_id = :confirm_id, confirm_ip = :confirm_ip, confirm_msg = :confirm_msg, confirm_dt = now(), mb_idcard_type = :mb_idcard_type WHERE log_no = :log_no";

        MapSqlParameterSource mapSqlParameterSource_security = new MapSqlParameterSource();
        mapSqlParameterSource_security.addValue("state", state);
        mapSqlParameterSource_security.addValue("confirm_id", confirm_id);
        mapSqlParameterSource_security.addValue("confirm_ip", ip);
        mapSqlParameterSource_security.addValue("confirm_msg", confirm_msg);
        mapSqlParameterSource_security.addValue("log_no", log_no);
        mapSqlParameterSource_security.addValue("mb_idcard_type", mb_idcard_type);

        if (status.equals("Y")) {
            // 승인인 경우에만 필드 업데이트
            // 반려인 경우 건드릴 필요가 없음
            String Memberquery = "UPDATE coinmarketing.member SET mb_level = :mb_level, country_code = :country_code, mb_idcard_status = :mb_idcard_status, mb_idcard_memo = :mb_idcard_memo, mb_idcard_check_dt = now() WHERE mb_no = :mb_no";
            MapSqlParameterSource mapSqlParameterSource_member = new MapSqlParameterSource();

            mapSqlParameterSource_member.addValue("mb_level", 4);
            mapSqlParameterSource_member.addValue("country_code", country_code);
            mapSqlParameterSource_member.addValue("mb_idcard_status", status);
            mapSqlParameterSource_member.addValue("mb_idcard_memo", confirm_msg);
            mapSqlParameterSource_member.addValue("mb_no", mb_no);

            namedParameterJdbcTemplate.update(Memberquery, mapSqlParameterSource_member);
        } else {

            String Memberquery = "UPDATE coinmarketing.member SET mb_idcard_status = :mb_idcard_status WHERE mb_no = :mb_no";
            MapSqlParameterSource mapSqlParameterSource_member = new MapSqlParameterSource();

            mapSqlParameterSource_member.addValue("mb_idcard_status", status);
            mapSqlParameterSource_member.addValue("mb_idcard_memo", confirm_msg);
            mapSqlParameterSource_member.addValue("mb_no", mb_no);

            namedParameterJdbcTemplate.update(Memberquery, mapSqlParameterSource_member);
        }

        return namedParameterJdbcTemplate.update(query, mapSqlParameterSource_security);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean ClearWebMemberCertInfo(AdminMember adminMember, HttpServletRequest req) {
        int mb_no = Integer.parseInt(req.getParameter("mb_no"));
        String mb_id = req.getParameter("mb_id");
        int security_type = Integer.parseInt(req.getParameter("security_type"));
        String ip = req.getHeader("X-FORWARDED-FOR");

        if (ip == null) ip = req.getHeader("Proxy-Client-IP");
        if (ip == null) ip = req.getHeader("WL-Proxy-Client-IP");
        if (ip == null) ip = req.getHeader("HTTP_CLIENT_IP");
        if (ip == null) ip = req.getHeader("HTTP_X_FORWARDED_FOR");
        if (ip == null) ip = req.getRemoteAddr();

        String sqlInsert = "INSERT INTO coinmarketing.member_security_log " +
                " (mb_no, mb_id, security_type, reg_dt, state, confirm_id, confirm_msg, confirm_ip, confirm_dt) " +
                " VALUES (:mb_no, :mb_id, :security_type, NOW(), 'CAN', :confirm_id, 'admin clear certification', :confirm_ip, NOW()) ";

        MapSqlParameterSource mapSqlParameterSource_security = new MapSqlParameterSource();
        mapSqlParameterSource_security.addValue("mb_no", mb_no);
        mapSqlParameterSource_security.addValue("mb_id", mb_id);
        mapSqlParameterSource_security.addValue("security_type", security_type);
        mapSqlParameterSource_security.addValue("confirm_id", adminMember.getId());
        mapSqlParameterSource_security.addValue("confirm_ip", ip);


        namedParameterJdbcTemplate.update(sqlInsert, mapSqlParameterSource_security);

        //인증 구분 (0 : 이메일, 1 : 본인인증(SMS), 2 : 계좌인증, 3 : 신분인증, 4 : OTP 인증)
        // OTP 인증 취소시에는 레벨 조정 및 요청 내역 취소 없음
        final String CELAR_SECURITY_TYPE4 = "UPDATE coinmarketing.member SET otp_secret_key = NULL, otp_reg_dt = NULL WHERE mb_id = :mb_id;";
        // 신분증 취소
        final String CELAR_SECURITY_TYPE3 = "UPDATE coinmarketing.member SET mb_idcard_status = 'N', mb_idcard_check_dt = NULL, mb_idcard_memo = NULL, country_code = NULL, mb_level = 3  WHERE mb_id = :mb_id;";
        // 계좌 취소
        final String CELAR_SECURITY_TYPE2 = "UPDATE coinmarketing.member SET bank_account = NULL, bank_account_mask = NULL, bank_code = NULL,  mb_bank_confirm_dt = NULL, mb_level = 2 WHERE mb_id = :mb_id;";
        // 본인인증(SMS) 취소
        final String CELAR_SECURITY_TYPE1 = "UPDATE coinmarketing.member SET mb_hp = NULL, mb_hp_confirm_dt = NULL, country_num = NULL, mb_level = 1  WHERE mb_id = :mb_id;";

        MapSqlParameterSource mapSqlParameterSource_member = new MapSqlParameterSource();
        mapSqlParameterSource_member.addValue("mb_id", mb_id);

        switch (security_type) {
            case 0:
                // 이메일은 인증 취소 없음
                break;
            case 1:
                // SMS 취소 시 SMS, 계좌, 신분증 삭제
                namedParameterJdbcTemplate.update(CELAR_SECURITY_TYPE3, mapSqlParameterSource_member);
                namedParameterJdbcTemplate.update(CELAR_SECURITY_TYPE2, mapSqlParameterSource_member);
                namedParameterJdbcTemplate.update(CELAR_SECURITY_TYPE1, mapSqlParameterSource_member);
                break;
            case 2:
                // 계좌 취소 시 계좌, 신분증 삭제
                namedParameterJdbcTemplate.update(CELAR_SECURITY_TYPE3, mapSqlParameterSource_member);
                namedParameterJdbcTemplate.update(CELAR_SECURITY_TYPE2, mapSqlParameterSource_member);
                break;
            case 3:
                // 신분증 취소 시 신분증 삭제
                namedParameterJdbcTemplate.update(CELAR_SECURITY_TYPE3, mapSqlParameterSource_member);
                break;
            case 4:
                // OTP 취소 시 다른 액션 없음
                namedParameterJdbcTemplate.update(CELAR_SECURITY_TYPE4, mapSqlParameterSource_member);
                break;
            case 99:
                // 관리자 인증 ???
                break;
            default:
                break;
        }

        // OTP 삭제 요청이 아닌 경우의 요청 상태인 것들은 모두 CAN으로 변경
        if (security_type < 4) {
            String sqlClearRequests = "UPDATE coinmarketing.member_security_log SET state='CAN', confirm_msg='admin clear certification', confirm_dt = NOW(), confirm_id = :confirm_id WHERE state='REQ' AND mb_id = :mb_id AND security_type < 4 AND security_type >= :security_type";
            MapSqlParameterSource mapSqlParameterSource_idcard = new MapSqlParameterSource();
            mapSqlParameterSource_idcard.addValue("confirm_id", adminMember.getId());
            mapSqlParameterSource_idcard.addValue("security_type", security_type);
            mapSqlParameterSource_idcard.addValue("mb_id", mb_id);
            namedParameterJdbcTemplate.update(sqlClearRequests, mapSqlParameterSource_idcard);
        }

        return true;
    }

    /* 신분인증 여권번호 존재 체크 */
    public int countMemeberIdcardNoExist(String idcardNo) throws Exception {

        String query = "SELECT COUNT(log_no) FROM coinmarketing.member_security_log WHERE idcard_no = :idcard_no AND state = 'OK'";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("idcard_no", idcardNo);

        return namedParameterJdbcTemplate.queryForObject(query, mapSqlParameterSource, Integer.class);
    }


    /**
     * 코인 주소 가져오기
     **/
    public List<MemberWalletAddress> getMemberWalletAddressList(String mb_id) throws Exception {

        String query = "SELECT mb_id, w_coin_type, w_addr, created_at, deposit_memo FROM coinmarketing.member_wallet_addr WHERE mb_id = :mb_id";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("mb_id", mb_id);
        return namedParameterJdbcTemplate.query(query, mapSqlParameterSource, new BeanPropertyRowMapper<>(MemberWalletAddress.class));
    }


    /**
     * member 상한액 수정
     **/
    public int modifyExtraPayLimit(int mbNo, int limit) throws Exception {

        String query = "UPDATE coinmarketing.member SET extrapay_limit =:limit where mb_no=:mbNo";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("mbNo", mbNo);
        mapSqlParameterSource.addValue("limit", limit);

        return namedParameterJdbcTemplate.update(query, mapSqlParameterSource);
    }


    /**
     * member 출금가능금액 수정
     **/
    public int modifyWithdrawLimit(int mbNo, int limit) throws Exception {

        String query = "UPDATE coinmarketing.member SET withdraw_limit =:limit where mb_no=:mbNo";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("mbNo", mbNo);
        mapSqlParameterSource.addValue("limit", limit);

        return namedParameterJdbcTemplate.update(query, mapSqlParameterSource);
    }


    /**
     * 지급/회수 Log 리스트 개수
     **/
    public int countPaymentRepaymentList(String search_id, String search_sdate, String search_edate, String search_admin_id, String search_coin, String search_pr_type, String search_content_type) throws Exception {

        String query = "SELECT COUNT(C.no) " +
                "FROM (SELECT A.no " +
                "                  FROM coinmarketing.admin_payment_repayment A JOIN coinmarketing.admin_payment_repayment_member_info B ON A.no = B.pr_no";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

        if (!search_sdate.equals("") && !search_edate.equals("")) {

            query += " AND A.reg_dt BETWEEN :search_sdate AND :search_edate";
            mapSqlParameterSource.addValue("search_sdate", search_sdate + " 00:00:00");
            mapSqlParameterSource.addValue("search_edate", search_edate + " 23:59:59");
        }

        // 서브 쿼리 조회
        if (!search_id.equals("")) {
            query += " AND B.mb_id LIKE CONCAT('%', :search_id, '%')";
            mapSqlParameterSource.addValue("search_id", search_id);
        }

        if (!search_admin_id.equals("")) {
            query += " AND A.admin_id LIKE CONCAT('%', :search_admin_id, '%') ";
            mapSqlParameterSource.addValue("search_admin_id", search_admin_id);
        }

        //if (!search_coin.equals("all")) {
          //  query += " AND A.coin_name = :search_coin";
            //mapSqlParameterSource.addValue("search_coin", search_coin);
        //}

        if (!search_pr_type.equals("all")) {
            query += " AND A.pr_type = :search_pr_type";
            mapSqlParameterSource.addValue("search_pr_type", search_pr_type);
        }

        if (!search_content_type.equals("all")) {
            query += " AND A.content_type = :search_content_type";
            mapSqlParameterSource.addValue("search_content_type", search_content_type);
        }

        query += " group by A.no) C";

        return namedParameterJdbcTemplate.queryForObject(query, mapSqlParameterSource, Integer.class);
    }


    /**
     * 지급/회수 Log 리스트
     **/
    public List<PaymentRepayment> selectPaymentRepaymentList(String search_id, String search_sdate, String search_edate, String search_admin_id, String search_coin, String search_pr_type, String search_content_type, String sortName, String sortOrderBy, boolean excel, int start, int size) throws Exception {

        String query = "";

        // 엑셀다운로드일 때
        if (excel) {

            query += "/* Excel Download */ ";
        }

        query += "SELECT C.no, C.reg_dt, C.content_type, C.content, C.pr_type, C.coin_name, C.coin_quantity, C.complete_count, C.total_count, C.admin_id, C.mb_id " +
                "FROM (SELECT A.no, A.reg_dt, A.content_type, A.content, A.pr_type, A.coin_name, A.coin_quantity, A.complete_count, A.total_count, A.admin_id ,B.mb_id " +
                "FROM coinmarketing.admin_payment_repayment A JOIN coinmarketing.admin_payment_repayment_member_info B ON A.no = B.pr_no";


        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

        if (!search_sdate.equals("") && !search_edate.equals("")) {
            query += " AND A.reg_dt BETWEEN :search_sdate AND :search_edate";
            mapSqlParameterSource.addValue("search_sdate", search_sdate + " 00:00:00");
            mapSqlParameterSource.addValue("search_edate", search_edate + " 23:59:59");
        }

        // 서브 쿼리 조회
        if (!search_id.equals("")) {
            query += " AND B.mb_id LIKE CONCAT('%', :search_id, '%')";
            mapSqlParameterSource.addValue("search_id", search_id);
        }

        if (!search_admin_id.equals("")) {
            query += " AND A.admin_id LIKE CONCAT('%', :search_admin_id, '%')";
            mapSqlParameterSource.addValue("search_admin_id", search_admin_id);
        }

        //if (!search_coin.equals("all")) {
          //  query += " AND A.coin_name = :search_coin";
            //mapSqlParameterSource.addValue("search_coin", search_coin);
        //}

        if (!search_pr_type.equals("all")) {
            query += " AND A.pr_type = :search_pr_type";
            mapSqlParameterSource.addValue("search_pr_type", search_pr_type);
        }

        if (!search_content_type.equals("all")) {
            query += " AND A.content_type = :search_content_type";
            mapSqlParameterSource.addValue("search_content_type", search_content_type);
        }

        query += " GROUP BY A.no, B.mb_id) C";
        query += " ORDER BY C." + sortName + " " + sortOrderBy;

        if (!excel) {
            query += " LIMIT :start, :size";
            mapSqlParameterSource.addValue("start", start);
            mapSqlParameterSource.addValue("size", size);
        }

        return namedParameterJdbcTemplate.query(query, mapSqlParameterSource, new BeanPropertyRowMapper<>(PaymentRepayment.class));
    }


    /**
     * 지급/회수 Log By no
     **/
    public PaymentRepayment getPaymentRepaymentByNo(int no) throws Exception {

        String query = "SELECT no, reg_dt, content_type, content, pr_type, coin_name, coin_quantity, complete_count, total_count, admin_id FROM coinmarketing.admin_payment_repayment WHERE no = :no";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("no", no);

        return namedParameterJdbcTemplate.queryForObject(query, mapSqlParameterSource, new BeanPropertyRowMapper<>(PaymentRepayment.class));
    }


    /**
     * 전체 회원 리스트 개수
     **/
    public int getMemberCount() throws Exception {

        String query = "SELECT COUNT(mb_no) FROM coinmarketing.member";
        return namedParameterJdbcTemplate.queryForObject(query, new MapSqlParameterSource(), Integer.class);
    }


    /**
     * 전체 회원 리스트 (탈퇴회원 제외) 개수
     **/
    public int getNotDeleteMemberCount() throws Exception {

        String query = "SELECT COUNT(mb_no) FROM coinmarketing.member WHERE (mb_status = 'OK' OR mb_status = 'REST') AND mb_parent_id!=0";
        return namedParameterJdbcTemplate.queryForObject(query, new MapSqlParameterSource(), Integer.class);
    }


    /**
     * 전체 회원 ID 리스트(탈퇴회원 제외)
     */
    public List<String> getNotDeleteMemberIdList() throws Exception {
        String query = "SELECT mb_id FROM coinmarketing.member WHERE (mb_status = 'OK' OR mb_status = 'REST') AND mb_parent_id!=0";
        return namedParameterJdbcTemplate.queryForList(query, new MapSqlParameterSource(), String.class);
    }


    /**
     * 지급/회수 추가
     **/
    public int insertAdminPaymentRepayment(PaymentRepayment paymentRepayment) throws Exception {
        String query = "INSERT INTO coinmarketing.admin_payment_repayment (reg_dt, content_type, content, pr_type, coin_name, coin_quantity, complete_count, total_count, admin_id, is_payed) " +
                " VALUES (now(), :content_type, :content, :pr_type, :coin_name, :coin_quantity, :complete_count, :total_count, :admin_id, :is_payed)";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("content_type", paymentRepayment.getContent_type());
        mapSqlParameterSource.addValue("content", paymentRepayment.getContent());
        mapSqlParameterSource.addValue("pr_type", paymentRepayment.getPr_type());
        mapSqlParameterSource.addValue("coin_name", paymentRepayment.getCoin_name());
        mapSqlParameterSource.addValue("coin_quantity", paymentRepayment.getCoin_quantity());
        mapSqlParameterSource.addValue("complete_count", paymentRepayment.getComplete_count());
        mapSqlParameterSource.addValue("total_count", paymentRepayment.getTotal_count());
        mapSqlParameterSource.addValue("admin_id", paymentRepayment.getAdmin_id());
        mapSqlParameterSource.addValue("is_payed", paymentRepayment.getIs_payed());

        namedParameterJdbcTemplate.update(query, mapSqlParameterSource);
        return namedParameterJdbcTemplate.queryForObject("select @@identity", new MapSqlParameterSource(), Integer.class);
    }


    /**
     * 지급/회수 추가
     **/
    public int updateAdminPaymentRepayment(int pr_no) throws Exception {
        String query = "UPDATE coinmarketing.admin_payment_repayment SET is_payed='N' WHERE no=:pr_no";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("pr_no", pr_no);

        namedParameterJdbcTemplate.update(query, mapSqlParameterSource);
        return namedParameterJdbcTemplate.queryForObject("select @@identity", new MapSqlParameterSource(), Integer.class);
    }


    /**
     * @param paymentRepaymentMemberInfo PaymentRepaymentMemberInfo dto\n
     * @return int
     * @brief 지급/회수 상세보기 Log 추가\n
     * @details \n
     */
    public int insertAdminPaymentRepaymentMemberInfo(PaymentRepaymentMemberInfo paymentRepaymentMemberInfo) throws Exception {

        String query = "INSERT INTO coinmarketing.admin_payment_repayment_member_info (pr_no, state, mb_id, coin_quantity) VALUES (:pr_no, :state, :mb_id, :coin_quantity)";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("pr_no", paymentRepaymentMemberInfo.getPr_no());
        mapSqlParameterSource.addValue("state", paymentRepaymentMemberInfo.getState());
        mapSqlParameterSource.addValue("mb_id", paymentRepaymentMemberInfo.getMb_id());
        mapSqlParameterSource.addValue("coin_quantity", paymentRepaymentMemberInfo.getCoin_quantity());

        namedParameterJdbcTemplate.update(query, mapSqlParameterSource);

        return namedParameterJdbcTemplate.queryForObject("select @@identity", new MapSqlParameterSource(), Integer.class);
    }

    /**
     * @brief 지급/회수 상세보기 Log 리스트 개수\n
     * @details \n
     */
    public int countPaymentRepaymentMemberInfoList(int no) throws Exception {

        String query = "SELECT COUNT(no) FROM coinmarketing.admin_payment_repayment_member_info WHERE pr_no = :pr_no";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("pr_no", no);

        return namedParameterJdbcTemplate.queryForObject(query, mapSqlParameterSource, Integer.class);
    }

    /**
     * @brief 지급/회수 상세보기 Log 리스트\n
     * @details \n
     */
    public List<PaymentRepaymentMemberInfo> selectPaymentRepaymentMemberInfoList(int no, boolean excel, int start, int size) throws Exception {

        String query = "SELECT no, pr_no, state, content, mb_id FROM coinmarketing.admin_payment_repayment_member_info WHERE pr_no = :pr_no";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("pr_no", no);

        if (!excel) {
            if (!excel) {
                query += " LIMIT :start, :size";
                mapSqlParameterSource.addValue("start", start);
                mapSqlParameterSource.addValue("size", size);
            }
        }

        return namedParameterJdbcTemplate.query(query, mapSqlParameterSource, new BeanPropertyRowMapper<>(PaymentRepaymentMemberInfo.class));
    }


    /**
     * 블랙 컨슈머 리스트 갯수
     **/
    public int countBlackConsumerList(String search_state, String search_sdate, String search_edate, String search_id) throws Exception {

        String query = "SELECT COUNT(no) FROM coinmarketing.member_blackconsumer_history WHERE no > 0";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        if (!search_sdate.equals("") && !search_edate.equals("")) {

            query += " AND reg_dt BETWEEN :search_sdate AND :search_edate";
            parameterSource.addValue("search_sdate", search_sdate + " 00:00:00");
            parameterSource.addValue("search_edate", search_edate + " 23:59:59");
        }

        // 검색 답변상태가 전체가 아니면
        if (!search_state.equals("all")) {

            query += " AND state = :search_state";
            parameterSource.addValue("search_state", search_state);
        }

        // 검색 아이디가 있으면
        if (!search_id.equals("")) {

            query += " AND mb_id LIKE :search_id";
            parameterSource.addValue("search_id", "%" + search_id + "%");
        }

        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, Integer.class);
    }


    /**
     * 블랙 컨슈머 리스트
     **/
    public List<BlackConsumer> selectBlackConsumerList(String search_state, String search_sdate, String search_edate, String search_id, boolean excel, int start, int size) throws Exception {

        String query = "";

        // 엑셀다운로드일 때
        if (excel) {

            query += "/* Excel Download */ ";
        }

        query += "SELECT MY.*, M.mb_no "+
                " FROM coinmarketing.member_blackconsumer_history AS MY "+
                " LEFT JOIN coinmarketing.member AS M ON MY.mb_id = M.mb_id "+
                " WHERE no > 0 ";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        if (!search_sdate.equals("") && !search_edate.equals("")) {

            query += " AND reg_dt BETWEEN :search_sdate AND :search_edate";
            parameterSource.addValue("search_sdate", search_sdate + " 00:00:00");
            parameterSource.addValue("search_edate", search_edate + " 23:59:59");
        }

        // 검색 답변상태가 전체가 아니면
        if (!search_state.equals("all")) {

            query += " AND state = :search_state";
            parameterSource.addValue("search_state", search_state);
        }

        // 검색 아이디가 있으면
        if (!search_id.equals("")) {

            query += " AND M.mb_id LIKE :search_id";
            parameterSource.addValue("search_id", "%" + search_id + "%");
        }

        if (!excel) {

            query += " ORDER BY no desc LIMIT :start, :size";
            parameterSource.addValue("start", start);
            parameterSource.addValue("size", size);
        }

        return namedParameterJdbcTemplate.query(query, parameterSource, new BeanPropertyRowMapper<>(BlackConsumer.class));
    }


    /**
     * 블랙 컨슈머 추가
     **/
    public int insertBlackConsumer(BlackConsumer blackConsumer) throws Exception {
        String query = "INSERT INTO coinmarketing.member_blackconsumer_history (mb_id, state, type, type_memo, mb_blk_type, reg_dt, end_dt, reg_id) " +
                " VALUES (:mb_id, :state, :type, :type_memo, :mb_blk_type, now(), :end_dt, :reg_id)";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("mb_id", blackConsumer.getMb_id());
        mapSqlParameterSource.addValue("state", blackConsumer.getState());
        mapSqlParameterSource.addValue("type", blackConsumer.getType());
        mapSqlParameterSource.addValue("type_memo", blackConsumer.getType_memo());
        mapSqlParameterSource.addValue("mb_blk_type", blackConsumer.getMb_blk_type());
        mapSqlParameterSource.addValue("end_dt", blackConsumer.getEnd_dt());
        mapSqlParameterSource.addValue("reg_id", blackConsumer.getReg_id());

        return namedParameterJdbcTemplate.update(query, mapSqlParameterSource);
    }


    /**
     * 블랙 컨슈머 추가/해제의 따른 member테이블 mb_status, mb_blk_type 변경
     **/
    public int modifyMemberIsBlackConsumer(String mb_status, BlackConsumer blackConsumer) throws Exception {
        String query = "UPDATE coinmarketing.member SET mb_status = :mb_status, mb_blk_type = :mb_blk_type, mb_status_update_dt = now() WHERE mb_id = :mb_id";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("mb_status", mb_status);
        mapSqlParameterSource.addValue("mb_blk_type", blackConsumer.getMemo() == null ? blackConsumer.getMb_blk_type() : "");
        mapSqlParameterSource.addValue("mb_id", blackConsumer.getMb_id());

        return namedParameterJdbcTemplate.update(query, mapSqlParameterSource);
    }


    /**
     * 블랙 컨슈머 아이디로 정보 가져오기
     **/
    public BlackConsumer getBlackConsumerById(String mb_id) throws Exception {
        String query = "SELECT * FROM coinmarketing.member_blackconsumer_history WHERE mb_id = :mb_id AND state = 0";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("mb_id", mb_id);

        return namedParameterJdbcTemplate.queryForObject(query, mapSqlParameterSource, new BeanPropertyRowMapper<>(BlackConsumer.class));
    }


    /**
     * 블랙 컨슈머 해제
     **/
    public int releaseBlackConsumer(BlackConsumer blackConsumer) throws Exception {
        String query = "UPDATE coinmarketing.member_blackconsumer_history SET state = :state, memo = :memo, rel_id = :rel_id WHERE no = :no";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("state", blackConsumer.getState());
        mapSqlParameterSource.addValue("memo", blackConsumer.getMemo());
        mapSqlParameterSource.addValue("rel_id", blackConsumer.getRel_id());
        mapSqlParameterSource.addValue("no", blackConsumer.getNo());

        return namedParameterJdbcTemplate.update(query, mapSqlParameterSource);
    }


    /**
     * 블랙 컨슈머 종료날짜 지난 리스트
     **/
    public List<BlackConsumer> getBlackConsumerListByEndDt(String today) throws Exception {
        String query = "SELECT * FROM coinmarketing.member_blackconsumer_history WHERE end_dt < :today AND state = 0";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("today", today);

        return namedParameterJdbcTemplate.query(query, parameterSource, new BeanPropertyRowMapper<>(BlackConsumer.class));
    }


    /**
     * 회원
     **/
    public List<MemberLoginHis> selectMemberLoginHisList(
            String search_sdate,
            String search_edate,
            String mb_id,
            String login_reg_ip,
            String login_yn,
            int start,
            int size) throws Exception {

        String query = "";
        query += "SELECT * FROM coinmarketing.member_login_his WHERE login_no > 0";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

        if (mb_id != null && mb_id.length() > 0) {
            query += " AND mb_id LIKE CONCAT(:mb_id, '%') ";
            mapSqlParameterSource.addValue("mb_id", mb_id);
        }

        if (login_reg_ip != null && login_reg_ip.length() > 0) {
            query += " AND login_reg_ip LIKE CONCAT(:login_reg_ip, '%') ";
            mapSqlParameterSource.addValue("login_reg_ip", login_reg_ip);
        }

        if ("Y".equals(login_yn)) {
            query += " AND oauth_result_code=0";
        } else if ("N".equals(login_yn)) {
            query += " AND oauth_result_code!=0 ";
        }

        if (!search_sdate.equals("") && !search_edate.equals("")) {

            query += " AND login_reg_dt BETWEEN :search_sdate AND :search_edate";
            mapSqlParameterSource.addValue("search_sdate", search_sdate + " 00:00:00");
            mapSqlParameterSource.addValue("search_edate", search_edate + " 23:59:59");
        }

        query += " ORDER BY login_no DESC LIMIT :start, :size";
        mapSqlParameterSource.addValue("start", start);
        mapSqlParameterSource.addValue("size", size);

        return namedParameterJdbcTemplate.query(query, mapSqlParameterSource, new BeanPropertyRowMapper<>(MemberLoginHis.class));
    }


    public int countMemberLoginHisList(
            String search_sdate,
            String search_edate,
            String mb_id,
            String login_reg_ip,
            String login_yn
    ) throws Exception {
        String query = "SELECT COUNT(login_no) FROM coinmarketing.member_login_his WHERE login_no > 0";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

        if (mb_id != null && mb_id.length() > 0) {
            query += " AND mb_id LIKE CONCAT(:mb_id, '%') ";
            mapSqlParameterSource.addValue("mb_id", mb_id);
        }

        if (login_reg_ip != null && login_reg_ip.length() > 0) {
            query += " AND login_reg_ip LIKE CONCAT(:login_reg_ip, '%') ";
            mapSqlParameterSource.addValue("login_reg_ip", login_reg_ip);
        }

        if ("Y".equals(login_yn)) {
            query += " AND oauth_result_code=0";
        } else if ("N".equals(login_yn)) {
            query += " AND oauth_result_code!=0 ";
        }

        if (!search_sdate.equals("") && !search_edate.equals("")) {

            query += " AND login_reg_dt BETWEEN :search_sdate AND :search_edate";
            mapSqlParameterSource.addValue("search_sdate", search_sdate + " 00:00:00");
            mapSqlParameterSource.addValue("search_edate", search_edate + " 23:59:59");
        }


        return namedParameterJdbcTemplate.queryForObject(query, mapSqlParameterSource, Integer.class);
    }


    public List<MemberDownTree> getMemberDownTree(String sort_info, int mb_position) {
        String query = "SELECT A.mb_position, COUNT(A.mb_no) AS mb_count, B.coin_name, SUM(B.avail) AS avail_total, A.mb_sort_info FROM coinmarketing.member AS A RIGHT JOIN coinmarketing.member_balance AS B ON A.mb_no=B.mb_no And B.coin_name='LST' WHERE A.mb_sort_info LIKE CONCAT(:sort_info,'%') AND A.mb_position>:mb_position  GROUP BY A.mb_position, B.coin_name";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("sort_info", sort_info);
        mapSqlParameterSource.addValue("mb_position", mb_position);

        return namedParameterJdbcTemplate.query(query, mapSqlParameterSource, new BeanPropertyRowMapper<>(MemberDownTree.class));
    }


    public List<MemberSubDownTree> getMemberSubDownTree(int mb_position, String sort_info, String coin_name) {
//        String query = "SELECT IF(A.mb_position=:mb_position,A.mb_id,NULL) AS mb_id, B.coin_name, COUNT(A.mb_id) AS mb_id_count, A.mb_position, SUM(B.avail) AS avail_total, LEFT(A.mb_sort_info,:mb_position*6) AS sort_info, A.mb_sort_info, IF(A.mb_position=:mb_position,B.avail,0) AS mb_id_avail FROM coinmarketing.member  AS A RIGHT JOIN coinmarketing.member_balance AS B ON A.mb_no=B.mb_no AND not A.mb_position<:mb_position And B.coin_name='LST' WHERE mb_sort_info LIKE CONCAT(:sort_info,'%') GROUP BY sort_info, coin_name";
        String query = "SELECT A.mb_id, COUNT(A.mb_id) AS mb_id_count, A.mb_rank, A.mb_position, SUM(B.avail) AS avail_total, LEFT(A.mb_sort_info,:sort_length) AS sort_info, B.avail as mb_id_avail FROM (select mb_no, mb_id, mb_rank, mb_position, mb_sort_info from coinmarketing.member WHERE mb_sort_info LIKE CONCAT(:sort_info,'%') order by mb_sort_info) AS A JOIN (select mb_no, avail from coinmarketing.member_balance where coin_name=:coin_name) AS B ON A.mb_no=B.mb_no and mb_position>:mb_position GROUP BY sort_info;";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("mb_position", mb_position);
        mapSqlParameterSource.addValue("sort_length", (mb_position+1)*6);
        mapSqlParameterSource.addValue("sort_info", sort_info);
        mapSqlParameterSource.addValue("coin_name", coin_name);
        System.out.println(sort_info + " /// " + mb_position);

        return namedParameterJdbcTemplate.query(query, mapSqlParameterSource, new BeanPropertyRowMapper<>(MemberSubDownTree.class));
    }


    public List<WebMemberBalance> getMemberTreeExcel(String sort_info) {
        String query = "SELECT A.mb_no, A.mb_parent_id, A.mb_id, B.coin_name, B.avail, A.basic_pay, A.sponsor_pay, A.encourage_pay, A.rank_pay, A.apply_pay, A.mb_position FROM coinmarketing.member AS A RIGHT JOIN coinmarketing.member_balance AS B ON A.mb_no=B.mb_no And B.coin_name='LST' WHERE A.mb_sort_info LIKE CONCAT(:sort_info,'%') ORDER BY A.mb_sort_info";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("sort_info", sort_info);

        return namedParameterJdbcTemplate.query(query, mapSqlParameterSource, new BeanPropertyRowMapper<>(WebMemberBalance.class));
    }


    public List<WebMemberBalance> getMemberTreeView(String sort_info, int pdata) {
        String query = "SELECT A.mb_no, A.mb_parent_id, A.mb_id, B.coin_name, B.avail, A.basic_pay, A.sponsor_pay, A.encourage_pay, A.rank_pay, A.apply_pay, A.mb_position FROM coinmarketing.member AS A LEFT JOIN coinmarketing.member_balance AS B ON A.mb_no=B.mb_no And B.coin_name='LST' AND not A.mb_position<:pdata WHERE A.mb_sort_info LIKE CONCAT(:sort_info,'%') ORDER BY A.mb_sort_info";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("sort_info", sort_info);
        mapSqlParameterSource.addValue("pdata", pdata);

        return namedParameterJdbcTemplate.query(query, mapSqlParameterSource, new BeanPropertyRowMapper<>(WebMemberBalance.class));
    }


    public MemberEmailAuth getMemberEmailAuth(String mb_id, int auth_type, int minuteValue) {
        try {
            String sQuery = "SELECT auth_key, auth_type, ma_idx_user, email_state, send_date, email_address, mb_pwd FROM coinmarketing.member_email_auth where auth_type=:auth_type AND mb_id=:mb_id AND TIMESTAMPDIFF(MINUTE, send_date, CURRENT_TIMESTAMP())<=:minuteValue ORDER BY send_date DESC LIMIT 1";

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("mb_id", mb_id);
            mapSqlParameterSource.addValue("auth_type", auth_type);
            mapSqlParameterSource.addValue("minuteValue", minuteValue);

            return namedParameterJdbcTemplate.queryForObject(sQuery, mapSqlParameterSource, new BeanPropertyRowMapper<>(MemberEmailAuth.class));
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }


    public int setMemberEmailAuth(String auth_key, int email_state) {
        String sQuery = "UPDATE coinmarketing.member_email_auth SET email_state = :email_state WHERE auth_key = :auth_key";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("auth_key", auth_key);
        mapSqlParameterSource.addValue("email_state", email_state);

        return namedParameterJdbcTemplate.update(sQuery, mapSqlParameterSource);
    }


    /**
     * 전일 신규회원
     **/
    public int getYesterDayResistMemberCount(Date date) {
        try {
            String query = "SELECT count(*) FROM coinmarketing.member WHERE mb_reg_dt >= :yesterday AND mb_reg_dt < :today";
            String today = Utils.GetDateStringFromFormat(Utils.GetDateAddDays(date, 0), "yyyy.MM.dd");
            String yesterday = Utils.GetDateStringFromFormat(Utils.GetDateAddDays(date, -1), "yyyy.MM.dd");
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("today", today);
            mapSqlParameterSource.addValue("yesterday", yesterday);

            return namedParameterJdbcTemplate.queryForObject(query, mapSqlParameterSource, Integer.class);
        }catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }


    /**
     * 금일 신규회원
     **/
    public int getTodayResistMemberCount(Date date) {
        try {
            String query = "SELECT count(*) FROM coinmarketing.member WHERE mb_reg_dt >= :today";
            String today = Utils.GetDateStringFromFormat(Utils.GetDateAddDays(date, 0), "yyyy.MM.dd");
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("today", today);

            return namedParameterJdbcTemplate.queryForObject(query, mapSqlParameterSource, Integer.class);
        }catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }


    public MonitoringResponse getMemberRankCount() {
        String query = "SELECT COUNT(1) AS totalUsers, COUNT(IF(mb_rank='S0', 1, NULL)) AS S0User, COUNT(IF(mb_rank='S1', 1, NULL)) AS S1User, COUNT(IF(mb_rank='S2', 1, NULL)) AS S2User, COUNT(IF(mb_rank='S3', 1, NULL)) AS S3User, COUNT(IF(mb_rank='S4', 1, NULL)) AS S4User, COUNT(IF(mb_rank='S5', 1, NULL)) AS S5User FROM coinmarketing.member where mb_rank!=''";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

        return namedParameterJdbcTemplate.queryForObject(query, mapSqlParameterSource, new BeanPropertyRowMapper<>(MonitoringResponse.class));
    }


    public MonitoringResponse getNotConfirmWithdrawInfo() {
        String query = "SELECT COUNT(*) AS totalNotWithdrawCount, COALESCE(SUM(od_request_coin),0) AS totalNotWithdrawAmount FROM coinmarketing.member_request_order WHERE ACTION='withdraw' AND STATUS='REQ'";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        return namedParameterJdbcTemplate.queryForObject(query, mapSqlParameterSource, new BeanPropertyRowMapper<>(MonitoringResponse.class));
    }


    public List<CoinInvest> getTotalAvailCoin() {
        String query = "SELECT SUM(avail) AS wallet, coin_name AS coinName FROM coinmarketing.member_balance WHERE coin_name !='LST' GROUP BY coin_name";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        return namedParameterJdbcTemplate.query(query, mapSqlParameterSource, new BeanPropertyRowMapper<>(CoinInvest.class));
    }


    /**
     * 회원 이메일 변경
     **/
    public void updateMemberEmail(int mb_no, String mb_email) throws Exception {
        String sQuery = "UPDATE coinmarketing.member SET mb_email=:mb_email WHERE mb_no=:mb_no";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("mb_no", mb_no);
        mapSqlParameterSource.addValue("mb_email", mb_email);

        namedParameterJdbcTemplate.update(sQuery, mapSqlParameterSource);
    }


    /**
     * 회원 인증메일 등록
     **/
    public void insertAuthEmail(AuthEmailMember authEmailMember) throws Exception {
        String query = "INSERT INTO coinmarketing.member_email_auth (auth_key, auth_type, ma_idx_user, email_state, send_date, mb_id, email_address, mb_pwd, securitypin, od_id) " +
                " VALUES (:auth_key, :auth_type, :ma_idx_user, :email_state, now(), :mb_id, :email_address, :mb_pwd, :securitypin, :od_id)";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("auth_key", authEmailMember.getAuthKey());
        mapSqlParameterSource.addValue("auth_type", authEmailMember.getAuthType());
        mapSqlParameterSource.addValue("ma_idx_user", authEmailMember.getMa_idx_user());
        mapSqlParameterSource.addValue("email_state", authEmailMember.getEmailState());
        mapSqlParameterSource.addValue("mb_id", authEmailMember.getMb_id());
        mapSqlParameterSource.addValue("email_address", authEmailMember.getEAddress());
        mapSqlParameterSource.addValue("mb_pwd", authEmailMember.getPassword());
        mapSqlParameterSource.addValue("securitypin", authEmailMember.getSecuritypin());
        mapSqlParameterSource.addValue("od_id", authEmailMember.getOd_id());

        namedParameterJdbcTemplate.update(query, mapSqlParameterSource);
    }


    /**
     * 추천인 ID 변경 - parent_id update
     **/
    public void updateReferrerId_ParendId(ReferrerIdUpdate referrerIdUpdate) throws Exception {
        String sQuery = "UPDATE coinmarketing.member SET mb_parent_id=:mb_parent_id WHERE mb_id=:mb_id";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("mb_id", referrerIdUpdate.getMb_id());
        mapSqlParameterSource.addValue("mb_parent_id", referrerIdUpdate.getMb_parent_id());

        namedParameterJdbcTemplate.update(sQuery, mapSqlParameterSource);
    }


    /**
     * 추천인 ID 변경 - position update
     **/
    public void updateReferrerId_Position(ReferrerIdUpdate referrerIdUpdate) throws Exception {
        String sQuery = "UPDATE coinmarketing.member SET mb_position=mb_position+(:position_change) WHERE mb_sort_info like CONCAT(:mb_sort_info, '%') ";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("mb_sort_info", referrerIdUpdate.getMb_sort_info_self());
        mapSqlParameterSource.addValue("position_change", (referrerIdUpdate.getPosition_parent()+1-referrerIdUpdate.getPosition_self()));

        namedParameterJdbcTemplate.update(sQuery, mapSqlParameterSource);
    }


    /**
     * 추천인 ID 변경 - mb_sort_info update
     **/
    public void updateReferrerId_MbSortInfo(ReferrerIdUpdate referrerIdUpdate) throws Exception {
        String sQuery = "UPDATE coinmarketing.member SET mb_sort_info=replace(mb_sort_info, :mb_sort_info_target, :mb_sort_info_change) WHERE mb_sort_info like CONCAT(:mb_sort_info, '%')";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("mb_sort_info_target", referrerIdUpdate.getMb_sort_info_target());
        mapSqlParameterSource.addValue("mb_sort_info_change", referrerIdUpdate.getMb_sort_info_change());
        mapSqlParameterSource.addValue("mb_sort_info", referrerIdUpdate.getMb_sort_info_self());

        namedParameterJdbcTemplate.update(sQuery, mapSqlParameterSource);
    }




    /**
     * 후원 조직도 값 
     **/
    public WebMemberMainSponsor getWebMemeberMainSponser(String mb_id, String flag) {
        try {
            String sql = "CALL coinmarketing.sp_mainSponsor(:mb_id, :flag);";

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("mb_id", mb_id);
            mapSqlParameterSource.addValue("flag", flag);
            return namedParameterJdbcTemplate.queryForObject(
                    sql,
                    mapSqlParameterSource,
                    new BeanPropertyRowMapper<>(WebMemberMainSponsor.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 후원 조직도 값 
     **/
    public WebMemberMainReferer getWebMemeberMainReferer(String mb_no) {
        try {
            String sql = "CALL coinmarketing.sp_mainReferer(:mb_no);";

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("mb_no", mb_no);
            return namedParameterJdbcTemplate.queryForObject(
                    sql,
                    mapSqlParameterSource,
                    new BeanPropertyRowMapper<>(WebMemberMainReferer.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 패스워드 업데이트
     **/
    public int updatePassword(String mbPwd, String mbKey, int mbNo) {
        String query = "UPDATE coinmarketing.member SET mb_reset_pw = 'Y', mb_pwd = :mbPwd, mb_key =:mbKey, mb_up_dt = now() WHERE mb_no = :mbNo";    

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("mbPwd", mbPwd);
        mapSqlParameterSource.addValue("mbKey", mbKey);
        mapSqlParameterSource.addValue("mbNo", mbNo);       

        return namedParameterJdbcTemplate.update(query, mapSqlParameterSource);
    }
}