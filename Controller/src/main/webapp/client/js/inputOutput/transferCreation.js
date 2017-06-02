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
    const $recipientHolder = $transferParamsDialog.find("#recipient");
    const notifications = new NotificationsClass();
    const urlForTransferCreate = "/transfer/request/create";
    const modalTemplate = $container.find('.transferInfo p');
    const numberFormat = '0,0.00[0000000]';

    var currency;
    var currencyName;
    var merchant;
    var merchantName;
    var merchantMinSum;
    var merchantImageId;
    var recipient;
    var recipientUserIsNeeded;
    var amount;
    var commissionPercent;
    var commissionAmount;
    var commissionMerchantPercent;
    var commissionMerchantAmount;
    var totalAmount;
    var isVoucher;

    $container.find(".start-transfer").on('click', function () {
        if (checkAmount()) {
            fillModalWindow();
            showTransferDialog();
        }
    });

    function startTransfer(button) {
        currency = $amountHolder.data("currency-id");
        currencyName = $amountHolder.data("currency-name");
        merchant = $(button).data("merchant-id");
        merchantName = $(button).data("merchant-name");
        merchantMinSum = $(button).data("merchant-min-sum");
        merchantImageId = $(button).data("merchant-image-id");
        recipientUserIsNeeded = $(button).data("recipient-user-needed");
        amount = parseFloat($amountHolder.val());
        isVoucher = $(button).data("process_type").startsWith("INVOICE");
        if (checkAmount()) {
            fillModalWindow();
            showTransferDialog();
        }
    }

    function fillModalWindow() {
        getCommission(function (response) {
            var templateVariables = {
                amount: '__amount',
                currency: '__currency',
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
            newHTMLElements[1] = newHTMLElements[1]
                .replace(templateVariables.amount, "<span class='modal-amount'>" + commissionMerchantAmount + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>")
                .replace(templateVariables.percent, "<span class='modal-amount'>" + commissionMerchantPercent + "</span>");
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

    function checkAmount() {
        return !merchantMinSum || (amount >= merchantMinSum);
    }

    function showTransferDialog(message) {
        if (recipientUserIsNeeded) {
            $transferParamsDialog.find("#recipient-input-wrapper").show();
        } else {
            $transferParamsDialog.find("#recipient-input-wrapper").show();
        }
        $transferParamsDialog.find('#request-money-operation-btns-wrapper').show();
        $transferParamsDialog.find('#response-money-operation-btns-wrapper').hide();
        $transferParamsDialog.find('#message').hide();
        $transferParamsDialog.find('#message').html(message ? message : '');
        /**/
        $transferParamsDialog.find("#continue-btn").off('click').on('click', function () {
            recipient = $recipientHolder.val();
            if (!checkTransferParamsEnter()) {
                return;
            }
            $transferParamsDialog.one('hidden.bs.modal', function () {
                showFinPassModal();
            });
            $transferParamsDialog.modal("hide");
        });
        /**/
        $transferParamsDialog.modal();
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
        $transferParamsDialog.find('#recipient-input-wrapper').hide();
        $transferParamsDialog.find('#response-money-operation-btns-wrapper').show();
        $transferParamsDialog.find('#message').show();
        $transferParamsDialog.find('#message').html(message ? message : '');
        $transferParamsDialog.modal();
    }

    function checkTransferParamsEnter() {
        return /^\D+[\w\d\-_]+/.test(value);

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

