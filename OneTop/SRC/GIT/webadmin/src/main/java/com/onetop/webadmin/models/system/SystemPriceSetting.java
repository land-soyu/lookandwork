package com.onetop.webadmin.models.system;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SystemPriceSetting {

    private Integer idx;
    private String coin_name;
    private Integer ratio_auto;
    private BigDecimal manual_value;

}
