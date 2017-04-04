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
        $('#inputoutput-table').on('click', '#revokeBtcInvoiceButton', function (e) {
            e.preventDefault();
            var $form = $(this).parents('#inputoutput-center-tableBody__form');
            var invoiceId = $form.find('input[name=transactionId]').val();
            $.ajax({
                url: '/merchants/bitcoin/payment/address?id=' + invoiceId,
                type: 'GET',
                success: function (data) {
                    var $modal = $("#btc-invoice-revoke-modal");
                    $modal.find("#invoiceId").val(invoiceId);
                    $modal.find("#address-to-pay").val(data.address);

                    $modal.find("#btcInvoiceRevokeConfirm").off("click").one("click", function () {
                        var invoiceId = $('#invoiceId').val().trim();
                        $modal.modal('hide');
                        $.ajax({
                            url: '/merchants/bitcoin/payment/revoke?id=' + invoiceId,
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
                }
            });
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

        $('#inputoutput-table').on('click', 'button[data-source=INVOICE].revoke_button', function (e) {
            e.preventDefault();
            var $form = $(this).parents('#inputoutput-center-tableBody__form');
            $form.attr("action", "/merchants/invoice/payment/confirmation");
            var $action = $form.find('input[name=action]');
            $action.attr("value", "revoke");
            var $id = $form.find('input[name=transactionId]');
            var id = $(this).data("id");
            $id.attr("value", id);
            var $sourceType = $form.find('input[name=sourceType]');
            $sourceType.attr("value", "INVOICE");
            $form[0].submit();
        });

        $('#inputoutput-table').on('click', 'button[data-source=INVOICE].confirm_user_button', function (e) {
            e.preventDefault();
            var $form = $(this).parents('#inputoutput-center-tableBody__form');
            $form.attr("action", "/merchants/invoice/payment/confirmation");
            var $action = $form.find('input[name=action]');
            $action.attr("value", "confirm");
            var $id = $form.find('input[name=transactionId]');
            var id = $(this).data("id");
            $id.attr("value", id);
            var $sourceType = $form.find('input[name=sourceType]');
            $sourceType.attr("value", "INVOICE");
            $form[0].submit();
        });

    })(currentCurrencyPair);
}
