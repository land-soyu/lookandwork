$(document).ready(function() {

    // nav에 해당 메뉴에 색 변경
    $('#li_bank > a').addClass('on');

    //TxID 툴팁에 넣기
    $('.txid').each(function(){
        var $text = $(this).text();
        $(this).next().text($text);
    });

    // 검색 버튼 이벤트
    $('#search_bank2_btn').bind('click', function () {
        $('#page').val('1');
        $('#search_bank2_form').submit();
        return false;
    });

    $('#searchListCount').on('change', function () {

        $('#page').val('1');
        $('#search_bank2_form').submit();
        return false;
    });
});



// 코인 필터 버튼 이벤트
function onFilterClick(obj){
    var checked_cnt = 0;

    if(obj.id === 'seeAll') {
        if(obj.checked) {
            for (var i = 0; i < coinlist.length; i++) {
                var targetId = 'coin_' + coinlist[i].coin_no;
                document.getElementById(targetId).checked = false;
            }
        }
        else{
            for (var i = 0; i < coinlist.length; i++) {

                var targetId = 'coin_' + coinlist[i].coin_no;
                if(document.getElementById(targetId).checked) checked_cnt++;
            }
            if(checked_cnt <= 0){
                document.getElementById('seeAll').checked = true;
            }
        }
    }
    else
    {
        for (var i = 0; i < coinlist.length; i++) {

            var targetId = 'coin_' + coinlist[i].coin_no;
            if(document.getElementById(targetId).checked) checked_cnt++;
        }
        if(checked_cnt > 0)
            document.getElementById('seeAll').checked = false;
        else
            document.getElementById('seeAll').checked = true;
    }
}

$(function(){
    if($('#req_dt').attr('value') != undefined){
        var sList = $('#req_dt').attr('value').split("-");
        var dList = [];
        sList.forEach(function(item) {
            if(item.length > 0) {
                dList.push(new Date(item.trim().replace(/\./g, '-')));
            }
        });
        $('#req_dt').datepicker().data('datepicker').selectDate(dList);
    }

    $('#req_dt').datepicker({
        autoClose : true,										// 날짜 선택하면 자동으로 닫힘
        clearButton : true,
        todayButton : new Date(),								// 오늘 선택 버튼
        maxDate : new Date(),									// 최대 선택 날짜 범위(today)

        onSelect: function(formattedDate, date, picker) {		// 범위 1년 이상 선택 시 alert 띄우고 초기화
            if(!date || date.length < 2)
                return;

            var timespan = date[1] - date[0];
            var diffDays = timespan / (24 * 60 * 60 * 1000 * 365);

            if(diffDays > 1) {
                alert(DATEPICKER_ALERT_1);
                picker.clear();
                return;
            }
        }
    });

    /** Excel D/L click */
    $("#bank2_excel_down_btn").click(function() {
        var f = document.search_bank2_form;
        var params = get_form_options(f);

        if (Number(params.total) > 3000) {

            alert(ALERT_EXCELDOWNLOAD_ERR_MSG_1);
            return;
        }

        post_to_url("bank2/excelDown", "_blank", params, "post");
    });


});

function orderBy(clicked_id){
    var f = document.search_bank2_form;

    if(f.sortName.value == clicked_id){
        if(f.sortOrderBy.value == "DESC"){
            f.sortOrderBy.value = "ASC";
        }
        else{
            f.sortOrderBy.value = "DESC";
        }
    }
    else{
        f.sortName.value = clicked_id;
        f.sortOrderBy.value = "DESC";
    }

    f.submit();
}

// coinrequest 메일로 발송
function sendMailCoinRequest (req_id, mb_id) {

    if (confirm(SENDMAIL_WITHDRAW)) {

        $.ajax({
            url: 'bank_mailsend_proc',
            type: 'POST',
            async: false,
            data: {'req_id' : req_id, 'mb_id' : mb_id},
            success: function (data) {

                if (data == 'success') {

                    alert(SENDMAILSUCCESS);
                }
            }
        });
    }
}
