$(document).ready(function() {

    // nav에 해당 메뉴에 색 변경
    $('#li_sys > a').addClass('on');

    $('#import').on('click', function(){
        $('.popupArea').css('display', 'block');
    }) ;
});

function checkModify(){
    if(confirm(STR_Q_MODIFY)){
        window.location.href = 'sys9?mode=modify';
    }
}

function checkImport(){
    if(confirm(STR_Q_IMPORT)){
        importSetting();
    }
}

function importSetting() {
    var coin_no_obj, coin_name_obj, banking_deposit_use_obj, banking_withdraw_use_obj, banking_list_use_obj, banking_invest_use_obj, banking_recovery_use_obj;
    var coin_no, coin_name, banking_deposit_use, banking_withdraw_use, banking_list_use, banking_invest_use, banking_recovery_use;

    var setList = [];
    for(var i = 0; ; i++){
        coin_no_obj = document.getElementById('coin_no_' + i);
        if(coin_no_obj == null) break;
        coin_no = coin_no_obj.innerText;

        coin_name_obj = document.getElementById('coin_name_' + i);
        if(coin_name_obj == null) break;
        coin_name = coin_name_obj.innerText;

        banking_deposit_use_obj = document.getElementById('banking_deposit_use_' + i);
        banking_deposit_use = banking_deposit_use_obj.value.replace(/[,]/g,'');

        banking_withdraw_use_obj = document.getElementById('banking_withdraw_use_' + i);
        banking_withdraw_use = banking_withdraw_use_obj.value.replace(/[,]/g,'');

        banking_list_use_obj = document.getElementById('banking_list_use_' + i);
        banking_list_use = banking_list_use_obj.value.replace(/[,]/g,'');

        banking_invest_use_obj = document.getElementById('banking_invest_use_' + i);
        banking_invest_use = banking_invest_use_obj.value.replace(/[,]/g,'');

        banking_recovery_use_obj = document.getElementById('banking_recovery_use_' + i);
        banking_recovery_use = banking_recovery_use_obj.value.replace(/[,]/g,'');

        var banking_use_obj = {
            coin_no : coin_no,
            coin_name : coin_name,
            banking_deposit_use : banking_deposit_use,
            banking_withdraw_use : banking_withdraw_use,
            banking_list_use : banking_list_use,
            banking_invest_use : banking_invest_use,
            banking_recovery_use : banking_recovery_use
        }

        setList.push(banking_use_obj);
    }

    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'sys9_proc');
    xhr.setRequestHeader("Content-type", "application/json");
    xhr.onload = function(){
        if(xhr.status === 200){
            if(xhr.responseText === "success"){
                alert(STR_REQUEST_SUCCESS);
                window.location.href = "sys9";
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