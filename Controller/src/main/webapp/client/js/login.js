


$(function () {
    var $pinInput = $('#pin');
    var $pinSendButton = $('#send_pin');
    var $sendAgainButton = $('#send_pin_again');
    var $resultBlock = $('#send_pin_res');

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

    $sendAgainButton.on('click', function () {
        $.ajax('/login/new_pin_send', {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            data: $('#pin_code').serialize()
        }).success(function (response) {
            $resultBlock.text(response);
        }).fail(function (error, jqXHR, textStatus) {
           $resultBlock.text(error.responseText).css('color', 'red');
        });
    });

});