var currentEmail;

$(function () {

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

    $('#withdrawalTable').DataTable({
        "order": []
    });

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
                $(classname + ' td:nth-child(8)').html(acceptance[0] + '<br\>' + acceptance[1]);
                $(classname + ' td:nth-child(9)').html(result['email']);
                $(classname + ' td:last-child').html($('#accepted').html());
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
                $(classname + ' td:nth-child(8)').html(acceptance[0] + '<br\>' + acceptance[1]);
                $(classname + ' td:nth-child(9)').html(result['email']);
                $(classname + ' td:last-child').html($('#declined').html());
                $("#myModal").modal();
                document.getElementById("sendMessageCheckbox").checked = true;
                currentEmail = result.userEmail;
                document.getElementById("user_info").textContent = document.getElementById("language").innerText + ", " +  result.userEmail;
                $('#checkMessage').show();
            }
        });
    }


}


