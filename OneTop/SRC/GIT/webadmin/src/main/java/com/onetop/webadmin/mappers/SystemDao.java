package com.onetop.webadmin.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import com.onetop.webadmin.models.system.*;

@Repository
public class SystemDao {


    @Autowired
    @Qualifier("namedParameterWebAdminJdbcTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public List<SystemFee> GetSystemFee(){
        String systemFeeQuery = "SELECT invest_fee, recover_max30, recover_min30 FROM coinmarketing.system_fee";
        return namedParameterJdbcTemplate.query(systemFeeQuery, new MapSqlParameterSource(), new BeanPropertyRowMapper<>(SystemFee.class));
    }


    @Transactional(rollbackFor = Exception.class)
    public void UpdateSystemFee(SystemFee updateRequest){

        String updateFeeQuery = "UPDATE coinmarketing.system_fee SET invest_fee=:invest_fee, recover_max30=:recover_max30, recover_min30=:recover_min30";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("invest_fee", updateRequest.getInvest_fee());
        mapSqlParameterSource.addValue("recover_max30", updateRequest.getRecover_max30());
        mapSqlParameterSource.addValue("recover_min30", updateRequest.getRecover_min30());
        namedParameterJdbcTemplate.update(updateFeeQuery, mapSqlParameterSource);
    }


    public NowPrice GetNowPriceETH(){
        String nowPriceQuery = "SELECT coin_name, krw_price, usd_price FROM coinmarketing.now_price where coin_name='ETH'";
        return namedParameterJdbcTemplate.queryForObject(nowPriceQuery, new MapSqlParameterSource(), new BeanPropertyRowMapper<>(NowPrice.class));
    }


    public NowPrice GetNowPriceBTC(){
        String nowPriceQuery = "SELECT coin_name, krw_price, usd_price FROM coinmarketing.now_price where coin_name='BTC'";
        return namedParameterJdbcTemplate.queryForObject(nowPriceQuery, new MapSqlParameterSource(), new BeanPropertyRowMapper<>(NowPrice.class));
    }

    public NowPrice GetNowPriceLSR(){
        String nowPriceQuery = "SELECT coin_name, krw_price, usd_price FROM coinmarketing.now_price where coin_name='LSR'";
        return namedParameterJdbcTemplate.queryForObject(nowPriceQuery, new MapSqlParameterSource(), new BeanPropertyRowMapper<>(NowPrice.class));
    }


    public List<SystemInvestSetting> GetInvestSetting(){
        String investSettingQuery = "SELECT ratio_auto, manual_value, reinvest_amount, retrieve_min, invest_min, extrapay_rate, target_invest_rate FROM coinmarketing.system_invest_setting";
        return namedParameterJdbcTemplate.query(investSettingQuery, new MapSqlParameterSource(), new BeanPropertyRowMapper<>(SystemInvestSetting.class));
    }


    public SystemPriceSetting GetPriceSetting(){
        String investSettingQuery = "SELECT idx, coin_name, ratio_auto, manual_value FROM coinmarketing.system_price_setting where coin_name='LST'";
        return namedParameterJdbcTemplate.queryForObject(investSettingQuery, new MapSqlParameterSource(), new BeanPropertyRowMapper<>(SystemPriceSetting.class));
    }

    public SystemPriceSetting GetPriceSettingLSR(){
        String investSettingQuery = "SELECT idx, coin_name, ratio_auto, manual_value FROM coinmarketing.system_price_setting where coin_name='LSR'";
        return namedParameterJdbcTemplate.queryForObject(investSettingQuery, new MapSqlParameterSource(), new BeanPropertyRowMapper<>(SystemPriceSetting.class));
    }


