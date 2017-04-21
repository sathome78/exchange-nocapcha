/**
 * Created by Valk on 27.06.2016.
 */

function InputOutputClass(currentCurrencyPair) {
    if (InputOutputClass.__instance) {
        return InputOutputClass.__instance;
    } else if (this === window) {
        return new InputOutputClass(currentCurrencyPair);
    }
    InputOutputClass.__instance = this;
    /**/
    /**/
    var that = this;
    var timeOutIdForInputOutputData;
    var refreshIntervalForInputOutputData = 30000 * REFRESH_INTERVAL_MULTIPLIER;
    /**/
    var showLog = false;
    /**/
    var $inputoutputContainer = $('#inputoutput');
    var tableId = "inputoutput-table";
    var inputoutputCurrencyPairSelector;
    var tablePageSize = 20;
    /**/
    const numberFormat = '0,0.00[0000000]';
    const $pageContainer = $('#myinputoutput');
    const $refillDetailedParamsDialog = $pageContainer.find('#dialog-refill-confirmation-params-enter');
    const phrases = {
        "bankNotSelected": $pageContainer.find("#bank-not-selected").html(),
        "enterOtherBankPhrase": $pageContainer.find("#enter-other-bank-phrase").html(),
    };

    function onCurrencyPairChange(currentCurrencyPair) {
        that.updateAndShowAll(currentCurrencyPair);
    }

    this.syncCurrencyPairSelector = function () {
        inputoutputCurrencyPairSelector.syncState();
    };

    this.updateAndShowAll = function (refreshIfNeeded) {
        that.getAndShowInputOutputData(refreshIfNeeded);
    };


    this.getAndShowInputOutputData = function (refreshIfNeeded, page, direction) {
        if ($inputoutputContainer.hasClass('hidden') || !windowIsActive) {
            clearTimeout(timeOutIdForInputOutputData);
            timeOutIdForInputOutputData = setTimeout(function () {
                that.updateAndShowAll(true);
            }, refreshIntervalForInputOutputData);
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowInputOutputData');
        }
        var $inputoutputTable = $('#' + tableId).find('tbody');
        var url = '/dashboard/myInputoutputData/' + tableId + '' +
            '?page=' + (page ? page : '') +
            '&direction=' + (direction ? direction : '') +
            '&refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            headers: {
                "windowid": windowId
            },
            success: function (data) {
                if (!data) return;
                if (data.length == 0 || data[0].needRefresh) {
                    var $tmpl = $('#inputoutput-table_row').html().replace(/@/g, '%');
                    clearTable($inputoutputTable);
                    data.forEach(function (e) {
                        $inputoutputTable.append(tmpl($tmpl, e));
                    });
                    blink($inputoutputTable.find('td:not(:first-child)'));
                }
                if (data.length > 0) {
                    $('.inputoutput-table__page').text(data[0].page);
                } else if (refreshIfNeeded) {
                    var p = parseInt($('.inputoutput-table__page').text());
                    $('.inputoutput-table__page').text(++p);
                }
                clearTimeout(timeOutIdForInputOutputData);
                timeOutIdForInputOutputData = setTimeout(function () {
                    that.updateAndShowAll(true);
                }, refreshIntervalForInputOutputData);
            }
        });
    };

    /*=====================================================*/
    (function init(currentCurrencyPair) {
        inputoutputCurrencyPairSelector = new CurrencyPairSelectorClass('inputoutput-currency-pair-selector', currentCurrencyPair);
        inputoutputCurrencyPairSelector.init(onCurrencyPairChange);
        /**/
        syncTableParams(tableId, tablePageSize, function (data) {
            that.getAndShowInputOutputData();
        });
        /**/
        $('.inputoutput-table__backward').on('click', function (e) {
            e.preventDefault();
            that.getAndShowInputOutputData(true, null, 'BACKWARD');
        });
        $('.inputoutput-table__forward').on('click', function (e) {
            e.preventDefault();
            that.getAndShowInputOutputData(true, null, 'FORWARD');
        });

        $('#inputoutput-table').on('click', '#viewBtcInvoiceButton', function (e) {
            e.preventDefault();
            var $form = $(this).parents('#inputoutput-center-tableBody__form');
            var invoiceId = $form.find('input[name=transactionId]').val();
            $.ajax({
                url: '/merchants/bitcoin/payment/address?id=' + invoiceId,
                type: 'GET',
                success: function (data) {
                    var $modal = $("#btc-invoice-info-modal");
                    $modal.find("#invoiceId").val(invoiceId);
                    $modal.find("#address-to-pay").val(data.address);
                    $modal.find("#btc-transaction").val(data.hash);
                    $modal.modal();
                }
            });
        });

        $('#inputoutput-table').on('click', 'button[data-source=WITHDRAW].revoke_button', function (e) {
            e.preventDefault();
            var id = $(this).data("id");
            var $modal = $("#confirm-with-info-modal");
            $modal.find("label[for=info-field]").html($(this).html());
            $modal.find("#info-field").val(id);
            $modal.find("#confirm-button").off("click").one("click", function () {
                $modal.modal('hide');
                $.ajax({
                    url: '/withdraw/request/revoke?id=' + id,
                    headers: {
                        'X-CSRF-Token': $("input[name='_csrf']").val(),
                    },
                    type: 'POST',
                    success: function () {
                        that.updateAndShowAll(false);
                    }
                });
            });
            $modal.modal();
        });

        $('#inputoutput-table').on('click', 'button[data-source=REFILL].revoke_button', function (e) {
            e.preventDefault();
            var id = $(this).data("id");
            var $modal = $("#confirm-with-info-modal");
            $modal.find("label[for=info-field]").html($(this).html());
            $modal.find("#info-field").val(id);
            $modal.find("#confirm-button").off("click").one("click", function () {
                $modal.modal('hide');
                $.ajax({
                    url: '/refill/request/revoke?id=' + id,
                    headers: {
                        'X-CSRF-Token': $("input[name='_csrf']").val(),
                    },
                    type: 'POST',
                    success: function () {
                        that.updateAndShowAll(false);
                    }
                });
            });
            $modal.modal();
        });

        $('#inputoutput-table').on('click', 'button[data-source=REFILL].confirm_user_button', function (e) {
            e.preventDefault();
            var id = $(this).data("id");
            $.ajax({
                url: '/refill/request/info?id=' + id,
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val(),
                },
                type: 'GET',
                success: function (requestData) {
                    var refillDetailedParamsDialogResult= false;
                    $refillDetailedParamsDialog.find("#invoiceSubmit").off("click").one("click", function () {
                        refillDetailedParamsDialogResult = true;
                        $refillDetailedParamsDialog.modal("hide");
                    });
                    $refillDetailedParamsDialog.one("hidden.bs.modal", function () {
                        if (refillDetailedParamsDialogResult) {
                            sendConfirm(requestData.id);
                        }
                    });
                    showRefillDetailDialog(requestData);
                }
            });

            function showRefillDetailDialog(data) {
                resetForm();
                $refillDetailedParamsDialog.find("#amount").html(numeral(data.amount).format(numberFormat));
                $refillDetailedParamsDialog.find("#bank-name").html(data.recipientBankName);
                $refillDetailedParamsDialog.find("#bank-account").html(data.recipientBankAccount);
                $refillDetailedParamsDialog.find("#bank-recipient").html(data.recipientBankRecipient);
                getBankDataList(data.currencyId, function (bankDataList) {
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
                    /**/
                    $bankItem.val(0);
                    $bankItem.attr("data-bank-id", "");
                    $bankItem.attr("data-bank-code", "");
                    $bankItem.attr("data-bank-name", "");
                    $bankItem.html(phrases.enterOtherBankPhrase);
                    $bankSelect.append($bankItem.clone());
                });
                $refillDetailedParamsDialog.modal({
                    backdrop: 'static'
                });
            }

            function getBankDataList(currency, callback) {
                $.ajax({
                    url: '/withdraw/banks',
                    async: true,
                    type: "get",
                    contentType: "application/json",
                    data: {"currencyId": currency}
                }).success(function (response) {
                    if (callback) {
                        callback(response);
                    }
                });
            }

            function sendConfirm(id) {
                var data = new FormData();
                data.append('invoiceId', id);
                data.append('payerBankName', $refillDetailedParamsDialog.find('#bank-data-list').find('option:selected').text());
                data.append('payerBankCode', $refillDetailedParamsDialog.find("#bank-code").val());
                data.append('userAccount', $refillDetailedParamsDialog.find("#user-account").val());
                data.append('userFullName', $refillDetailedParamsDialog.find("#user-full-name").val());
                data.append('remark', $refillDetailedParamsDialog.find("#remark").val());
                data.append('receiptScan', $refillDetailedParamsDialog.find("#receipt-scan")[0].files[0]);
                $.ajax({
                    url: '/refill/request/confirm',
                    headers: {
                        'X-CSRF-Token': $("input[name='_csrf']").val(),
                    },
                    data: data,
                    type: 'POST',
                    cache: false,
                    contentType: false,
                    processData: false,
                    enctype: 'multipart/form-data',
                    success: function () {
                        that.updateAndShowAll(false);
                    }
                });
            }

        });

    })(currentCurrencyPair);
}
