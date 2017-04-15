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
    const $refillDetailedParamsDialog = $container.find('#dialog-refill-detailed-params-enter');
    const $loadingDialog = $container.find('#loading-process-modal');
    const $amountHolder = $container.find("#sum");
    const notifications = new NotificationsClass();
    const urlForRefillCreate = "/refill/request/create";
    const modalTemplate = $container.find('.paymentInfo p');
    const numberFormat = '0,0.00[0000000]';
    const phrases = {
        "bankNotSelected": $container.find("#bank-not-selected").html(),
    };

    var currency;
    var currencyName;
    var merchant;
    var merchantName;
    var merchantMinSum;
    var merchantImageId;
    var merchantIsSimpleInvoice;
    var amount;
    var commissionPercent;
    var commissionAmount;
    var totalAmount;
    var bankDataList;

    $container.find(".start-refill").on('click', function () {
        startRefill(this);
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

    function fillModalWindow() {
        getCommission(function (response) {
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
                .replace(templateVariables.amount, "<span class='modal-amount'>" + commissionAmount + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>")
                .replace(templateVariables.percent, "<span class='modal-amount'>" + commissionPercent + "</span>");
            newHTMLElements[2] = newHTMLElements[2]
                .replace(templateVariables.amount, "<span class='modal-amount'>" + totalAmount + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + currencyName + "</span>");
            var newHTML = '';
            $.each(newHTMLElements, function (index) {
                newHTML += newHTMLElements[index];
            });
            $('.paymentInfo').html(newHTML);
            $('.merchantError').hide();
        });
    }

    function checkAmount() {
        return !merchantMinSum || (amount >= merchantMinSum);
    }

    function showRefillDialog(message) {
        if (merchantIsSimpleInvoice) {
            performRefill();
        } else {
            $refillParamsDialog.find('#request-money-operation-btns-wrapper').show();
            $refillParamsDialog.find('#destination-input-wrapper').show();
            $refillParamsDialog.find('#response-money-operation-btns-wrapper').hide();
            $refillParamsDialog.find('#message').hide();
            $refillParamsDialog.find('#message').html(message ? message : '');
            $refillParamsDialog.find('#payment-qr').html('');
            $refillParamsDialog.find("#continue-btn").off('click').on('click', function () {
                if (!checkRefillParamsEnter()) {
                    return;
                }
                $refillParamsDialog.one('hidden.bs.modal', function () {
                    performRefill();
                });
                $refillParamsDialog.modal("hide");
            });
            $refillParamsDialog.modal();
        }
    }

    function checkRefillParamsEnter() {
        return true;
    }

    function performRefill() {
        var data = {
            currency: currency,
            merchant: merchant,
            sum: amount,
            merchantImage: merchantImageId,
            operationType: operationType,
        };
        if (merchantIsSimpleInvoice) {
            var refillDetailedParamsDialogResult = false;
            $refillDetailedParamsDialog.find("#invoiceSubmit").off("click").one("click", function () {
                refillDetailedParamsDialogResult = true;
                $refillDetailedParamsDialog.modal("hide");
            });
            $refillDetailedParamsDialog.one("hidden.bs.modal", function () {
                if (refillDetailedParamsDialogResult) {
                    data.recipientBankCode = $refillDetailedParamsDialog.find("#bank-code").val();
                    data.recipientBankName = $refillDetailedParamsDialog.find("#bank-name").val();
                    data.address = $refillDetailedParamsDialog.find("#user-account").val();
                    data.userFullName = $refillDetailedParamsDialog.find("#user-full-name").val();
                    data.remark = $refillDetailedParamsDialog.find("#remark").val();
                    sendRequest(data);
                }
            });
            showRefillDetailDialog();
        } else {
            sendRequest(data);
        }
    }

    function sendRequest(data) {
        $loadingDialog.one("shown.bs.modal", function () {
            $.ajax({
                url: urlForRefillCreate,
                async: false,
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
            }).success(function (result) {
                if (!result || !result['redirectionUrl']) {
                    var qrTag = result['qr'] ? "<img src='https://chart.googleapis.com/chart?chs=100x100&chld=L|2&cht=qr&chl=" + result['qr'] : '';
                    showRefillDialogAfterCreation(result['message'], qrTag);
                    notifications.getNotifications();
                } else {
                    window.location = result['redirectionUrl'];
                }
            }).complete(function () {
                $loadingDialog.modal("hide");
            });
        });
        $loadingDialog.modal({
            backdrop: 'static'
        });
    }

    function showRefillDetailDialog() {
        resetForm();
        $refillDetailedParamsDialog.find("#amount").html(numeral(amount).format(numberFormat));
        $refillDetailedParamsDialog.find("#commission-percent").html(commissionPercent);
        $refillDetailedParamsDialog.find("#commission-amount").html(numeral(commissionAmount).format(numberFormat));
        $refillDetailedParamsDialog.find("#total-amount").html(numeral(totalAmount).format(numberFormat));
        $refillDetailedParamsDialog.find(".currency").html(currencyName);
        getBankDataList(function () {
            var $bankSelect = $refillDetailedParamsDialog.find("#bank-data-list");
            $bankSelect.empty();
            var $bankItem = $("<option> </option>");
            $bankItem.val(-1);
            $bankItem.attr("data-bank-id", "");
            $bankItem.attr("data-bank-code", "");
            $bankItem.attr("data-bank-name", "");
            $bankItem.attr("data-bank-account", "");
            $bankItem.attr("data-bank-recipient", "");
            $bankItem.html(phrases.bankNotSelected);
            $bankSelect.append($bankItem.clone());
            /**/
            bankDataList.forEach(function (bank) {
                $bankItem.val(bank.id);
                $bankItem.attr("data-bank-id", bank.id);
                $bankItem.attr("data-bank-code", bank.code);
                $bankItem.attr("data-bank-name", bank.name);
                $bankItem.attr("data-bank-account", bank.accountNumber);
                $bankItem.attr("data-bank-recipient", bank.recipient);
                $bankItem.html(bank.name);
                $bankSelect.append($bankItem.clone());
            });
        });
        $refillDetailedParamsDialog.modal({
            backdrop: 'static'
        });
    }

    function showRefillDialogAfterCreation(message, qrTag) {
        $refillParamsDialog.find('#request-money-operation-btns-wrapper').hide();
        $refillParamsDialog.find('#destination-input-wrapper').hide();
        $refillParamsDialog.find('#response-money-operation-btns-wrapper').show();
        $refillParamsDialog.find('#message').show();
        $refillParamsDialog.find('#message').html(message ? message : '');
        $refillParamsDialog.find('#payment-qr').html(qrTag ? qrTag : '');
        $refillParamsDialog.modal();
    }

    function getCommission(callback) {
        $.ajax({
            url: '/refill/commission',
            async: false,
            type: "get",
            contentType: "application/json",
            data: {"amount": amount, "currency": currencyName, "merchant": merchantName}
        }).success(function (response) {
            amount = response['amount'];
            commissionPercent = response['commission'];
            commissionAmount = response['commissionAmount'];
            totalAmount = response['totalAmount'];
            var additional = response['addition'];
            $refillDetailedParamsDialog.find("#additional").html(additional);
            if (additional != 0) {
                $refillDetailedParamsDialog.find("#additional-wrapper").show();
            } else {
                $refillDetailedParamsDialog.find("#additional-wrapper").hide();
            }
            if (callback) {
                callback();
            }
        });
    }

    function getBankDataList(callback) {
        $.ajax({
            url: '/refill/banks',
            async: false,
            type: "get",
            contentType: "application/json",
            data: {"currencyId": currency}
        }).success(function (response) {
            bankDataList = response;
            if (callback) {
                callback();
            }
        });
    }

});


