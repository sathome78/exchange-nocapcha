

$(document).ready(function () {

    var $enabled = $('#enabled');
    var $minutes = $('#minutes');
    var $length = $('#length');
    var $sendButton = $('#update_updates');

    checkFields();

    $('.ch_field').on('input', function (e) {
        console.log("input");
        checkFields()
    });

    function checkFields() {
        if ($enabled.text() == 'true') {
            $sendButton.prop("disabled", false)
        } else if (checkMinutesField() && checkLengthField()) {
            $sendButton.prop("disabled", false)
        } else {
            $sendButton.prop("disabled", true)
        }
    }

    function checkMinutesField() {
        var value = $minutes.val();
        console.log("min " + value);
        return isInteger(value) && value < 1440;
    }

    function checkLengthField() {
        var value = $length.val();
        console.log("length " + value);
        return isInteger(value) && value < 1440;
    }

    function isInteger(x) {
        var isInt = parseInt(x, 10) == x;
        console.log(isInt);
        return isInt;
    }


});



