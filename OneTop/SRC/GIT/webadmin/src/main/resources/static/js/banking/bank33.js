$(document).ready(function() {
    // 검색 버튼 이벤트
    $('#search_bank33_btn').bind('click', function () {
        $('#page').val('1');
        $('#search_bank33_form').submit();
        return false;
    });

    $('#searchListCount').on('change', function () {

        $('#page').val('1');
        $('#search_bank33_form').submit();
        return false;
    });
});

$(function(e) {
	$('.form-ajax').submit(function(e) {
		e.preventDefault()
        $.ajax({
            url: $(this).attr('action'),
            type: 'POST',
            data: $(this).serialize(),
            async: false,
            success: function (data) {
                if (data == 'success') {
                    location.href = 'bank33';
                } else {
					alert(IMPORT_REQUESTFAIL);
				}
            },
            error: function (error) {
				alert(IMPORT_REQUESTFAIL);
			}
        });
	});
});

function orderBy(clicked_id){
    var f = document.search_bank33_form;

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

function popupApprove() {
	var return_wallet = $.trim($('#return_wallet').val());
	if (return_wallet == '') {
		$('#return_wallet').focus();
		alert(BANK33_ALERT_INPUT_RETURN_ADDRESS);
		return;
	}
	
	$('#approvePopup').find('input[name=approvePopup_request_address]').val(return_wallet);
    $('#approvePopup').css('display', 'block');

    document.getElementById("approvePopup_request_address").innerText = return_wallet;

}

function popupExchange() {
	var return_wallet = $.trim($('#return_wallet').val());
	if (return_wallet == '') {
		$('#return_wallet').focus();
		alert(BANK33_ALERT_INPUT_RETURN_ADDRESS);
		return;
	}
	
	$('#exchangePopup').find('input[name=return_wallet]').val(return_wallet);
    $('#exchangePopup').css('display', 'block');
}

function popupReject() {
    $('#rejectPopup').css('display', 'block');
}










function toApprove() {
	var mb_id = document.getElementById("content_member_no").value;
    var request_address = document.getElementById("approvePopup_request_address").innerText;
    var amount = document.getElementById("approve_amount").value;
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
			if (data == 'success') {
				location.href = 'bank33';
			} else {
				alert(data);
			}
		},
		error: function (error) {
			alert(IMPORT_REQUESTFAIL);
		}

    });
}

function toExchange() {
    var inputdata = document.getElementById("withdraw_wallet").value;
	var address = $.trim($('#return_wallet').val());

    console.log("inputdata : "+inputdata);
    console.log("address : "+address);

    // if (address == inputdata) {
    //     alert("지갑주소가 동일 합니다.");
		var mb_id = document.getElementById("content_member_no").value;

        $.ajax({
            url: 'withdraw_check_approve_proc',
            type: 'POST',
            async: false,
            data: {'idx' : mb_id},
            success: function (data) {
                if (data) {
                    alert("이미 처리된 내용입니다");
                    $('.popupArea').hide();
                } else {
                    exchange(mb_id);
                }
            }
        });
    // } else {
    //     alert("지갑주소를 다시 확인해 주세요.");
    // }
}
function exchange(mb_id) {
    var mg_id = document.getElementById("mg_id").value;
    $.ajax({
        url: 'withdraw_exchange_proc',
        type: 'POST',
        async: false,
        data: {'mb_id' : mb_id, 'mg_id': mg_id},
		success: function (data) {
			if (data == 'success') {
				location.href = 'bank33';
			} else {
				alert(IMPORT_REQUESTFAIL);
			}
		},
		error: function (error) {
			alert(IMPORT_REQUESTFAIL);
		}
    });
}

function toReject() {
    var mg_id = document.getElementById("mg_id").value;
	var mb_id = document.getElementById("content_member_no").value;
    var reject_reason = document.getElementById("reject_reason").value;
    var amount = document.getElementById("approvePopup_amount").innerText;

    $.ajax({
        url: 'withdraw_reject_proc',
        type: 'POST',
        async: false,
        data: {'mb_id' : mb_id, 'reject_reason' : reject_reason, 'total_value' : parseFloat(amount), 'mg_id' : mg_id},
		success: function (data) {
			if (data == 'success') {
				location.href = 'bank33';
			} else {
				alert(IMPORT_REQUESTFAIL);
			}
		},
		error: function (error) {
			alert(IMPORT_REQUESTFAIL);
		}

    });
}