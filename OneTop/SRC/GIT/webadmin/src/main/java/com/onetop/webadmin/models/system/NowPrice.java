package com.onetop.webadmin.models.system;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NowPrice {

    private String coin_name;
    private BigDecimal krw_price;
    private BigDecimal usd_price;

}
