package com.onetop.webadmin.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import com.onetop.webadmin.mappers.AuthorityDao;
import com.onetop.webadmin.models.authority.AccessRole;
import com.onetop.webadmin.models.authority.AdminLog;
import com.onetop.webadmin.models.authority.AdminMember;
import com.onetop.webadmin.responses.PageNaviResponse;
import com.onetop.webadmin.responses.authority.AdminLogResponse;
import com.onetop.webadmin.responses.authority.AdminMemberResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorityService extends PageService {

    private static final Logger log = LoggerFactory.getLogger(AuthorityService.class);

    @Autowired
    private AuthorityDao authorityDao;



    /**
     * 접근권한 리스트
     */
    public AdminMemberResponse adminMemberList(String search_type, String search_content, int page, int search_listCount) throws Exception {
        AdminMemberResponse adminMemberResponse = new AdminMemberResponse();
        List<AdminMember> noticeList = new ArrayList<AdminMember>();

        try{
            int total_count = authorityDao.countAdminMemberList(search_type, search_content);
            adminMemberResponse.setTotal_adminMember_count(total_count);

            PageNaviResponse pageNaviResponse = getPageNavi(total_count, page, search_listCount);
            adminMemberResponse.setPageNaviResponse(pageNaviResponse);

            int startIndex = (page - 1) * search_listCount;

            if (total_count > 0) {

                noticeList = authorityDao.selectAdminMemberList(search_type, search_content, startIndex, search_listCount);
            }

            adminMemberResponse.setAdminMemberList(noticeList);
        } catch (Exception e) {
            log.error("Fail to get noticeList, msg = " + e.getMessage());
        }
        return adminMemberResponse;
    }


    /**
     * 관리자 정보 가져오기 by no
     */
    public AdminMember getAdminMemberByNo(int no) {
        return authorityDao.getAdminMemberByNo(no);
    }


    /**
     * 관리자 정보 가져오기 by id
     */
    public AdminMember getAdminMemberById(String id) {
        return authorityDao.getAdminMemberById(id);
    }


    /**
     * 관리자 정보 추가
     */
    public int insertAdminMember(AdminMember adminMember) {
        return authorityDao.insertAdminMember(adminMember);
    }


    /**
     * 관리자 정보 변경
     */
    public int modifyAdminMember(AdminMember adminMember) {
        return  authorityDao.modifyAdminMember(adminMember);
    }


    /**
     * 관리자 정보 삭제
     */
    public int deleteAdminMember(String no) {
        return authorityDao.deleteAdminMember(no);
    }


    /**
     * 관리자 ID 체크
     */
    public int checkId(String id) {
        return authorityDao.checkId(id);
    }


    /**
     * 권한 정보 가져오기
     */
    public List<AccessRole> accessRoleList() {
        return authorityDao.accessRoleList();
    }


    /**
     * 관리자 Log 추가
     */
    public int insertAdminLog(HttpServletRequest request, int type, String content, String id) {
        String ip = request.getHeader("X-FORWARDED-FOR");

        if (ip == null) ip = request.getHeader("Proxy-Client-IP");
        if (ip == null) ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip == null) ip = request.getHeader("HTTP_CLIENT_IP");
        if (ip == null) ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (ip == null) ip = request.getRemoteAddr();

        AdminLog adminLog = new AdminLog();
        adminLog.setType(type);
        adminLog.setId(id);
        adminLog.setContent(content);
        adminLog.setIp(ip);

        return authorityDao.insertAdminLog(adminLog);
    }


    /**
     * 관리자 Log 리스트
     */
    public AdminLogResponse adminLogList(String search_sdate, String search_edate, String search_type, String search_id, String search_ip, boolean excel, int page, int search_listCount) throws Exception {
        AdminLogResponse adminLogResponse = new AdminLogResponse();
        List<AdminLog> adminLogList = new ArrayList<AdminLog>();

        try{
            int total_count = authorityDao.countAdminLogList(search_type, search_sdate, search_edate, search_id, search_ip);
            adminLogResponse.setTotal_adminLog_count(total_count);

            PageNaviResponse pageNaviResponse = getPageNavi(total_count, page, search_listCount);
            adminLogResponse.setPageNaviResponse(pageNaviResponse);

            int startIndex = (page - 1) * search_listCount;

            if (total_count > 0) {

                adminLogList = authorityDao.selectAdminLogList(search_type, search_sdate, search_edate, search_id, search_ip, excel, startIndex, search_listCount);
            }

            adminLogResponse.setAdminLogList(adminLogList);
        } catch (Exception e) {
            log.error("Fail to get admin log list, msg = " + e.getMessage());
        }

        return adminLogResponse;
    }


    /**
     * 시스템 변경 Log 리스트
     */
    public AdminLogResponse systemLogList(String search_sdate, String search_edate, String search_type, String search_id, boolean excel, int page, int search_listCount) throws Exception {
        AdminLogResponse adminLogResponse = new AdminLogResponse();
        List<AdminLog> adminLogList = new ArrayList<AdminLog>();

        try{
            int total_count = authorityDao.countSystemLogList(search_type, search_sdate, search_edate, search_id);
            adminLogResponse.setTotal_adminLog_count(total_count);

            PageNaviResponse pageNaviResponse = getPageNavi(total_count, page, search_listCount);
            adminLogResponse.setPageNaviResponse(pageNaviResponse);

            int startIndex = (page - 1) * search_listCount;

            if (total_count > 0) {

                adminLogList = authorityDao.selectSystemLogList(search_type, search_sdate, search_edate, search_id, excel, startIndex, search_listCount);
            }

            adminLogResponse.setAdminLogList(adminLogList);
        } catch (Exception e) {
            log.error("Fail to get system log list, msg = " + e.getMessage());
        }

        return adminLogResponse;
    }
}