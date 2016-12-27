/**
 * Created by Valk on 05.05.2016.
 */

var currentId;

$(function () {
    $('#upload-users-wallets').on('click', downloadUsersWalletsSummaryDatepiker);
});

$(function () {
    $('#upload-users-wallets-orders').on('click', downloadUsersWalletsSummaryDatepiker);
});

$(function () {
    $('#upload-users-wallets-inout').on('click', downloadUsersWalletsSummaryDatepiker);
});

function downloadUsersWalletsSummaryDatepiker() {
    currentId = this.id;
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
            var data = "startDate="+objArr[0].value+' 00:00:00'+'&'+"endDate="+objArr[1].value+' 23:59:59';
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
                            link.download = "downloadUsersWalletsSummary.csv";
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
                            link.download = "downloadUsersWalletsSummaryInOut.csv";
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
                            link.download = "downloadUsersWalletsTOTALSummaryInOut.csv";
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
                            link.download = "downloadUsersSummaryOrders.csv";
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


