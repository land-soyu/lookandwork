package com.onetop.webadmin.responses.exchange;

import java.util.ArrayList;
import java.util.List;

import com.onetop.webadmin.models.Counts;
import com.onetop.webadmin.models.MarketInfo;
import com.onetop.webadmin.models.adjustment.MemberBalanceLog;
import com.onetop.webadmin.responses.PageNaviResponse;

public class SignHistoryListResponse {
    private List<MemberBalanceLog> signHistoryList;

    private List<String> coinList;
    private List<MarketInfo> marketList;


    private PageNaviResponse pageNaviResponse;

    private Counts counts;

    public SignHistoryListResponse(){
        this.signHistoryList = new ArrayList<>();
        this.coinList = new ArrayList<>();
        this.marketList = new ArrayList<>();
        this.counts = new Counts();
        pageNaviResponse = new PageNaviResponse();
    }

    public List<MemberBalanceLog> getSignHistoryList() {
        return signHistoryList;
    }

    public void setSignHistoryList(List<MemberBalanceLog> signHistoryList) {
        this.signHistoryList = signHistoryList;
    }

    public List<String> getCoinList() {
        return coinList;
    }

    public void setCoinList(List<String> coinList) {
        this.coinList = coinList;
    }

    public List<MarketInfo> getMarketList() {
        return marketList;
    }

    public void setMarketList(List<MarketInfo> marketList) {
        this.marketList = marketList;
    }

    public PageNaviResponse getPageNaviResponse() {
        return pageNaviResponse;
    }

    public void setPageNaviResponse(PageNaviResponse pageNaviResponse) {
        this.pageNaviResponse = pageNaviResponse;
    }

    public Counts getCounts() {
        return counts;
    }

    public void setCounts(Counts counts) {
        this.counts = counts;
    }
}