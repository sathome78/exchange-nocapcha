/**
 * Created by ValkSam on 04.02.2017.
 */
angular.module("app")
    .controller("aboutUsCtrl", ['$anchorScroll', 'aboutUsService', 'newsUploadService', AboutUsCtrl]);

function AboutUsCtrl($anchorScroll, topicService, newsUploadService) {
    this.NEWS_TYPE_NAME = 'PAGE';
    this.TOP_MENU_NUMBER = null;
    this.EDITOR_FORM_SELECTOR = '#news-pageMaterials-add-modal';
    var subscribeService = null;
    var rootService = null;
    TopicCtrl.call(this, $anchorScroll, topicService, newsUploadService, subscribeService, rootService);
}