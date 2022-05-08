package com.onetop.webadmin.models.adjustment;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberBalanceInfo {
    private String mb_id;
    private String coin_name;
    private BigDecimal avail;
    private BigDecimal in_withdraw;


    private BigDecimal exc_avail;
    private BigDecimal exc_in_withdraw;

    public MemberBalanceInfo(){
        this.avail = BigDecimal.ZERO;
        this.in_withdraw = BigDecimal.ZERO;

        this.exc_avail = BigDecimal.ZERO;
        this.exc_in_withdraw = BigDecimal.ZERO;
    }

}
