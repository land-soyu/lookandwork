package com.onetop.webadmin.models.bank;

import java.util.Date;
import lombok.Data;

@Data
public class DepositInfo {
    private String  address;       //  
    private Date reg_dt;  //  
    private String  txid;  //  
    private String  amount;  //  
}

