var walletsDataTable;

$(function () {
    if ($.fn.dataTable.isDataTable('#walletsTable')) {
        walletsDataTable = $('#walletsTable').DataTable();
    } else {
        var id;
        window.location.search.substring(1).split('&')
            .some(function (e, i) {
                var result;
                if (result = (e.split('=')[0] == 'id')) {
                    id = e.split('=')[1];
                }
                return result;
            });
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