package com.onetop.webadmin.mappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import com.onetop.webadmin.config.ServerConfigs;
import com.onetop.webadmin.controllers.BankingController;
import com.onetop.webadmin.models.CoinInfo;
import com.onetop.webadmin.models.MarketInfo;
import com.onetop.webadmin.models.StaticDataManager;

@Repository
public class CoinDao {
    private static final Logger log = LoggerFactory.getLogger(BankingController.class);

    @Autowired
    @Qualifier("namedParameterWebAdminJdbcTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static StaticDataManager staticDataManager = new StaticDataManager();

    public List<CoinInfo> GetCoinList(){
        List<CoinInfo> coinList = new ArrayList<>();

        try{
            String sql = "SELECT coin_no, coin_name FROM coinmarketing.coin";

            coinList = (List<CoinInfo>) staticDataManager.GetStaticData("GetCoinList", ServerConfigs.STATIC_DATA_VALID_SECS);

            if(coinList == null){
                coinList =  namedParameterJdbcTemplate.query(sql,
                        new MapSqlParameterSource(),
                        new BeanPropertyRowMapper<>(CoinInfo.class));
                staticDataManager.UpdateStaticData("GetCoinList", coinList);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return coinList;
    }

    public List<MarketInfo> GetMarketNameList(){
        List<MarketInfo> marketList = new ArrayList<>();
        try{

            marketList = (List<MarketInfo>) staticDataManager.GetStaticData("GetMarketNameList", ServerConfigs.STATIC_DATA_VALID_SECS);

            if(marketList == null){
                String sql= "SELECT DISTINCT SUBSTRING_INDEX(market_name, '/', -1) as market FROM coinmarketing.market GROUP BY market";
                marketList =  namedParameterJdbcTemplate.query(sql,
                        new MapSqlParameterSource(),
                        new BeanPropertyRowMapper<>(MarketInfo.class));

                staticDataManager.UpdateStaticData("GetMarketNameList", marketList);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return marketList;
    }

    public List<String> GetMarketCoinList(){
        List<String> marketCoinList = new ArrayList<>();
        try{
            marketCoinList = (List<String>) staticDataManager.GetStaticData("GetMarketCoinList", ServerConfigs.STATIC_DATA_VALID_SECS);
            if(marketCoinList == null){
                String sql= "SELECT DISTINCT SUBSTRING_INDEX(market_name, '/', 1) as market FROM coinmarketing.market GROUP BY market";
                marketCoinList =  namedParameterJdbcTemplate.queryForList(sql,
                        new MapSqlParameterSource(),
                        String.class);

                staticDataManager.UpdateStaticData("GetMarketCoinList", marketCoinList);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return marketCoinList;
    }
}