/**
 * Created by Valk on 20.06.2016.
 */
function NewsClass() {
    if (NewsClass.__instance) {
        return NewsClass.__instance;
    } else if (this === window) {
        return new NewsClass(currentCurrencyPair);
    }
    NewsClass.__instance = this;
    /**/
    var that = this;
    /**/
    var tableNewsId = "news-table";
    /**/
    var timeOutIdForStatisticsForNews;
    var refreshIntervalForStatisticsForNews = 5000 * REFRESH_INTERVAL_MULTIPLIER;
    /**/
    var showLog = false;

    this.getNewsList = function (refreshIfNeeded) {
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getNewsList');
        }
        var $newsTable = $('#' + tableNewsId);
        var url = '/dashboard/news/' + tableNewsId + '?refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            success: function (data) {
                if (!data) return;
                if (data.length == 0 || data[0].needRefresh) {
                    var $tmpl = $('#news_table_row').html().replace(/@/g, '%');
                    $newsTable.find('.news_item').remove();
                    data.forEach(function (e) {
                        var $newItem = $(tmpl($tmpl, e));
                        $newItem.find('.news__admin__delete-news-variant').attr('onclick', 'rightSider.newsList.deleteNewsVariant('+ e.id+', "'+ e.resource+'","'+ e.variant+'")');
                        $newItem.find('.news__admin__add-news-variant').attr('onclick', 'rightSider.newsList.addNewsVariant('+ e.id+', "'+ e.resource+'")');
                        $newsTable.append($newItem);
                    });
                    blink($newsTable);
                }
                clearTimeout(timeOutIdForStatisticsForNews);
                timeOutIdForStatisticsForNews = setTimeout(function () {
                    that.getNewsList(true);
                }, refreshIntervalForStatisticsForNews);
            }
        });
    };

    this.addNews =function() {
        var newNews = true;
        $("#news-add-info__add-news").attr('onclick', 'rightSider.newsList.saveNewsVariant('+newNews+')');
        $(".news-add-info__date").css({'height': '2rem'});
        $("#newsId").val(null);
        $('#news-add-modal').modal();
    };

    this.addNewsVariant = function(newsId, resource) {
        var newNews = false;
        $("#news-add-info__add-news").attr('onclick', 'rightSider.newsList.saveNewsVariant('+newNews+')');
        $(".news-add-info__date").css({'overflow': 'hidden', 'height': '0'});
        $("#newsId").val(newsId);
        $("#resource").val(resource);
        $('#news-add-modal').modal();
    };

    this.deleteNewsVariant = function(newsId, resource, variant) {
        $("#news-delete-info__delete-news").attr('onclick', 'rightSider.newsList.removeNewsVariant()');
        $("#delete-newsId").val(newsId);
        $("#delete-newsVariant").val(variant);
        $("#delete-resource").val(resource);
        $('#news-delete-modal').modal();
    };

    this.saveNewsVariant = function(newNews) {
        var isError = false;
        $('.input-block-wrapper__error-wrapper').toggle(false);
        if (newNews && (!$('#newsDate').val() || !$('#newsDate').val().trim().match(/\d{4}\-\d{2}\-\d{2}/))) {
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
                    that.getNewsList();
                    successNoty(data.result);
                }
            });
        });
        $('#news-add-modal').modal('hide');
    };

    this.removeNewsVariant = function(newNews) {
        $('#news-delete-modal').one('hidden.bs.modal', function (e) {
            var data = $('#news-delete-info__form').serialize();
            $.ajax({
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                url: '/news/deleteNews',
                data: data,
                type: 'POST',
                success: function (data) {
                    that.getNewsList();
                    successNoty(data.result);
                }
            });
        });
        $('#news-delete-modal').modal('hide');
    };

    /*===========================================================*/
    (function init() {
        syncTableParams(tableNewsId, -1, function (data) {
            that.getNewsList();
        });
        $('#add-news-button').on('click', that.addNews);
    })();

}