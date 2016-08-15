$(function(){
    initLocaleSwitcher();
});

function initLocaleSwitcher() {
    $(".language").click(function (e) {
        e.preventDefault();
        var localeName = e.target.textContent.toUpperCase();
        var localeCode = 'ru';
        if (localeName == 'EN') {
            localeCode = 'en';
        } else if (localeName == 'RU') {
            localeCode = 'ru';
        } else if (localeName == 'CH') {
            localeCode = 'cn';
        } else if (localeName == 'TH') {
            localeCode = 'th';
        } else if (localeName == 'TH') {
            localeCode = 'th';
        } else if (localeName == 'ID') {
            localeCode = 'in';
        }

        var ref = '/dashboard/locale?locale=' + localeCode;
        $.get(ref)
            .always(function () {
                window.location.reload();
            });
    });
}
