$(function () {
    google.charts.setOnLoadCallback(drawChart);
});

function drawChart(period) {
    if (!period) {
        period = '6 MONTH';
    }
    $('#graphic').hide();
    $.get("/dashboard/chartArray?period=" + period, function (arrayResult) {
        var Combined = new Array();
        for (var i = 0; i < arrayResult.length; i++) {
            Combined[i] = [arrayResult[i][0], arrayResult[i][1],
                /*returns html for tip*/
                getHtml(
                    /*data for tip:*/
                    arrayResult[i][0], arrayResult[i][1],
                    /*titles for tip data:*/
                    arrayResult[i][2], arrayResult[i][3]
                )];
        }
        if (Combined.length == 0) {
            Combined[0] = ['', 0, getHtml()];
        }

        options = {
            chartArea: {
                left: 40,
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
                    opacity: 0,
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
            var chart = new google.visualization.ComboChart(document.getElementById("chart_div"));
            var dataTable = new google.visualization.DataTable();
            dataTable.addColumn('string', '');
            dataTable.addColumn('number', '');
            dataTable.addColumn({'type': 'string', 'role': 'tooltip', 'p': {'html': true}});
            dataTable.addRows(Combined);
            chart.draw(dataTable, options);
        } catch (e) {
            console.log(e);
            $('#graphic').show();
        }
    });
}

function getHtml(date, value, dateTitle, valueTitle) {
    var html;
    if (!date) {
        html = '<div class="areaChartTip">' +
            '<p>' + '<span>' + $('#noData').text() + '</span>' + '</p>' +
            '</div>';
    } else {
        html = '<div class="areaChartTip">' +
            '<p class="areaChartTip__date">' + '<span>' + dateTitle + '</span>' + ': ' + date + '</p>' +
            '<p class="areaChartTip__rate">' + '<span>' + valueTitle + '</span>' + ': ' + value + '</p>' +
            '</div>';
    }
    return html;
}