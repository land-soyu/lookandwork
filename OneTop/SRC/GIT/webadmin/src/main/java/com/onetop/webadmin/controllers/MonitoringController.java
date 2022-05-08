package com.onetop.webadmin.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

import com.onetop.webadmin.models.monitoring.CoinInvest;
import com.onetop.webadmin.services.AdjustmentService;
import com.onetop.webadmin.services.MemberService;

import java.util.List;

@Controller
public class MonitoringController {

    private static final Logger log = LoggerFactory.getLogger(MonitoringController.class);


    @Autowired
    private AdjustmentService adjustmentService;

    @Autowired
    private MemberService memberService;


    @RequestMapping("/dashboard")
    public ModelAndView monitoring(HttpSession session){

        ModelAndView mv = new ModelAndView();

        List<CoinInvest> coin = adjustmentService.getInvestCoinInfo();
        for(CoinInvest item : coin) {
            memberService.getTotalAvailCoin().forEach(availCoin -> {
                if(item.getCoinName().equals(availCoin.getCoinName())) {
                    item.setWallet(availCoin.getWallet());
                }
            });
        }

        mv.addObject("coinInvestList", coin);
        mv.addObject("member_proc", session.getAttribute("member"));
        mv.addObject("yesNewUsers", memberService.getYesterDayResistMemberCount());
        mv.addObject("nowNewUsers", memberService.getTodayResistMemberCount());
        mv.addObject("monitoringCoinInfoList", adjustmentService.getMonitoringCoinInfo());
        mv.addObject("monitoringResponse", memberService.getMemberRankCount());

        mv.setViewName("monitoring/dashboard");
        return mv;
    }
}