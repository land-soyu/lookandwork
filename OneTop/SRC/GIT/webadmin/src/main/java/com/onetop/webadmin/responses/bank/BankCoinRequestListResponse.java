package com.onetop.webadmin.responses.bank;

import java.util.ArrayList;
import java.util.List;

import com.onetop.webadmin.models.CoinInfo;
import com.onetop.webadmin.models.Counts;
import com.onetop.webadmin.models.bank.BankCoinRequest;
import com.onetop.webadmin.models.bank.WithdrawRequest;
import com.onetop.webadmin.responses.PageNaviResponse;

public class BankCoinRequestListResponse {
    List<BankCoinRequest> bankCoinRequestList;
    List<WithdrawRequest> withdrawRequestList;
    List<WithdrawRequest> oldWithdrawRequestList;
    List<CoinInfo> coinList;

    private PageNaviResponse pageNaviResponse;

    private Counts counts;

    public BankCoinRequestListResponse(){
        this.bankCoinRequestList = new ArrayList<>();
        this.withdrawRequestList = new ArrayList<>();
        this.oldWithdrawRequestList = new ArrayList<>();
        this.coinList = new ArrayList<>();
        this.counts = new Counts();
        pageNaviResponse = new PageNaviResponse();
    }


    public Counts getCounts() {
        return counts;
    }

    public void setCounts(Counts counts) {
        this.counts = counts;
    }

    public List<BankCoinRequest> getBankCoinRequestList() {
        return bankCoinRequestList;
    }

    public void setBankCoinRequestList(List<BankCoinRequest> bankCoinRequestList) {
        this.bankCoinRequestList = bankCoinRequestList;
    }

    public List<WithdrawRequest> getWithdrawRequestList() {
        return withdrawRequestList;
    }

    public void setWithdrawRequestList(List<WithdrawRequest> withdrawRequestList) {
        this.withdrawRequestList = withdrawRequestList;
    }

    public List<WithdrawRequest> getOldWithdrawRequestList() {
        return oldWithdrawRequestList;
    }

    public void setOldWithdrawRequestList(List<WithdrawRequest> oldWithdrawRequestList) {
        this.oldWithdrawRequestList = oldWithdrawRequestList;
    }

    public List<CoinInfo> getCoinList() {
        return coinList;
    }

    public void setCoinList(List<CoinInfo> coinList) {
        this.coinList = coinList;
    }

    public PageNaviResponse getPageNaviResponse() {
        return pageNaviResponse;
    }

    public void setPageNaviResponse(PageNaviResponse pageNaviResponse) {
        this.pageNaviResponse = pageNaviResponse;
    }

}
