$(function(){
    $.get("/merchants/data",function(result){
        (function loadCurrency() {
            $.each(result, function(key,val){
                $("#currency").append($('<option>', {
                    value: 1,
                    text: 'My option'
                }));
            });
        })();
    });
});