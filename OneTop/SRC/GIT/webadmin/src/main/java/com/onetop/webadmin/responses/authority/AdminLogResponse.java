package com.onetop.webadmin.responses.authority;

import java.util.ArrayList;
import java.util.List;

import com.onetop.webadmin.models.authority.AdminLog;
import com.onetop.webadmin.responses.PageNaviResponse;

public class AdminLogResponse {

    /**
     * @brief    	관리자 Log 리스트\n
     * @details	\n
     */
    private List<AdminLog> adminLogList;
    /**
     * @brief		리스트 네비게이션\n
     * @details    \n
     */
    private PageNaviResponse pageNaviResponse;
    /**
     * @brief		관리자 Log 수\n
     * @details    \n
     */
    private int total_adminLog_count;

    public AdminLogResponse() {

        adminLogList = new ArrayList<AdminLog>();
        pageNaviResponse = new PageNaviResponse();
    }

    public List<AdminLog> getAdminLogList() {

        return adminLogList;
    }

    public void setAdminLogList(List<AdminLog> adminLogList) {

        this.adminLogList = adminLogList;
    }

    public PageNaviResponse getPageNaviResponse() {

        return pageNaviResponse;
    }

    public void setPageNaviResponse(PageNaviResponse pageNaviResponse) {

        this.pageNaviResponse = pageNaviResponse;
    }

    public int getTotal_adminLog_count() {

        return total_adminLog_count;
    }

    public void setTotal_adminLog_count(int total_adminLog_count) {

        this.total_adminLog_count = total_adminLog_count;
    }
}