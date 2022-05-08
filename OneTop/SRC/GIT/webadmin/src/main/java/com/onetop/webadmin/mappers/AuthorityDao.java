package com.onetop.webadmin.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.onetop.webadmin.models.authority.AccessRole;
import com.onetop.webadmin.models.authority.AdminLog;
import com.onetop.webadmin.models.authority.AdminMember;

@Repository
public class AuthorityDao {

    @Autowired
    @Qualifier("namedParameterWebAdminJdbcTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * @brief    	 접근권한 관리 추가\n
     * @details	 \n
     * @param       adminMember    접근권한 관리 dto\n
     * @return      int
     */
    public int insertAdminMember(AdminMember adminMember) {

        System.out.println(adminMember);

        String query = "INSERT INTO coinmarketing.admin_member (id, password, is_password_init, name, phone_number, role, otp_key, otp_reg_dt, reg_dt, mod_dt, security_proposal) " +
                "VALUES (:id, :password, :is_password_init, :name, :phone_number, :role, :otp_key, now(), now(), now(), :security_proposal)";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("id", adminMember.getId());
        parameterSource.addValue("password", adminMember.getPassword());
        parameterSource.addValue("is_password_init", adminMember.getIs_password_init());
        parameterSource.addValue("name", adminMember.getName());
        parameterSource.addValue("phone_number", adminMember.getPhone_number());
        parameterSource.addValue("role", adminMember.getRole());
        parameterSource.addValue("otp_key", adminMember.getOtp_key());
        parameterSource.addValue("security_proposal", adminMember.getSecurity_proposal());

        // otp가 선택되지 않았으면
        if (adminMember.getOtp_key().equals("")) {

            query = "INSERT INTO coinmarketing.admin_member (id, password, is_password_init, name, phone_number, role, reg_dt, mod_dt, security_proposal) " +
                    "VALUES (:id, :password, :is_password_init, :name, :phone_number, :role, now(), now(), :security_proposal)";

            parameterSource = new MapSqlParameterSource();
            parameterSource.addValue("id", adminMember.getId());
            parameterSource.addValue("password", adminMember.getPassword());
            parameterSource.addValue("is_password_init", adminMember.getIs_password_init());
            parameterSource.addValue("name", adminMember.getName());
            parameterSource.addValue("phone_number", adminMember.getPhone_number());
            parameterSource.addValue("role", adminMember.getRole());
            parameterSource.addValue("security_proposal", adminMember.getSecurity_proposal());
        }

        namedParameterJdbcTemplate.update(query, parameterSource);
        return namedParameterJdbcTemplate.queryForObject("select @@identity", new MapSqlParameterSource(), Integer.class);
    }

    /**
     * @brief    	 접근권한 관리 변경\n
     * @details	 \n
     * @param       adminMember    접근권한 관리 dto\n
     * @return      int
     */
    public int modifyAdminMember(AdminMember adminMember) {

        String query = "";
        if (adminMember.getPassword().equals("")) {
            query = "UPDATE coinmarketing.admin_member SET name = :name, password = :password, phone_number = :phone_number, role = :role, otp_key = null, otp_reg_dt = null, mod_dt = now() WHERE no = :no";
        } else {
            query = "UPDATE coinmarketing.admin_member SET name = :name, password = :password, phone_number = :phone_number, role = :role, otp_key = null, otp_reg_dt = null, mod_dt = now(), is_password_init = 'N' WHERE no = :no";
        }

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("name", adminMember.getName());
        parameterSource.addValue("password", adminMember.getPassword());
        parameterSource.addValue("phone_number", adminMember.getPhone_number());
        parameterSource.addValue("role", adminMember.getRole());
        parameterSource.addValue("no", adminMember.getNo());

        return namedParameterJdbcTemplate.update(query, parameterSource);
    }

    /**
     * @brief       접근권한 관리 삭제\n
     * @details	 \n
     * @param       no    접근권한 관리 번호\n
     * @return      int
     */
    public int deleteAdminMember(String no) {

        String query = "DELETE FROM coinmarketing.admin_member WHERE no = :no";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("no", no);

        return namedParameterJdbcTemplate.update(query, parameterSource);
    }

    /**
     * @brief       접근권한 관리 리스트\n
     * @details	 \n
     * @param       search_type       검색타입\n
     * @param       search_content    검색내용\n
     * @param       start             페이징 시작값\n
     * @param       size              페이징 표시값\n
     * @return      AdminMember
     */
    public List<AdminMember> selectAdminMemberList(String search_type, String search_content, int start, int size) throws Exception {

        String query = "SELECT * FROM coinmarketing.admin_member";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        // 검색내용이 존재하면
        if (!search_content.equals("")) {

            if (search_type.equals("all")) {

                query += " WHERE id LIKE :id or name LIKE :name or phone_number LIKE :phone_number";
                parameterSource.addValue("id", "%"+search_content+"%");
                parameterSource.addValue("name", "%"+search_content+"%");
                parameterSource.addValue("phone_number", "%"+search_content+"%");
            } else {

                query += " WHERE " + search_type + " LIKE :search_content";
                parameterSource.addValue("search_content", "%"+search_content+"%");
            }
        }

        query += " ORDER BY mod_dt desc LIMIT :start, :size";
        parameterSource.addValue("start", start);
        parameterSource.addValue("size", size);

        return namedParameterJdbcTemplate.query(query, parameterSource, new BeanPropertyRowMapper<AdminMember>(AdminMember.class));
    }

    /**
     * @brief       전체 접근권한 관리 리스트\n
     * @details	 \n
     * @return      AdminMember
     */
    public List<AdminMember> selectAllAdminMemberList() throws Exception {

        String query = "SELECT * FROM coinmarketing.admin_member ORDER BY mod_dt desc";

        return namedParameterJdbcTemplate.query(query, new BeanPropertyRowMapper<AdminMember>(AdminMember.class));
    }

    /**
     * @brief       접근권한 관리 리스트 갯수\n
     * @details	 \n
     * @param       search_type       검색타입\n
     * @param       search_content    검색내용\n
     * @return      int
     */
    public int countAdminMemberList(String search_type, String search_content) throws Exception {

        String query = "SELECT COUNT(no) FROM coinmarketing.admin_member";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        // 검색내용이 존재하면
        if (!search_content.equals("")) {

            if (search_type.equals("all")) {

                query += " WHERE id LIKE :id or name LIKE :name or phone_number LIKE :phone_number";
                parameterSource.addValue("id", "%"+search_content+"%");
                parameterSource.addValue("name", "%"+search_content+"%");
                parameterSource.addValue("phone_number", "%"+search_content+"%");
            } else {

                query += " WHERE " + search_type + " LIKE :search_content";
                parameterSource.addValue("search_content", "%"+search_content+"%");
            }
        }

        query += " ORDER BY mod_dt desc";

        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, Integer.class);
    }

