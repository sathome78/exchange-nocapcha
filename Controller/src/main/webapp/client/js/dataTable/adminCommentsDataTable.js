var commentsDataTable;

$(function () {

    update();

    function update() {
        $('#checkMessage').hide();
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
                                return '<input type="image" src="/client/img/email.png" style="width: 20px; height: 20px"/> ';
                            }else {
                                return "<input type='image' src='/client/img/Delete_Icon_16.png' onclick='deleteUserComment.call(this, event)' />" ;
                            }
                            return data;
                        }
                    },
                    {
                        "data": "id"
                        ,
                        "visible": false
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
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            data: {
                "newComment": newComment,
                "email": email,
                "sendMessage": sendMessage

            },
            success: function (data) {
                update();
            },
            error: function (err) {
                console.log(err);
            }
        });
        $("[data-dismiss=modal]").trigger({ type: "click" });
    });

    $('#createCommentCancel').on('click', function () {
        document.getElementById("commentText").value = "";
        document.getElementById("sendMessageCheckbox").checked = false;
    });

    $('#sendMessageCheckbox').on('click', function () {
        if(document.getElementById("sendMessageCheckbox").checked){
            $('#checkMessage').show();
        }else{
            $('#checkMessage').hide();
        }
    });

    });

function deleteUserComment(e) {

    if (confirm($('#prompt_delete_user_comment_rqst').html())) {
        var element = $(this);
        var row = $(this).closest('tr');
        var data = $('#commentsTable').dataTable().fnGetData(row);

        $.ajax({
            url: '/admin/deleteUserComment',
            type: 'POST',
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            data: {
                "commentId": data.id
            },
            success: function (data) {
                // update();
                commentsDataTable.ajax.reload();
            },
            error: function (err) {
                console.log(err);
            }
        });
    }
}