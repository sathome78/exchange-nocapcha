$(function () {
    google.charts.setOnLoadCallback(drawChart);
});

function drawChart(period) {
    $('#graphic').hide();
    var url = '/dashboard/chartArray/area';
    url = period ? url + '?period=' + period : url;
    $.get(url, function (arrayResult) {
        var backDealInterval = arrayResult[0][0]; //BackDealInterval is here
        setActivePeriodSwitcherButton(backDealInterval);
        /**/
        var Combined = new Array();
        for (var i = 1; i < arrayResult.length; i++) {
            /*skip first row - BackDealInterval is there*/
            Combined[i-1] = [arrayResult[i][0], arrayResult[i][1],
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

function setActivePeriodSwitcherButton(backDealInterval) {
    var id = backDealInterval.intervalValue+backDealInterval.intervalType.toLowerCase();
    $('.period-menu__item').removeClass('active');
    $('#'+id).addClass('active');

}