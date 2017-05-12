/**
 * Created by Valk on 20.06.2016.
 */

/**/
/*it's need to distinguish different windows (tab) of the browser*/
var windowId = Math.floor((Math.random()) * 10000).toString(36) + Math.floor((Math.random()) * 10000).toString(36);
/*it's need to prevent ajax request if window (tab) is not active*/
var windowIsActive = true;
/*for testing*/
var REFRESH_INTERVAL_MULTIPLIER = 1;
/**/
window.onblur = function () {
    windowIsActive = false;
};
window.onfocus = function () {
    windowIsActive = true;
};

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

function blink_green($element) {
    $element.addClass('blink_green');
    setTimeout(function () {
        $element.removeClass('blink_green');
    }, 250);
}

function getButtonsSet(id, sourceType, merchant, buttonDataList, tableIdFor) {
    var buttonsSet = '';
    buttonDataList.forEach(function (e) {
        if (e.tableIdListOnly.indexOf(tableIdFor) > -1
        ) {
            buttonsSet = buttonsSet +
                '<button data-id = ' + id +
                '        data-source = ' + sourceType +
                '        data-merchant= ' + merchant +
                '        style="font-size: 1.1rem;" ' +
                '        class="action-button table-button-block__button btn ' + e.buttonId + '">' +
                e.buttonTitle +
                '</button>&nbsp;';
        }
    });
    return buttonsSet;
}