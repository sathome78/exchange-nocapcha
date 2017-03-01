/**
 * Created by ogolv on 26.07.2016.
 */
var invoiceRequestsDataTable;
var $invoiceRequestsTable;
var amountChanging;
$(document).ready(function () {
    var acceptLocMessage = $('#acceptLocMessage').text();
    var declineLocMessage = $('#declineLocMessage').text();
    var acceptedLocMessage = $('#acceptedLocMessage').text();
    var declinedLocMessage = $('#declinedLocMessage').text();
    var onConfirmationLocMessage = $('#onConfirmationLocMessage').text();
    var revokedByUserLocMessage = $('#revokedByUserLocMessage').text();
    var timeOutExpiredLocMessage = $('#timeOutExpiredLocMessage').text();
    var changeAmountLocMessage = $('#changeAmountLocMessage').text();
    var cancelLocMessage = $('#cancelLocMessage').text();
    /**/
    $invoiceRequestsTable = $('#invoice_requests');
    var urlBase = '/2a8fy7b07dxe44/invoiceRequests';
    var urlVarPart = '?availableActionSet=ACCEPT_MANUAL';


    $('#invoice-requests-for-accept').addClass('active');


    $('#invoice-requests-for-accept').click(function () {
        changeUrlAndReload(this, '?availableActionSet=ACCEPT_MANUAL')
    });
    $('#invoice-requests-accepted').click(function () {
        changeUrlAndReload(this, '/ACCEPTED_ADMIN')
    });

    function changeUrlAndReload($elem, urlPart) {
        urlVarPart = urlPart;
        $('.myorders__button').removeClass('active');
        $($elem).addClass('active');
        updateInvoiceTable();
    }



    function updateInvoiceTable() {if ($.fn.dataTable.isDataTable('#invoice_requests')) {
        invoiceRequestsDataTable = $($invoiceRequestsTable).DataTable();
        invoiceRequestsDataTable.ajax.url(urlBase + urlVarPart).load();
    } else {
        invoiceRequestsDataTable = $($invoiceRequestsTable).DataTable({
            "ajax": {
                "url": urlBase + urlVarPart,
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
                        } else if (data === "REVOKED_USER") {
                            return revokedByUserLocMessage;
                        } else if (data === "EXPIRED") {
                            return timeOutExpiredLocMessage;
                        } else if (data === "CONFIRMED_USER") {
                            return '<div class="table-button-block" style="white-space: nowrap">' +
                                '<button style="font-size: 11px;" class="table-button-block__button btn btn-success" onclick="showAcceptModal(this)">' +
                                acceptLocMessage +
                                '</button>' +
                                '&nbsp;' +
                                //'<button style="font-size: 11px;" class="table-button-block__button btn btn-danger" onclick="declineInvoice(event,' + row.transaction.id + ')">' +
                                '<button style="font-size: 11px;" class="table-button-block__button btn btn-danger" onclick=declineInvoice(event,' + row.transaction.id + ',"'+row.userEmail+'")>' +declineLocMessage +
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
        });}
    }

    updateInvoiceTable();

    $('#changeAmount').on('click', function () {
        var $amountInput = $('#actualAmount');
        if (amountChanging) {
            amountChanging = false;
            $(this).text(changeAmountLocMessage);
            $($amountInput).val($('#initialAmount').val());
            $('#actualPaymentSum').val('');
            $($amountInput).prop('readonly', true);
            $($amountInput).prop('disabled', true);
        } else {
            amountChanging = true;
            $(this).text(cancelLocMessage);
            $($amountInput).prop('readonly', false);
            $($amountInput).prop('disabled', false);
            $($amountInput).val('');
        }
    });
    $('#actualAmount').on('input', function () {
        $('#actualPaymentSum').val($(this).val());
    });


    $('#submitAccept').on('click', function () {
        acceptInvoice(updateInvoiceTable)
    });
    $('#cancelAccept').on('click', function () {
        $('#acceptModal').modal('hide');

    })

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
    var bankCode = rowData.payerBankCode ? rowData.payerBankCode : '';
    $('#info-bankFrom').text(replaceAbsentWithDash(rowData.payerBankName) + ' ' + bankCode);
    $('#info-userAccount').text(replaceAbsentWithDash(rowData.payerAccount));
    $('#info-userFullName').text(replaceAbsentWithDash(rowData.userFullName));
    $('#info-remark').find('textarea').html(replaceAbsentWithDash(rowData.remark));
    var receiptImage;
    if (rowData.receiptScanPath) {
        receiptImage = '<a href="' + rowData.receiptScanPath + '" class="col-sm-4" data-toggle="lightbox"><img src="' +
            rowData.receiptScanPath + '" class="img-responsive"></a>';
    } else {
        receiptImage = '-';
    }

    $('#info-receipt').html(receiptImage);
}

function replaceAbsentWithDash(value) {
    return value ? value : '-';
}

function showAcceptModal($elem) {
    var $row = $($elem).parents('tr');
    var $initialAmountInput = $('#initialAmount');
    var $actualAmountInput = $('#actualAmount');
    var rowData = invoiceRequestsDataTable.row($row).data();
    var totelAmount = rowData.transaction.amount + rowData.transaction.commissionAmount;
    $($initialAmountInput).val(totelAmount);
    $($actualAmountInput).val(totelAmount);
    $($actualAmountInput).prop('readonly', true);
    $($actualAmountInput).prop('disabled', true);
    amountChanging = false;
    $('#changeAmount').text($('#changeAmountLocMessage').text());
    $('#transactionId').val(rowData.transaction.id);
    $('#actualPaymentSum').val('');
    $('#acceptModal').modal();
}



