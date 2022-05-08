$(document).ready(function() {


});


function checkSave(){
    var handling_objs = document.getElementsByName('handling');
    var reject_reason = document.getElementById('reject_reason').value.trim();

    var handling = null;

    for(var i = 0; i < handling_objs.length; i++){
        if(handling_objs[i].checked){
            handling = handling_objs[i].value;
        }
    }

    var coin_name=  document.getElementById('coin_name').innerText;
    var od_request_coin = document.getElementById('od_request_coin').innerText;
    var od_id = document.getElementById('od_id').value;

    if(handling==null) {
        alert(STR_FIELD_IS_EMPTY3);
    }else{
        if(handling == 'OK'){
            document.getElementById('withdrawalPopup').style.display = 'block';
        }
        else if(handling == 'REJECT'){
            console.log("reject_reason : " + reject_reason);
            if(handling === null || reject_reason == null || reject_reason.length <= 0){
                alert(STR_FIELD_IS_EMPTY2);
                return;
            }

            if(confirm(od_request_coin + ' ' + coin_name + '\n' + STR_Q_PROCESS_REJECT)){
                send_request(handling, od_id, "", "", "", reject_reason);
            }
        }
    }
}

function confirm_withdraw(){
    var coin_name =  document.getElementById('coin_name').innerText;
    var od_request_coin = document.getElementById('od_request_coin').innerText;

    var od_id = document.getElementById('od_id').value;
    //var otp_pw = document.getElementById('otp_pw').value;
    var admin_pw = document.getElementById('admin_pw').value;
    //var withdraw_pw = document.getElementById('withdraw_pw').value;

    if(od_id == null || od_id.length <= 0 ||        
        admin_pw == null || admin_pw.length <= 0 )
    {
        alert(STR_FIELD_IS_EMPTY);
        return;
    }

    if(confirm(od_request_coin + ' ' + coin_name + '\n' + STR_Q_PROCESS_CONFIRM)){
        send_request('OK', od_id, "", admin_pw, "", null);
    }
}

function send_request(handling, od_id, otp_pw, admin_pw, withdraw_pw, reason){
    var params = {
        handling : handling,
        od_id : od_id,
        otp_pw : otp_pw == '' ? otp_pw : rsaEncrypt(otp_pw),
        admin_pw : admin_pw == '' ? admin_pw : rsaEncrypt(admin_pw),
        withdraw_pw : withdraw_pw == '' ? withdraw_pw : rsaEncrypt(withdraw_pw),
        reason : reason
    };

    send_json_request('bank2_mod_proc', params, function(result){
        console.log('result : ' + result);
        var obj = JSON.parse(result);
        if(obj.resultCode === "0000"){
            alert(STR_REQUEST_SUCCESS);
            window.location.reload();
            window.opener.location.reload();
        }
        else{
            alert(obj.resultMsg);
            // 아무 액션 없음.
        }

        //window.close();
        /*
        if(result === "success"){
            alert(STR_REQUEST_SUCCESS);
            window.location.reload();
        }
        else{
            alert(STR_REQUEST_FAIL);
        }
        */
    });
}

// RSA 암호화
function rsaEncrypt(value) {

    var rsa = new RSAKey();
    rsa.setPublic($('#modulus').val(), $('#exponent').val());

    return rsa.encrypt(value);
}