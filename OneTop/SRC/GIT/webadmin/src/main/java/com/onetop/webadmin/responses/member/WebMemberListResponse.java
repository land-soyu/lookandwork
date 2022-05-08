package com.onetop.webadmin.responses.member;

import java.util.ArrayList;
import java.util.List;

import com.onetop.webadmin.models.Counts;
import com.onetop.webadmin.models.member.WebMember;
import com.onetop.webadmin.models.member.WebMemberSecurityLog;
import com.onetop.webadmin.responses.PageNaviResponse;

public class WebMemberListResponse {
    private List<WebMember> webMemberList;
    private List<WebMemberSecurityLog> webMemberSecurityList;

    private PageNaviResponse pageNaviResponse;

    private Counts counts;

    public WebMemberListResponse(){
        this.webMemberList = new ArrayList<>();
        this.counts = new Counts();
        pageNaviResponse = new PageNaviResponse();
    }


    public Counts getCounts() {
        return counts;
    }

    public void setCounts(Counts counts) {
        this.counts = counts;
    }

    public List<WebMember> getWebMemberList() {
        return webMemberList;
    }

    public void setWebMemberList(List<WebMember> webMemberList) {
        this.webMemberList = webMemberList;
    }

    public List<WebMemberSecurityLog> getWebMemberSecurityList() {return webMemberSecurityList;}

    public void setWebMemberSecurityList(List<WebMemberSecurityLog> webMemberSecurityList) {this.webMemberSecurityList = webMemberSecurityList;}

    public PageNaviResponse getPageNaviResponse() {
        return pageNaviResponse;
    }

    public void setPageNaviResponse(PageNaviResponse pageNaviResponse) {
        this.pageNaviResponse = pageNaviResponse;
    }


}
