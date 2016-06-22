/**
 * Created by Valk on 09.05.2016.
 */

function ChartAmchartsClass(type, period) {
    if ($('.amcharts-graphics').css('display') == 'none') {
        throw new Error('Amcharts chart is switched off');
    }
    if (ChartAmchartsClass.__instance) {
        return ChartAmchartsClass.__instance;
    } else if (this === window) {
        return new ChartAmchartsClass(chart);
    }
    ChartAmchartsClass.__instance = this;

    var that = this;

    this.chartType = null;

    var stockChart;

    this.drawChart = function () {
        switch (that.chartType) {
            case 'STOCK':
            {
                stockChart.draw();
                break;
            }
        }
    };

    /*==========================================*/
    (function init(type, period) {
        stockChart = new StockChartAmchartsClass();
        that.chartType = type;
        $('.period-menu__item').on('click', setPeriod);
        $('.chart-type-menu__item').on('click', setChart);
        syncButtonToPeriod(period);
    })(type, period);

    function setPeriod() {
        var period;
        var button = this;
        switch ($(button).attr('id')) {
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
        syncCurrentParams(null, period, null, function (data) {
            $(button).siblings().removeClass('active');
            $(button).toggleClass('active');
            trading.getAndShowStatisticsForCurrency();
            that.drawChart(data.chartType);
        });
    }

    function syncButtonToPeriod(period){
        var id = period.replace(/\s/g, '').toLowerCase();
        $('.period-menu__item').removeClass('active');
        $('#' + id).addClass('active');
    }

    function setChart() {
        var button = this;
        switch ($(button).attr('id')) {
            case 'stock':
            {
                chartType = 'STOCK';
                break;
            }
        }
        syncCurrentParams(null, null, chartType, function (data) {
            $(button).siblings().removeClass('active');
            $(button).toggleClass('active');
            chartType = data.chartType;
            that.drawChart();
        });
    }
}