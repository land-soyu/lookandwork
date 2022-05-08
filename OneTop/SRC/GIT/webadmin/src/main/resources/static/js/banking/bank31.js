$(document).ready(function() {
    window.history.pushState(null, '', location.href);

    window.onpopstate = () => {
        history.go(1);
        handleGoback();
    }

    // nav에 해당 메뉴에 색 변경
    $('#li_bank > a').addClass('on');

    //TxID 툴팁에 넣기
    $('.txid').each(function(){
        var $text = $(this).text();
        $(this).next().text($text);
    });

    // 검색 버튼 이벤트
    $('#search_bank31_btn').bind('click', function () {
        $('#page').val('1');
        $('#search_bank31_form').submit();
        return false;
    });

    $('#searchListCount').on('change', function () {

        $('#page').val('1');
        $('#search_bank31_form').submit();
        return false;
    });
});

function handleGoback() {
    if ($('#contentView1').css('display') == 'none') {
        contentViewOnDisplay();
    } 
    if ($('#contentView2').css('display') == 'none') {
        history.go(-1);
    }
}

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
    $("#bank31_excel_down_btn").click(function() {
        var f = document.search_bank31_form;
        var params = get_form_options(f);

        if (Number(params.total) > 3000) {

            alert(ALERT_EXCELDOWNLOAD_ERR_MSG_1);
            return;
        }

        post_to_url("bank31/excelDown", "_blank", params, "post");
    });
    /** Excel D/L click */
    $("#bank313_excel_down_btn").click(function() {
        var f = document.search_bank31_form;
        var params = get_form_options(f);

        if (Number(params.total) > 3000) {

            alert(ALERT_EXCELDOWNLOAD_ERR_MSG_1);
            return;
        }

        post_to_url("bank31/excelDownAll", "_blank", params, "post");
    });


});

