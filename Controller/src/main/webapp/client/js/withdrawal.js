var currentEmail;
var $withdrawalTable;
var withdrawalDataTable;
$(function () {

    $.get('/2a8fy7b07dxe44/withdrawRequests', function (data) {
        console.log(data);
    });

    $withdrawalTable = $('#withdrawalTable');
    var withdrawRequestsBaseUrl

    $('.accept_withdrawal_rqst').submit(function (e) {
        e.preventDefault();
        var id = $(this).serializeArray()[0]['value'];
        promptAcceptRequest(id);
    });
    
    $('.decline_withdrawal_rqst').submit(function (e) {
        e.preventDefault();
        var id = $(this).serializeArray()[0]['value'];
        promptDeclineRequest(id);
    });

    updateWithdrawalTable('/2a8fy7b07dxe44/withdrawRequests');





    $withdrawalTable.find('button')

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
});

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
                var classname = '.id_' + requestId;
                var acceptance = result['acceptance'].split(/\s/);
                $(classname + ' td:nth-child(9)').html(acceptance[0] + '<br\>' + acceptance[1]);
                $(classname + ' td:nth-child(10)').html(result['email']);
                $(classname + ' td:nth-child(11)').html($('#accepted').html());
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
                var classname = '.id_' + requestId;
                var acceptance = result['acceptance'].split(/\s/);
                $(classname + ' td:nth-child(9)').html(acceptance[0] + '<br\>' + acceptance[1]);
                $(classname + ' td:nth-child(10)').html(result['email']);
                $(classname + ' td:nth-child(11)').html($('#declined').html());
                $("#myModal").modal();
                document.getElementById("sendMessageCheckbox").checked = true;
                currentEmail = result.userEmail;
                document.getElementById("user_info").textContent = document.getElementById("language").innerText + ", " +  result.userEmail;
                $('#checkMessage').show();
            }
        });
    }


}

function viewRequestInfo($elem) {
    var $row = $($elem).parents('tr');
    fillModal($row);
    $('#withdraw-info-modal').modal();

}

function fillModal($row) {
    $('#info-currency').text($($row).find('td:nth-child(5)').text());
    $('#info-amount').text($($row).find('td:nth-child(4)').text());
    $('#info-commissionAmount').text($($row).find('td:nth-child(6)').text());
    $('#info-bankRecipient').text($($row).find('td:nth-child(13)').text());
    $('#info-userAccount').text($($row).find('td:nth-child(8)').text());
    $('#info-userFullName').text($($row).find('td:nth-child(14)').text());
    $('#info-remark').find('textarea').html($($row).find('td:nth-child(12)').text());
}

function updateWithdrawalTable(url) {
    if ($.fn.dataTable.isDataTable('#withdrawalTable')) {
        withdrawalDataTable = $($withdrawalTable).DataTable();
        withdrawalDataTable.ajax.url(url).load();
    } else {
        withdrawalDataTable = $($withdrawalTable).DataTable({
            "ajax": {
                "url": url,
                "dataSrc": ""
            },
            "serverSide": true,
            "paging": true,
            "info": true,
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
                    "data": "acceptance",
                    "name": "WITHDRAW_REQUEST.acceptance",
                    "render": function (data) {
                        return data ? data.replace(' ', '<br/>') : '-';
                    },
                    "className": "text-center"
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
                                '<button style="font-size: 11px;" class="table-button-block__button btn btn-success" >' +
                                acceptLocMessage +
                                '</button>' +
                                '&nbsp;' +
                                '<button style="font-size: 11px;" class="table-button-block__button btn btn-danger" >' +
                                declineLocMessage +
                                '</button>' +
                                '</div>';
                        }

                    },
                    "className": "text-center"
                }

            ],
            "order": []
        });
    }
}