/**
 * Created by OLEG on 27.01.2017.
 */
$(function () {
    var $optionsError = $('#optionsError');
    var $options = $('#notification-options-table').find('input[type="checkbox"]');
    var $button = $('#submitNoitficationOptionsButton');
    $($optionsError).hide();

    $($options).change(function () {
        console.log($(this).prop('checked'));
        var checkedInRow = $(this).parents('tr').find('input[type="checkbox"]').filter(function () {
            return $(this).prop('checked');
        }).length;
        if (checkedInRow === 0) {
            $($button).prop('disabled', true);
            $($optionsError).show();
        } else {
            $($button).prop('disabled', false);
            $($optionsError).hide();
        }


    })
});