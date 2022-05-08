$(document).ready(function() {

    // 대상 회원 선택
    $('dl.targetUserSel input[name=userSelect]').on('click', function(){ //라벨을 클릭
        $('#select_total').html('0');
        $('#select_exist').html('0');
        $('#attach_file_name').val(NOT_SELECT_CSV);
        $('#csvFile').val('');
        $('#select_member').val('all');
        $('#coin_quantity').val('');
        $('#exel').parent().next().hide();
        switch ($(this).attr('id')) {
            case "userAll":
                $('#coin_quantity_field').show();
                break;
            case "exel":
                $(this).parent().next().show();
                $('#coin_quantity_field').hide();
                break;
        }
    });


    // 특정 회원 검색 후 확인 버튼 클릭시
    $('#confirmBtn').bind('click', function () {
        selectMemeberPop();
    });

    // csv파일 업로드
    $('#csvFile').bind('change', function() {
        getCsvFile();
    });
});


// csv파일 업로드
function getCsvFile() {
    var formData = new FormData();

    for (var i = 0; i < $('#csvFile')[0].files.length; i++) {
        formData.append('uploadCsv', $('#csvFile')[0].files[i]);
        $('#attach_file_name').val($('#csvFile')[0].files[i].name);
    }

    $.ajax({
        url: 'ajax/read/csv',
        type: 'POST',
        data: formData,
        async: false,
        contentType: false,
        processData: false,
        success: function (data) {
            console.log(data.coin_total_amount);
            $('#select_total').html(data.total);
            $('#select_exist').html(data.exist);
            $('#coin_quantity').val(data.coin_total_amount);
            $('#select_member').val(data.select_member);
            console.log(JSON.parse(data.select_member));
        }
    });
}


// 특정 회원 검색
function searchMemberPop() {
    if ($('#search_id').val() == '') {
        alert(INPUT_MEMBER_ID);
        $('#search_id').focus();
        return;
    }

    $.ajax({
        url: 'ajax/search/id',
        type: 'POST',
        data: {'member_id': $('#search_id').val()},
        async: false,
        success: function (data) {
            var html = '';

            if (data.length > 0) {
                $.each(data, function(index, item) {
                    html += '<tr>';
                    html += '<td>';
                    html += '<input type="radio" name="search_member_no" id="search_member_no_' + index + '" value="' + item.mb_id + '" checked><label for="search_member_no_'  + index + '"></label>';
                    html += '</td>';
                    html += '<td>' + item.mb_id + '</td>';
                    html += '<td>'+ item.totalPrice +'</td>';
                    html += '</tr>';
                });

                $('#userSearch').css('display', 'block');
                $('#searchMemeberList').html(html);
            } else {

                alert(MEMBER_NOT_EXIST);
                return;
            }
        }
    });
}


// 특정 회원 검색 후 확인 버튼 클릭시
function selectMemeberPop() {
    $('#search_id').val($('input[name="search_member_no"]:checked').val());
    $('#userSearch').css('display', 'none');
}


// 개별 유저 다음 버튼 이벤트
function selectUser_Next() {

    if ($('#search_id').val() == '') {
        alert(INPUT_MEMBER_ID);
        $('#search_id').focus();
        return;
    }

    // 대상 회원 선택값
    var count = 1;

    var coin_name = $('#coin_name').val();
    var coin_quantity = $('#coin_quantity').val();

    if (coin_quantity == '') {
        alert(INPUT_QUANTITY);
        $('#coin_quantity').focus();
        return;
    }

    var content_type = $('#content_type')[0];
    var content = $('#content').val();

    if (content == '') {
        alert(INPUT_CONTENT);
        $('#content').focus();
        return;
    }

    $('#finalConfirm').css('display', 'block');
    $('#final_coin_name').html(coin_name);
    //$('#final_coin_quantity').html(coin_quantity + " " + coin_name);
    $('#final_coin_quantity').html(coin_quantity);
    //$('#final_total_quantity').html((coin_quantity  * count)+ " " + coin_name + ' (' + count + ' ' + PEOPLE + ')');
    $('#final_total_quantity').html((coin_quantity  * count)+ " " + ' (' + count + ' ' + PEOPLE + ')');
    $('#final_content').html(content_type.options[content_type.selectedIndex].text + " : " + content);
}


// 개별 유저 최종 승인
function selectUser_Next_Approval() {
    if ($('#pw').val() == '') {
        alert(ALERT_MESSAGE_13);
        return;
    }

    // RSA 암호화
    var rsa = new RSAKey();
    rsa.setPublic($('#modulus').val(), $('#exponent').val());

    var formDate = new FormData($('#payment_repayment_form')[0]);
    formDate.append('pw', rsa.encrypt($('#pw').val()));

    $.ajax({
        url: 'ajax/mem4/approval/proc',
        type: 'POST',
        data: formDate,
        contentType: false,
        async: false,
        processData: false,
        success: function (data) {

            var pr_type = $('#pr_type').val() == 0 ? PR_TYPE_0 : PR_TYPE_1;

            if (data == 'success') {
                var coin_name = $('#coin_name').val();
                var coin_quantity = $('#coin_quantity').val();

                //var successText = coin_quantity + ' ' + coin_name + ' ' + pr_type + ' ' + ALERT_MESSAGE_8;
                var successText = coin_quantity + ' ' + pr_type + ' ' + ALERT_MESSAGE_8;
                alert(successText);

                $('#finalConfirm').css('display', 'none');
                location.href = 'mem4';
            } else {
                if(data != null){
                    alert(pr_type + ' ' + data);
                } else{
                    alert(pr_type + ' ' + ALERT_MESSAGE_9);
                }
            }
        }
    });
}




