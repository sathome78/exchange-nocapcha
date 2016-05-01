/**
 * Created by Valk on 05.05.2016.
 */

$(function(){
    $('#upload-users-wallets').on('click', uploadUsersWalletsSummary);
});

function uploadUsersWalletsSummary() {
    $.ajax({
            url: '/admin/uploadUsersWalletsSummary',
            type: 'GET',
            success: function (data) {
                $('<a href="data:text/plain,%EF%BB%BF' + encodeURIComponent(data) + '" download="uploadUsersWalletsSummary.csv"/a>')[0].click();
            }
        }
    );
}
