jQuery(function($) {
	$(document).ready(function() {

        // 코인 sortBox toggle == S
        $('.sortBox > .tit').on('click', function(){
            $(this).next().not(":animated").slideToggle();
        });
        // // 코인 sortBox toggle == E

        //  팝업 닫기 공통
        $('.popCancel').on('click', function(){
            $('.popupArea').hide();
            return false;
        });

        // 채널 channelBox toggle == S
        $('.channelBox > .tit').on('click', function(){
            $(this).next().not(":animated").slideToggle();
        });
        // // 채널 channelBox toggle == E

    });
});

// 페이지 번호로 이동
function goPage(strPageNum, form_name) {

    $('#page').val(strPageNum);
    $(form_name).submit();
}

// 전으로 10페이지
function goPrePage(form_name) {

    var strPageNum = "";

    var nPageNum = $('#page').val();
    nPageNum *= 1;

    var ntemp = 0
    if (nPageNum % 10 == 0)
        ntemp = 1

    var nPreLast = (Math.floor(nPageNum / 10) - ntemp) * 10

    if (nPreLast < 1)
        nPageNum = 1;
    else
        nPageNum = nPreLast;

    strPageNum = "" + nPageNum;

    $('#page').val(strPageNum);
    $(form_name).submit();
}

// 다음으로 10페이지
function goNextPage(form_name) {

    var strPageNum = "";

    var nPageNum = $('#page').val();
    nPageNum *= 1;

    var nTotPage = $('#totalPage').val();
    nTotPage *= 1;

    var ntemp = 0
    if (nPageNum % 10 == 0)
        ntemp = 1

    var nNextBegin = (Math.floor((nPageNum / 10)) - ntemp) * 10 + 11;

    nPageNum += nNextBegin - nPageNum;

    if (nPageNum > nTotPage)
        nPageNum = nTotPage;

    strPageNum = "" + nPageNum;

    $('#page').val(strPageNum);
    $(form_name).submit();
}

function getPrecision (num) {
    var numAsStr = num.toFixed(10); //number can be presented in exponential format, avoid it
    numAsStr = numAsStr.replace(/0+$/g, '');

    var precision = String(numAsStr).replace('.', '').length - num.toFixed().length;
    return precision;
}

// 숫자 입력 필드
function numberWithCommas(num, field, field2, defMin, defMax, precision, needToShowAlert, alertMessage, field_head, idx_to){

    // KRW는 제외
    if ($('#coin_name').val() == 'KRW') {

        return;
    }

    if(num == null) num = 0;
    num =  uncomma(num);
    num = Number(num);

    if(defMin != null && num < defMin){
        num = defMin;
        if(needToShowAlert != null && needToShowAlert && alertMessage != null && alertMessage.length > 0){
            alert(alertMessage);
        }
    }

    if(defMax != null && defMax < num){
        num = defMax;
        if(needToShowAlert != null && needToShowAlert && alertMessage != null && alertMessage.length > 0){
            alert(alertMessage);
        }
    }

    field.value =  num.toFixed(precision);

    if(field2 != null){
        field2.value = field.value;
    }

    if(field_head != null && field_head.length > 0){
        for(var i = 0; i <= idx_to; i++){
            if(document.getElementById(field_head + i) != null) document.getElementById(field_head + i).innerText = field.value;
        }
    }
}

function numberWithPrecision(num, precision){
    if(num == null) num = 0;
    num = Number(num);

    return  num.toFixed(precision);
}

function get_form_options(form) {
    var params = {};

    for(i = 0; i < form.length; i++){
        if(form[i].type == "checkbox" || form[i].type == "radio") {
            if(form[i].checked) {
                params[form[i].name] = uncomma(form[i].value);
            }
        }
        else {
            params[form[i].name] = uncomma(form[i].value);
        }
    }

    return params;
}

function show_member_detail(mb_id){
    var params = {
        mb_id : mb_id
    };

    post_to_url("member_detail", "_blank", params, "post");
}
function show_member_detail_no(mb_id, mb_no){
    var params = {
        mb_id : mb_id,
        mb_no : mb_no
    };

    post_to_url("member_detail_no", "_blank", params, "post");
}
function show_member_detail_no_41(){
    var mb_no = document.getElementById("content2_member_no").innerText;
    var mb_id = document.getElementById("content2_member_id").innerText;

    var params = {
        mb_id : mb_id,
        mb_no : mb_no
    };

    post_to_url("member_detail_no", "_blank", params, "post");
}

