var canvas;
var ctx;
var candleChartAreaHeight = 160;
var chartAreaLeft = 50;
var chartAreaRight = 30;
var candleChartAreaTop = 5;
var candleChartAreaBottom = 3;
var barChartAreaTop = 3;
var barChartAreaBottom = 23;
var candleDataTable;
var candleChart;
var queryResultArray;

function drawChartCandle(period) {
    $('.candle-description').css('padding-left', chartAreaLeft + 'px');
    $('#candle-open').html('open: ');
    $('#candle-close').html('close: ');
    $('#candle-low').html('low: ');
    $('#candle-high').html('high: ');
    $('#candle-volume').html('volume: ');
    $('#candle-date').html('date: ');
    /**/
    var url = '/dashboard/chartArray/candle';
    url = period ? url + '?period=' + period : url;
    waiterSwitch(true);
    $.get(url, function (data) {
        queryResultArray = data;
        var backDealInterval = queryResultArray[0][0]; //BackDealInterval is here
        setActivePeriodSwitcherButton(backDealInterval);
        /*CANDLE*/
        var candleChartDataArray = prepareCandleData(queryResultArray);
        var candleOptions = prepareCandleOptions();
        var candleChart = drawCandleChart(candleChartDataArray, candleOptions, "candle-chart_div");
        /*BAR*/
        var barChartDataArray = prepareBarData(queryResultArray);
        var barOptions = prepareBarOptions();
        drawBarChart(barChartDataArray, barOptions, "bar-chart_div");
        /**/
        waiterSwitch(false);
        /*CANVAS*/
        canvas = document.getElementById('graphic-canvas');
        var $candleChartDiv = $('#candle-chart_div');
        var $barChartDiv = $('#bar-chart_div');
        canvas.width = parseInt($candleChartDiv.css('width')) - chartAreaLeft - chartAreaRight;
        canvas.height = parseInt($candleChartDiv.css('height')) + parseInt($barChartDiv.css('height'));
        $(canvas).css({
            position: 'absolute',
            left: chartAreaLeft,
            top: candleChartAreaTop
        });
        ctx = canvas.getContext("2d");
        ctx.lineWidth = 0.5;
        ctx.strokeStyle = "#555";
        $('.graphic').mousemove(drawCrosshair);
    });
}

function drawCrosshair(e) {
    var pos = $(this).offset();
    var elem_left = pos.left;
    var elem_top = pos.top;
    /**/
    var Xinner = e.pageX - elem_left;
    var Yinner = e.pageY - elem_top;
    /**/
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.beginPath();
    ctx.moveTo(Xinner - chartAreaLeft - 1, 0);
    ctx.lineTo(Xinner - chartAreaLeft - 1, canvas.height - barChartAreaBottom);
    ctx.stroke();
    var y = Math.min(candleChartAreaHeight-candleChartAreaTop-candleChartAreaBottom, Yinner - candleChartAreaTop);
    ctx.moveTo(0, y);
    ctx.lineTo(canvas.width, y);
    ctx.stroke();
    ctx.closePath();
    /**/
    var candle = findCandle(Xinner);
    if (candle) {
        $('#candle-open').html('open: <span>' + candle.open + '</span>');
        $('#candle-close').html('close: <span>' + candle.close + '</span>');
        $('#candle-low').html('low: <span>' + candle.low + '</span>');
        $('#candle-high').html('high: <span>' + candle.high + '</span>');
        $('#candle-volume').html('volume: <span>' + candle.volume + '</span>');
        $('#candle-date').html('<span></span>' +
        '<span class="date">' + candle.beginDate.split(' ')[0] + '</span>' +
        ' ' + candle.beginDate.split(' ')[1] +
        ' - ' +
        ((candle.beginDate.split(' ')[0] === candle.endDate.split(' ')[0]) ? '' : '<span class="date">' + candle.endDate.split(' ')[0] + '</span>') +
        ' ' + candle.endDate.split(' ')[1]);
    }
}

/*CANDLE*/

