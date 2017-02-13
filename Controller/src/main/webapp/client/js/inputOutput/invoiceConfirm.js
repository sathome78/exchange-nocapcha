/**
 * Created by OLEG on 07.02.2017.
 */
var DIGITS_ONLY_REGEX = /^\d+$/;
var NAME_REGEX = /^[a-zA-Z]([-']?[a-zA-Z]+)*( [a-zA-Z]([-']?[a-zA-Z]+)*)+$/;
var BANK_NAME_REGEX = /^[a-zA-Z]([-']?[a-zA-Z]+)*( [a-zA-Z]([-']?[a-zA-Z]+)*)*$/;

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
        $('#payerBankName').val($(this).val());
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
        $('#payerBankName').val('');
        $($otherBankInputDiv).hide();
        $($otherBankInputDiv).find('#bankSelect').val('')
    } else if (bankId === 0) {
        $('#payerBankName').val('');
        $($otherBankInputDiv).show();
    } else {
        $($otherBankInputDiv).hide();
        $('#payerBankName').val($($bankSelect).find('option:selected').text());
        $($otherBankInputDiv).find('#bankSelect').val('')
    }
    checkFields();
}

function checkFields() {
    var payerBankTest = validateString($('#payerBankName').val(), BANK_NAME_REGEX, $('#bankNameError'));
    var payerAccountTest = validateString($('#userAccount').val(), DIGITS_ONLY_REGEX, $('#userAccountError'));
    var payerFullNameTest = validateString($('#userFullName').val(), NAME_REGEX, $('#userFullNameError'));
    console.log(payerBankTest);
    console.log(payerAccountTest);
    console.log(payerFullNameTest);



    if (payerBankTest && payerAccountTest && payerFullNameTest) {
        $('#invoiceSubmit').prop('disabled', false);
    } else {
        $('#invoiceSubmit').prop('disabled', true);
    }
}

function validateString(str, regex, errorDiv) {
    if (!str) {
        $(errorDiv).hide();
        return false;
    }
    if (regex.test(str)) {
        $(errorDiv).hide();
        return true;
    } else {
        $(errorDiv).show();
        return false;
    }

}