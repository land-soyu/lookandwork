package com.onetop.webadmin.models.system;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SystemWithdrawFee {

    private Integer idx;
    private String coinname;
    private BigDecimal max30;
    private BigDecimal min30;

}
