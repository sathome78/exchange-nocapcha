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
var $currentSubMenuItem;
var notifications;

var wss; //web-socket connection
var ordersSubscription;
var connectedPS = false;
var currentCurrencyPairId;
var subscribedCurrencyPairId;


var socket = new SockJS('/public_socket');
var client = Stomp.over(socket);

var onConnect = function() {
    connectedPS = true;
    subscribeTradeOrders();
};

function subscribeTradeOrders() {
    console.log('subscribe to ' + currentCurrencyPairId);
    if (subscribedCurrencyPairId != undefined) {
        ordersSubscription.unsubscribe();
    }
    ordersSubscription = client.subscribe("/app/topic.trade_orders." + currentCurrencyPairId, function(message) {
        subscribedCurrencyPairId = currentCurrencyPairId;
        onTradeOrdersMessage(JSON.parse(message.body));
    });
}


function connectSocket() {
    client.connect({}, onConnect);
}

function onTradeOrdersMessage(message) {
    console.log(message);
    $.each( message, function( key, value ) {
        console.log(key);
        switch (key) {
            case "INIT" : {
                initTradeOrders(value);
                break;
            }
            case "ADD" : {
                break;
            }
            case "REMOVE" : {
                break;
            }
            case "REPLACE" : {
                break
            }
        }
    });
}

function initTradeOrders(array) {
    console.log(array);
    array.forEach(function(object){
        object = JSON.parse(object);
        switch (object.type){
            case "BUY" : {
                console.log('update buy orders');
                trading.updateAndShowBuyOrders(object.data, true);
                break;
            }
            case "SELL" : {
                console.log('update sell orders');
                trading.updateAndShowSellOrders(object.data, true);
                break;
            }
        }
    });
}


/*function connectOrdersSocket(sessionId) {
    if (wss!=null) {
        wss.close();
    }
    console.log("id" + sessionId);
    wss = new SockJS('/public_sockets?session_id=' + sessionId);
    console.log("connect to client");

    wss.onopen = function() {
        console.log('socket connected');
        wss.send('trading:newCurrencyPair');
    };

    wss.onmessage = function (message) {
        console.log(message);
        var payload = JSON.parse(message.data);
        switch (payload.source) {
            case "orders": {
                ordersMessageHandle(payload);
                break;
            }
        }

    };
}*/



