$(function () {
    changeCurrency();

    $("button[name='calculateBuy']").click(function () {
        $.get("/dashboard/commission/buy", function (commission) {
            var sumBuy = document.getElementById("amountBuyForm1").value;
            var sumSell = document.getElementById("amountSellForm1").value;
            if (sumBuy != 0 && sumSell != 0) {
                var total = sumBuy * sumSell;
                var computedCommission = (total * commission) / 100;
                document.getElementById("buyCommission").innerHTML = 1*computedCommission.toFixed(9);
                document.getElementById("sumBuyWithCommission").innerHTML = total + computedCommission;

            } else {
                document.getElementById("buyCommission").innerHTML = 0;
                document.getElementById("sumBuyWithCommission").innerHTML = 0;
            }
        })

    });

    $("button[name='calculateSell']").click(function () {
        $.get("/dashboard/commission/sell", function (commission) {
            var sumSell = document.getElementById("amountSellForm2").value;
            var sumBuy = document.getElementById("amountBuyForm2").value;
            if (sumBuy != 0 && sumSell != 0) {
                var total = sumBuy * sumSell;
                var computedCommission = (total * commission) / 100;
                document.getElementById("sellCommission").innerHTML = 1*computedCommission.toFixed(9);
                document.getElementById("sumSellWithCommission").innerHTML = total - computedCommission;
            } else {
                document.getElementById("sellCommission").innerHTML = 0;
                document.getElementById("sumSellWithCommission").innerHTML = 0;
            }
        })
    });

    $("select[name='currencyPair-select']").change(function () {
        changeCurrency();
    })

});


function changeCurrency() {
    var selectedVal = $("#currencyPair-select").find(":selected").val();
    var chartPair = document.getElementById("chartPair");
    if (chartPair) chartPair.innerHTML = selectedVal;
}