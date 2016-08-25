/**
 * Created by Valk on 06.06.2016.
 */

function OrdersClass(currentCurrencyPair) {
    if (OrdersClass.__instance) {
        return OrdersClass.__instance;
    } else if (this === window) {
        return new OrdersClass(currentCurrencyPair);
    }
    OrdersClass.__instance = this;
    /**/
    var that = this;
    /**/
    var timeOutIdForOrdersData;
    var refreshIntervalForOrdersData = 5000 * REFRESH_INTERVAL_MULTIPLIER;
    /**/
    var showLog = false;
    /**/
    var tableSellId = "orders-sell-table";
    var tableBuyId = "orders-buy-table";
    var $ordersContainer = $('#orders');
    var ordersCurrencyPairSelector;
    var tableSellPageSize = 5;
    var tableBuyPageSize = 5;
    var myordersStatusForShow = 'OPENED';

    function onCurrencyPairChange(currentCurrencyPair) {
        that.updateAndShowAll(false, 1, null);
    }

    /**/
    this.syncCurrencyPairSelector = function () {
        ordersCurrencyPairSelector.syncState(function (pairHasChanged) {
            if (pairHasChanged) {
                that.updateAndShowAll(false, 1, null);
            }
        });
    };

    this.updateAndShowAll = function (refreshIfNeeded, page, direction) {
        that.getAndShowSellOrdersData(refreshIfNeeded, page, direction);
        that.getAndShowBuyOrdersData(refreshIfNeeded, page, direction);
    };


    this.getAndShowSellOrdersData = function (refreshIfNeeded, page, direction) {
        if ($ordersContainer.hasClass('hidden') || !windowIsActive) {
            clearTimeout(timeOutIdForOrdersData);
            timeOutIdForOrdersData = setTimeout(function () {
                that.updateAndShowAll(true);
            }, refreshIntervalForOrdersData);
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowSellOrdersData');
        }
        var $ordersSellTable = $('#' + tableSellId).find('tbody');
        var url = '/dashboard/myOrdersData/' + tableSellId + '' +
            '?type=SELL' +
            '&status=' + myordersStatusForShow + '' +
            '&page=' + (page ? page : '') +
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
                    var $tmpl = $('#orders-sell-table_row').html().replace(/@/g, '%');
                    clearTable($ordersSellTable);
                    data.forEach(function (e) {
                        $ordersSellTable.append(tmpl($tmpl, e));
                    });
                    blink($ordersSellTable.find('td:not(:first-child)'));
                }
                if (data.length > 0) {
                    $('.orders-sell-table__page').text(data[0].page);
                } else if (refreshIfNeeded) {
                    var p = parseInt($('.orders-sell-table__page').text());
                    $('.orders-sell-table__page').text(++p);
                }
                clearTimeout(timeOutIdForOrdersData);
                timeOutIdForOrdersData = setTimeout(function () {
                    that.updateAndShowAll(true);
                }, refreshIntervalForOrdersData);
            }
        });
    };

    this.getAndShowBuyOrdersData = function (refreshIfNeeded, page, direction) {
        if ($ordersContainer.hasClass('hidden') || !windowIsActive) {
            clearTimeout(timeOutIdForOrdersData);
            timeOutIdForOrdersData = setTimeout(function () {
                that.updateAndShowAll(true);
            }, refreshIntervalForOrdersData);
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowSellOrdersData');
        }
        var $ordersBuyTable = $('#' + tableBuyId).find('tbody');
        var url = '/dashboard/myOrdersData/' + tableBuyId + '' +
            '?type=BUY' +
            '&status=' + myordersStatusForShow +
            '&page=' + (page ? page : '') +
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
                    var $tmpl = $('#orders-buy-table_row').html().replace(/@/g, '%');
                    clearTable($ordersBuyTable);
                    data.forEach(function (e) {
                        $ordersBuyTable.append(tmpl($tmpl, e));
                    });
                    blink($ordersBuyTable.find('td:not(:first-child)'));
                }
                if (data.length > 0) {
                    $('.orders-buy-table__page').text(data[0].page);
                } else if (refreshIfNeeded) {
                    var p = parseInt($('.orders-buy-table__page').text());
                    $('.orders-buy-table__page').text(++p);
                }
                clearTimeout(timeOutIdForOrdersData);
                timeOutIdForOrdersData = setTimeout(function () {
                    that.updateAndShowAll(true);
                }, refreshIntervalForOrdersData);
            }
        });
    };

    this.createOrder = function (onSuccess, onError) {
        $.ajax({
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            url: '/order/create/',
            type: 'POST',
            success: function (data) {
                onSuccess(data);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                onError(jqXHR, textStatus, errorThrown);
            }
        });
    };

    this.acceptOrder = function (ordersList, onSuccess, onError) {
        $.ajax({
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            url: '/order/accept/',
            contentType: "application/json; charset=utf-8",
            type: 'POST',
            data: ordersList.join(' '),
            success: function (data) {
                onSuccess(data);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                onError(jqXHR, textStatus, errorThrown);
            }
        });
    };

    this.deleteOrder = function (onSuccess, onError) {
        $.ajax({
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            url: '/order/delete/',
            type: 'POST',
            success: function (data) {
                onSuccess(data);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                onError(jqXHR, textStatus, errorThrown);
            }
        });
    };

    /*=====================================================*/
    (function init(currentCurrencyPair) {
        ordersCurrencyPairSelector = new CurrencyPairSelectorClass('orders-currency-pair-selector', currentCurrencyPair);
        ordersCurrencyPairSelector.init(onCurrencyPairChange);
        /**/
        syncTableParams(tableSellId, tableSellPageSize, function (data) {
            that.getAndShowSellOrdersData();
        });
        syncTableParams(tableBuyId, tableBuyPageSize, function (data) {
            that.getAndShowBuyOrdersData();
        });
        /**/
        $('#orders-sell-table').on('click', '.button_delete_order', submitOrderDeleting);
        $('#orders-buy-table').on('click', '.button_delete_order', submitOrderDeleting);
        $('#order-delete-confirm__submit').on('click', deletingOrder);
        /**/
        $('.orders-sell-table__backward').on('click', function (e) {
            e.preventDefault();
            that.getAndShowSellOrdersData(true, null, 'BACKWARD');
        });
        $('.orders-sell-table__forward').on('click', function (e) {
            e.preventDefault();
            that.getAndShowSellOrdersData(true, null, 'FORWARD');
        });
        /**/
        $('.orders-buy-table__backward').on('click', function (e) {
            e.preventDefault();
            that.getAndShowBuyOrdersData(true, null, 'BACKWARD');
        });
        $('.orders-buy-table__forward').on('click', function (e) {
            e.preventDefault();
            that.getAndShowBuyOrdersData(true, null, 'FORWARD');
        });
    })(currentCurrencyPair);

    /*PREPARE DATA FOR MODAL DIALOG FOR DELETING ORDER ... */
    function submitOrderDeleting() {
        var orderId = $(this).attr("id");
        var data = {orderId: orderId};
        showOrderDeleteDialog(data);
    }

    /*...PREPARE DATA FOR MODAL DIALOG FOR DELETING ORDER */

    /*MODAL DIALOG FOR DELETING ORDER ... */
    function showOrderDeleteDialog(data) {
        $.ajax({
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            url: '/order/submitdelete/' + data.orderId,
            type: 'POST',
            success: function (data) {
                $('#order-delete-confirm__modal').find('#operationTypeName').val(data.operationTypeName);
                $('#order-delete-confirm__modal').find('#currencyPairName').val(data.currencyPairName);
                $('#order-delete-confirm__modal').find('#amount').val(data.amount);
                $('#order-delete-confirm__modal').find('#exrate').val(data.exrate);
                $('#order-delete-confirm__modal').find('#total').val(data.total);
                /**/
                $('#order-delete-confirm__modal').modal();
            },
            error: function (jqXHR, textStatus, errorThrown) {
            }
        });
    }

    /*... MODAL DIALOG FOR DELETING ORDER*/

    /*CALL DELETING THE ORDER AND CONTROL RESULT ... */
    function deletingOrder(event) {
        event.preventDefault();
        $('#order-delete-confirm__modal').one('hidden.bs.modal', function (e) {
            that.deleteOrder(onDeleteOrderSuccess, onDeleteOrderError)
        });
        $('#order-delete-confirm__modal').modal('hide');
    }

    function onDeleteOrderSuccess(data) {
        that.updateAndShowAll();
        leftSider.getStatisticsForMyWallets();
        successNoty(data.result);
    }

    function onDeleteOrderError(jqXHR, textStatus, errorThrown) {
    }

    /*... CALL DELETING THE ORDER AND CONTROL RESULT*/
}
