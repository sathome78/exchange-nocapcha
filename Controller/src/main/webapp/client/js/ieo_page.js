
var ieoDataTable;

$(function () {

    $.ajaxSetup({
        headers:
            { 'X-CSRF-TOKEN': $('input[name="_csrf"]').attr('value') }
    });

    loadIeoTable();

    $.datetimepicker.setDateFormatter({
        parseDate: function (date, format) {
            var d = moment(date, format);
            return d.isValid() ? d.toDate() : false;
        },

        formatDate: function (date, format) {
            return moment(date).format(format);
        }
    });

    $('#start_date_create').datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm:ss',
        lang: 'ru',
        value: new Date(),
        defaultDate: new Date(),
        defaultTime: '00:00'
    });

    $('#end_date_create').datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm:ss',
        lang: 'ru'
    });


    $('#start_date_upd').datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm:ss',
        lang: 'ru'
    });

    $('#end_date_upd').datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm:ss',
        lang: 'ru'
    });

    $('#soldAt').datetimepicker({
        format: 'YYYY-MM-DD HH:mm:ss',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm:ss',
        lang: 'ru'
    });


    $('#ieoTable').on('click', 'tbody tr', function () {
        var row = ieoDataTable.row( this );
        var currentData = row.data();
        showUpdate(currentData);
    });

    $('#ieo_create').click(function () {
        $('#currencyToPairWith').val('BTC');
        $('#create_ieo').show();
    });

    $('#ieo_create_close').click(function () {
        /*clear data*/
        $("#update_ieo-form  :input:not(:checkbox):not(:button) textarea").val("");
        $('#create_ieo').hide();
    });

    $('#ieo_update_send').click(function () {
        sendUpdateIeo($('#id_upd').val());
    });

    $('#ieo_update_close').click(function () {
        /*clear data*/
        $("#update_ieo-form  :input:not(:checkbox):not(:button) textarea").val("");
        $('#update_ieo').hide();
    });

    $('#isTestIeo').click(function () {
        if ($(this).is(':checked')) {
            $('#testTxCountWrapper').show();
        } else {
            $('#testTxCountWrapper').hide();
            $('#testTxCount').val('')
        }
    });

    $('#ieo_create_send').click(function () {
        sendCreateIeo()
    });

    $('#ieo_approve_send').click(function () {
        $.ajax({
            type: "POST",
            url: "/2a8fy7b07dxe44/ieo/approve/" + $('#id_upd').val(),
            contentType: "application/json; charset=utf-8",
            success: function(data) {
                console.log(data);
                successNoty("IEO successfully ended!");
                loadIeoTable();
            },
            error: function() {
            }
        });
    });

    $('#ieo_revert_send').click(function () {
        $.ajax({
            type: "POST",
            url: "/2a8fy7b07dxe44/ieo/revert/" + $('#id_upd').val(),
            contentType: "application/json; charset=utf-8",
            success: function(data) {
                console.log(data);
                successNoty("Warning! Ieo reverted");
                loadIeoTable();
            },
            error: function() {
            }
        });
    });

    function showUpdate(data) {
        $('#id_upd').val(data.id);
        $('#currencyName').val(data.currencyName);
        $('#coinDescription').val(data.currencyDescription);
        /*$('#makerEmail').val(data.makerEmail);*/
        $('#status').val(data.status);
        $('#rate').val(data.rate);
        $('#amount').val(data.amount);
        $('#available_balance').val(data.availableBalance);
        $('#minAmount').val(data.minAmount);
        $('#maxAmountPerUser').val(data.maxAmountPerUser);
        $('#maxAmountPerClaim').val(data.maxAmountPerClaim);
        $('#start_date_upd').val(data.startDate);
        $('#end_date_upd').val(data.endDate);
        $('#createdAt').val(data.createdAt);
        $('#createdBy').val(data.createdBy);
        $('#version').val(data.version);
        $('#count_test_transactions').val(data.countTestTransactions);
        $('#is_test_ieo').prop('checked', data.testIeo);
        $('#generalDescription').val(data.description);
        $('#logo_upd').val(data.logo);
        $('#content_upd').val(data.content);
        $('#soldAt').val(data.soldAt);
        $('#policy_upd').val(data.licenseAgreement);
        $('#update_ieo').show();
        getPolicy(data.id);
    }


    function sendCreateIeo() {
        var formData = JSON.stringify($("#create_ieo_form").serializeArray().map(function(x){this[x.name] = x.value; return this;}.bind({}))[0]);
        $("#ieo_create_send").attr("disabled", true);
        $.ajax({
            type: "POST",
            url: "/2a8fy7b07dxe44/ieo",
            data: formData,
            contentType:"application/json; charset=utf-8",
            success: function(data) {
                $('#ieo_create_send').attr("disabled", false);
                successNoty("Ieo created!");
                loadIeoTable();
                $("#update_ieo-form  :input:not(:checkbox):not(:button) textarea").val("");
                $('#create_ieo').hide();
            },
            error: function(msg) {
                $('#ieo_create_send').attr("disabled", false);
                loadIeoTable();
            }
        });
    }

    function sendUpdateIeo(id) {
        var datastring = JSON.stringify($("#update_ieo-form").serializeArray().map(function(x){this[x.name] = x.value; return this;}.bind({}))[0]);
        $.ajax({
            type: "PUT",
            url: "/2a8fy7b07dxe44/ieo/" + id,
            data: datastring,
            contentType:"application/json; charset=utf-8",
            success: function(data) {
                successNoty("Ieo updated!");
                loadIeoTable();
                $("#update_ieo-form  :input:not(:checkbox):not(:button) textarea").val("");
                $('#update_ieo').hide();
            },
            error: function(errMsg) {
                errorNoty(errMsg);
                loadIeoTable();
            }
        })
    }

    $('#ieo_policy_update_send').click(function () {
        var id = $('#id_upd').val();
        var datastring = JSON.stringify($("#policyUpdForm").serializeArray().map(function(x){this[x.name] = x.value; return this;}.bind({}))[0]);
        $.ajax({
            type: "PUT",
            url: "/2a8fy7b07dxe44/ieoPolicy/" + id,
            data: datastring,
            contentType:"application/json; charset=utf-8",
            success: function(data) {
                successNoty("Ieo policy updated!");
                getPolicy(id)
            },
            error: function(errMsg) {
                errorNoty('error update ieo policy ' + errMsg);
            }
        })
    });

    function getPolicy(id) {
        $.ajax({
            type: "GET",
            url: "/2a8fy7b07dxe44/ieo/policy/" + id,
            contentType:"application/json; charset=utf-8",
            success: function(data) {
                console.log(data.data);
                $('#policy_upd').val(data.data);
            },
            error: function(errMsg) {
                $('#policy_upd').val('error loading policy: ' + errMsg);
            }
        })
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
                        "data": "maxAmountPerClaim"
                    },
                    {
                        "data": "maxAmountPerUser"
                    },
                    {
                        "data": "description"
                    },
                    {
                        "data": "testIeo"
                    },
                    {
                        "data": "soldAt"
                    },
                    {
                        "data": "content",
                        "visible": false
                    },
                    {
                        "data": "logo",
                        "visible": false
                    },
                    {
                        "data": "id",
                        "visible": false
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
