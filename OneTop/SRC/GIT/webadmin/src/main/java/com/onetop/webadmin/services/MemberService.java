package com.onetop.webadmin.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.onetop.webadmin.mappers.CoinDao;
import com.onetop.webadmin.mappers.MemberDao;
import com.onetop.webadmin.models.CoinInfo;
import com.onetop.webadmin.models.member.*;
import com.onetop.webadmin.models.monitoring.CoinInvest;
import com.onetop.webadmin.responses.PageNaviResponse;
import com.onetop.webadmin.responses.member.BlackConsumerResponse;
import com.onetop.webadmin.responses.member.MemberLoginHisResponse;
import com.onetop.webadmin.responses.member.PaymentRepaymentMemberInfoResponse;
import com.onetop.webadmin.responses.member.PaymentRepaymentResponse;
import com.onetop.webadmin.responses.monitoring.MonitoringResponse;
import com.onetop.webadmin.util.Utils;

@Service
public class MemberService extends PageService {
    private static final Logger log = LoggerFactory.getLogger(MemberService.class);


    @Autowired
    private MemberDao memberDao;

    @Autowired
    private CoinDao coinDao;


    /**
     * 코인 주소 가져오기
     */
    public List<MemberWalletAddress> getMemberWalletAddressList(String mb_id) throws Exception {

        return memberDao.getMemberWalletAddressList(mb_id);
    }


    /**
     * 회원의 상한액 변경
     */
    public int modifyMemberExtrapayLimit(int mbNo, int limit) throws Exception {
        return memberDao.modifyExtraPayLimit(mbNo, limit);
    }


    /**
     * 출금가능금액 변경
     */
    public int modifyMemberWithdrawLimit(int mbNo, int limit) throws Exception {
        return memberDao.modifyWithdrawLimit(mbNo, limit);
    }


    /**
     * 코인 리스트 가져오기
     */
    public List<CoinInfo> getCoinList() {

        return coinDao.GetCoinList();
    }


    /**
     * 지급/회수 Log 리스트
     */
    public PaymentRepaymentResponse getPaymentRepaymentList(String search_id, String search_sdate, String search_edate, String search_admin_id, String search_coin, String search_pr_type, String search_content_type, String sortName, String sortOrderBy, boolean excel, int page, int search_listCount) throws Exception {
        PaymentRepaymentResponse paymentRepaymentResponse = new PaymentRepaymentResponse();

        try {

            int total_count = memberDao.countPaymentRepaymentList(search_id, search_sdate, search_edate, search_admin_id, search_coin, search_pr_type, search_content_type);
            paymentRepaymentResponse.setTotal_paymentRepayment_count(total_count);

            PageNaviResponse pageNaviResponse = getPageNavi(total_count, page, search_listCount);
            paymentRepaymentResponse.setPageNaviResponse(pageNaviResponse);

            int startIndex = (page - 1) * search_listCount;

            List<PaymentRepayment> paymentRepaymentList = memberDao.selectPaymentRepaymentList(search_id, search_sdate, search_edate, search_admin_id, search_coin, search_pr_type, search_content_type, sortName, sortOrderBy, excel, startIndex, search_listCount);
            paymentRepaymentResponse.setPaymentRepaymentList(paymentRepaymentList);
        } catch (Exception e) {

            log.error("Fail to get payment/repayment log list, msg = " + e.getMessage());
        }

        return paymentRepaymentResponse;
    }


    /**
     * 지급/회수 Log By no
     */
    public PaymentRepayment getPaymentRepaymentByNo(int no) throws Exception {
        return memberDao.getPaymentRepaymentByNo(no);
    }


    /**
     * 전체 회원 리스트 개수
     */
    public int getMemberCount() throws Exception {
        return memberDao.getMemberCount();
    }


    /**
     * 전체 회원 리스트 (탈퇴회원 제외) 개수
     */
    public int getNotDeleteMemberCount() throws Exception {
        return memberDao.getNotDeleteMemberCount();
    }


