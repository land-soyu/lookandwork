package com.onetop.webadmin.responses.monitoring;

import lombok.Data;

import java.util.List;

import com.onetop.webadmin.models.monitoring.CoinInvest;

@Data
public class MonitoringResponse {

    // 회원현황
    private int yesNewUsers;
    private int nowNewUsers;
    private int totalUsers;
    private int S0User;
    private int S1User;
    private int S2User;
    private int S3User;
    private int S4User;
    private int S5User;

    // 회원자산 / 투자 현황
    private List<CoinInvest> coinInvestList;

    // 입출금 현황
    private int yesDepositCount;
    private double yesDepositAmount;
    private int yesWithdrawCount;
    private double yesWithdrawAmount;

    private int nowDepositCount;
    private double nowDepositAmount;
    private int nowWithdrawCount;
    private double nowWithdrawAmount;

    private int totalNotWithdrawCount;
    private int totalNotWithdrawAmount;

    // 투자 현황
    private int yesInvestCount;
    private double yesInvestAmount;

    // 투자 현황
    private int nowInvestCount;
    private double nowInvestAmount;
}