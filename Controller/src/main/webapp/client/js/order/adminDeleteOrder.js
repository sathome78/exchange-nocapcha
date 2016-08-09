/**
 * Created by Valk on 12.05.2016.
 */

function getOrderDetailedInfo(currentRow) {
    var order_id = currentRow.data().id;
    $.ajax({
        url: '/admin/orderinfo?id=' + order_id,
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
            if (data.orderStatusName.toUpperCase() === 'DELETED') {
                $("#delete-order-info__delete").toggle(false);
            } else {
                $("#delete-order-info__delete").toggle(true);
                /*$("#delete-order-info__delete").attr('onclick', 'deleteOrderByAdmin(' + data.id + ')');*/
                $("#delete-order-info__delete").on('click', function () {
                    deleteOrderByAdmin(data.id, currentRow);
                })
            }

            /**/
            $('#order-delete-modal').modal();
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
                url: '/admin/orderdelete?id=' + order_id,
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
                error: function(){
                    $('#order-delete-modal--result-info').find('.error-delete').toggle(true);
                    $('#order-delete-modal--result-info').modal();
                }
            }
        );
    })
    $('#order-delete-modal').modal('hide');
}

function searchAndDeleteOrderByAdmin() {
    $('#order-delete-modal--search').modal();
}

function searchOrder() {
    var isError = false;
    $('.input-block-wrapper__error-wrapper').toggle(false);
    var orderRate = $('#orderRate').val();
    var orderVolume = $('#orderVolume').val();
    var creatorEmail = $('#creatorEmail').val();

    if (orderRate.length > 0) {
        var match = orderRate.match(/^\d+(\.{1,1}\d+)?/);
        if (!match || match[0] !== orderRate){
            $('.input-block-wrapper__error-wrapper[for=orderRate]').toggle(true);
            isError = true;
        }
    }
    if (orderVolume.length > 0) {
        match = orderVolume.match(/^\d+(\.{1,1}\d+)?/);
        if (!match || match[0] !== orderVolume){
            $('.input-block-wrapper__error-wrapper[for=orderVolume]').toggle(true);
            isError = true;
        }
    }

    if (creatorEmail.length > 0) {
        match = creatorEmail.match(/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/);
        if (!match || match[0] !== creatorEmail){
            $('.input-block-wrapper__error-wrapper[for=creatorEmail]').toggle(true);
            isError = true;
        }
    }

    if ($('#orderDateFrom').val().length > 0 && (!$('#orderDateFrom').val().match(/\d{4}\-\d{2}\-\d{2}\s{1}\d{2}\:\d{2}\:\d{2}/))){
        $('.input-block-wrapper__error-wrapper[for=orderDateFrom]').toggle(true);
        isError = true;
    }
    if ($('#orderDateTo').val().length > 0 && !($('#orderDateTo').val().match(/\d{4}\-\d{2}\-\d{2}\s{1}\d{2}\:\d{2}\:\d{2}/))){
        $('.input-block-wrapper__error-wrapper[for=orderDateTo]').toggle(true);
        isError = true;
    }
    if (isError) {
        return;
    }
    var data = $('#delete-order-info__form').serialize();
    var searchUrl = '/admin/searchorders?' + data;
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
                    "data": "id"
                },
                {
                    "data": "dateCreation",
                    "render": function (data, type, row) {
                        if (type == 'display') {
                            return data.split(' ')[0] + '<br/>' + data.split(' ')[1];
                        }
                        return data;
                    }
                },
                {
                    "data": "currencyPairName"
                },
                {
                    "data": "orderTypeName"
                },
                {
                    "data": "exrate"
                },
                {
                    "data": "amountBase"
                },
                {
                    "data": "orderCreatorEmail"
                },
                {
                    "data": "status"
                }


            ],
            "order": [
                [0, 'asc']
            ]
        });
        $('#order-info-table tbody').on('click', 'tr', function () {
            var currentRow = orderDataTable.row( this );
            var currentData = currentRow.data();

            getOrderDetailedInfo(currentRow);
        } );


        $('#order-info-table').toggle(true);
    }

}
$(function () {
    $('#order-info-table').toggle(false);
});