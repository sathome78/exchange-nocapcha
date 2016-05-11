/**
 * Created by Valk on 09.05.2016.
 */

$(function () {
    $('.period-menu__item').on('click', redrawChart);
    $('.chart-type-menu__item').on('click', redrawChart);
    google.charts.setOnLoadCallback(redrawChart);
});

function redrawChart() {
    $(this).siblings().removeClass('active');
    var period = $('.period-menu__item.active');
    var chartType = $('.chart-type-menu__item.active');
    var id;
    /*SET PERIOD*/
    if (!period.is('div')) {
        id = $(this).attr('id');
    } else {
        id = period.attr('id');
    }
    switch (id) {
        case '12hour':
        {
            period = '12 HOUR';
            break;
        }
        case '24hour':
        {
            period = '24 HOUR';
            break;
        }
        case '7day':
        {
            period = '7 DAY';
            break;
        }
        case '1month':
        {
            period = '1 MONTH';
            break;
        }
        case '6month':
        {
            period = '6 MONTH';
            break;
        }
        default:
        {
            id = '24hour';
            period = '24 HOUR';
            break;
        }
    }
    $('#' + id).addClass('active');
    /*SET CHART*/
    if (!chartType.is('div')) {
        id = $(this).attr('id');
    } else {
        id = chartType.attr('id');
    }
    switch (id) {
        case 'candle':
        {
            chartType = 'CANDLE';
            break;
        }
        case 'area':
        {
            chartType = 'AREA';
            break;
        }
        default:
        {
            id = 'candle';
            chartType = 'CANDLE';
            break;
        }
    }
    $('#' + id).addClass('active');
    /**/
    drawChart(period, chartType);
    getStatisticsForCurrency(null, period);
}

function drawChart(period, chartType){
    if (! chartType) {
        chartType = 'CANDLE';
    }
    switch (chartType) {
        case 'CANDLE':
        {
            areaChartSwitch(false);
            candleChartSwitch(true);
            /**/
            drawChartCandle(period);
            break;
        }
        case 'AREA':
        {
            candleChartSwitch(false);
            areaChartSwitch(true);
            /**/
            drawChartArea(period);
            break;
        }
    }
}

function candleChartSwitch(state){
    $('#candle-description').toggle(state);
    $('#candle-chart-tip-wrapper').toggle(state);
    $('#graphic-canvas').toggle(state);
    $('#candle-chart_div').toggle(state);
    $('#bar-chart_div').toggle(state);
}

function areaChartSwitch(state){
    $('#area-chart_div').toggle(state);
}

function waiterSwitch(state){
    $('#graphic-wait').toggle(state);
}

function setActivePeriodSwitcherButton(backDealInterval) {
    var id = backDealInterval.intervalValue + backDealInterval.intervalType.toLowerCase();
    $('.period-menu__item').removeClass('active');
    $('#' + id).addClass('active');
}