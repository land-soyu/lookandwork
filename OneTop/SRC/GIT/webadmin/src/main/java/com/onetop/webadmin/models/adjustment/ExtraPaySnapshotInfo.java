package com.onetop.webadmin.models.adjustment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ExtraPaySnapshotInfo {

    private int snapshot_idx;
    private Date reserve_time;
    private String is_complete;
    private int daily_bonus_num;
    private BigDecimal daily_bonus_amount;
    private int recomd_bonus_num;
    private BigDecimal recomd_bonus_amount;
    private int matching_bonus_num;
    private BigDecimal matching_bonus_amount;
    private int team_bonus_num;
    private BigDecimal team_bonus_amount;
    private int donation_num;
    private BigDecimal donation_amount;
    private int total_num;
    private BigDecimal total_amount;
    private Date pay_date;
    private String pay_state;

}
