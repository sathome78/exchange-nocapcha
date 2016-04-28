var walletsDataTable;

$(function () {
    if ($.fn.dataTable.isDataTable('#walletsTable')) {
        walletsDataTable = $('#walletsTable').DataTable();
    } else {
        var id = $("#user-id").val();
        walletsDataTable = $('#walletsTable').DataTable({
            "ajax": {
                "url": '/admin/wallets?id=' + id,
                "dataSrc": ""
            },
            "paging": true,
            "info": true,
            "columns": [
                {
                    "data": "name"
                },
                {
                    "data": "activeBalance"
                },
                {
                    "data": "reservedBalance"
                }
            ],
            "order": [
                [
                    0,
                    "asc"
                ]
            ]
        });
    }
});