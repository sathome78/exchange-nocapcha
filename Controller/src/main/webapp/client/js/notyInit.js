/**
 * Created by Valk on 04.04.16.
 */

$(document).ajaxError(function (event, jqXHR, options, jsExc) {
    failNoty(jqXHR);
});

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
    closeNote();
    successNote = noty({
        text: text,
        type: 'success',
        layout: 'bottomRight',
        timeout: false
    });
}

function failNoty(jqXHR) {
    closeNote();
    var errorInfo = $.parseJSON(jqXHR.responseText);
    failedNote = noty({
        text: errorInfo.detail,
        type: 'error',
        layout: 'bottomRight',
        timeout: false
    });
}

//Show error message on page load - if massage was passed to page
+function showErrorNotyOnEntry() {
    var msg = $('#errorNoty').html();
    if (msg) {
        failedNote = noty({
            text: msg,
            type: 'error',
            layout: 'bottomRight',
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