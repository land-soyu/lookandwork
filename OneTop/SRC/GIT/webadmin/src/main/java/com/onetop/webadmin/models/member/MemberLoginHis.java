package com.onetop.webadmin.models.member;

import lombok.Data;

import java.util.Date;

@Data
public class MemberLoginHis {

    private int login_no;
    private int mb_no;
    private String mb_id;
    private String mb_key;
    private int oauth_result_code;
    private String oauth_result_msg;
    private String login_agent;
    private String login_block_yn;
    private Date login_reg_dt;
    private String login_reg_ip;
    private String login_user_os;
    private String login_user_browser;
    private Date KST_login_reg_dt;


}
