package com.onetop.webadmin.models.member;

import lombok.Data;

@Data
public class WebMemberMainReferer {
    private int CNT_REFERER;    //  추천 인원수
    private int UCNT;           //  산하 총인원 수
    private int TOTAL_AMT;      //  산하 총 매출
}

