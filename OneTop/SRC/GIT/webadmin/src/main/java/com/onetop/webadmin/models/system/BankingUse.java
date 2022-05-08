package com.onetop.webadmin.models.system;

import lombok.Data;

@Data
public class BankingUse {

    private int coin_no;
    private String coin_name;
    private String banking_deposit_use;
    private String banking_withdraw_use;
    private String banking_list_use;
    private String banking_invest_use;
    private String banking_recovery_use;

}
