package com.onetop.webadmin.responses.member;

import lombok.Data;

import java.util.List;

import com.onetop.webadmin.models.member.MemberLoginHis;
import com.onetop.webadmin.responses.PageNaviResponse;

/**
 * Created by kim young moon on 12/12/2018.
 */
@Data
public class MemberLoginHisResponse {
    /**
     * @brief    	회원 로그인 히스토리\n
     * @details	\n
     */
    private List<MemberLoginHis> memberLoginHisList;
    /**
     * @brief		리스트 네비게이션\n
     * @details    \n
     */
    private PageNaviResponse pageNaviResponse;
    /**
     * @brief		회원 로그인 히스토리 갯수\n
     * @details    \n
     */
    private int total_memberLoginHis_count;
}
