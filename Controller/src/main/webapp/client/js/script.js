$(document).ready(function () {
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

    +function switchPairSelector() {
        if ($('ul').is('.large-pair-selector')) {
            $("#pair-selector").css('display', 'none');
            $("#pair-selector-arrow").css('display', 'none');
        }
    }();

    $("#pair-selector").click(function (e) {
        e.preventDefault();
        $('.pair-selector__menu').slideToggle();
    });

    +function initCurrencyPairData() {
        if (!$('#pair-selector>div:first-child').text()) {
            getNewCurrencyPairData();
        }
    }();

    //set 'click' handler for container because new '.pair-selector__menu-item' may be added
    $(".pair-selector__menu").on('click', '.pair-selector__menu-item', function (e) {
        $('.pair-selector__menu-item').removeClass('active');
        $(this).addClass('active');
        $('#pair-selector>div:first-child').text($(this).text());
        getNewCurrencyPairData($(this).text());
    });

});



function getNewCurrencyPairData(newPairName) {
    var url = '/admin/changeCurrencyPair';
    $.ajax({
        url: newPairName ? url + '?currencyPairName=' + newPairName : url,
        type: 'GET',

        success: function (data) {
            $('#lastOrder>span:nth-child(2)').text(data['amountBuy'] + ' ' + data['lastOrderCurrency']);
            $('#priceStart>span:nth-child(2)').text(data['amountBuy'] + ' ' + data['lastOrderCurrency']);
            $('#priceEnd>span:nth-child(2)').text(data['amountBuy'] + ' ' + data['lastOrderCurrency']);
            $('#sumAmountBuyClosed>span:nth-child(2)').text(data['sumAmountBuyClosed'] + ' ' + data['currency1']);
            $('#sumAmountSellClosed>span:nth-child(2)').text(data['sumAmountSellClosed'] + ' ' + data['currency2']);
            //
            if (!$('#pair-selector>div:first-child').text()) {
                createPairSelectorMenu(data['name']);
            }
            if (newPairName) {
                window.location.reload();
            }
        }
    });
}

function createPairSelectorMenu(currencyPairName) {
    $('.pair-selector__menu').empty();
    $.ajax({
        url: '/admin/createPairSelectorMenu',
        type: 'GET',

        success: function (data) {
            data.forEach(function (e) {
                if (e === currencyPairName) {
                    $('.pair-selector__menu').append('<div class="pair-selector__menu-item active">' + e + '</div>');
                    $('#pair-selector>div:first-child').text(e);
                } else {
                    $('.pair-selector__menu').append('<div class="pair-selector__menu-item">' + e + '</div>');
                }
            });
        },

        error: function(jqXHR, textStatus, errorThrown){
            console.log('******************************');
            console.log(jqXHR);
            console.log(textStatus);
            console.log(errorThrown);
            console.log('******************************');
        }
    });
}




