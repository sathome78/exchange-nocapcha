/**
 * Created by Valk on 04.04.16.
 */

function beginAcceptOrder(id) {
    $.ajax({
        url: '/orders/submitaccept/check?id=' + id,
        type: 'GET',
        success: function(){
            window.location = '/orders/submitaccept?id='+id;
        }
    });
}
