var commentsDataTable;

$(function () {
    $('#commentsTable').hide();
    $('#comments-table-init').click(update);

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

        preparingToStartModelWindowWithComment();
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
    });

    $('#sendMessageCheckbox').on('click', function () {
        document.getElementById("sendMessageCheckbox").checked ? $('#checkMessage').show() : $('#checkMessage').hide();
    });

});

function editUserComment(elem) {
    var row = $(elem).parents('tr');
    var rowData = commentsDataTable.row(row).data();
    console.log(rowData.comment);
    $("#commentId").val(rowData.id);
    $("#commentText").val(rowData.comment);

    preparingToStartModelWindowWithCommentForEditing();
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

/**
* Max count of symbols in comment on user for admin
 */
var maxCountOfSymbols = 400;

/**
* Preparing to start a modal window with a comment
*/
function preparingToStartModelWindowWithComment(){
    $("#checkLengthComment").html(maxCountOfSymbols);
    $("#checkMaxLengthComment").html(maxCountOfSymbols);
    $("#checkMaxLengthComment").prop('maxlength', maxCountOfSymbols);

    $("#sendMessageCheckbox").prop('checked', false);
    $('#checkMessage').hide();

    $("#createCommentConfirm").prop('disabled', true);
}

/**
* Preparing to start a modal window with a comment.
* For editing comment.
*/
function preparingToStartModelWindowWithCommentForEditing(){
    var presentValueOfLengthComment = $("#commentText").val().length;
    var remainingCharactersCurrentValue = maxCountOfSymbols - presentValueOfLengthComment;

    $("#checkLengthComment").html(remainingCharactersCurrentValue);
    $("#checkMaxLengthComment").html(maxCountOfSymbols);
    $("#checkMaxLengthComment").prop('maxlength', maxCountOfSymbols);

    $("#sendMessageCheckbox").prop('checked', false);
    $('#checkMessage').hide();

    $("#createCommentConfirm").prop('disabled', true);
}

/**
* The method for working with creating comments (adding a counter, the maximum length of comments)
* Added a restriction that the comment can not be empty or only with spaces.
*/
$(function(){
    $('#commentText').bind('input', function(){
        var commentText = this.value.length;
        var pattern = /^[\s]+$/;

        //Block button 'confirm' when textarea contains only spaces or length of comment = 0.
        commentText == 0 || pattern.test(this.value) ? $("#createCommentConfirm").prop('disabled', true) : $("#createCommentConfirm").prop('disabled', false);

        if (commentText > maxCountOfSymbols) {
            this.value = this.value.substr(0, maxCountOfSymbols);
        }
        var counter = (maxCountOfSymbols - commentText);

        counter <= 0 ? $("#checkLengthComment").html('0') : $("#checkLengthComment").html(counter);
    });
});