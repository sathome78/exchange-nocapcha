/**
 * Created by OLEG on 23.09.2016.
 */
var rolesDataTable;
var launchSettingsDataTable;

$(document).ready(function () {
    $('#bot-enabled-box').onoff();
    var $rolesTable = $('#roles-table');
    var $launchSettingsTable=$('#launch-settings-table');

    updateRolesDataTable();
    updateLaunchSettingsDataTable();


    $('#submitBotGeneralSettings').click(function(e) {
        e.preventDefault();
        submitBotGeneralSettings();
    });

    $('#submitNewBot').click(function(e) {
        e.preventDefault();
        submitNewBot();
    });

    $($rolesTable).on('click', 'i', function () {
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
    $($launchSettingsTable).on('click', '.launch-settings-change-btn', function (e) {
        e.preventDefault();
        var row = $(this).parents('tr');
        var rowData = launchSettingsDataTable.row(row).data();
        $('#launch-title-pair').text(rowData['currencyPairName']);
        $('#launch-settings-id').val(rowData['id']);
        $('#launch-currency-pair-id').val(rowData['currencyPairId']);
        $('#launchInterval').val(rowData['launchIntervalInMinutes']);
        $('#createTimeout').val(rowData['createTimeoutInSeconds']);
        $('#quantityPerSeq').val(rowData['quantityPerSequence']);
        $('#editLaunchSettingsModal').modal();
    });

    $($launchSettingsTable).on('click', '.trade-settings-change-btn', function (e) {
        e.preventDefault();
        var row = $(this).parents('tr');
        var rowData = launchSettingsDataTable.row(row).data();
        var orderType = $(this).data('order-type');
        $('#trade-title-pair').text(rowData['currencyPairName']);
        $('#trade-title-order-type').text(orderType);
        getTraderSettingsForCurrencyAndOrderType(rowData['id'], orderType);
        $('#editTradeSettingsModal').modal();
    });

    $('#submitLaunchSettings').click(function (e) {
        e.preventDefault();
        submitLaunchSettings();
    });

    $('#submitTradeSettings').click(function (e) {
        e.preventDefault();
        submitTradingSettings();
    });

    $($launchSettingsTable).on('click', 'i', function () {
        var row = $(this).parents('tr');
        var rowData = launchSettingsDataTable.row(row).data();
        toggleBotLaunchStatus(rowData['currencyPairId'], !rowData['isEnabledForPair']);
    });

});

function getTraderSettingsForCurrencyAndOrderType(launchSettingsId, orderType) {
    var url = '/2a8fy7b07dxe44/autoTrading/bot/tradingSettings?launchSettingsId=' + launchSettingsId + "&orderType=" + orderType;
    $.get(url, function (data) {
        $('#trade-settings-id').val(data['id']);
        $('#minAmount').val(data['minAmount']);
        $('#maxAmount').val(data['maxAmount']);
        $('#minPrice').val(data['minPrice']);
        $('#maxPrice').val(data['maxPrice']);
        $('#priceStep').val(data['priceStep']);
    })
}


function updateRolesDataTable() {
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

function updateLaunchSettingsDataTable() {
    var $launchSettingsTable = $('#launch-settings-table');
    var launchUrl = '/2a8fy7b07dxe44/autoTrading/bot/launchSettings?botId=' + $('#bot-id').val();
    var launchSettingsLoc=$('#launch-settings-loc').text();
    var buySettingsLoc=$('#buy-settings-loc').text();
    var sellSettingsLoc=$('#sell-settings-loc').text();
    if ($.fn.dataTable.isDataTable('#launch-settings-table')) {
        launchSettingsDataTable = $($launchSettingsTable).DataTable();
        launchSettingsDataTable.ajax.url(launchUrl).load();
    } else {
        launchSettingsDataTable = $($launchSettingsTable).DataTable({
            "ajax": {
                "url": launchUrl,
                "dataSrc": ""
            },
            "order": [],
            "columns": [
                {
                    "data": "currencyPairName"
                },
                {
                    "data": "isEnabledForPair",
                    "render": function (data) {
                        return '<span class="text-1_5">'.concat(data ? '<i class="fa fa-check green"></i>' : '<i class="fa fa-close red"></i>')
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
                        return '<button class="launch-settings-change-btn btn btn-sm btn-primary">' + launchSettingsLoc +'</button>';
                    }
                },
                {
                    "data": null,
                    "orderable": false,
                    "render": function () {
                        return '<button data-order-type="BUY" class="trade-settings-change-btn btn btn-sm btn-primary">' + buySettingsLoc +'</button>';
                    }
                },
                {
                    "data": null,
                    "orderable": false,
                    "render": function () {
                        return '<button data-order-type="SELL" class="trade-settings-change-btn btn btn-sm btn-primary">' + sellSettingsLoc +'</button>';
                    }
                }
            ]
        });
    }
}

function submitLaunchSettings() {
    var formData = $('#launch-settings-form').serialize();
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/autoTrading/bot/launchSettings/update',
        type: 'POST',
        data: formData,
        success: function () {
            $('#editLaunchSettingsModal').modal('hide');
            updateLaunchSettingsDataTable();
        }
    });
}

function submitTradingSettings() {
    var formData = $('#trade-settings-form').serialize();
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/autoTrading/bot/tradingSettings/update',
        type: 'POST',
        data: formData,
        success: function () {
            $('#editTradeSettingsModal').modal('hide');
        }
    });
}

function toggleBotLaunchStatus(currencyPairId, status) {
    var formData = new FormData();
    formData.append("currencyPairId", currencyPairId);
    formData.append("status", status);

    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/autoTrading/bot/launchSettings/toggle',
        type: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        success: function () {
            updateLaunchSettingsDataTable();
        }
    });
}


function submitBotGeneralSettings() {
    var formData = {
        id: $('#bot-id').val(),
        userId: $('#bot-user-id').val(),
        enabled: $('#bot-enabled-box').prop('checked'),
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