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
        value: moment().subtract(1, 'days').format('YYYY-MM-DD HH:mm:ss')
    });

    console.log($filterForm.serialize());

    $.get('/2a8fy7b07dxe44/getCandleTableData?' + $filterForm.serialize(), function (data) {
        console.log(data);
    });

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
                "columns": [
                    {
                        "data": "beginDate"
                    },
                    {
                        "data": "endDate"/*,
                        "render": function (data, type, row) {
                            return '<button class="address-ref" onclick="alert(\'' + row.address +'\')">' + data +' </button>';
                        }*/
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
                    },
                    {
                        "data": "acceptanceTime",
                        "render": function (data) {
                            return data ? data.replace(' ', '<br/>') : '-';
                        },
                        "className": "text-center"
                    },
                    {
                        "data": "hash",
                        "render": function (data, type, row) {
                            var readonly = data == null ? '' : 'readonly';
                            var inputValue = data == null ? '' : data;
                            return '<input id="bitcoin_hash' + row.invoiceId +'" ' + readonly + ' value="' + inputValue + '" style="width: 130px" ' +
                                'class="form-control input-block-wrapper__input">';
                        }
                    },
                    {
                        "data": "hash",
                        "render": function (data, type, row) {
                            var readonly = data == null ? '' : 'readonly';
                            var totalAmount = row.amount + row.commissionAmount;
                            return '<input id="manual_amount'+ row.invoiceId +'" ' + readonly + ' value="' + numeral(totalAmount).format('0.00000000') + '" style="width: 130px"  maxlength="9" ' +
                                'class="form-control input-block-wrapper__input numericInputField">';
                        }
                    },
                    {
                        "data": "confirmation"
                    },
                    {
                        "data": "provided",
                        "render": function (data, type, row) {
                            if (data) {
                                return row.acceptanceUserId == null ? 'by service' : row.acceptanceUserEmail;
                            } else {
                                return '<button class="acceptbtn" type="submit" onclick="submitAcceptBitcoin(' + row.invoiceId +')">' + acceptButtonLocMessage + '</button>'
                            }
                        }
                    }
                ],
                "order": []
            });}
    }


});