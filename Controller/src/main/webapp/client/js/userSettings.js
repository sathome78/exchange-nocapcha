
$(function () {

    var $email2faSendButton = $('#submitSessionOptionsButton');
    var $settingsForm = $('#2faSettings_form');
    var email = $('#u_email').text();
    var $checkbox = $('#enable_2fa');
    var checkboxStatus = $checkbox.attr("checked");
    var $result = $('#result');

    $email2faSendButton.on('click', function (event) {
        event.preventDefault();
        $.ajax('/2a8fy7b07dxe44/editUser/submit2faOptions?email=' + email, {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            data: $settingsForm.serialize()
        }).success(function (response) {
            $result.show();
            $result.text(response);
            checkboxStatus = $checkbox.attr("checked");
        }).fail(function (error, jqXHR, textStatus) {
            $checkbox.prop("checked", checkboxStatus === undefined ? false : checkboxStatus);
            $result.show();
            $result.text("error").css('color', 'red');
        });
    });

});
