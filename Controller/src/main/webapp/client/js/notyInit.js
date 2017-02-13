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
            if (!msg) {
                msg = getParameterByName('errorNoty');
            }
            if (!msg){
                msg = errorFromCookie();
                deleteCookie("errorNoty");
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
                msg = getParameterByName('successNoty');
            }
            if (!msg){
                msg = successFromCookie();
                deleteCookie("successNoty");
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
    $.cookie("errorNoty", message, { path: '/' });
}

function successInCookie(message){
    $.cookie("successNoty", message, { path: '/' });
}

function errorFromCookie(){
    return $.cookie("errorNoty");
}

function successFromCookie(){
    return $.cookie("successNoty");
}

function deleteCookie(name) {
    $.removeCookie(name, { path: '/' });
}

function getErrorMessage(jqXHR) {
    var errorInfo = $.parseJSON(jqXHR.responseText);
    var notyMessage = errorInfo.cause;
    var datail = errorInfo.detail ? errorInfo.detail : errorInfo.error;
    if (!datail && !notyMessage) {
        notyMessage = "Unknown error !";
    } else {
        notyMessage = notyMessage + "</br>" + datail;
    }
    return notyMessage;
}

