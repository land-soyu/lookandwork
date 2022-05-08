package com.onetop.webadmin.models.board;

import lombok.Data;

import java.util.Date;

@Data
public class QnaComment {

    private int no;
    private int qna_no;
    private String content;
    private String admin_id;
    private Date reg_dt;

}
