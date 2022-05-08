$(document).ready(function() {

    // nav에 해당 메뉴에 색 변경
    $('#li_sys > a').addClass('on');

    $('.toggle').on('click', function(){
        var $self = $(this);
        $($self).next().not(':animated').slideToggle({
            start:function(){
                $($self).find('i').not(':animated').toggleClass("fa-plus  fa-minus");
            }
        });
    });    

    $("p.topButton>a.btnType.import").click(function(){
        if(confirm(STR_Q_IMPORT)){
            var sendFormData = new Array();

            switch (location.pathname) {
                case "/basicextrapay":
                    basicExtraPay(sendFormData);
                    break;
                case "/sponsorextrapay":
                    sponsorExtraPay(sendFormData);
                    break;
                case "/encourageextrapay":
                    encourageExtraPay(sendFormData);
                    break;
                case "/rankextrapay":
                    rankExtraPay(sendFormData);
                    break;
            }           
            
            var xhr = new XMLHttpRequest();
            xhr.open('POST', location.pathname+'/add');
            xhr.setRequestHeader("Content-type", "application/json");
            xhr.onload = function(){
                if(xhr.status === 200){
                    if(xhr.responseText === "success"){
                        alert(STR_REQUEST_SUCCESS);
                        window.location.href = location.pathname;
                    }
                    else{
                        alert(STR_REQUEST_FAIL);
                    }
                }
                else{
                    alert(STR_REQUEST_FAIL);
                }
            };

            xhr.send(JSON.stringify(sendFormData));
        }
    });

    $("p.topButton>a.btnType.edit").click(function(){
        if(confirm(STR_Q_MODIFY)){
            window.location.href = location.href+"?mode=modify";
        }
    });

    $("div.buttonRight>a.btnType.cancel").click(function(){
        window.location.href = location.pathname;
    });





    function basicExtraPay(sendFormData){

        var idx_obj, basic_min_obj, basic_max_obj, payrate_obj;
        var idx, basic_min, basic_max, payrate;

        for(var i = 1; ; i++){
            idx_obj = document.getElementById('basic_' + i);
            if(idx_obj == null) break;
            idx = idx_obj.innerText;
    
            basic_min_obj = document.getElementById('basic_min_' + i);
            if(basic_min_obj == null) break;
            basic_min = basic_min_obj.value;

            basic_max_obj = document.getElementById('basic_max_' + i);
            if(basic_max_obj == null) break;
            basic_max = basic_max_obj.value;            

            payrate_obj = document.getElementById('payrate_' + i);
            if(payrate_obj == null) break;
            payrate = payrate_obj.value;
    
            var basicExtraPay = {
                idx : idx,
                minimum : basic_min,
                maximum : basic_max,
                paymentrate : payrate
            }                
            sendFormData.push(basicExtraPay);
        }
    }

    function sponsorExtraPay(sendFormData){

        var idx_obj, payrate_obj;
        var idx, payrate;

        for(var i = 1; ; i++){
            idx_obj = document.getElementById('sponsor_' + i);
            if(idx_obj == null) break;
            idx = idx_obj.value;

            payrate_obj = document.getElementById('sponsor_paymentrate_' + i);
            if(payrate_obj == null) break;
            payrate = payrate_obj.value;
    
            var sponsorExtraPay = {
                idx : idx,                
                paymentrate : payrate
            }                
            sendFormData.push(sponsorExtraPay);
        }       
    }

    function encourageExtraPay(sendFormData){
        var idx_obj, payrate_obj;
        var idx, payrate;

        for(var i = 1; ; i++){
            idx_obj = document.getElementById('encourage_' + i);
            if(idx_obj == null) break;
            idx = idx_obj.value;

            payrate_obj = document.getElementById('encourage_paymentrate_' + i);
            if(payrate_obj == null) break;
            payrate = payrate_obj.value;
    
            var encourageExtraPay = {
                idx : idx,                
                paymentrate : payrate
            }                
            sendFormData.push(encourageExtraPay);
        }
    }

    function rankExtraPay(sendFormData){
        var idx_obj, investtotal_obj, step1_count_obj, five_high_count_obj, payrate_obj;
        var idx, investtotal, step1_count, five_high_count, payrate;

        for(var i = 1; ; i++){
            idx_obj = document.getElementById('rank_' + i);
            if(idx_obj == null) break;
            idx = idx_obj.value;            
    
            if( i == 2 ) {
                investtotal_obj = document.getElementById('investtotal_' + i);
                if(investtotal_obj == null) break;
                investtotal = investtotal_obj.value;
            }
            else{
                investtotal = 0;
            }

            if( i == 2 ) {
                five_high_count_obj = document.getElementById('five_high_count_' + i);
                if(five_high_count_obj == null) break;
                five_high_count = five_high_count_obj.value;
            }
            else{
                five_high_count = 0;
            }

            /*
            step1_count_obj = document.getElementById('step1_count_' + i);
            if(step1_count_obj == null) break;
            step1_count = step1_count_obj.value;          

            five_high_count_obj = document.getElementById('five_high_count_' + i);
            if(five_high_count_obj == null) break;
            five_high_count = five_high_count_obj.value;          
            */

            payrate_obj = document.getElementById('rank_paymentrate_' + i);
            if(payrate_obj == null) break;
            payrate = payrate_obj.value;
    
            var rankExtraPay = {
                idx : idx,
                investtoltal : investtotal,
                step1_count : step1_count,
                five_high_count : five_high_count,
                paymentrate : payrate
            }              
            console.log(rankExtraPay);              
            sendFormData.push(rankExtraPay);
        }
    }
});
