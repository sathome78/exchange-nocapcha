/**
 * Created by Valk on 04.06.2016.
 */

function MyWalletsClass() {
    if (MyWalletsClass.__instance) {
        return MyWalletsClass.__instance;
    } else if (this === window) {
        return new MyWalletsClass();
    }
    MyWalletsClass.__instance = this;
    /**/
    var that = this;
    /**/
    var timeOutIdForMyWalletsData;
    var refreshIntervalForMyWalletsData = 5000;
    /**/
    var showLog = false;
    /**/
    var $balanceContainer = $('#balance-page');

    this.getAndShowMyWalletsData = function (refreshIfNeeded) {
        if ($balanceContainer.hasClass('hidden')) {
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowMyWalletsData');
        }
        var $balanceTable = $('#balance-table').find('tbody');
        var url = '/dashboard/myWalletsData'+'?refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            success: function (data) {
                if (!data) return;
                if (data.length == 0 || data[0].needRefresh) {
                    var $tmpl = $('#balance-table_row').html().replace(/@/g, '%');
                    $balanceTable.find('tr').has('td').remove();
                    data.forEach(function (e) {
                        $balanceTable.append(tmpl($tmpl, e));
                    });
                    blink($balanceTable.find('td:not(:first-child)'));
                }
                clearTimeout(timeOutIdForMyWalletsData);
                timeOutIdForMyWalletsData = setTimeout(function () {
                    that.getAndShowMyWalletsData(true);
                }, refreshIntervalForMyWalletsData);
            }
        });
    };
    /*=====================================================*/
    (function init() {
        that.getAndShowMyWalletsData();
    })();
}