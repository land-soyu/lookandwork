package com.onetop.webadmin.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.onetop.webadmin.models.board.Notice;
import com.onetop.webadmin.models.board.Qna;
import com.onetop.webadmin.models.board.QnaComment;

@Repository
public class BoardDao {

    @Autowired
    @Qualifier("namedParameterWebAdminJdbcTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * @brief      공지사항 등록\n
     * @details    \n
     * @param      notice    공지사항 dto\n
     * @return     int
     */
    public int insertNotice(Notice notice) throws Exception {

        String query = "INSERT INTO bbs.notice (title, admin_id, admin_name, is_secret, content ,lang, reg_dt, mod_dt, type) VALUES (:title,:admin_id,:admin_name,:is_secret,:content,:lang,now(),now(),:type)";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("title", notice.getTitle());
        parameterSource.addValue("admin_id", notice.getAdmin_id());
        parameterSource.addValue("admin_name", notice.getAdmin_name());
        parameterSource.addValue("is_secret", notice.getIs_secret());
        parameterSource.addValue("content", notice.getContent());

        parameterSource.addValue("lang", notice.getLang());
        parameterSource.addValue("type", notice.getType());

        namedParameterJdbcTemplate.update(query, parameterSource);
        return namedParameterJdbcTemplate.queryForObject("SELECT @@identity", new MapSqlParameterSource(), Integer.class);
    }

    /**
     * @brief      공지사항 수정\n
     * @details    \n
     * @param      notice    공지사항 dto\n
     * @return     int
     */
    public int modifyNotice(Notice notice) throws Exception {

        String query = "UPDATE bbs.notice SET title = :title, admin_id = :admin_id , admin_name = :admin_name, is_secret = :is_secret , content = :content, lang = :lang, mod_dt = now() WHERE no = :no";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("title", notice.getTitle());
        parameterSource.addValue("admin_id", notice.getAdmin_id());
        parameterSource.addValue("admin_name", notice.getAdmin_name());
        parameterSource.addValue("is_secret", notice.getIs_secret());
        parameterSource.addValue("content", notice.getContent());
        parameterSource.addValue("lang", notice.getLang());
        parameterSource.addValue("no", notice.getNo());

        return namedParameterJdbcTemplate.update(query, parameterSource);
    }

    /**
     * @brief       공지사항 삭제\n
     * @details     \n
     * @param       no    공지사항 번호\n
     * @return      int
     */
    public int deleteNotice(String no) throws Exception {

        String query = "DELETE FROM bbs.notice WHERE no = :no";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("no", no);

        return namedParameterJdbcTemplate.update(query, parameterSource);
    }


    /**
     * 공지사항 메인 노출
     */
    public int noticeMainViewYN(String no, String yn) throws Exception {

        String query = "UPDATE bbs.notice SET main_view = :yn WHERE no = :no";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("no", no);
        parameterSource.addValue("yn", yn);

        return namedParameterJdbcTemplate.update(query, parameterSource);
    }


    /**
     * @brief      공지사항 리스트\n
     * @details    \n
     * @param      search_type       검색타입\n
     * @param      search_content    검색내용\n
     * @param      start             페이징시작값\n
     * @param      size              페이징표시값\n
     * @return     List
     */
    public List<Notice> selectNoticeList(String search_type, String search_content, String search_lang, int start, int size) throws Exception {

        String query = "SELECT no, title, reg_dt, mod_dt, admin_id, admin_name, is_secret, content, read_count, lang, main_view, type FROM bbs.notice WHERE no > 0";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        if (!search_type.equals("all")) {

            query += " AND is_secret = :search_type";
            parameterSource.addValue("search_type", search_type);
        }

        if (!search_content.equals("")) {

            query += " AND content LIKE :search_content";
            parameterSource.addValue("search_content", "%" + search_content + "%");
        }

        if (!search_lang.equals("all")) {

            query += " AND lang = :search_lang";
            parameterSource.addValue("search_lang", search_lang);
        }

        query += " ORDER BY no desc LIMIT :start, :size";
        parameterSource.addValue("start", start);
        parameterSource.addValue("size", size);

        return namedParameterJdbcTemplate.query(query, parameterSource, new BeanPropertyRowMapper<Notice>(Notice.class));
    }

    /**
     * @brief      공지사항 리스트 갯수\n
     * @details    \n
     * @param      search_type       검색타입\n
     * @param      search_content    검색내용\n
     * @return     int
     */
    public int countNoticeList(String search_type, String search_content, String search_lang) throws Exception {

        String query = "SELECT COUNT(no) FROM bbs.notice WHERE no > 0";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        if (!search_type.equals("all")) {

            query += " AND is_secret = :search_type";
            parameterSource.addValue("search_type", search_type);
        }

        if (!search_content.equals("")) {

            query += " AND content LIKE :search_content";
            parameterSource.addValue("search_content", "%" + search_content + "%");
        }

        if (!search_lang.equals("all")) {

            query += " AND lang = :search_lang";
            parameterSource.addValue("search_lang", search_lang);
        }

        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, Integer.class);
    }

