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
                    template: '<div class="noty_message"><div class="noty_header"><button type="button" class="close" aria-label="Close">' +
                    '<span aria-hidden="true">&times;</span></button></div><br/><span class="noty_text"></span><div class="noty_close"></div></div>',
                    type: 'error',
                    layout: 'center',
                    modal: true,
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
        template: '<div class="noty_message"><div class="noty_header"><button type="button" class="close" aria-label="Close">' +
        '<span aria-hidden="true">&times;</span></button></div><br/><span class="noty_text"></span><div class="noty_close"></div></div>',
        type: 'success',
        layout: 'center',
        modal: true,
        timeout: false
    });
}

function failNoty(jqXHR) {
    //closeNote();
    var errorInfo = $.parseJSON(jqXHR.responseText);
    if (!errorInfo.detail) return;
    failedNote = noty({
        text: errorInfo.detail,
        template: '<div class="noty_message"><div class="noty_header"><button type="button" class="close" aria-label="Close">' +
        '<span aria-hidden="true">&times;</span></button></div><br/><span class="noty_text"></span><div class="noty_close"></div></div>',
        type: 'error',
        layout: 'center',
        modal: true,
        timeout: false
    });
}

