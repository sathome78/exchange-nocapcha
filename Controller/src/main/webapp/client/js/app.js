/**
 * Created by Valk on 20.06.2016.
 */

function syncTableParams(tableId, limit, callback) {
    var url = '/dashboard/tableParams/' + tableId + '?';
    /*if parameter is empty, in response will be retrieved current value is set or default if non*/
    url = url + (limit ? '&limitValue=' + limit : '');
    $.ajax({
        url: url,
        type: 'GET',
        success: function (data) {
            if (callback) {
                callback(data);
            }
        }
    });
}

function blink($element) {
    $element.addClass('blink');
    setTimeout(function () {
        $element.removeClass('blink');
    }, 250);
}