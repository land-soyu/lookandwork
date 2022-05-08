$(document).ready(function () {

    // nav에 해당 메뉴에 색 변경
    $('#li_board > a').addClass('on');

    setSummernote('summernote', 580);
    $('#summernote').summernote('code', CONTENT);
});

function saveNotice() {

    var title = $('#title');
    if (title.val().trim() == '') {

        alert(getMessage(1));
        title.focus();
        return;
    }

    var content = $('#summernote').summernote('code');
    var checkContent = content.replace(/&nbsp;/gi, '').replace(/\s/gi, '');

    if (checkContent == '<p><br></p>' || checkContent == '<p></p>' || checkContent.length <= 0) {

        alert(getMessage(2));
        $('#summernote').summernote('focus');
        return;
    } else {

        content = content.replace(/\/attachments\/images/gi, IMAGE_ADDR);
        $('#summernote').summernote('code', content);
    }

    $.ajax({
        url: 'ajax/board1_mod/proc',
        type: 'POST',
        data: $('#boardModForm').serialize(),
        async: false,
        success: function (data) {

            if (data == 'success') {

                alert(getMessage(3));
                location.href = 'board1';
            }
        }
    });
}