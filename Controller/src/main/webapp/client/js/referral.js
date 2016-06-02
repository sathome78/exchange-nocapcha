$(function () {

    $('.ref-button').click(function () {
        $.ajax('/generateReferral', {
            method: 'get'
        }).done(function (e) {
            $('.ref-reference').html(e['referral']);
        });
    });
});
