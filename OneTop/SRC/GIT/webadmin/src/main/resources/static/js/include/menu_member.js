$(document).ready(function () {

    $('.toggle').on('click', function(){
        var $self = $(this);
        $($self).next().not(':animated').slideToggle({
            start:function(){
                $($self).find('i').not(':animated').toggleClass("fa-plus  fa-minus");
            }
        });
    });
});