    /**
     * @brief      공지사항 가져오기 by no\n
     * @details    \n
     * @param      no    공지사항 번호\n
     * @return     Notice
     */
    public Notice getNoticeByNo(int no) throws Exception {

        String query = "SELECT no, title, reg_dt, mod_dt, admin_id, admin_name, is_secret, content, read_count, lang, main_view, type FROM bbs.notice WHERE no = :no";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("no", no);

        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, new BeanPropertyRowMapper<Notice>(Notice.class));
    }


    /**
     * @brief      공지사항 마지막 index값\n
     * @details    \n
     */
    public int noticeLastIndex() throws Exception {

        String query = "SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = :table_schema AND TABLE_NAME = :table_name";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("table_schema", "bbs");
        parameterSource.addValue("table_name", "notice");

        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, Integer.class);
    }


    /**
     * @brief      문의/답변 리스트 갯수\n
     * @details    \n
     */
    public int countQnaList(String search_state, String search_sdate, String search_edate, String search_id, String search_name, int search_qna_type_first, int search_qna_type_second, String search_content) throws Exception {

        String query = "SELECT COUNT(no) FROM bbs.qna WHERE no > 0";

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

            query += " AND member_id LIKE :search_id";
            parameterSource.addValue("search_id", "%" + search_id + "%");
        }

        // 검색 이름이 있으면
        if (!search_name.equals("")) {

            query += " AND member_name LIKE :search_name";
            parameterSource.addValue("search_name", "%" + search_name + "%");
        }

        // 검색 문의구분 첫번째 구분 값이 전체가 아니면
        if (search_qna_type_first != 0) {

            if (search_qna_type_second != 0) {

                query += " AND qna_type = :search_qna_type_second";
                parameterSource.addValue("search_qna_type_second", search_qna_type_second);
            } else {

                if (search_qna_type_first == 1) {

                    query += " AND qna_type IN (1,2,3,4,5,100)";
                } else if (search_qna_type_first == 2) {

                    query += " AND qna_type IN (101,102,103,200)";
                }
            }
        }

        if (!search_content.equals("")) {

            query += " AND content LIKE :search_content";
            parameterSource.addValue("search_content", "%" + search_content + "%");
        }

        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, Integer.class);
    }

    /**
     * @brief      문의/답변 리스트\n
     * @details    \n
     */
    public List<Qna> selectQnaList(String search_state, String search_sdate, String search_edate, String search_id, String search_name, int search_qna_type_first, int search_qna_type_second, String search_content, int start, int size) throws Exception {

        String query = "SELECT no,title, reg_dt, member_id, content, state FROM bbs.qna WHERE no > 0";

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

            query += " AND member_id LIKE :search_id";
            parameterSource.addValue("search_id", "%" + search_id + "%");
        }

        // 검색 이름이 있으면
        if (!search_name.equals("")) {

            query += " AND member_name LIKE :search_name";
            parameterSource.addValue("search_name", "%" + search_name + "%");
        }

        // 검색 문의구분 첫번째 구분 값이 전체가 아니면
        if (search_qna_type_first != 0) {

            if (search_qna_type_second != 0) {

                query += " AND qna_type = :search_qna_type_second";
                parameterSource.addValue("search_qna_type_second", search_qna_type_second);
            } else {

                if (search_qna_type_first == 1) {

                    query += " AND qna_type IN (1,2,3,4,5,100)";
                } else if (search_qna_type_first == 2) {

                    query += " AND qna_type IN (101,102,103,200)";
                }
            }
        }

        if (!search_content.equals("")) {

            query += " AND content LIKE :search_content";
            parameterSource.addValue("search_content", "%" + search_content + "%");
        }

        query += " ORDER BY no desc LIMIT :start, :size";
        parameterSource.addValue("start", start);
        parameterSource.addValue("size", size);

        return namedParameterJdbcTemplate.query(query, parameterSource, new BeanPropertyRowMapper<Qna>(Qna.class));
    }

    /**
     * @brief      문의/답변 답변상태 수정 by no\n
     * @details    \n
     * @param      no    문의/답변 번호\n
     * @return     int
     */
    public int modifyQnaStateByNo(int no) throws Exception {

        String query = "UPDATE bbs.qna SET state = :state WHERE no = :no";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("state", "Y");
        parameterSource.addValue("no", no);

        return namedParameterJdbcTemplate.update(query, parameterSource);
    }

    /**
     * @brief      문의/답변 가져오기 by no\n
     * @details    \n
     * @param      no    문의/답변 번호\n
     * @return     Qna
     */
    public Qna getQnaByNo(int no) throws Exception {

        String query = "SELECT no, title, reg_dt, member_id, content, state FROM bbs.qna WHERE no = :no";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("no", no);

        return namedParameterJdbcTemplate.queryForObject(query, parameterSource, new BeanPropertyRowMapper<Qna>(Qna.class));
    }

    /**
     * @brief      문의/답변 답변 리스트\n
     * @details    \n
     * @param      no    문의/답변 번호\n
     * @return     List
     */
    public List<QnaComment> selectQnaCommentList(int no) throws Exception {

        String query = "SELECT no, qna_no, content, reg_dt, admin_id FROM bbs.qna_comment WHERE qna_no = :qna_no ORDER BY reg_dt DESC";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("qna_no", no);

        return namedParameterJdbcTemplate.query(query, parameterSource, new BeanPropertyRowMapper<QnaComment>(QnaComment.class));
    }

    /**
     * @brief      문의/답변 답변 등록\n
     * @details    \n
     * @param      qnaComment    문의/답변 답변 dto\n
     * @return     int
     */
    public int insertQnaComment(QnaComment qnaComment) throws Exception {

        String query = "INSERT INTO bbs.qna_comment (qna_no,admin_id,content,reg_dt) VALUES (:qna_no,:admin_id,:content,now())";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("qna_no", qnaComment.getQna_no());
        parameterSource.addValue("admin_id", qnaComment.getAdmin_id());
        parameterSource.addValue("content", qnaComment.getContent());

        return namedParameterJdbcTemplate.update(query, parameterSource);
    }
}