


$(function () {
    var $loginForm = $('#login_form');
    var $pinForm = $('#pin_code');
    var $pinInput = $('#pin');
    var $pinSendButton = $('#send_pin');

    console.log(window.location.href);
    if (window.location.href.indexOf('?pin') > 0) {
        $loginForm.css("display", "none");
        $pinForm.css("display", "block");
    } else  {
        $loginForm.css("display", "block");
        $pinForm.css("display", "none");
    }

    $pinInput.on('input', function () {
        checkPinInput()
    });

    function checkPinInput() {
       if ($pinInput.val().length > 3 && $pinInput.val().length < 20) {
           $pinSendButton.prop('disabled', false);
       } else {
           $pinSendButton.prop('disabled', true);
       }
    }

});
