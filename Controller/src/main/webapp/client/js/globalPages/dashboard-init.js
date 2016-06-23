/**
 * Created by Valk on 18.06.2016.
 */

var leftSider;
var rightSider;
var trading;
var myWallets;
var myHistory;
var orders;
/*for testing*/
var REFRESH_INTERVAL_MULTIPLIER = 1;

$(function dashdoardInit() {
    try {
        /*FOR EVERYWHERE ... */
        $(".input-block-wrapper__input").prop("autocomplete", "off");
        $(".numericInputField").prop("autocomplete", "off");
        $(".numericInputField").keypress(
            function (e) {
                return e.charCode >= 48 && e.charCode <= 57 || e.charCode == 46 || e.charCode == 0
            }
        );
        /*... FOR EVERYWHERE*/

        /*FOR HEADER...*/
        $('#menu-traiding').on('click', function () {
            trading.syncCurrencyPairSelector();
            showPage('trading');
            trading.updateAndShowAll();
        });
        $('#menu-mywallets').on('click', function () {
            showPage('balance-page');
            myWallets.getAndShowMyWalletsData();
        });
        $('#menu-myhistory').on('click', function () {
            showPage('myhistory');
            myHistory.updateAndShowAll();
        });
        $('#menu-orders').on('click', function () {
            orders.syncCurrencyPairSelector();
            showPage('orders');
            orders.updateAndShowAll();
        });
        /*...FOR HEADER*/

        /*FOR LEFT-SIDER ...*/
        leftSider = new LeftSiderClass();
        /*...FOR LEFT-SIDER*/

        /*FOR CENTER ON START UP ...*/
        syncCurrentParams(null, null, null, function (data) {
            trading = new TradingClass(data.period, data.chartType, data.currencyPair.name);
            myWallets = new MyWalletsClass();
            myHistory = new MyHistoryClass(data.currencyPair.name);
            orders = new OrdersClass(data.currencyPair.name);
        });
        /*...FOR CENTER ON START UP*/

        /*FOR RIGHT-SIDER ...*/
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
}

function syncCurrentParams(currencyPairName, period, chart, callback) {
    var url = '/dashboard/currentParams?';
    /*if parameter is empty, in response will be retrieved current value is set or default if non*/
    url = url + (currencyPairName ? '&currencyPairName=' + currencyPairName : '');
    url = url + (period ? '&period=' + period : '');
    url = url + (chart ? '&chart=' + chart : '');
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
