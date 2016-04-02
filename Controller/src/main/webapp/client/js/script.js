$(".reveal").click(function () {
    $('.reveal ul').slideToggle();
});

$("#other_pairs").click(function () {
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


$('.adminForm-toggler').click(function () {
    if ($(this).hasClass('active')) {
        return;
    }else {
        $('.tab-pane').removeClass('active');
        $('.adminForm-toggler').removeClass('active');
        $(this).addClass('active');
        var idx = $(this).index();
        $('.tab-pane:eq('+idx+')').addClass('active');
    }
});


//Enable REGISTER button if pass == repass when entering repass
$(document).ready(function () {
    /*Activates submit button if all field filled correct and capcha is passed
     * */
    if (document.getElementById("#register_button")) {
        document.getElementById("#register_button").disabled = true;
    }
    $("#repass").keyup(function () {
        console.log("keyup");
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


