/**
 * Created by Valk on 30.03.16.
 */

//for reCAPCHA on "big" login and register forms
function onloadCallback() {
    grecaptcha.render('cpch-field', {
        'sitekey': '6LfPFRwTAAAAAO86BgguULebb3tXZbur5ccLCvPX',
        'size': 'normal',
        'callback': capResultCheck,
        'expired-callback': capExpired
    });
    $('#cpch-field').removeClass('passed');
    $('#register_button').prop('disabled', true);
}

function capResultCheck(response) {
    $('#register_button').prop('disabled', false);
    $('#cpch-field').addClass('passed');
    $('.cpch-error-message').html('');
}

function capExpired() {
    $('#cpch-field').removeClass('passed');
    $('#register_button').prop('disabled', true);
}
