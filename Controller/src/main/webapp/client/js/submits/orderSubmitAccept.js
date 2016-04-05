/**
 * Created by Valk on 04.04.16.
 */

function submitAcceptOrder(id) {
    $.ajax({
        //url: '/orders/submitaccept?id=' + id,
        url: '/orders/submitaccept/check?id=' + id,
        type: 'GET',
        success: function(html){
            window.location = '/orders/submitaccept?id='+id;
        }
    });
    return false;
}
