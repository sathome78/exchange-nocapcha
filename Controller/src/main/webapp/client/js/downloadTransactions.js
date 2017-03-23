/**
 * Created by maks on 21.03.2017.
 */


$(document).ready(function() {

    $.datetimepicker.setDateFormatter({
        parseDate: function (date, format) {
            var d = moment(date, format);
            return d.isValid() ? d.toDate() : false;
        },

        formatDate: function (date, format) {
            return moment(date).format(format);
        }
    });

    $('#trans_download_start').datetimepicker({
        format: 'YYYY-MM-DD HH:mm',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        onShow:function( ct ){
            this.setOptions({
                maxDate:jQuery('#trans_download_end').val()?jQuery('#trans_download_end').val():false
            })
        },
        lang:'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });
    $('#trans_download_end').datetimepicker({
        format: 'YYYY-MM-DD HH:mm',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        onShow:function( ct ){
            this.setOptions({
                minDate:jQuery('#trans_download_start').val()?jQuery('#trans_download_start').val():false
            })
        },
        lang:'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });


    $('#download_trans_history').click(function() {
        var id = $("#user-id").val();
        window.location.replace("/2a8fy7b07dxe44/downloadTransactionsPage?id=" + id);
    });


    $('#download_trans_history_action').click(function() {
        var formParams = $('#transactions_history_download_form').serialize();
        var url = '/2a8fy7b07dxe44/downloadTransactions?id=' + $("#user-id").val() +'&' + formParams;
        window.open(url);
    });

    $('#trans_download_start, #trans_download_end').on('change', function() {
        var formParams = $('#transactions_history_download_form').serialize();
        var url = '/2a8fy7b07dxe44/transactions?id=' + $("#user-id").val() +'&' + formParams;
        transactionsDataTable.ajax.url(url).load();
    } );

});



