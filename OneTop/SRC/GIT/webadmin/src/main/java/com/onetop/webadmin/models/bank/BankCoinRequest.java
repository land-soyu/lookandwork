package com.onetop.webadmin.models.bank;

import java.math.BigDecimal;
import java.util.Date;

import com.onetop.webadmin.util.Utils;

import org.apache.poi.hpsf.Decimal;

public class BankCoinRequest {
    private int od_id;
    private String txid;
    private String status;
    private String action;
    private BigDecimal od_receipt_coin;
    private BigDecimal od_request_coin;
    private BigDecimal od_request_fee;
    private String od_request_address;
    private String deposit_memo;
    private String withdraw_memo;
    private int coin_flag;
    private String coin_name;
    private int mb_no;
    private String mb_id;
    private String od_reg_ip;
    private String user_confirm_yn;
    private Date user_confirm_dt;
    private String withdraw_confirm_yn;
    private Date confirm_dt;
    private String admin_id;
    private String reject;
    private BigDecimal tx_fee;
    private String error_message;
    private int confirm_cnt;
    private Date reg_dt;
    private Date admin_confirm_dt;

    private int cert_cnt;

    private Date mb_email_confirm_dt;
    private Date mb_hp_confirm_dt;
    private Date otp_reg_dt;
    private String mb_idcard_status;
    private String mb_use_language;
    private String channel;

    private String base_dir;
    private String attach_name;

    private String bank_code;
    private String bank_account;

    private String krw_deposit_matching_yn;

    private Date KST_reg_dt;
    private Date KST_confirm_dt;

    private int idx;
    private Date create_dt;
    private Date complete_dt;
    private int amount;
    private BigDecimal coin_amount;
    private BigDecimal quote_amount;
    private BigDecimal deposit_amount;
    private String address;

    private BigDecimal extra_amount;
    private String reason; 
    private String mg_id;
    private String is_handwriting;
    
    public String getIs_handwriting() {
        return is_handwriting;
    }
    public void setIs_handwriting(String is_handwriting) {
        this.is_handwriting = is_handwriting;
    }

    public BigDecimal getExtra_amount() {
        return extra_amount;
    }
    public void setExtra_amount(BigDecimal extra_amount) {
        this.extra_amount = extra_amount;
    }

    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getMg_id() {
        return mg_id;
    }
    public void setMg_id(String mg_id) {
        this.mg_id = mg_id;
    }

    public int getIdx() {
        return idx;
    }
    public void setIdx(int idx) {
        this.idx = idx;
    }

    public Date getCreate_dt() {
        return create_dt;
    }
    public void setCreate_dt(Date create_dt) {
        this.create_dt = create_dt;
    }

