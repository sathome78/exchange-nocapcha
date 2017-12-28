
var $withdrawalPage;
var $addressesTable;
var addressesDataTable;
var transferRequestsBaseUrl;
var filterParams;

$(function () {


    $withdrawalPage = $('#addresses-admin');
    $addressesTable = $('#addressesTable');
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
        addressesDataTable = $addressesTable.DataTable();
        addressesDataTable.ajax.url(url).load();
    } else {
        addressesDataTable = $addressesTable.DataTable({
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
                    "data": "userEmail",
                    "name": "USER.email"
                },
                {
                    "data": "currencyName",
                    "name": "CU.name"
                },
                {
                    "data": "address",
                    "name": "RRA.address"
                },
                {
                    "data": "addressFieldName",
                    "name": "CU.name"
                },
                {
                    "data": "generationDate",
                    "name": "RRA.date_generation",
                    "className": "text-center"
                }
            ],
            "createdRow": function (row, data, index) {
            },
            "order": [[0, 'desc']]
        });
    }
}
