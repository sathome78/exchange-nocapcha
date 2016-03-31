/**
 * Created by Valk on 30.03.16.
 */
//for reCAPCHA on HEAD
function onloadCallbackHead() {
    grecaptcha.render('cpch-head-field', {
        'sitekey': '6LfPFRwTAAAAAO86BgguULebb3tXZbur5ccLCvPX',
        'size': 'compact',
        'callback': capResultCheckHead,
        'expired-callback': capExpiredHead
    });
    $('#cpch-head-field').removeClass('passed');
    //$('#register_button').prop('disabled', true);
}

function capResultCheckHead(response) {
    //$('#register_button').prop('disabled', false);
    $('#cpch-head-field').addClass('passed');
    //$('.cpch-error-message').html('');
}

function capExpiredHead() {
    $('#cpch-head-field').removeClass('passed');
    //$('#register_button').prop('disabled', true);
}