    public Date getComplete_dt() {
        return complete_dt;
    }
    public void setComplete_dt(Date complete_dt) {
        this.complete_dt = complete_dt;
    }

    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }

    public BigDecimal getCoin_amount() {
        return coin_amount;
    }
    public void setCoin_amount(BigDecimal coin_amount) {
        this.coin_amount = coin_amount;
    }

    public BigDecimal getQuote_amount() {
        return quote_amount;
    }
    public void setQuote_amount(BigDecimal quote_amount) {
        this.quote_amount = quote_amount;
    }

    public BigDecimal getDeposit_amount() {
        return deposit_amount;
    }
    public void setDeposit_amount(BigDecimal deposit_amount) {
        this.deposit_amount = deposit_amount;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }



    public int getCert_cnt() {
        return cert_cnt;
    }

    public void setCert_cnt(int cert_cnt) {
        this.cert_cnt = cert_cnt;
    }

    public Date getMb_email_confirm_dt() {
        return mb_email_confirm_dt;
    }

    public void setMb_email_confirm_dt(Date mb_email_confirm_dt) {
        this.mb_email_confirm_dt = mb_email_confirm_dt;
    }

    public Date getMb_hp_confirm_dt() {
        return mb_hp_confirm_dt;
    }

    public void setMb_hp_confirm_dt(Date mb_hp_confirm_dt) {
        this.mb_hp_confirm_dt = mb_hp_confirm_dt;
    }

    public Date getOtp_reg_dt() {
        return otp_reg_dt;
    }

    public void setOtp_reg_dt(Date otp_reg_dt) {

        this.otp_reg_dt = otp_reg_dt;
    }

    public String getMb_idcard_status() {
        return mb_idcard_status;
    }

    public void setMb_idcard_status(String mb_idcard_status) {
        this.mb_idcard_status = mb_idcard_status;
    }

    public int getOd_id() {
        return od_id;
    }

    public void setOd_id(int od_id) {
        this.od_id = od_id;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public BigDecimal getOd_receipt_coin() {
        return od_receipt_coin;
    }

    public void setOd_receipt_coin(BigDecimal od_receipt_coin) {
        this.od_receipt_coin = od_receipt_coin;
    }

    public BigDecimal getOd_request_coin() {
        return od_request_coin;
    }

    public void setOd_request_coin(BigDecimal od_request_coin) {
        this.od_request_coin = od_request_coin;
    }

    public BigDecimal getOd_request_fee() {
        return od_request_fee;
    }

    public void setOd_request_fee(BigDecimal od_request_fee) {
        this.od_request_fee = od_request_fee;
    }

    public String getOd_request_address() {
        return od_request_address;
    }

    public void setOd_request_address(String od_request_address) {
        this.od_request_address = od_request_address;
    }

    public String getDeposit_memo() {
        return deposit_memo;
    }

    public void setDeposit_memo(String deposit_memo) {
        this.deposit_memo = deposit_memo;
    }

    public String getWithdraw_memo() {
        return withdraw_memo;
    }

    public void setWithdraw_memo(String withdraw_memo) {
        this.withdraw_memo = withdraw_memo;
    }

    public int getCoin_flag() {
        return coin_flag;
    }

    public void setCoin_flag(int coin_flag) {
        this.coin_flag = coin_flag;
    }

    public String getCoin_name() {
        return coin_name;
    }

    public void setCoin_name(String coin_name) {
        this.coin_name = coin_name;
    }

    public int getMb_no() {
        return mb_no;
    }

    public void setMb_no(int mb_no) {
        this.mb_no = mb_no;
    }

    public String getMb_id() {
        return mb_id;
    }

    public void setMb_id(String mb_id) {
        this.mb_id = mb_id;
    }

    public String getOd_reg_ip() {
        return od_reg_ip;
    }

    public void setOd_reg_ip(String od_reg_ip) {
        this.od_reg_ip = od_reg_ip;
    }

    public String getUser_confirm_yn() {
        return user_confirm_yn;
    }

    public void setUser_confirm_yn(String user_confirm_yn) {
        this.user_confirm_yn = user_confirm_yn;
    }

    public Date getUser_confirm_dt() {
        return user_confirm_dt;
    }

    public void setUser_confirm_dt(Date user_confirm_dt) {
        this.user_confirm_dt = user_confirm_dt;
    }

    public String getWithdraw_confirm_yn() {
        return withdraw_confirm_yn;
    }

    public void setWithdraw_confirm_yn(String withdraw_confirm_yn) {
        this.withdraw_confirm_yn = withdraw_confirm_yn;
    }

    public Date getConfirm_dt() {
        return confirm_dt;
    }

    public void setConfirm_dt(Date confirm_dt) {
        this.confirm_dt = confirm_dt;

        if (confirm_dt != null) this.KST_confirm_dt = Utils.ConvertUTCtoKST(confirm_dt);
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }

    public String getReject() {
        return reject;
    }

    public void setReject(String reject) {
        this.reject = reject;
    }

    public BigDecimal getTx_fee() {
        return tx_fee;
    }

    public void setTx_fee(BigDecimal tx_fee) {
        this.tx_fee = tx_fee;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public int getConfirm_cnt() {
        return confirm_cnt;
    }

    public void setConfirm_cnt(int confirm_cnt) {
        this.confirm_cnt = confirm_cnt;
    }

    public Date getReg_dt() {
        return reg_dt;
    }

    public void setReg_dt(Date reg_dt) {
        this.reg_dt = reg_dt;

        if (reg_dt != null) this.KST_reg_dt = Utils.ConvertUTCtoKST(reg_dt);
    }

    public Date getAdmin_confirm_dt() {
        return admin_confirm_dt;
    }

    public void setAdmin_confirm_dt(Date admin_confirm_dt) {
        this.admin_confirm_dt = admin_confirm_dt;
    }

    public String getMb_use_language() {
        return mb_use_language;
    }

    public void setMb_use_language(String mb_use_language) {
        this.mb_use_language = mb_use_language;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getBase_dir() {
        return base_dir;
    }

    public void setBase_dir(String base_dir) {
        this.base_dir = base_dir;
    }

    public String getAttach_name() {
        return attach_name;
    }

    public void setAttach_name(String attach_name) {
        this.attach_name = attach_name;
    }

    public String getBank_code() {
        return bank_code;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    public String getBank_account() {
        return bank_account;
    }

    public void setBank_account(String bank_account) {
        this.bank_account = bank_account;
    }

    public String getKrw_deposit_matching_yn() {
        return krw_deposit_matching_yn;
    }

    public void setKrw_deposit_matching_yn(String krw_deposit_matching_yn) {
        this.krw_deposit_matching_yn = krw_deposit_matching_yn;
    }

    public Date getKST_reg_dt() {
        return KST_reg_dt;
    }

    public void setKST_reg_dt(Date KST_reg_dt) {
        this.KST_reg_dt = KST_reg_dt;
    }

    public Date getKST_confirm_dt() {
        return KST_confirm_dt;
    }

    public void setKST_confirm_dt(Date KST_confirm_dt) {
        this.KST_confirm_dt = KST_confirm_dt;
    }
}