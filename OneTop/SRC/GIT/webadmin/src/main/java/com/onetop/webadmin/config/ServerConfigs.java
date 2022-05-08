package com.onetop.webadmin.config;

import java.util.ArrayList;
import java.util.List;

public class ServerConfigs {
    public final static String SHA256_KEY = "#binaries*597213";
    public final static String WITHDRAW_KRW_SHA256_KEY = "#bimax*5154485";
    public final static String DEPOSIT_KRW_HIGH_SHA256_KEY = "#binaries*702000";
    public final static int STATIC_DATA_VALID_SECS = 300;          // 300 초 = 5분

    public final static List<String> CHANNELS = new ArrayList<String>(){
        {
            add("WEB");
            add("APP");
            add("API");
        }
    };
}