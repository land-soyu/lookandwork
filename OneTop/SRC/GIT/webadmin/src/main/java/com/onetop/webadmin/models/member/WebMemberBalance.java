package com.onetop.webadmin.models.member;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WebMemberBalance {

    private int mb_no;
    private int mb_parent_id;
    private String mb_id;
    private String coin_name;

    private BigDecimal avail;

    private BigDecimal basic_pay;       // 누적 기본수당
    private BigDecimal sponsor_pay;     // 누적 후원수당
    private BigDecimal encourage_pay;   // 누적 장려수당
    private BigDecimal rank_pay;        // 누적 직급수당
    private BigDecimal apply_pay;       // 누적 지급수당

    private int mb_position;
}
