/**
 * Created by Valk on 12.05.2016.
 */

function getOrderDetailedInfo(order_id) {
    $.ajax({
        url: '/admin/orderinfo?id=' + order_id,
        type: 'GET',
        success: function (data) {
            $("#id").find('span').html(data.id);
            $("#dateCreation").find('span').html(data.dateCreation.replace('T', ' '));
            $("#dateAcception").find('span').html(data.dateAcception ? data.dateAcception.replace('T', ' ') : '-');
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
            $("#delete-order-info__delete").attr('onclick', 'deleteOrderByAdmin(' + data.id + ')');
            /**/
            $('#order-delete-modal').modal();
        }
    });
}

function deleteOrderByAdmin(order_id) {
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
                    }
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
    var match = $('#orderRate').val().match(/^\d+(\.{1,1}\d+)?/);
    if (!match || match[0] !== $('#orderRate').val()){
        $('.input-block-wrapper__error-wrapper[for=orderRate]').toggle(true);
        isError = true;
    }
    match = $('#orderVolume').val().match(/^\d+(\.{1,1}\d+)?/);
    if (!match || match[0] !== $('#orderVolume').val()){
        $('.input-block-wrapper__error-wrapper[for=orderVolume]').toggle(true);
        isError = true;
    }
    if (!$('#orderDate').val().match(/\d{4}\-\d{2}\-\d{2}\s{1}\d{2}\:\d{2}\:\d{2}/)){
        $('.input-block-wrapper__error-wrapper[for=orderDate]').toggle(true);
        isError = true;
    }
    if (isError) {
        return;
    }
    var data = $('#delete-order-info__form').serialize();
    $.ajax({
        url: '/admin/searchorder',
        type: 'GET',
        data: data,
        success: function (data) {
            $('#order-delete-modal--result-info').find('.delete-order-info__item').toggle(false);
            if (data === -1) {
                $('#order-delete-modal--result-info').find('.error-search').toggle(true);
                $('#order-delete-modal--result-info').modal();
            } else {
                $('#order-delete-modal--search').one('hidden.bs.modal', function (e) {
                    getOrderDetailedInfo(data);
                });
                $('#order-delete-modal--search').modal('hide');
            }
        },
        error: function(){
            $('#order-delete-modal--result-info').find('.delete-order-info__item').toggle(false);
            $('#order-delete-modal--result-info').find('.error-delete').toggle(true);
            $('#order-delete-modal--result-info').modal();
        }
    });
}
