/**
 * Created by ValkSam
 */

$(function transferCreation() {
    const $container = $("#merchants-transfer-center");
    const operationType = $container.find("#operationType").html();
    const $transferParamsDialog = $container.find('#dialog-transfer-creation');
    const $loadingDialog = $container.find('#loading-process-modal');
    const $finPasswordDialog = $container.find('#finPassModal');
    const $amountHolder = $container.find("#sum");
    const $recipientHolder = $container.find("#recipient");
    const $isVoucherHolder = $container.find("#is-voucher");
    const notifications = new NotificationsClass();
    const urlForTransferCreate = "/transfer/request/create";
    const modalTemplate = $container.find('.transferInfo p');
    const numberFormat = '0,0.00[0000000]';

    var currency;
    var currencyName;
    var minSum;
    var maxSum;
    var recipient;
    var isVoucher;
    var amount;
    var commissionPercent;
    var commissionAmount;
    var totalAmount;

    $container.find(".start-transfer").on('click', function () {
        startTransfer(this);
    });

    function startTransfer(button) {
        currency = $amountHolder.data("currency-id");
        currencyName = $amountHolder.data("currency-name");
        minSum = $amountHolder.data("min-amount");
        maxSum = $amountHolder.data("max-amount");
        recipient = $recipientHolder.val();
        isVoucher = $isVoucherHolder.val();
        amount = parseFloat($amountHolder.val());
        if (checkTransferParamsEnter()) {
            fillModalWindow();
            showWithdrawDialog();
        }
    }

    function fillModalWindow() {
        getCommission(function (response) {
            var templateVariables = {
                amount: '__amount',
                currency: '__currency',
                recipient: '__recipient',
                percent: '__percent',
                transferType: '__transferType'
            };
            var newHTMLElements = [];
            modalTemplate.slice().each(function (index, val) {
                newHTMLElements[index] = '<p>' + $(val).html() + '</p>';
            });
            newHTMLElements[0] = newHTMLElements[0]
                .replace(templateVariables.transferType, "<span class='modal-amount'>" + (isVoucher ? " will create the VOUCHER" : " will transfer ") + "</span>")
                .replace(templateVariables.amount, "<span class='modal-amount'>" + amount + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>")
                .replace(templateVariables.recipient, "<span class='modal-merchant'>" + (recipient ? " to " + recipient : "") + "</span>");
            newHTMLElements[1] = newHTMLElements[1]
                .replace(templateVariables.amount, "<span class='modal-amount'>" + commissionAmount + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>")
                .replace(templateVariables.percent, "<span class='modal-amount'>" + commissionPercent + "</span>");
            newHTMLElements[3] = newHTMLElements[2]
                .replace(templateVariables.amount, "<span class='modal-amount'>" + totalAmount + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>");
            var newHTML = '';
            $.each(newHTMLElements, function (index) {
                newHTML += newHTMLElements[index];
            });
            $('.transferInfo').html(newHTML);
        });
    }

    function showWithdrawDialog(message) {
        showFinPassModal();
        $withdrawParamsDialog.modal();
    }

    function showFinPassModal() {
        $finPasswordDialog.find('#check-fin-password-button').off('click').one('click', function (e) {
            e.preventDefault();
            var finPassword = $finPasswordDialog.find("#finpassword").val();
            $finPasswordDialog.one("hidden.bs.modal", function () {
                performTransfer(finPassword);
            });
            $finPasswordDialog.modal("hide");
        });
        $finPasswordDialog.modal({
            backdrop: 'static'
        });
    }

    function performTransfer(finPassword) {
        var data = {
            currency: currency,
            sum: amount,
            recipient: recipient,
            isVoucher: isVoucher,
            operationType: operationType,
        };
        sendRequest(data, finPassword);
    }

    function sendRequest(data, finPassword) {
        $loadingDialog.one("shown.bs.modal", function () {
            $.ajax({
                url: urlForTransferCreate + '?finpassword=' + finPassword,
                async: true,
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
            }).success(function (result) {
                showTransferDialogAfterCreation(result['message']);
                notifications.getNotifications();
            }).complete(function () {
                $loadingDialog.modal("hide");
            });
        });
        $loadingDialog.modal({
            backdrop: 'static'
        });
    }

    function showTransferDialogAfterCreation(message) {
        $transferParamsDialog.find('#request-money-operation-btns-wrapper').hide();
        $transferParamsDialog.find('#destination-input-wrapper').hide();
        $transferParamsDialog.find('#response-money-operation-btns-wrapper').show();
        $transferParamsDialog.find('#message').show();
        $transferParamsDialog.find('#message').html(message ? message : '');
        $transferParamsDialog.modal();
    }

    function checkTransferParamsEnter() {
        const NICKNAME_REGEX = /^\D+[\w\d\-_]+/;
        return (!minSum || (amount >= minSum))
            && (!maxSum || (amount >= maxSum))
            && NICKNAME_REGEX.test(value);

    }

    function getCommission(callback) {
        $.ajax({
            url: '/transfer/commission',
            async: false,
            type: "get",
            contentType: "application/json",
            data: {"amount": amount, "currency": currency}
        }).success(function (response) {
            amount = response['amount'];
            commissionAmount = response['companyCommissionAmount'];
            totalAmount = response['resultAmount'];
            if (callback) {
                callback();
            }
        });
    }

});


