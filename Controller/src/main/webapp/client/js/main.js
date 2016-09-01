
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

Number.prototype.noExponents= function(){
    var data= String(this).split(/[eE]/);
    if(data.length== 1) return data[0];

    var  z= '', sign= this<0? '-':'',
        str= data[0].replace('.', ''),
        mag= Number(data[1])+ 1;

    if(mag<0){
        z= sign + '0.';
        while(mag++) z += '0';
        return z + str.replace(/^\-/,'');
    }
    mag -= str.length;
    while(mag--) z += '0';
    return str + z;
}

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
    const INVOICE = 'Invoice';
    const EDC = 'EDC';

    const NO_ACTION = 'javascript:void(0);';

    var currency = $('#currency');
    var merchant = $('#merchant');
    var merchantName;
    var merchantMinSum;
    var fractionalAmount;
    var merchantImageId;
    var sum = $('#sum');
    var operationType = $('#operationType');
    var modalTemplate = $('.paymentInfo p');
    var button = $('#payment').find('button');
    button.prop('disabled',true);
    var merchantsData;

    $(".input-block-wrapper__input").prop("autocomplete", "off");
    $(".numericInputField").prop("autocomplete", "off");
    $(".numericInputField")
        .keypress(
            function (e) {
                var decimal = $(this).val().split('.')[1];
                if (decimal && decimal.length >= fractionalAmount+1) {
                    return false;
                }
                if (e.charCode >= 48 && e.charCode <= 57 || e.charCode == 46 || e.charCode == 0) {
                    if (e.key == '.' && $(this).val().indexOf('.') >= 0) {
                        return false;
                    }
                    var str = $(this).val() + e.key;
                    if (str.length > 1 && str.indexOf('0') == 0 && str.indexOf('.') != 1) {
                        $(this).val("");
                        return false
                    }
                } else {
                    return false;
                }
                return true;
            }
        )
        .on('input', function (e) {
            var val = $(this).val();
            var regx = /^(^[1-9]+\d*((\.{1}\d*)|(\d*)))|(^0{1}\.{1}\d*)|(^0{1})$/;
            var result = val.match(regx);
            var maxSum = 999999.99;
            if (!result || result[0] != val) {
                $(this).val('');
            }
            if ( val >= maxSum){
                $(this).val(maxSum);
            }
            if (operationType.val() === 'OUTPUT') {
                maxWalletSum = parseFloat($("#currencyFull").val().split(' ')[1]);
                if ( val >= maxWalletSum){
                    $(this).val(maxWalletSum);
                }
            }
            var decimal = $(this).val().split('.')[1];
            if (decimal && decimal.length > fractionalAmount) {
                $(this).val($(this).val().slice(0,-1));

            }
            if (parseFloat(sum.val()) > 0){
                button.prop('disabled',false);
            }else {
                button.prop('disabled',true);
            }
        });

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
                fractionalAmount = merchantsData[index].minSum.noExponents().split('.')[1].length;
            }
        });
        if (optionsHTML==='') {
            merchant.fadeOut();
            button.prop('disabled', true);
        } else {
            merchant.fadeIn();
            //button.prop('disabled', false);
        }
        merchant.empty();
        merchant.html(optionsHTML);
        if (isCorrectSum()) {
            //button.prop('disabled',false);
        } else {
            button.prop('disabled',true);
        }
    }

    function resetFormAction(operationType,merchant,form) {
        var formAction = {
            yandex:'/merchants/yandexmoney/payment/prepare',
            blockchainDeposit:'/merchants/bitcoin/payment/provide',
            perfectDeposit:'https://perfectmoney.is/api/step1.asp',
            advcash:'/merchants/advcash/payment/prepare',
            liqpay:'/merchants/liqpay/payment/prepare',
            nixmoney:'/merchants/nixmoney/payment/prepare',
            yandex_kassa:'http://din24.net/index.php?route=acc/success/order',
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
                case EDC:
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
                //$('#currency').find(':selected').html(response['balance']);
                //$('#currencyFull')..html(response['balance']);
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
                    $.ajax('/merchants/bitcoin/payment/prepare', {
                        headers: {
                            'X-CSRF-Token': $("input[name='_csrf']").val()
                        },
                        type: 'POST',
                        contentType: 'application/json;charset=utf-8',
                        dataType: 'json',
                        data: JSON.stringify($(form).serializeObject())
                    }).done(function (response) {
                        $('#inputPaymentProcess')
                            .prop('disabled', false)
                            .html($('#mrcht-ready').val());

                        $.each(response, function (key) {
                            if(key=='notification'){
                                $('.paymentInfo').html(response[key]);
                            }
                            if(key=='qr'){
                                $('.paymentQR').html("<img src='https://chart.googleapis.com/chart?chs=100x100&chld=L|2&cht=qr&chl=" + response[key] + "'>");
                            }
                        });
                        responseControls();
                    }).fail(function (error, jqXHR, textStatus) {
                        responseControls();
                        $('.paymentInfo').html(error.responseText);
                        console.log(textStatus);
                    });
                    break;
                case EDC :
                    $('#inputPaymentProcess')
                        .prop('disabled', true)
                        .html($('#mrcht-waiting').val());
                    $.ajax('/merchants/edc/payment/prepare', {
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

                case YANDEX_KASSA :
                    $.ajax('/merchants/yandex_kassa/payment/prepare', {
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
                case INVOICE :
                    $('#inputPaymentProcess')
                        .html($('#mrcht-waiting').val())
                        .prop('disabled', true);
                    $.ajax('/merchants/invoice/payment/prepare', {
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
                default:
                    callback();
            }
        }
    }

    function isCorrectSum() {
        var result = false;
        if (merchantName !== 'Blockchain'){
                var targetSum = parseFloat(sum.val());
                if (targetSum >= merchantMinSum) {
                    return result = true;
                }
            }
        return true;
    }

    function fillModalWindow(type,amount,currency) {
        $.ajax({
            url: '/merchants/commission',
            type: "get",
            contentType: "application/json",
            data : {"type":type, "amount":amount, "currency":currency, "merchant":merchantName}
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
                .replace(templateVariables.merchant, "<span class='modal-merchant'>"+merchantName+"</span>");
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
        var targetMerchant = merchantName;
        var paymentForm = $('#payment').clone();
        paymentForm.append('<input type="hidden" name="merchant" value="' + merchant + '">');
        paymentForm.append('<input type="hidden" name="merchantImage" value="' + merchantImageId + '">');
        resetFormAction(operationType.val(), targetMerchant,paymentForm);
        resetPaymentFormData(targetMerchant,paymentForm,function(){
            paymentForm.submit();
        });
    }

    function getCurrentCurrency() {
        return $("#currencyName").val();
    }

    $('button[name=assertInputPay]').click(function()  {
        var arr = this.value.split(':');
        merchant = arr[0];
        merchantName = arr[1];
        merchantMinSum = parseFloat(arr[2]);
        merchantImageId = parseFloat(arr[3]);

        $('.paymentInfo').html("");
        $('.paymentQR').html("");
        requestControls();
        $('#inputPaymentProcess')
        .html($('#mrcht-waiting').val())
            .prop('disabled', false);

        $('#inputPaymentProcess')
            .prop('disabled', false)
            .html($('#mrcht-ready').val());

        if (isCorrectSum()) {
            fillModalWindow('INPUT', sum.val(), getCurrentCurrency());
        }
    });

        $('button[name=assertOutputPay]').click(function()  {
            var arr = this.value.split(':');
            merchant = arr[0];
            merchantName = arr[1];
            merchantMinSum = parseFloat(arr[2]);
            merchantImageId = parseFloat(arr[3]);

            $('.wallet_input').show();
            setTimeout("$('.wallet_input>input').focus().val('')",200);
            requestControls();
            fillModalWindow('OUTPUT',sum.val(),getCurrentCurrency());
        });

    $('#inputPaymentProcess').on('click', function () {
        submitProcess();
    });

    $("#outputPaymentProcess").on('click', function () {
        var uid = $("input[name='walletUid']").val();
        if (uid.length>5){
            $("#destination").val(uid);
            submitProcess();
            setTimeout(function()
            {
                location.reload();
            },8000);
        }
    });

});