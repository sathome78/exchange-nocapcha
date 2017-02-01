/**
 * Created by OLEG on 23.09.2016.
 */
var currentLocale;
var commissionsDataTable;
var merchantsCommissionsDataTable;

$(document).ready(function () {
    var $commissionTable = $('#commissions-table');
    var $merchantCommissionTable = $('#merchant-commissions-table');
    var $commissionForm = $('#edit-commission-form');
    var $merchantCommissionForm = $('#edit-merchantCommission-form');
    var $roleNameSelect = $('#roleName');
    currentLocale = $('#language').text().trim().toLowerCase();




    updateCommissionsDataTable(addCommissionRowListener);
    updateMerchantCommissionsDataTable(addMerchantCommissionRowListener);
    $($roleNameSelect).on("change", updateCommissionsDataTable);




    function addCommissionRowListener() {
        $($commissionTable).find('tbody').on('click', 'tr', function () {
            var currentRoleName = $($roleNameSelect).val();
            var rowData = commissionsDataTable.row(this).data();
            var operationType = rowData.operationType;
            var commissionValue = parseFloat(rowData.value);
            $($commissionForm).find('input[name="userRole"]').val(currentRoleName);
            $('#operationType').val(operationType);
            $($commissionForm).find('input[name="commissionValue"]').val(commissionValue);
            $('#editCommissionModal').modal();
        });
    }

    function addMerchantCommissionRowListener() {
        $($merchantCommissionTable).find('tbody').on('click', 'tr', function () {
            var rowData = merchantsCommissionsDataTable.row(this).data();
            var merchantId = rowData.merchantId;
             var currencyId = rowData.currencyId;
             var merchantName = rowData.merchantName;
             var currencyName = rowData.currencyName;
             var inputCommissionValue = parseFloat(rowData.inputCommission);
             var outputCommissionValue = parseFloat(rowData.outputCommission);
             $($merchantCommissionForm).find('input[name="merchantId"]').val(merchantId);
             $($merchantCommissionForm).find('input[name="currencyId"]').val(currencyId);
             $('#merchantName').val(merchantName);
             $('#currencyName').val(currencyName);
             $($merchantCommissionForm).find('input[name="inputValue"]').val(inputCommissionValue);
             $($merchantCommissionForm).find('input[name="outputValue"]').val(outputCommissionValue);

             $('#editMerchantCommissionModal').modal();
        });
    }





    $('#submitCommission').click(function(e) {
        e.preventDefault();
        submitCommission()
    });

    $('#submitMerchantCommission').click(function(e) {
        e.preventDefault();
        submitMerchantCommission()
    });


});


function updateMerchantCommissionsDataTable(initCallback) {
    var $merchantCommissionTable = $('#merchant-commissions-table');
    var merchantCommissionUrl = '/2a8fy7b07dxe44/getMerchantCommissions';
    if ($.fn.dataTable.isDataTable('#merchant-commissions-table')) {
        merchantsCommissionsDataTable = $($merchantCommissionTable).DataTable();
        merchantsCommissionsDataTable.ajax.url(merchantCommissionUrl).load();
    } else {
        merchantsCommissionsDataTable = $($merchantCommissionTable).DataTable({
            "ajax": {
                "url": merchantCommissionUrl,
                "dataSrc": ""
            },
            "bFilter": false,
            "paging": false,
            "order": [],
            "initComplete": initCallback,
            "bLengthChange": false,
            "bPaginate": false,
            "bInfo": false,
            "columns": [
                {
                    "data": "merchantName"
                },
                {
                    "data": "currencyName"
                },
                {
                    "data": "inputCommission"
                },
                {
                    "data": "outputCommission"
                }
            ]
        });
    }
}

function updateCommissionsDataTable(initCallback) {
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
            "initComplete": initCallback,
            "bLengthChange": false,
            "bPaginate": false,
            "bInfo": false,
            "columns": [
                {
                    "data": "operationType",
                    "render": function (data, type, row) {
                        return row.operationTypeLocalized;
                    }
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

function submitMerchantCommission() {
    var formData =  $('#edit-merchantCommission-form').serialize();
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/commissions/editMerchantCommission',
        type: 'POST',
        data: formData,
        success: function () {
            updateMerchantCommissionsDataTable();
            $('#editMerchantCommissionModal').modal('hide');
        },
         error: function (error) {
         $('#editCommissionModal').modal('hide');
         console.log(error);
         }
    });
}
