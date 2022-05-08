package com.onetop.webadmin.responses.board;

import java.util.ArrayList;
import java.util.List;

import com.onetop.webadmin.models.board.Notice;
import com.onetop.webadmin.responses.PageNaviResponse;

public class NoticeResponse {

    /**
     * @brief    	공지사항 리스트\n
     * @details		\n
     */
    private List<Notice> noticeList;
    /**
     * @brief		리스트 네비게이션\n
     * @details      \n
     */
    private PageNaviResponse pageNaviResponse;
    /**
     * @brief		공지사항 리스트 수\n
     * @details     \n
     */
    private int total_notice_count;

    public NoticeResponse() {

        noticeList = new ArrayList<Notice>();
        pageNaviResponse = new PageNaviResponse();
    }

    public List<Notice> getNoticeList() {

        return noticeList;
    }

    public void setNoticeList(List<Notice> noticeList) {

        this.noticeList = noticeList;
    }

    public PageNaviResponse getPageNaviResponse() {

        return pageNaviResponse;
    }

    public void setPageNaviResponse(PageNaviResponse pageNaviResponse) {

        this.pageNaviResponse = pageNaviResponse;
    }

    public int getTotal_notice_count() {

        return total_notice_count;
    }

    public void setTotal_notice_count(int total_notice_count) {

        this.total_notice_count = total_notice_count;
    }
}