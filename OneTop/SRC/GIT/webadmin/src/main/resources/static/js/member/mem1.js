$(document).ready(function() {

    // nav에 해당 메뉴에 색 변경
    $('#li_mem > a').addClass('on');

    // 공지사항 검색 버튼 이벤트
    $('#search_mem1_btn').bind('click', function () {
        $('#page').val('1');
        $('#search_mem1_form').submit();
        return false;
    });

    $('#searchListCount').on('change', function () {

        $('#page').val('1');
        $('#search_mem1_form').submit();
        return false;
    });
});


$(function(){
    if($('#reg_dt').attr('value') != undefined){
        var sList = $('#reg_dt').attr('value').split("-");
        var dList = [];
        sList.forEach(function(item) {
            if(item.length > 0) {
                dList.push(new Date(item.trim().replace(/\./g, '-')));
            }
        });
        $('#reg_dt').datepicker().data('datepicker').selectDate(dList);
    }

    $('#reg_dt').datepicker({
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


    if($('#last_login_dt').attr('value') != undefined){
        var sList = $('#last_login_dt').attr('value').split("-");
        var dList = [];
        sList.forEach(function(item) {
            if(item.length > 0) {
                dList.push(new Date(item.trim().replace(/\./g, '-')));
            }
        });
        if(dList.length > 0) {
            $('#last_login_dt').datepicker().data('datepicker').selectDate(dList);
        }
    }

    $('#last_login_dt').datepicker({
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
    $("#mem1_excel_down_btn").click(function() {
        var f = document.search_mem1_form;
        var params = get_form_options(f);

        if (Number(params.total) > 3000) {

            alert(ALERT_EXCELDOWNLOAD_ERR_MSG_1);
            return;
        }

        post_to_url("mem1/excelDown", "_blank", params, "post");
    });

});


function orderBy(clicked_id){
    var f = document.search_mem1_form;

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