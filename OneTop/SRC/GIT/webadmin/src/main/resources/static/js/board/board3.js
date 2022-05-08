$(document).ready(function() {

    // nav에 해당 메뉴에 색 변경
    $('#li_board > a').addClass('on');

    // QNA 검색 버튼 이벤트
    $('#search_qna_btn').bind('click', function () {

        var qna_first_type = $('#search_qna_type_first').val();
        var qna_second_type;

        if (qna_first_type == 1) qna_second_type = $('#search_qna_type_second_1').val();
        else if (qna_first_type == 2)   qna_second_type = $('#search_qna_type_second_2').val();
        else qna_second_type = $('#search_qna_type_second_0').val();

        $('input[name="search_qna_type_second"]').val(qna_second_type);
        $('#page').val('1');
        $('#search_qna_form').submit();
        return false;
    });
    
    $('#search_qna_type_first').bind('change', function () {

        // 고객문의
        if ($(this).val() == 1) {
            
            $('#search_qna_type_second_0').css('display', 'none');
            $('#search_qna_type_second_1').css('display', '');
            $('#search_qna_type_second_2').css('display', 'none');

        } 
        // 제휴문의
        else if ($(this).val() == 2) {

            $('#search_qna_type_second_0').css('display', 'none');
            $('#search_qna_type_second_1').css('display', 'none');
            $('#search_qna_type_second_2').css('display', '');
        } 
        // 전체
        else {

            $('#search_qna_type_second_0').css('display', '');
            $('#search_qna_type_second_1').css('display', 'none');
            $('#search_qna_type_second_2').css('display', 'none');
        }
    });

    $('#searchListCount').on('change', function () {

        $('#page').val('1');
        $('#search_qna_form').submit();
        return false;
    });
});

$(function(){
    if($('#search_reg_dt').attr('value') != undefined){
        var sList = $('#search_reg_dt').attr('value').split("-");
        var dList = [];
        sList.forEach(function(item) {
            if(item.length > 0) {
                dList.push(new Date(item.trim().replace(/\./g, '-')));
            }
        });
        $('#search_reg_dt').datepicker().data('datepicker').selectDate(dList);
    }

    $('#search_reg_dt').datepicker({
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
});

/* 답변상태 히스토리 가져오기 */
function showHistoryPopup(no){

    $('#statusHistory').html('');

    $.ajax({
        url: 'ajax/board3_comment',
        type: 'POST',
        async: false,
        data: {'no': no},
        success: function (data) {

            for(var i = 0; i < data.length; i++) {

                var html = '<tr>';
                html += '<td>' + data[i].admin_id + '</td>';
                html += '<td>' + new Date(data[i].reg_dt).format('yyyy-MM-dd hh:mm:ss') + '</td>';
                html += '</tr>';
                $('#statusHistory').append(html);
            }
        }
    });

    document.getElementById('statusHistoryListPopup').style.display = 'block';
}