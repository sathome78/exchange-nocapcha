/**
 * Created by OLEG on 09.03.2017.
 */

var $candleTable;
var candleDataTable;

$(function () {
    $candleTable = $('#candle-table');
    var $filterForm = $('#candleFilterForm');

    $.datetimepicker.setDateFormatter({
        parseDate: function (date, format) {
            var d = moment(date, format);
            return d.isValid() ? d.toDate() : false;
        },

        formatDate: function (date, format) {
            return moment(date).format(format);
        }
    });

    $('#datetimepicker_start').datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang:'ru',
        defaultDate: new Date(),
        defaultTime: '00:00',
        value: moment().subtract(1, 'days').format('YYYY-MM-DD HH:mm:ss'),
        onChangeDateTime: function(current_time, $input){
            updateCandleTable();
            $($input).datetimepicker('hide');
        }
    });

    $('#currencyPair, #interval').on('change', updateCandleTable);



    function updateCandleTable() {
        var url = '/2a8fy7b07dxe44/getCandleTableData?' + $filterForm.serialize();

        if ($.fn.dataTable.isDataTable('#candle-table')) {
            candleDataTable = $($candleTable).DataTable();
            candleDataTable.ajax.url(url).load();
        } else {
            candleDataTable = $($candleTable).DataTable({
                "ajax": {
                    "url": url,
                    "dataSrc": ""
                },
                "paging": false,
                "info": false,
                "bFilter": false,
                "columns": [
                    {
                        "data": "beginDate",
                        "render": function (data) {
                            return moment(data).format('YYYY-MM-DD HH:mm:ss');
                        }
                    },
                    {
                        "data": "endDate",
                        "render": function (data) {
                            return moment(data).format('YYYY-MM-DD HH:mm:ss');
                        }
                    },
                    {
                        "data": "lowRate",
                        "render": function (data) {
                            return numeral(data).format('0.00[000000]');
                        }
                    },
                    {
                        "data": "highRate",
                        "render": function (data) {
                            return numeral(data).format('0.00[000000]');
                        }
                    },
                    {
                        "data": "openRate",
                        "render": function (data) {
                            return numeral(data).format('0.00[000000]');
                        }
                    },
                    {
                        "data": "closeRate",
                        "render": function (data) {
                            return numeral(data).format('0.00[000000]');
                        }
                    },
                    {
                        "data": "baseVolume",
                        "render": function (data) {
                            return numeral(data).format('0.00[000000]');
                        }
                    }
                ],
                "order": []
            });}
    }
    updateCandleTable();


});