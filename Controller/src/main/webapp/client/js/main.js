
/* --------- Serializes form data to json object -------------- */

$.fn.serializeObject = function()
{
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

/* --------- make merchants module start -------------- */

$(function(){

    const YANDEX = 'Yandex.Money';
    const PERFECT = 'Perfect Money';
    const NO_ACTION = 'javascript:void(0);';

    var currency = $('#currency');
    var merchant = $('#merchant');
    var sum = $('#sum');
    var operationType = $('#operationType');
    var merchantsData;

    (function loadData(dataUrl) {
        $.ajax({
            url: dataUrl,
            type: 'GET',
            dataType: 'json'
        }).done(function (data) {
            merchantsData = data;
            resetMerchantsList(currency.val());
        }).fail(function (jqXHR, textStatus) {
            console.log(jqXHR);
            console.log(textStatus);
            if (textStatus === 'parsererror') {
                console.log('Requested JSON parse failed.');
            } else if (textStatus === 'abort') {
                console.log('Ajax request was aborted.');
            } else {
                console.log('Error status code:' + jqXHR.status);
            }
        });
    })('/merchants/data');

    function resetMerchantsList(currency) {
        var optionsHTML = '';
        $.each(merchantsData,function(index){
           //noinspection JSUnresolvedVariable
            if (merchantsData[index].currencyId == currency) {
               optionsHTML+='<option value="'+merchantsData[index].merchantId+'">'+merchantsData[index].description+'</option>';
           }
            merchant.empty();
            merchant.html(optionsHTML);
        });
    }

    function resetFormAction(operationType,merchant,form) {
        var formAction = {
            yandex:'/merchants/yandexmoney/payment/prepare',
            perfectDeposit:'https://perfectmoney.is/api/step1.asp',
            perfectWithdraw:'/merchants/perfectmoney/payment/provide'
        };
        if (operationType === 'INPUT') {
            switch (merchant) {
                case YANDEX :
                    form.action = formAction.yandex;
                    break;
                case PERFECT :
                    form.action = formAction.perfectDeposit;
                    break;
                default:
                    form.action = NO_ACTION;
            }
        } else {
            switch (merchant) {
                case YANDEX :
                    form.action = formAction.yandex;
                    break;
                case PERFECT :
                    form.action = formAction.perfectWithdraw;
                    break;
                default:
                    paymentForm.action = NO_ACTION;
            }
        }
    }

    function resetPaymentFormData(form,targetMerchant,serializedData) {
        switch (targetMerchant) {
            case PERFECT :
                $.ajax("/merchants/perfectmoney/payment/prepare", {
                    headers : {
                        "X-CSRF-Token": $("input[name='_csrf']").val()
                    },
                    type: "post",
                    contentType: "application/json",
                    dataType: "json",
                    data:JSON.stringify(serializedData)
                }).done(function (response) {
                    var inputsHTML = '';
                    $.each(response, function (key) {
                        $(form).append('<input type="hidden" name="' + key + '" value="' + response[key] + '">');
                    });
                    // var targetCurrentHTML = $(form).html();
                    // var targetNewHTML = targetCurrentHTML + inputsHTML;
                    // $(form).html(targetNewHTML);
                }).fail(function (error) {
                    console.log(error);
                });
                break;
        }
    }

    currency.on('change', function () {
        resetMerchantsList(this.value);
    });

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
    
    $("#payment").submit(function () {
        var targetMerchant = merchant.find(':selected').html();
        var operationType = $(operationType).val(); 
        resetFormAction(operationType, targetMerchant, this);
        if (operationType==='INPUT') {
            resetPaymentFormData(this,targetMerchant,$(this).serializeObject());   
        }
    });


    

    // $.get("/merchants/data",function(result){
    //     merchantsData = result;
    //     loadMeansOfPayment();
    //     var merchant = $("#meansOfPaymentSelect").find(":selected").val();
    //     // changeFormActionAndSubmitName(merchant);
    //
    // });
    // $("#inputCurrency").change(function(){
    //     alert(this.value)
    // });
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
    //$("#meansOfPaymentSelect").change(function(){
    //    var merchant = $("#meansOfPaymentSelect").find(":selected").val();
    //    changeFormActionAndSubmitName(merchant);
    //});

    // $("#inputPaymentProcess").bind(
    //     "click", function() {
    //         switch (this.name) {
    //             case "yandexMoneyPay" : submitPayForm();
    //                 break;
    //             case "perfectMoneyPay" : perfectMoneySubmit();
    //                 break;
    //         }
    //     }
    // );



    // $("button[name='paymentOutput']").bind(
    //     "click", function() {
    //         var uid = $("input[name='walletUid']").val();
    //         if (uid.length>5){
    //             $("#destination").val(uid);
    //             var currency = $("#currencySelect").find(":selected").val();
    //             var merchant = $("#meansOfPaymentSelect").find(":selected").val();
    //             var sum = $("#sum").val();
    //             var operationType = $("#operationType").val();
    //             var destination = $("#walletUid");
    //             var payData = {
    //                 "currency":currency,
    //                 "merchant":merchant,
    //                 "sum":sum,
    //                 "operationType":operationType,
    //                 "destination":destination
    //             };
    //             $.ajax("/merchants/perfectmoney/payment/prepare",{
    //                 type:"post",
    //                 contentType:"application/json",
    //                 dataType:"json",
    //                 success:function(data) {
    //                     submitPayForm();
    //                 },
    //                 data:JSON.stringify(payData)
    //             });
    //         } else {
    //
    //         }
    //     }
    // );
    // $("button[name='assertInputPay']").click(function(){
    //     $.get("/merchants/commission/input",function(commission){
    //         commission = parseFloat(commission);
    //         if (isNaN(commission)){
    //             $(".modal-body")
    //                 .empty()
    //                 .append("К сожалению в настоящий момент оплата недоступна.");
    //         } else {
    //             var sum = parseFloat($("input[name='sum']").val());
    //             var currency = $("select[name='currency']").find(":selected").text();
    //             var meanOfPayment = $("select[name='merchant']").find(":selected").text();
    //             var computedCommission = Math.ceil((sum * commission))/100;
    //             var finalSum = sum + computedCommission;
    //             $(".modal-body")
    //                 .empty()
    //                 .append("Вы вводите через платежную систему "+meanOfPayment+" " + sum+" "+currency + "<br/>"+
    //                     "Коммиссия биржи составит : "+computedCommission+" "+currency+".<br/>"+
    //                     "Итого сумма к оплате : "+finalSum+" " + currency+".");
    //         }
    //     });
    // });

    // $("button[name='assertOutputPay']").click(function(){
    //     $.get("/merchants/commission/output",function(commission){
    //         commission = parseFloat(commission);
    //         if (isNaN(commission)){
    //             $(".modal-body")
    //                 .empty()
    //                 .append("К сожалению в настоящий момент оплата недоступна.");
    //         } else {
    //             var sum = parseFloat($("input[name='sum']").val());
    //             var currency = $("select[name='currency']").find(":selected").text().split(" ",1);
    //             var meanOfPayment = $("select[name='meansOfPayment']").find(":selected").text();
    //             var computedCommission = Math.ceil((sum * commission))/100;
    //             var finalSum = sum - computedCommission;
    //             $(".modal-header")
    //                 .empty()
    //                 .append("Вы выводите через платежную систему "+meanOfPayment+" : " + +sum+" "+currency+" <br/>"+
    //                     "Коммиссия биржи составит "+computedCommission+" "+currency+". <br/> " +
    //                     "Итого сумма к получению: "+finalSum+" " + currency+".");
    //         }
    //     });
    // });

});
// function loadMeansOfPayment(operationType) {
//     var selectedCurrency = $("#currency").find(":selected").val();
//     var merchants = $("#merchant");
//     merchants.empty();
//     for (var key in merchantsData) {
//         if (key==selectedCurrency){
//             for(var value in merchantsData[key]) {
//                 merchants.append($('<option>', {
//                     value: merchantsData[key][value]["id"],
//                     text : merchantsData[key][value]["description"]
//                 }));
//             }
//         }
//     }
// }
//

//
// var changeFormActionAndSubmitName = function (merchant) {
//     var form  = $('form[name="payment"]');
//     var selectedCurrency = $("#currencySelect").find(":selected").val();
//     var payProcessButton = $("#inputPaymentProcess");
//     for (var i = 0; i < merchantsData[selectedCurrency].length; i++) {
//         if (merchantsData[selectedCurrency][i].id == merchant) {
//             merchant = merchantsData[selectedCurrency][i]["name"];
//             switch (merchant) {
//                 case "yandexmoney" :
//                     form.attr("action","/merchants/yandexmoney/payment/prepare");
//                     payProcessButton.attr("name","yandexMoneyPay");
//                     break;
//                 case "perfectmoney" :
//                     form.attr("action","https://perfectmoney.is/api/step1.asp");
//                     payProcessButton.attr("name","perfectMoneyPay");
//                     break;
//                 default :
//                     payProcessButton.attr("name","");
//             }
//         }
//     }
// };
//
// var submitPayForm = function () {
//     $('form[name="payment"]').submit();
// };
//
// var perfectMoneySubmit = function() {
//     var currency = $("#currencySelect").find(":selected").val();
//     var merchant = $("#meansOfPaymentSelect").find(":selected").val();
//     var sum = $("#sum").val();
//     var operationType = $("#operationType").val();
//     var payData = {
//         "currency":currency,
//         "merchant":merchant,
//         "sum":sum,
//         "operationType":operationType
//     };
//     $.ajax("/merchants/perfectmoney/payment/prepare",{
//         type:"post",
//         contentType:"application/json",
//         dataType:"json",
//         success:function(data) {
//             $('form[name="payment"]')
//                 .append("<input type='hidden' name='PAYEE_ACCOUNT' value='"+data.PAYEE_ACCOUNT+"'</input>")
//                 .append("<input type='hidden' name='PAYEE_NAME' value='"+data.PAYEE_NAME+"'</input>")
//                 .append("<input type='hidden' name='PAYMENT_AMOUNT' value='"+data.PAYMENT_AMOUNT+"'</input>")
//                 .append("<input type='hidden' name='PAYMENT_UNITS' value='"+data.PAYMENT_UNITS+"'</input>")
//                 .append("<input type='hidden' name='PAYMENT_ID' value='"+data.PAYMENT_ID+"'</input>")
//                 .append("<input type='hidden' name='PAYMENT_URL' value='"+data.PAYMENT_URL+"'</input>")
//                 .append("<input type='hidden' name='STATUS_URL' value='"+data.STATUS_URL+"'</input>")
//                 .append("<input type='hidden' name='NOPAYMENT_URL' value='"+data.NOPAYMENT_URL+"'</input>")
//                 .append("<input type='hidden' name='FORCED_PAYMENT_METHOD' value='"+data.FORCED_PAYMENT_METHOD+"'</input>")
//                 .append("<input type='hidden' name='PAYMENT_URL_METHOD' value='"+data.PAYMENT_URL_METHOD+"'</input>")
//                 .append("<input type='hidden' name='NOPAYMENT_URL_METHOD' value='"+data.NOPAYMENT_URL_METHOD+"'</input>")
//             submitPayForm();
//         },
        //error:function(error) {
        //    alert(JSON.stringify(error))
        //    $(".modal-body")
        //        .empty()
        //        .append("ERROR");
        //},
        // data:JSON.stringify(payData)
    // });
// };