/**
 * Created by ValkSam on 03.11.2016.
 */

angular
    .module('app')
    .controller('rootCtrl', RootCtrl);

function RootCtrl($scope, $location, $http, $cookies, $window, rootService, languageService) {

    var controller = this;

    this.getFormattedLanguageMenu = function () {
        return languageService.languageMenu.map(function (e) {
            return languageService.convertToIsoLanguageForMenu(e).trim();
        });
    };

    this.getFormattedCurrentLanguage = function () {
        return controller.getFormattedLanguage(languageService.currentLanguage.lang);
    };

    this.getFormattedLanguage = function (lang) {
        return languageService.convertToIsoLanguageForMenu(lang);
    };

}
