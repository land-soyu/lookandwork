package com.onetop.webadmin.models.system;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SystemInvestSetting {

    private BigDecimal reinvest_amount;
    private Integer retrieve_min;
    private Integer invest_min;
    private BigDecimal extrapay_rate;
    private Integer target_invest_rate;

}
