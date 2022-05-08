package com.onetop.webadmin.models.member;

import lombok.Data;

@Data
public class MemberDownTree {

    private int mb_position;
    private int mb_count;
    private String coin_name;
    private String avail_total;
    private String mb_sort_info;

}
