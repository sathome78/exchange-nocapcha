$(document).ready(function () {
    $(".lang__item").click(function (e) {
        var localeName = e.target.textContent;
        var localeCode = 'ru';
        if (localeName == 'English') {
            localeCode = 'en';
        } else if (localeName == 'Русский') {
            localeCode = 'ru';
        } else if (localeName == 'Chinese') {
            localeCode = 'cn';
        }
        var ref = '?locale=' + localeCode;
        $.get(ref)
            .always(function () {
            window.location.reload();
        });
    });
});
