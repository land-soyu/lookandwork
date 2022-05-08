$(document).ready(function() {

    // nav에 해당 메뉴에 색 변경
    $('#li_board > a').addClass('on');

    setSummernote('summernote', 300);
    setSummernote('summernote2', 300);
});

function saveOperationMonitoringComment() {

    var content =  $('#summernote2').summernote('code');
    var checkContent = content.replace(/&nbsp;/gi, '').replace(/\s/gi, '');

    var formData = new FormData();
    formData.append('no', $('#no').val());
    formData.append('content', content);

    for (var i = 0; i < $('#commentUploadFile')[0].files.length; i++) {

        formData.append('uploadFiles', $('#commentUploadFile')[0].files[i]);
    }

    if ($('#commentUploadFile')[0].files.length > 0 || (content != '<p><br></p>' && checkContent != '<p></p>' && checkContent.length > 0)) {

        // 답변등록
        $.ajax({
            url: 'ajax/board5_mod_comment/proc',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            async: false,
            success: function (data) {}
        });
    }

    formData = new FormData($('#boardModForm')[0]);

    $.ajax({
        url: 'ajax/board5_mod/proc',
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        async: false,
        success: function (data) {

            if (data == 'success') {

                alert(getMessage(3));
                location.reload();
            }
        }
    });
}

function saveOperationMonitoring() {

    if ($('#qna_type').val() == 0) {

        alert(getMessage(5));
        return;
    }

    var title = $('#title');
    if (title.val().trim() == '') {

        alert(getMessage(1));
        title.focus();
        return;
    }

    var content =  $('#summernote').summernote('code');
    var checkContent = content.replace(/&nbsp;/gi, '').replace(/\s/gi, '');

    if (checkContent == '<p><br></p>' || checkContent == '<p></p>' || checkContent.length <= 0) {

        alert(getMessage(2));
        $('#summernote').summernote('focus');
        return;
    } else {

        $('#summernote').html(content);
    }

    if ($('#check_id').val() == 0) {

        alert(getMessage(6));
        return;
    }

    var formData = new FormData($('#boardModForm')[0]);

    $.ajax({
        url: 'ajax/board5_mod/proc',
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        async: false,
        success: function (data) {

            if (data == 'success') {

                alert(getMessage(3));
                location.href = 'board5';
            }
        }
    });
}