package com.onetop.webadmin.models.monitoring;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Data
public class CompanyBalanceDetail {

    private BigInteger ord_no;
    private Date date;
    private String action;
    private String detail;
    private BigDecimal amount;
    private String amount_coin_name;
    private String result;
    private String admin_id;
    private String memo;

}
