package com.onetop.webadmin.responses.member;

import java.util.ArrayList;
import java.util.List;

import com.onetop.webadmin.models.member.PaymentRepaymentMemberInfo;
import com.onetop.webadmin.responses.PageNaviResponse;

public class PaymentRepaymentMemberInfoResponse {

    /**
     * @brief    	관리자 지급/회수 리스트\n
     * @details	\n
     */
    private List<PaymentRepaymentMemberInfo> paymentRepaymentMemberInfoList;
    /**
     * @brief		관리자 지급/회수 네비게이션\n
     * @details    \n
     */
    private PageNaviResponse pageNaviResponse;
    /**
     * @brief		관리자 지급/회수 수\n
     * @details    \n
     */
    private int total_paymentRepaymentMemberInfo_count;

    public PaymentRepaymentMemberInfoResponse() {

        paymentRepaymentMemberInfoList = new ArrayList<PaymentRepaymentMemberInfo>();
        pageNaviResponse = new PageNaviResponse();
    }

    public List<PaymentRepaymentMemberInfo> getPaymentRepaymentMemberInfoList() {

        return paymentRepaymentMemberInfoList;
    }

    public void setPaymentRepaymentMemberInfoList(List<PaymentRepaymentMemberInfo> paymentRepaymentMemberInfoList) {

        this.paymentRepaymentMemberInfoList = paymentRepaymentMemberInfoList;
    }

    public PageNaviResponse getPageNaviResponse() {

        return pageNaviResponse;
    }

    public void setPageNaviResponse(PageNaviResponse pageNaviResponse) {

        this.pageNaviResponse = pageNaviResponse;
    }

    public int getTotal_paymentRepaymentMemberInfo_count() {

        return total_paymentRepaymentMemberInfo_count;
    }

    public void setTotal_paymentRepaymentMemberInfo_count(int total_paymentRepaymentMemberInfo_count) {

        this.total_paymentRepaymentMemberInfo_count = total_paymentRepaymentMemberInfo_count;
    }
}