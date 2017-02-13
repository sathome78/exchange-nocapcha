/**
 * Created by ValkSam
 * SYNC version for project: exrates <-> edinarCoin
 */

angular
    .module('app')
    .controller('newsUploadCtrl', ['newsUploadService', NewsUploadCtrl]);

function NewsUploadCtrl(newsUploadService) {

    var that = this;

    this.tinymceContent = newsUploadService.tinymceContent;
    this.tinymceOptionsContent = newsUploadService.initTinyMceContent();

    this.tinymceTitle = newsUploadService.tinymceTitle;
    this.tinymceOptionsTitle = newsUploadService.initTinyMceTitle();

    this.submitNewsFromEditor = newsUploadService.submitNewsFromEditor;

}
