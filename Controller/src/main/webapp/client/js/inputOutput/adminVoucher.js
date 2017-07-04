var currentEmail;
var $withdrawalPage;
var $voucherTable;
var withdrawalDataTable;
var transferRequestsBaseUrl;
var filterParams;

$(function () {

    $.datetimepicker.setDateFormatter({
        parseDate: function (date, format) {
            var d = moment(date, format);
            return d.isValid() ? d.toDate() : false;
        },

        formatDate: function (date, format) {
            return moment(date).format(format);
        }
    });

    $('#filter-datetimepicker_start').datetimepicker({
        format: 'YYYY-MM-DD HH:mm',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang: 'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });
    $('#filter-datetimepicker_end').datetimepicker({
        format: 'YYYY-MM-DD HH:mm',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang: 'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });


    $withdrawalPage = $('#withdraw-requests-admin');
    $voucherTable = $('#voucherTable');
    filterParams = '';
    transferRequestsBaseUrl = '/2a8fy7b07dxe44/transfer/requests?';
    $('#withdraw-requests-manual').addClass('active');


    updateVoucherTable();

    $('#filter-apply').on('click', function (e) {
        e.preventDefault();
        filterParams = $('#withdrawal-request-search-form').serialize();
        updateVoucherTable();
    });

    $('#filter-reset').on('click', function (e) {
        e.preventDefault();
        $('#withdrawal-request-search-form')[0].reset();
        filterParams = '';
        updateVoucherTable();
    });

    $('#voucherTable').on('click', 'button[data-source=USER_TRANSFER].revoke_admin_button', function (e) {
        e.preventDefault();
        var id = $(this).data("id");
        var $modal = $("#confirm-with-info-modal");
        $modal.find("label[for=info-field]").html($(this).html());
        $modal.find("#info-field").val(id);
        $modal.find("#confirm-button").off("click").one("click", function () {
            $modal.modal('hide');
            $.ajax({
                url: '/2a8fy7b07dxe44/transfer/request/revoke?id=' + id,
                async: false,
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                type: 'POST',
                complete: function () {
                    updateVoucherTable();
                }
            });
        });
        $modal.modal();
    });

});

function getRowId($elem) {
    var rowData = retrieveRowDataForElement($elem);
    return rowData.transaction.id;
}

function viewRequestInfo($elem) {
    var rowData = retrieveRowDataForElement($elem);
    var $modal = $withdrawalPage.find('#withdraw-info-modal');
    fillModal($modal, rowData);
    $modal.modal();
}

function retrieveRowDataForElement($elem) {
    var $row = $($elem).parents('tr');
    return withdrawalDataTable.row($row).data();
}

function fillModal($modal, rowData) {
    $modal.find('#info-currency').text(rowData.currencyName);
    $modal.find('#info-amount').text(rowData.amount);
    $modal.find('#info-commissionAmount').text(rowData.commissionAmount);
    $modal.find('#info-status').text(rowData.status);
    $modal.find('#info-status-date').text(rowData.statusModificationDate);
    var recipientBank = rowData.recipientBankName ? rowData.recipientBankName : '';
    var recipientBankCode = rowData.recipientBankCode ? rowData.recipientBankCode : '';
    $modal.find('#info-bankRecipient').text(recipientBank + ' ' + recipientBankCode);
    $modal.find('#info-wallet').text(rowData.wallet);
    $modal.find('#info-destination-tag').text(rowData.destinationTag);
    var userFullName = rowData.userFullName ? rowData.userFullName : '';
    $modal.find('#info-userFullName').text(rowData.userFullName);
    $modal.find('#info-remark').find('textarea').html(rowData.remark);
}




function updateVoucherTable() {
    var filter = filterParams.length > 0 ? '&' + filterParams : '';
    var url = transferRequestsBaseUrl + filter;
    if ($.fn.dataTable.isDataTable('#voucherTable')) {
        withdrawalDataTable = $voucherTable.DataTable();
        withdrawalDataTable.ajax.url(url).load();
    } else {
        withdrawalDataTable = $voucherTable.DataTable({
            "ajax": {
                "url": url,
                "dataSrc": "data"
            },
            "serverSide": true,
            "paging": true,
            "info": true,
            "bFilter": true,
            "columns": [
                {
                    "data": "id",
                    "name": "TRANSFER_REQUEST.id",
                    "render": function (data) {
                        return '<button class="request_id_button" onclick="viewRequestInfo(this)">' + data + '</button>';
                    }
                },
                {
                    "data": "dateCreation",
                    "name": "TRANSFER_REQUEST.date_creation",
                    "render": function (data) {
                        return data.replace(' ', '<br/>');
                    },
                    "className": "text-center"
                },
                {
                    "data": "userId",
                    "name": "email",
                    "render": function (data, type, row) {
                        return '<a data-userEmail="' + row.creatorEmail + '" href="/2a8fy7b07dxe44/userInfo?id=' + data + '">' + row.creatorEmail + '</a>'
                    }
                },
                {
                    "data": "netAmount",
                    "name": "TRANSFER_REQUEST.amount"
                },
                {
                    "data": "currencyName",
                    "name": "currency"
                },

                {
                    "data": "commissionAmount",
                    "name": "TRANSFER_REQUEST.commission"
                },
                {
                    "data": "merchantName",
                    "name": "merchant_name",
                    "render": function (data, type, row) {
                        var merchantName = data;
                        var merchantImageName = '';
                        if (row.merchantImage && row.merchantImage.image_name != merchantName) {
                            merchantImageName = ' ' + row.merchantImage.image_name;
                        }
                        return merchantName + merchantImageName;
                    }
                },
                {
                    "data": "status",
                    "name": "TRANSFER_REQUEST.status_id"
                },
                {
                    "data": "recipientEmail",
                    "name": "recipient_email"
                },
                {
                    "data": "",
                    "name": "",
                    "render": function (data, type, row) {
                        if (data && row.isEndStatus) {
                            return '';
                        } else {
                            return getButtonsSet(row.id, row.sourceType, row.merchantName,
                                    row.buttons, "voucherTable");
                        }
                    },
                    "className": "text-center"
                }
            ],
            "createdRow": function (row, data, index) {
            },
            "order": [[0, 'desc']]
        });
    }
}
