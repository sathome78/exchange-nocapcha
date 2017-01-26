/**
 * Created by OLEG on 23.09.2016.
 */
var currencyLimitDataTable;

$(document).ready(function () {

    var $currencyLimitsTable = $('#currency-limits-table');
    var $editCurrencyLimitForm = $('#edit-currency-limit-form');



    $('#roleName, #operationType').change(updateCurrencyLimitsDataTable);

    $($currencyLimitsTable).find('tbody').on('click', 'tr', function () {
        var rowData = currencyLimitDataTable.row(this).data();
        var currencyId = rowData.currency.id;
        var currencyName = rowData.currency.name;
        var currentMinLimit = rowData.minSum;
        var operationType = $('#operationType').val();
        var userRole = $('#roleName').val();
        $($editCurrencyLimitForm).find('input[name="currencyId"]').val(currencyId);
        $('#currency-name').val(currencyName);
        $($editCurrencyLimitForm).find('input[name="operationType"]').val(operationType);
        $($editCurrencyLimitForm).find('input[name="roleName"]').val(userRole);
        $($editCurrencyLimitForm).find('input[name="minAmount"]').val(currentMinLimit);
        $('#editLimitModal').modal();
    });
    $('#submitNewLimit').click(function(e) {
        e.preventDefault();
        submitNewLimit()
    });



    updateCurrencyLimitsDataTable();


});

function updateCurrencyLimitsDataTable() {
    var $currencyLimitsTable = $('#currency-limits-table');
    var userRole = $('#roleName').val();
    var operationType = $('#operationType').val();
    var urlBase = '/2a8fy7b07dxe44/editCurrencyLimits/retrieve';
    var currencyLimitUrl = urlBase + '?roleName=' + userRole + '&operationType=' + operationType;
    if ($.fn.dataTable.isDataTable('#currency-limits-table')) {
        currencyLimitDataTable = $($currencyLimitsTable).DataTable();
        currencyLimitDataTable.ajax.url(currencyLimitUrl).load();
    } else {
        currencyLimitDataTable = $($currencyLimitsTable).DataTable({
            "ajax": {
                "url": currencyLimitUrl,
                "dataSrc": ""
            },
            "bFilter": false,
            "paging": false,
            "order": [],
            "bLengthChange": false,
            "bPaginate": false,
            "bInfo": false,
            "columns": [
                {
                    "data":"currency.id",
                    "visible": false
                },
                {
                    "data": "currency.name"
                },
                {
                    "data": "minSum"
                }
            ]
        });
    }
}



function submitNewLimit() {
    var formData =  $('#edit-currency-limit-form').serialize();
    console.log(formData);
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/editCurrencyLimits/submit',
        type: 'POST',
        data: formData,
        success: function () {
            updateCurrencyLimitsDataTable();
            $('#editLimitModal').modal('hide');
        },
        error: function (error) {
            $('#editLimitModal').modal('hide');
            console.log(error);
        }
    });
}