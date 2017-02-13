<%--
  Created by IntelliJ IDEA.
  User: Valk
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="adminEnum"
       value="<%=me.exrates.model.enums.UserRole.ADMINISTRATOR%>"/>
<style>
  option.active {
    font-weight: bold;
  }

  .md-datepicker-calendar-pane {
    z-index: 9999
  }
</style>
<sec:authorize access="hasAnyAuthority('${adminEnum}')">
  <div id="news-pageMaterials-add-modal" class="modal fade modal-form-dialog" tabindex="-1" role="dialog"
       ng-controller="newsUploadCtrl as newsUploadCtrl">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                  aria-hidden="true">&times;</span></button>
          <h4 class="modal-title"><loc:message code="news.add"/></h4>
        </div>
        <div class="modal-body news-add-info">
          <div class="tab-content">
            <div id="editor">
                <%--LOAD FROM EDITOR--%>
              <form id="news-add-editor-form">
                <div class="modal-header row">
                  <div class="col-md-3">
                    <label for="variantEd"><loc:message code="news.locale"/></label>
                  </div>
                  <div class="col-md-9">
                    <select id="variantEd" name="newsVariant"
                            class="form-control"
                            ng-disabled="newsUploadCtrl.tinymceContent.hideLanguagePanel"
                            ng-model="newsUploadCtrl.tinymceContent.language">
                      <option class='list'
                              ng-repeat="lang in rootCtrl.getFormattedLanguageMenu()"
                              ng-class="{active:rootCtrl.getFormattedLanguage(newsUploadCtrl.tinymceContent.language)=='{{lang}}'}"
                              value="{{lang}}">
                        {{lang}}
                      </option>
                    </select>
                  </div>
                  <br>
                  <hr>
                  <div
                          ng-hide="['EVENT', 'FEASTDAY'].indexOf(newsUploadCtrl.tinymceContent.newsType) < 0">
                    <div class="col-md-3">
                        <%--<label for="titleEd"><loc:message code="calendar"/> </label>--%>
                    </div>
                    <div class="col-md-9">
                      <md-datepicker ng-model="newsUploadCtrl.tinymceContent.calendarDate"
                                     md-placeholder="Enter date">
                      </md-datepicker>
                    </div>
                    <br>
                    <hr>
                  </div>
                </div>
                <h4 class="text-center">NEWS CONTENT</h4>
                <div class="modal-header">
                  <div>
                <textarea id="news-content"
                          ui-tinymce="newsUploadCtrl.tinymceOptionsContent"
                          ng-model="newsUploadCtrl.tinymceContent.html">
                </textarea>
                  </div>
                </div>
                <div
                        ng-if="newsUploadCtrl.tinymceContent.showTitleImg">
                  <h4 class="text-center">NEWS TITLE</h4>
                  <div class="modal-header">
                    <div>
                    <textarea id="news-title"
                              ui-tinymce="newsUploadCtrl.tinymceOptionsTitle"
                              ng-model="newsUploadCtrl.tinymceTitle.html">
                    </textarea>
                    </div>
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <div>
            <button id="news-add-info__add-news"
                    ng-click="newsUploadCtrl.submitNewsFromEditor()">
              <loc:message code="admin.save"/>
            </button>
            <button data-dismiss="modal">
              <loc:message code="admin.cancel"/></button>
          </div>
        </div>
      </div>
    </div>
  </div>
</sec:authorize>

