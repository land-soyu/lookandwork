$(document).ready(function () {

    $('#bi_btn').on('click', function () {

        if (location.href.indexOf('admin_mod') == -1) {

            location.href='/';
        }
    });
});

function logout() {

    if (confirm(logout_text)) {

        location.href='logout_proc';
    }
}