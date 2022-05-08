package com.onetop.webadmin.models.member;

import lombok.Data;

import java.util.Date;

@Data
public class BlackConsumer {

    private int no;
    private String mb_id;
    private int state;
    private int type;
    private String type_memo;
    private String mb_blk_type;
    private Date reg_dt;
    private Date end_dt;
    private String reg_id;
    private String rel_id;
    private String memo;

    private String mb_no;
}
