
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

$(function(){
    $('.merchantError').hide();
});


/* --------- make merchants module start -------------- */

$(function(){

    const YANDEX = 'Yandex.Money';
    const PERFECT = 'Perfect Money';
    const NO_ACTION = 'javascript:void(0);';

    var currency = $('#currency');
    var merchant = $('#merchant');
    var sum = $('#sum');
    var operationType = $('#operationType');
    var modalTemplate = $('.paymentInfo').html().trim().split("\n");
    var button = $('#payment').find('button');
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
            if (merchantsData[index].currencyId == currency) {
               optionsHTML+='<option value="'+merchantsData[index].merchantId+'">'+merchantsData[index].description+'</option>';
           }
        });
        if (optionsHTML==='') {
            merchant.fadeOut();
            button.prop('disabled', true);
        } else {
            merchant.fadeIn();
            button.prop('disabled', false);
        }
        merchant.empty();
        merchant.html(optionsHTML);
        if (isCorrectSum()) {
            button.prop('disabled',false);
        } else {
            button.prop('disabled',true);
        }
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
                    form.attr('action', formAction.yandex);
                    break;
                case PERFECT :
                    form.attr('action', formAction.perfectDeposit);
                    break;
                default:
                    form.attr('action', NO_ACTION);
            }
        } else {
            switch (merchant) {
                case YANDEX :
                    form.attr('action', formAction.yandex);
                    break;
                case PERFECT :
                    form.attr('action', formAction.perfectWithdraw);
                    break;
                default:
                    form.attr('action', NO_ACTION);
            }
        }
    }

    function resetPaymentFormData(targetMerchant,form,callback) {
        if (operationType.val() === 'OUTPUT') {
            callback();
        } else {
            switch (targetMerchant) {
                case PERFECT :
                    $.ajax("/merchants/perfectmoney/payment/prepare", {
                        headers: {
                            "X-CSRF-Token": $("input[name='_csrf']").val()
                        },
                        type: "POST",
                        contentType: "application/json",
                        dataType: "json",
                        data: JSON.stringify($(form).serializeObject())
                    }).done(function (response) {
                        var inputsHTML = '';
                        $.each(response, function (key) {
                            $(form).append('<input type="hidden" name="' + key + '" value="' + response[key] + '">');
                        });
                        var targetCurrentHTML = $(form).html();
                        var targetNewHTML = targetCurrentHTML + inputsHTML;
                        $(form).html(targetNewHTML);
                        callback();
                    }).fail(function (error) {
                        console.log(error);
                    });
                    break;
                default:
                    callback();
            }
        }
    }
    
    function isCorrectSum() {
        var result = false;
        $.each(merchantsData,function(index) {
            if (merchantsData[index].merchantId == merchant.val()) {
                var minSum = parseFloat(merchantsData[index].minSum);
                var targetSum = parseFloat(sum.val());
                if (targetSum >= minSum) {
                    return result = true;
                }
            }
        });
        return result;
    }
    
    function fillModalWindow(url) {
        $.ajax({
            url: url,
            type: "get",
            contentType: "application/json"
        }).done(function (response) {
            console.log('handle');
            var commission = parseFloat(response);
            var targetCurrentSum = parseFloat(sum.val());
            var computedCommission = Math.ceil(sum.val() * commission) / 100;
            var targetNewSum = targetCurrentSum + computedCommission;
            var selectedCurrency = $('#currency').find(':selected').text().split(" ")[0];
            var templateVariables = {
                amount: '__amount',
                currency: '__currency',
                merchant: '__merchant',
                percent: '__percent'
            };
            var newHTMLElements = modalTemplate.slice(); //Create new array from template modalTemplate
            newHTMLElements[0] = newHTMLElements[0]
                .replace(templateVariables.amount, targetCurrentSum)
                .replace(templateVariables.currency, selectedCurrency)
                .replace(templateVariables.merchant, merchant.find(':selected').html());
            newHTMLElements[1] = newHTMLElements[1]
                .replace(templateVariables.amount, computedCommission)
                .replace(templateVariables.currency, selectedCurrency)
                .replace(templateVariables.percent, response + "%");
            newHTMLElements[2] = newHTMLElements[2]
                .replace(templateVariables.amount, targetNewSum)
                .replace(templateVariables.currency, selectedCurrency);
            var newHTML = '';
            $.each(newHTMLElements, function (index) {
                newHTML += newHTMLElements[index];
            });
            $('.paymentInfo').html(newHTML);
            $('.merchantError').hide();

        }).fail(function () {
            $('.paymentInfo').hide();
            $('.merchantError').show();
        });
    }

    currency.on('change', function () {
        resetMerchantsList(this.value);
    });

    function submitProcess() {
        var targetMerchant = merchant.find(':selected').html();
        var paymentForm = $('#payment');
        resetFormAction(operationType.val(), targetMerchant,paymentForm);
        resetPaymentFormData(targetMerchant,paymentForm,function(){
            paymentForm.submit();
        });
    }
    

    if ($("#assertInputPay").length) {
        $("#assertInputPay").bind('click',function(){
            fillModalWindow('/merchants/commission/input');   
        });
    }
    
    if ($("#assertOutputPay").length) {
        $("#assertOutputPay").bind('click',function(){
            fillModalWindow('/merchants/commission/output');        
        });
    }

    $('#inputPaymentProcess').on('click', function () {
        submitProcess();
    });

    $("#outputPaymentProcess").on('click', function () {
        var uid = $("input[name='walletUid']").val();
        if (uid.length>5){
            $("#destination").val(uid);
            submitProcess();
        } else {

        }
    });

    sum.on('keydown', function (e) {
        var k = e.which;
        /* numeric inputs can come from the keypad or the numeric row at the top */
        if (k != 37 && k != 39 && k != 8 && k != 190 && (k < 48 || k > 57) && (k < 96 || k > 105)) {
            e.preventDefault();
            return false;
        }
    });

    sum.on('keyup', function (e) {
        if (isCorrectSum()) {
            button.prop('disabled',false);
        } else {
            button.prop('disabled',true);
        }
    });
    
});