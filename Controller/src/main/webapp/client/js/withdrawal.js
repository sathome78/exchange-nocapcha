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
            data: data
        }).done(function(result) {
            alert(result['success']);
            var classname = '.id_' + requestId;
            var acceptance = result['acceptance'].split(/\s/);
            $(classname + ' td:nth-child(8)').html(acceptance[0] + '<br\>' + acceptance[1]);
            $(classname + ' td:nth-child(9)').html(result['email']);
            $(classname + ' td:last-child').html($('#accepted').html());
        }).fail(function(error){
            alert(JSON.stringify(error));
            alert(error['responseJSON']['error'])
        });
    }
}

function promptDeclineRequest(requestId) {
    if (confirm($('#prompt_dec_rqst').html())) {
        var data = "requestId=" + requestId;
        $.ajax('/merchants/withdrawal/request/decline',{
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            dataType: 'json',
            data: data
        }).done(function(result) {
            alert(result['success']);
            var classname = '.id_' + requestId;
            $(classname).remove();
        }).fail(function(error){
            alert(JSON.stringify(error));
            alert(error['responseJSON']['error'])
        });
    }
}