    /**
     * @brief 전체 회원 ID 리스트(탈퇴회원 제외)\n
     * @details \n
     */
    public List<String> getNotDeleteMemberIdList() throws Exception {

        return memberDao.getNotDeleteMemberIdList();
    }


    /**
     * 지급/회수 추가
     */
    public int insertAdminPaymentRepayment(PaymentRepayment paymentRepayment) throws Exception {
        return memberDao.insertAdminPaymentRepayment(paymentRepayment);
    }


    /**
     * 지급/회수 추가완료
     */
    public int updateAdminPaymentRepayment(int apr_no) throws Exception {
        return memberDao.updateAdminPaymentRepayment(apr_no);
    }


    /**
     * @brief 지급/회수 상세보기 Log 추가\n
     * @details \n
     */
    public int insertAdminPaymentRepaymentMemberInfo(PaymentRepaymentMemberInfo paymentRepaymentMemberInfo) throws Exception {

        return memberDao.insertAdminPaymentRepaymentMemberInfo(paymentRepaymentMemberInfo);
    }

    /**
     * @brief 지급/회수 상세보기 Log 리스트\n
     * @details \n
     */
    public PaymentRepaymentMemberInfoResponse getPaymentRepaymentMemberInfoList(int no, boolean excel, int page) throws Exception {

        PaymentRepaymentMemberInfoResponse paymentRepaymentMemberInfoResponse = new PaymentRepaymentMemberInfoResponse();

        try {

            int total_count = memberDao.countPaymentRepaymentMemberInfoList(no);
            paymentRepaymentMemberInfoResponse.setTotal_paymentRepaymentMemberInfo_count(total_count);

            PageNaviResponse pageNaviResponse = getPageNavi(total_count, page, SIZE_PER_PAGE);
            paymentRepaymentMemberInfoResponse.setPageNaviResponse(pageNaviResponse);

            int startIndex = (page - 1) * SIZE_PER_PAGE;

            List<PaymentRepaymentMemberInfo> paymentRepaymentMemberInfoList = memberDao.selectPaymentRepaymentMemberInfoList(no, excel, startIndex, SIZE_PER_PAGE);
            paymentRepaymentMemberInfoResponse.setPaymentRepaymentMemberInfoList(paymentRepaymentMemberInfoList);
        } catch (Exception e) {

            log.error("Fail to get payment/repayment member info list, msg = " + e.getMessage());
        }

        return paymentRepaymentMemberInfoResponse;
    }


