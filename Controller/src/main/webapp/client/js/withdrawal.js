var currentEmail;
var $withdrawalTable;
var withdrawalDataTable;
var withdrawRequestsBaseUrl;
var tableViewType;
var filterParams;

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

    $('#filter-datetimepicker_start').datetimepicker({
        format: 'YYYY-MM-DD HH:mm',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang:'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });
    $('#filter-datetimepicker_end').datetimepicker({
        format: 'YYYY-MM-DD HH:mm',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang:'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });


    $withdrawalTable = $('#withdrawalTable');
    tableViewType = "FOR_WORK";
    filterParams = '';
    withdrawRequestsBaseUrl = '/2a8fy7b07dxe44/withdrawRequests?viewType=';
    $('#withdraw-requests-new').addClass('active');


    $('#withdraw-requests-new').click(function () {
        changeTableViewType(this, "FOR_WORK")
    });
    $('#withdraw-requests-accepted').click(function () {
        changeTableViewType(this, "POSTED")
    });
    $('#withdraw-requests-declined').click(function () {
        changeTableViewType(this, "DECLINED")
    });

    function changeTableViewType($elem, newStatus) {
        tableViewType = newStatus;
        $('.myorders__button').removeClass('active');
        $($elem).addClass('active');
        updateWithdrawalTable();
    }


    updateWithdrawalTable();

    $('#createCommentConfirm').on('click', function () {

        var newComment = document.getElementById("commentText").value;
        var email = currentEmail;
        var sendMessage = document.getElementById("sendMessageCheckbox").checked;
        if (sendMessage == true){
            if (confirm($('#prompt_send_message_rqst').html() + " " + email + "?")) {

            }else{
                $("[data-dismiss=modal]").trigger({ type: "click" });
                return;
            }
        }
        $.ajax({
            url: '/2a8fy7b07dxe44/addComment',
            type: 'POST',
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            data: {
                "newComment": newComment,
                "email": email,
                "sendMessage": sendMessage

            },
            success: function (data) {
            },
            error: function (err) {
                console.log(err);
            }
        });
        $("[data-dismiss=modal]").trigger({ type: "click" });
        return;
    });

    $('#filter-apply').on('click', function (e) {
        e.preventDefault();
        filterParams = $('#withdrawal-request-search-form').serialize();
        updateWithdrawalTable();
    });

    $('#filter-reset').on('click', function (e) {
        e.preventDefault();
        $('#withdrawal-request-search-form')[0].reset();
        filterParams = '';
        updateWithdrawalTable();

    });
});

function submitAccept($elem) {
    promptAcceptRequest(getRowId($elem))
}
function submitDecline($elem) {
    promptDeclineRequest(getRowId($elem))
}

function promptAcceptRequest(requestId) {
    if (confirm($('#prompt_acc_rqst').html())) {
        var data = "requestId=" + requestId;
        $.ajax('/merchants/withdrawal/request/accept',{
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            dataType: 'json',
            data: data,
            success: function (result) {
                alert(result['success']);
                updateWithdrawalTable();
            }
        });
    }
}

function promptDeclineRequest(requestId) {
    if (confirm($('#prompt_dec_rqst').html())) {
        var data = "requestId=" + requestId;
        document.getElementById("commentText").value = "";
        document.getElementById("user_info").textContent = "";
        $.ajax('/merchants/withdrawal/request/decline',{
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            dataType: 'json',
            data: data,
            success: function (result) {
                alert(result['success']);
                updateWithdrawalTable();
                $("#myModal").modal();
                document.getElementById("sendMessageCheckbox").checked = true;
                currentEmail = result.userEmail;
                document.getElementById("user_info").textContent = document.getElementById("language").innerText + ", " +  result.userEmail;
                $('#checkMessage').show();
            }
        });
    }


}

function getRowId($elem) {
    var rowData = retrieveRowDataForElement($elem);
    return rowData.transaction.id;
}

function retrieveRowDataForElement($elem) {
    var $row = $($elem).parents('tr');
    return withdrawalDataTable.row($row).data();
}

function viewRequestInfo($elem) {
    var rowData = retrieveRowDataForElement($elem);
    fillModal(rowData);
    $('#withdraw-info-modal').modal();

}

function fillModal(rowData) {
    $('#info-currency').text(rowData.currencyName);
    $('#info-amount').text(rowData.amount);
    $('#info-commissionAmount').text(rowData.commissionAmount);
    var recipientBank = rowData.recipientBankName ? rowData.recipientBankName : '';
    var recipientBankCode = rowData.recipientBankCode ? rowData.recipientBankCode : '';
    var userFullName = rowData.userFullName ? rowData.userFullName : '';
    $('#info-bankRecipient').text(recipientBank + ' ' + recipientBankCode);
    $('#info-status').text(rowData.status);
    $('#info-status-date').text(rowData.statusModificationDate);
    $('#info-wallet').text(rowData.wallet);
    $('#info-userFullName').text(rowData.userFullName);
    $('#info-remark').find('textarea').html(rowData.remark);
}



function updateWithdrawalTable() {
    var filter = filterParams.length > 0 ? '&' + filterParams : '';
    var url = withdrawRequestsBaseUrl + tableViewType + filter;
    if ($.fn.dataTable.isDataTable('#withdrawalTable')) {
        withdrawalDataTable = $($withdrawalTable).DataTable();
        withdrawalDataTable.ajax.url(url).load();
    } else {
        withdrawalDataTable = $($withdrawalTable).DataTable({
            "ajax": {
                "url": url,
                "dataSrc": "data"
            },
            "serverSide": true,
            "paging": true,
            "info": true,
            "bFilter": false,
            "columns":[
                {
                    "data": "id",
                    "name": "WITHDRAW_REQUEST.id",
                    "render": function (data) {
                        return '<button class="request_id_button" onclick="viewRequestInfo(this)">' + data + '</button>';
                    }
                },
                {
                    "data": "dateCreation",
                    "name": "WITHDRAW_REQUEST.date_creation",
                    "render": function (data) {
                        return data.replace(' ', '<br/>');
                    },
                    "className": "text-center"
                },
               /* {
                    "data": "status",
                    "name": "WITHDRAW_REQUEST.status_id",
                },*/
                {
                    "data": "userId",
                    "name": "WITHDRAW_REQUEST.user_id",
                    "render": function (data, type, row) {
                        return '<a href="/2a8fy7b07dxe44/userInfo?id=' + data + '">' + row.userEmail + '</a>'
                    }
                },
                {
                    "data": "amount",
                    "name": "WITHDRAW_REQUEST.amount",
                },
                {
                    "data": "currencyName",
                    "name": "WITHDRAW_REQUEST.currency_id",
                },

                {
                    "data": "commissionAmount",
                    "name": "WITHDRAW_REQUEST.commission_amount",
                },
                {
                    "data": "merchantName",
                    "name": "WITHDRAW_REQUEST.merchant_id",
                    "render": function (data, type, row) {
                        var merchantName = data;
                        var merchantImageName = '';
                        if (row.merchantImage && row.merchantImage.image_name != merchantName) {
                            merchantImageName = ' ' + row.merchantImage.image_name;
                        }
                        return merchantName + merchantImageName;
                    }
                },
                {
                    "data": "wallet",
                    "name": "WITHDRAW_REQUEST.wallet",
                },
                {
                    "data": "adminHolderEmail",
                    "name": "WITHDRAW_REQUEST.admin_holder_id",
                    "render": function (data, type, row) {
                        if (data) {
                            return '<a href="/2a8fy7b07dxe44/userInfo?id=' + row.adminHolderId + '">' + data + '</a>';
                        } else {
                            return getButtonsSet(row.id, row.sourceType, row.buttons, "withdrawalTable");
                        }
                    },
                    "className": "text-center"
                }
            ],
            "order": [[ 0, 'desc' ]]
        });
    }
}

/*return '<div class="table-button-block" style="white-space: nowrap">' +
 '<button style="font-size: 11px;" class="table-button-block__button btn btn-success" onclick="submitAccept(this)" >' +
 acceptLocMessage +
 '</button>' +
 '&nbsp;' +
 '<button style="font-size: 11px;" class="table-button-block__button btn btn-danger" onclick="submitDecline(this)" >' +
 declineLocMessage +
 '</button>' +
 '</div>';*/