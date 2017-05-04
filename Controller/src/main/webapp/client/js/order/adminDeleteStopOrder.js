/**
 * Created by maks on 03.05.2017.
 */

var currentRowGlobal;

function getOrderDetailedInfo(currentRow, orderId, enableDelete) {
    $.ajax({
        url: '/2a8fy7b07dxe44/stopOrderinfo?id=' + orderId,
        type: 'GET',
        success: function (data) {
            $("#id").find('span').html(data.id);
            $("#dateCreation").find('span').html(data.dateCreation);
            $("#dateAcception").find('span').html(data.dateAcception ? data.dateAcception : '-');
            $("#currencyPairName").find('span').html(data.currencyPairName);
            $("#orderStatusName").find('span').html(data.orderStatusName.toUpperCase());
            $("#orderTypeName").find('span').html(data.orderTypeName);
            $("#stop_rate").find('span').html(data.stopRate);
            $("#exrate").find('span').html(data.exrate);
            $("#amountBase").find('span').html(data.amountBase + ' ' + data.currencyBaseName);
            $("#amountConvert").find('span').html(data.amountConvert + ' ' + data.currencyConvertName);
            $("#orderCreatorEmail").find('span').html(data.orderCreatorEmail);
            $("#companyCommission").find('span').html(data.companyCommission);
           currentRowGlobal = currentRow;
             /**/
            if (data.orderStatusName.toUpperCase() === 'DELETED'
               /* || data.orderStatusName.toUpperCase() === 'CANCELLED'*/
                ||/* data.orderStatusName.toUpperCase() === 'CLOSED' ||*/ !enableDelete) {
                $("#delete-order-info__delete").toggle(false);
            } else {
                $("#delete-order-info__delete").toggle(true);
                /*$("#delete-order-info__delete").attr('onclick', 'deleteOrderByAdmin(' + data.id + ')');*/
            }

            /**/
            $('#stop-order-delete-modal').modal();
        }
    });
}


function deleteOrderByAdmin() {
    var order_id = $('#id').find('span').text();

    $('#stop-order-delete-modal').one('hidden.bs.modal', function (e) {
            /*placed in close callback because we must give time for #order-delete-modal to restore parameters of <body>
             * otherwise we get the shift of the window to the left every time when open and then close #order-delete-modal--ok
             */
        $.ajax({
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                url: '/2a8fy7b07dxe44/stopOrderDelete?id=' + order_id,
                type: 'POST',
                success: function (data) {
                    $('#stop-order-delete-modal--result-info').find('.delete-order-info__item').toggle(false);
                    if (data === false) {
                        $('#stop-order-delete-modal--result-info').find('.error-delete').toggle(true);
                    } else {
                        $('#stop-order-delete-modal--result-info').find('.success').toggle(true);
                        $("#stop-order-delete-modal--result-info").find('.success').find('span').html(data);
                        var updated = currentRowGlobal.data();
                        updated.status = "DELETED";
                        currentRowGlobal.data(updated).draw();

                    }
                    $('#stop-order-delete-modal--result-info').modal();
                },
                error: function(jqXHR){
                }
            }
        );
    });
    $('#stop-order-delete-modal').modal('hide');
}

/*function searchAndDeleteOrderByAdmin() {
    $('#order-delete-modal--search').modal();
}*/

function validateErrorForm() {
    var isError = false;
    $('.input-block-wrapper__error-wrapper').toggle(false);
    var creatorEmail = $('#creatorEmail').val();

    if (creatorEmail.length > 0) {
        match = creatorEmail.match(/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/);
        if (!match || match[0] !== creatorEmail) {
            $('.input-block-wrapper__error-wrapper[for=creatorEmail]').toggle(true);
            isError = true;
        }
    }

    return isError;
}

function searchOrder() {
    var isError = validateErrorForm();
    if (isError) {
        return;
    }
    var data = $('#delete-order-info__form').serialize();
    var searchUrl = '/2a8fy7b07dxe44/searchStopOrders?' + data;
    var orderDataTable;
    if ($.fn.dataTable.isDataTable('#order-info-table')) {
        orderDataTable = $('#order-info-table').DataTable();
        orderDataTable.ajax.url(searchUrl).load();
    } else {
        orderDataTable = $('#order-info-table').DataTable({
            "serverSide": true,
            "ajax": {
                "url": searchUrl,
                "dataSrc": "data"
            },
            "paging": true,
            "info": true,
            "bFilter": false,
            "columns": [
                {
                    "data": "id",
                    "name": "STOP_ORDERS.id"
                },
                {
                    "data": "dateCreation",
                    "name": "STOP_ORDERS.date_creation",
                    "render": function (data, type, row) {
                        if (type == 'display') {
                            return data.split(' ')[0] + '<br/>' + data.split(' ')[1];
                        }
                        return data;
                    }
                },
                {
                    "data": "currencyPairName",
                    "name": "CURRENCY_PAIR.name"
                },
                {
                    "data": "orderTypeName",
                    "name": "ORDER_OPERATION.name"
                },
                {
                    "data": "stopRate",
                    "name": "STOP_ORDERS.stop_rate"
                },
                {
                    "data": "exrate",
                    "name": "STOP_ORDERS.limit_rate"
                },
                {
                    "data": "amountBase",
                    "name": "STOP_ORDERS.amount_base"
                },
                {
                    "data": "orderCreatorEmail",
                    "name": "CREATOR.email"
                },
                {
                    "data": "status",
                    "name": "STOP_ORDERS.status_id"
                }


            ],
            "order": [
                [0, 'desc']
            ]
        });
        $('#order-info-table tbody').on('click', 'tr', function () {
            var currentRow = orderDataTable.row( this );
            var currentData = currentRow.data();

            getOrderDetailedInfo(currentRow, currentRow.data().id, true);
        } );


        $('#order-info-table').toggle(true);
    }

}
$(function () {
    $('#order-info-table').toggle(false);
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
        format: 'YYYY-MM-DD HH:mm:ss',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang:'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });
    $('#datetimepicker_end').datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang:'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });
    $('#delete-order-info__search').on('click', searchOrder);
    $('#delete-order-info__reset').on('click', function () {
        $('#delete-order-info__form')[0].reset();
        searchOrder();
    });

    if ($('#delete-order-info__form').size() > 0) {
        searchOrder();
    }

    $("#delete-order-info__delete").on('click', function () {
        deleteOrderByAdmin();
    })
});
