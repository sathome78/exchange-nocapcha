/**
 * Created by ValkSam
 * SYNC version for project: exrates <-> edinarCoin
 */

angular
    .module('app')
    .factory('newsUploadService', NewsUploadService);

function NewsUploadService($http, $location, languageService) {

    const TINYMCE_URL = '/client/js/lib/tinymce/';

    var service = {};

    var modalForm;

    service.topicService;

    service.tinymceContent = {
        'language': languageService.getCurrentLanguage(),
    };

    service.tinymceTitle = {};

    service.showForm = function (modalFormSelector, newsType, topicService, calendarDate) {
        service.topicService = topicService;
        modalForm = angular.element(modalFormSelector);
        modalForm.modal();
        if (topicService) {
            setTinymceContent(topicService.getTopic());
            setTinymceTitle(topicService.getTopic().imgSrc);
            service.tinymceContent.hideLanguagePanel = false;
        } else {
            setTinymceContent({
                'id': '',
                'language': languageService.getCurrentLanguage(),
                'newsType': newsType,
                "calendarDate": calendarDate,
            });
            service.tinymceContent.hideLanguagePanel = true;
            setTinymceTitle();
        }
    };

    service.showEditVideoForm = function (modalFormSelector, newsType, video) {
        angular.element(modalFormSelector).modal();
        service.tinymceContent.id = video.id;
        service.tinymceContent.variantId = video.variantId;
        service.tinymceContent.language = video.language;
        service.tinymceContent.title = video.title;
        service.tinymceContent.brief = video.brief;
        service.tinymceContent.resource = video.originUrl;
        service.tinymceContent.newsType = video.newsType;
        service.tinymceContent.notifySubscribers = !video.variantId;
        service.tinymceContent.hideLanguagePanel = false;
    };

    function setTinymceContent(newsTopic) {
        service.tinymceContent.id = newsTopic.id;
        service.tinymceContent.variantId = newsTopic.variantId;
        service.tinymceContent.language = newsTopic.language;
        service.tinymceContent.title = newsTopic.title;
        service.tinymceContent.brief = newsTopic.brief;
        service.tinymceContent.html = newsTopic.content;
        service.tinymceContent.resource = newsTopic.resource;
        service.tinymceContent.newsType = newsTopic.newsType;
        service.tinymceContent.notifySubscribers = !newsTopic.variantId;
        service.tinymceContent.calendarDate = (!newsTopic.calendarDate) ? new Date() : new Date(newsTopic.calendarDate);
        service.tinymceContent.showTitleImg = !newsTopic.noTitleImg;
    }

    function setTinymceTitle(imgSrc) {
        if (imgSrc) {
            service.tinymceTitle.html = '<img src=' + imgSrc + '/>';
        } else {
            service.tinymceTitle.html = 'the title image is not defined ! ';
        }
    }

    $(function fixTheProblemWithNotFocusedAndReadonlyFieldsInTinyMceUploadForm() {
        $(document).on('focusin', function (e) {
            if ($(e.target).closest(".mce-window").length) {
                e.stopImmediatePropagation();
            }
        });
    });

    service.initTinyMceContent = function () {
        var language = languageService.getCurrentLanguage();
        language = ['ru', 'en'].indexOf(language) >= 0 ? language : 'en';
        var languageUrl = TINYMCE_URL.concat('langs/' + language + '.js');
        return {
            selector: '#news-content',
            height: 500,
            theme: 'modern',
            plugins: [
                'advlist autolink lists link image charmap print preview hr anchor pagebreak',
                'searchreplace wordcount visualblocks visualchars code fullscreen',
                'insertdatetime media nonbreaking table contextmenu directionality',
                'emoticons template paste textcolor colorpicker textpattern imagetools youtube'
            ],
            toolbar1: 'insertfile undo redo | bold italic | fontselect fontsizeselect | alignleft aligncenter alignright alignjustify | ' +
            'bullist numlist outdent indent',
            toolbar2: 'print preview | forecolor backcolor emoticons | link | image | youtube | code',
            language_url: languageUrl,
            image_advtab: false,
            relative_urls: false,
            image_title: false,
            link_title: false,
            image_description: false,
            image_dimensions: false,
            // valid_elements : 'a*[!href|download:|target:"_self"]',
            font_formats: "Andale Mono=andale mono,times;" +
            "Arial=arial,helvetica,sans-serif;" +
            "Arial Black=arial black,avant garde;" +
            "Book Antiqua=book antiqua,palatino;" +
            "Comic Sans MS=comic sans ms,sans-serif;" +
            "Courier New=courier new,courier;" +
            "Georgia=georgia,palatino;" +
            "Helvetica=helvetica;" +
            "Impact=impact,chicago;" +
            "Symbol=symbol;" +
            "Tahoma=tahoma,arial,helvetica,sans-serif;" +
            "Terminal=terminal,monaco;" +
            "Times New Roman=times new roman,times;" +
            "Trebuchet MS=trebuchet ms,geneva;" +
            "Verdana=verdana,geneva;" +
            "Webdings=webdings;" +
            "Wingdings=wingdings,zapf dingbats",
            fontsize_formats: '8pt 10pt 12pt 14pt 18pt 24pt 36pt',
            link_context_toolbar: true,
            target_list: false,
            file_picker_callback: function (callback, value, meta) {
                var input = document.createElement('input');
                input.setAttribute('type', 'file');
                input.setAttribute('accept', 'image/*');
                input.onchange = function () {
                    if (meta.filetype == "image") {
                        insertImageInContent(input, function (data) {
                            if (data.location) {
                                callback(data.location);
                            }
                        })
                    } else if (meta.filetype == "file") {
                        insertFileLinkInContent(input, function (data) {
                            if (data.location) {
                                callback(data.location, {text: 'Enter here text of the link label'});
                            }
                        })
                    }
                };
                input.click();
            },
            file_picker_types: 'file image',
            content_css: [
                '//fonts.googleapis.com/css?family=Lato:300,300i,400,400i',
                '//www.tinymce.com/css/codepen.min.css'
            ]
        }
    };

    service.initTinyMceTitle = function () {
        return {
            selector: '#news-title',
            height: 50,
            theme: 'modern',
            plugins: [
                'image '
            ],
            menubar: '',
            toolbar1: ' image ',
            image_advtab: false,
            relative_urls: false,
            image_title: false,
            image_description: false,
            image_dimensions: false,
            file_picker_callback: function (callback, value, meta) {
                console.log(meta.filetype);
                var input = document.createElement('input');
                input.setAttribute('type', 'file');
                input.setAttribute('accept', 'image/*');
                input.onchange = function () {
                    insertImageInContent(input, function (data) {
                        if (data.location) {
                            callback(data.location);
                        }
                    })
                };
                input.click();
            },
            file_picker_types: 'image',
        }
    };

    function insertImageInContent(input, callbackOnSuccess) {
        var data = new FormData();
        data.append("newsId", service.tinymceContent.id);
        data.append("newsType", service.tinymceContent.newsType);
        data.append('file', $(input)[0].files[0]);
        $.ajax('/news/upload/image', {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: "POST",
            contentType: false,
            processData: false,
            data: data,
            success: function (data) {
                callbackOnSuccess(data);
            }
        });
    }

    function insertFileLinkInContent(input, callbackOnSuccess) {
        var data = new FormData();
        data.append("newsId", service.tinymceContent.id);
        data.append("newsType", service.tinymceContent.newsType);
        data.append('file', $(input)[0].files[0]);
        $.ajax('/news/upload/file', {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: "POST",
            contentType: false,
            processData: false,
            data: data,
            success: function (data) {
                callbackOnSuccess(data);
            }
        });
    }

    service.submitNewsFromEditor = function () {
        window.setTimeout(function () {
            var dto = {
                id: service.tinymceContent.id,
                variantId: service.tinymceContent.variantId,
                title: service.tinymceContent.title,
                brief: service.tinymceContent.brief,
                content: service.tinymceContent.html,
                resource: service.tinymceContent.resource,
                language: languageService.convertToApplicationLanguage(service.tinymceContent.language),
                titleImgHtml: service.tinymceTitle.html,
                notifySubscribers: service.tinymceContent.notifySubscribers,
                newsType: service.tinymceContent.newsType,
                calendarDate: dateToStr(service.tinymceContent.calendarDate),
                noTitleImg: !service.tinymceContent.showTitleImg,
                newsTopicUrl: $location.absUrl()
            };
            $.ajax("/news/addNews", {
                headers: {
                    'X-CSRF-Token': $("input[name='_csrf']").val()
                },
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(dto),
                success: function (data) {
                    modalForm.one('hidden.bs.modal', function (e) {
                        if (updatedTheCurrentTopic()) {
                            service.topicService.loadTopicFromDb();
                        }
                        languageService.changeLocale(service.tinymceContent.language);
                        service.tinymceContent.id = data.id;
                        successNoty(data.callbackMessage);
                    });
                    modalForm.modal('hide');
                },
            })
        }, 1000);
    };

    function updatedTheCurrentTopic() {
        return service.topicService && service.tinymceContent.language == languageService.getCurrentLanguage();
    }

    function dateToStr(date) {
        if (!date) {
            return undefined;
        }
        return date.getFullYear() + '-' + ('0' + (date.getMonth() + 1)).slice(-2) + '-' + ('0' + date.getDate()).slice(-2);
    }

    return service;
}


