/**
 * Created by kim young moon on 12/12/2018.
 */

$(document).ready(function () {

    // nav에 해당 메뉴에 색 변경
    $('#li_sys > a').addClass('on');

    //snapshot 등록 팝업
    $("#snapshotInsertBtn").click(function () {
        // alert("ASD?");
        // 초기화
        $('#date').val('');
        $('#hour').val('00');
        $('#minutes').val('00');        

        $('#registPopup').css('display', 'block');
        return false;
    });

    //snapshot 지급 팝업
    $(".snapshotPaymentBtn").click(function () {       

        // 초기화                
        $('input[name="idxPayment"]').val($(this).attr("id"));
        $('#datePayment').val('');
        $('#hourPayment').val('00');
        $('#minutesPayment').val('00');                        

        $('#registPaymentPopup').css('display', 'block');
        return false;
    });


    $('#date').datepicker({
        minDate: new Date()
    });

    $('#datePayment').datepicker({
        minDate: new Date()
    });

});

function insertSnapshot() {    
    if ($('#date').val() == '') {

        alert("snapshot 시간을 등록해주세요");
        return;
    }

    if (confirm("등록하시겠습니까?")) {
        $.ajax({
            url: 'ajax/mem18/proc',
            type: 'POST',
            data: $('#insert_mem18_form').serialize(),
            async: false,
            success: function (data) {
                if (data == 'success') {
                    location.href = 'mem18';
                }
            }
        });
    }
}

function insertPayment() {    
    if ($('#datePayment').val() == '') {

        alert("지급 예정 시간을 등록해주세요");
        return;
    }
    console.log($('#payment_mem18_form').serialize());

    if (confirm("등록하시겠습니까?")) {
        $.ajax({
            url: 'ajax/mem18/payment',
            type: 'POST',
            data: $('#payment_mem18_form').serialize(),
            async: false,
            success: function (data) {
                if (data == 'success') {
                    location.href = 'mem18';
                }
            }
        });
    }
}


function downloadExcel(snapshotIdx) {
    // var f = document.search_bank10_form;
    // var params = get_form_options(f);
    console.log(snapshotIdx);
    var params = {
        snapshotIdx: snapshotIdx
    }


    post_to_url("mem18/excelDown", "_blank", params, "post");
}

function cancelPayment(snapshotIdx) {    
    var params = {
        snapshotIdx: snapshotIdx
    }
    if (confirm("지급 취소하시겠습니까?")) {
        $.ajax({
            url: 'ajax/mem18/cancel',
            type: 'POST',
            data: params,
            async: false,
            success: function (data) {
                if (data == 'success') {
                    location.href = 'mem18';
                }
            }
        });
    }   
}



