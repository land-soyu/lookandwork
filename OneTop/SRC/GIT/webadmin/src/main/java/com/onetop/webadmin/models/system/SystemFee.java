package com.onetop.webadmin.models.system;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SystemFee {

    private BigDecimal invest_fee;
    private BigDecimal recover_max30;
    private BigDecimal recover_min30;

}
