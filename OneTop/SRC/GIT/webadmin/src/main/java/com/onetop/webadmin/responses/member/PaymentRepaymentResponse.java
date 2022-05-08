package com.onetop.webadmin.responses.member;

import java.util.ArrayList;
import java.util.List;

import com.onetop.webadmin.models.member.PaymentRepayment;
import com.onetop.webadmin.responses.PageNaviResponse;

public class PaymentRepaymentResponse {

    /**
     * @brief    	관리자 지급/회수 리스트\n
     * @details	\n
     */
    private List<PaymentRepayment> paymentRepaymentList;
    /**
     * @brief		관리자 지급/회수 네비게이션\n
     * @details    \n
     */
    private PageNaviResponse pageNaviResponse;
    /**
     * @brief		관리자 지급/회수 수\n
     * @details    \n
     */
    private int total_paymentRepayment_count;

    public PaymentRepaymentResponse() {

        paymentRepaymentList = new ArrayList<PaymentRepayment>();
        pageNaviResponse = new PageNaviResponse();
    }

    public List<PaymentRepayment> getPaymentRepaymentList() {

        return paymentRepaymentList;
    }

    public void setPaymentRepaymentList(List<PaymentRepayment> paymentRepaymentList) {

        this.paymentRepaymentList = paymentRepaymentList;
    }

    public PageNaviResponse getPageNaviResponse() {

        return pageNaviResponse;
    }

    public void setPageNaviResponse(PageNaviResponse pageNaviResponse) {

        this.pageNaviResponse = pageNaviResponse;
    }

    public int getTotal_paymentRepayment_count() {

        return total_paymentRepayment_count;
    }

    public void setTotal_paymentRepayment_count(int total_paymentRepayment_count) {

        this.total_paymentRepayment_count = total_paymentRepayment_count;
    }
}