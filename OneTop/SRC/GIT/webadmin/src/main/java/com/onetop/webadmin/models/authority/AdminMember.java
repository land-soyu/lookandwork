package com.onetop.webadmin.models.authority;

import lombok.Data;

import java.util.Date;

@Data
public class AdminMember {

    private int no;
    private String id;
    private String password;
    private String is_password_init;
    private String name;
    private String phone_number;
    private Date reg_dt;
    private Date mod_dt;
    private int role;
    private String otp_key;
    private Date otp_reg_dt;
    private String security_proposal;

}
