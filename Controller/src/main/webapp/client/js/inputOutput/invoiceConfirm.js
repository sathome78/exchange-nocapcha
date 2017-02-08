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
    var payeeBankTest = validateString($('#payeeBankName').val(), BANK_NAME_REGEX, $('#bankNameError'));
    var payeeAccountTest = validateString($('#userAccount').val(), DIGITS_ONLY_REGEX, $('#bankNameError'));
    var payeeFullNameTest = validateString($('#userFullName').val(), NAME_REGEX, $('#userFullNameError'));



    if (payeeBankTest && payeeAccountTest && payeeFullNameTest) {
        $('#invoiceSubmit').prop('disabled', false);
    } else {
        $('#invoiceSubmit').prop('disabled', true);
    }
}

function validateString(str, regex, errorDiv) {
    console.log(str);
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