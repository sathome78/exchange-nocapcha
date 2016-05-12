/**
 * Created by Valk on 04.04.16.
 */

function submitCreateOrder(orderId, orderType) {
    var form;
    if (orderType == 'BUY') {
        form = $('#createBuyOrderForm');
    }
    else {
        form = $('#createSellOrderForm');
    }
    $.ajax({
        url: '/orders/create',
        type: 'POST',
        data: form.serialize(),
        success: function () {
            window.location = '/orders?result=createsuccess';
        },
        error: function () {

        }
    });
}
