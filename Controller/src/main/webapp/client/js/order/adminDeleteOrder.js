/**
 * Created by Valk on 12.05.2016.
 */

function getOrderDetailedInfo(currentRow, orderId, enableDelete) {
    $.ajax({
        url: '/2a8fy7b07dxe44/orderinfo?id=' + orderId,
        type: 'GET',
        success: function (data) {
            $("#id").find('span').html(data.id);
            $("#dateCreation").find('span').html(data.dateCreation);
            $("#dateAcception").find('span').html(data.dateAcception ? data.dateAcception : '-');
            $("#currencyPairName").find('span').html(data.currencyPairName);
            $("#orderStatusName").find('span').html(data.orderStatusName.toUpperCase());
            $("#orderTypeName").find('span').html(data.orderTypeName);
            $("#exrate").find('span').html(data.exrate);
            $("#amountBase").find('span').html(data.amountBase + ' ' + data.currencyBaseName);
            $("#amountConvert").find('span').html(data.amountConvert + ' ' + data.currencyConvertName);
            $("#orderCreatorEmail").find('span').html(data.orderCreatorEmail);
            $("#orderAcceptorEmail").find('span').html(data.orderAcceptorEmail?data.orderAcceptorEmail:'-');
            $("#transactionCount").find('span').html(data.transactionCount);
            $("#companyCommission").find('span').html(data.companyCommission?data.companyCommission + ' ' + data.currencyConvertName:'-');
            /**/
            if (data.orderStatusName.toUpperCase() === 'DELETED' || !enableDelete) {
                $("#delete-order-info__delete").toggle(false);
            } else {
                $("#delete-order-info__delete").toggle(true);
                $("#delete-order-info__delete").on('click', function () {
                    deleteOrderByAdmin(data.id, currentRow);
                })
            }
            if (data.orderStatusName.toUpperCase() === 'OPENED') {
                $("#delete-order-info__accept").toggle(true);
                $("#delete-order-info__accept").on('click', function () {
                    acceptOrderByAdmin(data.id, currentRow);
                });
            } else {
                $("#delete-order-info__accept").toggle(false);
            }

            /**/
            $('#order-delete-modal').modal();
        }
    });
}

function getTransferDetailedInfo(orderId) {
    $.ajax({
        url: '/2a8fy7b07dxe44/transferInfo?id=' + orderId,
        type: 'GET',
        success: function (data) {
            $("#info-date").html(data.creationDate);
            $("#info-currency").html(data.currencyName);
            $("#info-amount").html(data.amount);
            $("#info-userFrom").html("<a href='mailto:" +  data.userFromEmail + "'>" + data.userFromEmail + "</a>");
            $("#info-userTo").html("<a href='mailto:" + data.userToEmail + "'>" + data.userToEmail + "</a>");
            $("#info-commissionAmount").html(data.comission);
            $('#user_transfer_info_modal').modal();
        }
    });
}

function deleteOrderByAdmin(order_id, currentRow) {
    $('#order-delete-modal').one('hidden.bs.modal', function (e) {
        /*placed in close callback because we must give time for #order-delete-modal to restore parameters of <body>
         * otherwise we get the shift of the window to the left every time when open and then close #order-delete-modal--ok
         */
        $.ajax({
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                url: '/2a8fy7b07dxe44/orderdelete?id=' + order_id,
                type: 'POST',
                success: function (data) {
                    $('#order-delete-modal--result-info').find('.delete-order-info__item').toggle(false);
                    if (data === -1) {
                        $('#order-delete-modal--result-info').find('.error-delete').toggle(true);
                    } else {
                        $('#order-delete-modal--result-info').find('.success').toggle(true);
                        $("#order-delete-modal--result-info").find('.success').find('span').html(data);
                        var updated = currentRow.data();
                        updated.status = "DELETED";
                        currentRow.data(updated).draw();

                    }
                    $('#order-delete-modal--result-info').modal();
                },
                error: function(jqXHR){
                    if (jqXHR.status != 403) {
                        $('#order-delete-modal--result-info').find('.error-delete').toggle(true);
                        $('#order-delete-modal--result-info').modal();
                    }

                }
            }
        );
    });
    $('#order-delete-modal').modal('hide');
}

function acceptOrderByAdmin(order_id, currentRow) {
    $('#order-delete-modal').one('hidden.bs.modal', function (e) {
        /*placed in close callback because we must give time for #order-delete-modal to restore parameters of <body>
         * otherwise we get the shift of the window to the left every time when open and then close #order-delete-modal--ok
         */
        $.ajax({
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                url: '/2a8fy7b07dxe44/order/accept?id=' + order_id,
                type: 'POST',
                success: function (data) {
                    var updated = currentRow.data();
                    updated.status = "CLOSED";
                    currentRow.data(updated).draw();

                    successNoty(data['result'], 'successOrder');
                }
            }
        );
    })
    $('#order-delete-modal').modal('hide');
}

function searchAndDeleteOrderByAdmin() {
    $('#order-delete-modal--search').modal();
}

function validateErrorForm() {
    var isError = false;
    $('.input-block-wrapper__error-wrapper').toggle(false);
    var creatorEmail = $('#creatorEmail').val();
    var acceptorEmail = $('#acceptorEmail').val();

    if (creatorEmail.length > 0) {
        match = creatorEmail.match(/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/);
        if (!match || match[0] !== creatorEmail) {
            $('.input-block-wrapper__error-wrapper[for=creatorEmail]').toggle(true);
            isError = true;
        }
    }
    if (acceptorEmail.length > 0) {
        match = acceptorEmail.match(/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/);
        if (!match || match[0] !== acceptorEmail) {
            $('.input-block-wrapper__error-wrapper[for=acceptorEmail]').toggle(true);
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
    var searchUrl = '/2a8fy7b07dxe44/searchorders?' + data;
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
                    "name": "EXORDERS.id"
                },
                {
                    "data": "dateCreation",
                    "name": "EXORDERS.date_creation",
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
                    "data": "exrate",
                    "name": "EXORDERS.exrate"
                },
                {
                    "data": "amountBase",
                    "name": "EXORDERS.amount_base"
                },
                {
                    "data": "orderCreatorEmail",
                    "name": "CREATOR.email"
                },
                {
                    "data": "status",
                    "name": "EXORDERS.status_id"
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
});