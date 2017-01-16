/**
 * Created by OLEG on 23.09.2016.
 */
$(document).ready(function () {

    $('#currency-limits-table').DataTable({
        "bFilter": false,
        "paging": false,
        "order": [],
        "bLengthChange": false,
        "bPaginate": false,
        "bInfo": false
    });

    $('#currency-limits-table tbody').on('click', 'tr', function () {
        var currencyId = $(this).data('id');
        var currencyName = $(this).find('td:first').text().trim();
        var currentMinLimit = parseFloat($(this).find('td:nth-child(3) .minLimitUnformatted').text());
        $('#edit-currency-limit-form').find('input[name="currencyId"]').val(currencyId);
        $('#currency-name').val(currencyName);
        $('#edit-currency-limit-form').find('input[name="minAmount"]').val(currentMinLimit);
        $('#editLimitModal').modal();
    });
    $('#submitNewLimit').click(function(e) {
        e.preventDefault();
        var id = $('#edit-currency-limit-form').find('input[name="currencyId"]').val();
        var newLimit =  $('#edit-currency-limit-form').find('input[name="minAmount"]').val();
        submitNewLimit(id, parseFloat(newLimit))
    });


});



function submitNewLimit(currencyId, minLimit) {
    var formData =  $('#edit-currency-limit-form').serialize();
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/editCurrencyLimits/submit',
        type: 'POST',
        data: formData,
        success: function (data) {
            var minFractionDigits = 2;
            var fractionPart = minLimit.toString().split('.')[1];
            if (fractionPart && fractionPart.length > minFractionDigits) {
                minFractionDigits = fractionPart.length;
            }
            var $cell = $('#currency-limits-table').find('tr[data-id="' + currencyId + '"] td:nth-child(3)');
            $($cell).find('.minLimitFormatted').text(minLimit.toLocaleString(undefined, {
                minimumFractionDigits: minFractionDigits
            }));
            $($cell).find('.minLimitUnformatted').text(minLimit);

            $('#editLimitModal').modal('hide');
        },
        error: function (error) {
            $('#editLimitModal').modal('hide');
            console.log(error);
        }
    });
}