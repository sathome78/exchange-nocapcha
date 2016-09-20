var transactionsDataTable;

$(function () {
    $('#transactionsTable tfoot th').each( function () {
        var title = $(this).text();
        $(this).html( '<input type="text" style="width: 100%" placeholder="Search" />' );
    } );



    if ($.fn.dataTable.isDataTable('#transactionsTable')) {
        transactionsDataTable = $('#transactionsTable').DataTable();
    } else {
        var id = $("#user-id").val();
        transactionsDataTable = $('#transactionsTable').DataTable({
            "serverSide": true,
            "ajax": {
                "url": '/admin/transactions?id=' + id,
                "dataSrc": "data"
            },
            "paging": true,
            "info": true,
            "columns": [
                {
                    "data": "datetime",
                    "render": function (data, type, row) {
                        if (type == 'display') {
                            return data.split(' ')[0];
                        }
                        return data;
                    }
                },
                {
                    "data": "datetime",
                    "render": function (data, type, row) {
                        if (type == 'display') {
                            return data.split(' ')[1];
                        }
                        return data;
                    }
                },
                {
                    "data": "operationType"
                },
                {
                    "data": "status"
                },
                {
                    "data": "currency"
                },
                {
                    "data": "amount"
                },
                {
                    "data": "commissionAmount"
                },
                {
                    "data": "merchant.description"
                },
                {
                    "data": "order",
                    "render": function (data, type, row) {
                        if (data && data.id > 0) {
                            return '<button class="transactionlist-delete-order-button">' + data.id + '</button>';
                        } else {
                            return '';
                        }
                    }
                }
            ],
            "order": [
                [
                    0,
                    "asc"
                ]
            ],
            "searchDelay": 1000
        });
        $('#transactionsTable tbody').on('click', '.transactionlist-delete-order-button', function () {
            var currentRow = transactionsDataTable.row($(this).parents('tr'));
            getOrderDetailedInfo(currentRow, currentRow.data().order.id, false);
        });
    }




});