    /**
     *  csv 파일 읽어오기
     */
    public HashMap<String, String> csvFileRead(MultipartFile uploadCsv) throws Exception {
        HashMap<String, String> csvList = new HashMap<>();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(uploadCsv.getInputStream(), "UTF-8"));
            Charset.forName("UTF-8");
            String line = "";
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] excelData = line.split(",");

                if(excelData.length==2){
                    csvList.put(excelData[0].trim(), excelData[1].trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) br.close();
        }

        System.out.println(csvList);

        return csvList;
    }


    /**
     * 블랙 컨슈머 리스트
     *
     * @param search_state
     * @param search_sdate
     * @param search_edate
     * @param search_id
     * @param page
     * @param search_listCount
     * @return
     * @throws Exception
     */
    public BlackConsumerResponse blackConsumerList(String search_state, String search_sdate, String search_edate, String search_id, boolean excel, int page, int search_listCount) throws Exception {

        BlackConsumerResponse blackConsumerResponse = new BlackConsumerResponse();

        try {

            int total_count = memberDao.countBlackConsumerList(search_state, search_sdate, search_edate, search_id);
            blackConsumerResponse.setTotal_blackConsumer_count(total_count);

            PageNaviResponse pageNaviResponse = getPageNavi(total_count, page, search_listCount);
            blackConsumerResponse.setPageNaviResponse(pageNaviResponse);

            int startIndex = (page - 1) * search_listCount;

            List<BlackConsumer> blackConsumerList = memberDao.selectBlackConsumerList(search_state, search_sdate, search_edate, search_id, excel, startIndex, search_listCount);
            blackConsumerResponse.setBlackConsumerList(blackConsumerList);
        } catch (Exception e) {

            log.error("Fail to get black consumer List, msg = " + e.getMessage());
        }

        return blackConsumerResponse;
    }

    /**
     * 블랙 컨슈머 추가
     * member_blackconsumer_history 추가 후 member 테이블에 mb_status, mb_blk_type 변경
     *
     * @param blackConsumer
     * @return
     * @throws Exception
     */
    public int insertBlackConsumer(BlackConsumer blackConsumer) throws Exception {

        memberDao.insertBlackConsumer(blackConsumer);

        return memberDao.modifyMemberIsBlackConsumer("BLK", blackConsumer);
    }

    /**
     * 블랙 컨슈머 아이디로 정보 가져오기
     *
     * @param mb_id
     * @return
     * @throws Exception
     */
    public BlackConsumer getBlackConsumerById(String mb_id) throws Exception {

        return memberDao.getBlackConsumerById(mb_id);
    }

    /**
     * 블랙 컨슈머 해제
     * member_blackconsumer_history테이블 state 변경 후 member 테이블에 mb_status, mb_blk_type 변경
     *
     * @param blackConsumer
     * @return
     * @throws Exception
     */
    public int releaseBlackConsumer(BlackConsumer blackConsumer) throws Exception {

        memberDao.releaseBlackConsumer(blackConsumer);

        return memberDao.modifyMemberIsBlackConsumer("OK", blackConsumer);
    }


    /**
     * 사용자 로그인 히스토리
     *
     * @param search_sdate
     * @param search_edate
     * @param mb_id
     * @param login_reg_ip
     * @param login_yn
     * @param page
     * @param search_listCount
     * @return
     * @throws Exception
     */
    public MemberLoginHisResponse memberLoginHisList(
            String search_sdate,
            String search_edate,
            String mb_id,
            String login_reg_ip,
            String login_yn,
            int page,
            int search_listCount
    ) throws Exception {
        MemberLoginHisResponse memberLoginHisResponse = new MemberLoginHisResponse();

        try {

            int total_count = memberDao.countMemberLoginHisList(search_sdate, search_edate, mb_id, login_reg_ip, login_yn);
            memberLoginHisResponse.setTotal_memberLoginHis_count(total_count);

            PageNaviResponse pageNaviResponse = getPageNavi(total_count, page, search_listCount);
            memberLoginHisResponse.setPageNaviResponse(pageNaviResponse);

            int startIndex = (page - 1) * search_listCount;

            List<MemberLoginHis> memberLoginHisList = memberDao.selectMemberLoginHisList(search_sdate, search_edate, mb_id, login_reg_ip, login_yn, startIndex, search_listCount);
            for (int i = 0; i < memberLoginHisList.size(); i++) {
                memberLoginHisList.get(i).setKST_login_reg_dt(Utils.ConvertUTCtoKST(memberLoginHisList.get(i).getLogin_reg_dt()));
            }


            memberLoginHisResponse.setMemberLoginHisList(memberLoginHisList);
        } catch (Exception e) {

            log.error("Fail to get login history List, msg = " + e.getMessage());
        }

        return memberLoginHisResponse;
    }


    public int getYesterDayResistMemberCount() {
        return memberDao.getYesterDayResistMemberCount(new Date());
    }


    public int getTodayResistMemberCount() {
        return memberDao.getTodayResistMemberCount(new Date());
    }


    public MonitoringResponse getMemberRankCount() {
        return memberDao.getMemberRankCount();
    }


    public List<CoinInvest> getTotalAvailCoin() {
        return memberDao.getTotalAvailCoin();
    }


    /**
     * 회원 이메일 변경
     **/
    public void updateMemberEmail(int mbNo, String mb_email) throws Exception {
        memberDao.updateMemberEmail(mbNo, mb_email);
    }


    /**
     * 추천인 ID 변경
     **/
    public void updateReferrerId(ReferrerIdUpdate referrerIdUpdate) throws Exception {
        memberDao.updateReferrerId_ParendId(referrerIdUpdate);
        memberDao.updateReferrerId_Position(referrerIdUpdate);
        memberDao.updateReferrerId_MbSortInfo(referrerIdUpdate);
    }
}