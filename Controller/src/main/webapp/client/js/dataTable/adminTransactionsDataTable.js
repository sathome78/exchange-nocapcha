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
                            return '<button id="transactionlist-delete-order-button" onclick=getOrderDetailedInfo(' + data.id + ')>' + data.id + '</button>';
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
    }
    transactionsDataTable.columns().every( function () {
        var that = this;
        $( 'input', this.footer() ).on( 'keyup change', function () {
            if ( that.search() !== this.value ) {
                that
                    .search( this.value )
                    .draw();
            }
        } );
    } );

});

