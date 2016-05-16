
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
    $('.merchantError').hide();
    $('.response_money_operation_btn').hide();
});

$(function(){

    const YANDEX = 'Yandex.Money';
    const PERFECT = 'Perfect Money';
    const BLOCKCHAIN = 'Blockchain';
    const ADVCASH = 'Advcash Money';
    const EDR_COIN = 'EDR Coin';
    const LIQPAY = 'LiqPay';
    const NIX = 'Nix Money';
    const YANDEX_KASSA = 'Yandex kassa';
    const PRIVAT24 = 'Privat24';
    const INTERKASSA = 'Interkassa';

    const NO_ACTION = 'javascript:void(0);';
    
    var currency = $('#currency');
    var merchant = $('#merchant');
    var sum = $('#sum');
    var operationType = $('#operationType');
    var modalTemplate = $('.paymentInfo p');
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
            blockchainDeposit:'/merchants/blockchain/payment/provide',
            perfectDeposit:'https://perfectmoney.is/api/step1.asp',
            advcash:'/merchants/advcash/payment/prepare',
            liqpay:'/merchants/liqpay/payment/prepare',
            nixmoney:'/merchants/nixmoney/payment/prepare',
            yandex_kassa:'http://shop.itfoxy.com/index.php?route=acc/success/order',
            privat24:'https://api.privatbank.ua/p24api/ishop',
            interkassa:'https://sci.interkassa.com/'

        };
        if (operationType === 'INPUT') {
            switch (merchant) {
                case YANDEX :
                    form.attr('action', formAction.yandex);
                    break;
                case PERFECT :
                    form.attr('action', formAction.perfectDeposit);
                    break;
                case ADVCASH :
                    form.attr('action', formAction.advcash);
                    break;
                case LIQPAY :
                    form.attr('action', formAction.liqpay);
                    break;
                case NIX :
                    form.attr('action', formAction.nixmoney);
                    break;
                case YANDEX_KASSA :
                    form.attr('action', formAction.yandex_kassa);
                    break;
                case PRIVAT24 :
                    form.attr('action', formAction.privat24);
                    break;
                case INTERKASSA :
                    form.attr('action', formAction.interkassa);
                    break;
                case BLOCKCHAIN:
                default:
                    form.attr('action', NO_ACTION);
            }
        }
    }

    function responseControls () {
        $('.request_money_operation_btn').hide();
        $('.response_money_operation_btn').show();
    }

    function requestControls() {
        $('.request_money_operation_btn').show();
        $('.response_money_operation_btn').hide();
    }

    function resetPaymentFormData(targetMerchant,form,callback) {
        if (operationType.val() === 'OUTPUT') {
            $.ajax('/merchants/payment/withdraw', {
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                type: 'POST',
                contentType: 'application/json',
                dataType: 'json',
                data: JSON.stringify($(form).serializeObject())
            }).done(function (response) {
                $('#currency').find(':selected').html(response['balance']);
                responseControls();
                $('.paymentInfo').html(response['success']);
                $('.wallet_input').hide();
            }).fail(function (error, jqXHR, textStatus) {
                console.log(textStatus);
                console.log(jqXHR);
                responseControls();
                $('.paymentInfo').html(error['responseJSON']['failure']);
                $('.wallet_input').hide();
            });
        } else {
            switch (targetMerchant) {
                case PERFECT :
                    $.ajax('/merchants/perfectmoney/payment/prepare', {
                        headers: {
                            'X-CSRF-Token': $("input[name='_csrf']").val()
                        },
                        type: 'POST',
                        contentType: 'application/json',
                        dataType: 'json',
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
                        responseControls();
                        $('.paymentInfo').html(error.responseText);
                        console.log(error);
                    });
                    break;
                case BLOCKCHAIN :
                    $('#inputPaymentProcess')
                        .html($('#mrcht-waiting').val())
                        .prop('disabled', true);
                    $.ajax('/merchants/blockchain/payment/prepare', {
                        headers: {
                            'X-CSRF-Token': $("input[name='_csrf']").val()
                        },
                        type: 'POST',
                        contentType: 'application/json;charset=utf-8',
                        dataType: 'text',
                        data: JSON.stringify($(form).serializeObject())
                    }).done(function (response) {
                        $('#inputPaymentProcess')
                            .prop('disabled', false)
                            .html($('#mrcht-ready').val());
                        $('.paymentInfo').html(response);
                        responseControls();
                    }).fail(function (error, jqXHR, textStatus) {
                        responseControls();
                        $('.paymentInfo').html(error.responseText);
                        console.log(textStatus);
                    });
                    break;
                case EDR_COIN :
                    $('#inputPaymentProcess')
                        .prop('disabled', true)
                        .html($('#mrcht-waiting').val());
                        
                    $.ajax('/merchants/edrcoin/payment/prepare', {
                      headers: {
                          'X-CSRF-Token': $("input[name='_csrf']").val()
                      },
                      type: 'POST',
                      contentType: 'application/json',
                      data: JSON.stringify($(form).serializeObject()),
                      success:function (response) {
                          $('#inputPaymentProcess')
                              .prop('disabled', false)
                              .html($('#mrcht-ready').val());
                          console.log(response);
                          $('.paymentInfo').html(response);
                          responseControls();
                      },
                      error:function (jqXHR, textStatus, errorThrown) {
                          console.log(jqXHR);
                          console.log(textStatus);
                          console.log(errorThrown);
                          $('.paymentInfo').html(error.responseText);
                          responseControls();
                      }
                  });
                      break;
                case PRIVAT24 :
                    $.ajax('/merchants/privat24/payment/prepare', {
                        headers: {
                            'X-CSRF-Token': $("input[name='_csrf']").val()
                        },
                        type: 'POST',
                        contentType: 'application/json',
                        dataType: 'json',
                        data: JSON.stringify($(form).serializeObject())
                    }).done(function (response) {
                        var inputsHTML = '';
                        $new_form = $("<form></form>");
                        $.each(response, function (key) {
                            $new_form.append('<input type="hidden" name="' + key + '" value="' + response[key] + '">');
                        });
                        var targetCurrentHTML = $new_form.html();
                        var targetNewHTML = targetCurrentHTML + inputsHTML;
                        $(form).html(targetNewHTML);
                        callback();
                    }).fail(function (error) {
                        responseControls();
                        $('.paymentInfo').html(error.responseText);
                        console.log(error);
                    });
                    break;

                case INTERKASSA :
                    $.ajax('/merchants/interkassa/payment/prepare', {
                        headers: {
                            'X-CSRF-Token': $("input[name='_csrf']").val()
                        },
                        type: 'POST',
                        contentType: 'application/json',
                        dataType: 'json',
                        data: JSON.stringify($(form).serializeObject())
                    }).done(function (response) {
                        var inputsHTML = '';
                        $new_form = $("<form></form>");
                        $.each(response, function (key) {
                            $new_form.append('<input type="hidden" name="' + key + '" value="' + response[key] + '">');
                        });
                        var targetCurrentHTML = $new_form.html();
                        var targetNewHTML = targetCurrentHTML + inputsHTML;
                        $(form).html(targetNewHTML);
                        callback();
                    }).fail(function (error) {
                        responseControls();
                        $('.paymentInfo').html(error.responseText);
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
    
    function fillModalWindow(type,amount,currency) {
        $.ajax({
            url: '/merchants/commission',
            type: "get",
            contentType: "application/json",
            data : {"type":type, "amount":amount, "currency":currency, "merchant":merchant.find(':selected').html()}
        }).done(function (response) {
            var templateVariables = {
                amount: '__amount',
                currency: '__currency',
                merchant: '__merchant',
                percent: '__percent'
            };
            var newHTMLElements = []; 
              modalTemplate.slice().each(function(index,val){
                  newHTMLElements[index] = '<p>'+$(val).html()+'</p>';
              });
            newHTMLElements[0] = newHTMLElements[0]
                .replace(templateVariables.amount, "<span class='modal-amount'>"+amount+"</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>"+getCurrentCurrency()+"</span>")
                .replace(templateVariables.merchant, "<span class='modal-merchant'>"+merchant.find(':selected').html()+"</span>");
            newHTMLElements[1] = newHTMLElements[1]
                .replace(templateVariables.amount, "<span class='modal-amount'>" + response['commissionAmount'] + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + getCurrentCurrency() + "</span>")
                .replace(templateVariables.percent, "<span class='modal-amount'>"+response['commission'] + "%" + "</span>");
            newHTMLElements[2] = newHTMLElements[2]
                .replace(templateVariables.amount, "<span class='modal-amount'>" + response['amount'] + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + getCurrentCurrency() + "</span>");
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

    function getCurrentCurrency() {
        return $("#currency").find(":selected").html().trim().split(' ')[0];
    }
    
    if ($("#assertInputPay").length) {
        $("#assertInputPay").bind('click',function(){
            requestControls();
            fillModalWindow('INPUT',sum.val(),getCurrentCurrency());
        });
    }
    
    if ($("#assertOutputPay").length) {
        $("#assertOutputPay").bind('click',function() {
            $('.wallet_input').show();
            setTimeout("$('.wallet_input>input').focus().val('')",200);
            requestControls();
            fillModalWindow('OUTPUT',sum.val(),$('select[name="currency"]').find(':selected').data('currency'));
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
