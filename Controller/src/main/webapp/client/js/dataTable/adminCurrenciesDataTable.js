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
        var currentMinLimit = parseFloat($(this).find('td:nth-child(3)').text());
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
        url: '/admin/editCurrencyLimits/submit',
        type: 'POST',
        data: formData,
        success: function (data) {
            $('#currency-limits-table').find('tr[data-id="' + currencyId + '"] td:nth-child(3)').html(minLimit.toLocaleString('en-US', {
                minimumFractionDigits: 2
            }));
            $('#editLimitModal').modal('hide');
        },
        error: function (error) {
            $('#editLimitModal').modal('hide');
            console.log(error);
            alert(error)
        }
    });
}