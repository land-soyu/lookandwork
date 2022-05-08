package com.onetop.webadmin.models.bank;

import java.util.Date;

import com.onetop.webadmin.util.Utils;

public class BankMemberRequestOrderCsv {
    private int idx;
    private Date reg_dt;
    private String base_dir;
    private String attach_name;
    private int count;
    private int amount;

    private Date KST_reg_dt;

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public Date getReg_dt() {
        return reg_dt;
    }

    public void setReg_dt(Date reg_dt) {
        this.reg_dt = reg_dt;

        if (reg_dt != null) this.KST_reg_dt = Utils.ConvertUTCtoKST(reg_dt);
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getKST_reg_dt() {
        return KST_reg_dt;
    }

    public void setKST_reg_dt(Date KST_reg_dt) {
        this.KST_reg_dt = KST_reg_dt;
    }
}