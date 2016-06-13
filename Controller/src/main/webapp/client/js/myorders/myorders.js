/**
 * Created by Valk on 06.06.2016.
 */

function MyOrdersClass(currentCurrencyPair) {
    if (MyOrdersClass.__instance) {
        return MyOrdersClass.__instance;
    } else if (this === window) {
        return new MyOrdersClass(currentCurrencyPair);
    }
    MyOrdersClass.__instance = this;
    /**/
    var that = this;
    var timeOutIdForMyOrdersData;
    var refreshIntervalForMyOrdersData = 5000*REFRESH_INTERVAL_MULTIPLIER;
    /**/
    var showLog = false;
    /**/
    var tableSellId = "myorders-sell-table";
    var tableBuyId = "myorders-buy-table";
    var $myordersContainer = $('#myorders');
    var myordersCurrencyPairSelector;
    var myordersStatusForShow;
    var tableSellPageNumber = 1;
    var tableBuyPageNumber = 1;
    var fieldVisibleForOpenStatus = [
        'myo_orid',
        'myo_dcrt',
        'myo_amnt',
        'myo_rate',
        'myo_totl',
        'myo_comm',
        'myo_amcm',
        'myo_delt'];
    var fieldVisibleForCancelledStatus = [
        'myo_orid',
        'myo_dcrt',
        'myo_amnt',
        'myo_rate',
        'myo_totl',
        'myo_cnsl'];
    var fieldVisibleForClosedStatus = [
        'myo_orid',
        'myo_dcrt',
        'myo_amnt',
        'myo_rate',
        'myo_totl',
        'myo_comm',
        'myo_amcm',
        'myo_deal'];


    function onCurrencyPairChange(currentCurrencyPair) {
        that.updateAndShowAll(currentCurrencyPair);
    }

    this.syncCurrencyPairSelector = function () {
        myordersCurrencyPairSelector.syncState();
    };

    this.updateAndShowAll = function (refreshIfNeeded) {
        that.getAndShowMySellOrdersData(refreshIfNeeded);
        that.getAndShowMyBuyOrdersData(refreshIfNeeded);
    };


    this.getAndShowMySellOrdersData = function (refreshIfNeeded) {
        if ($myordersContainer.hasClass('hidden')) {
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowMySellOrdersData');
        }
        var $myordersSellTable = $('#'+tableSellId).find('tbody');
        var url = '/dashboard/myOrdersData/' + tableSellId + '?type=SELL&status=' + myordersStatusForShow + '&page=' + tableSellPageNumber+'&refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            success: function (data) {
                if (!data) return;
                if (data.length == 0 || data[0].needRefresh) {
                    $('#' + tableSellId).addClass('hidden');
                    var $tmpl = $('#myorders-sell-table_row').html().replace(/@/g, '%');
                    $myordersSellTable.find('tr').has('td').remove();
                    data.forEach(function (e) {
                        $myordersSellTable.append(tmpl($tmpl, e));
                    });
                    showFields(myordersStatusForShow, tableSellId);
                    $('#' + tableSellId).removeClass('hidden');
                    blink($myordersSellTable.find('td:not(:first-child)'));
                }
                clearTimeout(timeOutIdForMyOrdersData);
                timeOutIdForMyOrdersData = setTimeout(function () {
                    that.updateAndShowAll(true);
                }, refreshIntervalForMyOrdersData);
            }
        });
    };

    this.getAndShowMyBuyOrdersData = function (refreshIfNeeded) {
        if ($myordersContainer.hasClass('hidden')) {
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowMyBuyOrdersData');
        }
        var $myordersBuyTable = $('#'+tableBuyId).find('tbody');
        var url = '/dashboard/myOrdersData/' + tableBuyId + '?type=BUY&status=' + myordersStatusForShow + '&page=' + tableBuyPageNumber+'&refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            success: function (data) {
                if (!data) return;
                if (data.length == 0 || data[0].needRefresh) {
                    $('#' + tableBuyId).addClass('hidden');
                    var $tmpl = $('#myorders-buy-table_row').html().replace(/@/g, '%');
                    $myordersBuyTable.find('tr').has('td').remove();
                    data.forEach(function (e) {
                        $myordersBuyTable.append(tmpl($tmpl, e));
                    });
                    showFields(myordersStatusForShow, tableBuyId);
                    $('#' + tableBuyId).removeClass('hidden');
                    blink($myordersBuyTable.find('td:not(:first-child)'));
                }
                clearTimeout(timeOutIdForMyOrdersData);
                timeOutIdForMyOrdersData = setTimeout(function () {
                    that.updateAndShowAll(true);
                }, refreshIntervalForMyOrdersData);
            }
        });
    };

    function showFields(myordersStatusForShow, tableId) {
        var fieldsList;
        switch (myordersStatusForShow) {
            case 'OPENED':
            {
                fieldsList = fieldVisibleForOpenStatus;
                break;
            }
            case 'CANCELLED':
            {
                fieldsList = fieldVisibleForCancelledStatus;
                break;
            }
            case 'CLOSED':
            {
                fieldsList = fieldVisibleForClosedStatus;
                break;
            }
        }
        $('#' + tableId).find('tr th').addClass('hidden');
        $('#' + tableId).find('tr td').addClass('hidden');
        fieldsList.forEach(function (e) {
            $('#' + tableId).find('.' + e).removeClass('hidden');
        })
    }
    /*=====================================================*/
    (function init (currentCurrencyPair) {
        myordersCurrencyPairSelector = new CurrencyPairSelectorClass('myorders-currency-pair-selector', currentCurrencyPair);
        myordersCurrencyPairSelector.init(onCurrencyPairChange);
        myordersStatusForShow = 'CLOSED';
        $('#myorders-button-deal').addClass('active');
        /**/
        $('#myorders-button-cancelled').on('click', function () {
            $('.myorders__button').removeClass('active');
            $(this).addClass('active');
            myordersStatusForShow = 'CANCELLED';
            tableSellPageNumber = 1;
            tableBuyPageNumber = 1;
            that.updateAndShowAll();
        });
        $('#myorders-button-deal').on('click', function () {
            $('.myorders__button').removeClass('active');
            $(this).addClass('active');
            myordersStatusForShow = 'CLOSED';
            tableSellPageNumber = 1;
            tableBuyPageNumber = 1;
            that.updateAndShowAll();
        });
        /**/
        syncTableParams(tableSellId, -1, function (data) {
            that.getAndShowMySellOrdersData();
        });
        syncTableParams(tableBuyId, -1, function (data) {
            that.getAndShowMyBuyOrdersData();
        });
    })(currentCurrencyPair);
}
