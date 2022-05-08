$(document).ready(function() {

    // nav에 해당 메뉴에 색 변경
    $('#li_sys > a').addClass('on');

    $('#import').on('click', function(){
        $('.popupArea').css('display', 'block');
    }) ;
});

function checkModify(){
    if(confirm(STR_Q_MODIFY)){
        window.location.href = 'sys3?mode=modify';
    }
}

function checkImport(){
    if(confirm(STR_Q_IMPORT)){
        importSetting();
    }
}

function importSetting() {
    var coin_name_obj, withdraw_max30_obj, withdraw_min30_obj;
    var coin_name, withdraw_max30, withdraw_min30;

    var setList = [];
    for(var i = 0; ; i++){
        coin_name_obj = document.getElementById('coin_name_' + i);
        if(coin_name_obj == null) break;
        coin_name = coin_name_obj.innerText;

        withdraw_max30_obj = document.getElementById('withdraw_fee_max30_' + i);
        withdraw_max30 = withdraw_max30_obj.value;

        withdraw_min30_obj = document.getElementById('withdraw_fee_min30_' + i);
        withdraw_min30 = withdraw_min30_obj.value;              

        var withdraw_fee_obj = {            
            coinname : coin_name,
            max30 : withdraw_max30,
            min30 : withdraw_min30
        }
        setList.push(withdraw_fee_obj);
    }

    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'sys3_proc');
    xhr.setRequestHeader("Content-type", "application/json");
    xhr.onload = function(){
        if(xhr.status === 200){
            if(xhr.responseText === "success"){
                alert(STR_REQUEST_SUCCESS);
                window.location.href = "sys3";
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