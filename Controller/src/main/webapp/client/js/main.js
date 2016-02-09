var object;
$(function(){
    $.get("/merchants/data",function(result){
        object = result;
        loadMeansOfPayment()
    });
    $("button[name='paymentProcess']").bind(
        "click", function() {
            $('form[name="payment"]').submit();
        }
    );
    $("button[name='assertInputPay']").click(function(){
        $.get("/merchants/commission/input",function(commission){
            commission = parseFloat(commission);
            if (isNaN(commission)){
                $(".modal-body")
                    .empty()
                    .append("К сожалению в настоящий момент оплата недоступна.");
            } else {
                var sum = parseFloat($("input[name='sum']").val());
                var currency = $("select[name='currency']").find(":selected").text();
                var meanOfPayment = $("select[name='meansOfPayment']").find(":selected").text();
                var computedCommission = sum * commission;
                var finalSum = sum + computedCommission;
                $(".modal-body")
                    .empty()
                    .append("Вы вводите "+sum+" "+currency+" через платежную систему "+meanOfPayment+". " +
                        "Коммиссия биржи составит - "+computedCommission+" "+currency+". Итого сумма к оплате : "+finalSum+" " +
                        currency+".");
            }
        });
    });
});
function loadMeansOfPayment() {
    var selectedVal = $("#currencySelect").find(":selected").val()
    $("#meansOfPaymentSelect")
        .empty();
    for (var key in object) {
        if (key==selectedVal){
            for(var value in object[key]) {
                $("#meansOfPaymentSelect").append($('<option>', {
                    value: object[key][value]["id"],
                    text : object[key][value]["description"]
                }));
            }
        }
    }
}