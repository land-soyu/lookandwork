package com.onetop.webadmin.models;

import lombok.Data;

@Data
public class Counts {

    private int total;
    private int search;

    public Counts(){
        this.total = 0;
        this.search = 0;
    }

}
