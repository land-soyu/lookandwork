package com.onetop.webadmin.models.adjustment;

import lombok.Data;

import java.util.Date;

@Data
public class MemberEmailAuth {

    private String auth_key;
    private int auth_type;
    private int ma_idx_user;
    private int email_state;
    private Date send_date;
    private String email_address;
    private String mb_pwd;

}
