/**
 * Created by Valk on 04.04.16.
 */

function submitCreateOrder(id) {
    form = $('#submitOrderForm');
    $.ajax({
        url: '/orders/create',
        type: 'POST',
        data: form.serialize(),
        success: function(html){
            window.location = '/orders/createordersuccess';
        }
    });
    return false;
}
