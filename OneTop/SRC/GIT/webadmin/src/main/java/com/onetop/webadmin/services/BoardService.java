package com.onetop.webadmin.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.onetop.webadmin.mappers.BoardDao;
import com.onetop.webadmin.models.board.Notice;
import com.onetop.webadmin.models.board.Qna;
import com.onetop.webadmin.models.board.QnaComment;
import com.onetop.webadmin.responses.PageNaviResponse;
import com.onetop.webadmin.responses.board.NoticeResponse;
import com.onetop.webadmin.responses.board.QnaResponse;

@Service
public class BoardService extends PageService {


    private static final Logger log = LoggerFactory.getLogger(BoardService.class);

    @Autowired
    private BoardDao boardDao;



    /**
     * 공지사항 리스트
     */
    public NoticeResponse noticeList(String search_type, String search_content, String search_lang, int page, int search_listCount) throws Exception {
        NoticeResponse noticeResponse = new NoticeResponse();

        try{
            int total_count = boardDao.countNoticeList(search_type, search_content, search_lang);
            noticeResponse.setTotal_notice_count(total_count);

            PageNaviResponse pageNaviResponse = getPageNavi(total_count, page, search_listCount);
            noticeResponse.setPageNaviResponse(pageNaviResponse);

            int startIndex = (page - 1) * search_listCount;

            List<Notice> noticeList = boardDao.selectNoticeList(search_type, search_content, search_lang, startIndex, search_listCount);
            noticeResponse.setNoticeList(noticeList);
        } catch (Exception e) {
            log.error("Fail to get noticeList, msg = " + e.getMessage());
        }
        return noticeResponse;
    }


    /**
     * 공지사항 가져오기 by no
     */
    public Notice getNoticeByNo(int no) throws Exception {

        return boardDao.getNoticeByNo(no);
    }


    /**
     * 공지사항 등록
     */
    public int insertNotice(Notice notice) throws Exception {
        return boardDao.insertNotice(notice);
    }


    /**
     * 공지사항 수정
     */
    public int modifyNotice(Notice notice) throws Exception {

        return boardDao.modifyNotice(notice);
    }


    /**
     * 공지사항 삭제
     */
    public int deleteNotice(String no) throws Exception {
        return boardDao.deleteNotice(no);
    }


    /**
     * 공지사항 메인 노출
     */
    public int noticeMainViewYN(String no, String yn) throws Exception {
        return boardDao.noticeMainViewYN(no, yn);
    }


    /**
     * 공지사항 마지막 index값
     */
    public int noticeLastIndex() throws Exception {
        return boardDao.noticeLastIndex();
    }


    /**
     * 문의/답변 리스트
     */
    public QnaResponse qnaList(String search_state, String search_sdate, String search_edate, String search_id, String search_name, int search_qna_type_first, int search_qna_type_second, String search_content, int page, int search_listCount) throws Exception {

        QnaResponse qnaResponse = new QnaResponse();

        try{

            int total_count = boardDao.countQnaList(search_state, search_sdate, search_edate, search_id, search_name, search_qna_type_first, search_qna_type_second, search_content);
            qnaResponse.setTotal_qna_count(total_count);

            PageNaviResponse pageNaviResponse = getPageNavi(total_count, page, search_listCount);
            qnaResponse.setPageNaviResponse(pageNaviResponse);

            int startIndex = (page - 1) * search_listCount;

            List<Qna> qnaList = boardDao.selectQnaList(search_state, search_sdate, search_edate, search_id, search_name, search_qna_type_first, search_qna_type_second, search_content, startIndex, search_listCount);
            qnaResponse.setQnaList(qnaList);
        } catch (Exception e) {

            log.error("Fail to get qnaList, msg = " + e.getMessage());
            e.printStackTrace();
        }

        return qnaResponse;
    }


    /**
     * QNA 답변상태 수정 by no
     */
    public int modifyQnaStateByNo(int no) throws Exception {

        return boardDao.modifyQnaStateByNo(no);
    }


    /**
     * QNA 가져오기 by no
     */
    public Qna getQnaByNo(int no) throws Exception {

        return boardDao.getQnaByNo(no);
    }


    /**
     * 문의/답변 답변 리스트
     */
    public List<QnaComment> qnaCommentList(int no) throws Exception {

        return boardDao.selectQnaCommentList(no);
    }


    /**
     * QnaComment 등록
     */
    public int insertQnaComment(QnaComment qnaComment) throws Exception {

        return boardDao.insertQnaComment(qnaComment);
    }
}