package com.onetop.webadmin.models.bank;

import java.math.BigDecimal;
import java.util.Date;

import com.onetop.webadmin.util.Utils;

public class BankKrwTransaction {
    private int tr_no;
    private int req_id;
    private String mb_id;
    private String trans_desc;
    private String income_name;
    private Date reg_dt;
    private BigDecimal amount;
    private String matching_yn;
    private String is_point_pay_yn;
    private String account_memo;
    private String withdraw_bank_name;
    private String memo;
    private String is_repayment_yn;
    private Date repayment_dt;

    private Date KST_reg_dt;

    public int getTr_no() {
        return tr_no;
    }

    public void setTr_no(int tr_no) {
        this.tr_no = tr_no;
    }

    public int getReq_id() {
        return req_id;
    }

    public void setReq_id(int req_id) {
        this.req_id = req_id;
    }

    public String getMb_id() {
        return mb_id;
    }

    public void setMb_id(String mb_id) {
        this.mb_id = mb_id;
    }

    public String getTrans_desc() {
        return trans_desc;
    }

    public void setTrans_desc(String trans_desc) {
        this.trans_desc = trans_desc;
    }

    public String getIncome_name() {
        return income_name;
    }

    public void setIncome_name(String income_name) {
        this.income_name = income_name;
    }

    public Date getReg_dt() {
        return reg_dt;
    }

    public void setReg_dt(Date reg_dt) {
        this.reg_dt = reg_dt;

        if (reg_dt != null) this.KST_reg_dt = Utils.ConvertUTCtoKST(reg_dt);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMatching_yn() {
        return matching_yn;
    }

    public void setMatching_yn(String matching_yn) {
        this.matching_yn = matching_yn;
    }

    public String getIs_point_pay_yn() {
        return is_point_pay_yn;
    }

    public void setIs_point_pay_yn(String is_point_pay_yn) {
        this.is_point_pay_yn = is_point_pay_yn;
    }

    public String getAccount_memo() {
        return account_memo;
    }

    public void setAccount_memo(String account_memo) {
        this.account_memo = account_memo;
    }

    public String getWithdraw_bank_name() {
        return withdraw_bank_name;
    }

    public void setWithdraw_bank_name(String withdraw_bank_name) {
        this.withdraw_bank_name = withdraw_bank_name;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getKST_reg_dt() {
        return KST_reg_dt;
    }

    public void setKST_reg_dt(Date KST_reg_dt) {
        this.KST_reg_dt = KST_reg_dt;
    }

    public String getIs_repayment_yn() {
        return is_repayment_yn;
    }

    public void setIs_repayment_yn(String is_repayment_yn) {
        this.is_repayment_yn = is_repayment_yn;
    }

    public Date getRepayment_dt() {
        return repayment_dt;
    }

    public void setRepayment_dt(Date repayment_dt) {
        this.repayment_dt = repayment_dt;
    }
}