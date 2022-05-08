package com.onetop.webadmin.models.member;

import lombok.Data;

import java.util.Date;

@Data
public class MemberWalletAddress {

    private String mb_id;
    private String w_coin_type;
    private String w_addr;
    private Date created_at;
    private String deposit_memo;

}
