$(document).ready(function() {
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

        {
            //вариант c изменением url
            //window.location.href = window.location.href.split('?')[0] + '?locale='+localeCode;
        }
        {
            //вариант без изменения url
            var ref = '?locale=' + localeCode;
            $.get(ref, function () {
                window.location.reload();
            });
        }
    });
});
