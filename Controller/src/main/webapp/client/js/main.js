var object;
$(function(){
    $.get("/merchants/data",function(result){
        object = result;
        loadMeansOfPayment()
    });
    $("input[name='sum']").keyup(function(){
        var val = document.getElementById("#").value;
        if (isNaN(val) || val < 1) {
            $("button[name='assertOutputPay']").prop("disabled", true);
            $("button[name='assertInputPay']").prop("disabled", true);
        } else {
            $("button[name='assertOutputPay']").prop("disabled", false);
            $("button[name='assertInputPay']").prop("disabled", false);
        }

    });
    $("#meansOfPaymentSelect").change(function(){
        if ($("#meansOfPaymentSelect").find(":selected").text()=="Perfect Money") {
            $("button[name='assertInputPay']").prop("disabled", true).html("Сервис в разработке");

        } else {
            $("button[name='assertInputPay']").prop("disabled", false).html("Пополнить");
        }
    });
    $("button[name='paymentProcess']").bind(
        "click", function() {
            $('form[name="payment"]').submit();
        }
    );
    $("button[name='paymentOutput']").bind(
        "click", function() {
            var uid = $("input[name='walletUid']").val();
            if (uid.length>5){
                $("#destination").val(uid);
                $('form[name="payment"]').submit();
            } else {

            }
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
                var meanOfPayment = $("select[name='merchant']").find(":selected").text();
                var computedCommission = Math.ceil((sum * commission))/100;
                var finalSum = sum + computedCommission;
                $(".modal-body")
                    .empty()
                    .append("Вы вводите через платежную систему "+meanOfPayment+" " + sum+" "+currency + "<br/>"+
                        "Коммиссия биржи составит : "+computedCommission+" "+currency+".<br/>"+
                        "Итого сумма к оплате : "+finalSum+" " + currency+".");
            }
        });
    });

    $("button[name='assertOutputPay']").click(function(){
        $.get("/merchants/commission/output",function(commission){
            commission = parseFloat(commission);
            if (isNaN(commission)){
                $(".modal-body")
                    .empty()
                    .append("К сожалению в настоящий момент оплата недоступна.");
            } else {
                var sum = parseFloat($("input[name='sum']").val());
                var currency = $("select[name='currency']").find(":selected").text().split(" ",1);
                var meanOfPayment = $("select[name='meansOfPayment']").find(":selected").text();
                var computedCommission = Math.ceil((sum * commission))/100;
                var finalSum = sum - computedCommission;
                $(".modal-header")
                    .empty()
                    .append("Вы выводите через платежную систему "+meanOfPayment+" : " + +sum+" "+currency+" <br/>"+
                        "Коммиссия биржи составит "+computedCommission+" "+currency+". <br/> " +
                        "Итого сумма к получению: "+finalSum+" " + currency+".");
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