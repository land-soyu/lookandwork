package com.onetop.webadmin.models.exchange;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SignHistory {

    private String ord_no;
    private String sign_no;
    private String mb_id;
    private BigDecimal sign_price;
    private String sign_price_coin_name;
    private BigDecimal sign_amount;
    private String sign_amount_coin_name;
    private BigDecimal sign_balance;
    private String sign_balance_coin_name;
    private BigDecimal fee;
    private String fee_coin_name;
    private Date sign_date;
    private String action;
    private String req_ip_addr;
    private String noti_email;
    private BigDecimal total_coin;
    private String total_coin_name;
    private String channel;

}
