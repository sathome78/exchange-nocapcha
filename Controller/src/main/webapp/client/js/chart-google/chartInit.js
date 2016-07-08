/**
 * Created by Valk on 09.05.2016.
 */

function ChartGoogleClass() {
    if( $('.google-graphics').css('display') == 'none'){
        throw new Error('google chart is switched off');
    }
    if (ChartGoogleClass.__instance) {
        return ChartGoogleClass.__instance;
    } else if (this === window) {
        return new ChartGoogleClass();
    }
    ChartGoogleClass.__instance = this;

    var that = this;
    var chartType;
    var areaChart = new ChartAreaClass();
    var candleChart = new ChartCandleClass();

    this.init = function (chart) {
        chartType = chart;
        $('.period-menu__item').on('click', setPeriod);
        $('.chart-type-menu__item').on('click', setChart);
        google.charts.setOnLoadCallback(that.drawChart);
    };

    this.drawChart = function () {
        switch (chartType) {
            case 'CANDLE':
            {
                areaChartSwitch(false);
                candleChartSwitch(true);
                /**/
                candleChart.draw();
                break;
            }
            case 'AREA':
            {
                candleChartSwitch(false);
                areaChartSwitch(true);
                /**/
                areaChart.draw();
                break;
            }
        }
    };

    function setPeriod() {
        var period;
        switch ($(this).attr('id')) {
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
        }
        syncCurrentParams(null, period, null, null, function (data) {
            $(this).siblings().removeClass('active');
            $(this).siblings().toggleClass('active');
            trading.getAndShowStatisticsForCurrency();
            that.drawChart(data.chartType);
        });
    }

    function setChart() {
        switch ($(this).attr('id')) {
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
        }
        syncCurrentParams(null, null, chartType, null, function (data) {
            $(this).siblings().removeClass('active');
            $(this).siblings().toggleClass('active');
            chartType = data.chartType;
            that.drawChart();
        });
    }

    function candleChartSwitch(state) {
        $('#candle-description').toggle(state);
        $('#candle-chart-tip-wrapper').toggle(state);
        $('#graphic-canvas').toggle(state);
        $('#candle-chart_div').toggle(state);
        $('#bar-chart_div').toggle(state);
    }

    function areaChartSwitch(state) {
        $('#area-chart_div').toggle(state);
    }
}