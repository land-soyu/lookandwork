package com.onetop.webadmin.models.authority;

import lombok.Data;

import java.util.Date;

@Data
public class AdminLog {

    private int no;
    private int type;
    private String content;
    private String id;
    private String ip;
    private Date reg_dt;

}
