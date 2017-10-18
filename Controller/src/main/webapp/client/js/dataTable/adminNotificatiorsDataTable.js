/**
 * Created by OLEG on 23.09.2016.
 */
var currentLocale;
var commissionsDataTable;
var merchantsCommissionsDataTable;

$(document).ready(function () {
    var $notifictorsTable = $('#notificators-table');

    var $commissionForm = $('#edit-commission-form');

    var $roleNameSelect = $('#roleName');
    currentLocale = $('#language').text().trim().toLowerCase();

    $($notifictorsTable).on('click', 'tr', function () {
        var currentRoleName = $($roleNameSelect).val();
        var rowData = commissionsDataTable.row(this).data();
        var operationType = rowData.operationType;
        var commissionValue = parseFloat(rowData.value);
        $($commissionForm).find('input[name="userRole"]').val(currentRoleName);
        $('#operationType').val(operationType);
        $($commissionForm).find('input[name="commissionValue"]').val(commissionValue);
        $('#editCommissionModal').modal();
    });






    updateCommissionsDataTable();

    $($roleNameSelect).on("change", updateCommissionsDataTable);



    $('#submitCommission').click(function(e) {
        e.preventDefault();
        submitCommission()
    });



});



function updateCommissionsDataTable() {
    var $notifictorsTable = $('#notificators-table');

    var currentRoleName = $('#roleName').val();
    var settingsUrlBase = '/2a8fy7b07dxe44/getNotificatorsSettingsForRole?role=';
    var setingsUrl = settingsUrlBase + currentRoleName;
    if ($.fn.dataTable.isDataTable('#notificators-table')) {
        commissionsDataTable = $($notifictorsTable).DataTable();
        commissionsDataTable.ajax.url(setingsUrl).load();
    } else {
        commissionsDataTable = $($notifictorsTable).DataTable({
            "ajax": {
                "url": setingsUrl,
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
                    "data": "name"
                },
                {
                    "data": "messagePrice",
                    "render": function (data, type, row) {
                        return data == null ? 'N/A' : data;
                    }
                },

                {
                    "data": "subscribePrice",
                    "render": function (data, type, row) {
                        return data == null ? 'N/A' : data;
                    }
                },
                {
                    "data": "enabled"
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


