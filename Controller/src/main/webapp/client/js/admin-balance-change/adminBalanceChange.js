/**
 * Created by OLEG on 25.11.2016.
 */
var walletsDataTable;
$(function () {
    if ($.fn.dataTable.isDataTable('#walletsTable')) {
        walletsDataTable = $('#walletsTable').DataTable();
    }
    $('#manualBalanceSubmit').on('click', function (e) {
        e.preventDefault();
        var currencyName = $('select#currency option:selected').text().trim();
        var formData = $('#manualBalanceChangeForm').serialize();
        $.ajax({
            url: '/admin/changeActiveBalance/submit',
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            data: formData,
            success: function () {
                $("input[name='amount']").val('');
                walletsDataTable.ajax.reload(function () {
                    var row = $('#walletsTable').find('tr').filter(function () {
                        var name = $(this).find('td:first').text().trim();
                        return name === currencyName;
                    });
                    blink_green(row);
                });

            }
        })
    })
});