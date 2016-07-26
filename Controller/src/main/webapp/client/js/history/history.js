/**
 * Created by Valk on 06.06.2016.
 */

function MyHistoryClass(currentCurrencyPair) {
    if (MyHistoryClass.__instance) {
        return MyHistoryClass.__instance;
    } else if (this === window) {
        return new MyHistoryClass(currentCurrencyPair);
    }
    MyHistoryClass.__instance = this;
    /**/
    var that = this;
    var $myhistoryContainer = $('#myhistory');
    var $myHistoryActivePage;
    var myOrders;
    var $myordersContainer = $('#myorders');
    var inputOutput;
    var $inputOutputContainer = $('#myinputoutput');
    var myReferral;
    var $myreferralContainer = $('#myreferral');
    /**/
    function showMyHistoryPage($myHistoryActivePage) {
        if ($myhistoryContainer.hasClass('hidden')) {
            return;
        }
        $myHistoryActivePage.siblings('.center-frame-container').addClass('hidden');
        $myHistoryActivePage.removeClass('hidden');
    }
    /**/
    this.updateAndShowAll = function () {
        showMyHistoryPage($myHistoryActivePage);
        that.getAndShowMyOrdersPage();
        that.getAndShowInputOutputPage();
        that.getAndShowMyReferralPage();
    };

    this.getAndShowMyOrdersPage = function () {
        if ($myhistoryContainer.hasClass('hidden')) {
            return;
        }
        myOrders.syncCurrencyPairSelector();
        myOrders.updateAndShowAll();
    };

    this.getAndShowMyReferralPage = function () {
        if ($myhistoryContainer.hasClass('hidden')) {
            return;
        }
        myReferral.syncCurrencyPairSelector();
        myReferral.updateAndShowAll();
    };

    this.getAndShowInputOutputPage = function () {
        if ($myhistoryContainer.hasClass('hidden')) {
            return;
        }
        /**/
    };
    /*=====================================================*/
    (function init (currentCurrencyPair) {
        myOrders = new MyOrdersClass(currentCurrencyPair);
        myReferral = new MyReferralClass(currentCurrencyPair);
        inputOutput = new InputOutputClass(currentCurrencyPair);
        /**/
        $('#myhistory-button-orders').addClass('active');
        /**/
        $('#myhistory-button-orders').on('click', function () {
            $('.myhistory__button').removeClass('active');
            $(this).addClass('active');
            $myHistoryActivePage = $myordersContainer;
            showMyHistoryPage($myHistoryActivePage);
            that.updateAndShowAll();
        });
        $('#myhistory-button-inputoutput').on('click', function () {
            $('.myhistory__button').removeClass('active');
            $(this).addClass('active');
            $myHistoryActivePage = $inputOutputContainer;
            showMyHistoryPage($myHistoryActivePage);
            that.updateAndShowAll();
        });
        $('#myhistory-button-referral').on('click', function () {
            $('.myhistory__button').removeClass('active');
            $(this).addClass('active');
            $myHistoryActivePage = $myreferralContainer;
            showMyHistoryPage($myHistoryActivePage);
            that.updateAndShowAll();
        });
        $myHistoryActivePage = $myordersContainer;
        showMyHistoryPage($myHistoryActivePage);
        that.updateAndShowAll();
    })(currentCurrencyPair);

}
