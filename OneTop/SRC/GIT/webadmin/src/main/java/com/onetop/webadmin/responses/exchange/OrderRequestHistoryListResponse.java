package com.onetop.webadmin.responses.exchange;

import java.util.ArrayList;
import java.util.List;

import com.onetop.webadmin.models.Counts;
import com.onetop.webadmin.models.MarketInfo;
import com.onetop.webadmin.models.exchange.OrderRequestHistory;
import com.onetop.webadmin.responses.PageNaviResponse;

public class OrderRequestHistoryListResponse {
    private List<OrderRequestHistory> orderRequestHistoryList;
    private List<String> coinList;
    private List<MarketInfo> marketList;


    private PageNaviResponse pageNaviResponse;

    private Counts counts;

    public OrderRequestHistoryListResponse(){
        this.orderRequestHistoryList = new ArrayList<>();
        this.coinList = new ArrayList<>();
        this.marketList = new ArrayList<>();
        this.counts = new Counts();
        pageNaviResponse = new PageNaviResponse();
    }

    public List<MarketInfo> getMarketList() {
        return marketList;
    }

    public void setMarketList(List<MarketInfo> marketList) {
        this.marketList = marketList;
    }

    public List<OrderRequestHistory> getOrderRequestHistoryList() {
        return orderRequestHistoryList;
    }

    public void setOrderRequestHistoryList(List<OrderRequestHistory> orderRequestHistoryList) {
        this.orderRequestHistoryList = orderRequestHistoryList;
    }

    public List<String> getCoinList() {
        return coinList;
    }

    public void setCoinList(List<String> coinList) {
        this.coinList = coinList;
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
