package com.onetop.webadmin.models.adjustment;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Data
public class MemberBalanceLog {
    private int idx;
    private int mb_no;
    private String type;
    private BigDecimal total_bonus;
    private BigDecimal daily_bonus;
    private BigDecimal recomd_bonus;
    private BigDecimal matching_bonus;
    private BigDecimal team_bonus;
    private BigDecimal donation_amount;
    private String standard_idx;
    private Date up_dt;

    private String admin_id;
    private String content;

    private String mb_id;

    public MemberBalanceLog(){
        this.total_bonus = BigDecimal.ZERO;
        this.daily_bonus = BigDecimal.ZERO;
        this.recomd_bonus = BigDecimal.ZERO;
        this.matching_bonus = BigDecimal.ZERO;
        this.team_bonus = BigDecimal.ZERO;
        this.donation_amount = BigDecimal.ZERO;
    }

}