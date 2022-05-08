package com.onetop.webadmin.models.member;

import lombok.Data;

@Data
public class ReferrerIdUpdate {

    private String mb_id;
    private int mb_parent_id;
    private int position_self;
    private int position_parent;
    private String mb_sort_info_self;
    private String mb_sort_info_target;
    private String mb_sort_info_change;

}
