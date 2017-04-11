var onPage = 20;

var mainUser ;
var $pagination;
var search;
var defaultOpts = {
    hideOnlyOnePage:true,
    totalPages: 4,
    first: "<<",
    prev:"<",
    next:">",
    last:">>",
    onPageClick: function (event, page) {
        loadInfo(mainUser, page)
    }
};

$(function () {
    mainUser = $('#user-id').val();
    $pagination = $('#pagination-demo');
    $pagination.twbsPagination(defaultOpts);
});


function loadInfo(userId, page, port) {
    var url = '/2a8fy7b07dxe44/referralInfo?userId='+ userId
        + "&profitUser=" + mainUser + "&onPage=" + onPage + "&page=" + page;
    console.log(url);
    $.ajax({
        url: url,
        type: 'GET',
        success: function (data) {
            console.log('resp ' + JSON.stringify(data));
            append(data.referralInfoDtos, userId, port);
            if (page > 0 && data.totalPages > 0) {
                refreshPagination(data.totalPages)
            }
        }
    });
}


function ShowHide(userId) {
    if(userId) {
        var port = $(".reffil_" + userId + " ul");
        if (port.children().length > 0 ) {
            $(".reffil_" + userId).slideUp();
            port.empty();
        } else {
            var page = -1;
            loadInfo(userId, page, port)
        }

    }
}

function append(data, userId, port) {
    $("#refTemplate").template("listRefs");
    if (mainUser === userId || !port) {
        $('.reffil_' + mainUser).html($.tmpl("listRefs", data))
    } else {
        port.append($.tmpl("listRefs", data));
        $(".reffil_" + userId).slideDown();
    }
}

function refreshPagination(totalPages) {
    var currentPage = $pagination.twbsPagination('getCurrentPage');
    $pagination.twbsPagination('destroy');
    $pagination.twbsPagination($.extend({}, defaultOpts,  {
        startPage: currentPage,
        totalPages: totalPages,
        initiateStartPageClick: false
    }));
}

function searchRef(value, page) {
    var url = '/2a8fy7b07dxe44/findReferral?email=' + value + "&profitUser=" + mainUser;
    console.log('url ' + url);
    $.ajax({
        url: url,
        type: 'GET',
        success: function (data) {
            console.log('resp ' + JSON.stringify(data));
            append(data.referralInfoDtos, mainUser, $('.reffil_' + mainUser));
        }
    });
}




$( document ).ready(function() {

    $('#refSearchButton').on("click", function () {
        searchId = $('#refSearch').val();
       searchRef(searchId, 1);
        $pagination.twbsPagination('destroy');
    });

    $('#refSearchClearButton').on("click", function () {
       loadInfo(mainUser, 1);
        $('#refSearch').val("");
    });

});



