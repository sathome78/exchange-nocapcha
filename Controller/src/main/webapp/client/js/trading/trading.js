/**
 * Created by Valk on 02.06.2016.
 */

function TradingClass(period, chartType, currentCurrencyPair) {
    if (TradingClass.__instance) {
        return TradingClass.__instance;
    } else if (this === window) {
        return new TradingClass();
    }
    TradingClass.__instance = this;
    /**/
    var that = this;
    var chart = null;

    var $tradingContainer = $('#trading');
    var dashboardCurrencyPairSelector;
    var refreshInterval = 5000 * REFRESH_INTERVAL_MULTIPLIER;
    var timeOutId;
    var $graphicsLoadingImg = $('#graphics-container').find('.loading');
    /**/
    var showLog = false;
    /**/
    this.ordersListForAccept = [];
    /**/
    this.commissionSell;
    this.commissionBuy;
    /**/
    this.ROUND_SCALE = 9;

    function onCurrencyPairChange() {
        that.updateAndShowAll();
        that.fillOrderCreationFormFields();
    }

    this.syncCurrencyPairSelector = function () {
        dashboardCurrencyPairSelector.syncState();
    };

    this.updateAndShowAll = function (refreshIfNeeded) {
        that.getAndShowAcceptedOrdersHistory(refreshIfNeeded, function () {
            that.getAndShowAcceptedOrdersHistory_myDeals(refreshIfNeeded);
            that.getAndShowStatisticsForCurrency();
            that.getAndShowChart();
        });
        that.getAndShowSellOrders(refreshIfNeeded);
        that.getAndShowBuyOrders(refreshIfNeeded);

    };

    this.getAndShowStatisticsForCurrency = function () {
        if ($tradingContainer.hasClass('hidden') || !windowIsActive) {
            return;
        }
        var url = '/dashboard/ordersForPairStatistics';
        $.ajax({
            url: url,
            type: 'GET',
            success: function (data) {
                $('#lastOrderAmountBase>span').text(data.lastOrderAmountBase + ' ' + data.currencyPair.currency1.name);
                $('#firstOrderRate>span').text(data.firstOrderRate + ' ' + data.currencyPair.currency2.name);
                $('#lastOrderRate>span').text(data.lastOrderRate + ' ' + data.currencyPair.currency2.name);
                $('#sumBase>span').text(data.sumBase + ' ' + data.currencyPair.currency1.name);
                $('#sumConvert>span').text(data.sumConvert + ' ' + data.currencyPair.currency2.name);
                $('#minRate').text(data.minRate + ' ' + data.currencyPair.currency2.name);
                $('#maxRate').text(data.maxRate + ' ' + data.currencyPair.currency2.name);
            }
        });
    };

    this.getAndShowChart = function () {
        if ($tradingContainer.hasClass('hidden') || !windowIsActive) {
            return;
        }
        if (chart) {
            chart.drawChart();
        }
    };

    this.getAndShowAcceptedOrdersHistory = function (refreshIfNeeded, callback) {
        if ($tradingContainer.hasClass('hidden') || !windowIsActive) {
            clearTimeout(timeOutId);
            timeOutId = setTimeout(function () {
                that.updateAndShowAll(true);
            }, refreshInterval);
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowAcceptedOrdersHistory/ALL');
        }
        var $ordersHistoryTable = $('#orders-history-table').find('tbody');
        var url = '/dashboard/acceptedOrderHistory/ALL?refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            headers: {
                "windowid": windowId
            },
            success: function (data) {
                if (!data) return;
                if (data.length == 0 || data[0].needRefresh) {
                    var $tmpl = $('#orders-history-table_row').html().replace(/@/g, '%');
                    clearTable($ordersHistoryTable);
                    data.forEach(function (e) {
                        $ordersHistoryTable.append(tmpl($tmpl, e));
                    });
                    blink($ordersHistoryTable);
                    if (callback) {
                        callback();
                    }
                }
                clearTimeout(timeOutId);
                timeOutId = setTimeout(function () {
                    that.updateAndShowAll(true);
                }, refreshInterval);
            }
        });
    };

    this.getAndShowAcceptedOrdersHistory_myDeals = function (refreshIfNeeded) {
        if ($tradingContainer.hasClass('hidden') || !windowIsActive || $('#orders-history-table__my-deals').hasClass('hidden')) {
            clearTimeout(timeOutId);
            timeOutId = setTimeout(function () {
                that.updateAndShowAll(true);
            }, refreshInterval);
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowAcceptedOrdersHistory/MY');
        }
        var $ordersHistoryTable = $('#orders-history-table__my-deals').find('tbody');
        var url = '/dashboard/acceptedOrderHistory/MY?refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            headers: {
                "windowid": windowId
            },
            success: function (data) {
                if (!data) return;
                if (data.length == 0 || data[0].needRefresh) {
                    var $tmpl = $('#orders-history-table_row__my-deals').html().replace(/@/g, '%');
                    clearTable($ordersHistoryTable);
                    data.forEach(function (e) {
                        $ordersHistoryTable.append(tmpl($tmpl, e));
                    });
                    blink($ordersHistoryTable);
                }
                clearTimeout(timeOutId);
                timeOutId = setTimeout(function () {
                    that.updateAndShowAll(true);
                }, refreshInterval);
            }
        });
    };

    this.getAndShowSellOrders = function (refreshIfNeeded) {
        if ($tradingContainer.hasClass('hidden') || !windowIsActive) {
            clearTimeout(timeOutId);
            timeOutId = setTimeout(function () {
                that.updateAndShowAll(true);
            }, refreshInterval);
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowSellOrders');
        }
        var $ordersSellTable = $('#dashboard-orders-sell-table').find('tbody');
        var url = '/dashboard/sellOrders?refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            headers: {
                "windowid": windowId
            },
            success: function (data) {
                if (!data) return;
                if (data.length == 0 || data[0].needRefresh) {
                    var $tmpl = $('#dashboard-orders-sell-table_row').html().replace(/@/g, '%');
                    clearTable($ordersSellTable);
                    data.forEach(function (e) {
                        $ordersSellTable.append(tmpl($tmpl, e));
                    });
                    blink($('#dashboard-orders-sell-table'));
                }
                clearTimeout(timeOutId);
                timeOutId = setTimeout(function () {
                    that.updateAndShowAll(true);
                }, refreshInterval);
            }
        });
    };

    this.getAndShowBuyOrders = function (refreshIfNeeded) {
        if ($tradingContainer.hasClass('hidden') || !windowIsActive) {
            clearTimeout(timeOutId);
            timeOutId = setTimeout(function () {
                that.updateAndShowAll(true);
            }, refreshInterval);
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowBuyOrders');
        }
        var $ordersBuyTable = $('#dashboard-orders-buy-table').find('tbody');
        var url = '/dashboard/BuyOrders?refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            headers: {
                "windowid": windowId
            },
            success: function (data) {
                if (!data) return;
                if (data.length == 0 || data[0].needRefresh) {
                    var $tmpl = $('#dashboard-orders-buy-table_row').html().replace(/@/g, '%');
                    clearTable($ordersBuyTable);
                    data.forEach(function (e) {
                        $ordersBuyTable.append(tmpl($tmpl, e));
                    });
                    blink($('#dashboard-orders-buy-table'));
                }
                clearTimeout(timeOutId);
                timeOutId = setTimeout(function () {
                    that.updateAndShowAll(true);
                }, refreshInterval);
            }
        });
    };

    this.fillOrderCreationFormFields = function() {
        $(document).one("ajaxStop", function () {
            var currencyPairName = $('.currency-pair-selector__button').first().text().trim();
            var initialAmount = 1;
            var initialAmountString = initialAmount.toFixed(that.ROUND_SCALE);
            $('#amountBuy').val(initialAmountString);
            var lastBuyExrate = getLastExrate('#dashboard-orders-buy-table .dashboard-order__tr:first', currencyPairName);
            $('#exchangeRateBuy').val(lastBuyExrate);
            calculateFieldsForBuy();
            $('#amountSell').val(initialAmountString);
            var lastSellExrate = getLastExrate('#dashboard-orders-sell-table .dashboard-order__tr:first', currencyPairName);
            $('#exchangeRateSell').val(lastSellExrate);
            calculateFieldsForSell();
            that.fillOrderBalance(currencyPairName);


        });
    };

    this.fillOrderBalance = function (currencyPairName) {
        if ($('#currentBaseBalance').length > 0 && $('#currentConvertBalance').length > 0)  {
            var currencies = currencyPairName.split('\/');
            var currentBaseBalance = getCurrentBalanceByCurrency(currencies[0]);
            var currentConvertBalance = getCurrentBalanceByCurrency(currencies[1]);
            $('#currentBaseBalance').text(currentBaseBalance);
            $('#currentConvertBalance').text(currentConvertBalance);
        }
    };

    function getCurrentBalanceByCurrency(currencyName) {
        return $('#mywallets_table').find('tr td:contains(' + currencyName + ')').filter(function (index) {
            return $(this).text().trim() === currencyName;
        }).next().text().trim();
    }

    function getLastExrate($selector, currencyPairName) {
        var lastRate;
        if ($($selector).size() === 0) {
            var $cell = $('#currency_table').find("tr td:contains('" + currencyPairName + "')");
            if ($cell.size() === 0) {
                return '';
            } else {
                lastRate = $cell.next().text()
            }
            return (parseNumber(lastRate)).toFixed(that.ROUND_SCALE);

        } else {
            lastRate = $($selector).find('.order_exrate').text();
            return (parseNumber(lastRate)).toFixed(that.ROUND_SCALE);
        }
    }



    this.resetOrdersListForAccept = function() {
        if (that.ordersListForAccept.length != 0) {
            that.ordersListForAccept = [];
            switchCreateOrAcceptButtons();
        }
    };

    this.clearOrdersCreationForm = function() {
        $('.item__input').val('');
        $('.buyBTC__input').val('');
        calculateFieldsForBuy();
        calculateFieldsForSell();
        switchCreateOrAcceptButtons();
    };

    function getOrderCommissions() {
        var url = '/dashboard/orderCommissions';
        $.ajax({
            url: url,
            type: 'GET',
            success: function (data) {
                if (!data) return;
                that.commissionSell = data.sellCommission;
                that.commissionBuy = data.buyCommission;
                calculateFieldsForBuy();
                calculateFieldsForSell();
            }
        });
    }

    function calculateFieldsForBuy(e) {
        var amount = +$('#amountBuy').val();
        var exchangeRate = +$('#exchangeRateBuy').val();
        var totalForBuy = +$('#totalForBuy').val(amount * exchangeRate).val();
        var commission = that.commissionBuy;
        var calculatedCommissionForBuy = +(totalForBuy * commission / 100).toFixed(that.ROUND_SCALE);
        var totalWithCommissionForBuy = +(totalForBuy + calculatedCommissionForBuy).toFixed(that.ROUND_SCALE);
        $('#totalForBuy>span:first').text(totalForBuy.toFixed(that.ROUND_SCALE));
        $('#calculatedCommissionForBuy>span:first').text(calculatedCommissionForBuy.toFixed(that.ROUND_SCALE));
        $('#totalWithCommissionForBuy>span:first').text(totalWithCommissionForBuy.toFixed(that.ROUND_SCALE));
    }

    function calculateFieldsForSell() {
        var amount = +$('#amountSell').val();
        var exchangeRate = +$('#exchangeRateSell').val();
        var totalForSell = +$('#totalForSell').val(amount * exchangeRate).val();
        var commission = that.commissionSell;
        var calculatedCommissionForSell = +(totalForSell * commission / 100).toFixed(that.ROUND_SCALE);
        var totalWithCommissionForSell = +(totalForSell - calculatedCommissionForSell).toFixed(that.ROUND_SCALE);
        $('#totalForSell>span:first').text(totalForSell.toFixed(that.ROUND_SCALE));
        $('#calculatedCommissionForSell>span:first').text(calculatedCommissionForSell.toFixed(that.ROUND_SCALE));
        $('#totalWithCommissionForSell>span:first').text(totalWithCommissionForSell.toFixed(that.ROUND_SCALE));
    }

    /*=========================================================*/
    (function init(period, chartType, currentCurrencyPair) {
        getOrderCommissions();
        dashboardCurrencyPairSelector = new CurrencyPairSelectorClass('dashboard-currency-pair-selector', currentCurrencyPair);
        dashboardCurrencyPairSelector.init(onCurrencyPairChange);
        try {
            chart = new ChartGoogleClass();
        } catch (e) {
        }
        try {
            chart = new ChartAmchartsClass("STOCK", period, $graphicsLoadingImg);
        } catch (e) {
        }
        if (chart) {
            try {
                chart.init(chartType);
            } catch (e) {
            }
        }
        that.updateAndShowAll(false);
        that.fillOrderCreationFormFields();

        /**/
        $('#amountBuy').on('keyup', calculateFieldsForBuy).on('keydown', that.resetOrdersListForAccept);
        $('#exchangeRateBuy').on('keyup', calculateFieldsForBuy).on('keydown', that.resetOrdersListForAccept);
        $('#amountSell').on('keyup', calculateFieldsForSell).on('keydown', that.resetOrdersListForAccept);
        $('#exchangeRateSell').on('keyup', calculateFieldsForSell).on('keydown', that.resetOrdersListForAccept);
        /**/
        $('.dashboard-order__table').on('click', '.dashboard-order__tr', fillOrdersFormFromCurrentOrder);
        /**/
        $('#dashboard-buy').on('click', orderBuy);
        $('#dashboard-sell').on('click', orderSell);
        /**/
        $('#dashboard-buy-accept').on('click', orderBuyAccept);
        $('#dashboard-sell-accept').on('click', orderSellAccept);
        /**/
        $('#order-create-confirm__submit').on('click', orderCreate);
        /**/
        $('.dashboard-accept-reset__button').on('click', resetOrdersListForAcceptOnClick);
        /**/
        $('.deals-scope-switcher__wrapper').on('click', '.deals-scope-switcher__button', function (e) {
            $(this).siblings().removeClass('ht-active');
            $(this).addClass('ht-active');
            var $tableId = $('#' + $(this).data('tableid'));
            $('.orders-history-table').addClass('hidden');
            $tableId.removeClass('hidden');
            that.getAndShowAcceptedOrdersHistory();
            that.getAndShowAcceptedOrdersHistory_myDeals();
        });
        /**/
        switchCreateOrAcceptButtons();
    })(period, chartType, currentCurrencyPair);

    function fillOrdersFormFromCurrentOrder() {
        that.ordersListForAccept = [];
        /**/
        var orderAmountSumm = 0;
        $(this).prevAll('.dashboard-order__tr').each(function (i, e) {
            var orderId = $(e).find('.order_id').text();
            var orderType = $(e).find('.order_type').text();
            var orderAmount = $(e).find('.order_amount').attr('title');
            var orderExRate = $(e).find('.order_exrate').attr('title');
            var data = {
                orderId: orderId,
                orderType: orderType,
                orderAmount: orderAmount,
                orderExRate: orderExRate
            };
            that.ordersListForAccept.unshift(data);
            orderAmountSumm += parseNumber(orderAmount);
            orderAmountSumm = (+orderAmountSumm.toFixed(that.ROUND_SCALE));
        });
        var orderId = $(this).find('.order_id').text();
        var orderType = $(this).find('.order_type').text();
        var orderAmount = $(this).find('.order_amount').attr('title');
        var orderExRate = parseNumber($(this).find('.order_exrate').attr('title'));
        var data = {
            orderId: orderId,
            orderType: orderType,
            orderAmount: orderAmount,
            orderExRate: orderExRate
        };
        that.ordersListForAccept.push(data);
        orderAmountSumm += parseNumber(orderAmount);
        /**/
        $('#amountBuy').val(orderAmountSumm.toFixed(that.ROUND_SCALE));
        $('#exchangeRateBuy').val(orderExRate);
        $('#amountSell').val(orderAmountSumm.toFixed(that.ROUND_SCALE));
        $('#exchangeRateSell').val(orderExRate);
        /**/
        calculateFieldsForSell();
        calculateFieldsForBuy();
        switchCreateOrAcceptButtons(orderType, that.ordersListForAccept.length);
    }

    function switchCreateOrAcceptButtons(acceptedOrderType, ordersForAcceptionCount) {
        var s;
        s = $('#dashboard-sell-accept').text();
        s = s.split('(')[0].trim() + ' (' + ordersForAcceptionCount + ')';
        $('#dashboard-sell-accept').text(s);
        s = $('#dashboard-buy-accept').text();
        s = s.split('(')[0].trim() + ' (' + ordersForAcceptionCount + ')';
        $('#dashboard-buy-accept').text(s);
        if (!acceptedOrderType) {
            $('#dashboard-sell-accept').addClass('hidden');
            $('#dashboard-sell-accept-reset').addClass('hidden');
            $('#dashboard-buy-accept').addClass('hidden');
            $('#dashboard-buy-accept-reset').addClass('hidden');
            $('#dashboard-sell').removeClass('hidden');
            $('#dashboard-buy').removeClass('hidden');
        }
        if (acceptedOrderType == 'BUY') {
            $('#dashboard-sell-accept').removeClass('hidden');
            $('#dashboard-sell-accept-reset').removeClass('hidden');
            $('#dashboard-buy-accept').addClass('hidden');
            $('#dashboard-buy-accept-reset').addClass('hidden');
            $('#dashboard-sell').addClass('hidden');
            $('#dashboard-buy').removeClass('hidden');
        }
        if (acceptedOrderType == 'SELL') {
            $('#dashboard-sell-accept').addClass('hidden');
            $('#dashboard-sell-accept-reset').addClass('hidden');
            $('#dashboard-buy-accept').removeClass('hidden');
            $('#dashboard-buy-accept-reset').removeClass('hidden');
            $('#dashboard-sell').removeClass('hidden');
            $('#dashboard-buy').addClass('hidden');
        }
    }

    function resetOrdersListForAcceptOnClick(e) {
        e.preventDefault();
        that.resetOrdersListForAccept();
    }

    /*PREPARE DATA FOR MODAL DIALOG FOR CREATION ORDER ... */
    function orderBuy(event) {
        event.preventDefault();
        var data = {operationType: 'BUY'};
        $.map($('#dashboard-buy-form').serializeArray(), function (e) {
            if (e.name == 'amount') {
                data.amount = e.value;
            }
            if (e.name == 'exchangeRate') {
                data.rate = e.value;
            }
        });
        that.clearOrdersCreationForm();
        showOrderCreateDialog(data);
    }

    function orderSell(event) {
        event.preventDefault();
        var data = {operationType: 'SELL'};
        $.map($('#dashboard-sell-form').serializeArray(), function (e) {
            if (e.name == 'amount') {
                data.amount = e.value;
            }
            if (e.name == 'exchangeRate') {
                data.rate = e.value;
            }
        });
        that.clearOrdersCreationForm();
        showOrderCreateDialog(data);
    }

    /*...PREPARE DATA FOR MODAL DIALOG FOR CREATION ORDER */

    /*MODAL DIALOG FOR CREATION ORDER ... */
    function showOrderCreateDialog(data) {
        /**/
        var $balanceErrorContainer = $('#order-create-confirm__modal').find('[for=balance]');
        $balanceErrorContainer.empty();
        var $amountErrorContainer = $('#order-create-confirm__modal').find('[for=amount]');
        $amountErrorContainer.empty();
        var $exrateErrorContainer = $('#order-create-confirm__modal').find('[for=exrate]');
        $exrateErrorContainer.empty();
        $('#order-create-confirm__submit').removeClass('hidden');
        $.ajax({
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            url: '/order/submitnew/' + data.operationType,
            data: data,
            type: 'POST',
            success: function (data) {
                $('#order-create-confirm__modal').find('#operationTypeName').val(data.operationTypeName);
                $('#order-create-confirm__modal').find('#currencyPairName').val(data.currencyPairName);
                $('#order-create-confirm__modal').find('#balance').val(data.balance);
                $('#order-create-confirm__modal').find('#amount').val(data.amount);
                $('#order-create-confirm__modal').find('#exrate').val(data.exrate);
                $('#order-create-confirm__modal').find('#total').val(data.total);
                $('#order-create-confirm__modal').find('#commission').val(data.commission);
                $('#order-create-confirm__modal').find('#totalWithComission').val(data.totalWithComission);
                /**/
                $('#order-create-confirm__modal').modal();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                var responseData = jqXHR.responseJSON;
                for (var f in responseData) {
                    if (f.split('_')[0] == 'balance') {
                        $balanceErrorContainer.append('<div class="input-block-wrapper__error">' + responseData[f] + '</div>');
                    }
                    if (f.split('_')[0] == 'amount') {
                        $amountErrorContainer.append('<div class="input-block-wrapper__error">' + responseData[f] + '</div>');
                    }
                    if (f.split('_')[0] == 'exrate') {
                        $exrateErrorContainer.append('<div class="input-block-wrapper__error">' + responseData[f] + '</div>');
                    }
                }
                var data = responseData.order;
                if (data) {
                    $('#order-create-confirm__modal').find('#operationTypeName').val(data.operationTypeName);
                    $('#order-create-confirm__modal').find('#currencyPairName').val(data.currencyPairName);
                    $('#order-create-confirm__modal').find('#balance').val(data.balance);
                    $('#order-create-confirm__modal').find('#amount').val(data.amount);
                    $('#order-create-confirm__modal').find('#exrate').val(data.exrate);
                    $('#order-create-confirm__modal').find('#total').val(data.total);
                    $('#order-create-confirm__modal').find('#commission').val(data.commission);
                    $('#order-create-confirm__modal').find('#totalWithComission').val(data.totalWithComission);
                    /**/
                    $('#order-create-confirm__submit').addClass('hidden');
                    $('#order-create-confirm__modal').modal();
                }
            }
        });
    }

    /*... MODAL DIALOG FOR CREATION ORDER*/

    /*CALL CREATION THE SUBMITTED ORDER AND CONTROL RESULT ... */
    function orderCreate(event) {
        event.preventDefault();
        $('#order-create-confirm__modal').one('hidden.bs.modal', function (e) {
            orders.createOrder(onCreateOrderSuccess, onCreateOrderError);
        });
        $('#order-create-confirm__modal').modal('hide');
    }

    function onCreateOrderSuccess(data) {
        that.getAndShowSellOrders();
        that.getAndShowBuyOrders();
        leftSider.getStatisticsForMyWallets();
        leftSider.getStatisticsForAllCurrencies();
        successNoty(data.result);
    }

    function onCreateOrderError(jqXHR, textStatus, errorThrown) {
    }

    /*... CALL CREATION THE SUBMITTED ORDER AND CONTROL RESULT*/

    /*PREPARE DATA FOR ACCEPTION ORDER ... */
    function orderBuyAccept(event) {
        event.preventDefault();
        orderAccept(event);
    }

    function orderSellAccept(event) {
        event.preventDefault();
        orderAccept(event);
    }

    /*... PREPARE DATA FOR ACCEPTION ORDER */

    /*CALL ACCEPTANCE THE ORDERS LIST AND CONTROL RESULT ... */
    function orderAccept(event) {
        event.preventDefault();
        var ordersList = that.ordersListForAccept.map(function (e) {
            return parseInt(e.orderId);
        });
        that.clearOrdersCreationForm();
        switchCreateOrAcceptButtons();
        orders.acceptOrder(ordersList, onAcceptOrderSuccess, onAcceptOrderError);
    }

    function onAcceptOrderSuccess(data) {
        that.ordersListForAccept = [];
        that.updateAndShowAll();
        leftSider.getStatisticsForMyWallets();
        leftSider.getStatisticsForAllCurrencies();
        successNoty(data.result);
    }

    function onAcceptOrderError(jqXHR, textStatus, errorThrown) {
        that.ordersListForAccept = [];
    }

    /*... CALL ACCEPTANCE THE ORDERS LIST AND CONTROL RESULT*/


}