/**
 * Created by Valk on 25.03.16.
 */
$(document).ready(function () {

        +function () {
            var names = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
            var nameQuery = names.filter(function (elem) {
                return (elem.split('=')[0] == 'name');
            })[0];
            var nameValue = '';
            if (nameQuery) {
                nameValue = nameQuery.split('=')[1];
                nameValue = decodeURIComponent(nameValue);
            }

            //установить значения по умолчанию - первый в спсике
            $('.exchange__pair').removeClass('active').removeAttr('selected').first().addClass('active').attr('selected', '');
            $('.exchange__pair a').removeClass('active').first().addClass('active');

            var found = false;
            /*для заполнения списков необходимо, чтобы в нижнюю часть ('другие пары') попадали пары за исключением попавших в верхнюю часть
             found убрать, когда будет так работать*/ //TODO

            $('.exchange__pair').each(function (idx, element) {
                if (element.childNodes[0].innerHTML == nameValue) {
                    if (!found) {
                        //удаляем установленное по умолчанию
                        $('.exchange__pair').removeClass('active').removeAttr('selected');
                        $('.exchange__pair a').removeClass('active');
                        //устанавливаем на нужном
                        element.setAttribute('selected', '');
                        if ($(element).parent().parent().find('.exchange').length != 0) {
                            $(element).addClass('active');
                            $(element).children().addClass('active');
                        }
                        found = true;
                    }
                }
            });
        }();


        $('.exchange__pair').on('click', function () {
            $('.exchange__pair').removeClass('active');
            $(this).addClass('active');
        })


    }
);
