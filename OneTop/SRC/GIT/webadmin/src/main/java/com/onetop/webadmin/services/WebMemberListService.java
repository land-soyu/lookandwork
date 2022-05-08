package com.onetop.webadmin.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;

import com.onetop.webadmin.config.ServerConfigs;
import com.onetop.webadmin.mappers.MemberDao;
import com.onetop.webadmin.models.Counts;
import com.onetop.webadmin.models.authority.AdminMember;
import com.onetop.webadmin.models.member.WebMember;
import com.onetop.webadmin.models.member.WebMemberSecurityLog;
import com.onetop.webadmin.responses.PageNaviResponse;
import com.onetop.webadmin.responses.member.WebMemberListResponse;
import com.onetop.webadmin.util.AES256Util;
import com.onetop.webadmin.util.PasswordUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebMemberListService extends PageService {

    private static final Logger log = LoggerFactory.getLogger(WebMemberListService.class);

    @Autowired
    private MemberDao memberDao;



    public List<WebMember> GetWebSeacherMemberList(String mb_id){
        return memberDao.GetWebSearchMemberList(mb_id);
    }
    public List<WebMember> GetWebSeacherMemberListByMB_NO(String mb_no){
        return memberDao.GetWebSeacherMemberListByMB_NO(mb_no);
    }


    public WebMember GetWebMember(String mb_id){
        return memberDao.GetWebMember(mb_id);
    }
    public WebMember GetWebMember_no(String mb_no){
        return memberDao.GetWebMember_no(mb_no);
    }


    public WebMemberListResponse GetWebMemberList(
            String mb_id,
            String mb_referrer,
            String mb_reg_dt_from, String mb_reg_dt_to,
            String mb_last_login_dt_from, String mb_last_login_dt_to,
            List<String> stateList,
            List<String> rankList,
            String sortName,
            String sortOrderBy,
            boolean excel,
            int currentPage,
            int search_listCount
    ) {
        WebMemberListResponse response = new WebMemberListResponse();
        try {
            HashMap<String, Object> resultSet = memberDao.GetAllWebMemberList(mb_id, mb_referrer, mb_reg_dt_from, mb_reg_dt_to, mb_last_login_dt_from, mb_last_login_dt_to, stateList, rankList, sortName, sortOrderBy, excel, currentPage, search_listCount);

            Counts counts = (Counts)resultSet.get("counts");
            response.setCounts(counts);

            PageNaviResponse naviResponse = getPageNavi(counts.getSearch(), currentPage, search_listCount);
            response.setPageNaviResponse(naviResponse);
            response.setWebMemberList((List<WebMember>)resultSet.get("list"));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

        return response;
    }


    public int CheckWebMemberIdcardNoExist(String idcardNo) throws Exception {

        AES256Util aes256Util = new AES256Util(PasswordUtil.sha256(ServerConfigs.SHA256_KEY), "");
        String encIdcardNo = aes256Util.aesEncode(idcardNo);

        return memberDao.countMemeberIdcardNoExist(encIdcardNo);
    }


    public WebMemberListResponse GetWebMemberCertList(
            String mb_id,
            String request_dt_from, String request_dt_to,
            String proct_dt_from, String proct_dt_to,
            List<String> stateList,
            List<String> certList,
            boolean excel,
            int currentPage,
            int search_listCount
    ) {
        WebMemberListResponse response = new WebMemberListResponse();
        try {
            HashMap<String, Object> resultSet = memberDao.GetAllWebMemberCertList(mb_id, request_dt_from, request_dt_to, proct_dt_from, proct_dt_to, stateList, certList, excel, currentPage, search_listCount);

            Counts counts = (Counts)resultSet.get("counts");
            response.setCounts(counts);

            PageNaviResponse naviResponse = getPageNavi(counts.getSearch(), currentPage, search_listCount);
            response.setPageNaviResponse(naviResponse);
            response.setWebMemberSecurityList((List<WebMemberSecurityLog>)resultSet.get("list"));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

        return response;
    }


    public Map<String, Object> GetWebMemberIdCardInfo(String log_no, String mb_id) throws UnsupportedEncodingException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Map<String, Object> tempMap = memberDao.GetWebMemberIdCardInfo(log_no, mb_id);

        String result = "";
        String path = "";
        if(tempMap.get("upload_path") != null){
            String fileName = path + tempMap.get("upload_path").toString();
            try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {

                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                String text = sb.toString();

                String regDt = new SimpleDateFormat("yyyyMMddHHmmss").format(tempMap.get("reg_dt"));
                AES256Util aes256Util = new AES256Util(tempMap.get("mb_id").toString(), regDt);
                result = aes256Util.aesDecode(text);

                tempMap.put("upload_path", result);

            } catch (Exception e) {
                log.error(e.getMessage());
                return tempMap;
            }
        }

        if(tempMap.get("upload_path2") != null){
            String fileName = path + tempMap.get("upload_path2").toString();
            try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {

                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                String text = sb.toString();

                String regDt = new SimpleDateFormat("yyyyMMddHHmmss").format(tempMap.get("reg_dt"));
                AES256Util aes256Util = new AES256Util(tempMap.get("mb_id").toString(), regDt);
                result = aes256Util.aesDecode(text);

                tempMap.put("upload_path2", result);

            } catch (Exception e) {
                log.error(e.getMessage());
                return tempMap;
            }
        }

        String idcardNo = "";
        if (tempMap.get("idcard_no") != null) {

            try {

                AES256Util aes256Util = new AES256Util(PasswordUtil.sha256(ServerConfigs.SHA256_KEY), "");
                idcardNo = aes256Util.aesDecode(tempMap.get("idcard_no").toString());
                tempMap.put("idcard_no", idcardNo);
            } catch (Exception e) {

                log.error("Fail to decode idcard no, msg = " + e.getMessage());
                return tempMap;
            }
        }

        return tempMap;
    }


    public int UpdateWebMemberIdCardRequest(HttpServletRequest req) throws Exception {
        return memberDao.UpdateWebMemberIdCardRequest(req);
    }


    public String ClearWebMemberCertInfo(AdminMember adminMember, HttpServletRequest req){
        try {
            if (memberDao.ClearWebMemberCertInfo(adminMember, req)) {
                return "success";
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "fail";
    }
}