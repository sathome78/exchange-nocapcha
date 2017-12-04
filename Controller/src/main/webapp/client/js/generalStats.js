$(function () {
    const $datetimepickerStart = $('#datetimepicker_start');
    const $datetimepickerEnd = $('#datetimepicker_end');
    const datetimeFormat = 'YYYY-MM-DD HH:mm';


    $.datetimepicker.setDateFormatter({
        parseDate: function (date, format) {
            var d = moment(date, format);
            return d.isValid() ? d.toDate() : false;
        },

        formatDate: function (date, format) {
            return moment(date).format(format);
        }
    });

    $($datetimepickerStart).datetimepicker({
        format: datetimeFormat,
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang:'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });
    $($datetimepickerEnd).datetimepicker({
        format: datetimeFormat,
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang:'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });
    $($datetimepickerEnd).val(moment($($datetimepickerEnd).datetimepicker('getValue')).format(datetimeFormat));
    $($datetimepickerStart).val(moment($($datetimepickerEnd).datetimepicker('getValue')).subtract(1, 'days').format(datetimeFormat));
    refreshUsersNumber();

    $('#refresh-users').click(refreshUsersNumber);
    $('#download-currencies-report').click(getCurrenciesTurnover);
    $('#download-currency-pairs-report').click(getCurrencyPairsTurnover);




});

function refreshUsersNumber() {
    const fullUrl = '/2a8fy7b07dxe44/generalStats/newUsers?' + getTimeParams();
    $.get(fullUrl, function (data) {
        $('#new-users-quantity').text(data)
    })
}

function getCurrencyPairsTurnover() {
    const fullUrl = '/2a8fy7b07dxe44/generalStats/currencyPairTurnover?' + getTimeParams();
    $.get(fullUrl, function (data) {
        saveToDisk(data, 'currencyPairs.csv')
    })
}

function getCurrenciesTurnover() {
    const fullUrl = '/2a8fy7b07dxe44/generalStats/currencyTurnover?' + getTimeParams();
    $.get(fullUrl, function (data) {
        saveToDisk(data, 'currencies.csv')
    })

}

function getTimeParams() {
    return 'startTime=' +
        $('#datetimepicker_start').val().replace(' ', '_') + '&endTime=' +
        $('#datetimepicker_end').val().replace(' ', '_');
}