function show_member_balance_log(mb_id){
    var params = {
        mb_id : mb_id
    };

    post_to_url("mem5", "_blank", params, "post");
}

function show_bank_detail(req_id, mb_id){
    var params = {
        req_id : req_id,
        mb_id : mb_id
    };

    post_to_url("bank2_mod", "_blank", params, "post");
}

function post_to_url(path, target, params, method) {
    var form = document.createElement("form");
    form.setAttribute("action", path);
    form.setAttribute("target", target);
    form.setAttribute("method", method);

    for(var key in params) {
        var hiddenField = document.createElement("input");
        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", key);
        hiddenField.setAttribute("value", params[key]);
        form.appendChild(hiddenField);
    }
    document.body.appendChild(form);
    form.submit();
}

function uncomma(num ){
    return num.replace(/[,]/g, "");
}

if (!String.prototype.endsWith) {
    String.prototype.endsWith = function(searchString, position) {
        var subjectString = this.toString();
        if (typeof position !== 'number' || !isFinite(position) || Math.floor(position) !== position || position > subjectString.length) {
            position = subjectString.length;
        }
        position -= searchString.length;
        var lastIndex = subjectString.indexOf(searchString, position);
        return lastIndex !== -1 && lastIndex === position;
    };
}

Date.prototype.format = function(f) {
    if (!this.valueOf()) return " ";

    var weekName = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
    var d = this;

    return f.replace(/(yyyy|yy|MM|dd|E|hh|mm|ss|a\/p)/gi, function($1) {
        switch ($1) {
            case "yyyy": return d.getFullYear();
            case "yy": return (d.getFullYear() % 1000).zf(2);
            case "MM": return (d.getMonth() + 1).zf(2);
            case "dd": return d.getDate().zf(2);
            case "E": return weekName[d.getDay()];
            case "HH": return d.getHours().zf(2);
            case "hh": return ((h = d.getHours() % 12) ? h : 12).zf(2);
            case "mm": return d.getMinutes().zf(2);
            case "ss": return d.getSeconds().zf(2);
            case "a/p": return d.getHours() < 12 ? "오전" : "오후";
            default: return $1;
        }
    });
};
String.prototype.string = function(len){var s = '', i = 0; while (i++ < len) { s += this; } return s;};
String.prototype.zf = function(len){return "0".string(len - this.length) + this;};
Number.prototype.zf = function(len){return this.toString().zf(len);};


function copyToClipboard(val, message){
    var t = document.createElement("textarea");
    document.body.appendChild(t);
    t.value = val;
    t.select();
    document.execCommand('copy');
    document.body.removeChild(t);

    if(message != null && message.length > 0){
        alert(message + '\n' + val);
    }
    else{
        alert(val);
    }

}

function send_json_request(url, params, handler){
    var xhr = new XMLHttpRequest();
    xhr.open('POST', url);
    xhr.setRequestHeader("Content-type", "application/json");
    xhr.onload = function(){
        if(xhr.status === 200){
            //console.log("request_success : " + xhr.responseText);
            handler(xhr.responseText);

            //return xhr.responseText;
        }
        else{
            //console.log("request_fail : " + xhr.status);
            handler("error");
            //return "error";
        }
    };

    xhr.send(JSON.stringify(params));
}

