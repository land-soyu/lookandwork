package com.onetop.webadmin.responses.member;

import java.util.ArrayList;
import java.util.List;

import com.onetop.webadmin.models.member.BlackConsumer;
import com.onetop.webadmin.responses.PageNaviResponse;

public class BlackConsumerResponse {

    /**
     * @brief    	블랙 컨슈머 리스트\n
     * @details	\n
     */
    private List<BlackConsumer> blackConsumerList;
    /**
     * @brief		리스트 네비게이션\n
     * @details    \n
     */
    private PageNaviResponse pageNaviResponse;
    /**
     * @brief		블랙 컨슈머 수\n
     * @details    \n
     */
    private int total_blackConsumer_count;

    public BlackConsumerResponse() {

        blackConsumerList = new ArrayList<BlackConsumer>();
        pageNaviResponse = new PageNaviResponse();
    }

    public List<BlackConsumer> getBlackConsumerList() {

        return blackConsumerList;
    }

    public void setBlackConsumerList(List<BlackConsumer> blackConsumerList) {

        this.blackConsumerList = blackConsumerList;
    }

    public PageNaviResponse getPageNaviResponse() {

        return pageNaviResponse;
    }

    public void setPageNaviResponse(PageNaviResponse pageNaviResponse) {

        this.pageNaviResponse = pageNaviResponse;
    }

    public int getTotal_blackConsumer_count() {

        return total_blackConsumer_count;
    }

    public void setTotal_blackConsumer_count(int total_blackConsumer_count) {

        this.total_blackConsumer_count = total_blackConsumer_count;
    }
}