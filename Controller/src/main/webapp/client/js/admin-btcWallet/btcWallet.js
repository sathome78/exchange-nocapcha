/**
 * Created by OLEG on 24.03.2017.
 */
var txHistoryDataTable;

$(function () {
   var $passwordModal = $('#password-modal');
   var $paymentConfirmModal = $('#payment-confirm-modal');


    updateTxHistoryTable();
    retrieveFee();
    checkSendBtcFormFields();

    $('#input-amount, #input-address').on('input', function () {
        checkSendBtcFormFields();
    });

    $('#submit-btc').click(function () {
        $($passwordModal).modal();
    });

    $('#submit-wallet-pass').click(function () {
        $.ajax('/2a8fy7b07dxe44/bitcoinWallet/unlock', {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            data: $('#password-form').serialize(),
            success: function () {
                $($passwordModal).modal('hide');
                fillConfirmModal();
                $($paymentConfirmModal).modal();
            }
        })
    });

    $('#confirm-btc-submit').click(function () {
        var $btcFrom = $('#send-btc-form');
        $.ajax('/2a8fy7b07dxe44/bitcoinWallet/send', {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            data: $($btcFrom).serialize(),
            success: function (data) {
                $($paymentConfirmModal).modal('hide');
                $($btcFrom)[0].reset();
                retrieveFee();
                $('#current-btc-balance').text(data.newBalance);
                successNoty(data.message)
            }
        })
    });

});

function checkSendBtcFormFields() {
    var amount = $('#input-amount').val();
    var address = $('#input-address').val();
    if (amount.length === 0 || address.length === 0) {
        $('#submit-btc').prop('disabled', true);
    } else {
        $('#submit-btc').prop('disabled', false);
    }


}

function fillConfirmModal() {
    var templateVariables = {
        amount: '__amount__',
        address: '__address__'
    };
    var promptMessage = $('#confirmBtcMessage').text();
    var address = $('#input-address').val();
    var amount = $('#input-amount').val();
    promptMessage = promptMessage.replace(templateVariables.amount, amount)
        .replace(templateVariables.address, address);
    $('#btc-confirm-prompt').text(promptMessage);

}




function updateTxHistoryTable() {
    var $txHistoryTable = $('#txHistory');
    var url = '/2a8fy7b07dxe44/bitcoinWallet/transactions';

    if ($.fn.dataTable.isDataTable('#txHistory')) {
        txHistoryDataTable = $($txHistoryTable).DataTable();
        txHistoryDataTable.ajax.url(url).load();
    } else {
        txHistoryDataTable = $($txHistoryTable).DataTable({
            "ajax": {
                "url": url,
                "dataSrc": ""
            },
            "columns": [
                {
                    "data": "time",
                    "render": function (data) {
                        return data.replace(' ', '<br/>');
                    },
                    "className": "text-center"
                },
                {
                    "data": "txId",
                    "render": function (data) {
                        var inputValue = data ? data : '';
                        return '<input readonly value="' + inputValue + '" style="width: 130px" ' +
                            'class="form-control input-block-wrapper__input">';
                    }
                },
                {
                    "data": "category",
                    "render": function (data) {
                        var dataClass;
                        if (data === 'receive') {
                            dataClass = 'class="green"';
                        } else if (data === 'send') {
                            dataClass = 'class="red"';
                        } else {
                            dataClass = '';
                        }
                        return '<span '+ dataClass + '><strong>' + data + '</strong></span>'
                    }
                },
                {
                    "data": "address",
                    "render": function (data) {
                        var inputValue = data ? data : '';
                        return '<input readonly value="' + inputValue + '" style="width: 130px" ' +
                            'class="form-control input-block-wrapper__input">';
                    }
                },
                {
                    "data": "amount"
                },
                {
                    "data": "fee"
                },
                {
                    "data": "confirmations"
                },
                {
                    "data": "txId",
                    "visible": false
                },
                {
                    "data": "address",
                    "visible": false
                }

            ],
            "order": [[
                0,
                "desc"
            ]]
        });}
}

function retrieveFee() {
    $.get('/2a8fy7b07dxe44/bitcoinWallet/estimatedFee', function (data) {
        console.log(data);
        $('#input-fee').val(data);
    })
}





