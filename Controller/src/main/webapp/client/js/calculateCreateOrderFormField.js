/**
 * Created by Valk on 13.04.16.
 */

$(function () {
    $('#amountBuy').on('keyup', calculateFieldsForBuy);
    $('#exchangeRateBuy').on('keyup', calculateFieldsForBuy);
    $('#amountSell').on('keyup', calculateFieldsForSell);
    $('#exchangeRateSell').on('keyup', calculateFieldsForSell);

    calculateFieldsForBuy();
    calculateFieldsForSell();
});


function calculateFieldsForBuy() {
    if ($('#amountBuy').prop('readonly')) {
        //not calculate if form is only to show order
        return;
    }
    var amount = +$('#amountBuy').val();
    var exchangeRate = +$('#exchangeRateBuy').val();
    var totalForBuy = +$('#totalForBuy').val(amount * exchangeRate).val();
    var comission = +$('#comissionForBuyRate').val();
    var calculatedComissionForBuy = +$('#calculatedComissionForBuy').val(totalForBuy * comission / 100).val();
    var totalWithComissionForBuy = +$('#totalWithComissionForBuy').val(totalForBuy + calculatedComissionForBuy).val();
    var currencyBaseBalance = $('#currencyConvertBalance').val();
    currencyBaseBalance = +(currencyBaseBalance.replace(',','.'));
    if ((totalWithComissionForBuy > currencyBaseBalance) || (amount <= 0) || (exchangeRate <= 0) || (!totalForBuy)) {
        $('#totalWithComissionForBuy').css('color', 'red');
        $('#submitOrderBuy').prop('disabled', true).css('color', 'gray');
    } else {
        $('#totalWithComissionForBuy').css('color', 'white');
        $('#submitOrderBuy').prop('disabled', false).css('color', 'white');
    }

    if ((totalWithComissionForBuy > currencyBaseBalance) || (amount <= 0)) {
        $('#totalForBuy').css('color', 'red');
    } else {
        $('#totalForBuy').css('color', 'white');
    }
}

function calculateFieldsForSell() {
    if ($('#amountSell').prop('readonly')) {
        //not calculate if form is only to show order
        return;
    }
    var amount = +$('#amountSell').val();
    var exchangeRate = +$('#exchangeRateSell').val();
    var totalForSell = +$('#totalForSell').val(amount * exchangeRate).val();
    var comission = +$('#comissionForSellRate').val();
    var calculatedComissionForSell = +$('#calculatedComissionForSell').val(totalForSell * comission / 100).val();
    var totalWithComissionForSell = +$('#totalWithComissionForSell').val(totalForSell - calculatedComissionForSell).val();
    var currencyBaseBalance = $('#currencyBaseBalance').val();
    currencyBaseBalance = +currencyBaseBalance.replace(',','.');
    if ((amount > currencyBaseBalance) || (amount <= 0) || (exchangeRate <= 0) || (!totalForSell)) {
        $('#totalWithComissionForSell').css('color', 'red');
        $('#submitOrderSell').prop('disabled', true).css('color', 'gray');
    } else {
        $('#totalWithComissionForSell').css('color', 'white');
        $('#submitOrderSell').prop('disabled', false).css('color', 'white');
    }

    if ((amount > currencyBaseBalance) || (amount <= 0)) {
        $('#amountSell').css('color', 'red');
    } else {
        $('#amountSell').css('color', 'white');
    }
}
