var currentEmail;
var $withdrawalTable;
var withdrawalDataTable;
var withdrawRequestsBaseUrl;
var requestStatus;
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
    requestStatus = 1;
    filterParams = '';
    withdrawRequestsBaseUrl = '/2a8fy7b07dxe44/withdrawRequests?status=';
    $('#withdraw-requests-new').addClass('active');


    $('#withdraw-requests-new').click(function () {
        changeStatus(this, 1)
    });
    $('#withdraw-requests-accepted').click(function () {
        changeStatus(this, 2)
    });
    $('#withdraw-requests-declined').click(function () {
        changeStatus(this, 3)
    });

    function changeStatus($elem, newStatus) {
        requestStatus = newStatus;
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

    $('#info-currency').text(rowData.transaction.currency.name);
    $('#info-amount').text(rowData.transaction.amount);
    $('#info-commissionAmount').text(rowData.transaction.commissionAmount);
    var recipientBank = rowData.recipientBankName ? rowData.recipientBankName : '';
    var recipientBankCode = rowData.recipientBankCode ? rowData.recipientBankCode : '';
    var userFullName = rowData.userFullName ? rowData.userFullName : '';
    $('#info-bankRecipient').text(recipientBank + ' ' + recipientBankCode);
    $('#info-acceptance').text(rowData.acceptance);
    $('#info-userFullName').text(userFullName);
    $('#info-remark').find('textarea').html(rowData.remark);
}



function updateWithdrawalTable() {
    var filter = filterParams.length > 0 ? '&' + filterParams : '';
    var url = withdrawRequestsBaseUrl + requestStatus + filter;
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
                    "data": "transaction.id",
                    "name": "TRANSACTION.id",
                    "render": function (data) {
                        return '<button class="request_id_button" onclick="viewRequestInfo(this)">' + data + '</button>';
                    }
                },
                {
                    "data": "transaction.datetime",
                    "name": "TRANSACTION.datetime",
                    "render": function (data) {
                        return data.replace(' ', '<br/>');
                    },
                    "className": "text-center"
                },
                {
                    "data": "userId",
                    "name": "USER.email",
                    "render": function (data, type, row) {
                        return '<a href="/2a8fy7b07dxe44/userInfo?id=' + data + '">' + row.userEmail + '</a>'
                    }
                },
                {
                    "data": "transaction.amount",
                    "name": "TRANSACTION.amount"
                },
                {
                    "data": "transaction.currency.name",
                    "name": "CURRENCY.name"
                },

                {
                    "data": "transaction.commissionAmount",
                    "name": "TRANSACTION.commission_amount"
                },
                {
                    "data": "transaction",
                    "name": "MERCHANT.name",
                    "render": function (data, type, row) {
                        var merchantName = data.merchant.name;
                        var merchantImageName;
                        if (data.merchantImage && data.merchantImage.image_name != merchantName) {
                            merchantImageName = ' ' + data.merchantImage.image_name;
                        } else {
                            merchantImageName = '';
                        }
                        return data.merchant.name + merchantImageName;
                    }
                },
                {
                    "data": "wallet",
                    "name": "WITHDRAW_REQUEST.wallet"
                },
                {
                    "data": "processedById",
                    "name": "ADMIN.email",
                    "render": function (data, type, row) {
                        if (data) {
                            return '<a href="/2a8fy7b07dxe44/userInfo?id=' + data + '">' + row.processedBy + '</a>';
                        } else {
                            var acceptLocMessage = $('#acceptRequestMessage').text();
                            var declineLocMessage = $('#declineRequestMessage').text();
                            return '<div class="table-button-block" style="white-space: nowrap">' +
                                '<button style="font-size: 11px;" class="table-button-block__button btn btn-success" onclick="submitAccept(this)" >' +
                                acceptLocMessage +
                                '</button>' +
                                '&nbsp;' +
                                '<button style="font-size: 11px;" class="table-button-block__button btn btn-danger" onclick="submitDecline(this)" >' +
                                declineLocMessage +
                                '</button>' +
                                '</div>';
                        }

                    },
                    "className": "text-center"
                }

            ],
            "order": [[ 0, 'desc' ]]
        });
    }
}