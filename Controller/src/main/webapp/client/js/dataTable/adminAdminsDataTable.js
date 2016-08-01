var adminsDataTable;

$(function () {
    if ( $.fn.dataTable.isDataTable( '#adminsTable' ) ) {
        adminsDataTable = $('#adminsTable').DataTable();
    } else {
        adminsDataTable = $('#adminsTable').DataTable({
            "ajax": {
                "url": '/admin/admins/',
                "dataSrc": ""
            },
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