/**
 * Created by OLEG on 03.02.2017.
 */
$(function () {

    updateBankDetails();
    ensureCompleteInput();

    $('#bankId').on('change', function () {
        updateBankDetails();
        ensureCompleteInput();
    });

    $('#userFullName').on('input', ensureCompleteInput);

    $('#invoiceCancel').click(function () {
        $('#invoiceCancelForm').submit();
    });


});

function updateBankDetails() {
    var $bankTableName = $('#bankName');
    var $bankTableAccount = $('#bankAccount');
    var $bankTableRecipient = $('#bankRecipient');
    $('#bankName, #bankAccount, #bankRecipient').empty();
    var bankId = $('#bankId').val();
    var $bankInfo = $('#bankInfo').find('p[data-bankid="' + bankId + '"]');
    $($bankTableName).append($($bankInfo).find('span:first').clone());
    $($bankTableAccount).append($($bankInfo).find('span:nth-child(2)').clone());
    $($bankTableRecipient).append($($bankInfo).find('span:nth-child(3)').clone());
}

function ensureCompleteInput() {
    var bankId = parseInt($('#bankId').val());
    var userFullName = $('#userFullName').val();

    if (bankId === -1 || !userFullName || userFullName.length < 2) {
        $('#invoiceSubmit').prop('disabled', true);
    } else {
        $('#invoiceSubmit').prop('disabled', false);
    }
}