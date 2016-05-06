/**
 * Created by Valk on 05.05.2016.
 */

$(function () {
    $('#upload-users-wallets').on('click', uploadUsersWalletsSummary);
});

function uploadUsersWalletsSummary() {
    $.ajax({
            url: '/admin/uploadUsersWalletsSummary',
            type: 'GET',
            success: function (data) {
                /* not works in FF
                $('<a href="data:text/plain,%EF%BB%BF' + encodeURIComponent(data) + '" download="uploadUsersWalletsSummary.csv"/a>')[0].click();*/
                var link = document.createElement('a');
                link.href = "data:text/plain;charset=utf-8,%EF%BB%BF" + encodeURIComponent(data);
                link.download = "uploadUsersWalletsSummary.csv";
                var e = document.createEvent('MouseEvents');
                e.initEvent('click', true, true);
                link.dispatchEvent(e);
            }
        }
    );
}