function GetRandomColor(idx){
    var kelly_colors_hex = [
        "#FFB300", // Vivid Yellow
        "#803E75", // Strong Purple
        "#FF6800", // Vivid Orange
        "#A6BDD7", // Very Light Blue
        "#C10020", // Vivid Red
        "#CEA262", // Grayish Yellow
        "#817066", // Medium Gray

        // The following don't work well for people with defective color vision
        "#007D34", // Vivid Green
        "#F6768E", // Strong Purplish Pink
        "#00538A", // Strong Blue
        "#FF7A5C", // Strong Yellowish Pink
        "#53377A", // Strong Violet
        "#FF8E00", // Vivid Orange Yellow
        "#B32851", // Strong Purplish Red
        "#F4C800", // Vivid Greenish Yellow
        "#7F180D", // Strong Reddish Brown
        "#93AA00", // Vivid Yellowish Green
        "#593315", // Deep Yellowish Brown
        "#F13A13", // Vivid Reddish Orange
        "#232C16", // Dark Olive Green
    ];

    if(idx >= 0 && idx < kelly_colors_hex.length){
        return kelly_colors_hex[idx % kelly_colors_hex.length];
    }

    else if(idx < 0){
        return "#FF5555";
    }

    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for (var i = 0; i < 6; i++ ) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;

}

// 사이드 메뉴 활성화 효과
function subMenuActive(){
    $('.leftMenu ul.sub_menu li a').removeClass('active'); //모든 서브메뉴의 active class 삭제

    // url 에서 페이지 제목 가져옴
    var _url = window.location.href;
    _url = _url.split('/');
    var _pageName = _url[3];

    _pageName = _pageName.split('?')[0];
    _pageName = _pageName.split('_')[0];

    // 타겟 메뉴 클래스 추가
    var targetMenu = document.getElementById('menu_'+_pageName);
    $(targetMenu).addClass('active');

}
document.addEventListener("DOMContentLoaded", function(){
    subMenuActive();
});

function onComboboxCheck(self, args){
    console.log(args);
    var checkCount = 0;
    for(var i = 0; i < args.length; i++){
        if(document.getElementById(args[i]).checked) checkCount++;
    }
    if(checkCount <= 0){
        document.getElementById(self).checked = true;
    }
}

function preventDuplicatedSelection(src, dest, exception, defIndex){
    var src_obj = document.getElementById(src);
    var dest_obj = document.getElementById(dest);

    var src_value = src_obj.options[src_obj.selectedIndex].value;
    var dest_value = dest_obj.options[dest_obj.selectedIndex].value;

    if(src_value !== exception && dest_value !== exception) {
        if (src_value === dest_value) {
            src_obj.selectedIndex = defIndex;
        }
    }
}

function isNumberKey(evt) {
    var charCode = (evt.which) ? evt.which : event.keyCode;

    if (charCode != 46 && charCode > 31 && (charCode < 48 || charCode > 57))
        return false;
    // Textbox value
    var _value = event.srcElement.value;

    // 소수점(.)이 두번 이상 나오지 못하게
    var _pattern0 = /^\d*[.]\d*$/; // 현재 value값에 소수점(.) 이 있으면 . 입력불가
    if (_pattern0.test(_value)) {
        if (charCode == 46) {
            return false;
        }
    }

    return true;

}

