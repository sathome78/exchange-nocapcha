function acceptInvoice(e, id) {
    var event = e || window.event;
    event.stopPropagation();
    if (confirm($('#prompt_acc_rqst').html())) {
        $.ajax({
            url: '/merchants/invoice/payment/accept?id=' + id,
            type: 'GET',
            success: function () {
                window.location = '/2a8fy7b07dxe44/invoiceConfirmation';
            },
            error: function (jqXHR) {
                errorInCookie(getErrorMessage(jqXHR));
                window.location = '/2a8fy7b07dxe44/invoiceConfirmation';
            }
        });
    }
    return false;
}

function declineInvoice(e, id, email) {
    var event = e || window.event;
    event.stopPropagation();
    var $modal = $("#note-before-decline-modal");
    $.ajax({
        url: '/2a8fy7b07dxe44/phrases/invoice_decline?email=' + email,
        type: 'GET',
        success: function (data) {
            $list = $modal.find("#phrase-template-list");
            $list.html("<option></option>");
            data.forEach(function (e) {
                $list.append($("<option></option>").append(e));
            });
            $modal.find("#createCommentConfirm").one("click", function () {
                var comment = $('#commentText').val().trim();
                if (!comment) {
                    return;
                }
                $modal.modal('hide');
                $.ajax({
                    url: '/merchants/invoice/payment/decline?id=' + id+'&comment='+comment,
                    headers: {
                        'X-CSRF-Token': $("input[name='_csrf']").val(),
                    },
                    type: 'POST',
                    success: function () {
                        window.location = '/2a8fy7b07dxe44/invoiceConfirmation';
                    }
                });
            });
            $modal.find("#createCommentCancel").one("click", function () {
                $modal.modal('hide');
            });
            $modal.modal();
        }
    });
}

function submitAcceptBitcoin(id) {
    if (confirm($('#prompt_acc_rqst').html())) {

        var bitcoin_hash = document.getElementById("bitcoin_hash" + id).value;
        var amount = document.getElementById("manual_amount" + id).value;
        if (bitcoin_hash == '') {
            alert("Hash is empty!");
            return false;
        }
        if (amount == '') {
            alert("Amount is empty!");
            return false;
        }
        if (!parseFloat(amount) > 0) {
            alert('Amount must be more 0')
            return false;
        }
        $.ajax({
            url: '/merchants/bitcoin/payment/accept?id=' + id + '&hash=' + bitcoin_hash + '&amount=' + parseFloat(amount),

            type: 'GET',
            success: function () {
                window.location = '/2a8fy7b07dxe44/bitcoinConfirmation';
            }
        });
    }
    return false;
}


