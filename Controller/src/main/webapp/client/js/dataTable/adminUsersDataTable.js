var usersDataTable;

$(function () {
    if ( $.fn.dataTable.isDataTable( '#usersTable' ) ) {
        usersDataTable = $('#usersTable').DataTable();
    } else {
        usersDataTable = $('#usersTable').DataTable({
            "ajax": {
                "url": '/admin/users/',
                "dataSrc": ""
            },
            "paging": true,
            "info": true,
            "columns": [
                {
                    "data": "id",
                    "render": function (data, type, row){
                        if (type == 'display') {
                            return '<a href="/admin/userInfo?id='+data+'">'+row['nickname']+'</a>';
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