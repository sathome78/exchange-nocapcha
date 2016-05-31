/**
 * Created by Valk
 */

function addNews() {
    $("#news-add-info__add-news").attr('onclick', 'saveNewsVariant(true)');
    $(".news-add-info__date").css({'height': '100%'});
    $("#newsId").val(null);
    $('#news-add-modal').modal();
}

function addNewsVariant(newsId, resource) {
    $("#news-add-info__add-news").attr('onclick', 'saveNewsVariant(false)');
    $(".news-add-info__date").css({'height': '0'});
    $("#newsId").val(newsId);
    $("#resource").val(resource);
    $('#news-add-modal').modal();
}

function saveNewsVariant(newNews) {
    var isError = false;
    $('.input-block-wrapper__error-wrapper').toggle(false);
    if (newNews && (!$('#newsDate').val() || !$('#newsDate').val().trim().match(/\d{4}\-\d{2}\-\d{2}/))){
        $('.input-block-wrapper__error-wrapper[for=newsDate]').toggle(true);
        isError = true;
    }
    if (isError) {
        return;
    }
    $('#news-add-modal').one('hidden.bs.modal', function (e) {
        var data = new FormData($('#news-add-info__form')[0]);
        $.ajax({
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            url: '/news/addNewsVariant',
            data: data,
            cache: false,
            contentType: false,
            processData: false,
            enctype: 'multipart/form-data',
            type: 'POST',
            success: function (data) {
                successNoty(data.result);
            }
        });
    });
    $('#news-add-modal').modal('hide');
}
