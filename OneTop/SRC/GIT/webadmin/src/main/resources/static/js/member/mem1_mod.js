$(document).ready(function() {

    /** Excel D/L click */
    $("#mem1_mod_excel_down_btn").click(function() {
        var f = document.search_mem1_mod_form;
        var params = get_form_options(f);

        if (Number(params.total) > 3000) {

            alert(ALERT_EXCELDOWNLOAD_ERR_MSG_1);
            return;
        }

        post_to_url("mem1_mod/excelDown", "_blank", params, "post");
    });


    $('#membertree .treeheader div').click(function () {
        $('#membertree').addClass('off');
    });

    $('div.community_tree td.treeview').click(function () {
        $('#viewChart').text("");
        $('#membertree').removeClass('off');
        $('#membertree .treecontent .tableStyle').css('display','block');
        $.ajax({
            url: 'member_sub_tree?mb_position='+$(this).attr('pdata')+'&sort_info='+$(this).attr('vdata'),
            type: 'GET',
            contentType: false,
            async: false,
            processData: false,
            success: function (data) {
                var subtree="";
                if(data!="") {
                    for(var i=0;i<data.length;i++) {
                        subtree += "<tr>";
                        subtree += "<td>" + data[i].mb_id + "</td>";
                        subtree += "<td>" + data[i].mb_id_avail + "</td>";
                        subtree += "<td>" + data[i].mb_rank + "</td>";
                        subtree += "<td>" + (data[i].mb_id_count-1) + "</td>";
                        subtree += "<td>" + (Number(data[i].avail_total)-Number(data[i].mb_id_avail)).toFixed(4) + " LST</td>";
                        subtree += "</tr>";
                    }
                    $('#subCategory').text("").append(subtree);
                }
            }
        });
    });


    var total=0, total_lat=0.0;
    $('.community_tree table tbody').find('td:nth-child(2)').each(function(){
        if($(this).text()!="") {
            total+=parseInt($(this).text().replace(/[^0-9]/g, ""));
        }
    });
    $('.community_tree table tbody tr:last-child td:eq(1)').text(total + " 명");

    $('.community_tree table tbody').find('td:nth-child(3)').each(function(){
        if($(this).text()!="") {
            total_lat+=parseFloat($(this).text().replace(/[^0-9.]/g, ""));
        }
    });
    $('.community_tree table tbody tr:last-child td:eq(2)').text(total_lat.toFixed(4) + " LST");


});


function membertreeexport() {
    var dom_tree=$('.community_tree table tbody tr:first-child td.treeview');
    var mb_positon = dom_tree.attr('pdata')-dom_tree.attr('ndata');
    if(dom_tree!=undefined) {
        location.href="mem1/membertreeexcelDown?sortName=" + dom_tree.attr('vdata').substr(0,mb_positon*6);
    }
}


function fulltree() {
    $('#viewChart').text("");

    $('#membertree').removeClass('off');

    var dom_tree=$('.community_tree table tbody tr:first-child td.treeview');
    if(dom_tree!=undefined) {
        $.ajax({
            url: 'member_full_tree?sortName='+dom_tree.attr('vdata')+'&pdata='+dom_tree.attr('pdata'),
            type: 'POST',
            contentType: false,
            async: false,
            processData: false,
            success: function (data) {
                if(data!="") {
                    var datascource= data;
                    $('#membertree .treecontent .tableStyle').css('display','none');
                    $('#viewChart').orgchart({
                        'data' : datascource,
                        'nodeContent': 'title'
                    });
                }
            }
        });
    }
}


/**
 * 블랙 컨슈머 등록
 */
var isAjaxing = false;

function addBlackConumer() {

    if (isAjaxing) {
        return;
    }
    isAjaxing = true;

    // 종료일 체크
    if ($('#blk_add_end_dt').val() == '') {
        alert(ALERT_MESSAGE_SELECT_END_DATE);
        isAjaxing = false;
        return;
    }

    // 정지 범위 체크
    if ($('input[name="mb_blk_type"]:checked').length == 0) {
        alert(ALERT_MESSAGE_SELECT_BLT_TYPE);
        isAjaxing = false;
        return;
    }

    var formData = new FormData($('#blackconsumerForm')[0]);

    $.ajax({
        url: 'mem8/blackconsumer/add',
        type: 'POST',
        data: formData,
        async: false,
        contentType: false,
        processData: false,
        success: function (data) {

            if (data == 'success') {

                setTimeout(function () {

                    isAjaxing = false;
                }, 1000);

                location.reload();
            }
        }
    });
}


/**
 * 블랙 컨슈머 등록 POPUP
 * @param mb_id
 */
function openBlackConsumerPop(mb_id) {
    $('#blk_add_mb_id').val(mb_id);
    $('#blackConsumerPopup').css('display', 'block');
}


/**
 * 블랙 컨슈머 해제
 */
