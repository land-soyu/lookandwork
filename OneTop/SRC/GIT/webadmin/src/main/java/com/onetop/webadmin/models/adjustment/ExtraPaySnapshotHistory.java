package com.onetop.webadmin.models.adjustment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ExtraPaySnapshotHistory {

    private Integer idx;
    private Integer snapshot_idx;
    private Integer mb_no;
    private String mb_id;    
    private BigDecimal purchase;    
    private BigDecimal total_bonus;
    private BigDecimal daily_bonus;
    private BigDecimal recomd_bonus;
    private BigDecimal matching_bonus;
    private BigDecimal team_bonus;
    private BigDecimal donation_amount;    
    private String is_apply;
    private Date up_dt;

}
