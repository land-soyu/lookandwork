package com.onetop.webadmin.responses.member;

import java.util.ArrayList;
import java.util.List;

import com.onetop.webadmin.models.Counts;
import com.onetop.webadmin.models.MarketInfo;
import com.onetop.webadmin.models.adjustment.MemberBalanceLog;
import com.onetop.webadmin.responses.PageNaviResponse;

public class MemberBalanceLogResponse {

    private List<MemberBalanceLog> memberBalanceLogList;
    private List<String> coinList;
    private List<String> assetCoinList;
    private List<MarketInfo> marketList;

    private PageNaviResponse pageNaviResponse;

    private Counts counts;

    public MemberBalanceLogResponse(){
        this.memberBalanceLogList = new ArrayList<>();
        this.assetCoinList = new ArrayList<>();
        this.coinList = new ArrayList<>();
        this.marketList = new ArrayList<>();
        this.counts = new Counts();
        pageNaviResponse = new PageNaviResponse();
    }

    public List<MemberBalanceLog> getMemberBalanceLogList() {
        return memberBalanceLogList;
    }

    public void setMemberBalanceLogList(List<MemberBalanceLog> memberBalanceLogList) {
        this.memberBalanceLogList = memberBalanceLogList;
    }

    public List<String> getCoinList() {
        return coinList;
    }

    public void setCoinList(List<String> coinList) {
        this.coinList = coinList;
    }

    public List<String> getAssetCoinList() {
        return assetCoinList;
    }

    public void setAssetCoinList(List<String> assetCoinList) {
        this.assetCoinList = assetCoinList;
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