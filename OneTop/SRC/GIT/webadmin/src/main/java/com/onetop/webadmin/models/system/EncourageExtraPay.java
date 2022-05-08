package com.onetop.webadmin.models.system;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EncourageExtraPay {

    private int idx;
    private int groupNum;
    private BigDecimal paymentrate;

}