$(function dashdoardInit() {
    var sessionId = $('#session').text();
    connectSocket(sessionId);
    var $2faModal = $('#noty2fa_modal');
    var $2faConfirmModal = $('#noty2fa_confirm_modal');
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
                    if (e.charCode >= 48 && e.charCode <= 57 || e.charCode == 46 || e.charCode == 44 || e.charCode == 0) {
                        var keyPressed = e.key == ',' ? '.' : e.key;

                        if (keyPressed == '.' && $(this).val().indexOf('.') >= 0) {
                            return false;
                        }
                        var str = $(this).val() + keyPressed;
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
                if (val[val.length - 1] == ',') {
                    val = val.replace(',', '.');
                    $(this).val(val)
                }
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

        $('#login-qr').html("<img src='https://chart.googleapis.com/chart?chs=150x150&chld=L|2&cht=qr&chl=" + sessionId + "'>");
        /*...FOR HEADER*/

        /*FOR LEFT-SIDER ...*/

        leftSider = new LeftSiderClass();
        $('#currency_table').on('click', 'td:first-child', function (e) {
            var newCurrentCurrencyPairName = $(this).text().trim();
            syncCurrentParams(newCurrentCurrencyPairName, null, null, null, null, function (data) {
                if ($currentPageMenuItem.length) {
                    $currentPageMenuItem.click();
                    if ($currentSubMenuItem && $currentSubMenuItem.length) {
                        $currentSubMenuItem.click();
                    }
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


        syncCurrentParams(null, null, null, null, null, function (data) {
            showPage($('#startup-page-id').text().trim());
            trading = new TradingClass(data.period, data.chartType, data.currencyPair.name, data.orderRoleFilterEnabled);
            leftSider.setOnWalletsRefresh(function () {
                trading.fillOrderBalance($('.currency-pair-selector__button').first().text().trim())
            });
            myWallets = new MyWalletsClass();
            myStatements = new MyStatementsClass();
            myHistory = new MyHistoryClass(data.currencyPair.name);
            orders = new OrdersClass(data.currencyPair.name);
            showSubPage($('#startup-subPage-id').text().trim());
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

        /*FOR POLL ...*/
       /*var startPoll = $("#start-poll").val() == 'true';
        if (startPoll) {
            var $pollDialog = $("#poll-modal");
            $pollDialog.modal();
            doPoll($pollDialog);
        }*/
        /*...FOR POLL*/
        /*2fa notify*/

        var notify2fa = $("#noty2fa").val() == 'true';
        console.log('2fa here ' + notify2fa );
        if (notify2fa) {
          $2faModal.modal({
              backdrop: 'static',
              keyboard: false
          });
        }
        /*end 2fa notify*/
    } catch (e) {
        /*it's need for ignoring error from old interface*/
    }

    $('#decline_2fa').on('click', function () {
        $2faModal.modal('hide');
        $2faConfirmModal.modal({
            backdrop: 'static',
            keyboard: false
        });
    });

    $('#decline_2fa_finally').on('click', function () {
        $2faConfirmModal.modal('hide');
    });

    $('.accept_2fa').on('click', function () {
        window.location.href = '/settings?2fa';
    });

});



function showPage(pageId) {
    if (!pageId) {
        return;
    }
    $('.center-frame-container').addClass('hidden');
    $('#' + pageId).removeClass('hidden');
    $currentPageMenuItem = $('#' + $('#' + pageId).data('menuitemid'));
}

function showSubPage(subPageId) {
    if (subPageId) {
        $currentSubMenuItem = $('#' + $('#' + subPageId).data('submenuitemid'));
        $($currentSubMenuItem).click();
    }
}


function syncCurrentParams(currencyPairName, period, chart, showAllPairs, enableFilter, callback) {
    var url = '/dashboard/currentParams?';
    /*if parameter is empty, in response will be retrieved current value is set or default if non*/
    url = url + (currencyPairName ? '&currencyPairName=' + currencyPairName : '');
    url = url + (period ? '&period=' + period : '');
    url = url + (chart ? '&chart=' + chart : '');
    url = url + (showAllPairs != null ? '&showAllPairs=' + showAllPairs : '');
    url = url + (enableFilter != null ? '&orderRoleFilterEnabled=' + enableFilter : '');
    $.ajax({
        url: url,
        type: 'GET',
        success: function (data) {
            /*sets currencyBaseName for all pages*/
            $('.currencyBaseName').text(data.currencyPair.currency1.name);
            $('.currencyConvertName').text(data.currencyPair.currency2.name);
            /**/
            currentCurrencyPairId = data.currencyPair.id;
            console.log('connected= ' + connectedPS + ' subscribedID=' + subscribedCurrencyPairId + ' currentId=' + currentCurrencyPairId);
            if (connectedPS && subscribedCurrencyPairId != currentCurrencyPairId) {
                subscribeTradeOrders()
            }
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


function doPoll($pollDialog) {
    Survey.Survey.cssType = "bootstrap";
    var surveyToken;
    var surveyData = getSurveyData(function (response) {
        var surveyData = response;
        surveyToken = surveyData.token;
        var surveyJSON = JSON.parse(surveyData.json);
        var surveyItems = surveyData.items;
        /**/
        surveyJSON.locale = $.cookie("myAppLocaleCookie");
        surveyJSON.pages.forEach(function (page, pi) {
            page.elements.forEach(function (element, ei) {
                var name = element.name;
                surveyItems.forEach(function (item) {
                    if (item.name == name) {
                        surveyJSON.pages[pi].elements[ei].title = item.title;
                        return;
                    }
                })
            });
        });
        $pollDialog.find("#description").html(surveyData.description);
        /**/
        var survey = new Survey.Model(surveyJSON);
        $("#surveyContainer").Survey({
            model: survey,
            onComplete: sendDataToServer
        });
    });

    function sendDataToServer(survey) {
        survey.sendResult(surveyToken);
        var result = JSON.stringify(survey.data);
        savePollAsDone(surveyToken, result);
    }

    function getSurveyData(callback) {
        $.ajax({
            type: 'GET',
            url: '/survey/getSurvey',
            success: function (data) {
                callback(data);
            }
        });
    }

    function savePollAsDone(surveyToken, result) {
        $.ajax({
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val(),
            },
            contentType: "text/plain; charset=utf-8",
            type: 'POST',
            url: '/survey/saveAsDone?surveyToken=' + surveyToken,
            data: result,
        });
    }
}