function prepareCandleData(queryResultArray) {
    var chartDataArray = [];
    for (var i = 1; i < queryResultArray.length; i++) {
        queryResultArray[i][0] = queryResultArray[i][0].replace('T', ' ');
        queryResultArray[i][1] = queryResultArray[i][1].replace('T', ' ');
        /*html for tooltip*/
        var html = getCandleHtml(
            /*data for tip:*/
            {
                beginDate: queryResultArray[i][0],
                endDate: queryResultArray[i][1]
            },
            {
                low: queryResultArray[i][4],
                open: queryResultArray[i][2],
                close: queryResultArray[i][3],
                high: queryResultArray[i][5],
                volume: queryResultArray[i][6]
            },
            /*titles for tip data:*/
            {
                date: queryResultArray[i][7],
                rate: queryResultArray[i][8],
                volume: queryResultArray[i][9]
            }
        );
        /*skip first row - BackDealInterval is there*/
        chartDataArray[i - 1] = [queryResultArray[i][1], queryResultArray[i][4], queryResultArray[i][2], queryResultArray[i][3], queryResultArray[i][5],
            /*html for tip*/
            html];
        /*store html in queryResultArray. For findCandle()*/
        queryResultArray[i][queryResultArray[i].length] = html;
    }
    if (chartDataArray.length == 0) {
        chartDataArray[0] = ['', 0, 0, 0, 0, getCandleHtml()];
    }
    return chartDataArray;
}

