/**
 * Created by ValkSam on 04.02.2017.
 */
angular.module("app")
    .factory("aboutUsService", ['$http', '$location', 'languageService', 'newsManipulatorService', AboutUsService]);

function AboutUsService($http, $location, languageService, newsManipulatorService) {
    this.NEWS_TYPE_NAME = 'PAGE';
    this.OTHER_NEWS_LIST_LIMIT = 0;
    this.PARENT_URL = "/aboutUs";
    this.GET_TOPIC_URL = "/pageMaterials/aboutUs/content";
    this.MARK_VISIT_TOPIC_URL = "none";
    this.SUBSCRIBE_URL = "none";
    /**/
    return TopicService.call(this, $http, $location, languageService, newsManipulatorService);
}