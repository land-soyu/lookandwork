package com.onetop.webadmin.models.board;

import lombok.Data;

import java.util.Date;

@Data
public class AdminQna {

    private int no;
    private int qna_type;
    private String title;
    private Date reg_dt;
    private String inquiry_id;
    private String inquiry_name;
    private String check_id;
    private String check_name;
    private int state;
    private Date mod_dt;
    private String content;
    private String base_dir;
    private String attach_1;
    private String attach_2;
    private String attach_3;

}
