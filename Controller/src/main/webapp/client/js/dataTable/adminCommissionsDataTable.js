/**
 * Created by OLEG on 23.09.2016.
 */
var currentLocale;
var commissionsDataTable;

$(document).ready(function () {
    var $commissionTable = $('#commissions-table');
    var $merchantCommissionTable = $('#merchant-commissions-table');
    var $commissionForm = $('#edit-commission-form');
    var $merchantCommissionForm = $('#edit-merchantCommission-form');
    var $roleNameSelect = $('#roleName');
    currentLocale = $('#language').text().trim().toLowerCase();




    updateCommissionsDataTable();
    $($roleNameSelect).on("change", updateCommissionsDataTable);


    $($merchantCommissionTable).DataTable({
        "bFilter": false,
        "paging": false,
        "order": [],
        "bLengthChange": false,
        "bPaginate": false,
        "bInfo": false
    });


    $($commissionTable).find('tbody').on('click', 'tr', function () {
        var currentRoleName = $($roleNameSelect).val();
        var operationType = $(this).find('td:first').text().trim();
        var commissionValue = parseFloat($(this).find('td:nth-child(2)').text());
        $($commissionForm).find('input[name="userRole"]').val(currentRoleName);
        $('#operationType').val(operationType);
        $($commissionForm).find('input[name="commissionValue"]').val(commissionValue);
        $('#editCommissionModal').modal();
    });
    $('#submitCommission').click(function(e) {
        e.preventDefault();
        var id = $($commissionForm).find('input[name="commissionId"]').val();
        var value =  $($commissionForm).find('input[name="commissionValue"]').val();
        submitCommission()
    });

    $($merchantCommissionTable).find('tbody').on('click', 'tr', function () {
        var merchantId = $(this).data('merchantid');
        var currencyId = $(this).data('currencyid');
        var merchantName = $(this).find('td:first').text().trim();
        var currencyName = $(this).find('td:nth-child(2)').text().trim();
        var commissionValue = parseFloat($(this).find('td:nth-child(3) .merchantCommissionUnformatted').text());
        $($merchantCommissionForm).find('input[name="merchantId"]').val(merchantId);
        $($merchantCommissionForm).find('input[name="currencyId"]').val(currencyId);
        $('#merchantName').val(merchantName);
        $('#currencyName').val(currencyName);
        $($merchantCommissionForm).find('input[name="commissionValue"]').val(commissionValue);
        $('#editMerchantCommissionModal').modal();
    });

    $('#submitMerchantCommission').click(function(e) {
        e.preventDefault();
        var merchantId = $($merchantCommissionForm).find('input[name="merchantId"]').val();
        var currencyId = $($merchantCommissionForm).find('input[name="currencyId"]').val();
        var value =  $($merchantCommissionForm).find('input[name="commissionValue"]').val();
        submitMerchantCommission(merchantId, currencyId, parseFloat(value))
    });


});

function updateCommissionsDataTable() {
    var $commissionTable = $('#commissions-table');

    var currentRoleName = $('#roleName').val();
    var commissionUrlBase = '/2a8fy7b07dxe44/getCommissionsForRole?role=';
    var commissionUrl = commissionUrlBase + currentRoleName;
    if ($.fn.dataTable.isDataTable('#commissions-table')) {
        commissionsDataTable = $($commissionTable).DataTable();
        commissionsDataTable.ajax.url(commissionUrl).load();
    } else {
        commissionsDataTable = $($commissionTable).DataTable({
            "ajax": {
                "url": commissionUrl,
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
                    "data": "operationType"
                },
                {
                    "data": "value"
                }
            ]
        });
    }
}

function submitCommission() {
    var formData =  $('#edit-commission-form').serialize();
    console.log(formData);
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/commissions/editCommission',
        type: 'POST',
        data: formData,
        success: function () {
            updateCommissionsDataTable();

            $('#editCommissionModal').modal('hide');
        },
        error: function (error) {
            $('#editCommissionModal').modal('hide');
            console.log(error);
        }
    });
}

function submitMerchantCommission(merchantId, currencyId, value) {
    var formData =  $('#edit-merchantCommission-form').serialize();
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/commissions/editMerchantCommission',
        type: 'POST',
        data: formData,
        success: function () {
            var minFractionDigits = 2;
            var fractionPart = value.toString().split('.')[1];
            if (fractionPart && fractionPart.length > minFractionDigits) {
                minFractionDigits = fractionPart.length;
            }
            var $cell = $('#merchant-commissions-table').find('tr[data-merchantid="' + merchantId + '"]' +
                '[data-currencyid="' + currencyId + '"] td:nth-child(3)');
            $($cell).find('.merchantCommissionFormatted').text(value.toLocaleString(currentLocale, {
                minimumFractionDigits: minFractionDigits
            }));
            $($cell).find('.merchantCommissionUnformatted').text(value);

            $('#editMerchantCommissionModal').modal('hide');
        },
         error: function (error) {
         $('#editCommissionModal').modal('hide');
         console.log(error);
         }
    });
}
