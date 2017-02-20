/**
 * Created by ValkSam
 * SYNC version for project: exrates <-> edinarCoin
 */

angular
    .module('app')
    .factory("newsManipulatorService", NewsManipulatorService);

function NewsManipulatorService($http, $sce) {

    const YOUTUBE_KEY = 'AIzaSyCPz0_w4_vQKHdi2sNTsHikKqxPXUpmTng';

    var service = {};

    service.delete = function (newsId, onSuccessCallback) {
        var url = 'news/delete';
        var data = {
            'id': newsId
        };
        $.ajax(url, {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val(),
                'Content-Type': 'application/json'
            },
            type: "POST",
            data: JSON.stringify(data),
            success: function (data) {
                if (onSuccessCallback) {
                    onSuccessCallback(data);
                }
            }
        });
    };

    service.deleteTopic = function (url, newsVariantId, onSuccessCallback) {
        var data = {
            'id': newsVariantId
        };
        $.ajax(url, {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val(),
                'Content-Type': 'application/json'
            },
            type: "POST",
            data: JSON.stringify(data),
            success: function (data) {
                if (onSuccessCallback) {
                    onSuccessCallback(data);
                }
            }
        });
    };

    service.syncToWalletSite = function (newsType, url, onSuccessCallback) {
        var data = {
            'newsType': newsType
        };
        $.ajax(url, {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val(),
                'Content-Type': 'application/json'
            },
            type: "POST",
            data: JSON.stringify(data),
            success: function (data) {
                if (onSuccessCallback) {
                    onSuccessCallback(data);
                }
            }
        });
    };

    service.loadListFromDb = function (newsType, url, lang, filter, sortMode, pageNumber, pageSize, onSuccessLoadList, onErrorLoadList) {
        var offset = pageNumber - 1;
        url = url +
            '/' + lang +
            '?offset=' + offset +
            '&limit=' + pageSize +
            '&sortAsc=' + (sortMode == 'asc') +
            '&newsType=' + newsType +
            '&filter=' + filter;
        loadPagableListFromDbByUrl(url, onSuccessLoadList, onErrorLoadList);
    };

    service.loadTopicFromDb = function (url, onSuccessLoadTopic, onErrorLoadTopic) {
        $http.get(url)
            .then(
                function (response) {
                    if (onSuccessLoadTopic) {
                        var data = response.data;
                        var topicDto = getTopicDto(data);
                        onSuccessLoadTopic(topicDto);
                    }
                },
                function (responce) {
                    if (onErrorLoadTopic) {
                        onErrorLoadTopic(responce);
                    }
                });
    };

    function getTopicDto(data) {
        var dto = {};
        if (!data) return;
        dto.id = data.id;
        dto.topicRef = data.referenceToNewstopic;
        dto.imgSrc = data.titleImageSource;
        dto.imgAlt = data.brief;
        dto.variantId = data.newsVariantId;
        dto.language = data.language;
        dto.date = data.newsVariantDate;
        dto.title = data.title;
        dto.brief = data.brief;
        dto.content = data.content;
        dto.visitCount = data.visitCount;
        dto.resource = data.resource;
        dto.newsType = data.newsType;
        dto.calendarDate = data.calendarDate;
        dto.noTitleImg = data.noTitleImg;
        return dto;
    }

    service.loadArchiveListFromDb = function (newsType, url, lang, filter, dateThresholdDaysAgo, sortMode, pageNumber, pageSize, onSuccessLoadList, onErrorLoadList) {
        var offset = pageNumber - 1;
        url = url +
            '/' + lang +
            '?offset=' + offset +
            '&limit=' + pageSize +
            '&sortAsc=' + (sortMode == 'asc') +
            '&newsType=' + newsType +
            '&dateThresholdDaysAgo=' + dateThresholdDaysAgo +
            '&filter=' + filter;
        loadPagableListFromDbByUrl(url, onSuccessLoadList, onErrorLoadList);
    };

    service.loadEventListFromDb = function (newsType, url, lang, filter, sortMode, pageNumber, pageSize, calendarDate, onSuccessLoadList, onErrorLoadList) {
        var offset = pageNumber - 1;
        url = url +
            '/' + lang +
            '?offset=' + offset +
            '&limit=' + pageSize +
            '&sortAsc=' + (sortMode == 'asc') +
            '&newsType=' + newsType +
            '&calendarDate=' + calendarDate +
            '&filter=' + filter;
        loadPagableListFromDbByUrl(url, onSuccessLoadList, onErrorLoadList);
    };

    service.loadIdListForYearMonthFromDb = function (newsTypeList, url, lang, calendarDate, onSuccessLoadList, onErrorLoadList) {
        url = url +
            '/' + lang +
            '?newsTypeList=' + newsTypeList +
            '&calendarYearMonth=' + calendarDate;
        loadDataFromDbByUrl(url, onSuccessLoadList, onErrorLoadList);
    };

    service.loadEventDataListForYearFromDb = function (newsTypeList, url, lang, calendarDate, onSuccessLoadList, onErrorLoadList) {
        url = url +
            '/' + lang +
            '?newsTypeList=' + newsTypeList +
            '&calendarYear=' + calendarDate;
        loadDataFromDbByUrl(url, onSuccessLoadList, onErrorLoadList);
    };

    service.loadFeastDayListFromDb = function (newsType, url, lang, calendarDate, onSuccessLoadList, onErrorLoadList) {
        var sortMode = "asc";
        url = url +
            '/' + lang +
            '?sortAsc=' + (sortMode == 'asc') +
            '&newsType=' + newsType +
            '&calendarDate=' + calendarDate;
        loadPagableListFromDbByUrl(url, onSuccessLoadList, onErrorLoadList);
    };

    service.loadNewsVariantTitleListFromDb = function (url, idList, onSuccessLoadTitle, onErrorLoadTitle) {
        url = url +
            '?newsVariantIdList=' + idList;
        loadDataFromDbByUrl(url, onSuccessLoadTitle, onErrorLoadTitle);
    };

    service.loadNewsVariantFromDb = function (url, id, onSuccessLoadTitle, onErrorLoadTitle) {
        url = url +
            '?newsVariantId=' + id;
        loadDataFromDbByUrl(url, onSuccessLoadTitle, onErrorLoadTitle);
    };

    function loadPagableListFromDbByUrl(url, onSuccessLoadList, onErrorLoadList) {
        $http.get(url)
            .then(
                function (response) {
                    if (onSuccessLoadList) {
                        var newsDtoList = getNewsDtoList(response.data.list);
                        setYouTubeStatistics(newsDtoList);
                        onSuccessLoadList(
                            newsDtoList,
                            response.data.pageNumber,
                            response.data.pageCount);
                    }
                },
                function () {
                    if (onErrorLoadList) {
                        onErrorLoadList();
                    }
                });
    }

    function loadDataFromDbByUrl(url, onSuccessLoad, onErrorLoad) {
        $http.get(url)
            .then(
                function (response) {
                    if (onSuccessLoad) {
                        onSuccessLoad(response.data);
                    }
                },
                function () {
                    if (onErrorLoad) {
                        onErrorLoad();
                    }
                });
    }

    function getNewsDtoList(newsList) {
        var dtoList = [];
        newsList.forEach(function (e) {
            dtoList.push({
                id: e.id,
                newsVariantId: e.newsVariantId,
                newsRef: e.referenceToNewstopic,
                imgSrc: e.titleImageSource,
                imgAlt: e.brief,
                date: e.date,
                title: e.title,
                brief: e.brief,
                tags: e.tagList,
                newsType: e.newsType,
                visitCount: e.showsCount,
                calendarDate: e.calendarDate,
                noTitleImg: e.noTitleImg,
                language: e.language,
                /*for videos*/
                originUrl: e.resource,
                url: $sce.trustAsResourceUrl(e.resource),
                autoplayUrl: $sce.trustAsResourceUrl(getAutoplayUrlForYouTubeVideo(e.resource)),
                videoId: e.ytVideoId
            });
        });
        return dtoList;
    }

    function getAutoplayUrlForYouTubeVideo(videoUrl) {
        if (!videoUrl || !videoUrl.trim().startsWith("http")) {
            return "";
        }
        if (videoUrl.indexOf('?') !== -1) {
            videoUrl = videoUrl + '&autoplay=1&enablejsapi=1';
        } else {
            videoUrl = videoUrl + '?autoplay=1&enablejsapi=1';
        }
        return videoUrl;
    }

    function setYouTubeStatistics(list) {
        var videoIds = list
            .filter(function (e) {
                return e.newsType === "VIDEO";
            })
            .map(function (a) {
                return a.videoId;
            });
        getYouTubeStatisticsForIds(videoIds, function (statisticsForEachIdArray) {
            statisticsForEachIdArray.forEach(function (item) {
                var newsListItem = list.find(function (e) {
                    return item.id === e.videoId;
                });
                if (newsListItem) {
                    newsListItem.showsCount = item.viewsCount;
                    newsListItem.likes = item.likesCount;
                    newsListItem.dislikes = item.dislikeCount;
                }
            })
        });
    }

    function getYouTubeStatisticsForIds(videoIds, callback) {
        var url = 'https://www.googleapis.com/youtube/v3/videos?part=statistics&' +
            'id=' + videoIds.toString() +
            '&key=' + YOUTUBE_KEY;
        $http.get(url)
            .then(function (response) {
                    if (response.status != 200) return;
                    var arr = response.data.items;
                    statisticsForEachIdArray = [];
                    arr.forEach(function (item) {
                            statisticsForEachIdArray.push(
                                {
                                    'id': item.id,
                                    'viewsCount': item.statistics.viewCount,
                                    'likesCount': item.statistics.likeCount,
                                    'dislikeCount': item.statistics.dislikeCount
                                }
                            );
                        }
                    );
                    if (callback) {
                        callback(statisticsForEachIdArray);
                    }
                }
            );
    }

    return service;

}