function orderBy(clicked_id){
    var f = document.search_bank31_form;

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


function popupRecord() {
    $('#recordPopup').css('display', 'block');
}
function popupApprove() {
    $('#approvePopup').css('display', 'block');
}
function toApprove() {
    var mb_id = document.getElementById("content2_member_no")._value;
    var request_address = document.getElementById("approvePopup_request_address").innerText;
    var amount = document.getElementById("approvePopup_amount")._value;
    var coin_name = document.getElementById("approvePopup_coin_name").innerText;

    $.ajax({
        url: 'withdraw_check_approve_proc',
        type: 'POST',
        async: false,
        data: {'idx' : mb_id},
        success: function (data) {
            if (data) {
                alert("이미 처리된 내용입니다");
                $('.popupArea').hide();
                var f = document.search_bank31_form;
                f.submit();

                contentViewOnDisplay();
            } else {
                approve(mb_id, request_address, coin_name, amount);
            }
        }
    });
}
function approve(idx, request_address, coin_name, amount) {    
    var mg_id = document.getElementById("mg_id").value;
    $.ajax({
        url: 'withdraw_approve_proc',
        type: 'POST',
        async: false,
        data: {'idx' : idx, 'request_address' : request_address, 'coin_name': coin_name, 'amount' : amount, 'mg_id' : mg_id},
        success: function (data) {

            switch (data) {
                case 'success':
                    alert("승인 완료");
                    $('.popupArea').hide();
    
                    var f = document.search_bank31_form;
                    f.submit();
    
                    contentViewOnDisplay();
                    break;
                case 'fail':
                    //approve(idx, request_address, coin_name, amount);
                    break;
            }
        }
    });
}
function toAllApprove(searchValue, all_amount, mg_id) {
    var request_address = document.getElementById("content3_withdraw_address").innerText;
    var coin_name = document.getElementById("content3_withdraw_coin_name").innerText;

    console.log("searchValue : "+searchValue);
    console.log("all_amount : "+all_amount);
    console.log("request_address : "+request_address);
    console.log("mg_id : "+mg_id);

    $.ajax({
        url: 'withdraw_check_approve_proc',
        type: 'POST',
        async: false,
        data: {'idx' : searchValue},
        success: function (data) {
            if (data) {
                alert("이미 처리된 내용입니다");
                $('.popupArea').hide();
                var f = document.search_bank31_form;
                f.submit();

                contentView3OffDisplay();
            } else {
                allApprove(searchValue, request_address, coin_name, all_amount, mg_id);
            }
        }
    });
}
function allApprove(searchValue, request_address, coin_name, all_amount, mg_id) {
    $.ajax({
        url: 'withdraw_approve_all_proc',
        type: 'POST',
        async: false,
        data: {'idx' : searchValue, 'request_address' : request_address, 'coin_name' : coin_name, 'amount' : all_amount, 'mg_id' : mg_id},
        success: function (data) {

            switch (data) {
                case 'success':
                    alert("승인 완료");
                    $('.popupArea').hide();
    
                    var f = document.search_bank31_form;
                    f.submit();
    
                    contentView3OffDisplay();
                    break;
                case 'fail':
                    //approve(mb_id, request_address, amount);
                    break;
            }
        }
    });
}

function popupExchange() {
    $('#exchangePopup').css('display', 'block');
}
function toExchange() {
    var inputdata = document.getElementById("withdrwa_request_address").value;
    var address = document.getElementById("content2_request_address").innerText;

    if (address == inputdata) {
        alert("지갑주소가 동일 합니다.");
        var mb_id = document.getElementById("content2_member_no")._value;

        $.ajax({
            url: 'withdraw_check_approve_proc',
            type: 'POST',
            async: false,
            data: {'idx' : mb_id},
            success: function (data) {
                if (data) {
                    alert("이미 처리된 내용입니다");
                    $('.popupArea').hide();
                    var f = document.search_bank31_form;
                    f.submit();
    
                    contentViewOnDisplay();
                } else {
                    exchange(mb_id);
                }
            }
        });
    } else {
        alert("지갑주소를 다시 확인해 주세요.");
    }
}
function exchange(mb_id) {
    var mg_id = document.getElementById("mg_id").value;
    $.ajax({
        url: 'withdraw_exchange_proc',
        type: 'POST',
        async: false,
        data: {'mb_id' : mb_id, 'mg_id' : mg_id},
        success: function (data) {
            if (data == 'success') {
                alert("거래소 출금 처리 완료");
                $('.popupArea').hide();

                var f = document.search_bank31_form;
                f.submit();

                contentViewOnDisplay();
            }
        }
    });
}
function popupAllExchange() {
    $('#allExchangePopup').css('display', 'block');
}
function toAllExchange(searchValue, mg_id) {
    var inputdata = document.getElementById("withdrwa_request_address_all").value;
    var address = document.getElementById("content3_withdraw_address").innerText;

    console.log("address : "+address);
    console.log("inputdata : "+inputdata);
    if (address == inputdata) {
        alert("지갑주소가 동일 합니다.");
        $.ajax({
            url: 'withdraw_check_approve_proc',
            type: 'POST',
            async: false,
            data: {'idx' : searchValue},
            success: function (data) {
                if (data) {
                    alert("이미 처리된 내용입니다");
                    $('.popupArea').hide();
                    var f = document.search_bank31_form;
                    f.submit();
    
                    contentView3OffDisplay();
                } else {
                    allExchange(searchValue, mg_id);
                }
            }
        });
    } else {
        alert("지갑주소를 다시 확인해 주세요.");
    }
}
function allExchange(searchValue, mg_id) {
    $.ajax({
        url: 'withdraw_exchange_all_proc',
        type: 'POST',
        async: false,
        data: {'mb_id' : searchValue, 'mg_id' : mg_id},
        success: function (data) {
            if (data == 'success') {
                alert("거래소 출금 처리 완료");
                $('.popupArea').hide();

                var f = document.search_bank31_form;
                f.submit();

                contentView3OffDisplay();
            }
        }
    });
}


function popupReject() {
    $('#rejectPopup').css('display', 'block');
}
function toReject() {
    var mg_id = document.getElementById("mg_id").value;

    var mb_id = document.getElementById("content2_member_no")._value;
    var reject_reason = document.getElementById("reject_reason").value;

    var amount = document.getElementById("content2_amount")._value;
    var fee = document.getElementById("content2_fee")._value;
    var total_value = parseFloat(amount)+parseFloat(fee);

    $.ajax({
        url: 'withdraw_reject_proc',
        type: 'POST',
        async: false,
        data: {'mb_id' : mb_id, 'reject_reason' : reject_reason, 'total_value' : total_value, 'mg_id' : mg_id},
        success: function (data) {

            if (data == 'success') {

                alert("거절 완료");
                $('.popupArea').hide();

                var f = document.search_bank31_form;
                f.submit();

                contentViewOnDisplay();
            }
        }
    });
}

function popupAllReject() {
    $('#allRejectPopup').css('display', 'block');
}
function toAllReject(searchVlaue, all_amount, all_fee, mg_id) {
    var reject_reason = document.getElementById("reject_reason_all").value;

    $.ajax({
        url: 'withdraw_reject_all_proc',
        type: 'POST',
        async: false,
        data: {'mb_id' : searchVlaue, 'reject_reason' : reject_reason, 'total_value' : (parseFloat(all_amount) + parseFloat(all_fee)), 'mg_id' : mg_id},
        success: function (data) {

            if (data == 'success') {

                alert("거절 완료");
                $('.popupArea').hide();

                var f = document.search_bank31_form;
                f.submit();

                contentView3OffDisplay();
            }
        }
    });
}

function checkedAll(checked) {
    var $positions = document.querySelectorAll(".listCheck");
    for (let i in $positions) {
        $positions[i].checked = checked;
    }    

    if (checked) {
        $('.totalRequest').bind('click', false);
    } else {
        $('.totalRequest').unbind('click', false);
    }
}
function checkedItem(checked) {
    var $positions = document.querySelectorAll(".listCheck");
    if (checked) {

    } else {
        $positions[0].checked = checked;
    }
}

/**
 * 일괄 출금 요청 화면의 화면전환
 */
 function contentView3OffDisplay() {

    document.getElementById("searchValue").value = "";

    var f = document.search_bank31_form;
    f.submit();
    
    $('#contentView1').show();
    $('#contentView3').hide();
}
/**
 * 일괄 출금 요청 화면의 화면전환
 * 입출금 관리 > 출금 관리 > 출금 요청 > 일괄 출금 요청
*/
 function contentView3OnDisplay(withdrawList) {

    var positions = document.querySelectorAll(".listCheck:checked");
    let param = "";
    let check = "";
    let check2 = "";

    console.log("positions.length : "+positions.length);
    if (positions.length > 0) {

        if (positions[0].value == "on") {
            const c = positions[1].value;
            const arr = c.split('|');
            param = arr[0];
            check = arr[1];
            check2 = arr[2];
            for(let i=2;i<positions.length;i++) {
                const c1 = positions[i].value;
                const arr1 = c1.split('|');
                param = arr1[0] +", " +param;
                if (check != arr1[1]) { return; }
                if (check2 != arr1[2]) { return; }
            }
        } else {
            const c = positions[0].value;
            const arr = c.split('|');
            param = arr[0];
            check = arr[1];
            check2 = arr[2];
            for(let i=1;i<positions.length;i++) {
                const c1 = positions[i].value;
                const arr1 = c1.split('|');
                param = arr1[0] +", " +param;
                if (check != arr1[1]) { return; }
                if (check2 != arr1[2]) { return; }
            }
        }
        console.log(param);
        var params = {
            "checked" : param
        };
        document.getElementById("searchValue").value = param;

        $('#contentView1').hide();
        $('#contentView3').show();

        var f = document.search_bank31_form;
        f.submit();

    }



}