$(document).ready(function() {

    // nav에 해당 메뉴에 색 변경
    $('#li_mem > a').addClass('on');

    // Excel D/L click
    $("#mem6_mod_excel_down_btn").click(function() {

        var f = document.search_payment_repayment_member_info_form;
        var params = get_form_options(f);
        post_to_url("mem6_mod/excelDown", "_blank", params, "post");
    });
});
