/**
 * Created by Valk on 04.04.16.
 */

function submitCreateOrder() {
    form = $('#submitOrderForm');
    $.ajax({
        url: '/orders/create',
        type: 'POST',
        data: form.serialize(),
        success: function(){
            window.location = '/orders/createordersuccess';
        }
    });
}
