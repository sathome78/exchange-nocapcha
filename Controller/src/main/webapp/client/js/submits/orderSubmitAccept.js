/**
 * Created by Valk on 04.04.16.
 */

function submitAcceptOrder(id) {
    $.ajax({
        url: '/orders/accept?id=' + id,
        type: 'GET',
        success: function(){
            window.location = '/orders/acceptordersuccess';
        }
    });
    return false;
}
