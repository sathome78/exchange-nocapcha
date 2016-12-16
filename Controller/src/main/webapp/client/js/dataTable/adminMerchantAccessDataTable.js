/**
 * Created by OLEG on 23.09.2016.
 */
var currentLocale;

$(document).ready(function () {
    var $merchantAccessTable = $('#merchant-options-table');
    $($merchantAccessTable).DataTable({
        "bFilter": false,
        "paging": false,
        "order": [],
        "bLengthChange": false,
        "bPaginate": false,
        "bInfo": false
    });

    $merchantAccessTable.find('td').on('click', 'i', function () {
        var operationType = $(this).parent().data('operationtype');
        var merchantId = $(this).parents('tr').data('merchantid');
        var currencyId = $(this).parents('tr').data('currencyid');
        if (confirm($('#prompt-toggle-block').html())) {
            toggleBlock(merchantId, currencyId, operationType, this);
        }
    })

});


function toggleBlock(merchantId, currencyId, operationType, $element) {
    var formData = new FormData();
    formData.append("merchantId", merchantId);
    formData.append("currencyId", currencyId);
    formData.append("operationType", operationType);

    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/admin/merchantAccess/toggleBlock',
        type: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        success: function () {
            $($element).toggleClass('fa-lock red');
            $($element).toggleClass('fa-unlock');
        }
    });

}