
function submitAcceptInvoice(id) {
    $.ajax({
        url: '/merchants/invoice/payment/accept?id=' + id,
        type: 'GET',
        success: function(){
            window.location = '/transaction_invoice';
        }
    });
    return false;
}
