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
                    "data": "totalInput",
                    "render": function (data, type, row) {
                        return formatDecimalValue(data);
                    }
                },
                {
                    "data": "totalSell",
                    "render": function (data, type, row) {
                        return formatDecimalValue(data);
                    }
                },
                {
                    "data": "totalBuy",
                    "render": function (data, type, row) {
                        return formatDecimalValue(data);
                    }
                },
                {
                    "data": "totalOutput",
                    "render": function (data, type, row) {
                        return formatDecimalValue(data);
                    }
                },
                {
                    "data": "reserveOrders",
                    "render": function (data, type, row) {
                        return formatDecimalValue(data);
                    }
                },
                {
                    "data": "reserveWithdraw",
                    "render": function (data, type, row) {
                        return formatDecimalValue(data);
                    }
                },
                {
                    "data": "activeBalance",
                    "render": function (data, type, row) {
                        return formatDecimalValue(data);
                    }
                },
                {
                    "data": "reservedBalance",
                    "render": function (data, type, row) {
                        return formatDecimalValue(data);
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

function formatDecimalValue(val) {
    return numbro(val).format('0.00[000000]')
}