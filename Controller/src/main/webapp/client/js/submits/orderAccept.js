/**
 * Created by Valk on 04.04.16.
 */

function acceptOrder(id) {
    $.ajax({
        url: '/orders/accept?id=' + id,
        type: 'GET',
        success: function(html){
            window.location = '/orders/acceptordersuccess';
        }
    });
    return false;
}
