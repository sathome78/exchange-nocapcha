/**
 * Created by OLEG on 07.02.2017.
 */
$(function () {

    var $bankSelect = $('#bankSelect');
    var $otherBank = $('#otherBank');
    var $otherBankInputDiv = $($otherBank).parents('.input-block-wrapper');
    $($otherBankInputDiv).hide();
    updateBankSelection($bankSelect, $otherBankInputDiv);

    $($bankSelect).on('change', function () {
        updateBankSelection(this, $otherBankInputDiv);
    });
    $($otherBank).on('input', function () {
        $('#payeeBankName').val($(this).val());
        checkFields();
    });
    $('#userAccount, #userFullName').on('input', function () {
        checkFields();
    });

    $('#invoiceCancel').click(function () {
        window.location = '/dashboard?startupPage=myhistory&startupSubPage=myinputoutput';
    });


});

function updateBankSelection($bankSelect, $otherBankInputDiv) {
    var bankId = parseInt($($bankSelect).val());
    if (bankId === -1) {
        $('#payeeBankName').val('');
        $($otherBankInputDiv).hide();
        $($otherBankInputDiv).find('#bankSelect').val('')
    } else if (bankId === 0) {
        $('#payeeBankName').val('');
        $($otherBankInputDiv).show();
    } else {
        $('#payeeBankName').val($($bankSelect).find('option:selected').text());
        $($otherBankInputDiv).find('#bankSelect').val('')
    }
    checkFields();
}

function checkFields() {
    var payeeBank = $('#payeeBankName').val();
    var payeeAccount = $('#userAccount').val();
    var payeeFullName = $('#userFullName').val();
    if (validateString(payeeBank) && validateString(payeeAccount) && validateString(payeeFullName)) {
        $('#invoiceSubmit').prop('disabled', false);
    } else {
        $('#invoiceSubmit').prop('disabled', true);
    }
}

function validateString(string) {
    return string.length > 2;
}