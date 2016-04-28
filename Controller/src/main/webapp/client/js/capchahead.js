/**
 * Created by Valk on 30.03.16.
 */
//for reCAPCHA on HEAD
function onloadCallbackHead() {
    var key = $('#cpch-head-field').attr('data-sitekey');
    grecaptcha.render('cpch-head-field', {
        'sitekey': key,
        'size': 'compact',
        'callback': capResultCheckHead,
        'expired-callback': capExpiredHead
    });
    $('#cpch-head-field').removeClass('passed');
}

function capResultCheckHead(response) {
    $('#cpch-head-field').addClass('passed');
}

function capExpiredHead() {
    $('#cpch-head-field').removeClass('passed');
}

