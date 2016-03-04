var merchantsData;
var json;
$(function(){
    $.ajaxSetup({
        headers: {
            "X-CSRF-Token": getCSRFToken()
        }
    });
    $.get("/merchants/data",function(result){
        merchantsData = result;
        loadMeansOfPayment();
        var merchant = $("#meansOfPaymentSelect").find(":selected").val();
        changeFormActionAndSubmitName(merchant);

    });
    //$("input[name='sum']").keyup(function(){
    //    var val = document.getElementById("#").value;
    //    if (isNaN(val) || val < 1) {
    //        $("button[name='assertOutputPay']").prop("disabled", true);
    //        $("button[name='assertInputPay']").prop("disabled", true);
    //    } else {
    //        $("button[name='assertOutputPay']").prop("disabled", false);
    //        $("button[name='assertInputPay']").prop("disabled", false);
    //    }
    //
    //});
    $("#meansOfPaymentSelect").change(function(){
        var merchant = $("#meansOfPaymentSelect").find(":selected").val();
        changeFormActionAndSubmitName(merchant);
    });

    $("#inputPaymentProcess").bind(
        "click", function() {
            switch (this.name) {
                case "yandexMoneyPay" : submitPayForm();
                    break;
                case "perfectMoneyPay" : perfectMoneySubmit();
                    break;
            }
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
    var selectedCurrency = $("#currencySelect").find(":selected").val();
    var merchants = $("#meansOfPaymentSelect");
    merchants.empty();
    for (var key in merchantsData) {
        if (key==selectedCurrency){
            for(var value in merchantsData[key]) {
                merchants.append($('<option>', {
                    value: merchantsData[key][value]["id"],
                    text : merchantsData[key][value]["description"]
                }));
            }
        }
    }
}

var getCSRFToken = function () {
    return $("input[name='_csrf']").val();
};

var changeFormActionAndSubmitName = function (merchant) {
    var form  = $('form[name="payment"]');
    var selectedCurrency = $("#currencySelect").find(":selected").val();
    var payProcessButton = $("#inputPaymentProcess");
    for (var i = 0; i < merchantsData[selectedCurrency].length; i++) {
        if (merchantsData[selectedCurrency][i].id == merchant) {
            merchant = merchantsData[selectedCurrency][i]["name"];
            switch (merchant) {
                case "yandexmoney" :
                    form.attr("action","/merchants/yandexmoney/payment/prepare");
                    payProcessButton.attr("name","yandexMoneyPay");
                    break;
                case "perfectmoney" :
                    form.attr("action","https://perfectmoney.is/api/step1.asp");
                    payProcessButton.attr("name","perfectMoneyPay");
                    break;
                default :
                    payProcessButton.attr("name","");
            }
        }
    }
};

var submitPayForm = function () {
    $('form[name="payment"]').submit();
};

var perfectMoneySubmit = function() {
    var currency = $("#currencySelect").find(":selected").val();
    var merchant = $("#meansOfPaymentSelect").find(":selected").val();
    var sum = $("#sum").val();
    var payData = {
        "currency":1,
        "merchant":2,
        "sum":sum
    };
    $.ajax("/merchants/perfectmoney/payment/prepare",{
        type:"post",
        contentType:"application/json",
        dataType:"json",
        success:function(data) {
            $('form[name="payment"]')
                .append("<input type='hidden' name='PAYEE_ACCOUNT' value='"+data.PAYEE_ACCOUNT+"'</input>")
                .append("<input type='hidden' name='PAYEE_NAME' value='"+data.PAYEE_NAME+"'</input>")
                .append("<input type='hidden' name='PAYMENT_AMOUNT' value='"+data.PAYMENT_AMOUNT+"'</input>")
                .append("<input type='hidden' name='PAYMENT_UNITS' value='"+data.PAYMENT_UNITS+"'</input>")
                .append("<input type='hidden' name='PAYMENT_ID' value='"+data.PAYMENT_ID+"'</input>")
                .append("<input type='hidden' name='PAYMENT_URL' value='"+data.PAYMENT_URL+"'</input>")
                .append("<input type='hidden' name='NOPAYMENT_URL' value='"+data.NOPAYMENT_URL+"'</input>")
                .append("<input type='hidden' name='FORCED_PAYMENT_METHOD' value='"+data.FORCED_PAYMENT_METHOD+"'</input>")
                .append("<input type='hidden' name='PAYMENT_URL_METHOD' value='"+data.PAYMENT_URL_METHOD+"'</input>")
                .append("<input type='hidden' name='NOPAYMENT_URL_METHOD' value='"+data.NOPAYMENT_URL_METHOD+"'</input>");
            submitPayForm();
        },
        //error:function(error) {
        //    alert(JSON.stringify(error))
        //    $(".modal-body")
        //        .empty()
        //        .append("ERROR");
        //},
        data:JSON.stringify(payData)
    });
};