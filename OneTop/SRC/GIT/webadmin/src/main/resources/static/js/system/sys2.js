$(document).ready(function() {

    // nav에 해당 메뉴에 색 변경
    $('#li_sys > a').addClass('on');

    $('#import').on('click', function(){
        $('.popupArea').css('display', 'block');
    }) ;
});

function checkModify(){
    if(confirm(STR_Q_MODIFY)){
        window.location.href = 'sys2?mode=modify';
    }
}

function checkImport(){
    if(confirm(STR_Q_IMPORT)){
        importSetting();
    }
}

function importSetting() {
    var ratio_auto_obj, manual_value_obj, ratio_auto, manual_value, reivent_value, retrieve_min, invent_min, extrapay_rate, target_invest_rate;

    var setList = [];

    for(var i = 1; ; i++){
        ratio_auto_obj = document.getElementById('coin_ratio_'+i);
        if(ratio_auto_obj == null) break;
        manual_value_obj = document.getElementById('coin_value_'+i);   
        
        ratio_auto = ratio_auto_obj.selectedIndex;
        manual_value = manual_value_obj.value.replace(/[,]/g,'');       
        
        var price = {            
            idx : i,
            ratio_auto : ratio_auto,
            manual_value : manual_value
        }
        setList.push(price);
    }

    reivent_value = document.getElementById('coin_reinvest_0').value.replace(/[,]/g,'');
    retrieve_min = document.getElementById('coin_retrieve_min_0').value.replace(/[,]/g,'');
    invent_min = document.getElementById('coin_invest_min_0').value.replace(/[,]/g,'');
    extrapay_rate = document.getElementById('coin_extrapay_rate_0').value.replace(/[,]/g,'');
    target_invest_rate = document.getElementById('coin_target_invest_rate_0').value.replace(/[,]/g,'');

    var set_obj = {
        reinvest_amount : reivent_value,
        retrieve_min : retrieve_min,
        invest_min : invent_min,
        extrapay_rate : extrapay_rate,
        target_invest_rate : target_invest_rate
    };

    var set_req = {       
        price : setList, 
        invest : set_obj,        
    };
    console.log(set_req);

    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'sys2_proc');
    xhr.setRequestHeader("Content-type", "application/json");
    xhr.onload = function(){
        if(xhr.status === 200){
            if(xhr.responseText === "success"){
                alert(STR_REQUEST_SUCCESS);
                window.location.href = "sys2";
            }
            else{
                alert(STR_REQUEST_FAIL);
            }
        }
        else{
            alert(STR_REQUEST_FAIL);
        }
    };

    xhr.send(JSON.stringify(set_req));
}