    /**
     * @brief       접근권한 관리 가져오기 by no\n
     * @details	 \n
     * @param       no    접근권한 관리 번호\n
     * @return      AdminMember
     */
    public AdminMember getAdminMemberByNo(int no) {

        String query = "SELECT * FROM coinmarketing.admin_member WHERE no = :no";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("no", no);

        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, new BeanPropertyRowMapper<AdminMember>(AdminMember.class));
    }

    /**
     * @brief       접근권한 관리 가져오기 by id\n
     * @details	 \n
     * @param       id    접근권한 관리 아이디\n
     * @return      AdminMember
     */
    public AdminMember getAdminMemberById(String id) {

        String query = "SELECT * FROM coinmarketing.admin_member WHERE id = :id";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("id", id);

        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, new BeanPropertyRowMapper<AdminMember>(AdminMember.class));
    }

    /**
     * @brief       접근권한 관리 ID 체크\n
     * @details	 \n
     * @param       id    접근권한 관리 아이디\n
     * @return      int
     */
    public int checkId(String id) {

        String query = "SELECT count(no) FROM coinmarketing.admin_member WHERE id = :id";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("id", id);

        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, Integer.class);
    }

    /**
     * @brief       권한정보 가져오기\n
     * @details	 \n
     * @return      List
     */
    public List<AccessRole> accessRoleList() {

        String query = "SELECT A.role, A.desc FROM coinmarketing.access_role A";
        return namedParameterJdbcTemplate.query(query, new BeanPropertyRowMapper<AccessRole>(AccessRole.class));
    }


    /**
     * @brief    	 관리자 Log 추가\n
     * @details	 \n
     * @param       adminLog    관리자 log dto\n
     * @return      int
     */
    public int insertAdminLog(AdminLog adminLog) {

        String query = "INSERT INTO coinmarketing.admin_log (type, content, id, ip, reg_dt) VALUES (:type, :content, :id, :ip, now())";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("type", adminLog.getType());
        parameterSource.addValue("content", adminLog.getContent());
        parameterSource.addValue("id", adminLog.getId());
        parameterSource.addValue("ip", adminLog.getIp());

        return namedParameterJdbcTemplate.update(query, parameterSource);
    }

    /**
     * @brief       관리자 Log 갯수\n
     * @details	 \n
     * @param       search_type     검색타입\n
     * @param       search_sdate    검색시작일\n
     * @param       search_edate    검색종료일\n
     * @param       search_id       검색아이디\n
     * @param       search_ip       검색아이피\n
     * @return      int
     */
    public int countAdminLogList(String search_type, String search_sdate, String search_edate, String search_id, String search_ip) throws Exception {

        String query = "SELECT COUNT(no) FROM coinmarketing.admin_log WHERE no > 0";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        if (!search_sdate.equals("") && !search_edate.equals("")) {

            query += " AND reg_dt BETWEEN :search_sdate AND :search_edate";
            parameterSource.addValue("search_sdate", search_sdate + " 00:00:00");
            parameterSource.addValue("search_edate", search_edate + " 23:59:59");
        }

        // 검색 작업이 전체가 아니면
        if (!search_type.equals("all")) {

            if (search_type.equals("1")) query += " AND type not in (1,2)";
            else if (search_type.equals("2")) query += " AND type in (1,2)";
        }

        // 검색 아이디가 있으면
        if (!search_id.equals("")) {

            query += " AND id = :search_id";
            parameterSource.addValue("search_id", search_id);
        }

        // 검색 IP가 있으면
        if (!search_ip.equals("")) {

            query += " AND ip = :search_ip";
            parameterSource.addValue("search_ip", search_ip);
        }

        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, Integer.class);
    }

    /**
     * @brief       관리자 Log 리스트\n
     * @details	 \n
     * @param       search_type     검색타입\n
     * @param       search_sdate    검색시작일\n
     * @param       search_edate    검색종료일\n
     * @param       search_id       검색아이디\n
     * @param       search_ip       검색아이피\n
     * @param       start           페이징시작값\n
     * @param       size            페이징표시값\n
     * @return      List
     */
    public List<AdminLog> selectAdminLogList(String search_type, String search_sdate, String search_edate, String search_id, String search_ip, boolean excel, int start, int size) throws Exception {

        String query = "";

        // 엑셀다운로드일 때
        if (excel) {

            query += "/* Excel Download */ ";
        }

        query += "SELECT no, type, content, id, ip, reg_dt FROM coinmarketing.admin_log WHERE no > 0";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        if (!search_sdate.equals("") && !search_edate.equals("")) {

            query += " AND reg_dt BETWEEN :search_sdate AND :search_edate";
            parameterSource.addValue("search_sdate", search_sdate + " 00:00:00");
            parameterSource.addValue("search_edate", search_edate + " 23:59:59");
        }

        // 검색 작업이 전체가 아니면
        if (!search_type.equals("all")) {

            if (search_type.equals("1")) query += " AND type not in (1,2)";
            else if (search_type.equals("2")) query += " AND type in (1,2)";
        }

        // 검색 아이디가 있으면
        if (!search_id.equals("")) {

            query += " AND id = :search_id";
            parameterSource.addValue("search_id", search_id);
        }

        // 검색 IP가 있으면
        if (!search_ip.equals("")) {

            query += " AND ip = :search_ip";
            parameterSource.addValue("search_ip", search_ip);
        }

        query += " ORDER BY no desc";

        if (!excel) {

            query += " LIMIT :start, :size";
            parameterSource.addValue("start", start);
            parameterSource.addValue("size", size);
        }

        return namedParameterJdbcTemplate.query(query, parameterSource, new BeanPropertyRowMapper<AdminLog>(AdminLog.class));
    }

    /**
     * @brief       시스템 변경 Log 갯수\n
     * @details	 \n
     * @param       search_type     검색타입\n
     * @param       search_sdate    검색시작일\n
     * @param       search_edate    검색종료일\n
     * @param       search_id       검색아이디\n
     * @return      int
     */
    public int countSystemLogList(String search_type, String search_sdate, String search_edate, String search_id) throws Exception {

        String query = "SELECT COUNT(no) FROM coinmarketing.admin_log WHERE type IN (211,221,231,241,232,271,272,273,274)";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        if (!search_sdate.equals("") && !search_edate.equals("")) {

            query += " AND reg_dt BETWEEN :search_sdate AND :search_edate";
            parameterSource.addValue("search_sdate", search_sdate + " 00:00:00");
            parameterSource.addValue("search_edate", search_edate + " 23:59:59");
        }

        // 검색 구분이 전체가 아니면
        if (!search_type.equals("all")) {

            query += " AND type = :search_type";
            parameterSource.addValue("search_type", search_type);
        }

        // 검색 아이디가 있으면
        if (!search_id.equals("")) {

            query += " AND id LIKE :search_id";
            parameterSource.addValue("search_id", "%" + search_id + "%");
        }

        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, Integer.class);
    }

    /**
     * @brief       시스템 변경 Log 리스트\n
     * @details	 \n
     * @param       search_type     검색타입\n
     * @param       search_sdate    검색시작일\n
     * @param       search_edate    검색종료일\n
     * @param       search_id       검색아이디\n
     * @param       start           페이징시작값\n
     * @param       size            페이징표시값\n
     * @return      List
     */
    public List<AdminLog> selectSystemLogList(String search_type, String search_sdate, String search_edate, String search_id, boolean excel, int start, int size) throws Exception {

        String query = "";

        // 엑셀다운로드일 때
        if (excel) {

            query += "/* Excel Download */ ";
        }

        query += "SELECT no, type, content, id, ip, reg_dt FROM coinmarketing.admin_log WHERE type in (211,221,231,241,232,271,272,273,274)";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        if (!search_sdate.equals("") && !search_edate.equals("")) {

            query += " AND reg_dt BETWEEN :search_sdate AND :search_edate";
            parameterSource.addValue("search_sdate", search_sdate + " 00:00:00");
            parameterSource.addValue("search_edate", search_edate + " 23:59:59");
        }

        // 검색 구분이 전체가 아니면
        if (!search_type.equals("all")) {

            query += " AND type = :search_type";
            parameterSource.addValue("search_type", search_type);
        }

        // 검색 아이디가 있으면
        if (!search_id.equals("")) {

            query += " AND id LIKE :search_id";
            parameterSource.addValue("search_id", "%" + search_id + "%");
        }

        query += " ORDER BY no desc";

        if (!excel) {

            query += " LIMIT :start, :size";
            parameterSource.addValue("start", start);
            parameterSource.addValue("size", size);
        }

        return namedParameterJdbcTemplate.query(query, parameterSource, new BeanPropertyRowMapper<AdminLog>(AdminLog.class));
    }


    public int changeAuthAdmin(String id, String security_proposal) {

        String query = "UPDATE coinmarketing.admin_member SET security_proposal = :security_proposal WHERE id = :id";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("security_proposal", security_proposal);
        parameterSource.addValue("id", id);

        return namedParameterJdbcTemplate.update(query, parameterSource);
    }
}