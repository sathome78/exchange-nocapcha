/**
 * Created by Valk on 04.04.16.
 */

$(document).ajaxError(function (event, jqXHR, options, jsExc) {
    failNoty(jqXHR);
});

var failedNote;

function closeNote() {
    if (failedNote) {
        failedNote.close();
        failedNote = undefined;
    }
}

//for future
function successNoty(text) {
    closeNote();
    noty({
        text: text,
        type: 'success',
        layout: 'bottomRight',
        timeout: true
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

//for future
function showErrorNotyOnEntry(){
    var msg = $('#errorNoty').html();
    if (msg){
        failedNote = noty({
            text: msg,
            type: 'error',
            layout: 'bottomRight',
            timeout: false
        });
    }
} //();