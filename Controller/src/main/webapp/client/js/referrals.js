$( document ).ready(function() {
    var userId = $('#user-id').val();
    ShowHide(userId)
});


function ShowHide(userId) {
    if(userId) {
        var port = $(".reffil_" + userId + " ul");
        if (port.children().length > 0 ) {
            $(".reffil_" + userId).slideUp();
            $(".reffil_" + userId + " ul").empty();
        } else {
            var profitUser = $('#user-id').val();
            $.ajax({
                url: '/2a8fy7b07dxe44/referralInfo?userId=' + userId + "&profitUser=" + profitUser,
                type: 'GET',
                success: function (data) {
                    $("#refTemplate").template("listRefs");
                    console.log(data);
                    port.append($.tmpl("listRefs", data));
                    $(".reffil_" + userId).slideDown();
                }
            });
        }

    }
}
