package com.onetop.webadmin.responses.board;

import java.util.ArrayList;
import java.util.List;

import com.onetop.webadmin.models.board.Qna;
import com.onetop.webadmin.responses.PageNaviResponse;

public class QnaResponse {

    /**
     * @brief    	문의/답변 리스트\n
     * @details		\n
     */
    private List<Qna> qnaList;
    /**
     * @brief		리스트 네비게이션\n
     * @details      \n
     */
    private PageNaviResponse pageNaviResponse;
    /**
     * @brief		문의/답변 리스트 수\n
     * @details     \n
     */
    private int total_qna_count;

    public QnaResponse() {

        qnaList = new ArrayList<Qna>();
        pageNaviResponse = new PageNaviResponse();
    }

    public List<Qna> getQnaList() {

        return qnaList;
    }

    public void setQnaList(List<Qna> qnaList) {

        this.qnaList = qnaList;
    }

    public PageNaviResponse getPageNaviResponse() {

        return pageNaviResponse;
    }

    public void setPageNaviResponse(PageNaviResponse pageNaviResponse) {

        this.pageNaviResponse = pageNaviResponse;
    }

    public int getTotal_qna_count() {

        return total_qna_count;
    }

    public void setTotal_qna_count(int total_qna_count) {

        this.total_qna_count = total_qna_count;
    }
}