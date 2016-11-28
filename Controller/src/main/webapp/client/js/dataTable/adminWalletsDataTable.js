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
    $('#walletsTable').find('tbody').on('click', 'tr', function () {
        var currentRow = walletsDataTable.row( this );
        var currentData = currentRow.data();
        console.log(currentRow);
        console.log(currentData);
        window.location = "/admin/userStatements/" + currentData.id;
    })
});