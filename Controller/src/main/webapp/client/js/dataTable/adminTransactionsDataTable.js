var transactionsDataTable;

$(function () {
    var $amountSlider = $('#amount-slider');
    $amountSlider.slider({
        range: true,
        min: 0,
        max: 10000000,
        values: [ 0, 0 ],
        slide: function( event, ui ) {
            $( "#amount" ).val( ui.values[ 0 ] + " - " + ui.values[ 1 ] );
        }
    });
    $( "#amount" ).val($amountSlider.slider( "values", 0 ) + " - " + $amountSlider.slider( "values", 1 ) );

    var $commissionSlider = $('#commission-slider');
    $commissionSlider.slider({
        range: true,
        min: 0,
        max: 10000000,
        values: [ 0, 0 ],
        slide: function( event, ui ) {
            $( "#commission-amount" ).val( ui.values[ 0 ] + " - " + ui.values[ 1 ] );
        }
    });
    $( "#commission-amount" ).val($commissionSlider.slider( "values", 0 ) + " - " + $commissionSlider.slider( "values", 1 ) );

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

    $('#filter-apply').on('click', function (e) {
        e.preventDefault();
        var formParams = $('#transaction-search-form').serialize();
        console.log(formParams);
    })




});

