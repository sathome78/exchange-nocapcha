/**
 * Created by Valk on 05.05.2016.
 */

var currentId;
var currentRole;

function uploadUserWallets(role) {
    currentRole = role;
    currentId = 'upload-users-wallets';
    downloadUsersWalletsSummaryDatepiker();
}

function uploadUserWalletsInOut(role) {
    currentRole = role;
    currentId = 'upload-users-wallets-inout';
    downloadUsersWalletsSummaryDatepiker();
}

function uploadUserWalletsOrders(role) {
    currentRole = role;
    currentId = 'upload-users-wallets-orders';
    downloadUsersWalletsSummaryDatepiker();
}

function uploadUserWalletsOrdersByCurrencyPairs(role) {
    currentRole = role;
    currentId = 'upload-users-wallets-orders-by-currency-pairs';
    downloadUsersWalletsSummaryDatepiker();
}

function uploadInputOutputSummaryReport(role) {
    currentRole = role;
    currentId = 'downloadInputOutputSummaryReport';
    showDialog();
}

function downloadUsersWalletsSummaryDatepiker() {
    $('#order-delete-modal--date-picker').modal();
}

function downloadUsersWalletsSummary() {
    var isError = false;
    $('.input-block-wrapper__error-wrapper').toggle(false);
    if (!$('#startDate').val().match(/\d{4}\-\d{2}\-\d{2}/)){
        $('.input-block-wrapper__error-wrapper[for=startDate]').toggle(true);
        isError = true;
    }
    if (!$('#endDate').val().match(/\d{4}\-\d{2}\-\d{2}/)){
        $('.input-block-wrapper__error-wrapper[for=endDate]').toggle(true);
        isError = true;
    }
    if (isError) {
        return;
    }

        $('#order-delete-modal--date-picker').one('hidden.bs.modal', function (e) {
            var objArr = $('#datepicker__form').serializeArray();
            var data = "startDate="+objArr[0].value+' 00:00:00'+'&'+"endDate="+objArr[1].value+' 23:59:59' + "&role="+currentRole;
            if (currentId == 'upload-users-wallets'){
                $.ajax({
                        url: '/2a8fy7b07dxe44/downloadUsersWalletsSummary',
                        type: 'GET',
                        data: data,
                        success: function (data) {
                            /* not works in FF
                             $('<a href="data:text/plain,%EF%BB%BF' + encodeURIComponent(data) + '" download="downloadUsersWalletsSummary.csv"/a>')[0].click();*/
                            var link = document.createElement('a');
                            link.href = "data:text/plain;charset=utf-8,%EF%BB%BF" + encodeURIComponent(data);
                            link.download = "downloadUsersWalletsSummary_" + currentRole + ".csv";
                            var e = document.createEvent('MouseEvents');
                            e.initEvent('click', true, true);
                            link.dispatchEvent(e);
                        }
                    }
                );
            }
            if (currentId == 'upload-users-wallets-inout'){
                $.ajax({
                        url: '/2a8fy7b07dxe44/downloadUsersWalletsSummaryInOut',
                        type: 'GET',
                        data: data,
                        success: function (data) {
                            var link = document.createElement('a');
                            link.href = "data:text/plain;charset=utf-8,%EF%BB%BF" + encodeURIComponent(data);
                            link.download = "downloadUsersWalletsSummaryInOut_" + currentRole + ".csv";
                            var e = document.createEvent('MouseEvents');
                            e.initEvent('click', true, true);
                            link.dispatchEvent(e);
                        }
                    }
                );
                $.ajax({
                        url: '/2a8fy7b07dxe44/downloadUsersWalletsSummaryTotalInOut',
                        type: 'GET',
                        data: data,
                        success: function (data) {
                            var link = document.createElement('a');
                            link.href = "data:text/plain;charset=utf-8,%EF%BB%BF" + encodeURIComponent(data);
                            link.download = "downloadUsersWalletsTOTALSummaryInOut_" + currentRole + ".csv";
                            var e = document.createEvent('MouseEvents');
                            e.initEvent('click', true, true);
                            link.dispatchEvent(e);
                        }
                    }
                );
            }
            if (currentId == 'upload-users-wallets-orders') {
                $.ajax({
                        url: '/2a8fy7b07dxe44/downloadUserSummaryOrders',
                        type: 'GET',
                        data: data,
                        success: function (data) {
                            var link = document.createElement('a');
                            link.href = "data:text/plain;charset=utf-8,%EF%BB%BF" + encodeURIComponent(data);
                            link.download = "downloadUsersSummaryOrders_" + currentRole + ".csv";
                            var e = document.createEvent('MouseEvents');
                            e.initEvent('click', true, true);
                            link.dispatchEvent(e);
                        }
                    }
                );
            }
            if (currentId == 'upload-users-wallets-orders-by-currency-pairs') {
                $.ajax({
                        url: '/2a8fy7b07dxe44/downloadUserSummaryOrdersByCurrencyPairs',
                        type: 'GET',
                        data: data,
                        success: function (data) {
                            var link = document.createElement('a');
                            link.href = "data:text/plain;charset=utf-8,%EF%BB%BF" + encodeURIComponent(data);
                            link.download = "downloadUsersSummaryOrdersByCurrencyPairs_" + currentRole + ".csv";
                            var e = document.createEvent('MouseEvents');
                            e.initEvent('click', true, true);
                            link.dispatchEvent(e);
                        }
                    }
                );
            }
            });

    $('#order-delete-modal--date-picker').modal('hide');
}

