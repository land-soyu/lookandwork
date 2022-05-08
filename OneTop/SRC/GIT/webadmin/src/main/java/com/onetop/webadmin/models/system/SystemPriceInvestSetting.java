package com.onetop.webadmin.models.system;

import lombok.Data;

import java.util.List;

@Data
public class SystemPriceInvestSetting {

    private List<SystemPriceSetting> price;
    private SystemInvestSetting invest;

}
