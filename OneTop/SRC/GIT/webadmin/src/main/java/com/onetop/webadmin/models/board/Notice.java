package com.onetop.webadmin.models.board;

import lombok.Data;

import java.util.Date;

@Data
public class Notice {

    private int no;
    private String title;
    private Date reg_dt;
    private Date mod_dt;
    private String admin_id;
    private String admin_name;
    private String is_secret;
    private String content;
    private int read_count;
    private String lang;
    private String main_view;

    private String type;

}
