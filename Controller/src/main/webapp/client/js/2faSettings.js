
$(function () {

    var $email2faSendButton = $('#submitSessionOptionsButton');
    var $settingsForm = $('#2faSettings_form');
    var email = $('#u_email').text();
    var $checkbox = $('#login2fa');
    var checkboxStatus = $checkbox.attr("checked");
    var $result = $('#result');
    var url = "/2a8fy7b07dxe44/set2fa";
    const $smsNumberError = $('#phone_error');
    const $smsModal = $('#sms_connect_modal');




    $email2faSendButton.on('click', function (event) {

        event.preventDefault();
        $.ajax(url, {
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

    $('.contact_info').on('click', function() {
        resetSmsConnectModal();
        var userId = $("[name='userId']").val();
        var notificatorId = $(this).data("id");
        $.ajax({
            url: '/2a8fy7b07dxe44/2FaOptions/contact_info?userId=' + userId + '&notificatorId=' + notificatorId,
            type: 'GET',
            success: function (data) {
                console.log(data);
                data = JSON.parse(data);
                $smsModal.modal();
                $('#sms_info_block').show();
                $('#sms_number').text(data.contact);
                $('#sms_price').text(data.price);
            }
        });
    });

    function resetSmsConnectModal() {
        $('#sms_info_block').hide();
        $('#sms_code_block').hide();
        $('#sms_connect_block').hide();
        $smsNumberError.text('');
    }

});
