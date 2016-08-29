
function submitAcceptInvoice(id) {
    $.ajax({
        url: '/merchants/invoice/payment/accept?id=' + id,
        type: 'GET',
        success: function(){
            window.location = '/admin/invoiceConfirmation';
        }
    });
    return false;
}
