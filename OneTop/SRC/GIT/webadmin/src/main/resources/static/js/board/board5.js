$(document).ready(function() {

    // nav에 해당 메뉴에 색 변경
    $('#li_board > a').addClass('on');

    // QNA 검색 버튼 이벤트
    $('#search_board5_btn').bind('click', function () {

        $('#page').val('1');
        $('#search_board5_form').submit();
        return false;
    });

    $('#searchListCount').on('change', function () {

        $('#page').val('1');
        $('#search_board5_form').submit();
        return false;
    });
});
