var walletsDataTable;

$(function () {
    if ($.fn.dataTable.isDataTable('#walletsTable')) {
        walletsDataTable = $('#walletsTable').DataTable();
    } else {
        var id = $("#user-id").val();
        walletsDataTable = $('#walletsTable').DataTable({
            "bFilter": false,
            "paging": false,
            "order": [],
            "bLengthChange": false,
            "bPaginate": false,
            "bInfo": false,
            "ajax": {
                "url": '/2a8fy7b07dxe44/wallets?id=' + id,
                "dataSrc": ""
            },
            /*"paging": true,*/
            "info": true,
            "columns": [
                {
                    "data": "name"
                },
                {
                    "data": "activeBalance",
                    "render": function (data, type, row) {
                        return row.activeBalanceFormatted;
                    }
                },
                {
                    "data": "reservedBalance",
                    "render": function (data, type, row) {
                        return row.reservedBalanceFormatted;
                    }
                }
            ]
        });
    }
    $('#walletsTable').find('tbody').on('click', 'tr', function () {
        var currentRow = walletsDataTable.row( this );
        var currentData = currentRow.data();
        window.location = "/2a8fy7b07dxe44/userStatements/" + currentData.id;
    })
});