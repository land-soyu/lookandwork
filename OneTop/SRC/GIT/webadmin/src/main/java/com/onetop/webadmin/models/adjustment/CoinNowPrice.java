package com.onetop.webadmin.models.adjustment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CoinNowPrice {

    private String coin_name;
    private BigDecimal krw_price;
    private BigDecimal usd_price;
    private Date up_dt;

}
