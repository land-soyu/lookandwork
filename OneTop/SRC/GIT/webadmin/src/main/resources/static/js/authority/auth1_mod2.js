/* 개인정보수정창 닫기 (닫기 버튼은 처음 비밀번호 변경시에는 없다. 무조건 변경해야하기 때문에) */
function closeWindow() {

    if (confirm(getMessage(9))) {

        window.open('about:blank', '_self').close();
    }
}

/* 개인정보수정 저장 */
function saveAdminMember() {

    if ($('#phone_number').val() != '') {

        var checkNumber = /^[0-9]*$/;
        if (!checkNumber.test($('#phone_number').val())) {

            alert(getMessage(7));
            $('#phone_number').focus();
            return;
        }
    }

    var old_password = $('#old_password_check');
    var enc_password = '';

    // 처음 비밀번호 변경시 무조건 변경해야 한다.
    if ($('#is_password_init').val() == 'Y'|| old_password.val() != '') {

        // 기존 번호 불일치
        if (!checkOldPassword(old_password.val())) {

            alert(getMessage(1));
            old_password.focus();
            return;
        }

        var password = $('#password');

        if (password.val() == '') {

            alert(getMessage(2));
            password.focus();
            return;
        }

        if (!PasswordCheck(password.val())) {

            alert(getMessage(3));
            password.focus();
            return;
        }

        var passwordCheck = $('#password_check');

        if (passwordCheck.val() == '') {

            alert(getMessage(4));
            passwordCheck.focus();
            return;
        }

        if (password.val() != passwordCheck.val()) {

            alert(getMessage(5));
            passwordCheck.focus();
            return;
        }

        // RSA 암호화
        var rsa = new RSAKey();
        rsa.setPublic($('#modulus').val(), $('#exponent').val());
        enc_password = rsa.encrypt(password.val());
    }

    var param = {
        'phone_number': $('#phone_number').val(),
        'password': enc_password
    }

    if (confirm(getMessage(8))) {

        $.ajax({
            url: 'ajax/admin_mod/modify',
            type: 'POST',
            data: param,
            async: false,
            success: function (data) {

                if (data == 'success') {

                    alert(getMessage(6));

                    // 현재 창에서 열린것이기 때문에 페이지이동
                    if ($('#state').val() == 'first') location.href = '/';
                    // 새창이열린것이기 때문에 새창을 닫아준다.
                    else window.open('about:blank', '_self').close();
                }
            }
        });
    }
}

/* 기존 비밀번호 체크 */
function checkOldPassword(password) {

    var state = false;

    $.ajax({
        url: 'ajax/check_old_password',
        type: 'POST',
        data: {'old_password': password},
        async: false,
        success: function (data) {

            if (data == 'success') state = true;
        }
    });

    return state;
}

/* 비밀번호 체크 */
function PasswordCheck(password) {

    var eng1 = /[a-z]/;
    var eng2 = /[A-Z]/;
    var num = /\d/;
    var sch = /[^a-zA-Z\d]/;;
    var spe = /[`~!@#$%^&*()_\-+={}[\]\\|:;"'<>,.?\/]/
    var sz = /^[\w\W]{8,20}$/;
    var rst1 = eng1.test(password) && eng2.test(password) && num.test(password) && spe.test(password) && sz.test(password);
    var rst2 = eng1.test(password) && eng2.test(password) && sch.test(password) && spe.test(password) && sz.test(password);
    var rst = rst1 || rst2;

    return rst;
}