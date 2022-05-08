$(document).ready(function() {

    // nav에 해당 메뉴에 색 변경
    $('#li_bank > a').addClass('on');

    //TxID 툴팁에 넣기
    $('.txid').each(function(){
        var $text = $(this).text();
        $(this).next().text($text);
    });

    // 검색 버튼 이벤트
    $('#search_bank1_btn').bind('click', function () {
        $('#page').val('1');
        $('#search_bank1_form').submit();
        return false;
    });

    $('#searchListCount').on('change', function () {

        $('#page').val('1');
        $('#search_bank1_form').submit();
        return false;
    });
    
    $('#asset_type').change(function(e) {
		var v = $(this).val();
		if (v == 1)
			$('#row_txid').show();
		else
			$('#row_txid').hide();
	});
    
    $("#saleInsertBtn").click(function () {
        $('#asset_type').trigger('change');
        $('#registPopup').css('display', 'block');
        return false;
    });
    
    $('.popCancelCustom').click(function(e) {
		e.preventDefault();
		$(this).closest('.popupArea').hide();
	});
	
	// 목록 행 클릭시 이동
	$('.btn-table').on('click', '.btn-row', function(e) {
		location.href = $(this).data('href');
	});
	
	// 목록에서 수정/저장 기능 
	$('.data-table').on('click', '.action-data-update', function(e) {
		e.preventDefault();
		e.stopPropagation();
		
		var $btn = $(this);
		var $row = $btn.closest('.data-row');
		if (!$row.length)
			return;
			
		if ( ! $row.hasClass('mode-update') ) {
			
			// 수정모드로 전환
			var $tmpl_input = $('<input class="data-input" type="input">');
			
			$row.find('.data-updateable').each(function(i, el) {
				var $el = $(el);
				var name = $el.data('name');
				var value;
				
				// 값을 input 필드로 변환
				if ($el.is('[data-value]'))
					value = $el.data('value');
				else
					value = $el.text();
					
				var $input = $tmpl_input.clone()
								.attr('name', name)
								.val(value);
				
				$(this).html($input);
			});
			
			$row.addClass('mode-update');
			$btn.text(SAVE_BTN);
			
		} else {
			
			// 저장동작 수행
			var requestData = {};
			$row.find('.data-input').each(function(i, el) {
				var $el = $(el);
                if ($el.val() == '') {
                    requestData['idx'] = $el.text();
                } else {
                    requestData[$el.attr('name')] = $el.val();
                }
			});
			
			$.ajax({
	            url: 'ajax/bank1/update',
	            type: 'POST',
	            async: false,
	            data: requestData,
	            success: function (data) {
	
	                if (data == 'success') {
		
						// 일반 모드로 복귀
						$row.find('.data-updateable').each(function(i, el) {
							var $el = $(el);
							var name = $el.data('name');
							var value = requestData[name];
							
							// input 필드를 값으로 변환
							if ($el.is('[data-value]'))
								$el.data('value', value);
							else
								$el.text(value);
						});
						$row.removeClass('mode-update');
						$btn.text(MODIFY_BTN);
						
	                } else {
						alert(IMPORT_REQUESTFAIL);
					}
	                
	            },
	            error: function (error) {
					alert(IMPORT_REQUESTFAIL);
				}
	        });
			
		}
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
    $("#bank1_excel_down_btn").click(function() {
        var f = document.search_bank1_form;
        var params = get_form_options(f);

        if (Number(params.total) > 3000) {

            alert(ALERT_EXCELDOWNLOAD_ERR_MSG_1);
            return;
        }

        post_to_url("bank1/excelDown", "_blank", params, "post");
    });
});

function orderBy(clicked_id){
    var f = document.search_bank1_form;

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

    if (confirm(SENDMAIL_DEPOSIT)) {

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

// 회원 아이디 검색
function searchMemberPop() {
    var searchID = $.trim( $('#member_id').val() );
    $('#userSearch').css('display', 'block');
    requestSearchMember(searchID)
}

function requestSearchMember(searchID) {
	$('#userSearchQuery').val(searchID)
	$('#searchMemeberList').html("");
	
	if (searchID == '') {
		// alert(INPUT_MEMBER_ID);
		return;
	}
	
    $.ajax({
        url: 'ajax/dummy/search/id',
        type: 'POST',
        data: {"member_id": searchID},
        async: false,
        success: function (data) {
            var html = '';

            if (data.length > 0) {
                $.each(data, function(index, item) {
                    html += '<tr>';
                    html += '<td>';
                    html += '<input type="radio" name="search_member_id" id="search_member_id_' + index + '" value="' + item.mb_id + '|'+item.mb_no +'"><label for="search_member_id_'  + index + '"></label>';
                    html += '</td>';
                    html += '<td>' + item.mb_id + '</td>';
                    html += '<td>'+ item.mb_no +'</td>';
                    html += '<td>'+ item.mb_name +'</td>';
                    html += '<td>'+ item.mb_reg_dt +'</td>';
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


// 회원 아이디 검색 후 확인 버튼 클릭시
function selectMemeberPop() {
    var c = $('input[name="search_member_id"]:checked').val().split('|');
    $('#member_id').val(c[0]);
    $('#member_no').val(c[1]);
    $('#userSearch').css('display', 'none');
}

//---------------------------------

// 회원 번호 검색
function searchMemberNoPop() {
    var searchNO = $.trim( $('#member_no').val() );
    $('#userSearchNo').css('display', 'block');
    requestSearchMemberNo(searchNO)
}

function requestSearchMemberNo(searchNO) {
	$('#userSearchQueryNo').val(searchNO)
	$('#searchMemeberListNo').html("");
	
	if (searchNO == '') {
		// alert(INPUT_MEMBER_ID);
		return;
	}
	
    $.ajax({
        url: 'ajax/dummy/search/no',
        type: 'POST',
        data: {"member_no": searchNO},
        async: false,
        success: function (data) {
            var html = '';

            if (data.length > 0) {
                $.each(data, function(index, item) {
                    html += '<tr>';
                    html += '<td>';
                    html += '<input type="radio" name="search_member_no" id="search_member_no_' + index + '" value="' + item.mb_id + '|'+item.mb_no + '" checked><label for="search_member_no_'  + index + '"></label>';
                    html += '</td>';
                    html += '<td>'+ item.mb_no +'</td>';
                    html += '<td>' + item.mb_id + '</td>';
                    html += '<td>'+ item.mb_name +'</td>';
                    html += '<td>'+ item.mb_reg_dt +'</td>';
                    html += '</tr>';
                });

                $('#userSearchNo').css('display', 'block');
                $('#searchMemeberNoList').html(html);
            } else {
                alert(MEMBER_NOT_EXIST);
                return;
            }
        }
    });
}

// 회원 번호 검색 후 확인 버튼 클릭시
function selectMemeberNoPop() {
    var c = $('input[name="search_member_no"]:checked').val().split('|');
    $('#member_id').val(c[0]);
    $('#member_no').val(c[1]);
    $('#userSearchNo').css('display', 'none');
}

function insertSale() {
    if (confirm(BANK1_NEW_SAVE_CONFIRM)) {
        $.ajax({
            url: 'ajax/bank1/new',
            type: 'POST',
            data: $('#insert_bank1_form').serialize(),
            async: false,
            success: function (data) {
                if (data == 'success') {
                    location.href = 'bank1';
                } else {
					alert(IMPORT_REQUESTFAIL);
				}
            },
            error: function (error) {
				alert(IMPORT_REQUESTFAIL);
			}
        });
    }
}