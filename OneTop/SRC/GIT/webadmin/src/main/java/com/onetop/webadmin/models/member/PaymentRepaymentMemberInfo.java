package com.onetop.webadmin.models.member;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRepaymentMemberInfo {

    private int no;
    private int pr_no;
    private BigDecimal coin_quantity;
    private String state;
    private String content;
    private String mb_id;

}
