package com.onetop.webadmin.models.board;

import lombok.Data;

import java.util.Date;

@Data
public class OperationMonitoringComment {

    private int no;
    private int qna_no;
    private String content;
    private String admin_id;
    private Date reg_dt;
    private String base_dir;
    private String attach_1;
    private String attach_2;
    private String attach_3;

}
