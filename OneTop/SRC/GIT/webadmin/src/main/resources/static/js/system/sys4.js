$(document).ready(function() {

    // nav에 해당 메뉴에 색 변경
    $('#li_sys > a').addClass('on');

    $('#import').on('click', function(){
        $('.popupArea').css('display', 'block');
    }) ;
});



function checkModify(){
    if(confirm(STR_Q_MODIFY)){
        window.location.href = 'sys4?mode=modify';
    }
}

function checkImport(){
    if(confirm(STR_Q_IMPORT)){
        importSetting();
    }
}

function getWithddrawLimitValue(element_id, isInputBox){
    var element_obj = document.getElementById(element_id);
    if(isInputBox)
        return element_obj.value.replace(/[,]/g,'');
    return element_obj.innerText.replace(/[,]/g,'');
}

function importSetting() {

    var coin_no_obj, coin_name_obj, withdraw_limit_min_obj,  withdraw_limit_day_sms,  withdraw_limit_day_account,  withdraw_limit_day_id_card,  withdraw_limit_day_otp;
    var coin_no, coin_name, withdraw_limit_min, withdraw_limit_day_sms,  withdraw_limit_day_account,  withdraw_limit_day_id_card,  withdraw_limit_day_otp;

    var setList = [];
    var isBTC;
    for(var i = 0; ; i++){
        coin_no_obj = document.getElementById('coin_no_' + i);
        if(coin_no_obj == null) break;
        coin_no = coin_no_obj.innerText;

        coin_name_obj = document.getElementById('coin_name_' + i);
        if(coin_name_obj == null) break;
        coin_name = coin_name_obj.innerText;
        isBTC = coin_name === 'BTC';

        withdraw_limit_min =  getWithddrawLimitValue('withdraw_limit_min_' + i, true);
        if(withdraw_limit_min.length < 0){
            alert(coin_name + STR_NOT_ENOUGH_PARAMS);
            return;
        }

        withdraw_limit_day_account =  getWithddrawLimitValue('withdraw_limit_day_account_' + i, isBTC);
        if(withdraw_limit_day_account.length < 0){
            alert(coin_name + STR_NOT_ENOUGH_PARAMS);
            return;
        }


        withdraw_limit_day_id_card =  getWithddrawLimitValue('withdraw_limit_day_id_card_' + i, isBTC);
        if(withdraw_limit_day_id_card.length < 0){
            alert(coin_name + STR_NOT_ENOUGH_PARAMS);
            return;
        }


        var withdraw_limit_obj = {
            coin_no : coin_no,
            coin_name : coin_name,
            withdraw_limit_min : withdraw_limit_min,
            withdraw_limit_day_sms : "0",
            withdraw_limit_day_account : withdraw_limit_day_account,
            withdraw_limit_day_id_card : withdraw_limit_day_id_card,
            withdraw_limit_day_otp : "0"
        };

        setList.push(withdraw_limit_obj);
    }

    coin_no_obj = document.getElementById('coin_no_krw');
    if(coin_no_obj != null) {
        coin_no = coin_no_obj.innerText;

        coin_name_obj = document.getElementById('coin_name_krw');
        coin_name = coin_name_obj.innerText;

        withdraw_limit_min = getWithddrawLimitValue('withdraw_limit_min_krw', true);
        if (withdraw_limit_min.length < 0) {
            alert(coin_name + STR_NOT_ENOUGH_PARAMS);
            return;
        }

        withdraw_limit_day_account = getWithddrawLimitValue('withdraw_limit_day_account_krw', true);
        if (withdraw_limit_day_account.length < 0) {
            alert(coin_name + STR_NOT_ENOUGH_PARAMS);
            return;
        }


        withdraw_limit_day_id_card = getWithddrawLimitValue('withdraw_limit_day_id_card_krw', true);
        if (withdraw_limit_day_id_card.length < 0) {
            alert(coin_name + STR_NOT_ENOUGH_PARAMS);
            return;
        }

        var withdraw_limit_obj = {
            coin_no: coin_no,
            coin_name: coin_name,
            withdraw_limit_min: withdraw_limit_min,
            withdraw_limit_day_sms: "0",
            withdraw_limit_day_account: withdraw_limit_day_account,
            withdraw_limit_day_id_card: withdraw_limit_day_id_card,
            withdraw_limit_day_otp: "0"
        };

        setList.push(withdraw_limit_obj);
    }

    console.log(setList);

    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'sys4_proc');
    xhr.setRequestHeader("Content-type", "application/json");
    xhr.onload = function(){
        if(xhr.status === 200){
            if(xhr.responseText === "success"){
                alert(STR_REQUEST_SUCCESS);
                window.location.href = "sys4";
            }
            else{
                alert(STR_REQUEST_FAIL);
            }
        }
        else{
            alert(STR_REQUEST_FAIL);
        }
    };

    xhr.send(JSON.stringify(setList));

}