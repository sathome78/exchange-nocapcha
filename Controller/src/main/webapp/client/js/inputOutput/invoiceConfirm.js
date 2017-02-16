/**
 * Created by OLEG on 07.02.2017.
 */
var DIGITS_ONLY_REGEX = /^\d+$/;
var NAME_REGEX = /^[a-zA-Z]([-']?[a-zA-Z]+)*( [a-zA-Z]([-']?[a-zA-Z]+)*)+$/;
var BANK_NAME_REGEX = /^[a-zA-Z]([-']?[a-zA-Z]+)*([ ,.]{0,2}[a-zA-Z\d]([-']?[a-zA-Z\d]+)*)*$/;
var BANK_CODE_REGEX = /^[\d]{2,5}$/;

$(function () {

    var $bankSelect = $('#bankSelect');
    var $otherBank = $('#otherBank');
    var $otherBankInputDiv = $($otherBank).parents('.input-block-wrapper');
    var $bankCode = $('#bankCode');
    $($otherBankInputDiv).hide();
    $($bankCode).prop('readonly', true);
    updateBankSelection($bankSelect, $otherBankInputDiv, $bankCode);

    $($bankSelect).on('change', function () {
        updateBankSelection(this, $otherBankInputDiv, $bankCode);
    });
    $($otherBank).on('input', function () {
        $('#payerBankName').val($(this).val());
        checkFields();
    });
    $('#userAccount, #userFullName, #bankCode').on('input', function () {
        checkFields();
    });

    $('#invoiceCancel').click(function () {
        window.location = '/dashboard?startupPage=myhistory&startupSubPage=myinputoutput';
    });

    $('#invoiceReturn').click(function () {
        window.location = '/dashboard?startupPage=myhistory&startupSubPage=myinputoutput';
    });

    $('#invoiceRevokeAction').on('click', function (e) {
        e.preventDefault();
        var $form = $(this).parents('#confirmationForm');
        var $action = $form.find('input[name=action]');
        $action.attr("value", "revoke");
        $form[0].submit();
    });

});

function updateBankSelection($bankSelect, $otherBankInputDiv, $bankCode) {
    var bankId = parseInt($($bankSelect).val());
    if (bankId === -1) {
        $('#payerBankName').val('');
        $($otherBankInputDiv).hide();
        $($otherBankInputDiv).find('#bankSelect').val('');
        $($bankCode).val('');
        $($bankCode).prop('readonly', true);
    } else if (bankId === 0) {
        $('#payerBankName').val('');
        $($otherBankInputDiv).show();
        if ($('#invoiceConfirmed').text().trim() != 'true' ) {
            $($bankCode).prop('readonly', false);
            $($bankCode).val('');
        }
    } else {
        $($otherBankInputDiv).hide();
        $($bankCode).val($($bankSelect).val());
        $($bankCode).prop('readonly', true);
        $('#payerBankName').val($($bankSelect).find('option:selected').text());
        $($otherBankInputDiv).find('#bankSelect').val('')
    }
    checkFields();
}

function checkFields() {
    var payerBankTest = validateString($('#payerBankName').val(), BANK_NAME_REGEX, $('#bankNameError'), false);
    var payerBankCodeTest = validateString($('#bankCode').val(), BANK_CODE_REGEX, $('#bankCodeError'), true);
    var payerAccountTest = validateString($('#userAccount').val(), DIGITS_ONLY_REGEX, $('#userAccountError'), false);
    var payerFullNameTest = validateString($('#userFullName').val(), NAME_REGEX, $('#userFullNameError'), false);

    if (payerBankTest && payerAccountTest && payerFullNameTest && payerBankCodeTest) {
        $('#invoiceSubmit').prop('disabled', false);
    } else {
        $('#invoiceSubmit').prop('disabled', true);
    }
}

function validateString(str, regex, errorDiv, allowAbsent) {
    if (!str) {
        $(errorDiv).hide();
        return allowAbsent;
    }
    if (regex.test(str)) {
        $(errorDiv).hide();
        return true;
    } else {
        $(errorDiv).show();
        return false;
    }

}