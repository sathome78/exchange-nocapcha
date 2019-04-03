
var ieoDataTable;

$(function () {

    function showCreate() {

    }

    $('#ieoTable').on('click', 'tbody tr', function () {
        var row = ieoDataTable.row( this );
        var currentData = row.data();
        showUpdate(currentData);
    });

    $('#ieo_create').click(function () {
        $('#create_form').toggle();
    });

    $('#ieo_update_send').click(function () {
        sendUpdateIeo($('#id_upd').text);
    });

    $('#ieo_create_send').click(function () {
        sendCreateIeo()
    });

    function showUpdate(data) {
        $('#id_upd').text(data.id);
        $('#currencyName').val(data.currencyName);
        $('#description').val(data.description);
        $('#makerEmail').val(data.makerEmail);
        $('#status').val(data.status); /*select*/
        $('#rate').val(data.rate);
        $('#amount').val(data.amount);
        $('#minAmount').val(data.minAmount);
        $('#maxAmountPerUser').val(data.maxAmountPerUser);
        $('#maxAmountPerClaim').val(data.maxAmountPerClaim);
        $('#start_date_upd').val(data.startDate); /*date*/
        $('#end_date_upd').val(data.endDate); /*date*/
        $('#createdAt').val(data.createdAt); /*date*/
        $('#createdBy').val(data.createdBy);
        $('#version').val(data.version);
        $('#update_ieo').show();
    }


    function sendCreateIeo() {
        var datastring = $("#create_ieo_form").serialize();
        $.ajax({
            type: "POST",
            url: "/2a8fy7b07dxe44/ieo",
            data: datastring,
            dataType: "json",
            success: function(data) {
                /*todo show success window*/
                loadIeoTable();
            },
            error: function() {
                /*todo show error window*/
            }
        });
    }

    function sendUpdateIeo(id) {
        var datastring = $("#update_ieo-form").serialize();
        $.ajax({
            type: "PUT",
            url: "/2a8fy7b07dxe44/ieo/" + id,
            data: datastring,
            dataType: "json",
            success: function(data) {
                /*todo show success window*/
               loadIeoTable();
            },
            error: function() {
                /*todo show error window*/
            }
        });
    }




    function loadIeoTable() {
        var url = '/2a8fy7b07dxe44/ieo/all';
        if ($.fn.dataTable.isDataTable('#ieoTable')) {
            ieoDataTable = $('#ieoTable').DataTable();
            ieoDataTable.ajax.url(url).load()
        } else {
            ieoDataTable = $('#ieoTable').DataTable({
                "order": [
                    [
                        0,
                        "asc"
                    ]
                ],
                "deferRender": true,
                "paging": true,
                "info": true,
                "ajax": {
                    "url": url,
                    "dataSrc": ""
                },
                "columns": [
                    {
                        "data": "id",
                        "visible": false
                    },
                    {
                        "data": "currencyDescription"
                    },
                    {
                        "data": "currencyName"
                    },
                    {
                        "data": "status"
                    },
                    {
                        "data": "contributors"
                    },
                    {
                        "data": "priceString"
                    },
                    {
                        "data": "availableBalance"
                    },
                    {
                        "data": "startDate"
                    },
                    {
                        "data": "endDate"
                    },
                    {
                        "data": "amount"
                    },
                    {
                        "data": "minAmount"
                    },
                    {
                        "data": "maxAmountPerUser"
                    },
                    {
                        "data": "maxAmountPerClime"
                    },
                    {
                        "data": "createdAt",
                        "visible": false
                    },
                    {
                        "data": "rate",
                        "visible": false
                    },
                    {
                        "data": "createdBy",
                        "visible": false
                    },
                    {
                        "data": "version",
                        "visible": false
                    }


                ],
                "destroy" : true
            });
        }
    }
});