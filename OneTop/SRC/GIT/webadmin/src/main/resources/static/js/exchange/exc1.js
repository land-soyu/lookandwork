$(document).ready(function() {

    // nav에 해당 메뉴에 색 변경
    $('#li_exc > a').addClass('on');

    // 검색 버튼 이벤트
    $('#search_exc1_btn').bind('click', function () {
        $('#page').val('1');
        $('#search_exc1_form').submit();
        return false;
    });

    $('#searchListCount').on('change', function () {

        $('#page').val('1');
        $('#search_exc1_form').submit();
        return false;
    });
});


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
    },
    onHide: function(dp, animationCompleted){
        if(animationCompleted){
            if(dp.selectedDates.length === 1){
                dp.selectedDates[1] = dp.selectedDates[0];
                dp.selectDate(dp.selectedDates);
            }
        }
    }
});

/** Excel D/L click */
$("#exc1_excel_down_btn").click(function() {
    var f = document.search_exc1_form;
    var params = get_form_options(f);

    if (Number(params.total) > 3000) {

        alert(ALERT_EXCELDOWNLOAD_ERR_MSG_1);
        return;
    }

    post_to_url("exc1/excelDown", "_blank", params, "post");

    //makeExcelLoding('exc1/excelDown', params);
});

function orderBy(clicked_id){
    var f = document.search_exc1_form;

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

function showCancelPopup(od_id, od_price, od_amount, od_total){
    document.getElementById('cancel_od_id').innerText = od_id;
    document.getElementById('cancel_od_price').innerText = od_price;
    document.getElementById('cancel_od_amount').innerText = od_amount;
    document.getElementById('cancel_od_total').innerText = od_total;
    document.getElementById('cancelListPopup').style.display = 'block';
}

function showSignPopup(mb_id, action, ord_no, lb_od_amount, lb_remain_amount){
    var title = STR_SIGN_TITLE;
    if(action === 'bid'){
        title = '[' + STR_BID + '] ' + STR_SIGN_TITLE;
    }
    else if(action === 'ask'){
        title = '[' + STR_ASK + '] ' + STR_SIGN_TITLE;
    }
    document.getElementById('sign_title').innerText = title;

    document.getElementById('sign_od_amount').innerText = "";
    document.getElementById('sign_remain_amount').innerText = "";
    document.getElementById('dealListPopup').style.display = 'block';
    document.getElementById('sign_tbody').innerHTML = "";
    send_request(mb_id, ord_no, lb_od_amount, lb_remain_amount);
}


function send_request(mb_id, ord_no, lb_od_amount, lb_remain_amount){
    var params = {
        mb_id : mb_id,
        ord_no : ord_no
    };

    send_json_request('exc1_sign_detail', params, function(result){
        if(result !== "fail" && result !== "error"){
            var obj = JSON.parse(result);
            var rows = "";

            document.getElementById('sign_od_amount').innerText = numberWithPrecision(obj["orderHistory"].ord_amount, 8) + ' ' + lb_od_amount;
            document.getElementById('sign_remain_amount').innerText = numberWithPrecision((obj["orderHistory"].ord_amount - obj["orderHistory"].sign_amount), 8) + ' ' + lb_remain_amount;

            for(var i = 0; i < obj["signHistory"].length; i++){
                rows += "<tr>" +
                    " <td>" + obj["signHistory"][i].sign_no + "</td> "+
                    " <td>" + numberWithPrecision(obj["signHistory"][i].sign_price, 8) + "</td> "+
                    " <td>" + obj["signHistory"][i].market_name.split("/")[1] + "</td> "+
                    " <td>" + numberWithPrecision(obj["signHistory"][i].sign_amount, 8) + "</td> "+
                    " <td>" + obj["signHistory"][i].market_name.split("/")[0] + "</td> "+
                    " <td>" + numberWithPrecision(obj["signHistory"][i].sign_balance, 8) + "</td> "+
                    " <td>" + obj["signHistory"][i].market_name.split("/")[1] + "</td> "+
                    " <td>" + numberWithPrecision(obj["signHistory"][i].fee, 8) + "</td> ";

                if(obj["signHistory"][i].action ==='bid'){
                    rows += " <td>" + obj["signHistory"][i].market_name.split("/")[0] + "</td> ";
                }
                else if(obj["signHistory"][i].action ==='ask')
                {
                    rows += " <td>" + obj["signHistory"][i].market_name.split("/")[1] + "</td> ";
                }
                else{
                    rows += " <td></td> ";
                }

                rows += " </tr>";
            }
            document.getElementById('sign_tbody').innerHTML = rows;

            document.getElementById('dealListPopup').style.display = 'block';
        }
        else{
            alert(STR_REQUEST_FAIL);
        }
    });

}