
var $withdrawalPage;
var $voucherTable;
var withdrawalDataTable;
var transferRequestsBaseUrl;
var filterParams;

$(function () {


    $withdrawalPage = $('#addresses-admin');
    $voucherTable = $('#addressesTable');
    filterParams = '';
    transferRequestsBaseUrl = '/2a8fy7b07dxe44/refillAddresses/table?';
    $('#withdraw-requests-manual').addClass('active');


    updateAddressesTable();

    $('#filter-apply').on('click', function (e) {
        e.preventDefault();
        filterParams = $('#addresses-search-form').serialize();
        updateAddressesTable();
    });

    $('#filter-reset').on('click', function (e) {
        e.preventDefault();
        $('#addresses-search-form')[0].reset();
        filterParams = '';
        updateAddressesTable();
    });

});




function updateAddressesTable() {
    var filter = filterParams.length > 0 ? '&' + filterParams : '';
    var url = transferRequestsBaseUrl + filter;
    if ($.fn.dataTable.isDataTable('#addressesTable')) {
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
                    "data": "email",
                    "name": "TRANSFER_REQUEST.email",
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
