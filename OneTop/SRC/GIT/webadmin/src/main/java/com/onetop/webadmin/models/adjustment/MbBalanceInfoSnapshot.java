package com.onetop.webadmin.models.adjustment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MbBalanceInfoSnapshot {

    private int snapshot_idx;
    private Date snapshot_time;
    private String mb_id;
    private String coin_name;
    private BigDecimal avail;
    private BigDecimal in_use;
    private BigDecimal total;
    private BigDecimal in_withdraw;

}