function releaseBlackConumer() {
    if ($('#memo').val() == '') {

        alert(ALERT_MESSAGE_INPUT_MEMO);
        return;
    }

    var formData = new FormData($('#releaseBlackconsumerForm')[0]);

    $.ajax({
        url: 'mem8/blackconsumer/release',
        type: 'POST',
        data: formData,
        contentType: false,
        async: false,
        processData: false,
        success: function (data) {
            if (data == 'success') {
                location.reload();
            }
        }
    });
}


/**
 * 블랙 컨슈머 해제 POPUP
 */
function openReleaseBlackConsumerPop(mb_id) {
    $('#blk_release_mb_id').val(mb_id);
    $('#releaseBlackConsumerPopup').css('display', 'block');
}


/**
 * 수당 상한액 설정 POPUP
 * @param mb_no
 * @param extrapaylimit
 */
function openExtrapayLimitPop(mb_no, extrapaylimit) {
    $('#extrapaylimit_mb_no').val(mb_no);    
    $("#extrapay_limit").val(extrapaylimit);    
    $('#extrapaylimitPopup').css('display', 'block');
}


function openWithdrawLimitPop(mb_no, withdrawlimit) {
    $('#withdrawlimit_mb_no').val(mb_no);
    $("#withdraw_limit").val(withdrawlimit);
    $('#withdrawlimitPopup').css('display', 'block');
}


function openEmailEditPop(mb_no, mb_email) {
    $('#emailEdit_mb_no').val(mb_no);
    $("#mb_email").val(mb_email);
    $('#emailEditPopup').css('display', 'block');
}


$("#otpcancle").click(function(){
    var mbId = $(this).attr('mbId');
    var mbNo = $(this).attr('mbNo');
    var security_type = 4;

    var message = '';

    if (security_type == 1) {
        message = CLEARCERT_1;
    } else if (security_type == 2) {
        message = CLEARCERT_2;
    } else if (security_type == 3) {
        message = CLEARCERT_3;
    } else if (security_type == 4) {
        message = CLEARCERT_4;
    }

    if (!confirm(message)) {
        return;
    }

    var xhttp = new XMLHttpRequest();
    var formData = new FormData();

    formData.append('mb_no', mbNo);
    formData.append('mb_id', mbId);
    formData.append('security_type', security_type);

    var url = 'mem2/clear_security';
    xhttp.open('POST', url, false);

    xhttp.onload = function () { //통신 상태변경(성공/실패)시 이벤트
        if (this.readyState == 4 && this.status == 200) {
            if(xhttp.responseText === 'success'){
                alert(STR_REQUEST_SUCCESS);
                location.reload(true);
            }
            else{
                alert(STR_REQUEST_FAIL);
            }
        }
    };
    xhttp.send(formData);
});


function send_password_reset_request(mb_id){

    if(mb_id == null || mb_id.length <= 0)
    {
        alert(STR_FIELD_IS_EMPTY);
        return;
    }    

    if(confirm(mb_id + '\n' + STR_RESET_PASSWORD)){
        send_request(mb_id);
    }
}


function send_recovery_code_request(mb_id){

    if(mb_id == null || mb_id.length <= 0)
    {
        alert(STR_FIELD_IS_EMPTY);
        return;
    }

    if(confirm(mb_id + '\n' + STR_RESEND_RECOVERY_CODE)){
        send_recovery_request(mb_id);
    }
}


function send_request(mb_id){
    var params = {
        mb_id : mb_id        
    };

    send_json_request('reset_password_request_proc', params, function(result){
        if(result !== 'error'){
            var obj = JSON.parse(result);

            if(obj.resultCode === "0000"){
                alert(STR_REQUEST_SUCCESS);
            }
            else{
                if(obj.message !== undefined){
                    alert(obj.message);
                }
                else{
                    alert(obj.resultMsg);
                }
            }
        }
        else{
            alert(result);
        }
    });
}


function send_recovery_request(mb_id){
    var params = {
        mb_id : mb_id
    };

    send_json_request('send_recovery_code_request_proc', params, function(result){
        if(result !== 'error'){
            var obj = JSON.parse(result);
            if(obj.resultCode === "0000"){
                alert(STR_REQUEST_SUCCESS);
            }
            else{
                if(obj.message !== undefined){
                    alert(obj.message);
                }
                else{
                    alert(obj.resultMsg);
                }
            }
        }
        else{
            alert(result);
        }
    });
}


/**
 * 글자 byte 체크
 */
function checkBytes(inputObj, inputString, limit, labelObj){
    var cutIndex = 0;
    var stringByteLength = (function(s,b,i,c){
        for(b=i=0;c=s.charCodeAt(i++);b+=c>>11?3:c>>7?2:1){
            if(limit != null){
                cutIndex = i;
                if(b >= limit) break;
            }
        }
        return b
    })(inputString);

    if(labelObj != null){
        labelObj.innerText = stringByteLength;
    }

    if(limit != null){
        inputString = inputString.substring(0, cutIndex);
    }

    if(inputObj != null){
        inputObj.value = inputString;
    }

    return !(limit != null && stringByteLength >= limit);
}


/**
 * 블랙 컨슈머 등록창 취소시 동작
 */
