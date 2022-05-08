$(document).ready(function() {

    // nav에 해당 메뉴에 색 변경
    $('#li_sys > a').addClass('on');

    $('#import').on('click', function(){
        $('.popupArea').css('display', 'block');
    }) ;
});

function checkModify(){
    if(confirm(STR_Q_MODIFY)){
        window.location.href = 'sys1?mode=modify';
    }
}

function checkImport(){
    if(confirm(STR_Q_IMPORT)){
        importSetting();
    }
}

function importSetting() {
    var invest_fee_obj, recover_max30_obj, recover_min30_obj;
    var invest_fee, recover_max30, recover_min30;    
    
    invest_fee_obj = document.getElementById('invest_fee_0');
    invest_fee = invest_fee_obj.value;

    recover_max30_obj = document.getElementById('recover_max30_0');
    recover_max30 = recover_max30_obj.value;

    recover_min30_obj = document.getElementById('recover_min30_0');
    recover_min30 = recover_min30_obj.value;        

    var set_obj = {
        invest_fee : invest_fee,
        recover_max30 : recover_max30,
        recover_min30 : recover_min30
    };            
    //console.log(setList);

    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'sys1_proc');
    xhr.setRequestHeader("Content-type", "application/json");
    xhr.onload = function(){
        if(xhr.status === 200){
            if(xhr.responseText === "success"){
                alert(STR_REQUEST_SUCCESS);
                window.location.href = "sys1";
            }
            else{
                alert(STR_REQUEST_FAIL);
            }
        }
        else{
            alert(STR_REQUEST_FAIL);
        }
    };    
    xhr.send(JSON.stringify(set_obj));
}
