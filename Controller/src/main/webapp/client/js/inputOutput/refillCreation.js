/**
 * Created by ValkSam on 10.04.2017.
 */
/**
 * Created by ValkSam on 10.04.2017.
 */

$(function refillCreation() {
    const $container = $("#merchants-input-center");
    const operationType = $container.find("#operationType").html();
    const $refillParamsDialog = $container.find('#dialog-refill-creation');
    const $finPasswordDialog = $container.find('#finPassModal');
    const $amountHolder = $container.find("#sum");
    const notifications = new NotificationsClass();
    const urlForRefillWithMerchant = "/refill/request/merchant/create";
    const urlForRefillWithInvoice = "/refill/request/invoice/detail/prepare_to_entry";
    const modalTemplate = $container.find('.paymentInfo p');

    var currency;
    var currencyName;
    var merchant;
    var merchantName;
    var merchantMinSum;
    var merchantImageId;
    var merchantIsSimpleInvoice;
    var amount;

    $container.find(".start-refill").on('click', function () {
        startRefill(this);
    });

    $refillParamsDialog.find("#continue-btn").on('click', function () {
        if (!checkRefillParamsEnter()) {
            return;
        }
        $withdrawParamsDialog.one('hidden.bs.modal', function () {
            showFinPassModal();
        });
        $withdrawParamsDialog.modal("hide");
    });

    function startRefill(button) {
        currency = $(button).data("currency-id");
        currencyName = $(button).data("currency-name");
        merchant = $(button).data("merchant-id");
        merchantName = $(button).data("merchant-name");
        merchantMinSum = $(button).data("merchant-min-sum");
        merchantImageId = $(button).data("merchant-image-id");
        merchantIsSimpleInvoice = $(button).data("simple-invoice");
        amount = parseFloat($amountHolder.val());
        if (checkAmount()) {
            fillModalWindow();
            showRefillDialog();
        }
    }

    function checkAmount() {
        return !merchantMinSum || (amount >= merchantMinSum);
    }

    function showRefillDialog(message) {
        $withdrawParamsDialog.find('#request-money-operation-btns-wrapper').show();
        $withdrawParamsDialog.find('#destination-input-wrapper').show();
        $withdrawParamsDialog.find('#response-money-operation-btns-wrapper').hide();
        $withdrawParamsDialog.find('#message').hide();
        $withdrawParamsDialog.find('#message').html(message ? message : '');
        $withdrawParamsDialog.find('#paymentQR').html('');
        $refillParamsDialog.modal();
    }

    function showRefillDialogAfterCreation(message, qr) {
        $withdrawParamsDialog.find('#request-money-operation-btns-wrapper').hide();
        $withdrawParamsDialog.find('#destination-input-wrapper').hide();
        $withdrawParamsDialog.find('#response-money-operation-btns-wrapper').show();
        $withdrawParamsDialog.find('#message').show();
        $withdrawParamsDialog.find('#message').html(message ? message : '');
        $withdrawParamsDialog.find('#paymentQR').html(qr ? qr : '');
        $withdrawParamsDialog.modal();
    }

    function checkRefillParamsEnter() {
        return true;
    }

    function showFinPassModal() {
        $finPasswordDialog.find('#check-fin-password-button').off('click').one('click', function (e) {
            e.preventDefault();
            var finPassword = $finPasswordDialog.find("#finpassword").val();
            performRefill(finPassword);
        });
        $finPasswordDialog.modal({
            backdrop: 'static'
        });
    }

    function fillModalWindow() {
        $.ajax({
            url: '/merchants/commission',
            type: "get",
            contentType: "application/json",
            data: {"type": operationType, "amount": amount, "currency": currencyName, "merchant": merchantName}
        }).done(function (response) {
            var templateVariables = {
                amount: '__amount',
                currency: '__currency',
                merchant: '__merchant',
                percent: '__percent'
            };
            var newHTMLElements = [];
            modalTemplate.slice().each(function (index, val) {
                newHTMLElements[index] = '<p>' + $(val).html() + '</p>';
            });
            newHTMLElements[0] = newHTMLElements[0]
                .replace(templateVariables.amount, "<span class='modal-amount'>" + amount + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>")
                .replace(templateVariables.merchant, "<span class='modal-merchant'>" + merchantName + "</span>");
            newHTMLElements[1] = newHTMLElements[1]
                .replace(templateVariables.amount, "<span class='modal-amount'>" + response['commissionAmount'] + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>")
                .replace(templateVariables.percent, "<span class='modal-amount'>" + response['commission'] + "</span>");
            newHTMLElements[2] = newHTMLElements[2]
                .replace(templateVariables.amount, "<span class='modal-amount'>" + response['amount'] + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>");
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

    function performRefill(finPassword) {
        var url = merchantIsSimpleInvoice ? urlForRefillWithInvoice : urlForRefillWithMerchant;
        var data = {
            currency: currency,
            merchant: merchant,
            sum: amount,
            destination: destination,
            merchantImage: merchantImageId,
            operationType: operationType,
        };
        $finPasswordDialog.modal("hide");
        $.ajax({
            url: url + '?finpassword=' + finPassword,
            async: false,
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
        }).success(function (result) {
            if (!result || !result['redirectionUrl']) {
                var qr = result['qr'] ? "<img src='https://chart.googleapis.com/chart?chs=100x100&chld=L|2&cht=qr&chl=" + result['qr'] : '';
                showRefillDialogAfterCreation(result['message'], qr);
                notifications.getNotifications();
            } else {
                window.location = result['redirectionUrl'];
            }
        });
    }

});