function showDialog(){
    var $dialog = $('#report-dialog-currency-date-direction-dialog');
    $dialog.modal();
}

function makeReport() {
    var $dialog = $('#report-dialog-currency-date-direction-dialog');
    var $form = $dialog.find('form');
    if (!isDatesValid($form)){
        return;
    }
    $dialog.one('hidden.bs.modal', function (e) {
        var objArr = $form.serializeArray();
        var data = "startDate="+objArr[0].value+' 00:00:00'+
            '&'+"endDate="+objArr[1].value+' 23:59:59' +
            '&'+"currencyList="+objArr[2].value +
            '&'+"direction="+objArr[3].value +
            "&role="+currentRole;
        if (currentId == 'downloadInputOutputSummaryReport') {
            $.ajax({
                    url: '/2a8fy7b07dxe44/downloadInputOutputSummaryReport',
                    type: 'GET',
                    data: data,
                    success: function (data) {
                        var link = document.createElement('a');
                        link.href = "data:text/plain;charset=utf-8,%EF%BB%BF" + encodeURIComponent(data);
                        link.download = "downloadUsersSummaryOrders_" + currentRole + ".csv";
                        var e = document.createEvent('MouseEvents');
                        e.initEvent('click', true, true);
                        link.dispatchEvent(e);
                    }
                }
            );
        }

    });

    $dialog.modal('hide');
}

function isDatesValid($form){
    var $startDateErrorWrapper= $form.find('.input-block-wrapper__error-wrapper[for=start-date]');
    var $endDateErrorWrapper= $form.find('.input-block-wrapper__error-wrapper[for=end-date]');
    $startDateErrorWrapper.toggle(false);
    $endDateErrorWrapper.toggle(false);
    var $startDatePiker = $form.find('#start-date');
    var $endDatePiker = $form.find('#end-date');
    var isError = false;
    if (!$startDatePiker.val().match(/\d{4}\-\d{2}\-\d{2}/)){
        $startDateErrorWrapper.toggle(true);
        isError = true;
    }
    if (!$endDatePiker.val().match(/\d{4}\-\d{2}\-\d{2}/)){
        $endDateErrorWrapper.toggle(true);
        isError = true;
    }
    return !isError;
}


