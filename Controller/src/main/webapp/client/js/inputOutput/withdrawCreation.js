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
    const $loadingDialog = $container.find('#loading-process-modal');
    const $finPasswordDialog = $container.find('#finPassModal');
    const $amountHolder = $container.find("#sum");
    const $destinationHolder = $withdrawParamsDialog.find("#walletUid");
    const notifications = new NotificationsClass();
    const urlForWithdrawCreate = "/withdraw/request/create";
    const modalTemplate = $container.find('.paymentInfo p');
    const numberFormat = '0,0.00[0000000]';
    const phrases = {
        "bankNotSelected": $container.find("#bank-not-selected").html(),
        "enterOtherBankPhrase": $container.find("#enter-other-bank-phrase").html(),
    };

    var currency;
    var currencyName;
    var merchant;
    var merchantName;
    var merchantMinSum;
    var merchantImageId;
    var destination;
    var merchantIsSimpleInvoice;
    var amount;
    var commissionPercent;
    var commissionAmount;
    var totalAmount;
    var bankDataList;

    $container.find(".start-withdraw").on('click', function () {
        startWithdraw(this);
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

    function showWithdrawDialog(message) {
        if (merchantIsSimpleInvoice) {
            showFinPassModal();
        } else {
            $withdrawParamsDialog.find('#request-money-operation-btns-wrapper').show();
            $withdrawParamsDialog.find('#response-money-operation-btns-wrapper').hide();
            $withdrawParamsDialog.find('#message').hide();
            $withdrawParamsDialog.find('#message').html(message ? message : '');
            /**/
            $withdrawParamsDialog.find("#continue-btn").off('click').on('click', function () {
                destination = $destinationHolder.val();
                if (!checkWithdrawParamsEnter()) {
                    return;
                }
                $withdrawParamsDialog.one('hidden.bs.modal', function () {
                    showFinPassModal();
                });
                $withdrawParamsDialog.modal("hide");
            });
            /**/
            $withdrawParamsDialog.modal();
        }
    }

    function showFinPassModal() {
        $finPasswordDialog.find('#check-fin-password-button').off('click').one('click', function (e) {
            e.preventDefault();
            var finPassword = $finPasswordDialog.find("#finpassword").val();
            $finPasswordDialog.one("hidden.bs.modal", function () {
                performWithdraw(finPassword);
            });
            $finPasswordDialog.modal("hide");
        });
        $finPasswordDialog.modal({
            backdrop: 'static'
        });
    }

    function performWithdraw(finPassword) {
        var data = {
            currency: currency,
            merchant: merchant,
            sum: amount,
            destination: destination,
            merchantImage: merchantImageId,
            operationType: operationType,
        };
        if (merchantIsSimpleInvoice) {
            var withdrawDetailedParamsDialogResult = false;
            $withdrawDetailedParamsDialog.find("#invoiceSubmit").off("click").one("click", function () {
                withdrawDetailedParamsDialogResult = true;
                $withdrawDetailedParamsDialog.modal("hide");
            });
            $withdrawDetailedParamsDialog.one("hidden.bs.modal", function () {
                if (withdrawDetailedParamsDialogResult) {
                    data.recipientBankCode = $withdrawDetailedParamsDialog.find("#bank-code").val();
                    var bankId = $withdrawDetailedParamsDialog.find('#bank-data-list').val();
                    data.recipientBankName = bankId == 0 ?
                        $withdrawDetailedParamsDialog.find("#other-bank").val() :
                        $withdrawDetailedParamsDialog.find('#bank-data-list').find('option:selected').text();
                    data.destination = $withdrawDetailedParamsDialog.find("#user-account").val();
                    data.userFullName = $withdrawDetailedParamsDialog.find("#user-full-name").val();
                    data.remark = $withdrawDetailedParamsDialog.find("#remark").val();
                    sendRequest(data, finPassword);
                }
            });
            showWithdrawDetailDialog();
        } else {
            sendRequest(data, finPassword);
        }
    }

    function sendRequest(data, finPassword){
        $loadingDialog.one("shown.bs.modal", function () {
            $.ajax({
                url: urlForWithdrawCreate + '?finpassword=' + finPassword,
                async: true,
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
            }).complete(function () {
                $loadingDialog.modal("hide");
            });
        });
        $loadingDialog.modal({
            backdrop: 'static'
        });
    }

    function showWithdrawDetailDialog() {
        resetForm();
        $withdrawDetailedParamsDialog.find("#amount").html(numeral(amount).format(numberFormat));
        $withdrawDetailedParamsDialog.find("#commission-percent").html(commissionPercent);
        $withdrawDetailedParamsDialog.find("#commission-amount").html(numeral(commissionAmount).format(numberFormat));
        $withdrawDetailedParamsDialog.find("#total-amount").html(numeral(totalAmount).format(numberFormat));
        $withdrawDetailedParamsDialog.find(".currency").html(currencyName);
        getBankDataList(function () {
            var $bankSelect = $withdrawDetailedParamsDialog.find("#bank-data-list");
            $bankSelect.empty();
            var $bankItem = $("<option> </option>");
            $bankItem.val(-1);
            $bankItem.attr("data-bank-id", "");
            $bankItem.attr("data-bank-code", "");
            $bankItem.attr("data-bank-name", "");
            $bankItem.html(phrases.bankNotSelected);
            $bankSelect.append($bankItem.clone());
            /**/
            bankDataList.forEach(function (bank) {
                $bankItem.val(bank.id);
                $bankItem.attr("data-bank-id", bank.id);
                $bankItem.attr("data-bank-code", bank.code);
                $bankItem.attr("data-bank-name", bank.name);
                $bankItem.html(bank.name);
                $bankSelect.append($bankItem.clone());
            });
            /**/
            $bankItem.val(0);
            $bankItem.attr("data-bank-id", "");
            $bankItem.attr("data-bank-code", "");
            $bankItem.attr("data-bank-name", "");
            $bankItem.html(phrases.enterOtherBankPhrase);
            $bankSelect.append($bankItem.clone());
        });
        $withdrawDetailedParamsDialog.modal({
            backdrop: 'static'
        });
    }

    function showWithdrawDialogAfterCreation(message) {
        $withdrawParamsDialog.find('#request-money-operation-btns-wrapper').hide();
        $withdrawParamsDialog.find('#destination-input-wrapper').hide();
        $withdrawParamsDialog.find('#response-money-operation-btns-wrapper').show();
        $withdrawParamsDialog.find('#message').show();
        $withdrawParamsDialog.find('#message').html(message ? message : '');
        $withdrawParamsDialog.modal();
    }

    function checkWithdrawParamsEnter() {
        return merchantIsSimpleInvoice || destination.length > 3;
    }

    function getCommission(callback) {
        $.ajax({
            url: '/withdraw/commission',
            async: false,
            type: "get",
            contentType: "application/json",
            data: {"amount": amount, "currency": currency, "merchant": merchant}
        }).success(function (response) {
            amount = response['amount'];
            commissionPercent = response['commission'];
            commissionAmount = response['commissionAmount'];
            totalAmount = response['totalAmount'];
            var additional = response['addition'];
            $withdrawDetailedParamsDialog.find("#additional").html(additional);
            if (additional != 0) {
                $withdrawDetailedParamsDialog.find("#additional-wrapper").show();
            } else {
                $withdrawDetailedParamsDialog.find("#additional-wrapper").hide();
            }
            if (callback) {
                callback();
            }
        });
    }

    function getBankDataList(callback) {
        $.ajax({
            url: '/withdraw/banks',
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


