var usersDataTable;

$(function () {
    if ( $.fn.dataTable.isDataTable( '#usersTable' ) ) {
        usersDataTable = $('#usersTable').DataTable();
    } else {
        usersDataTable = $('#usersTable').DataTable({
            "ajax": {
                "url": '/admin/usersList',
                "dataSrc": "data"
            },
            "serverSide": true,
            "paging": true,
            "info": true,
            "columns": [
                {
                    "data": "nickname",
                    "render": function (data, type, row){
                         if (type == 'display') {
                            return '<a href="/admin/userInfo?id='+row['id']+'">'+data+'</a>';
                        }
                        return data;
                    }
                },
                {
                    "data": "email",
                    "render": function (data, type, row) {
                        if (type == 'display') {
                            return '<a href="mailto:' + data + '">' + data + '</a>';
                        }
                        return data;
                    }
                },
                {
                    "data": "regdate"
                },
                {
                    "data": "role"
                },
                {
                    "data": "status"
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