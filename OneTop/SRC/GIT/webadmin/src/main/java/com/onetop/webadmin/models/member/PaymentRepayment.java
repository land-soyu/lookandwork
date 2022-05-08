package com.onetop.webadmin.models.member;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaymentRepayment {

    private int no;
    private Date reg_dt;
    private int content_type;
    private String content;
    private int pr_type;
    private String coin_name;
    private BigDecimal coin_quantity;
    private int complete_count;
    private int total_count;
    private String admin_id;
    private String mb_id;
    private String is_payed;

}
