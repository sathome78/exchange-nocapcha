/**
 * Created by ValkSam
 * SYNC version for project: exrates <-> edinarCoin
 */

function TopicService($http, $location, languageService, newsManipulatorService) {
    const NEWS_TYPE_NAME = this.NEWS_TYPE_NAME;
    const OTHER_NEWS_LIST_LIMIT = this.OTHER_NEWS_LIST_LIMIT;
    const PARENT_URL = this.PARENT_URL;
    const GET_NEWS_LIST_URL = this.GET_NEWS_LIST_URL ? this.GET_NEWS_LIST_URL : '/newsList';
    const GET_TOPIC_URL = this.GET_TOPIC_URL ? this.GET_TOPIC_URL : $location.path() + "/content";
    const MARK_VISIT_TOPIC_URL = this.MARK_VISIT_TOPIC_URL ? (this.MARK_VISIT_TOPIC_URL == "none" ? undefined : this.MARK_VISIT_TOPIC_URL) : $location.path() + "/visit";
    const SUBSCRIBE_URL = this.SUBSCRIBE_URL ? (this.SUBSCRIBE_URL == "none" ? undefined : this.MARK_VISIT_TOPIC_URL) : "/news/subscribe";
    const DELETE_TOPIC_URL = this.DELETE_TOPIC_URL ? this.DELETE_TOPIC_URL : "news/newstopic/delete";

    var service = {};

    service.topic = {};

    service.otherList = [];

    service.topicVisits = 0;

    var storedTopicRef;

    service.topicSubscribeForm = {
        'name': '',
        'email': '',
        'url': SUBSCRIBE_URL,
        'sendButtonDisabled': false,
        'newsType': NEWS_TYPE_NAME
    };

    service.getTopic = function () {
        return service.topic;
    };

    service.getOtherList = function () {
        return service.otherList;
    };

    service.getTopicVisits = function () {
        return service.topic.visitCount;
    };

    service.initTopic = function () {
        if ($.isEmptyObject(service.topic) || ($location.path() != storedTopicRef)) {
            var lang = languageService.getCurrentLanguage();
            storeVisitThisTopic();
            service.loadTopicFromDb();
            loadOtherListFromDb(lang);
        }
    };

    service.deleteTopic = function () {
        newsManipulatorService.deleteTopic(DELETE_TOPIC_URL, service.topic.variantId, onSuccessDeleteCallback);
    };

    function onSuccessDeleteCallback(data) {
        if (PARENT_URL) {
            languageService.redirect(PARENT_URL);
        }
        successNoty(data.callbackMessage);
    }

    function storeVisitThisTopic() {
        var url = MARK_VISIT_TOPIC_URL;
        if (!url) {
            service.topic.visitCount = 0;
            return;
        }
        $http.get(url)
            .then(function (response) {
                var data = response.data;
                if (!data) return;
                if (data != -1) {
                    service.topic.visitCount = data;
                }
            });
    }

    service.loadTopicFromDb = function () {
        var url = GET_TOPIC_URL;
        newsManipulatorService.loadTopicFromDb(url, onSuccessLoadTopic, onErrorLoadTopic);
    };

    function onSuccessLoadTopic(data) {
        service.topic.id = data.id;
        service.topic.topicRef = data.topicRef;
        service.topic.imgSrc = !data.imgSrc ? "" : data.imgSrc;
        service.topic.imgAlt = data.imgAlt;
        service.topic.variantId = data.variantId;
        service.topic.language = data.language;
        service.topic.date = data.date;
        service.topic.title = data.title;
        service.topic.brief = data.brief;
        service.topic.content = data.content;
        service.topic.visitCount = data.visitCount;
        service.topic.resource = data.resource;
        service.topic.newsType = data.newsType;
        service.topic.calendarDate = data.calendarDate;
        service.topic.noTitleImg = data.noTitleImg;
        storedTopicRef = service.topic.topicRef;
    }

    function onErrorLoadTopic(responce) {
        for (field in service.topic) {
            service.topic[field] = null;
        }
        errorNoty(responce.data.cause);
    }

    function loadOtherListFromDb(lang) {
        if (OTHER_NEWS_LIST_LIMIT == 0) {
            return;
        }
        var list = [];
        var url = GET_NEWS_LIST_URL +
            '/' + lang +
            '?offset=0&limit=' + OTHER_NEWS_LIST_LIMIT +
            '&sortAsc=' + false +
            '&newsType=' + NEWS_TYPE_NAME +
            '&excludeId=' + getCurrentTopicIdFromUrl();
        $http.get(url)
            .then(function (response) {
                var data = response.data.list;
                if (!data) return;
                data.forEach(function (e) {
                    list.push({
                        id: e.id,
                        topicRef: e.referenceToNewstopic,
                        imgSrc: e.titleImageSource,
                        imgAlt: e.brief,
                        date: e.date,
                        title: e.title,
                        brief: e.brief
                    });
                });
                storeOtherList(list);
            });
    }

    function getCurrentTopicIdFromUrl() {
        var regexp = /(\d+)\/?[\D]*$/g;
        var match = regexp.exec($location.path());
        return match[1];
    }

    function storeOtherList(otherList) {
        service.otherList.length = 0;
        service.otherList.push.apply(service.otherList, otherList);
    }

    return service;
}
