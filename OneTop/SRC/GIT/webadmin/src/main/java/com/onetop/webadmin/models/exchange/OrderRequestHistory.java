package com.onetop.webadmin.models.exchange;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderRequestHistory {

    private String mb_id;
    private String market_name;
    private String ord_no;
    private Date ord_date;
    private String action;
    private Boolean is_cancel;
    private BigDecimal ord_price;
    private BigDecimal ord_amount;
    private BigDecimal balance;
    private BigDecimal fee;
    private String channel;
    private String req_ip_addr;
    private BigDecimal sign_amount;

}