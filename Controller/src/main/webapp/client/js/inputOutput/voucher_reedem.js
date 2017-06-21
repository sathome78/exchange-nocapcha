/**
 * Created by maks on 20.06.2017.
 */

$(function() {
    var $modal = $('#voucher_reedem_modal');
    var $form = $('#voucher_code_form');
    var $result = $('#voucher_result');


    $('#voucher_reedem_dialog_button').on('click', function () {
        $modal.modal();
    });

    $('#submit_code').on('click', function () {
        console.log('click');
        var url = '/transfer/accept';
        var data = $form.serialize();
        $.ajax({
            url: url,
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            data: data
        }).success(function (result) {
            console.log(result);
            $result.show().text(result);
            $form.hide();
        }).error(function (event, jqXHR, options, jsExc) {
            console.log(jqXHR);
        });
    });


});
