/**
 * Created by ValkSam on 09.11.2016.
 */
angular
    .module('app')
    .directive("bindCompiledHtml", function ($compile) {
        return {
            scope: {
                rawHtml: "=bindCompiledHtml",
            },
            link: function (scope, elem, attrs) {
                scope.$watch('rawHtml',
                    function (rawHtml) {
                        var newElem = $compile(rawHtml)(scope.$parent);
                        if (rawHtml && newElem && newElem.length==0){
                            newElem = $compile("<div>"+rawHtml+"</div>")(scope.$parent);
                        }
                        elem.contents().remove();
                        elem.append(newElem);
                    });
            }
        };
    });

