var commentsDataTable;

$(function () {
    $('#commentsTable').hide();
    $('#comments-table-init').click(update);

    var maxCount = 400;

    $( "#comments-button" ).click(function() {
        $("#checkLengthComment").html(maxCount);
        $("#checkMaxLengthComment").html(maxCount);
        $("#checkMaxLengthComment").prop('maxlength', maxCount);

        if(document.getElementById("sendMessageCheckbox").checked){
            $('#checkMessage').show();
        }else{
            $('#checkMessage').hide();
        }
    });

    $('#commentText').bind('input', function(){
        var revText = this.value.length;

        if (revText > maxCount) {
            this.value = this.value.substr(0, maxCount);
        }
        var cnt = (maxCount - revText);
        if (cnt <= 0) {
            $("#checkLengthComment").html('0');
        }
        else {
            $("#checkLengthComment").html(cnt);
        }
    });

    function update() {
        $('#checkMessage').hide();
        if ($.fn.dataTable.isDataTable('#commentsTable')) {
            commentsDataTable.ajax.reload();
        } else {
            var id = $("#user-id").val();
            commentsDataTable = $('#commentsTable').DataTable({
                "ajax": {
                    "url": '/2a8fy7b07dxe44/comments?id=' + id,
                    "dataSrc": ""
                },
                "paging": true,
                "info": true,

                "columns": [

                    {
                        "data": "creationTime"
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
                            if (data) {
                                return '<input type="image" src="/client/img/email.png" style="width: 20px; height: 20px"/> ';
                            } else if (row.editable) {
                                return '<input type="image" src="/client/img/edit_icon_32.png" onclick="editUserComment(this)" style="width: 20px; height: 20px"/>' ;
                            }
                            return "";
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
                        "desc"
                    ]
                ]
            });
        }
        $('#commentsTable').show();

    }



    $('#comments-button').on('click', function () {
        $("#commentText").val("");
        $("#myModal").modal();
    });

    function sendAddComment(newComment, email, sendMessage) {
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
                update();
            },
            error: function (err) {
                console.log(err);
            }
        });
    }

    function sendEditComment(commentId, newComment, email, sendMessage) {
        $.ajax({
            url: '/2a8fy7b07dxe44/editUserComment',
            type: 'POST',
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            data: {
                "commentId": commentId,
                "newComment": newComment,
                "email": email,
                "sendMessage": sendMessage

            },
            success: function (data) {
                $('#commentId').val("");
                update();
            },
            error: function (err) {
                console.log(err);
            }
        });
    }

    $('#createCommentConfirm').on('click', function () {
        var commentId = $('#commentId').val();
        var newComment = document.getElementById("commentText").value;
        var email = $("input[name='email']").val();
        var sendMessage = document.getElementById("sendMessageCheckbox").checked;
        if (sendMessage){
            if (!confirm($('#prompt_send_message_rqst').html() + " " + email + "?")) {
                return;
            }
        }
        if (commentId == null || commentId =="") {
            sendAddComment(newComment, email, sendMessage);
        } else {
            sendEditComment(commentId, newComment, email, sendMessage)

        }
        $("#myModal").modal('hide');
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

function editUserComment(elem) {
    var row = $(elem).parents('tr');
    var rowData = commentsDataTable.row(row).data();
    console.log(rowData.comment);
    $("#commentId").val(rowData.id);
    $("#commentText").val(rowData.comment);
    $("#myModal").modal();

}

function deleteUserComment(e) {

    if (confirm($('#prompt_delete_user_comment_rqst').html())) {
        var element = $(this);
        var row = $(this).closest('tr');
        var data = $('#commentsTable').dataTable().fnGetData(row);

        $.ajax({
            url: '/2a8fy7b07dxe44/deleteUserComment',
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