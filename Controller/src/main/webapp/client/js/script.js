$(document).delegate('*[data-toggle="lightbox"]', 'click', function(event) {
    event.preventDefault();
    $(this).ekkoLightbox();
    if ($(".delete_img").length !==0) {
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
        getStatisticsForCurrency();
    }();

    //set 'click' handler for container because new '.pair-selector__menu-item' may be added
    $(".pair-selector__menu").on('click', '.pair-selector__menu-item', function (e) {
        $('.pair-selector__menu-item').removeClass('active');
        $(this).addClass('active');
        $('#pair-selector>div:first-child').text($(this).text());
        getStatisticsForCurrency($(this).text());
    });

    $(".numericInputField").keypress(
        function (e) {
            return e.charCode >= 48 && e.charCode <= 57 || e.charCode == 46 || e.charCode == 0
        }
    );

});


function getStatisticsForCurrency(pairName, period) {
    var url = '/dashboard/changeCurrencyPair';
    url = pairName ? url + '?currencyPairName=' + (pairName + '&') : (url + '?');
    url = period ? url + 'period=' + period : url;
    $.ajax({
        url: url,
        type: 'GET',

        success: function (data) {
            $('#lastOrderAmountBase>span:nth-child(2)').text(data.lastOrderAmountBase + ' ' + data.currencyPair.currency1.name);
            $('#firstOrderRate>span:nth-child(2)').text(data.firstOrderRate + ' ' + data.currencyPair.currency2.name);
            $('#lastOrderRate>span:nth-child(2)').text(data.lastOrderRate + ' ' + data.currencyPair.currency2.name);
            $('#sumBase>span:nth-child(2)').text(data.sumBase + ' ' + data.currencyPair.currency1.name);
            $('#sumConvert>span:nth-child(2)').text(data.sumConvert + ' ' + data.currencyPair.currency2.name);
            if ($('#minRate').is('div')) {
                $('#minRate>span').text(data.minRate + ' ' + data.currencyPair.currency2.name);
                $('#maxRate>span').text(data.maxRate + ' ' + data.currencyPair.currency2.name);
            }
            //
            if (!$('#pair-selector>div:first-child').text()) {
                createPairSelectorMenu(data.currencyPair.name);
            }
            if (pairName) {
                window.location.reload();
            }
        }
    });
}

function createPairSelectorMenu(currencyPairName) {
    $('.pair-selector__menu').empty();
    $.ajax({
        url: '/dashboard/createPairSelectorMenu',
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
        }
    });
}

function setActivePeriodSwitcherButton(backDealInterval) {
    var id = backDealInterval.intervalValue + backDealInterval.intervalType.toLowerCase();
    $('.period-menu__item').removeClass('active');
    $('#' + id).addClass('active');
}

function promptDeleteDoc(id, path, userId) {
    if (confirm($('#prompt_delete_rqst').html())) {
        var data = "fileId=" + id + "&path=" + path + "&userId=" + userId;
        $.ajax('/admin/users/deleteUserFile',{
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            dataType: 'json',
            data: data
        }).done(function(result) {
            alert(result['success']);
            $('.modal').modal('toggle');
            $('#_' + id).remove();
        }).fail(function(error){
            console.log(JSON.stringify(error));
            alert(error['responseJSON']['error'])
        });
    }
}



