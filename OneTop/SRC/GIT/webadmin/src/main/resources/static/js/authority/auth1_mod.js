$(document).ready(function() {

    // nav에 해당 메뉴에 색 변경
    $('#li_auth > a').addClass('on');
});

function saveAdminMember() {

    // 이름 입력 체크
    if ($('#name').val() == '') {

        alert(getMessage(1));
        $('#name').focus();
        return;
    }

    // ID 입력 체크
    if ($('#id').val() == '') {

        alert(getMessage(2));
        $('#id').focus();
        return;
    }

    // 연락처 공백아니면
    if ($('#phone_number').val() != null && $('#phone_number').val() != '') {

        var checkNumber = /^[0-9]*$/;
        if (!checkNumber.test($('#phone_number').val())) {

            alert(getMessage(8));
            $('#phone_number').focus();
            return;
        }
    }

    var url = '';
    if ($('#no').val() == 0) {

        url = 'ajax/auth1_mod/add';

        if ($('#password').val() == '') {

            alert(getMessage(4));
            $('#password').focus();
            return;
        }

        var state = true;
        // ID 중복 체크
        $.ajax({
            url: 'ajax/auth1_mod/idcheck',
            type: 'POST',
            data: {'id': $('#id').val()},
            async: false,
            success: function (data) {

                if (data > 0) {

                    alert(getMessage(5));
                    $('#id').focus();
                    state = false;
                }
            }
        });

        if (!state) {

            return;
        }
    } else {

        url = 'ajax/auth1_mod/modify';
    }

    // 비밀번호값이 입력이 되었으면 RSA암호화 처리
    var password = $('#password').val();

    if (password != '') {

        $('#password').val(rsaEncrypt(password));
    }

    $.ajax({
        url: url,
        type: 'POST',
        data: $('#adminMemberForm').serialize(),
        async: false,
        success: function(data) {

            if(data == 'success') {

                alert(getMessage(6));
                location.href='auth1';
            }
        }
    });
}

// RSA 암호화
function rsaEncrypt(value) {

    var rsa = new RSAKey();
    rsa.setPublic($('#modulus').val(), $('#exponent').val());

    return rsa.encrypt(value);
}

function deleteAdminMember() {

    if (confirm(getMessage(7))) {

        $.ajax({
            url: 'ajax/auth1_mod/delete',
            type: 'POST',
            data: {'checkNo': $('#no').val()},
            async: false,
            success: function(data) {

                if(data == 'success') {

                    $('#popupArea').css('display', 'none');
                    location.href='auth1';
                }
            }
        });
    }
}