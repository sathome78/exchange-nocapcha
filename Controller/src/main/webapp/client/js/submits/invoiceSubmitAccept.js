function acceptInvoice(callback) {
    var data = $('#invoice-accept-form').serialize();
    $.ajax({
        url: '/merchants/invoice/payment/accept',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        type: 'POST',
        data: data,
        success: function () {
            $('#acceptModal').modal('hide');
            callback();
        },
        error: function (jqXHR) {
            errorInCookie(getErrorMessage(jqXHR));
            callback();
        }
    });
}

function declineInvoice(e, id, email, callback) {
    var event = e || window.event;
    event.stopPropagation();
    var $modal = $("#note-before-decline-modal");
    $.ajax({
        url: '/2a8fy7b07dxe44/phrases/invoice_decline?email=' + email,
        type: 'GET',
        success: function (data) {
            $modal.find("#user-language").val(data["lang"]);
            $list = $modal.find("#phrase-template-list");
            $list.html("<option></option>");
            data["list"].forEach(function (e) {
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
                        'X-CSRF-Token': $("input[name='_csrf']").val()
                    },
                    type: 'POST',
                    success: function () {
                        callback();
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


