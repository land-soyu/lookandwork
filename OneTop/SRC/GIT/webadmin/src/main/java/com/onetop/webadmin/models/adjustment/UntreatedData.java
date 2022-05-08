package com.onetop.webadmin.models.adjustment;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UntreatedData {

    private String coin_name;
    private int untreated_count;
    private BigDecimal coinvalue_count;

}
