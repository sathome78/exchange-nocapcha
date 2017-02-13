/**
 * Created by ValkSam
 * SYNC version for project: exrates <-> edinarCoin
 */

function TopicCtrl($anchorScroll, topicService, newsUploadService, subscribeService, rootService) {
    const NEWS_TYPE_NAME = this.NEWS_TYPE_NAME;
    const TOP_MENU_NUMBER = this.TOP_MENU_NUMBER;
    const EDITOR_FORM_SELECTOR = this.EDITOR_FORM_SELECTOR;

    var controller = this;

    this.topic = topicService.getTopic();
    this.otherNewsList = topicService.getOtherList();
    /**/
    this.topicVisits = topicService.getTopicVisits;
    /**/
    this.subscribeForm = topicService.topicSubscribeForm;
    this.sendEmailWithCallbackToken = function () {
        if (subscribeService) {
            subscribeService.subscr7ibe(controller.subscribeForm);
        }
    };
    /**/
    this.showEditForm = function () {
        newsUploadService.showForm(EDITOR_FORM_SELECTOR, NEWS_TYPE_NAME, topicService);
    };

    this.deleteTopic = function () {
        topicService.deleteTopic();
    };

    initCtrl();

    function initCtrl() {
        if (rootService) {
            rootService.setActiveMenuItem(TOP_MENU_NUMBER);
        }
        topicService.initTopic();
        $anchorScroll.yOffset = 1000;
        $anchorScroll('primary');
    }

}


