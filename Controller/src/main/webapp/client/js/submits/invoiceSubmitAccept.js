
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

function submitAcceptBitcoin(id) {
    var bitcoin_hash = document.getElementById("bitcoin_hash" + id).value;
    var amount = document.getElementById("manual_amount" + id).value;
    if(bitcoin_hash==''){
        alert("Hash is empty!");
        return false;
    }
    if(amount==''){
        alert("Amount is empty!");
        return false;
    }
    if(!parseFloat(amount)>0){
        alert('Amount must be more 0')
        return false;
    }
    $.ajax({
        url: '/merchants/bitcoin/payment/accept?id=' + id + '&hash=' + bitcoin_hash + '&amount=' + parseFloat(amount),

        type: 'GET',
        success: function(){
            window.location = '/admin/bitcoinConfirmation';
        }
    });
    return false;
}
