package com.onetop.webadmin.models.system;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RankExtraPay {

    private Integer idx;
    private String rank;
    private String investtoltal;
    private Integer step1_count;
    private Integer five_high_count;
    private Integer t1_line;
    private Integer t1_count;
    private Integer t2_line;
    private Integer t2_count;
    private Integer t3_line;
    private Integer t3_count;
    private Integer t4_line;
    private Integer t4_count;
    private String standard;
    private BigDecimal paymentrate;

}
