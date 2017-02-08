/**
 * Created by ogolv on 26.07.2016.
 */
var invoiceRequestsDataTable;

$(document).ready(function () {
    var confirmButtonMessage = $('#confirmButtonLocMessage').text();
    var confirmedMessage = $('#confirmedLocMessage').text();
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
                    "data": "payeeBankName",
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
                    "data": "acceptanceTime",
                    "render": function (data, type, row) {
                        return data ? confirmedMessage : '<button class="acceptbtn" onclick="submitAcceptInvoice(event,' + row.transaction.id + ')">' +
                            confirmButtonMessage + '</button>';
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
    $('#info-bankFrom').text(replaceAbsentWithDash(rowData.payeeBankName));
    $('#info-userAccount').text(replaceAbsentWithDash(rowData.payeeAccount));
    $('#info-userFullName').text(replaceAbsentWithDash(rowData.userFullName));
    $('#info-remark').find('textarea').html(replaceAbsentWithDash(rowData.remark));
}

function replaceAbsentWithDash(value) {
    return value ? value : '-';
}