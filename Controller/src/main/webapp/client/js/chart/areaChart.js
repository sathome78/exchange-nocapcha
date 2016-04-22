$(function () {
    google.charts.setOnLoadCallback(drawChart);

    function drawChart() {
        $('#graphic').hide();
        $.get("/dashboard/chartArray", function (arrayResult) {
            var Combined = new Array();
            for (var i = 0; i < arrayResult.length; i++) {
                Combined[i] = [arrayResult[i][0], arrayResult[i][1]];
            }
            if (Combined.length == 0) {
                Combined[0] = ['',0,0];
            }
            var data = google.visualization.arrayToDataTable(Combined, true);

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
                    //ticks: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16],
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
                crosshair: {trigger: 'both'},
                //series: {0: {type: "area", color: '#65180a'}, 1: {type: "bars", targetAxisIndex: 1, color: "#B87C7F"}},
                series: {0: {type: "area", color: '#65180a'}},
                legend: "none"
            };
            try {
                chart = new google.visualization.ComboChart(document.getElementById("chart_div"));
                view = new google.visualization.DataView(data);
                chart.draw(view, options);
            } catch (e) {
                console.log(e);
                $('#graphic').show();
            }
        });
    }
});