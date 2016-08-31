/**
 * Created by OLEG on 30.08.2016.
 */
$(function () {

     if ( $.fn.dataTable.isDataTable( '#user_sessions' ) ) {
        usersDataTable = $('#user_sessions').DataTable();
    } else {
        usersDataTable = $('#user_sessions').DataTable({
            "ajax": {
                "url": '/admin/userSessions',
                "dataSrc": ""
            },
            "paging": true,
            "info": true,
            "columns": [
                {
                    "data": "username"
                },
                {
                    "data": "sessionId"
                },
                {
                    "data":null,
                    "render": function () {
                        return "<button class='btn btn-danger full-width' onclick='expireSession.call(this, event)'>" +
                            "<span class='glyphicon glyphicon-remove'></span></button>";
                    }
                }

            ],
            "order": [
                [
                    0,
                    "asc"
                ]
            ]
        });
    }





});

function expireSession(e) {
    var $button = $(this);
    var sessionId = $button.parents('tr').children('td:nth-child(2)').text();
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    console.log(sessionId);
    $.ajax({
        url: '/admin/expireSession',
        type: 'POST',
        headers: {
            'X-CSRF-Token': token
        },
        data: {
            "sessionId": sessionId
        },
        success: function (data) {
            refreshTable();
        },
        error: function (err) {
            console.log(err);
        }
    });
}

function refreshTable() {
    usersDataTable.ajax.reload();
}