package com.onetop.webadmin.responses.monitoring;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonitoringCoinInfo {

    private int proceed;

    private String action;
    private String coin_name;
    private String market_name;

    private BigDecimal sum_sign_amount;
    private BigDecimal sum_sign_balance;
    private BigDecimal sum_adjust_balance;

    private String cdate;
}