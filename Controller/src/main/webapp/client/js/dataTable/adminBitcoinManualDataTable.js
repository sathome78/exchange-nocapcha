var $bitcoinRequestsTable;
var bitcoinRequestsDataTable;

$(document).ready(function () {

    var acceptButtonLocMessage = $('#acceptButtonLocMessage').text();
    $bitcoinRequestsTable = $('#btc_invoice_requests');
    var url = '/2a8fy7b07dxe44/bitcoinRequests';
    function updateBitcoinTable() {
        if ($.fn.dataTable.isDataTable('#btc_invoice_requests')) {
            bitcoinRequestsDataTable = $($bitcoinRequestsTable).DataTable();
            bitcoinRequestsDataTable.ajax.url(url).load();
        } else {
            bitcoinRequestsDataTable = $($bitcoinRequestsTable).DataTable({
                "ajax": {
                    "url": url,
                    "dataSrc": ""
                },
                "paging": true,
                "info": true,
                "columns": [
                    {
                        "data": "datetime",
                        "render": function (data) {
                            return data.replace(' ', '<br/>');
                        },
                        "className": "text-center"
                    },
                    {
                        "data": "invoiceId",
                        "render": function (data, type, row) {
                            return '<button class="address-ref" onclick="alert(\'' + row.address +'\')">' + data +' </button>';
                        }
                    },
                    {
                        "data": "userId",
                        "render": function (data, type, row) {
                            return '<a href="/2a8fy7b07dxe44/userInfo?id=' + data + '">' + row.userEmail + '</a>';
                        }
                    },
                    {
                        "data": "pendingPaymentStatus"
                    },
                    {
                        "data": "amount",
                        "render": function (data) {
                            return numeral(data).format('0.00[000000]');
                        }
                    },
                    {
                        "data": "commissionAmount",
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

    updateBitcoinTable();

});

