$(function () {
    changeCurrency();

    $("button[name='calculateBuy']").click(function () {
        $.get("/dashboard/commission/buy", function (commission) {
            var sumBuy = document.getElementById("amountBuyForm1").value;
            var sumSell = document.getElementById("amountSellForm1").value;
            if (sumBuy != 0 && sumSell != 0) {
                var computedCommission = Math.ceil(sumBuy * commission) / 100;
                document.getElementById("buyCommission").innerHTML = computedCommission.toFixed(2);
                document.getElementById("sumBuyWithCommission").innerHTML = ((sumBuy - computedCommission) * sumSell).toFixed(2);
                document.getElementById("sumSellForm1").value = sumBuy * sumSell;

            } else {
                document.getElementById("buyCommission").innerHTML = 0;
                document.getElementById("sumBuyWithCommission").innerHTML = 0;
                document.getElementById("sumSellForm1").value = 0;
            }

        })

    });

    $("button[name='calculateSell']").click(function () {
        $.get("/dashboard/commission/sell", function (commission) {
            var sumSell = document.getElementById("amountSellForm2").value;
            var sumBuy = document.getElementById("amountBuyForm2").value;
            if (sumBuy != 0 && sumSell != 0) {
                var computedCommission = Math.ceil(sumSell * commission) / 100;
                document.getElementById("sellCommission").innerHTML = (sumBuy * computedCommission).toFixed(2);
                document.getElementById("sumSellWithCommission").innerHTML = ((sumSell - computedCommission) * sumBuy).toFixed(2);
                document.getElementById("sumBuyForm2").value = sumBuy * sumSell;
            } else {
                document.getElementById("sellCommission").innerHTML = 0;
                document.getElementById("sumSellWithCommission").innerHTML = 0;
                document.getElementById("sumBuyForm2").value = 0;

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