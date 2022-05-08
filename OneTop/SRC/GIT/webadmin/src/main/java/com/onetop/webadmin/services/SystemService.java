package com.onetop.webadmin.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import com.onetop.webadmin.controllers.AuthorityController;
import com.onetop.webadmin.mappers.SystemDao;
import com.onetop.webadmin.models.system.BasicExtraPay;
import com.onetop.webadmin.models.system.EncourageExtraPay;
import com.onetop.webadmin.models.system.RankExtraPay;
import com.onetop.webadmin.models.system.SponsorExtraPay;

@Service
public class SystemService {


    private static final Logger log = LoggerFactory.getLogger(AuthorityController.class);


    @Autowired
    SystemDao systemDao;


    public String updateBasicextraPay(ArrayList<BasicExtraPay> basicExtraPays){
        String result = "";
        try {
            systemDao.updateBasicExtraPay(basicExtraPays);
            result = "success";
        }  catch (Exception e) {
            log.error("Fail to update BasicextraPay, msg = " + e.getMessage());
        }
        return result;
    }


    public String updateSponsorExtraPay(ArrayList<SponsorExtraPay> sponsorExtraPays){
        String result = "";
        try {
            systemDao.updateSponsorExtraPay(sponsorExtraPays);
            result = "success";
        }  catch (Exception e) {
            log.error("Fail to update SponsorextraPay, msg = " + e.getMessage());
        }
        return result;
    }


    public String updateEncourageExtraPay(ArrayList<EncourageExtraPay> encourageExtraPay){
        String result = "";
        try {

            systemDao.updateEncourageExtraPay(encourageExtraPay);
            result = "success";
        }  catch (Exception e) {
            log.error("Fail to update EncourageExtraPay, msg = " + e.getMessage());
        }
        return result;
    }


    public String updateRankExtraPay(ArrayList<RankExtraPay> rankExtraPay){
        String result = "";
        try {
            systemDao.updateRankExtraPay(rankExtraPay);
            result = "success";
        }  catch (Exception e) {
            log.error("Fail to update RankExtraPay, msg = " + e.getMessage());
        }
        return result;
    }
}