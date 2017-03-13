/**
 * Created by OLEG on 23.09.2016.
 */
var currentLocale;

$(document).ready(function () {
    var $merchantAccessTable = $('#merchant-options-table');
    $($merchantAccessTable).DataTable({
        "ajax": {
            "url": "/2a8fy7b07dxe44/merchantAccess/data",
            "dataSrc": ""
        },
        "bFilter": true,
        "paging": false,
        "order": [],
        "bLengthChange": false,
        "bPaginate": false,
        "bInfo": false,
        "columns": [
            {
                "data": "merchantName",
            },
            {
                "data": "currencyName",
            },
            {
                "data": "isRefillBlocked",
                "render": function (data) {
                    return'<span data-operationtype="INPUT">'.
                    concat(data ? '<i class="fa fa-lock red" aria-hidden="true"></i>' : '<i class="fa fa-unlock" aria-hidden="true"></i>')
                        .concat('</span>');
                },
            },
            {
                "data": "isWithdrawBlocked",
                "render": function (data) {
                    return'<span data-operationtype="OUTPUT">'.
                    concat(data ? '<i class="fa fa-lock red" aria-hidden="true"></i>' : '<i class="fa fa-unlock" aria-hidden="true"></i>')
                        .concat('</span>');
                },
            },
        ],
        "createdRow": function ( row, data, index ) {
            $(row).attr("data-merchantid", data.merchantId);
            $(row).attr("data-currencyid", data.currencyId);
        }
    });

    $merchantAccessTable.find('tbody').on('click', 'i', function () {
        var operationType = $(this).parent().data('operationtype');
        var merchantId = $(this).parents('tr').data('merchantid');
        var currencyId = $(this).parents('tr').data('currencyid');
        if (confirm($('#prompt-toggle-block').html())) {
            toggleBlock(merchantId, currencyId, operationType, this);
        }
    });

    $('#block-all-input').click(function () {
        setBlockForAll('INPUT', true);
    });
    $('#block-all-output').click(function () {
        setBlockForAll('OUTPUT', true);
    });
    $('#unblock-all-input').click(function () {
        setBlockForAll('INPUT', false);
    });
    $('#unblock-all-output').click(function () {
        setBlockForAll('OUTPUT', false);
    });

});


function toggleBlock(merchantId, currencyId, operationType, $element) {
    var formData = new FormData();
    formData.append("merchantId", merchantId);
    formData.append("currencyId", currencyId);
    formData.append("operationType", operationType);

    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/2a8fy7b07dxe44/merchantAccess/toggleBlock',
        type: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        success: function () {
            $($element).toggleClass('fa-lock red');
            $($element).toggleClass('fa-unlock');
        }
    });
}

function setBlockForAll(operationType, blockStatus) {
    if (confirm($('#prompt-toggle-block-all').html())) {
        var columnNumber = operationType === 'INPUT' ? 3 : 4;
        var formData = new FormData();
        formData.append("operationType", operationType);
        formData.append("blockStatus", blockStatus);

        $.ajax({
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            url: '/2a8fy7b07dxe44/merchantAccess/setBlockForAll',
            type: 'POST',
            data: formData,
            contentType: false,
            processData: false,
            success: function () {
                var $targetIcons = $('#merchant-options-table').find('tr td:nth-child(' + columnNumber + ') i');
                $($targetIcons).toggleClass('fa-lock red', blockStatus);
                $($targetIcons).toggleClass('fa-unlock', !blockStatus);
            }
        });

    }



}