// 전체 유저 다음 버튼 이벤트
function totalMemeberPop() {

    // 대상 회원 선택값
    var userSelect = $('input[name=userSelect]:checked').val();
    var count = 0;

    if (userSelect == 'all') {
        count = getAllMemberCount();
    } else if (userSelect == 'excel') {
        count = $('#select_member').val().split(',').length;
    }

    if ($('#select_member').val() == '') {
        alert(SELECT_MEMBER_NOT_EXIST);
        return;
    }

    var coin_name = $('#coin_name').val();
    var pr_type = $('#pr_type').val() == 0 ? PR_TYPE_0 : PR_TYPE_1;
    var coin_quantity = $('#coin_quantity').val();

    if (coin_quantity == '' && userSelect=='all') {
        alert(pr_type + ' ' + INPUT_QUANTITY);
        $('#coin_quantity').focus();
        return;
    }

    var content_type = $('#content_type')[0];
    var content = $('#content').val();

    if (content == '') {
        alert(pr_type + ' ' + INPUT_CONTENT);
        $('#content').focus();
        return;
    }

    var confirmText = '';

    if (locale == 'en') {
        //confirmText = ALERT_MESSAGE_7 + ' ' + ALERT_MESSAGE_6 + ' ' + pr_type + ' ' + count + ALERT_MESSAGE_5 + ' ' + coin_quantity + ' ' + coin_name + ' ?';
        confirmText = ALERT_MESSAGE_7 + ' ' + ALERT_MESSAGE_6 + ' ' + pr_type + ' ' + count + ALERT_MESSAGE_5 + ' ' + coin_quantity + ' ?';
    } else if (locale == 'ko') {
        //confirmText = count + ALERT_MESSAGE_5 + coin_quantity + " " + coin_name + ALERT_MESSAGE_6 + pr_type + ALERT_MESSAGE_7;
        confirmText = count + ALERT_MESSAGE_5 + coin_quantity + " " + ALERT_MESSAGE_6 + pr_type + ALERT_MESSAGE_7;
    }

    if (confirm(confirmText)) {
        if(pr_type === PR_TYPE_0){
            $('#final_confirm_title').html(STR_PAYMENT_TITLE);
            $('#final_content_msg').html(STR_PAYMENT);
        }
        else if (pr_type === PR_TYPE_1){
            $('#final_confirm_title').html(STR_REPAYMENT_TITLE);
            $('#final_content_msg').html(STR_REPAYMENT);
        }
        $('#finalConfirm').css('display', 'block');
        $('#final_coin_name').html(coin_name);

        if (userSelect == 'all') {
            $('#final_coin_quantity').html(coin_quantity + " " + coin_name).show();
            $('#final_total_quantity').html((coin_quantity  * count)+ " " + coin_name + ' (' + count + ' ' + PEOPLE + ')');
        } else if (userSelect == 'excel') {
            $('#final_coin_quantity').hide();
            $('#final_total_quantity').html((coin_quantity)+ " " + coin_name + ' (' + count + ' ' + PEOPLE + ')');
        }

        $('#final_content').html(content_type.options[content_type.selectedIndex].text + " : " + content);
    }
}


// 전체 유저 최종 승인
function totalUser_Next_approval() {

    if ($('#pw').val() == '') {
        alert(ALERT_MESSAGE_13);
        return;
    }

    // RSA 암호화
    var rsa = new RSAKey();
    rsa.setPublic($('#modulus').val(), $('#exponent').val());

    var formDate = new FormData($('#payment_repayment_form')[0]);
    // formDate.append('otpPw', rsa.encrypt($('#otpPw').val()));
    formDate.append('pw', rsa.encrypt($('#pw').val()));

    $.ajax({
        url: 'ajax/mem3/approval/proc',
        type: 'POST',
        data: formDate,
        contentType: false,
        async: false,
        processData: false,
        success: function (data) {

            var pr_type = $('#pr_type').val() == 0 ? PR_TYPE_0 : PR_TYPE_1;

            if (data == 'success') {

                var coin_name = $('#coin_name').val();
                var coin_quantity = $('#coin_quantity').val();

                //var successText = coin_quantity + ' ' + coin_name + ' ' + pr_type + ' ' + ALERT_MESSAGE_8;
                var successText = coin_quantity + ' ' + pr_type + ' ' + ALERT_MESSAGE_8;
                alert(successText);

                $('#finalConfirm').css('display', 'none');
                location.href = 'mem3';
            } else {
                if(data != null){
                    alert(pr_type + ' ' + data);
                }
                else{
                    alert(pr_type + ' ' + ALERT_MESSAGE_9);
                }
            }
        }
    });
}


// 전체회원 수
function getAllMemberCount() {
    var count = 0;

    $.ajax({
        url: 'ajax/member/count',
        type: 'POST',
        async: false,
        success: function (data) {

            count = data;
        }
    });

    return count;
}