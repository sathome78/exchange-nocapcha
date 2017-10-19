
var currentLocale;
var commissionsDataTable;

$(document).ready(function () {
    var $notifictorsTable = $('#notificators-table');

    var $commissionForm = $('#edit-commission-form');

    var $roleNameSelect = $('#roleName');
    currentLocale = $('#language').text().trim().toLowerCase();

    $($notifictorsTable).on('click', 'tr', function () {
        var currentRoleName = $($roleNameSelect).find('option:selected').text();
        var rowData = commissionsDataTable.row(this).data();
        var notificatorName = rowData.name;
        var value;
        if (rowData.payTypeEnum == 'PAY_FOR_EACH') {
            value = rowData.messagePrice;
        } else if (rowData.payTypeEnum == 'PREPAID_LIFETIME') {
            value = rowData.subscribePrice;
        } else {
            return;
        }
        $('#notificator_id').val(rowData.id);
        $($commissionForm).find('input[name="userRole"]').val(currentRoleName);
        $('#notificatorName').val(notificatorName);
        $($commissionForm).find('input[name="commissionValue"]').val(value);
        $('#editSettings').modal();
    });

    $($notifictorsTable).on('click', 'i', function (e) {
        var event = e || window.event;
        event.stopPropagation();
        var row = $(this).parents('tr');
        var rowData = commissionsDataTable.row(row).data();
        var notificatorId = rowData.id;
        var enabled = !rowData.enabled;
        toggleEnabledNotificatorSetting(notificatorId, enabled)
    });

    updateSettingsDataTable();

    $($roleNameSelect).on("change", updateSettingsDataTable);

    $('#submitCommission').click(function(e) {
        e.preventDefault();
        submitPrice()
    });

});



function updateSettingsDataTable() {
    var $notifictorsTable = $('#notificators-table');
    var roleId = $('#roleName').val();
    var settingsUrl = '/2a8fy7b07dxe44/getNotificatorsSettings?roleId=' + roleId;
    if ($.fn.dataTable.isDataTable('#notificators-table')) {
        commissionsDataTable = $($notifictorsTable).DataTable();
        commissionsDataTable.ajax.url(settingsUrl).load();
    } else {
        commissionsDataTable = $($notifictorsTable).DataTable({
            "ajax": {
                "url": settingsUrl,
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
                    "data": "enabled",
                    "render": function (data, type, row) {
                        return '<span>'.concat(data ? '<i class="fa fa-check green text-1_5" style="cursor: pointer"></i>' : '<i class="fa fa-close red text-1_5" style="cursor: pointer"></i>')
                            .concat('</span>');
                    }
                }
            ]
        });
    }
}

function submitPrice() {
    var $form =  $('#edit-commission-form');
    var currentRoleId = $('#roleName').val();
    var price = $form.find('input[name="commissionValue"]').val();
    var notificatorId =   $('#notificator_id').val();
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/setNotificatorsSetting?notificatorId='
        + notificatorId + '&roleId=' + currentRoleId + '&price=' + price,
        type: 'POST',
        success: function () {
            updateSettingsDataTable();
            $('#editSettings').modal('hide');
        },
        error: function (error) {
            $('#editSettings').modal('hide');
            console.log(error);
        }
    });
}

function toggleEnabledNotificatorSetting(notificatorId, enabled) {
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/notificatorSettings/enable?notificatorId=' + notificatorId + '&enable=' + enabled,
        type: 'POST',
        contentType: false,
        processData: false,
        success: function () {
            updateSettingsDataTable();
        }
    });
}


