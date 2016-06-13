$(document).delegate('*[data-toggle="lightbox"]', 'click', function (event) {
    event.preventDefault();
    $(this).ekkoLightbox();
    if ($(".delete_img").length !== 0) {
        $(".delete_img").submit(function (e) {
            e.preventDefault();
            var array = $(this).serializeArray();
            var id = array[0]['value'];
            var path = array[1]['value'];
            var userId = array[2]['value'];
            promptDeleteDoc(id, path, userId);
        });
    }

    if ($('#usersTable').length !== 0) {
        $('#usersTable').DataTable();
    }
});

$(function () {

    $(".reveal").click(function () {
        $('.reveal ul').slideToggle();
    });

    $("#other_pairs").click(function (e) {
        $('#other_pairs ul').slideToggle();
        $("#other_pairs").toggleClass("whiter");
    });

    $('.orderForm-toggler').click(function () {
        if ($(this).hasClass('active')) {
            return;
        }
        $('.tab-pane').toggleClass('active');
        $('.orderForm-toggler').toggleClass('active');
    });

    +function syncOrderFormTabPane() {
        var idx = $('.orderForm-toggler.active').index();
        if (idx >= 0) {
            $('.tab-pane').removeClass('active');
            $('.tab-pane:eq(' + idx + ')').addClass('active');
        }
    }();

    $('.adminForm-toggler').click(function () {
        if ($(this).hasClass('active')) {
            return;
        } else {
            $('.tab-pane').removeClass('active');
            $('.adminForm-toggler').removeClass('active');
            $(this).addClass('active');
            var idx = $(this).index();
            $('.tab-pane:eq(' + idx + ')').addClass('active');
        }
    });

    //Enable REGISTER button if pass == repass when entering repass
    /*Activates submit button if all field filled correct and capcha is passed
     * */
    if (document.getElementById("#register_button")) {
        document.getElementById("#register_button").disabled = true;
    }
    $("#repass").keyup(function () {
        var pass = $('#pass').val();
        var repass = $('#repass').val();
        var email = $('#email').val();
        var login = $('#login').val();
        var capchaPassed = $('#cpch-field').hasClass('passed');

        if ((pass.length != 0) && (pass === repass)) {
            $('.repass').css("display", "block");
            if ((email.length != 0) && (login.length != 0) && (capchaPassed)) {
                $("#register_button").prop('disabled', false);
            } else {
                $("#register_button").prop('disabled', true);
            }
        }
        else {
            $('.repass').css("display", "none");
            $("#register_button").prop('disabled', true);
        }
    });
});


function promptDeleteDoc(id, path, userId) {
    if (confirm($('#prompt_delete_rqst').html())) {
        var data = "fileId=" + id + "&path=" + path + "&userId=" + userId;
        $.ajax('/admin/users/deleteUserFile', {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            dataType: 'json',
            data: data
        }).done(function (result) {
            alert(result['success']);
            $('.modal').modal('hide');
            $('#_' + id).remove();
        }).fail(function (error) {
            console.log(JSON.stringify(error));
            alert(error['responseJSON']['error'])
        });
    }
}

/**************************

 **************************/
var leftSider;
var dashboard;
var myWallets;
var myHistory;
var orders;
/*for testing*/
var REFRESH_INTERVAL_MULTIPLIER = 1;

$(function init() {
    try {
        /* better in css - for more fluent
        $('.graphic-wrapper').css({'min-height': '238px'});
        */
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
            dashboard.syncCurrencyPairSelector();
            showPage('dashboard');
            dashboard.updateAndShowAll();
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
            dashboard = new DashboardClass(data.period, data.chartType, data.currencyPair.name);
            myWallets = new MyWalletsClass();
            myHistory = new MyHistoryClass(data.currencyPair.name);
            orders = new OrdersClass(data.currencyPair.name);
        });
        /*...FOR CENTER ON START UP*/

        /*FOR RIGHT-SIDER ...*/

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

function syncTableParams(tableId, limit, callback) {
    var url = '/dashboard/tableParams/' + tableId + '?';
    /*if parameter is empty, in response will be retrieved current value is set or default if non*/
    url = url + (limit ? '&limit=' + limit : '');
    $.ajax({
        url: url,
        type: 'GET',
        success: function (data) {
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

function blink($element) {
    $element.addClass('blink');
    setTimeout(function () {
        $element.removeClass('blink');
    }, 250);
}


/*$(function news(){
    var $newsContentPlace = $('#newstopic');
    var url = '/news/2015/MAY/27/48/newstopic.html';
    //var url = '/news/2015/MAY/27/48/newstopic';
    *//*$.ajax({
        url: url,
        type: 'GET',
       *//**//* headers: {
            Accept : "application/json; charset=utf-8",
            "Content-Type": "application/json; charset=utf-8"
        },
        contentType: "application/json; charset=utf-8",*//**//*

        success: function (data) {

            $newsContentPlace.append(data.content);
            //$newsContentPlace.append(data);
        }
    });*//*
    $newsContentPlace.load(url);
});*/


