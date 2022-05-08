package com.onetop.webadmin.models.member;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class WebMember {
    private int mb_no;
    private String mb_id;
    private String mb_name;
    private String mb_email;
    private String mb_phone;
    private int mb_level;
    private int cert_cnt;
    private String mb_use_language;
    private String mb_reg_path;
    private int web_visit_cnt;
    private int app_visit_cnt;
    private String mb_status;
    private BigDecimal mb_total_asset;
    private Date mb_reg_dt;
    private Date mb_last_login_dt;
    private Date mb_sms_auth_dt;

    private Date mb_email_confirm_dt;
    private Date otp_reg_dt;
    private int mb_position;

    private String country_code;
    //private String mb_sort_info;
    //private String mb_rank;
        
    private BigDecimal daily_bonus;       // 누적 데일리 보너스
    private BigDecimal referer_bonus;     // 누적 후원 보너스
    private BigDecimal matching_bonus;    // 누적 매칭 보너스
    private BigDecimal team_bonus;        // 누적 팀 보너스
    private BigDecimal apply_bonus;       // 누적 토탈 보너스
    private BigDecimal withdraw_bonus;    // 출금 가능 보너스

    //private int extrapay_limit;     // 지급 수당 제한

    //private int withdraw_limit;     // 출금 가능

    //private int total_referrer;         // 총 산하 추천인

    private String mb_referer;

    private String mb_sponsor;

    private BigDecimal buy_total_amount;    // 총 구매 총액


    private String mb_donate;
    private String amount_sum_withdraw;
    private String count_withdraw;
    private String amount_sum_buy;
    private String count_buy;
    
    public WebMember(){
        this.mb_total_asset = BigDecimal.ZERO;
    }
}

