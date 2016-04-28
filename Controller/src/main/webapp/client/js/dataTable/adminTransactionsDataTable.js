var transactionsDataTable;

$(function () {
    if ($.fn.dataTable.isDataTable('#transactionsTable')) {
        transactionsDataTable = $('#transactionsTable').DataTable();
    } else {
        var id = $("#user-id").val();
        transactionsDataTable = $('#transactionsTable').DataTable({
            "ajax": {
                "url": '/admin/transactions?id=' + id,
                "dataSrc": ""
            },
            "paging": true,
            "info": true,
            "columns": [
                {
                    "data": "datetime",
                    "render": function (data, type, row) {
                        if (type == 'display') {
                            return data.split('T')[0];
                        }
                        return data;
                    }
                },
                {
                    "data": "datetime",
                    "render": function (data, type, row) {
                        if (type == 'display') {
                            return data.split('T')[1];
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
                    "data": "order.id"
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