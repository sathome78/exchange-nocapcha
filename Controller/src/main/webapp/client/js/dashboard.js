
window.onload = function(){
    changeCurrency();
}

function changeCurrency() {
    var selectedVal = $("#currencyPair-select").find(":selected").val()
    document.getElementById("chartPair").innerHTML = selectedVal;
}


$(function(){
    $("button[name='calculateBuy']").click(function(){
        $.get("/dashboard/commission/buy",function(commission){
            var sum = document.getElementById("amountBuyForm1").value;
            var computedCommission = Math.ceil(sum * commission)/100;
            document.getElementById("buyCommission").innerHTML = computedCommission;
            document.getElementById("sumBuyWithCommission").innerHTML = sum - computedCommission;
        })

    })

    $("button[name='calculateSell']").click(function(){
        $.get("/dashboard/commission/sell",function(commission){
            var sum = document.getElementById("amountBuyForm2").value;
            var computedCommission = Math.ceil(sum * commission)/100;
            document.getElementById("sellCommission").innerHTML = computedCommission;
            document.getElementById("sumSellWithCommission").innerHTML = sum - computedCommission;
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