$(document).ready(function() {

    // nav에 해당 메뉴에 색 변경
    $('#li_auth > a').addClass('on');

    $('#searchListCount').on('change', function () {

        $('#page').val('1');
        $('#searchForm').submit();
        return false;
    });
});

function deleteAdminMember() {

    var checkNo = '';

    $(':checkbox[name="checkNo"]:checked').each(function () {

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
            url: 'ajax/auth1_mod/delete',
            type: 'POST',
            data: {'checkNo': checkNo},
            async: false,
            success: function(data) {

                if(data == 'success') {

                    $('#popupArea').css('display', 'none');
                    location.href='auth1';
                }
            }
        });
    }
}

function searchMember() {

    $('#page').val('1');
    $('#searchForm').submit();
}