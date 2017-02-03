/**
 * Created by OLEG on 03.02.2017.
 */
$(function () {

    updateBankDetails();

    $('#bankId').on('change', function () {
        updateBankDetails();
    })


});

function updateBankDetails() {
    var $bankDetails = $('#bankDetails');
    $($bankDetails).empty();
    var bankId = $('#bankId').val();
    var $bankInfo = $('#bankInfo').find('p[data-bankid="' + bankId + '"]');
    $($bankDetails).append($bankInfo.clone());
}