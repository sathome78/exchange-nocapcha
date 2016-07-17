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
    var refreshIntervalForStatisticsForMyWallets = 5000 * REFRESH_INTERVAL_MULTIPLIER;
    var timeOutIdForStatisticsForAllCurrencies;
    var refreshIntervalForStatisticsForAllCurrencies = 5000 * REFRESH_INTERVAL_MULTIPLIER;
    /**/
    var showLog = false;

    this.getStatisticsForMyWallets = function (refreshIfNeeded) {
        if (!windowIsActive) {
            clearTimeout(timeOutIdForStatisticsForMyWallets);
            timeOutIdForStatisticsForMyWallets = setTimeout(function () {
                that.getStatisticsForMyWallets(true);
            }, refreshIntervalForStatisticsForMyWallets);
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getStatisticsForMyWallets');
        }
        var $mywalletsTable = $('#mywallets_table').find('tbody');
        var url = '/dashboard/myWalletsStatistic?refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            headers: {
                "windowid": windowId
            },
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
        /*change true to false id need to poll always: if window inactive too*/
        if (false && !windowIsActive) {
            clearTimeout(timeOutIdForStatisticsForAllCurrencies);
            timeOutIdForStatisticsForAllCurrencies = setTimeout(function () {
                that.getStatisticsForAllCurrencies(true);
            }, refreshIntervalForStatisticsForAllCurrencies);
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getStatisticsForAllCurrencies');
        }
        var $currencyTable = $('#currency_table').find('tbody');
        var url = '/dashboard/currencyPairStatistic?refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            headers: {
                "windowid": windowId
            },
            success: function (data) {
                if (!data) return;
                if ('redirect' in data) {
                    var registered = $('#hello-my-friend')[0];
                    window.location = data.redirect.url + (data.redirect.urlParam1 && registered ? "?errorNoty=" + data.redirect.urlParam1 : '');
                    return;
                }
                data = data['list'];
                if (data.length == 0 || data[0].needRefresh) {
                    var $tmpl = $('#currency_table_row').html().replace(/@/g, '%');
                    $currencyTable.find('tr').has('td').remove();
                    data.forEach(function (e) {
                        $currencyTable.append(tmpl($tmpl, e));
                    });
                    blink($('#currency_table'));
                }
                setPairFilter();
                clearTimeout(timeOutIdForStatisticsForAllCurrencies);
                timeOutIdForStatisticsForAllCurrencies = setTimeout(function () {
                    that.getStatisticsForAllCurrencies(true);
                }, refreshIntervalForStatisticsForAllCurrencies);
                if (showLog) {
                    console.log(new Date() + ' getStatisticsForAllCurrencies ' + ' success');
                }
            },
            error: function (jqXHR, status, error) {
                if (showLog) {
                    console.log(new Date() + ' getStatisticsForAllCurrencies ' + ' error: ' + jqXHR + ' | ' + status + ' | ' + error);
                }
            }
        });
    };
    /*===========================================================*/
    (function init() {
        clearTimeout(timeOutIdForStatisticsForAllCurrencies);
        $.ajax({
            url: '/dashboard/firstentry',
            type: 'GET',
            success: function () {
                that.getStatisticsForAllCurrencies();
            }
        });
        that.getStatisticsForMyWallets();
        $('#refferal-generate').on('click', generateReferral);
        $('#refferal-copy').on('click', function () {
            selectAndCopyText($('#refferal-reference')[0]);
        });
        $('#pair-filter').on('keyup', function (e) {
            setPairFilter();
        });
        generateReferral();
    })();

    function setPairFilter() {
        var str = $('#pair-filter').val().toUpperCase();
        $('#currency_table').find('td:first-child').each(function (idx) {
            var pair = $(this).text();
            if (!pair || pair.indexOf(str) != -1) {
                $(this).parent().removeClass('hidden');
            } else {
                $(this).parent().addClass('hidden');
            }
        })
    }

    function selectAndCopyText(e) {
        var range = document.createRange();
        range.selectNodeContents(e);
        var selection = window.getSelection();
        selection.removeAllRanges();
        selection.addRange(range);
        document.execCommand("copy");
        selection.removeAllRanges();
        blink_green($('#refferal-reference'));
    }

    function generateReferral() {
        $.ajax('/generateReferral', {
            method: 'get'
        }).done(function (e) {
            $('#refferal-reference').html(e['referral']);
        });
        blink($('#refferal-reference'));
    }
}
