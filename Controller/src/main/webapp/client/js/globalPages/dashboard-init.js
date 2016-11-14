/**
 * Created by Valk on 18.06.2016.
 */

var leftSider;
var rightSider;
var trading;
var myWallets;
var myStatements;
var myHistory;
var orders;
var $currentPageMenuItem;
var notifications;

$(function dashdoardInit() {
    console.log('started');
    try {
        /*FOR EVERYWHERE ... */
        $(".input-block-wrapper__input").prop("autocomplete", "off");
        $(".numericInputField").prop("autocomplete", "off");
        $(".numericInputField")
            .keypress(
            function (e) {
                var decimal = $(this).val().split('.')[1];
                if (decimal && decimal.length >= trading.ROUND_SCALE) {
                    return false;
                }
                if (e.charCode >= 48 && e.charCode <= 57 || e.charCode == 46 || e.charCode == 0) {
                    if (e.key == '.' && $(this).val().indexOf('.') >= 0) {
                        return false;
                    }
                    var str = $(this).val() + e.key;
                    if (str.length > 1 && str.indexOf('0') == 0 && str.indexOf('.') != 1) {
                        return false
                    }
                } else {
                    return false;
                }
                return true;
            }
        )
            .on('input', function (e) {
                var val = $(this).val();
                var regx = /^(^[1-9]+\d*((\.{1}\d*)|(\d*)))|(^0{1}\.{1}\d*)|(^0{1})$/;
                var result = val.match(regx);
                if (!result || result[0] != val) {
                    $(this).val('');
                }
                var decimal = $(this).val().split('.')[1];
                if (decimal && decimal.length >= trading.ROUND_SCALE) {
                    $(this).val(+(+$(this).val()).toFixed(trading.ROUND_SCALE));
                }
            });
        /*... FOR EVERYWHERE*/

        /*FOR HEADER...*/
        notifications = new NotificationsClass();

        $('#menu-traiding').on('click', onMenuTraidingItemClick);
        function onMenuTraidingItemClick(e) {
            if (e) e.preventDefault();
            trading.syncCurrencyPairSelector();
            showPage('trading');
            trading.updateAndShowAll();
            trading.fillOrderCreationFormFields();
        }

        $('#menu-mywallets').on('click', function (e) {
            e.preventDefault();
            if (!e.ctrlKey) {
                showPage('balance-page');
                myWallets.getAndShowMyWalletsData();
            } else {
                window.open('/dashboard?startupPage=balance-page', '_blank');
                return false;
            }
        });
        $('#menu-myhistory').on('click', function (e) {
            e.preventDefault();
            if (!e.ctrlKey) {
                showPage('myhistory');
                myHistory.updateAndShowAll();
            } else {
                window.open('/dashboard?startupPage=myhistory', '_blank');
                return false;
            }
        });
        $('#menu-orders').on('click', function (e) {
            e.preventDefault();
            if (!e.ctrlKey) {
                orders.syncCurrencyPairSelector();
                showPage('orders');
                orders.updateAndShowAll();
            } else {
                window.open('/dashboard?startupPage=orders', '_blank');
                return false;
            }
        });
        var sessionId = $('#login-qr').text().trim();
        $('#login-qr').html("<img src='https://chart.googleapis.com/chart?chs=150x150&chld=L|2&cht=qr&chl=" + sessionId + "'>");
        /*...FOR HEADER*/

        /*FOR LEFT-SIDER ...*/

        leftSider = new LeftSiderClass();
        $('#currency_table').on('click', 'td:first-child', function (e) {
            var newCurrentCurrencyPairName = $(this).text().trim();
            syncCurrentParams(newCurrentCurrencyPairName, null, null, null, function (data) {
                if ($currentPageMenuItem.length) {
                    $currentPageMenuItem.click();
                } else {
                    onMenuTraidingItemClick();
                }
            });
            trading.fillOrderCreationFormFields();
        });
        $('#currency_table_wrapper').mCustomScrollbar({
            theme: "dark",
            axis: "yx",
            live: true

        });
        /*...FOR LEFT-SIDER*/

        /*FOR CENTER ON START UP ...*/

        $('#orders-sell-table-wrapper, #orders-buy-table-wrapper, #orders-history-table-wrapper').mCustomScrollbar({
            theme: "dark",
            axis: "y",
            live: true
        });






        syncCurrentParams(null, null, null, null, function (data) {
            showPage($('#startup-page-id').text().trim());
            trading = new TradingClass(data.period, data.chartType, data.currencyPair.name);
            leftSider.setOnWalletsRefresh(function () {
                trading.fillOrderBalance($('.currency-pair-selector__button').first().text().trim())
            });
            myWallets = new MyWalletsClass();
            myStatements = new MyStatementsClass();
            myHistory = new MyHistoryClass(data.currencyPair.name);
            orders = new OrdersClass(data.currencyPair.name);
        });
        /*...FOR CENTER ON START UP*/

        /*FOR RIGHT-SIDER ...*/
        $('#news_table_wrapper').mCustomScrollbar({
            theme: "dark",
            axis: "yx",
            live: true
        });

        rightSider = new RightSiderClass();
        /*...FOR RIGHT-SIDER*/
    } catch (e) {
        /*it's need for ignoring error from old interface*/
    }
});

function showPage(pageId) {
    if (!pageId) {
        return;
    }
    $('.center-frame-container').addClass('hidden');
    $('#' + pageId).removeClass('hidden');
    $currentPageMenuItem = $('#' + $('#' + pageId).data('menuitemid'));
}

function syncCurrentParams(currencyPairName, period, chart, showAllPairs, callback) {
    var url = '/dashboard/currentParams?';
    /*if parameter is empty, in response will be retrieved current value is set or default if non*/
    url = url + (currencyPairName ? '&currencyPairName=' + currencyPairName : '');
    url = url + (period ? '&period=' + period : '');
    url = url + (chart ? '&chart=' + chart : '');
    url = url + (showAllPairs != null ? '&showAllPairs=' + showAllPairs : '');
    $.ajax({
        url: url,
        type: 'GET',
        success: function (data) {
            /*sets currencyBaseName for all pages*/
            $('.currencyBaseName').text(data.currencyPair.currency1.name);
            $('.currencyConvertName').text(data.currencyPair.currency2.name);
            /**/
            if (callback) {
                callback(data);
            }
        }
    });
}

function parseNumber(numberStr) {
    /*ATTENTION: this func wil by work correctly if number always has decimal separator
     * for this reason we use BigDecimalProcessing.formatLocale(rs.getBigDecimal("some value"), locale, 2)
     * which makes 1000.00 from 1000 or 1000.00000
     * or we can use igDecimalProcessing.formatLocale(rs.getBigDecimal("some value"), locale, true)*/
    if (numberStr.search(/\,.*\..*/) != -1) {
        /*100,000.12 -> 100000.12*/
        numberStr = numberStr.replace(/\,/g, '');
    } else if (numberStr.search(/\..*\,.*/) != -1) {
        /*100.000,12 -> 100000.12*/
        numberStr = numberStr.replace(/\./g, '').replace(/\,/g, '.');
    } else if (numberStr.search(/\s.*\..*/) != -1) {
        /*100 000.12 -> 100000.12*/
        numberStr = numberStr.replace(/\s/g, '');
    } else if (numberStr.search(/\s.*\,.*/) != -1) {
        /*100 000,12 -> 100000.12*/
        numberStr = numberStr.replace(/\s/g, '').replace(/\,/g, '.');
    }
    numberStr = numberStr.replace(/\s/g, '').replace(/\,/g, '.');
    return parseFloat(numberStr);
}

