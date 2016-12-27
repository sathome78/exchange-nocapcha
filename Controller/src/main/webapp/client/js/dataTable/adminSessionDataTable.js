/**
 * Created by OLEG on 30.08.2016.
 */
$(function () {

     $.ajax({
         url: '/2a8fy7b07dxe44/userSessions',
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
                "url": '/2a8fy7b07dxe44/userSessions',
                "dataSrc": ""
            },
            "paging": true,
            "info": true,
            "columns": [
                {
                    "data": "userNickname",
                    "render": function (data, type, row){
                        if (type == 'display') {
                            return '<a href="/2a8fy7b07dxe44/userInfo?id='+row['userId']+'">'+data+'</a>';
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
                        return "<input type='image' src='/client/img/Delete_Icon_16.png' onclick='showConfirmModal.call(this, event)' />" ;
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

function showConfirmModal(e) {
    var element = $(this);
    var userEmail = element.parents('tr').children('td:nth-child(2)').text();
    var sessionId = element.parents('tr').children('td:nth-child(4)').text();
    $('#session-owner').text(userEmail);
    $('#session-id').text(sessionId);

    $('#expire-session-submit').on('click', function () {
        console.log(element);
        expireSession(sessionId);
        $('#expire-session-modal').modal('hide');
    });
    $('#expire-session-modal').modal();
}

function expireSession(sessionId) {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        url: '/2a8fy7b07dxe44/expireSession',
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