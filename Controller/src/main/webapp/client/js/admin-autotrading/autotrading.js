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
        $('#trade-title-pair').text(rowData['currencyPairName']);
        getTradingSettingsForCurrency(rowData['id']);
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

    $($launchSettingsTable).on('click', 'i.enable-for-pair', function () {
        var row = $(this).parents('tr');
        var rowData = launchSettingsDataTable.row(row).data();
        toggleBotLaunchStatus(rowData['currencyPairId'], !rowData['enabledForPair']);
    });

    $($launchSettingsTable).on('click', 'i.consider-user-orders', function () {
        var row = $(this).parents('tr');
        var rowData = launchSettingsDataTable.row(row).data();
        toggleConsiderUserOrders(rowData['id'], !rowData['userOrderPriceConsidered']);
    });

});

function getTradingSettingsForCurrency(launchSettingsId) {
    var url = '/2a8fy7b07dxe44/autoTrading/bot/tradingSettings?launchSettingsId=' + launchSettingsId;
    $.get(url, function (data) {
        var sellData = data['SELL'];
        var buyData = data['BUY'];

        $('#trade-settings-id-sell').val(sellData['id']);
        $('#trade-settings-id-buy').val(buyData['id']);

        $('#minAmountSell').val(sellData['minAmount']);
        $('#maxAmountSell').val(sellData['maxAmount']);
        $('#minPriceSell').val(sellData['minPrice']);
        $('#maxPriceSell').val(sellData['maxPrice']);
        $('#priceStepSell').val(sellData['priceStep']);

        $('#minAmountBuy').val(buyData['minAmount']);
        $('#maxAmountBuy').val(buyData['maxAmount']);
        $('#minPriceBuy').val(buyData['minPrice']);
        $('#maxPriceBuy').val(buyData['maxPrice']);
        $('#priceStepBuy').val(buyData['priceStep']);

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
                    },
                    "className": "text-center"
                },
                {
                    "data": "botAcceptionAllowedOnly",
                    "render": function (data) {
                        return '<span data-attribute="botAcceptionAllowedOnly">'.concat(data ? '<i class="fa fa-check green"></i>' : '<i class="fa fa-close red"></i>')
                            .concat('</span>');
                    },
                    "className": "text-center"
                },
                {
                    "data": "consideredForPriceRange",
                    "render": function (data) {
                        return '<span data-attribute="consideredForPriceRange">'.concat(data ? '<i class="fa fa-check green"></i>' : '<i class="fa fa-close red"></i>')
                            .concat('</span>');
                    },
                    "className": "text-center"
                }
            ]
        });
    }
}

function updateLaunchSettingsDataTable() {
    var $launchSettingsTable = $('#launch-settings-table');
    var launchUrl = '/2a8fy7b07dxe44/autoTrading/bot/launchSettings?botId=' + $('#bot-id').val();
    var launchSettingsLoc=$('#launch-settings-loc').text();
    var tradingSettingsLoc=$('#trading-settings-loc').text();
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
                    "data": "enabledForPair",
                    "render": function (data) {
                        return '<span class="text-1_5">'.concat(data ? '<i class="fa fa-check green enable-for-pair"></i>' :
                            '<i class="fa fa-close red enable-for-pair"></i>')
                            .concat('</span>');
                    }
                },
                {
                    "data": "userOrderPriceConsidered",
                    "render": function (data) {
                        return '<span class="text-1_5">'.concat(data ? '<i class="fa fa-check green consider-user-orders"></i>' :
                            '<i class="fa fa-close red consider-user-orders"></i>')
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
                        return '<button class="trade-settings-change-btn btn btn-sm btn-primary">' + tradingSettingsLoc +'</button>';
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
            launchSettingsDataTable.ajax.reload(null, false);
        }
    });
}

function submitTradingSettings() {
    var formDataSell = $('#trade-settings-form-sell').serializeArray();
    var formDataBuy = $('#trade-settings-form-buy').serializeArray();
    var formData = [];
    formData.push(serializedArrayToJsonObject(formDataSell));
    formData.push(serializedArrayToJsonObject(formDataBuy));
    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/autoTrading/bot/tradingSettings/update',
        type: 'POST',
        data: JSON.stringify(formData),
        contentType: 'application/json',
        success: function () {
            $('#editTradeSettingsModal').modal('hide');
        }
    });
}

function serializedArrayToJsonObject(array) {
    var result = {};
    array.forEach(function (t) {
        result[t['name']] = t['value'];
    });
    return result;
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
            launchSettingsDataTable.ajax.reload(null, false);
        }
    });
}

function toggleConsiderUserOrders(settingsId, considerUserOrders) {
    var formData = new FormData();
    formData.append("launchSettingsId", settingsId);
    formData.append("considerUserOrders", considerUserOrders);

    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/autoTrading/bot/launchSettings/userOrders/toggle',
        type: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        success: function () {
            launchSettingsDataTable.ajax.reload(null, false);
        }
    });
}


function submitBotGeneralSettings() {
    var formData = {
        id: $('#bot-id').val(),
        userId: $('#bot-user-id').val(),
        enabled: $('#bot-enabled-box').prop('checked'),
        acceptDelayInMillis: $('#timeout').val()
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