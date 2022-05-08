package com.onetop.webadmin.responses;

import org.json.simple.JSONObject;

public class PageNaviResponse {

    /**
     * @brief       점프 페이지 리스트 중 이전 점프 페이지 리스트 가능 여부\n
     * @details     \n
     */
    int isPrev;
    /**
     * @brief       점프 페이지 리스트 중 다음 점프 페이지 리스트 가능 여부\n
     * @details     \n
     */
    int isNext;
    /**
     * @brief       점프 페이지 리스트 중 시작할 페이지 번호\n
     * @details     \n
     */
    int decadeFirst;
    /**
     * @brief       점프 페이지 리스트 중 마지막 페이지 번호\n
     * @details     \n
     */
    int decadeLast;
    /**
     * @brief       전체 페이지 수\n
     * @details     \n
     */
    int lastPage;
    /**
     * @brief       현재 페이지\n
     * @details     \n
     */
    int current_page;
    /**
     * @brief       검색된 전체 게시물 수	\n
     * @details     \n
     */
    int total_count;

    public int getIsPrev() {

        return isPrev;
    }

    public void setIsPrev(int isPrev) {

        this.isPrev = isPrev;
    }

    public int getIsNext() {

        return isNext;
    }

    public void setIsNext(int isNext) {

        this.isNext = isNext;
    }

    public int getDecadeFirst() {

        return decadeFirst;
    }

    public void setDecadeFirst(int decadeFirst) {

        this.decadeFirst = decadeFirst;
    }

    public int getDecadeLast() {

        return decadeLast;
    }

    public void setDecadeLast(int decadeLast) {

        this.decadeLast = decadeLast;
    }

    public int getLastPage() {

        return lastPage;
    }

    public void setLastPage(int lastPage) {

        this.lastPage = lastPage;
    }

    public int getCurrent_page() {

        return current_page;
    }

    public void setCurrent_page(int current_page) {

        this.current_page = current_page;
    }

    public int getTotal_count() {

        return total_count;
    }

    public void setTotal_count(int total_count) {

        this.total_count = total_count;
    }

    /**
     * @brief       응답 데이터 읽어오기\n
     * @details     \n
     */
    public JSONObject getResponse() {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("isPrev", this.isPrev);
        jsonObject.put("isNext", this.isNext);
        jsonObject.put("decadeFirst", this.decadeFirst);
        jsonObject.put("decadeLast", this.decadeLast);
        jsonObject.put("lastPage", this.lastPage);
        jsonObject.put("current_page", this.current_page);
        jsonObject.put("total_count", this.total_count);

        return jsonObject;
    }
}