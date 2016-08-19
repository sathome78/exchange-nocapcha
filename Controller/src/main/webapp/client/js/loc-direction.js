/**
 * Created by OLEG on 19.08.2016.
 */
$(function () {
    if ($('#language').text().trim() === 'AR') {
        console.log($('body').css('direction'));
        $('body').css('direction', 'rtl');
        console.log($('body').css('direction'));
    }
});