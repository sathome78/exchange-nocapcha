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
        lang: 'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });
    $('#filter-datetimepicker_end').datetimepicker({
        format: 'YYYY-MM-DD HH:mm',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        lang: 'ru',
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
    $('#withdraw-requests-auto').click(function () {
        changeTableViewType(this, "AUTO_PROCESSING")
    });
    $('#withdraw-requests-accepted').click(function () {
        changeTableViewType(this, "POSTED")
    });
    $('#withdraw-requests-declined').click(function () {
        changeTableViewType(this, "DECLINED")
    });
    $('#withdraw-requests-All').click(function () {
        changeTableViewType(this, "ALL")
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
        if (sendMessage == true) {
            if (confirm($('#prompt_send_message_rqst').html() + " " + email + "?")) {

            } else {
                $("[data-dismiss=modal]").trigger({type: "click"});
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
        $("[data-dismiss=modal]").trigger({type: "click"});
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

    $('#withdrawalTable').on('click', 'button[data-source=WITHDRAW].post_holded_button', function (e) {
        e.preventDefault();
        var id = $(this).data("id");
        var $modal = $("#confirm-with-info-modal");
        $modal.find("label[for=info-field]").html($(this).html());
        $modal.find("#info-field").val(id);
        $modal.find("#confirm-button").off("click").one("click", function () {
            $modal.modal('hide');
            $.ajax({
                url: '/2a8fy7b07dxe44/withdraw/post?id=' + id,
                async: false,
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val(),
                },
                type: 'POST',
                complete: function () {
                    updateWithdrawalTable();
                }
            });
        });
        $modal.modal();
    });

    $('#withdrawalTable').on('click', 'button[data-source=WITHDRAW].take_to_work_button', function (e) {
        e.preventDefault();
        var id = $(this).data("id");
        var $modal = $("#confirm-with-info-modal");
        $modal.find("label[for=info-field]").html($(this).html());
        $modal.find("#info-field").val(id);
        $modal.find("#confirm-button").off("click").one("click", function () {
            $modal.modal('hide');
            $.ajax({
                url: '/2a8fy7b07dxe44/withdraw/take?id=' + id,
                async: false,
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val(),
                },
                type: 'POST',
                complete: function () {
                    updateWithdrawalTable();
                }
            });
        });
        $modal.modal();
    });

    $('#withdrawalTable').on('click', 'button[data-source=WITHDRAW].return_from_work_button', function (e) {
        e.preventDefault();
        var id = $(this).data("id");
        var $modal = $("#confirm-with-info-modal");
        $modal.find("label[for=info-field]").html($(this).html());
        $modal.find("#info-field").val(id);
        $modal.find("#confirm-button").off("click").one("click", function () {
            $modal.modal('hide');
            $.ajax({
                url: '/2a8fy7b07dxe44/withdraw/return?id=' + id,
                async: false,
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val(),
                },
                type: 'POST',
                complete: function () {
                    updateWithdrawalTable();
                }
            });
        });
        $modal.modal();
    });


    $('#withdrawalTable').on('click', 'button[data-source=WITHDRAW].decline_holded_button, button[data-source=WITHDRAW].decline_button', function (e) {
        e.stopPropagation();
        var id = $(this).data("id");
        var $modal = $("#note-before-decline-modal");
        var email = $(this).closest("tr").find("a[data-userEmail]").data("useremail");
        $.ajax({
            url: '/2a8fy7b07dxe44/phrases/withdraw_decline?email=' + email,
            type: 'GET',
            success: function (data) {
                $modal.find("#user-language").val(data["lang"]);
                $list = $modal.find("#phrase-template-list");
                $list.html("<option></option>");
                data["list"].forEach(function (e) {
                    $list.append($("<option></option>").append(e));
                });
                $modal.find("#createCommentConfirm").off("click").one("click", function (event) {
                    var $textArea = $(event.target).closest("#note-before-decline-modal").find("#commentText");
                    var comment = $textArea.val().trim();
                    if (!comment) {
                        return;
                    }
                    $modal.modal('hide');
                    $.ajax({
                        url: '/2a8fy7b07dxe44/withdraw/decline?id=' + id + '&comment=' + comment,
                        async: false,
                        headers: {
                            'X-CSRF-Token': $("input[name='_csrf']").val()
                        },
                        type: 'POST',
                        complete: function () {
                            updateWithdrawalTable();
                        }
                    });
                });
                $modal.find("#createCommentCancel").off("click").one("click", function () {
                    $modal.modal('hide');
                });
                $modal.modal();
            }
        });
    });

    $('#withdrawalTable').on('click', 'button[data-source=WITHDRAW].confirm_admin_button', function (e) {
        e.preventDefault();
        var id = $(this).data("id");
        var $modal = $("#confirm-with-info-modal");
        $modal.find("label[for=info-field]").html($(this).html());
        $modal.find("#info-field").val(id);
        $modal.find("#confirm-button").off("click").one("click", function () {
            $modal.modal('hide');
            $.ajax({
                url: '/2a8fy7b07dxe44/withdraw/confirm?id=' + id,
                async: false,
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val(),
                },
                type: 'POST',
                complete: function () {
                    updateWithdrawalTable();
                }
            });
        });
        $modal.modal();
    });
});

function promptDeclineRequest(requestId) {
    if (confirm($('#prompt_dec_rqst').html())) {
        var data = "requestId=" + requestId;
        document.getElementById("commentText").value = "";
        document.getElementById("user_info").textContent = "";
        $.ajax('/merchants/withdrawal/request/decline', {
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
                document.getElementById("user_info").textContent = document.getElementById("language").innerText + ", " + result.userEmail;
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
    var $modal = $('#withdraw-info-modal');
    fillModal($modal, rowData);
    $modal.modal();
}

function fillModal($modal, rowData) {
    $modal.find('#info-currency').text(rowData.currencyName);
    $modal.find('#info-amount').text(rowData.amount);
    $modal.find('#info-commissionAmount').text(rowData.commissionAmount);
    var recipientBank = rowData.recipientBankName ? rowData.recipientBankName : '';
    var recipientBankCode = rowData.recipientBankCode ? rowData.recipientBankCode : '';
    var userFullName = rowData.userFullName ? rowData.userFullName : '';
    $modal.find('#info-bankRecipient').text(recipientBank + ' ' + recipientBankCode);
    $modal.find('#info-status').text(rowData.status);
    $modal.find('#info-status-date').text(rowData.statusModificationDate);
    $modal.find('#info-wallet').text(rowData.wallet);
    $modal.find('#info-userFullName').text(rowData.userFullName);
    $modal.find('#info-remark').find('textarea').html(rowData.remark);
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
            "bFilter": true,
            "columns": [
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
                        return '<a data-userEmail="' + row.userEmail + '" href="/2a8fy7b07dxe44/userInfo?id=' + data + '">' + row.userEmail + '</a>'
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
                        if (data && row.isEndStatus) {
                            return '<a href="/2a8fy7b07dxe44/userInfo?id=' + row.adminHolderId + '">' + data + '</a>';
                        } else {
                            return tableViewType == "ALL" ? row.status : getButtonsSet(row.id, row.sourceType, row.buttons, "withdrawalTable");
                        }
                    },
                    "className": "text-center"
                }
            ],
            "createdRow": function (row, data, index) {
            },
            "order": [[0, 'desc']]
        });
    }
}
