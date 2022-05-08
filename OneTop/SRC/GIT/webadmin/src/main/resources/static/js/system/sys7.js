$(document).ready(function() {

    // nav에 해당 메뉴에 색 변경
    $('#li_board > a').addClass('on');

    var summemrnote_width = 0;
    if ($('#summernote').parent()[0] != undefined) {

        summemrnote_width = $('#summernote').parent()[0].clientWidth;
    }

    // summernote 설정
    $('#summernote').summernote({
        height: 200,
        width: summemrnote_width,
        maxWidth: summemrnote_width,
        minHeight: null,
        maxHeight: null,
        focus: true,
        tooltip: false,
        styleTags: ['p', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6'],
        toolbar: [
            ['style',['style']],
            ['font', ['bold', 'italic', 'underline', 'clear']],
            ['fontsize', ['fontsize']],
            ['color', ['color']],
            ['link', ['linkDialogShow', 'unlink']],
            ['para', ['paragraph']],
            ['height', ['height']],
            ['codeview', ['codeview']]
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

    var summemrnote2_width = 0;
    if ($('#summernote2').parent()[0] != undefined) {

        summemrnote2_width = $('#summernote2').parent()[0].clientWidth;
    }
    // summernote 설정
    $('#summernote2').summernote({
        height: 200,
        width: summemrnote2_width,
        maxWidth: summemrnote2_width,
        minHeight: null,
        maxHeight: null,
        focus: true,
        tooltip: false,
        styleTags: ['p', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6'],
        toolbar: [
            ['style',['style']],
            ['font', ['bold', 'italic', 'underline', 'clear']],
            ['fontsize', ['fontsize']],
            ['color', ['color']],
            ['link', ['linkDialogShow', 'unlink']],
            ['para', ['paragraph']],
            ['height', ['height']],
            ['codeview', ['codeview']]
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
});

function addBanner() {
    intiPop();
    $('#addConfirm').css('display', 'block');
}

function intiPop() {

    // 초기화
    $('#start_dt').val('');
    $('#end_dt').val('');
    $('#attach_name_view').html('');
    $('#attach_name_view_en').html('');
    $('#link_url').val('');
    $('#link_url_en').val('');
    $('#summernote').summernote('code', '');
    $('#summernote2').summernote('code', '');
    $('#attach_name').css('display', 'block');
    $('#attach_name_en').css('display', 'block');
    $('#attach_name_view').css('display', 'none');
    $('#attach_name_view_en').css('display', 'none');
    $('#addPopBtn').css('display', 'block');
    $('#modifyPopBtn').css('display', 'none');
}

function modifyBanner(no) {
    intiPop();

    $.ajax({
        url: 'sys7/banner/mod',
        type: 'POST',
        data: {'no': no},
        async: false,
        success: function (data) {

            if (data.type == 1) {

                $('#type_1').prop('checked', true);
            } else {

                $('#type_2').prop('checked', true);
            }

            $('#start_dt').val(new Date(data.start_dt.split('T')[0]).format('yyyy.MM.dd'));
            $('#end_dt').val(new Date(data.end_dt.split('T')[0]).format('yyyy.MM.dd'));
            $('#attach_name_view').html(data.attach_name);
            $('#attach_name_view_en').html(data.attach_name_en);
            $('#link_url').val(data.link_url);
            $('#link_url_en').val(data.link_url_en);
            $('#no').val(data.no);
            $('#summernote').summernote('code', data.text1_kr);
            $('#summernote2').summernote('code', data.text1_en);
        }
    });

    //$('#attach_name').css('display', 'none');
    //$('#attach_name_en').css('display', 'none');
    $('#attach_name_view').css('display', 'block');
    $('#attach_name_view_en').css('display', 'block');
    $('#addPopBtn').css('display', 'none');
    $('#modifyPopBtn').css('display', 'block');

    $('#addConfirm').css('display', 'block');
}

/* 배너 추가 */
function addProc() {

    if ($('#start_dt').val() == '') {
        alert(ALERT_MSG_1);
        return;
    }

    if ($('#end_dt').val() == '') {
        alert(ALERT_MSG_2);
        return;
    }


    if ($('#attach_name_en')[0].files.length == 0) {
        alert(ALERT_MSG_3);
        return;
    }

    var formData = new FormData($('#banner_add_form')[0]);

    $.ajax({
        url: 'sys7/banner/add',
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        async: false,
        success: function (data) {
            if (data == 'success') {
                location.href = 'sys7';
            }
        }
    });
}

function modifyProc() {

    if ($('#start_dt').val() == '') {

        alert(ALERT_MSG_1);
        return;
    }

    if ($('#end_dt').val() == '') {

        alert(ALERT_MSG_2);
        return;
    }

    var formData = new FormData($('#banner_add_form')[0]);

    $.ajax({
        url: 'sys7/banner/modify_proc',
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        async: false,
        success: function (data) {

            if (data == 'success') {

                location.href = 'sys7';
            }
        }
    });
}

/* 배너 삭제 */
function deleteBanner(no, attach_name) {

    if (confirm(ALERT_MSG_4)) {

        $.ajax({
            url: 'sys7/banner/delete',
            type: 'POST',
            async: false,
            data: {'no': no, 'attach_name': attach_name},
            success: function (data) {

                if (data == 'success') {

                    location.href = 'sys7';
                }
            }
        });
    }
}