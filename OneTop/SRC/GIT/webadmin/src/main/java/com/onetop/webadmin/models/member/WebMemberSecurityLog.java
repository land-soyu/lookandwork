package com.onetop.webadmin.models.member;

import lombok.Data;

import java.util.Date;

@Data
public class WebMemberSecurityLog {

    private int rowNum;
    private int log_no;
    private int mb_no;
    private String mb_id;
    private int security_type;
    private String state;
    private String confirm_id;
    private String confirm_msg;
    private String confirm_ip;

    private String idcard_no;
    private String country_code;
    private String mb_use_language;
    private String first_name;
    private String last_name;
    private String upload_path;

    private Date reg_dt;
    private Date confirm_dt;

    private String bank_account;
    private String bank_code;
    private String bank_account_holder;

}