function prepareCandleOptions() {
    return {
        height: candleChartAreaHeight,
        chartArea: {
            left: chartAreaLeft,
            right: chartAreaRight,
            top: candleChartAreaTop,
            bottom: candleChartAreaBottom,
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
            gridlines: {count: 10, color: 'transparent'},
            baselineColor: 'transparent',
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
        candlestick: {
            fallingColor: {
                fill: "#9C1616",
                stroke: "#600E0E",
                strokeWidth: 1
            },
            risingColor: {
                fill: "#F8CEC8",
                stroke: "#F0F5F5",
                strokeWidth: 1
            }
        },
        enableInteractivity: false, /*to avoid tip on hover event*/
        colors: ['#65180a'],
        tooltip: {isHtml: true},
        series: {0: {type: "candlesticks"}},
        legend: "none"
    };
}

function drawCandleChart(chartDataArray, options, chartPlace) {
    try {
        var chart = new google.visualization.ComboChart(document.getElementById(chartPlace));
        var dataTable = new google.visualization.DataTable();
        dataTable.addColumn('string', '');
        dataTable.addColumn('number', '');
        dataTable.addColumn('number', '');
        dataTable.addColumn('number', '');
        dataTable.addColumn('number', '');
        dataTable.addColumn({'type': 'string', 'role': 'tooltip', 'p': {'html': true}});
        dataTable.addRows(chartDataArray);
        /**/
        /*'select' event will not be triggered because enableInteractivity: false'*/
        google.visualization.events.addListener(chart, 'click', showTooltip);
        /**/
        chart.draw(dataTable, options);
        candleDataTable = dataTable;
        candleChart = chart;

    } catch (e) {
        console.log(e);
    }
}

function showTooltip(e){
    var candle = findCandle(e.x);
    if (candle) {
        $('.candle-chart-tip')
            .remove();
        console.log($(candle.html));
        $('.candle-chart-tip-wrapper')
            .append($(candle.html))
            .css({
                position: 'absolute',
                'z-index': 100,
                visibility: 'hidden'
            })
            .find('.candle-chart-tip')
            .on('click', function () {
                $(this).remove();
            });
        var left = e.x;
        var top = e.y;
        var height = parseInt($('.candle-chart-tip-wrapper').css('height'));
        var width = parseInt($('.candle-chart-tip-wrapper').css('width'));
        var containerHeight = parseInt($('.graphic').css('height'));
        var containerWidth = parseInt($('.graphic').css('width'));
        left = Math.max(0, left + width < containerWidth ? left + 10 : left - width - 10);
        top = Math.max(10, top + height < containerHeight ? top + 4 : top - height - 10);
        $('.candle-chart-tip-wrapper').css({
            left: left,
            top: top,
            visibility: 'visible'
        });
    }
}

function getCandleHtml(date, value, title) {
    var html;
    if (!date) {
        html = '<div class="area-chart-tip candle-chart-tip">' +
        '<p>' + '<span>' + $('#noData').text() + '</span>' + '</p>' +
        '</div>';
    } else {
        html = '<div class="area-chart-tip candle-chart-tip">' +
        '<div class="cross-close">Х</div>'+
        //'<p class="area-chart-tip__date">' + '<span>' + title.date + '</span>' + ': ' + date.endDate + '</p>' +
        '<p class="area-chart-tip__date">' + date.beginDate+'</br>'+date.endDate + '</p>' +
        '<p class="area-chart-tip__rate">' + '<span>' + title.rate + ':' + '</span></p>' +
        '<p class="area-chart-tip__candle">' + '<span>open</span>' + ': ' + value.open + '</p>' +
        '<p class="area-chart-tip__candle">' + '<span>close</span>' + ': ' + value.close + '</p>' +
        '<p class="area-chart-tip__candle">' + '<span>low</span>' + ': ' + value.low + '</p>' +
        '<p class="area-chart-tip__candle">' + '<span>high</span>' + ': ' + value.high + '</p>' +
        '</br>' +
        '<p class="area-chart-tip__rate">' + '<span>' + title.volume + '</span>' + ': ' + value.volume + '</p>' +
        '</div>';
    }
    return html;
}

/*find candle at the x-coordinate on chart and return corresponding object*/
function findCandle(x) {
    var result;
    var $rectList = $('#candle-chart_div g g g g').find('rect');
    [].some.call($rectList, function (e, idx) {
        var $e = $(e);
        if (x >= parseInt($e.attr('x')) && (x <= parseInt($e.attr('x')) + parseInt($e.attr('width')))) {
            var candle = queryResultArray[$e.parent().index() + 1]; //data begin from index 1 (in [0] - BackDealInterval info)
            result = {
                beginDate: candle[0],
                endDate: candle[1],
                low: candle[4],
                open: candle[2],
                close: candle[3],
                high: candle[5],
                volume: candle[6],
                html: candle[10]
            };
            return true;
        }
    });
    return result;
}

/*BAR*/
function prepareBarData(queryResultArray) {
    var chartDataArray = [];
    for (var i = 1; i < queryResultArray.length; i++) {
        /*skip first row - BackDealInterval is there*/
        chartDataArray[i - 1] = [queryResultArray[i][1], queryResultArray[i][6],
            /*returns html for tip*/
            getBarHtml(
                /*data for tip:*/
                {
                    beginDate: queryResultArray[i][0],
                    endDate: queryResultArray[i][1]
                },
                {
                    volume: queryResultArray[i][6]
                },
                /*titles for tip data:*/
                {
                    date: queryResultArray[i][7],
                    rate: queryResultArray[i][8],
                    volume: queryResultArray[i][9]
                }
            )];
    }
    if (chartDataArray.length == 0) {
        chartDataArray[0] = ['', 0, getBarHtml()];
    }
    return chartDataArray;
}

function prepareBarOptions() {
    return {
        height: 70,
        chartArea: {
            left: chartAreaLeft,
            right: chartAreaRight,
            top: barChartAreaTop,
            bottom: barChartAreaBottom,
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
            gridlines: {count: 0},
            baselineColor: 'transparent'
        },
        hAxis: {
            textStyle: {
                color: '#74210D',
                fontSize: 11
            },
            allowContainerBoundaryTextCufoff: true,
            slantedText: false,
            minTextSpacing: '2%'
        },
        enableInteractivity: false,
        colors: ['#65180a'],
        tooltip: {isHtml: true},
        series: {0: {type: "bars", targetAxisIndex: 0, color: "#ebebeb"}},
        legend: "none"
    };
}

function drawBarChart(chartDataArray, options, chartPlace) {
    try {
        var chart = new google.visualization.ComboChart(document.getElementById(chartPlace));
        var dataTable = new google.visualization.DataTable();
        dataTable.addColumn('string', '');
        dataTable.addColumn('number', '');
        dataTable.addColumn({'type': 'string', 'role': 'tooltip', 'p': {'html': true}});
        dataTable.addRows(chartDataArray);
        chart.draw(dataTable, options);
    } catch (e) {
        console.log(e);
    }
}

function getBarHtml(date, value, title) {
    var html;
    if (!date) {
        html = '<div class="area-chart-tip">' +
        '<p>' + '<span>' + $('#noData').text() + '</span>' + '</p>' +
        '</div>';
    } else {
        html = '<div class="area-chart-tip">' +
        '<p class="area-chart-tip__date">' + '<span>' + title.date + '</span>' + ': ' + date.endDate + '</p>' +
        '<p class="area-chart-tip__rate">' + '<span>' + title.volume + '</span>' + ': ' + value.volume + '</p>' +
        '</div>';
    }
    return html;
}