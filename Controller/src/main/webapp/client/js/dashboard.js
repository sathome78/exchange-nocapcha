
window.onload = function(){
    changeCurrency();
};

function changeCurrency() {
    var selectedVal = $("#currencyPair-select").find(":selected").val();
    var chartPair = document.getElementById("chartPair");
    if (chartPair) chartPair.innerHTML = selectedVal;

    //for new interface
    selectedVal = document.querySelector('.exchange__pair[selected]').innerHTML;
    document.getElementById("chartPair").innerHTML = selectedVal;
}


$(function(){
    $("button[name='calculateBuy']").click(function(){
        $.get("/dashboard/commission/buy",function(commission){
            var sumBuy = document.getElementById("amountBuyForm1").value;
            var sumSell = document.getElementById("amountSellForm1").value;
            if(sumBuy != 0 && sumSell != 0) {
                var computedCommission = Math.ceil(sumBuy * commission)/100;
                document.getElementById("buyCommission").innerHTML = computedCommission.toFixed(2);
                document.getElementById("sumBuyWithCommission").innerHTML = ((sumBuy - computedCommission)*sumSell).toFixed(2);
                document.getElementById("sumSellForm1").value = sumBuy*sumSell;

            }else {
                document.getElementById("buyCommission").innerHTML = 0;
                document.getElementById("sumBuyWithCommission").innerHTML = 0;
                document.getElementById("sumSellForm1").value = 0;
            }

        })

    })

    $("button[name='calculateSell']").click(function(){
        $.get("/dashboard/commission/sell",function(commission){
            var sumSell = document.getElementById("amountSellForm2").value;
            var sumBuy = document.getElementById("amountBuyForm2").value;
            if(sumBuy != 0 && sumSell != 0) {
                var computedCommission = Math.ceil(sumSell * commission)/100;
                document.getElementById("sellCommission").innerHTML = (sumBuy*computedCommission).toFixed(2);
                document.getElementById("sumSellWithCommission").innerHTML = ((sumSell - computedCommission)*sumBuy).toFixed(2);
                document.getElementById("sumBuyForm2").value = sumBuy*sumSell;
            }else {
                document.getElementById("sellCommission").innerHTML = 0;
                document.getElementById("sumSellWithCommission").innerHTML = 0;
                document.getElementById("sumBuyForm2").value = 0;

            }
        })

    })


    $("select[name='currencyPair-select']").change(function(){
        changeCurrency();
        // second part
    })

})

$(document).ready(function() {


    // Chart dashboard
    function drawVisualization() {
        $.get("/dashboard/chartArray",function(arrayResult){

            var Combined = new Array();

            for (var i = 0; i < arrayResult.length; i++){

                Combined[i] = [ arrayResult[i][0], arrayResult[i][1], arrayResult[i][2],  arrayResult[i][3], arrayResult[i][4], arrayResult[i][5]];

            }
            var data = google.visualization.arrayToDataTable(Combined, true);

            options = {
                chartArea:{
                    left: 50,
                    top: 10,
                    width: 565,
                    height: 150
                },
                colors:["#515151","#515151"],
                candlestick:{
                    fallingColor:{
                        fill: "#0ab92b",
                        stroke: "green",
                        strokeWidth: 1
                    },
                    risingColor:{
                        fill: "#f01717",
                        stroke: "#d91e1e",
                        strokeWidth: 1
                    },
                    hollowIsRising: true
                },
                series: {0: {type: "candlesticks"}, 1: {type: "bars", targetAxisIndex:1, color:"#ebebeb"}},
                legend:"none"
            };

            chart = new google.visualization.ComboChart(document.getElementById("chart_div"));
            chart.draw(data, options);
        });

    }

    google.setOnLoadCallback(drawVisualization);

});