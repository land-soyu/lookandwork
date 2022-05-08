package com.onetop.webadmin.responses.authority;

import java.util.ArrayList;
import java.util.List;

import com.onetop.webadmin.models.authority.AdminMember;
import com.onetop.webadmin.responses.PageNaviResponse;

public class AdminMemberResponse {

    /**
     * @brief    	접근권한 리스트\n
     * @details	\n
     */
    private List<AdminMember> adminMemberList;
    /**
     * @brief		리스트 네비게이션\n
     * @details    \n
     */
    private PageNaviResponse pageNaviResponse;
    /**
     * @brief		접근권한 수\n
     * @details    \n
     */
    private int total_adminMember_count;

    public AdminMemberResponse() {

        adminMemberList = new ArrayList<AdminMember>();
        pageNaviResponse = new PageNaviResponse();
    }

    public List<AdminMember> getAdminMemberList() {

        return adminMemberList;
    }

    public void setAdminMemberList(List<AdminMember> adminMemberList) {

        this.adminMemberList = adminMemberList;
    }

    public PageNaviResponse getPageNaviResponse() {

        return pageNaviResponse;
    }

    public void setPageNaviResponse(PageNaviResponse pageNaviResponse) {

        this.pageNaviResponse = pageNaviResponse;
    }

    public int getTotal_adminMember_count() {

        return total_adminMember_count;
    }

    public void setTotal_adminMember_count(int total_adminMember_count) {

        this.total_adminMember_count = total_adminMember_count;
    }
}