function closeBlackConsumerPopup() {
    if (confirm(ALERT_MESSAGE_CANCEL_POP)) {
        // 초기화
        $('#blk_add_end_dt').val('');
        $('#type_memo').val('');
        $('input[name="mb_blk_type"]').prop('checked', false);

        $('#blackConsumerPopup').hide();
    }
}


/**
 * 수당상한액 변경
 */
function extrapay_limit_change_request(){
    var mb_no = $("#extrapaylimit_mb_no").val();
    var extrapay_limit = $("#extrapay_limit").val();
    var params = {
        mb_no : mb_no,
        extrapay_limit : extrapay_limit
    };

    if (confirm(CHANGE_EXTRAPAY_LIMIT_DT)) {
        $.ajax({
            url: 'change_extrapay_limit_proc',
            type: 'post',
            data: params,
            async: false,
            success: function (data) {
                if (data == 'success') {
                    location.reload(true);
                    alert(SUCCESS_EXTRAPAY_LIMIT_DT);
                }
            }
        });
    }
}


/**
 * 출금가능금액 변경
 */
function withdraw_limit_change_request(){
    var mb_no = $("#withdrawlimit_mb_no").val();
    var withdraw_limit = $("#withdraw_limit").val();
    var params = {
        mb_no : mb_no,
        withdraw_limit : withdraw_limit
    };

    if (confirm(CHANGE_WITHDRAW_LIMIT_DT)) {
        $.ajax({
            url: 'change_withdraw_limit_proc',
            type: 'post',
            data: params,
            async: false,
            success: function (data) {
                if (data == 'success') {
                    location.reload(true);
                    alert(SUCCESS_WITHDRAW_LIMIT_DT);
                }
            }
        });
    }
}



/**
 * 회원 이메일 변경
 **/
function email_change_request(){
    var params = {
        mb_no : $("#emailEdit_mb_no").val(),
        mb_email : $("#mb_email").val()
    };
    if (params.mb_email=="") {
        alert(CHANGE_EMAIL_BLANK);
    } else if (confirm(CHANGE_EMAIL)) {
        $.ajax({
            url: 'change_email_proc',
            type: 'post',
            data: params,
            async: false,
            success: function (data) {
                if (data == 'success') {
                    location.reload(true);
                    alert(SUCCESS_CHANGE_EMAIL);
                }
            }
        });
    }
}


/**
 * 회원 이메일 유효성 체크
 **/
function valid_email() {
    var email = /^[0-9a-zA-Z]([0-9a-zA-Z._-])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-z])*.[a-zA-Z]{2,3}$/;
    if(!email.test($("#mb_email").val())) {
        $('#emailValid').text(EMAIL_VALID_NO);
    }else{
        $('#emailValid').text("");
    }
}


/**
 * 인증메일 재발송
 **/
function email_resend(mb_id) {
    if (confirm(AUTH_EMAIL_RESEND)) {
        $.ajax({
            url: 'auth_email_save',
            type: 'post',
            data: {
                mb_id : mb_id
            },
            async: false,
            success: function (data) {
                if (data == 'success') {
                    location.reload(true);
                    alert(SUCCESS_AUTH_EMAIL_RESEND);
                }
            }
        });
    }
}


/**
 * 추천인 ID 변경
 **/
function openReferrerIdEditPop(mb_id, parent_id) {
    $('#referrerId_self').val(mb_id);
    $("#referrerId_parent").val(parent_id);
    $('#referrerIdValid').text("");
    $('#referrerIdPopup').css('display', 'block');
}


/**
 * 추천인 ID 검색
 **/
function referrerId_Search() {
    $.ajax({
        url: 'referrerid_search',
        type: 'post',
        data: {
            mb_id : $('#referrerId_parent').val()
        },
        async: false,
        success: function (data) {
            if (data == 'ok') {
                $('#referrerIdValid').addClass('ok');
                $('#referrerIdValid').text(REFERRER_ID_SEARCH_OK);
            }else{
                $('#referrerIdValid').removeClass('ok');
                $('#referrerIdValid').text(REFERRER_ID_SEARCH_NO);
            }
        }
    });
}


/**
 * 추천인 ID 저장
 **/
function referrerId_Proc() {
    if($('#referrerIdValid').attr('class')=='ok'){
        var params = {
            mb_id: $('#referrerId_self').val(),
            mb_parent_id: $('#referrerId_parent').val()
        };
        if (params.mb_parent_id=="") {
            alert(REFERRER_ID_BLANK);
        } else if (confirm(CHANGE_REFERRER_ID)) {
            $.ajax({
                url: 'referrerid_proc',
                type: 'post',
                data: params,
                async: false,
                success: function (data) {
                    if (data == 'success') {
                        location.reload(true);
                        alert(SUCCESS_REFERRER_ID);
                    }
                }
            });
        }
    }
}



function goBank1() {
    var params = {};
    post_to_url("bank1", "_blank", params, "post");
}
function goMem5() {
    var params = {};
    post_to_url("mem5", "_blank", params, "post");
}
function goBank41() {
    var params = {};
    post_to_url("bank41", "_blank", params, "post");
}


