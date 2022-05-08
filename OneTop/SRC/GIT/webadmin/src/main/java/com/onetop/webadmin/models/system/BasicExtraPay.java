package com.onetop.webadmin.models.system;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BasicExtraPay {

    private Integer idx;
    private Integer minimum;
    private Integer maximum;
    private BigDecimal paymentrate;

}
