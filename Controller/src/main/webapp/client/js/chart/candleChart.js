$(document).ready(function () {
    function drawVisualization() {
        $.get("/dashboard/chartArray", function (arrayResult) {
            var Combined = new Array();
            //for (var i = 0; i < arrayResult.length; i++){
            for (var i = 0; i < 10; i++) {
                //Combined[i] = [ arrayResult[i][0], arrayResult[i][1], arrayResult[i][2],  arrayResult[i][3], arrayResult[i][4], arrayResult[i][5]];
                //Combined[i] = [ '2016-01-01', i-1, i+1,  i+2, i-2, i*5];
                Combined[i] = ['2016-01-01', 2, 3, 4, 5, i * 5];
                //Combined[i] = [2016+i, 2, 3, 4, 5, i * 5];
            }
            var data = google.visualization.arrayToDataTable(Combined, true);

            options = {
                chartArea: {
                    left: 50,
                    top: 10,
                    width: 565,
                    height: 150,
                    backgroundColor: {
                        fillOpacity: 0.25,
                        fill: '#FFF'
                    }
                },
                backgroundColor: {
                    fillOpacity: 0,
                    fill: '#FFF'
                },
                colors: ["green", "blue"],
                vAxis: {
                    gridlines: {
                        color: 'orange',
                        count: 3
                    },
                    minorGridlines: {
                        color: 'magenta',
                        count: 4
                    },
                    baselineColor: 'blue',
                    textStyle: {
                        color: 'green',
                        fontName: 'sans-serif',
                        fontSize: 10
                    }
                },

                hAxis: {
                    ticks: [1, 1.3, 1.6, 4, 6],
                    textStyle: {
                        color: 'red',
                        fontName: 'sans-serif',
                        fontSize: 10
                    },
                    gridlines: {
                        color: 'green'
                    },
                    baselineColor: 'blue'
                },
                candlestick: {
                    fallingColor: {
                        fill: "#0ab92b",
                        stroke: "green",
                        strokeWidth: 5
                    },
                    risingColor: {
                        fill: "#f01717",
                        stroke: "#d91e1e",
                        strokeWidth: 1
                    },
                    hollowIsRising: true
                },
                series: {0: {type: "candlesticks"}, 1: {type: "bars", targetAxisIndex: 1, color: "#ebebeb"}},
                legend: "none"
            };
            chart = new google.visualization.ComboChart(document.getElementById("chart_div"));
            chart.draw(data, options);
        });
    }
    try {
        google.setOnLoadCallback(drawVisualization);
    } catch (e) {
        console.log(e);
    }
});