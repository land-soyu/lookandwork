package com.onetop.webadmin.models.member;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AuthEmailMember implements Serializable {

    private String authKey;

    @Size(min = 1, max = 1, message = "auth_type Size Error")
    private String authType;
    private int ma_idx_user;
    private String emailState;
    private LocalDateTime sendDate;
    private String mb_id;

    @Email
    private String eAddress;
    private String password;
    private String securitypin;
    private int od_id;

}