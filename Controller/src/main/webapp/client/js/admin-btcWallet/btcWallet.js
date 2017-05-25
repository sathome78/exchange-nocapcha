/**
 * Created by OLEG on 24.03.2017.
 */
var txHistoryDataTable;
var urlBase;
$(function () {
   var $passwordModal = $('#password-modal');
   var $paymentConfirmModal = $('#payment-confirm-modal');
   var merchantName = $('#merchantName').text();
   urlBase = '/2a8fy7b07dxe44/bitcoinWallet/' + merchantName + '/';


    updateTxHistoryTable();
    retrieveFee();
    checkSendBtcFormFields();

    $('#addPayment').click(function (e) {
        e.preventDefault();
        var $newPaymentDiv = $('#payment_0').clone(true);
        var currentNum = $('.btcWalletPayment').size();
        $newPaymentDiv.attr('id', 'payment_' + currentNum);
        $($newPaymentDiv).find('input[name="amount"]').attr('id', 'amount_' + currentNum).val('');
        $($newPaymentDiv).find('input[name="address"]').attr('id', 'address_' + currentNum).val('');
        $('#payments').append($newPaymentDiv);
        checkSendBtcFormFields();
    });


    $('.input-amount, .input-address').on('input', function () {
        checkSendBtcFormFields();
    });

    $('#input-fee-actual').on('input', function () {
        $('#tx-fee-form').find('input[name="fee"]').val($(this).val())
    });

    $('#submit-btc').click(function () {
        $($passwordModal).modal();
    });

    $('#reset-btc').click(function () {
        resetForm();
    });


    $('#submit-wallet-pass').click(function () {
        $.ajax(urlBase + 'unlock', {
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
        var data = {};
        $('.btcWalletPayment').each(function () {
            var address = $(this).find('input[name="address"]').val();
            data[address] = parseFloat($(this).find('input[name="amount"]').val());
        });
        console.log(data);
        $.ajax(urlBase + 'sendToMany', {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            contentType: 'application/json; charset=UTF-8',
            data: JSON.stringify(data),
            success: function (data) {
                $($paymentConfirmModal).modal('hide');
                resetForm();
                $('#current-btc-balance').text(data.newBalance);
                successNoty(data.message)
            }
        })
    });
    $('#submitChangeFee').click(function (e) {
        e.preventDefault();
        updateTxFee();
    });

});

function checkSendBtcFormFields() {
    var isFormValid = true;
    $('.btcWalletPayment').each(function () {
        var amount =  $(this).find('input[name="amount"]').val();
        var address = $(this).find('input[name="address"]').val();
        if (amount.length === 0 || address.length === 0) {
            $('#submit-btc').prop('disabled', true);
            isFormValid = false;
            return false;
        }
    });
    if (isFormValid) {
        $('#submit-btc').prop('disabled', false);
    }
}

function resetForm() {
    var $payments = $('#payments');
    var $initialPayment = $('#payment_0');
    $($payments).empty();
    $payments.append($initialPayment);
    $('#send-btc-form')[0].reset();
    retrieveFee();
    checkSendBtcFormFields();
}

function fillConfirmModal() {
    var templateVariables = {
        amount: '__amount__',
        address: '__address__'
    };
    var promptMessage = $('#confirmBtcMessage').text();
    var addresses = '';
    var totalAmount = 0;
    $('.btcWalletPayment').each(function () {
        if (addresses.length === 0) {
            addresses = addresses + $(this).find('input[name="address"]').val();
        } else {
            addresses = addresses + ', ' + $(this).find('input[name="address"]').val();
        }
        totalAmount = totalAmount + parseFloat($(this).find('input[name="amount"]').val())
    });
    promptMessage = promptMessage.replace(templateVariables.amount, totalAmount)
        .replace(templateVariables.address, addresses);
    $('#btc-confirm-prompt').text(promptMessage);

}




function updateTxHistoryTable() {
    var $txHistoryTable = $('#txHistory');
    var viewMessage = $('#viewMessage').text();
    var url = urlBase + 'transactions';

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
                    "data": "",
                    "render": function () {
                        return '<button class="btn btn-sm btn-info" onclick="viewTransactionData(this)">' + viewMessage + '</button>'
                    }
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
    $.get(urlBase + 'estimatedFee', function (data) {
        $('#input-fee').val(data);
    });
    $.get(urlBase + 'actualFee', function (data) {
        $('#input-fee-actual').val(data);
    })
}

function updateTxFee() {
    var data = $('#tx-fee-form').serialize();
    $.ajax(urlBase + 'setFee', {
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        type: 'POST',
        data: data,
        success: function () {
            retrieveFee();
        }
    });

}

function viewTransactionData($elem) {
    var $row = $($elem).parents('tr');
    var rowData = txHistoryDataTable.row($row).data();
    var url = urlBase + 'transaction/details?currency=' + $('#currencyName').text() +
            '&address=' + rowData.address +
            '&hash=' + rowData.txId;
    console.log(url);
    $.get(url, function (data) {
        console.log(data);
    })
}




