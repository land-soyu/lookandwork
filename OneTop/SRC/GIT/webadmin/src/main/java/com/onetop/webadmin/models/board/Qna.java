package com.onetop.webadmin.models.board;

import lombok.Data;

import java.util.Date;

@Data
public class Qna {

    private int no;
    private String title;
    private Date reg_dt;
    private String member_id;
    private String content;
    private String state;

}
