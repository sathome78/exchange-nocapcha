var commentsDataTable;

$(function () {

    update();

    function update() {
        if ($.fn.dataTable.isDataTable('#commentsTable')) {
            commentsDataTable.ajax.reload();
        } else {
            var id = $("#user-id").val();
            commentsDataTable = $('#commentsTable').DataTable({
                "ajax": {
                    "url": '/admin/comments?id=' + id,
                    "dataSrc": ""
                },
                "paging": true,
                "info": true,

                "columns": [

                    {
                        "data": "commentsTime"
                    },
                    {
                        "data": "creator.email"
                    },
                    {
                        "data": "comment"
                    },
                    {
                        "data": "messageSent",
                        "render": function (data, type, row){
                            if (row.messageSent == true) {
                                return '<img src="/client/img/email.png" style="width: 20px; height: 20px"/> ';
                            }else {
                                return "";
                            }
                            return data;
                        }
                    }
                ],
                "order": [
                    [
                        0,
                        "asc"
                    ]
                ]
            });
        }
    }

    $('#comments-button').on('click', function () {
        $("#myModal").modal();
    });
    $('#createCommentConfirm').on('click', function () {
        var newComment = document.getElementById("commentText").value;
        var email = $("input[name='email']").val();
        var sendMessage = document.getElementById("sendMessageCheckbox").checked;
        if (sendMessage == true){
            if (confirm($('#prompt_send_message_rqst').html() + " " + email + "?")) {

            }else{
                $("[data-dismiss=modal]").trigger({ type: "click" });
                return;
            }
        }
        $.ajax({
            url: '/admin/addComment',
            type: 'POST',
            headers: {
                //'X-CSRF-Token': token
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            data: {
                "newComment": newComment,
                "email": email,
                "sendMessage": sendMessage

            },
            success: function (data) {
                //refreshTable();
            },
            error: function (err) {
                console.log(err);
            }
        });
        $("[data-dismiss=modal]").trigger({ type: "click" });
        update();
    });

    $('#createCommentCancel').on('click', function () {
        document.getElementById("commentText").value = "";
        document.getElementById("sendMessageCheckbox").checked = false;
    });

    });