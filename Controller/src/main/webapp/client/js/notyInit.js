/**
 * Created by Valk on 04.04.16.
 */

const SUCCESS_NOTY_NAME = "successNoty";
const ERROR_NOTY_NAME = "errorNoty";

$(function () {
        $(document).ajaxError(function (event, jqXHR, options, jsExc) {
            failNoty(jqXHR);
        });

        //Show error message on page load - if massage was passed to page
        +function showErrorNotyOnEntry() {
            var msg = $('#errorNoty').html();
            if (!msg) {
                msg = getParameterByName('errorNoty');
            }
            if (!msg){
                msg = errorFromCookie();
                deleteCookie(ERROR_NOTY_NAME);
            }
            if (msg) {
                failedNote = noty({
                    text: msg,
                    template: '<div class="noty_message"><div class="noty_header"><button type="button" class="close" aria-label="Close">' +
                    '<span aria-hidden="true">&times;</span></button></div><br/><span class="noty_text"></span><div class="noty_close"></div></div>',
                    type: 'error',
                    layout: 'topCenter',
                    modal: true,
                    timeout: false
                });
            }
        }();

        //Show success message on page load - if massage was passed to page
        +function showSuccessNotyOnEntry() {
            var msg = $('#successNoty').html();
            if (!msg) {
                msg = getParameterByName(SUCCESS_NOTY_NAME);
            }
            if (!msg){
                msg = successFromCookie();
                deleteCookie(SUCCESS_NOTY_NAME);
            }
            if (msg) {
                successNoty(msg);
            }
        }();

    }
);

var failedNote;
var successNote;

function getParameterByName(paramName) {
    var url = window.location.href;
    paramName = paramName.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + paramName + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

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
    if (!text) {
        var msgFromCookie = successFromCookie();
        deleteCookie(SUCCESS_NOTY_NAME);
        text = msgFromCookie;
    }
    successNote = noty({
        text: text,
        template: '<div class="noty_message"><div class="noty_header"><button type="button" class="close" aria-label="Close">' +
        '<span aria-hidden="true">&times;</span></button></div><br/><span class="noty_text"></span><div class="noty_close"></div></div>',
        type: 'success',
        layout: 'topCenter',
        modal: true,
        timeout: false
    });
}

function failNoty(jqXHR) {
    var notyMessage = getErrorMessage(jqXHR);
    if (notyMessage.length > 0) {
        failedNote = noty({
            text: notyMessage,
            template: '<div class="noty_message"><div class="noty_header"><button type="button" class="close" aria-label="Close">' +
            '<span aria-hidden="true">&times;</span></button></div><br/><span class="noty_text"></span><div class="noty_close"></div></div>',
            type: 'error',
            layout: 'topCenter',
            modal: true,
            timeout: false
        });
    }
}

function errorNoty(text) {
    failedNote = noty({
        text: text,
        template: '<div class="noty_message"><div class="noty_header"><button type="button" class="close" aria-label="Close">' +
        '<span aria-hidden="true">&times;</span></button></div><br/><span class="noty_text"></span><div class="noty_close"></div></div>',
        type: 'error',
        layout: 'topCenter',
        modal: true,
        timeout: false
    });
}

function errorInCookie(message){
    $.cookie(ERROR_NOTY_NAME, message, { path: '/' });
}

function successInCookie(message){
    $.cookie(SUCCESS_NOTY_NAME, message, { path: '/' });
}

function errorFromCookie(){
    return $.cookie(ERROR_NOTY_NAME);
}

function successFromCookie(){
    return $.cookie(SUCCESS_NOTY_NAME);
}

function deleteCookie(name) {
    $.removeCookie(name, { path: '/' });
}

function getErrorMessage(jqXHR) {
    var errorInfo = $.parseJSON(jqXHR.responseText);
    var notyMessage = errorInfo.cause;
    var detail = errorInfo.detail ? errorInfo.detail : errorInfo.error;
    if (!detail && !notyMessage) {
        notyMessage = "";
    } else {
        notyMessage = notyMessage + "</br>" + detail;
    }
    return notyMessage;
}

