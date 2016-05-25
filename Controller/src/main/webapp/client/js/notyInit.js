/**
 * Created by Valk on 04.04.16.
 */

$(function () {
        $(document).ajaxError(function (event, jqXHR, options, jsExc) {
            failNoty(jqXHR);
        });

        //Show error message on page load - if massage was passed to page
        +function showErrorNotyOnEntry() {
            var msg = $('#errorNoty').html();
            if (msg) {
                failedNote = noty({
                    text: msg,
                    type: 'error',
                    layout: 'bottomLeft',
                    timeout: false
                });
            }
        }();

        //Show success message on page load - if massage was passed to page
        +function showSuccessNotyOnEntry() {
            var msg = $('#successNoty').html();
            if (msg) {
                successNoty(msg);
            }
        }();


        $('.order-noty').hover(
            showOrderNoty,
            closeOrderNoty)
    }
);

var failedNote;
var successNote;

function closeNote() {
    if (failedNote) {
        failedNote.close();
        failedNote = undefined;
    }
    if (successNote) {
        successNote.close();
        successNote = undefined;
    }
}

function successNoty(text) {
    //closeNote();
    successNote = noty({
        text: text,
        type: 'success',
        layout: 'bottomLeft',
        timeout: false
    });
}

function failNoty(jqXHR) {
    //closeNote();
    var errorInfo = $.parseJSON(jqXHR.responseText);
    failedNote = noty({
        text: errorInfo.detail,
        type: 'error',
        layout: 'bottomLeft',
        timeout: false
    });
}

var orderNoty;

function showOrderNoty() {
    if (! $(this).text().trim()) {
        return;
    }
    if (orderNoty) {
        //orderNoty.close();
        orderNoty = undefined;
    }
    var operationType = $(this).find('#operationType').val();
    var amountBase = $(this).find('#amountBase').val();
    var exRate = $(this).find('#exRate').val();
    var amountConvert = $(this).find('#amountConvert').val();
    var dateCreation = $(this).find('#dateCreation').val();
    var dateAcception = $(this).find('#dateAcception').val();
    var text = 'TYPE: ' + '<b>' + operationType + '</b><br/>' +
        'AMOUNT: ' + '<b>' + amountBase + '</b><br/>' +
        'EXRATE: ' + '<b>' + exRate + '</b><br/>' +
        'TOTAL: ' + '<b>' + amountConvert + '</b><br/>' +
        'CREATED: ' + '<b>' + dateCreation + '</b><br/>' +
        'ACCEPTED: ' + '<b>' + dateAcception + '</b><br/>';
    orderNoty = noty({
        text: text,
        type: 'info',
        layout: 'bottomLeft',
        timeout: false
    });
}

function closeOrderNoty() {
    if (orderNoty) {
        orderNoty.close();
        orderNoty = undefined;
    }
}