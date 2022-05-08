package com.onetop.webadmin.responses.board;

import java.util.ArrayList;
import java.util.List;

import com.onetop.webadmin.models.board.AdminQna;
import com.onetop.webadmin.responses.PageNaviResponse;

public class AdminQnaResponse {

    /**
     * @brief    	오류 확인 요청 리스트\n
     * @details		\n
     */
    private List<AdminQna> adminQnaList;
    /**
     * @brief		리스트 네비게이션\n
     * @details      \n
     */
    private PageNaviResponse pageNaviResponse;
    /**
     * @brief		오류 확인 요청 리스트 수\n
     * @details     \n
     */
    private int total_adminQna_count;

    public AdminQnaResponse() {

        adminQnaList = new ArrayList<AdminQna>();
        pageNaviResponse = new PageNaviResponse();
    }

    public List<AdminQna> getAdminQnaList() {

        return adminQnaList;
    }

    public void setAdminQnaList(List<AdminQna> adminQnaList) {

        this.adminQnaList = adminQnaList;
    }

    public PageNaviResponse getPageNaviResponse() {

        return pageNaviResponse;
    }

    public void setPageNaviResponse(PageNaviResponse pageNaviResponse) {

        this.pageNaviResponse = pageNaviResponse;
    }

    public int getTotal_adminQna_count() {

        return total_adminQna_count;
    }

    public void setTotal_adminQna_count(int total_adminQna_count) {

        this.total_adminQna_count = total_adminQna_count;
    }
}