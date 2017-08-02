/**
 * Created by OLEG on 23.09.2016.
 */
var rolesDataTable;

$(document).ready(function () {
    var $rolesTable = $('#roles-table');





    updateRolesDataTable(rolesRowListener);

    function rolesRowListener() {
        $($rolesTable).find('tbody').on('click', 'i', function () {
            var row = $(this).parents('tr');
            var rowData = rolesDataTable.row(row).data();
            console.log(rowData)
        });
    }



    /*$('#submitCommission').click(function(e) {
        e.preventDefault();
        submitCommission()
    });
*/

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
                        return '<span>'.concat(data ? '<i class="fa fa-check green"></i>' : '<i class="fa fa-close red"></i>')
                            .concat('</span>');
                    }
                },
                {
                    "data": "botAcceptionAllowed",
                    "render": function (data) {
                        return '<span>'.concat(data ? '<i class="fa fa-check green"></i>' : '<i class="fa fa-close red"></i>')
                            .concat('</span>');
                    }
                }
            ]
        });
    }
}

/*
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
            updateRolesDataTable();

            $('#editCommissionModal').modal('hide');
        },
        error: function (error) {
            $('#editCommissionModal').modal('hide');
            console.log(error);
        }
    });
}
*/
