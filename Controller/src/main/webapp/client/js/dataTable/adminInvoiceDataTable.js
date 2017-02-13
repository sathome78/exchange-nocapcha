/**
 * Created by ogolv on 26.07.2016.
 */
var invoiceRequestsDataTable;

$(document).ready(function () {
    var acceptLocMessage = $('#acceptLocMessage').text();
    var declineLocMessage = $('#declineLocMessage').text();
    var acceptedLocMessage = $('#acceptedLocMessage').text();
    var declinedLocMessage = $('#declinedLocMessage').text();
    var onConfirmationLocMessage = $('#onConfirmationLocMessage').text();
    var cancelledByUserLocMessage = $('#cancelledByUserLocMessage').text();
    var timeOutExpiredLocMessage = $('#timeOutExpiredLocMessage').text();
    /**/
    var $invoiceRequestsTable = $('#invoice_requests');

    var url = '/2a8fy7b07dxe44/invoiceRequests/';

    if ($.fn.dataTable.isDataTable('#invoice_requests')) {
        invoiceRequestsDataTable = $($invoiceRequestsTable).DataTable();
        invoiceRequestsDataTable.ajax.url(url).load();
    } else {
        invoiceRequestsDataTable = $($invoiceRequestsTable).DataTable({
            "ajax": {
                "url": url,
                "dataSrc": ""
            },
            "paging": true,
            "info": true,
            "columns": [
                {
                    "data": "transaction.id",
                    "render": function (data, type, row) {
                        return '<button class="acceptbtn" onclick="viewDetailedInvoiceInfo(this)">' +
                            data + '</button>';
                    }
                },
                {
                    "data": "transaction.datetime",
                    "render": function (data) {
                        return data.replace(' ', '<br/>');
                    },
                    "className": "text-center"
                },
                {
                    "data": "userId",
                    "render": function (data, type, row) {
                        return '<a href="/2a8fy7b07dxe44/userInfo?id=' + data + '">' + row.userEmail + '</a>'
                    }
                },
                {
                    "data": "transaction.currency.name"
                },
                {
                    "data": "transaction.amount"
                },
                {
                    "data": "transaction.commissionAmount"
                },
                {
                    "data": "invoiceBank",
                    "render": function (data) {
                        return data ? data.name : '-';
                    }
                },
                {
                    "data": "payerBankName",
                    "render": function (data) {
                        return data ? data : '-';
                    },
                    "className": "text-center"
                },
                {
                    "data": "acceptanceTime",
                    "render": function (data) {
                        return data ? data.replace(' ', '<br/>') : '-';
                    },
                    "className": "text-center"
                },
                {
                    "data": "invoiceRequestStatus",
                    "render": function (data, type, row) {
                        if (data === "CREATED_USER") {
                            return onConfirmationLocMessage;
                        } else if (data === "ACCEPTED_ADMIN") {
                            return acceptedLocMessage;
                        } else if (data === "DECLINED_ADMIN") {
                            return declinedLocMessage;
                        } else if (data === "CANCELLED_USER") {
                            return cancelledByUserLocMessage;
                        } else if (data === "EXPIRED") {
                            return timeOutExpiredLocMessage;
                        } else if (data === "CONFIRMED_USER") {
                            return '<div class="table-button-block" style="white-space: nowrap">' +
                                '<button style="font-size: 11px;" class="table-button-block__button btn btn-success" onclick="acceptInvoice(event,' + row.transaction.id + ')">' +
                                acceptLocMessage +
                                '</button>' +
                                '&nbsp;' +
                                '<button style="font-size: 11px;" class="table-button-block__button btn btn-danger" onclick="declineInvoice(event,' + row.transaction.id + ')">' +
                                declineLocMessage +
                                '</button>' +
                                '</div>';
                        } else {
                            return "unsupported value of field"
                        }
                    },
                    "className": "text-center"
                },
                {
                    "data": "acceptanceUserId",
                    "render": function (data, type, row) {
                        return data ? '<a href="/2a8fy7b07dxe44/userInfo?id=' + data + '">' + row.acceptanceUserEmail + '</a>' : '-';
                    },
                    "className": "text-center"
                }
            ],
            "order": []
        });
    }


});

function viewDetailedInvoiceInfo(elem) {
    var $row = $(elem).parents('tr');
    var rowData = invoiceRequestsDataTable.row($row).data();
    fillInvoiceInfoModal(rowData);
    $('#invoice-info-modal').modal();
}

function fillInvoiceInfoModal(rowData) {
    $('#info-currency').text(rowData.transaction.currency.name);
    $('#info-amount').text(rowData.transaction.amount);
    $('#info-commissionAmount').text(rowData.transaction.commissionAmount);
    var bankName = rowData.invoiceBank ? rowData.invoiceBank.name : '-';
    var bankAccount = rowData.invoiceBank ? rowData.invoiceBank.accountNumber : '-';
    var bankRecipient = rowData.invoiceBank ? rowData.invoiceBank.recipient : '-';
    $('#info-bankName').text(bankName);
    $('#info-bankAccount').text(bankAccount);
    $('#info-bankRecipient').text(bankRecipient);
    $('#info-bankFrom').text(replaceAbsentWithDash(rowData.payerBankName));
    $('#info-userAccount').text(replaceAbsentWithDash(rowData.payerAccount));
    $('#info-userFullName').text(replaceAbsentWithDash(rowData.userFullName));
    $('#info-remark').find('textarea').html(replaceAbsentWithDash(rowData.remark));
}

function replaceAbsentWithDash(value) {
    return value ? value : '-';
}