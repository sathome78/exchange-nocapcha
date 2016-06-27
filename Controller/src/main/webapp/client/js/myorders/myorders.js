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
    var tableSellPageSize = 5;
    var tableBuyPageSize = 5;
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


    this.getAndShowMySellOrdersData = function (refreshIfNeeded, page, direction) {
        if ($myordersContainer.hasClass('hidden') || !windowIsActive) {
            clearTimeout(timeOutIdForMyOrdersData);
            timeOutIdForMyOrdersData = setTimeout(function () {
                that.updateAndShowAll(true);
            }, refreshIntervalForMyOrdersData);
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowMySellOrdersData');
        }
        var $myordersSellTable = $('#'+tableSellId).find('tbody');
        var url = '/dashboard/myOrdersData/' + tableSellId + '' +
            '?type=SELL&status=' + myordersStatusForShow + '' +
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
                if (data.length > 0) {
                    $('.myorders-sell-table__page').text(data[0].page);
                } else if (refreshIfNeeded){
                    var p = parseInt($('.myorders-sell-table__page').text());
                    $('.myorders-sell-table__page').text(++p);
                }
                clearTimeout(timeOutIdForMyOrdersData);
                timeOutIdForMyOrdersData = setTimeout(function () {
                    that.updateAndShowAll(true);
                }, refreshIntervalForMyOrdersData);
            }
        });
    };

    this.getAndShowMyBuyOrdersData = function (refreshIfNeeded, page, direction) {
        if ($myordersContainer.hasClass('hidden') || !windowIsActive) {
            clearTimeout(timeOutIdForMyOrdersData);
            timeOutIdForMyOrdersData = setTimeout(function () {
                that.updateAndShowAll(true);
            }, refreshIntervalForMyOrdersData);
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowMyBuyOrdersData');
        }
        var $myordersBuyTable = $('#'+tableBuyId).find('tbody');
        var url = '/dashboard/myOrdersData/' + tableBuyId + '' +
            '?type=BUY&status=' + myordersStatusForShow + '' +
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
                if (data.length > 0) {
                    $('.myorders-buy-table__page').text(data[0].page);
                } else if (refreshIfNeeded){
                    var p = parseInt($('.myorders-buy-table__page').text());
                    $('.myorders-buy-table__page').text(++p);
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
            that.updateAndShowAll();
        });
        $('#myorders-button-deal').on('click', function () {
            $('.myorders__button').removeClass('active');
            $(this).addClass('active');
            myordersStatusForShow = 'CLOSED';
            that.updateAndShowAll();
        });
        /**/
        syncTableParams(tableSellId, tableSellPageSize, function (data) {
            that.getAndShowMySellOrdersData();
        });
        syncTableParams(tableBuyId, tableBuyPageSize, function (data) {
            that.getAndShowMyBuyOrdersData();
        });
        /**/
        $('.myorders-sell-table__backward').on('click', function(){
            that.getAndShowMySellOrdersData(true, null, 'BACKWARD');
        });
        $('.myorders-sell-table__forward').on('click', function(){
            that.getAndShowMySellOrdersData(true, null, 'FORWARD');
        });
        /**/
        $('.myorders-buy-table__backward').on('click', function(){
            that.getAndShowMyBuyOrdersData(true, null, 'BACKWARD');
        });
        $('.myorders-buy-table__forward').on('click', function(){
            that.getAndShowMyBuyOrdersData(true, null, 'FORWARD');
        });
    })(currentCurrencyPair);
}
