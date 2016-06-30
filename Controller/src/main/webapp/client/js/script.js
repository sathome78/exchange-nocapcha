$(document).delegate('*[data-toggle="lightbox"]', 'click', function (event) {
    event.preventDefault();
    $(this).ekkoLightbox();
    if ($(".delete_img").length !== 0) {
        $(".delete_img").submit(function (e) {
            e.preventDefault();
            var array = $(this).serializeArray();
            var id = array[0]['value'];
            var path = array[1]['value'];
            var userId = array[2]['value'];
            promptDeleteDoc(id, path, userId);
        });
    }

    if ($('#usersTable').length !== 0) {
        $('#usersTable').DataTable();
    }
});

$(function () {
    $('.adminForm-toggler').click(function () {
        if ($(this).hasClass('active')) {
            return;
        } else {
            $('.tab-pane').removeClass('active');
            $('.adminForm-toggler').removeClass('active');
            $(this).addClass('active');
            var idx = $(this).index();
            $('.tab-pane:eq(' + idx + ')').addClass('active');
        }
    });

    //Enable REGISTER button if pass == repass when entering repass
    /*Activates submit button if all field filled correct and capcha is passed
     * */
    if (document.getElementById("#register_button")) {
        document.getElementById("#register_button").disabled = true;
    }
    $("#repass").keyup(function () {
        var pass = $('#pass').val();
        var repass = $('#repass').val();
        var email = $('#email').val();
        var login = $('#login').val();
        var capchaPassed = $('#cpch-field').hasClass('passed');

        if ((pass.length != 0) && (pass === repass)) {
            $('.repass').css("display", "block");
            if ((email.length != 0) && (login.length != 0) && (capchaPassed)) {
                $("#register_button").prop('disabled', false);
            } else {
                $("#register_button").prop('disabled', true);
            }
        }
        else {
            $('.repass').css("display", "none");
            $("#register_button").prop('disabled', true);
        }
    });
});


function promptDeleteDoc(id, path, userId) {
    if (confirm($('#prompt_delete_rqst').html())) {
        var data = "fileId=" + id + "&path=" + path + "&userId=" + userId;
        $.ajax('/admin/users/deleteUserFile', {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            dataType: 'json',
            data: data
        }).done(function (result) {
            alert(result['success']);
            $('.modal').modal('hide');
            $('#_' + id).remove();
        }).fail(function (error) {
            console.log(JSON.stringify(error));
            alert(error['responseJSON']['error'])
        });
    }
}





