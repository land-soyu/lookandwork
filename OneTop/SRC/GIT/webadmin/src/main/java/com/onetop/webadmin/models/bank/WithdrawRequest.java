package com.onetop.webadmin.models.bank;

import java.math.BigDecimal;
import java.util.Date;

import com.onetop.webadmin.util.Utils;

public class WithdrawRequest {
    private int rownum;
    private int idx;
    private int mb_no;
    private String status;
    private int amount;
    private BigDecimal fee;
    private String coin_name;
    private BigDecimal quote_amount;
    private String withdraw_address;
    private BigDecimal withdraw_amount;
    private String reject;
    private String txid;
    private Date create_dt;
    private Date update_dt;
    private int withdraw_batch_id;
    private int withdraw_exchange;

    private String mb_id;
    private String approve_id;

    private String return_reason;
    private BigDecimal return_amount;
    private String return_mg_id;
    private String mg_id;

    public int getRownum() {
        return this.rownum;
    }

    public void setRownum(int rownum) {
        this.rownum = rownum;
    }

    public int getIdx() {
        return this.idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getMg_id() {
        return mg_id;
    }

    public void setMg_id(String mg_id) {
        this.mg_id = mg_id;
    }

    public String getReturn_mg_id() {
        return return_mg_id;
    }

    public void setReturn_mg_id(String return_mg_id) {
        this.return_mg_id = return_mg_id;
    }

    public BigDecimal getReturn_amount() {
        return return_amount;
    }

    public void setReturn_amount(BigDecimal return_amount) {
        this.return_amount = return_amount;
    }

    public String getReturn_reason() {
        return return_reason;
    }

    public void setReturn_reason(String return_reason) {
        this.return_reason = return_reason;
    }

    public int getMb_no() {
        return this.mb_no;
    }

    public void setMb_no(int mb_no) {
        this.mb_no = mb_no;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public BigDecimal getFee() {
        return this.fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getCoin_name() {
        return this.coin_name;
    }

    public void setCoin_name(String coin_name) {
        this.coin_name = coin_name;
    }

    public BigDecimal getQuote_amount() {
        return this.quote_amount;
    }

    public void setQuote_amount(BigDecimal quote_amount) {
        this.quote_amount = quote_amount;
    }

    public String getWithdraw_address() {
        return this.withdraw_address;
    }

    public void setWithdraw_address(String withdraw_address) {
        this.withdraw_address = withdraw_address;
    }

    public BigDecimal getWithdraw_amount() {
        return this.withdraw_amount;
    }

    public void setWithdraw_amount(BigDecimal withdraw_amount) {
        this.withdraw_amount = withdraw_amount;
    }

    public String getReject() {
        return this.reject;
    }

    public void setReject(String reject) {
        this.reject = reject;
    }

    public String getTxid() {
        return this.txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public Date getCreate_dt() {
        return this.create_dt;
    }

    public void setCreate_dt(Date create_dt) {
        this.create_dt = create_dt;
    }

    public Date getUpdate_dt() {
        return this.update_dt;
    }

    public void setUpdate_dt(Date update_dt) {
        this.update_dt = update_dt;
    }

    public String getMb_id() {
        return this.mb_id;
    }

    public void setMb_id(String mb_id) {
        this.mb_id = mb_id;
    }

    public int getWithdraw_batch_id() {
        return this.withdraw_batch_id;
    }

    public void setWithdraw_batch_id(int withdraw_batch_id) {
        this.withdraw_batch_id = withdraw_batch_id;
    }

    public int getWithdraw_exchange() {
        return this.withdraw_exchange;
    }

    public void setWithdraw_exchange(int withdraw_exchange) {
        this.withdraw_exchange = withdraw_exchange;
    }
    public String getApprove_id() {
        return this.approve_id;
    }

    public void setApprove_id(String approve_id) {
        this.approve_id = approve_id;
    }

}