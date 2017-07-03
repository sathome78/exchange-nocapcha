


$(function () {
    var $loginForm = $('#login_block');
    var $pinForm = $('#pin_block');
    var $pinInput = $('#pin');
    var $pinSendButton = $('#send_pin');

    if (window.location.href.indexOf('?pin') > 0) {
        $pinForm.show();
        $loginForm.hide();
    } else  {
        $loginForm.show();
        $pinForm.hide();
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
