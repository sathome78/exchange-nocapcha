/**
 * Created by ValkSam on 10.04.2017.
 */
/**
 * Created by ValkSam on 10.04.2017.
 */

$(function withdrawCreation() {
    const $container = $("#merchants-output-center");
    const operationType = $container.find("#operationType").html();
    const $withdrawParamsDialog = $container.find('#dialog-withdraw-creation');
    const $withdrawDetailedParamsDialog = $container.find('#dialog-withdraw-detailed-params-enter');
    const $finPasswordDialog = $container.find('#finPassModal');
    const $amountHolder = $container.find("#sum");
    const $destinationHolder = $withdrawParamsDialog.find("#walletUid");
    const notifications = new NotificationsClass();
    const urlForWithdrawCreate = "/withdraw/request/merchant/create";
    const modalTemplate = $container.find('.paymentInfo p');

    var currency;
    var currencyName;
    var merchant;
    var merchantName;
    var merchantMinSum;
    var merchantImageId;
    var destination;
    var merchantIsSimpleInvoice;
    var amount;

    $container.find(".start-withdraw").on('click', function () {
        startWithdraw(this);
    });

    $withdrawParamsDialog.find("#continue-btn").on('click', function () {
        destination = $destinationHolder.val();
        if (!checkWithdrawParams()) {
            return;
        }
        $withdrawParamsDialog.one('hidden.bs.modal', function () {
            showFinPassModal();
        });
        $withdrawParamsDialog.modal("hide");
    });

    function startWithdraw(button) {
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
            showWithdrawDialog();
        }
    }

    function checkAmount() {
        return !merchantMinSum || (amount >= merchantMinSum);
    }

    function showWithdrawDialog(message) {
        $withdrawParamsDialog.find('#request-money-operation-btns-wrapper').show();
        if (merchantIsSimpleInvoice) {
            $withdrawParamsDialog.find('#destination-input-wrapper').hide();
        } else {
            $withdrawParamsDialog.find('#destination-input-wrapper').show();
        }
        $withdrawParamsDialog.find('#response-money-operation-btns-wrapper').hide();
        $withdrawParamsDialog.find('#message').hide();
        $withdrawParamsDialog.find('#message').html(message ? message : '');
        $withdrawParamsDialog.modal();
    }

    function showWithdrawDialogAfterCreation(message) {
        $withdrawParamsDialog.find('#request-money-operation-btns-wrapper').hide();
        $withdrawParamsDialog.find('#destination-input-wrapper').hide();
        $withdrawParamsDialog.find('#response-money-operation-btns-wrapper').show();
        $withdrawParamsDialog.find('#message').show();
        $withdrawParamsDialog.find('#message').html(message ? message : '');
        $withdrawParamsDialog.modal();
    }

    function checkWithdrawParams() {
        return destination.length > 3;
    }

    function showFinPassModal() {
        $finPasswordDialog.find('#check-fin-password-button').off('click').one('click', function (e) {
            e.preventDefault();
            var finPassword = $finPasswordDialog.find("#finpassword").val();
            performWithdraw(finPassword);
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

    function performWithdraw(finPassword) {
        $finPasswordDialog.modal("hide");
        var data = {
            currency: currency,
            merchant: merchant,
            sum: amount,
            destination: destination,
            merchantImage: merchantImageId,
            operationType: operationType,
        };
        if (merchantIsSimpleInvoice) {
            $withdrawDetailedParamsDialog.modal();
            data.recipientBankCode = $withdrawDetailedParamsDialog.find("bankId").val(); //TODO или bankCode - что используется на бэке?
            data.userFullName = $withdrawDetailedParamsDialog.find("userFullName").val();
            data.remark = $withdrawDetailedParamsDialog.find("remark").val();
        }
        $.ajax({
            url: urlForWithdrawCreate + '?finpassword=' + finPassword,
            async: false,
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
        }).success(function (result) {
            if (!result || !result['redirectionUrl']) {
                showWithdrawDialogAfterCreation(result['message']);
                notifications.getNotifications();
            } else {
                window.location = result['redirectionUrl'];
            }
        });
    }

});


