package com.onetop.webadmin.models.authority;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

@Data
public class AdminMemberAuth implements Serializable {

    private ArrayList<String> adminid;

}
