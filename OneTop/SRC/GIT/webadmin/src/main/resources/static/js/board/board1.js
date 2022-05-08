$(document).ready(function() {

    // nav에 해당 메뉴에 색 변경
    $('#li_board > a').addClass('on');

    // 공지사항 검색 버튼 이벤트
    $('#search_notice_btn').bind('click', function () {

        $('#page').val('1');
        $('#search_notice_form').submit();
        return false;
    });

    $('#checkNo_all').on('click',function () {

        if ($(this).prop('checked') == true) {

            $('input[name="checkNo"]').prop('checked', true);
        } else {

            $('input[name="checkNo"]').prop('checked', false);
        }
    });

    $('input[name="checkNo"]').bind('click', function () {

        if ($(this).prop('checked') == false)
            $('#checkNo_all').prop('checked', false);
    });

    $('#searchListCount').on('change', function () {

        $('#page').val('1');
        $('#search_notice_form').submit();
        return false;
    });

    $('td.listCheck input[id^=checkView]').click(function () {
        var yn = "";
        if($(this).is(":checked")){
            if(confirm("메인에 게시 하시겠습니까?")) {
                yn="y";
            }else{
                $(this).prop("checked", false);
            }
        }else{
            if(confirm("게시를 해제 하시겠습니까?")) {
                yn="n";
            }else{
                $(this).prop("checked", true);
            }
        }
        if(yn!="") {
            $.ajax({
                url: "ajax/board1/mainview",
                type: 'POST',
                data: {'yn':yn, 'checkViewNo':$(this).val()},
                async: false,
                success: function (data) {
                    if (data == 'success') {
                        location.href='board1';
                    }
                }
            });
        }
    });
});

function deleteNotice() {

    var checkNo = '';

    $(':checkbox[name="checkNo"]:checked').each(function() {

        if (checkNo == '') {

            checkNo = $(this).val();
        } else {

            checkNo += ',' + $(this).val();
        }
    });

    if (checkNo == '') {

        alert(getMessage(1));
        return;
    }

    if (confirm(getMessage(2))) {

        $.ajax({
            url: 'ajax/board1/delete',
            type: 'POST',
            data: {'checkNo': checkNo},
            async: false,
            success: function (data) {

                if (data == 'success') {

                    location.href='board1';
                }
            }
        });
    }
}