    @Transactional(rollbackFor = Exception.class)
    public void UpdateInvestSettingAuto(SystemInvestSetting updateRequest){
        String updateFeeQuery = "UPDATE coinmarketing.system_invest_setting SET reinvest_amount=:reinvest_amount, retrieve_min=:retrieve_min, invest_min=:invest_min, extrapay_rate=:extrapay_rate, target_invest_rate=:target_invest_rate";

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("reinvest_amount", updateRequest.getReinvest_amount());
        mapSqlParameterSource.addValue("retrieve_min", updateRequest.getRetrieve_min());
        mapSqlParameterSource.addValue("invest_min", updateRequest.getInvest_min());
        mapSqlParameterSource.addValue("extrapay_rate", updateRequest.getExtrapay_rate());
        mapSqlParameterSource.addValue("target_invest_rate", updateRequest.getTarget_invest_rate());
        namedParameterJdbcTemplate.update(updateFeeQuery, mapSqlParameterSource);
    }


    @Transactional(rollbackFor = Exception.class)
    public void UpdatePriceSettingAuto(List<SystemPriceSetting> updateRequest){
        String updateFeeQuery = "UPDATE coinmarketing.system_price_setting SET ratio_auto=:ratio_auto, manual_value=:manual_value where idx=:idx";

        for(SystemPriceSetting item : updateRequest) {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("ratio_auto", item.getRatio_auto());
            mapSqlParameterSource.addValue("manual_value", item.getManual_value());
            mapSqlParameterSource.addValue("idx", item.getIdx());
            namedParameterJdbcTemplate.update(updateFeeQuery, mapSqlParameterSource);
        }
    }


    public List<SystemWithdrawFee> GetWithdrawFee(){
        String withdrawFeeQuery = "SELECT * FROM coinmarketing.system_withdraw_fee";
        return namedParameterJdbcTemplate.query(withdrawFeeQuery, new MapSqlParameterSource(), new BeanPropertyRowMapper<>(SystemWithdrawFee.class));
    }


    @Transactional(rollbackFor = Exception.class)
    public void UpdateWithdrawFee(List<SystemWithdrawFee> updateRequest){
        String updateFeeQuery = "UPDATE coinmarketing.system_withdraw_fee SET max30=:max30, min30=:min30 where coinname=:coinname";
        for(SystemWithdrawFee item : updateRequest){
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("max30", item.getMax30());
            mapSqlParameterSource.addValue("min30", item.getMin30());
            mapSqlParameterSource.addValue("coinname", item.getCoinname());
            namedParameterJdbcTemplate.update(updateFeeQuery, mapSqlParameterSource);
        }
    }


    public List<BankingUse> GetBankingUse(){
        String bankingUseQuery = "SELECT coin_no, coin_name, banking_deposit_use, banking_withdraw_use, banking_list_use, banking_invest_use, banking_recovery_use FROM coinmarketing.coin";
        return namedParameterJdbcTemplate.query(bankingUseQuery, new MapSqlParameterSource(), new BeanPropertyRowMapper<>(BankingUse.class));
    }


    @Transactional(rollbackFor = Exception.class)
    public void UpdateBankingUse(List<BankingUse> updateRequest){
        String updateUseQuery = "UPDATE coinmarketing.coin SET banking_deposit_use = :banking_deposit_use, banking_withdraw_use = :banking_withdraw_use, banking_list_use = :banking_list_use, banking_invest_use = :banking_invest_use, banking_recovery_use = :banking_recovery_use  WHERE coin_name = :coin_name";
        for(BankingUse item : updateRequest){
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("banking_deposit_use", item.getBanking_deposit_use());
            mapSqlParameterSource.addValue("banking_withdraw_use", item.getBanking_withdraw_use());
            mapSqlParameterSource.addValue("banking_list_use", item.getBanking_list_use());
            mapSqlParameterSource.addValue("banking_invest_use", item.getBanking_invest_use());
            mapSqlParameterSource.addValue("banking_recovery_use", item.getBanking_recovery_use());
            mapSqlParameterSource.addValue("coin_name", item.getCoin_name());
            namedParameterJdbcTemplate.update(updateUseQuery, mapSqlParameterSource);
        }
    }


    /** 기본수당 조회 */
    public List<BasicExtraPay> getBasicExtraPay(){
        String sQuery = "SELECT idx, classification, minimum, maximum, paymentrate FROM coinmarketing.system_basicextrapay ORDER BY classification";
        return namedParameterJdbcTemplate.query(sQuery, new MapSqlParameterSource(), new BeanPropertyRowMapper<>(BasicExtraPay.class));
    }


