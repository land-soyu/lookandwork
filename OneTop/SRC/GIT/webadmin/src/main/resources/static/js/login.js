$(document).ready(function() {

    var cookie_id = getCookie('id');

    if (cookie_id != '') {

        $('#id').val(cookie_id);
        $('#saveId').prop('checked', true);
    }

    $('#password').bind('keydown', function () {

        // 로그인화면 엔터키 이벤트
        if (window.event.keyCode == 13) {

            login();
        }
    });
});

function login() {

    var id = $('#id');
    if (id.val() == '') {

        alert(getMessage(1));
        id.focus();
        return;
    }

    var password = $('#password');
    if (password.val() == '') {

        alert(getMessage(2));
        password.focus();
        return;
    }

    if ($('#saveId').prop('checked')) {

        saveId(id.val());
    } else {

        saveId('');
    }

    // RSA 암호화
    var rsa = new RSAKey();
    rsa.setPublic($('#modulus').val(), $('#exponent').val());

    var param = {
        'id': id.val(),
        'password': rsa.encrypt(password.val())
    }

    $.ajax({
        url: 'ajax/login_proc',
        type: 'POST',
        data: param,
        async: false,
        success: function (data) {
            if (data == 'success') {
                location.reload();
            } else {
                alert(getMessage(3));
            }
        }
    });
}

function saveId(id) {

    if (id != '') {

        // id 쿠키에 저장(id 값 7일간)
        setSave("userid", id, 7);
    } else {

        // id 쿠키 삭제
        setSave("userid", id, -1);
    }
}

function setSave(name, value, expiredays) {

    var today = new Date();

    today.setDate(today.getDate() + expiredays);
    document.cookie = name + '=' + encodeURIComponent(value) + '; path=/; expires=' + today.toGMTString() + ';'
}

function getCookie(name) {

    // id 쿠키에서 id 값을 가져온다.
    var cook = document.cookie + ';';
    var idx = cook.indexOf(name, 0);
    var val = '';

    if(idx != -1) {

        cook = cook.substring(idx, cook.length);
        begin = cook.indexOf('=', 0) + 1;
        end = cook.indexOf(';', begin);
        val = unescape(cook.substring(begin, end));
    }

    return val;
}