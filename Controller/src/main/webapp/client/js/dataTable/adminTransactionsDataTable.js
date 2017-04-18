var transactionsDataTable;

$(function () {


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
        format: 'YYYY-MM-DD HH:mm',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang: 'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });
    $('#datetimepicker_end').datetimepicker({
        format: 'YYYY-MM-DD HH:mm',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang: 'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });


    if ($.fn.dataTable.isDataTable('#transactionsTable')) {
        transactionsDataTable = $('#transactionsTable').DataTable();
    } else {
        var id = $("#user-id").val();
        transactionsDataTable = $('#transactionsTable').DataTable({
            "serverSide": true,
            "ajax": {
                "url": '/2a8fy7b07dxe44/transactions?id=' + id,
                "dataSrc": "data"
            },
            "paging": true,
            "info": true,
            "bFilter": false,
            "order": [[0, "desc"]],
            "columns": [
                {
                    "data": "datetime",
                    "name":"TRANSACTIOIN.datetime",
                    "render": function (data, type, row) {
                        if (type == 'display') {
                            return data.split(' ')[0];
                        }
                        return data;
                    }
                },
                {
                    "data": "datetime",
                    "render": function (data, type, row) {
                        if (type == 'display') {
                            return data.split(' ')[1];
                        }
                        return data;
                    },
                    "orderable": false
                },
                {
                    "data": "operationType",
                    "name":"TRANSACTIOIN.operation_type_id"
                },
                {
                    "data": "status",
                    "name":"TRANSACTION.provided"
                },
                {
                    "data": "currency",
                    "name":"TRANSACTION.currency_id"

                },
                {
                    "data": "amount",
                    "name":"TRANSACTION.amount",
                    "render": function (data, type, row) {
                        if (type == 'display') {
                            return numeral(data).format('0.00[000000]');
                        }
                        return data;
                    }
                },
                {
                    "data": "commissionAmount",
                    "name":"TRANSACTION.commission_amount",
                    "render": function (data, type, row) {
                        if (type == 'display') {
                            return numeral(data).format('0.00[000000]');
                        }
                        return data;
                    }
                },
                {
                    "data": "merchant.description",
                    "name":"MERCHANT.description"
                },
                {
                    "data": "sourceId",
                    "render": function (data, type, row) {
                        if (data) {
                            return '<button class="transactionlist-order-detail-button"' +
                                ' data-sourceType=' + row.sourceType +
                                ' data-sourceId=' + data + '>' +
                                data +
                                '</button>';
                        } else {
                            return '';
                        }
                    }
                }
            ],
            "searchDelay": 1000
        });
        $('#transactionsTable tbody').on('click', '.transactionlist-order-detail-button', function () {
            var row = transactionsDataTable.row($(this).parents('tr'));
            getSourceTypeDetailedInfo($(this).data("sourcetype"), $(this).data("sourceid"));
        });

        function getSourceTypeDetailedInfo(sourceType, sourceId) {
            if (sourceType === "USER_TRANSFER") {
                getTransferDetailedInfo(sourceId);
            } else if (sourceType === "ORDER") {
                getOrderDetailedInfo(null, sourceId, false);
            } else if (sourceType === "WITHDRAW") {
                getWithdrawDetailedInfo(sourceId);
            }
        }

        function getTransferDetailedInfo(orderId) {
            $.ajax({
                url: '/2a8fy7b07dxe44/transferInfo?id=' + orderId,
                type: 'GET',
                success: function (data) {
                    $("#info-date").html(data.creationDate);
                    $("#info-currency").html(data.currencyName);
                    $("#info-amount").html(numeral(data.amount).format('0.00[000000]'));
                    $("#info-userFrom").html("<a href='mailto:" +  data.userFromEmail + "'>" + data.userFromEmail + "</a>");
                    $("#info-userTo").html("<a href='mailto:" + data.userToEmail + "'>" + data.userToEmail + "</a>");
                    $("#info-commissionAmount").html(numeral(data.comission).format('0.00[000000]'));
                    $('#user_transfer_info_modal').modal();
                }
            });
        }

        function getWithdrawDetailedInfo(id) {
            $.ajax({
                url: '/2a8fy7b07dxe44/withdraw/info?id=' + id,
                type: 'GET',
                success: function (data) {
                    var $modal = $('#withdraw-info-modal');
                    $modal.find('#info-currency').text(data.currencyName);
                    $modal.find('#info-amount').text(data.amount);
                    $modal.find('#info-commissionAmount').text(data.commissionAmount);
                    var recipientBank = data.recipientBankName ? data.recipientBankName : '';
                    var recipientBankCode = data.recipientBankCode ? data.recipientBankCode : '';
                    var userFullName = data.userFullName ? data.userFullName : '';
                    $modal.find('#info-bankRecipient').text(recipientBank + ' ' + recipientBankCode);
                    $modal.find('#info-status').text(data.status);
                    $modal.find('#info-status-date').text(data.statusModificationDate);
                    $modal.find('#info-wallet').text(data.wallet);
                    $modal.find('#info-userFullName').text(data.userFullName);
                    $modal.find('#info-remark').find('textarea').html(data.remark);
                    $modal.modal();
                }
            });
        }

    }

    $('#filter-apply').on('click', function (e) {
        e.preventDefault();
        reloadTable();
    });

    $('#filter-reset').on('click', function (e) {
        e.preventDefault();
        $('#transaction-search-form')[0].reset();
        reloadTable();

    });

    function reloadTable() {
        var formParams = $('#transaction-search-form').serialize();
        var url = '/2a8fy7b07dxe44/transactions?id=' + $("#user-id").val() + '&' + formParams;
        transactionsDataTable.ajax.url(url).load();
    }


});

