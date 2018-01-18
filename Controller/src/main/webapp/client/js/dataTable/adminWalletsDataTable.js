var walletsDataTable;

$.fn.dataTable.ext.search.push(
    function( settings, data, dataIndex ) {
        var excludeZeroes = $('#exclude-zero-balances').prop('checked');
        var activeBalance = parseFloat(data[7]) || 0;
        var reservedBalance = parseFloat(data[8]) || 0;

        if (excludeZeroes && activeBalance === 0.0 && reservedBalance === 0.0) {
            return false;
        }
        return true;
    }
);


$(function () {
    if ($.fn.dataTable.isDataTable('#walletsTable')) {
        walletsDataTable = $('#walletsTable').DataTable();
    } else {
        var id = $("#user-id").val();
        walletsDataTable = $('#walletsTable').DataTable({
            /*"bFilter": false,*/
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
                /*{
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
                },*/
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
            ],
            dom: "t"
        });
    }
    $('#walletsTable').find('tbody').on('click', 'tr', function () {
        var currentRow = walletsDataTable.row( this );
        var currentData = currentRow.data();
        window.location = "/2a8fy7b07dxe44/userStatements/" + currentData.id;
    });

    $('#exclude-zero-balances').change(function() {
        walletsDataTable.draw();
    });
});

function formatDecimalValue(val) {
    return numbro(val).format('0.00[000000]')
}