$(function () {
    google.charts.setOnLoadCallback(drawChart);

    function drawChart() {
        $('#graphic').hide();
        $.get("/dashboard/chartArray", function (arrayResult) {
            var Combined = new Array();
            /*for (var i = 0; i < arrayResult.length; i++){
             Combined[i] = [ arrayResult[i][0], arrayResult[i][1], arrayResult[i][2],  arrayResult[i][3], arrayResult[i][4], arrayResult[i][5]];
             }
             var data = google.visualization.arrayToDataTable(Combined, true);*/

            var data = google.visualization.arrayToDataTable([
                [1, 13, 28, 38, 45, 11],
                [2, 31, 38, 55, 66, 21],
                [3, 50, 55, 77, 80, 13],
                [4, 77, 77, 66, 50, 15],
                [5, 77, 77, 66, 50, 12],
                [6, 37, 77, 66, 50, 14],
                [7, 57, 77, 66, 50, 14],
                [8, 67, 77, 66, 50, 5],
                [9, 17, 77, 66, 50, 6],
                [10, 67, 77, 66, 50, 9],
                [11, 77, 77, 66, 50, 14],
                [12, 87, 77, 66, 50, 4],
                [13, 27, 77, 66, 50, 24],
                [14, 37, 77, 66, 50, 11],
                [15, 47, 77, 66, 50, 2],
                [16, 68, 66, 22, 15, 7]
            ], true);

            options = {
                chartArea: {
                    left: 20,
                    right: 20,
                    top: 10,
                    bottom: 20,
                    backgroundColor: {
                        fillOpacity: 0.25,
                        fill: '#FFF'
                    }
                },
                backgroundColor: {
                    fillOpacity: 0,
                    fill: '#FFF'
                },
                vAxis: {
                    gridlines: {},
                    baselineColor: '#65180a',
                    textStyle: {
                        color: '#65180a',
                        fontSize: 10
                    }
                },
                hAxis: {
                    ticks: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16],
                    textStyle: {
                        color: '#65180a',
                        fontSize: 10
                    },
                    gridlines: {
                        color: 'transparent'
                    },
                    baselineColor: '#65180a'
                },
                tooltip: {isHtml: true},
                crosshair: { trigger: 'both' },
                series: {0: {type: "area", color: '#65180a'}, 1: {type: "bars", targetAxisIndex: 1, color: "#B87C7F"}},
                legend: "none"
            };
            try {
                chart = new google.visualization.ComboChart(document.getElementById("chart_div"));
                view = new google.visualization.DataView(data);
                view.hideColumns([2, 3, 4]);
                chart.draw(view, options);
            } catch (e){
                console.log(e);
                $('#graphic').show();
            }
        });
    }
});