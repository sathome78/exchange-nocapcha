/**
 * Created by OLEG on 23.09.2016.
 */
var rolesDataTable;
var launchSettingsDataTable;

$(document).ready(function () {
    $('#bot-enabled-box').onoff()
    var $rolesTable = $('#roles-table');





    updateRolesDataTable(rolesRowListener);
    updateLaunchSettingsDataTable();

    function rolesRowListener() {
        $($rolesTable).find('tbody').on('click', 'i', function () {
            var row = $(this).parents('tr');
            var rowData = rolesDataTable.row(row).data();
            var attribute = $(this).parents('span').data('attribute');
            rowData[attribute] = !rowData[attribute];
            $.ajax({
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                url: '/2a8fy7b07dxe44/autoTrading/roleSettings/update',
                type: 'POST',
                contentType: 'application/json;charset=UTF-8',
                data: JSON.stringify(rowData),
                success: updateRolesDataTable
            })


        });
    }



    $('#submitBotSettings').click(function(e) {
        e.preventDefault();
        submitBotSettings();
    });

    $('#submitNewBot').click(function(e) {
        e.preventDefault();
        submitNewBot();
    });
    $('#launch-settings-table').on('click', '.settings-change-btn', function (e) {
        e.preventDefault();
        $('#editSettingsModal').modal();
    })

});


function updateRolesDataTable(initCallback) {
    var $rolesTable = $('#roles-table');
    var roleUrl = '/2a8fy7b07dxe44/autoTrading/roleSettings';
    if ($.fn.dataTable.isDataTable('#roles-table')) {
        rolesDataTable = $($rolesTable).DataTable();
        rolesDataTable.ajax.url(roleUrl).load();
    } else {
        rolesDataTable = $($rolesTable).DataTable({
            "ajax": {
                "url": roleUrl,
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
                    "data": "userRole"
                },
                {
                    "data": "orderAcceptionSameRoleOnly",
                    "render": function (data) {
                        return '<span data-attribute="orderAcceptionSameRoleOnly">'.concat(data ? '<i class="fa fa-check green"></i>' : '<i class="fa fa-close red"></i>')
                            .concat('</span>');
                    }
                },
                {
                    "data": "botAcceptionAllowed",
                    "render": function (data) {
                        return '<span data-attribute="botAcceptionAllowed">'.concat(data ? '<i class="fa fa-check green"></i>' : '<i class="fa fa-close red"></i>')
                            .concat('</span>');
                    }
                }
            ]
        });
    }
}

function updateLaunchSettingsDataTable(initCallback) {
    var $launchSettingsTable = $('#launch-settings-table');
    var launchUrl = '/2a8fy7b07dxe44/autoTrading/bot/launchSettings?botId=' + $('#bot-id').val();
    if ($.fn.dataTable.isDataTable('#launch-settings-table')) {
        launchSettingsDataTable = $($launchSettingsTable).DataTable();
        launchSettingsDataTable.ajax.url(launchUrl).load();
    } else {
        launchSettingsDataTable = $($launchSettingsTable).DataTable({
            "ajax": {
                "url": launchUrl,
                "dataSrc": ""
            },
            "bFilter": false,
            "order": [],
            "initComplete": initCallback,
            "columns": [
                {
                    "data": "currencyPairName"
                },
                {
                    "data": "enabledForPair",
                    "render": function (data) {
                        return '<span class="text-1_5" data-attribute="isEnabledForPair">'.concat(data ? '<i class="fa fa-check green"></i>' : '<i class="fa fa-close red"></i>')
                            .concat('</span>');
                    }
                },
                {
                    "data": "launchIntervalInMinutes"
                },
                {
                    "data": "createTimeoutInSeconds"
                },
                {
                    "data": "quantityPerSequence"
                },
                {
                    "data": null,
                    "orderable": false,
                    "render": function () {
                        return '<button class="settings-change-btn btn btn-sm btn-danger">Change</button>';
                    }
                }
            ]
        });
    }
}

function submitBotSettings() {
    var formData =  {
        id: $('#bot-id').val(),
        userId: $('#bot-user-id').val(),
        isEnabled: $('#bot-enabled-box').prop('checked'),
        acceptDelayInSeconds: $('#timeout').val()
    };
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/autoTrading/bot/update',
        type: 'POST',
        contentType: 'application/json;charset=UTF-8',
        data: JSON.stringify(formData),
        success: function () {
            location.reload();
        }
    });
}

function submitNewBot() {
    var formData = $('#bot-creation-form').serialize();
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/autoTrading/bot/create',
        type: 'POST',
        data: formData,
        success: function () {
            location.reload();
        }
    });
}