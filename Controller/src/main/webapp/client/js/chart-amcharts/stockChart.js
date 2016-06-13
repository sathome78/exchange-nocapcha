/**
 * Created by Valk on 15.06.2016.
 */

function StockChartAmchartsClass() {
    if (StockChartAmchartsClass.__instance) {
        return StockChartAmchartsClass.__instance;
    } else if (this === window) {
        return new StockChartAmchartsClass(chart);
    }
    StockChartAmchartsClass.__instance = this;

    var that = this;
    var backDealInterval;
    var candleDataSourceUrl = '/dashboard/chartArray/candle';
    var candleDataSet;
    var barDataSet;
    var stockChartDivId = 'amcharts-stock_chart_div';
    var chartData = [];

    var chart = null;

    this.draw = function () {
        $.get(candleDataSourceUrl, function (queryResultArray) {
                backDealInterval = queryResultArray[0][0]; //BackDealInterval is here
                queryResultArray.splice(0, 1);
                chartData.length = 0;
                queryResultArray.forEach(function (item, i) {
                    //if (item[6] != 0)
                    chartData.push({
                        preddate: new Date(item[0]),
                        date: new Date(item[1]),
                        open: item[2],
                        close: item[3],
                        high: item[5],
                        low: item[4],
                        volume: item[6],
                        value: item[6]
                    });
                });
                switch (backDealInterval.intervalType) {
                    case 'MONTH':  {chart.categoryAxesSettings.minPeriod = "DD"; break;}
                    case 'DAY':  {chart.categoryAxesSettings.minPeriod = "hh"; break;}
                    case 'HOUR':  {chart.categoryAxesSettings.minPeriod = "mm"; break;}
                }

                if (!chart.div) {
                    chart.write(stockChartDivId);
                } else {
                    chart.validateData();
                }
            }
        );
    };

    /*=================================================*/
    (function init() {
        chart = new AmCharts.AmStockChart();
        /*CANDLE*/
        candleDataSet = new AmCharts.DataSet();
        candleDataSet.fieldMappings = [
            {
                fromField: "preddate",
                toField: "preddate"
            },
            {
                fromField: "date",
                toField: "date"
            },
            {
                fromField: "open",
                toField: "open"
            }, {
                fromField: "close",
                toField: "close"
            }, {
                fromField: "high",
                toField: "high"
            }, {
                fromField: "low",
                toField: "low"
            }, {
                fromField: "volume",
                toField: "volume"
            }];
        candleDataSet.color = "#7f8da9";
        candleDataSet.dataProvider = chartData;
        candleDataSet.title = "";
        candleDataSet.categoryField = "date";

        /*BAR*/
        barDataSet = new AmCharts.DataSet();
        barDataSet.fieldMappings = [{
            fromField: "volume",
            toField: "volume"
        }];
        barDataSet.color = "#fac314";
        barDataSet.dataProvider = chartData;
        barDataSet.compared = false;
        barDataSet.title = "";
        barDataSet.categoryField = "date";
        /**/
        chart.dataSets = [candleDataSet, barDataSet];
        /**/

        var categoryAxesSettings = new AmCharts.CategoryAxesSettings();
        categoryAxesSettings.equalSpacing = true;
        categoryAxesSettings.maxSeries = 0;
        categoryAxesSettings.dateFormats =
            [{period: 'fff', format: 'JJ:NN:SS'},
                {period: 'ss', format: 'JJ:NN:SS'},
                {period: 'mm', format: 'DD.MM JJ:NN'},
                {period: 'hh', format: 'DD.MM JJ:NN'},
                {period: 'DD', format: 'MMM DD'},
                {period: 'WW', format: 'MMM DD'},
                {period: 'MM', format: 'MMM'},
                {period: 'YYYY', format: 'YYYY'}];
        chart.categoryAxesSettings = categoryAxesSettings;
        chart.addListener("dataUpdated", function(e){
            chart.zoomOut();
        });

        /*PANEL 1*/
        var stockPanel = new AmCharts.StockPanel();
        stockPanel.title = "";
        stockPanel.showCategoryAxis = true;
        stockPanel.percentHeight = 80; /*%*/

        var valueAxis = new AmCharts.ValueAxis();
        valueAxis.dashLength = 5;
        valueAxis.precision = 9;
        valueAxis.labelsEnabled = false;
        valueAxis.gridCount = 3;
        stockPanel.addValueAxis(valueAxis);

        stockPanel.categoryAxis.dashLength = 5;

        var graph = new AmCharts.StockGraph();
        graph.type = "candlestick";
        graph.openField = "open";
        graph.closeField = "close";
        graph.highField = "high";
        graph.lowField = "low";
        graph.valueField = "high";
        graph.lineColor = "#7f8da9";
        graph.fillColors = "#7f8da9";
        graph.negativeLineColor = "#db4c3c";
        graph.negativeFillColors = "#db4c3c";
        graph.proCandlesticks = true;
        graph.fillAlphas = 1;
        graph.useDataSetColors = false;
        //graph.comparable = true;
        //graph.compareField = "value";
        graph.showBalloon = true;
        graph.balloonFunction = getBalloonText;
        stockPanel.addStockGraph(graph);
        /*var stockLegend = new AmCharts.StockLegend();
         stockLegend.valueTextRegular = undefined;
         stockLegend.periodValueTextComparing = "[[percents.value.close]]%";
         stockPanel.stockLegend = stockLegend;*/
        var chartCursor = new AmCharts.ChartCursor();
        chartCursor.valueLineEnabled = true;
        chartCursor.valueLineAxis = valueAxis;
        chartCursor.valueLineBalloonEnabled = true;
        stockPanel.chartCursor = chartCursor;
        chart.panels = [stockPanel];
        /*PANEL 2*/
        if (false) {
            var stockPanel2 = new AmCharts.StockPanel();
            stockPanel2.title = "";
            stockPanel2.percentHeight = 20;
            /*%*/
            stockPanel2.marginTop = 1;
            stockPanel2.showCategoryAxis = true;
            var valueAxis2 = new AmCharts.ValueAxis();
            valueAxis2.labelsEnabled = false;
            valueAxis2.autoGridCount = false;
            valueAxis2.gridAlpha = 0;
            valueAxis2.dashLength = 5;
            stockPanel2.addValueAxis(valueAxis2);
            stockPanel2.categoryAxis.dashLength = 5;
            stockPanel2.categoryAxis.markPeriodChange = true;
            stockPanel2.categoryAxis.boldPeriodBeginning = true;
            var graph2 = new AmCharts.StockGraph();
            graph2.valueField = "volume";
            graph2.type = "column";
            graph2.showBalloon = true;
            graph2.fillAlphas = 1;
            stockPanel2.addStockGraph(graph2);
            /*var legend2 = new AmCharts.StockLegend();
             legend2.markerType = "none";
             legend2.markerSize = 0;
             legend2.labelText = "";
             legend2.periodValueTextRegular = "[[value.close]]";
             stockPanel2.stockLegend = legend2;*/
            var chartCursor2 = new AmCharts.ChartCursor();
            chartCursor2.valueLineEnabled = true;
            chartCursor2.valueLineAxis = valueAxis2;
            chartCursor2.valueBalloonsEnabled = false;
            chartCursor2.categoryBalloonEnabled = true;
            chartCursor2.valueLineEnabled = true;
            chartCursor2.valueLineBalloonEnabled = true;
            chartCursor2.bulletsEnabled = true;
            stockPanel2.chartCursor = chartCursor2;
            chart.panels.push(stockPanel2);
        }
        /**/
        /*SCROLL BAR*/
        var graphForScrollBar = new AmCharts.StockGraph();
        var sbsettings = new AmCharts.ChartScrollbarSettings();
        sbsettings.graph = new AmCharts.StockGraph();
        sbsettings.graph.valueField = "volume";
        sbsettings.graphType = "column";
        sbsettings.height=45;
        //sbsettings.usePeriod = "hh";
        sbsettings.updateOnReleaseOnly = false;
        chart.chartScrollbarSettings = sbsettings;
        /*PERIOD SELECTOR*/
        periodSelector = new AmCharts.PeriodSelector();
        periodSelector.position = "bottom";
        periodSelector.periods = [{
            period: "DD",
            count: 10,
            label: "10 days"
        }, {
            period: "MM",
            count: 1,
            label: "1 month"
        }, {
            period: "YYYY",
            count: 1,
            label: "1 year"
        }, {
            period: "YTD",
            label: "YTD"
        }, {
            selected: true,
            period: "MAX",
            label: "MAX"
        }];
        //chart.periodSelector = periodSelector;
    })();

    function getBalloonText(graphDataItem, graph) {
        var html;
        if (graphDataItem.dataContext.volume == 0) {
            html = '<div class="area-chart-tip candle-chart-tip">' +
            '<p class="area-chart-tip__stock">' + graphDataItem.dataContext.preddate.toLocaleString() + '</br>' + '' + '</p>' +
            '<p class="area-chart-tip__stock">' + graphDataItem.dataContext.date.toLocaleString() + '</br>' + '' + '</p>' +
            '</div>';
        } else {
            html = '<div class="area-chart-tip candle-chart-tip">' +
            '<p class="area-chart-tip__stock">' + graphDataItem.dataContext.preddate.toLocaleString() + '</br>' + '' + '</p>' +
            '<p class="area-chart-tip__stock">' + graphDataItem.dataContext.date.toLocaleString() + '</br>' + '' + '</p>' +
            '<p class="area-chart-tip__stock">' + '<span>open</span>' + ': ' + graphDataItem.dataContext.open + '</p>' +
            '<p class="area-chart-tip__stock">' + '<span>close</span>' + ': ' + graphDataItem.dataContext.close + '</p>' +
            '<p class="area-chart-tip__stock">' + '<span>low</span>' + ': ' + graphDataItem.dataContext.low + '</p>' +
            '<p class="area-chart-tip__stock">' + '<span>high</span>' + ': ' + graphDataItem.dataContext.high + '</p>' +
            '</br>' +
            '<p class="area-chart-tip__stock">' + '<span>volume</span>' + ': ' + graphDataItem.dataContext.volume + '</p>' +
            '</div>';
        }
        return html;
    }

    function getAxisLabel(valueText, date, categoryAxis) {
        return new Date();
    }

}