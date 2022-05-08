package com.onetop.webadmin.responses.member;

import lombok.Data;

import java.util.List;

import com.onetop.webadmin.models.adjustment.ExtraPaySnapshotInfo;
import com.onetop.webadmin.responses.PageNaviResponse;

@Data
public class ExtraPaySnapshotResponse {
    private List<ExtraPaySnapshotInfo> extraPaySnapshotInfoList;
    private PageNaviResponse pageNaviResponse;
    private int total_mbBalanceInfoSnapshot_count;
}
