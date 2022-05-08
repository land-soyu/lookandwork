package com.onetop.webadmin.models.bank;

import lombok.Data;

@Data
public class BankWithdrawProcRequest {

    private String handling;
    private String od_id;
    private String otp_pw;
    private String admin_pw;
    private String withdraw_pw;
    private String reason;
    private String status;

}
