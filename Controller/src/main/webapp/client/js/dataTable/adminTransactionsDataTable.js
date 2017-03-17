var transactionsDataTable;

$(function () {


    $.datetimepicker.setDateFormatter({
        parseDate: function (date, format) {
            var d = moment(date, format);
            return d.isValid() ? d.toDate() : false;
        },

        formatDate: function (date, format) {
            return moment(date).format(format);
        }
    });

    $('#datetimepicker_start').datetimepicker({
        format: 'YYYY-MM-DD HH:mm',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang:'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });
    $('#datetimepicker_end').datetimepicker({
        format: 'YYYY-MM-DD HH:mm',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang:'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });





    if ($.fn.dataTable.isDataTable('#transactionsTable')) {
        transactionsDataTable = $('#transactionsTable').DataTable();
    } else {
        var id = $("#user-id").val();
        transactionsDataTable = $('#transactionsTable').DataTable({
            "serverSide": true,
            "ajax": {
                "url": '/2a8fy7b07dxe44/transactions?id=' + id,
                "dataSrc": "data"
            },
            "paging": true,
            "info": true,
            "bFilter": false,
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
                            return '<button class="transactionlist-order-detail-button">' + data.id + '</button>';
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
        $('#transactionsTable tbody').on('click', '.transactionlist-order-detail-button', function () {
            var currentRow = transactionsDataTable.row($(this).parents('tr'));
            getOrderDetailedInfo(currentRow, currentRow.data().order.id, false);
        });
    }

    $('#filter-apply').on('click', function (e) {
        e.preventDefault();
        reloadTable();
    });

    $('#filter-reset').on('click', function (e) {
        e.preventDefault();
        $('#transaction-search-form')[0].reset();
        reloadTable();

    });

    function reloadTable() {
        var formParams = $('#transaction-search-form').serialize();
        var url = '/2a8fy7b07dxe44/transactions?id=' + $("#user-id").val() +'&' + formParams;
        transactionsDataTable.ajax.url(url).load();
    }




});

