/**
 * Created by OLEG on 30.08.2016.
 */
$(function () {

     $.ajax({
         url: '/admin/userSessions',
         type: 'GET',
         success: function (data) {
             console.log(data);
         },
         error: function (err) {
             console.log(err);
         }
     });


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
                    "data": "userNickname",
                    "render": function (data, type, row){
                        if (type == 'display') {
                            return '<a href="/admin/userInfo?id='+row['userId']+'">'+data+'</a>';
                        }
                        return data;
                    }
                },
                {
                    "data": "userEmail",
                    "render": function (data, type, row) {
                        if (type == 'display') {
                            return '<a href="mailto:' + data + '">' + data + '</a>';
                        }
                        return data;
                    }
                },
                {
                    "data": "userRole"
                },
                {
                    "data": "sessionId"
                },
                {
                    "data":null,
                    "render": function () {
                        return "<input type='image' src='/client/img/Delete_Icon_48.png' onclick='expireSession.call(this, event)' />" ;
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
    var sessionId = $button.parents('tr').children('td:nth-child(4)').text();
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