/* board editor setting (id : element id, height: editor height) */
function setSummernote(id, height) {

    var summemrnote_width = 0;
    if ($('#' + id).parent()[0] != undefined) {

        summemrnote_width = $('#' + id).parent()[0].clientWidth;
    }

    // summernote 설정
    $('#' + id).summernote({
        height: height,
        width: summemrnote_width,
        maxWidth: summemrnote_width,
        minHeight: null,
        maxHeight: null,
        focus: true,
        styleTags: ['p', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6'],
        toolbar: [
            ['style',['style']],
            ['font', ['bold', 'italic', 'underline', 'clear']],
            ['fontsize', ['fontsize']],
            ['color', ['color']],
            ['link', ['linkDialogShow', 'unlink']],
            ['para', ['paragraph']],
            ['insert', [/*'link',*/ 'picture' /*, 'hr'*/]],
            ['height', ['height']],
            ['codeview', ['codeview']],
            ['view', ['fullscreen']]
        ],
        disableResizeEditor: true,
        callbacks: {
            onImageUpload: function(files, editor, welEditable) {

                for (var i = files.length - 1 ; i >= 0; i--) {

                    sendFile(files[i], this);
                }
            }
        }
    });

    // 에디터 resizebar 숨기기
    $('.note-resizebar').hide();
}

function sendFile(file, el) {

    var formDate = new FormData();
    formDate.append('file', file);
    formDate.append('pageUrl', $('#pageUrl').val());

    if ($('#no').val() == 0) {

        formDate.append('no', $('#nextNo').val());
    } else {

        formDate.append('no', $('#no').val());
    }

    $.ajax({
        url: '/attachments/images',
        type: 'POST',
        data: formDate,
        enctype: 'multipart/form-data',
        contentType: false,
        processData: false,
        success: function(url) {

            if (url == 'error') {

            } else if (url == 'Not image file') {

                alert(getMessage(3));
            } else {

                $(el).summernote('editor.insertImage', url);
            }
        }
    });
}
/* // board editor setting */

/* file download setting */
function fileDownloadAll(no, page) {

    var li = $('#fileDownAllBtn').prev().find('li');

    for (var i = 0; i < li.length; i++) {

        window.open(page + '/attach/download?no=' + no + '&fileNum=' + $(li[i]).attr('id'), 'download' + i);
    }
}

function commentFileDownloadAll(obj, no, page) {

    var li = $(obj).prev().find('li');

    for (var i = 0; i < li.length; i++) {

        window.open(page + '/comment/attach/download?no=' + no + '&fileNum=' + $(li[i]).attr('id'), 'download' + i);
    }
}

function fileDownloadAllZip(no, page) {

    var li = $('#fileDownAllBtn').prev().find('li');

    if (li.length > 1)
        window.open(page + '/attach/download/zip?no=' + no, 'download');
    else
        fileDownloadAll(no, page);
}

function commentFileDownloadAllZip(obj, no, page) {

    var li = $(obj).prev().find('li');

    if (li.length > 1)
        window.open(page + '/comment/attach/download/zip?no=' + no, 'download');
    else
        commentFileDownloadAll(obj, no, page);
}
/* // file download setting */

/* 0: 오늘, 1: 1주, 2: 1개월, 3: 3개월 */
function setDateChoiceChange(num) {

    var nowDate = new Date();
    var sdate = new Date();

    if (num == 1)
        sdate.setDate(nowDate.getDate() - 7);
    else if (num == 2)
        sdate.setMonth(nowDate.getMonth() - 1);
    else if (num == 3)
        sdate.setMonth(nowDate.getMonth() - 3);
    else if (num == 0)
    sdate.setDate(nowDate.getDate());

    $('#req_dt, #reg_dt, #search_reg_dt, #sign_dt, #request_dt, #change_dt, #search_buy_dt, #income_dt, #search_payment_dt').val(sdate.format('yyyy.MM.dd') + ' - ' + nowDate.format('yyyy.MM.dd'));
}


/**
 * 엑셀 다운로드시에 엑셀 생성 로딩화면
 * - 엑셀 처리되는로직에 추가해야함 response.setHeader("Set-Cookie", "fileDownload=true; path=/")
 *   추가하지 않으면 callback 함수 호출하지 않음
 * @param url
 * @param params
 */
function makeLoding(url, params, msg, successMsg, failMsg) {

    $.blockUI({
        css: {border: 'none',padding: '15px',backgroundColor: '#000','-webkit-border-radius': '10px', '-moz-border-radius': '10px',opacity: .5,color: '#fff'},
        message: msg
    });

    $.fileDownload((url), {
        httpMethod: 'POST',
        data: params,
        async: false,
        successCallback: function (data) {

            $.unblockUI();
            alert(successMsg);
        },
        failCallback: function (data) {

            $.unblockUI();
            alert(failMsg);
        }
    });
}

/**
 * 출금 요청 화면의 화면전환
 */
function contentViewOnDisplay() {
    $('#contentView1').show();
    $('#contentView2').hide();
}
/**
 * 출금 요청 화면의 화면전환
 * @param mb_id
 */
 function contentViewOffDisplay(idx, mb_no, mb_id, request_date, coin_name, request_addressm, amount, fee, quote_amount) {
    $('#contentView1').hide();
    $('#contentView2').show();

    document.getElementById("content2_member_no").innerText = mb_no;
    document.getElementById("content2_member_no")._value = idx;
    document.getElementById("content2_member_id").innerText = mb_id;
    document.getElementById("content2_withdraw_request_date").innerText = request_date+"( 1$="+quote_amount+""+coin_name+" )";
    document.getElementById("content2_coin_name").innerText = coin_name;
    document.getElementById("content2_request_address").innerText = request_addressm;
    document.getElementById("content2_amount").innerText = amount +" "+ coin_name;
    document.getElementById("content2_amount")._value = amount;
    document.getElementById("content2_use_point").innerText = "0 P";
    document.getElementById("content2_fee").innerText = fee+" P";
    document.getElementById("content2_fee")._value = fee;

    document.getElementById("approvePopup_coin_name").innerText = coin_name;
    document.getElementById("approvePopup_request_address").innerText = request_addressm;
    document.getElementById("approvePopup_amount").innerText = amount +" "+ coin_name;
    document.getElementById("approvePopup_amount")._value = amount;


    console.log("withdraw_old_data_proc");
    $.ajax({
        url: 'withdraw_old_data_proc',
        type: 'POST',
        async: false,
        data: {'mb_no' : mb_no},
        success: function (data) {
            if (data.oldWithdrawRequestList != null && data.oldWithdrawRequestList.length > 0) {
                document.getElementById("content2_old_request_date").innerText = data.oldWithdrawRequestList[0].create_dt;
                document.getElementById("content2_old_coin_name").innerText = data.oldWithdrawRequestList[0].coin_name;
                document.getElementById("content2_old_request_address").innerText = data.oldWithdrawRequestList[0].withdraw_address;
                document.getElementById("content2_old_use_point").innerText = "0 P";
                document.getElementById("content2_old_fee").innerText = data.oldWithdrawRequestList[0].fee +" P";

                console.log("request_addressm : "+request_addressm);
                console.log("data.oldWithdrawRequestList[0].withdraw_address : "+data.oldWithdrawRequestList[0].withdraw_address);
                if (request_addressm == data.oldWithdrawRequestList[0].withdraw_address) {
                    console.log("true");
                    document.getElementById("content2_request_address").style.color = "BLACK";
                } else {
                    console.log("false");
                    document.getElementById("content2_request_address").style.color = "RED";
                }
            }
        }
    });
}
/**
 * 출금 완료 화면의 화면전환
 * @param mb_id
 */
 function contentViewOffDisplay_(idx, mb_no, mb_id, status, txid, withdraw_address, update_dt, reject, amount, batchid, exchange, approve_id, withdraw_amount, create_dt, return_reason, return_amount, return_mg_id) {
    $('#contentView1').hide();
    $('#contentView2').show();

    document.getElementById("content2_member_no").innerText = mb_no;
    document.getElementById("content2_member_no")._value = idx;
    document.getElementById("content2_member_id").innerText = mb_id;

    document.getElementById("content2_withdraw_approval").innerText = status;
    if (status == "reject") {
        document.getElementById("content2_withdraw_sum").innerText = "-";
        document.getElementById("content2_withdraw_exchange").innerText = "-";
    } else {
        document.getElementById("content2_withdraw_sum").innerText = batchid;
        document.getElementById("content2_withdraw_exchange").innerText = exchange;
    }
    document.getElementById("content2_txid").innerText = txid=='null'?'-':txid;
    if (exchange == "X") {
        document.getElementById("content2_withdraw_exchange_address").innerText = "-";
    } else {
        document.getElementById("content2_withdraw_exchange_address").innerText = withdraw_address;
    }

    document.getElementById("content2_withdraw_approval_date").innerText = update_dt=='null'?'-':update_dt;
    document.getElementById("content2_withdraw_approval_id").innerText = approve_id=='null'?'-':approve_id;
    document.getElementById("content2_withdraw_approval_reject").innerText = reject=='null'?'-':reject;

    if ( return_mg_id == null || return_mg_id == '' || return_mg_id == '0' || return_mg_id == 'null') {
        $('#returnView').hide();
    } else {
        document.getElementById("content2_withdraw_return_date").innerText = create_dt;
        document.getElementById("content2_withdraw_return_reason").innerText = return_reason;
        document.getElementById("content2_withdraw_return_amount").innerText = return_amount;
        document.getElementById("content2_withdraw_return_id").innerText = return_mg_id;
    }
}

/**
 * 출금 승인에 대한 popup
 */
function popupApro() {
    window.open()
}
