package com.onetop.webadmin.services;

import com.onetop.webadmin.responses.PageNaviResponse;

public class PageService {

    /**
     * @brief       리스트에서 점프할 페이지 수\n
     * @details     \n
     */
    protected int DECADE_PAGE_SIZE = 10;

    /**
     * @brief       리스트에 표시할 페이지 수\n
     * @details     \n
     */
    protected int SIZE_PER_PAGE = 20;

    /**
     * @brief       회원 상테 페이지에 표시할 수\n
     * @details     \n
     */
    protected int SIZE_PER_PAGE_DETAIL = 10;
    /**
     * @brief                        리스트에서 하단의 리스트 네비게이션을 생성\n
     * @details                      \n
     * @param        total_count     총갯수\n
     * @param        page            페이지 번호\n
     * @param        size            리스트에 표시할 갯수\n
     */
    public PageNaviResponse getPageNavi(int total_count, int page, int size) {

        if (total_count == 0) {

            total_count = 1;
        }

        if (page == 0) {

            page = 1;
        }

        int firstPage = 1;
        int lastPage = total_count / size + 1;

        if (total_count % size == 0 && total_count > 0) lastPage--;
        if (page < firstPage) page = firstPage;
        if (page > lastPage) page = lastPage;

        int decadeFirst = page / DECADE_PAGE_SIZE * DECADE_PAGE_SIZE + 1;

        if (page % DECADE_PAGE_SIZE == 0) decadeFirst -= DECADE_PAGE_SIZE;

        int decadeLast = decadeFirst + DECADE_PAGE_SIZE - 1;

        if (decadeLast > lastPage) decadeLast = lastPage;

        boolean bPrev = decadeFirst == 1 ? false : true;
        boolean bNext = decadeLast == lastPage ? false : true;

        boolean bPrevDescade = decadeFirst <= 1 ? false : true;
        boolean bNextDescade = decadeLast <= (lastPage - DECADE_PAGE_SIZE) ? true : false;

        int startIndex = (page - 1) * size;

        PageNaviResponse pageNavi = new PageNaviResponse();
        pageNavi.setTotal_count(total_count);
        pageNavi.setCurrent_page(page);
        pageNavi.setLastPage(lastPage);

        if (bPrev) {

            pageNavi.setIsPrev(1);
        } else {

            pageNavi.setIsPrev(0);
        }

        if (bNext) {

            pageNavi.setIsNext(1);
        } else {

            pageNavi.setIsNext(0);
        }

        pageNavi.setDecadeFirst(decadeFirst);
        pageNavi.setDecadeLast(decadeLast);

        return pageNavi;
    }
}