    /** 기본수당 입력 */
    @Transactional(rollbackFor = Exception.class)
    public void updateBasicExtraPay(ArrayList<BasicExtraPay> basicExtraPays){
        String sQuery = "UPDATE coinmarketing.system_basicextrapay SET minimum =:minimum, maximum=:maximum, paymentrate=:paymentrate WHERE idx=:idx";
        for(BasicExtraPay item : basicExtraPays){
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("idx", item.getIdx());
            mapSqlParameterSource.addValue("minimum", item.getMinimum());
            mapSqlParameterSource.addValue("maximum", item.getMaximum());
            mapSqlParameterSource.addValue("paymentrate", item.getPaymentrate());
            namedParameterJdbcTemplate.update(sQuery, mapSqlParameterSource);
        }
    }


    /** 후원수당 조회 */
    public List<SponsorExtraPay> getSponsorExtraPay(){
        String sQuery = "SELECT idx, paymentrate FROM coinmarketing.system_sponsorextrapay ORDER BY idx";
        return namedParameterJdbcTemplate.query(sQuery, new MapSqlParameterSource(), new BeanPropertyRowMapper<>(SponsorExtraPay.class));
    }


    /** 후원수당 입력 */
    @Transactional(rollbackFor = Exception.class)
    public void updateSponsorExtraPay(ArrayList<SponsorExtraPay> sponsorExtraPay){
        String sQuery = "UPDATE coinmarketing.system_sponsorextrapay SET paymentrate=:paymentrate WHERE idx=:idx";
        for(SponsorExtraPay item : sponsorExtraPay){
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("idx", item.getIdx());
            mapSqlParameterSource.addValue("paymentrate", item.getPaymentrate());
            namedParameterJdbcTemplate.update(sQuery, mapSqlParameterSource);
        }
    }


    /** 장려수당 조회 */
    public List<EncourageExtraPay> getEncourageExtraPay(){
        String sQuery = "SELECT idx, group_num, paymentrate FROM coinmarketing.system_encourageextrapay ORDER BY idx";
        return namedParameterJdbcTemplate.query(sQuery, new MapSqlParameterSource(), new BeanPropertyRowMapper<>(EncourageExtraPay.class));
    }


    /** 장려수당 입력 */
    @Transactional(rollbackFor = Exception.class)
    public void updateEncourageExtraPay(ArrayList<EncourageExtraPay> encourageExtraPay){
        String sQuery = "UPDATE coinmarketing.system_encourageextrapay SET paymentrate=:paymentrate WHERE idx=:idx";
        for(EncourageExtraPay item : encourageExtraPay){
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("idx", item.getIdx());
            mapSqlParameterSource.addValue("paymentrate", item.getPaymentrate());
            namedParameterJdbcTemplate.update(sQuery, mapSqlParameterSource);
        }
    }


    /** 직급수당 조회 */
    public List<RankExtraPay> getRankExtraPay(){
        String sQuery = "SELECT idx, rank, investtoltal, step1_count, five_high_count, t1_line, t1_count, t2_line, t2_count, t3_line, t3_count, t4_line, t4_count, standard, paymentrate FROM coinmarketing.system_rankextrapay";
        return namedParameterJdbcTemplate.query(sQuery, new MapSqlParameterSource(), new BeanPropertyRowMapper<>(RankExtraPay.class));
    }


    /** 직급수당 입력 */
    @Transactional(rollbackFor = Exception.class)
    public void updateRankExtraPay(ArrayList<RankExtraPay> randeExtraPay){
        String sQuery = "UPDATE coinmarketing.system_rankextrapay SET investtoltal=:investtoltal, five_high_count=:five_high_count, paymentrate=:paymentrate WHERE idx=:idx";
        for(RankExtraPay item : randeExtraPay){
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("idx", item.getIdx());
            mapSqlParameterSource.addValue("investtoltal", item.getInvesttoltal());
            mapSqlParameterSource.addValue("five_high_count", item.getFive_high_count());
            mapSqlParameterSource.addValue("paymentrate", item.getPaymentrate());
            namedParameterJdbcTemplate.update(sQuery, mapSqlParameterSource);
        }
    }
}