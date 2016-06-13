/**
 * Created by Valk on 05.06.2016.
 */

function LeftSiderClass() {
    if (LeftSiderClass.__instance) {
        return LeftSiderClass.__instance;
    } else if (this === window) {
        return new LeftSiderClass(currentCurrencyPair);
    }
    LeftSiderClass.__instance = this;
    /**/
    var that = this;
    /**/
    var timeOutIdForStatisticsForMyWallets;
    var refreshIntervalForStatisticsForMyWallets = 5000*REFRESH_INTERVAL_MULTIPLIER;
    var timeOutIdForStatisticsForAllCurrencies;
    var refreshIntervalForStatisticsForAllCurrencies = 5000*REFRESH_INTERVAL_MULTIPLIER;
    /**/
    var showLog = false;

    this.getStatisticsForMyWallets = function (refreshIfNeeded) {
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getStatisticsForMyWallets');
        }
        var $mywalletsTable = $('#mywallets_table').find('tbody');
        var url = '/dashboard/myWalletsStatistic?refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            success: function (data) {
                if (!data) return;
                if (data.length == 0 || data[0].needRefresh) {
                    var $tmpl = $('#mywallets_table_row').html().replace(/@/g, '%');
                    $mywalletsTable.find('tr').has('td').remove();
                    data.forEach(function (e) {
                        $mywalletsTable.append(tmpl($tmpl, e));
                    });
                    blink($mywalletsTable);
                }
                clearTimeout(timeOutIdForStatisticsForMyWallets);
                timeOutIdForStatisticsForMyWallets = setTimeout(function () {
                    that.getStatisticsForMyWallets(true);
                }, refreshIntervalForStatisticsForMyWallets);
            }
        });
    };

    this.getStatisticsForAllCurrencies = function (refreshIfNeeded) {
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getStatisticsForAllCurrencies');
        }
        var $currencyTable = $('#currency_table').find('tbody');
        var url = '/dashboard/currencyPairStatistic?refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            success: function (data) {
                if (!data) return;
                if (data.length == 0 || data[0].needRefresh) {
                    var $tmpl = $('#currency_table_row').html().replace(/@/g, '%');
                    $currencyTable.find('tr').has('td').remove();
                    data.forEach(function (e) {
                        $currencyTable.append(tmpl($tmpl, e));
                    });
                    blink($('#currency_table'));
                }
                clearTimeout(timeOutIdForStatisticsForAllCurrencies);
                timeOutIdForStatisticsForAllCurrencies = setTimeout(function () {
                    that.getStatisticsForAllCurrencies(true);
                }, refreshIntervalForStatisticsForAllCurrencies);
            }
        });
    };
    /*===========================================================*/
    (this.init = function () {
        that.getStatisticsForAllCurrencies();
        that.getStatisticsForMyWallets();
    })();
}
