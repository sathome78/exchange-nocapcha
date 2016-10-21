<%--
  Created by IntelliJ IDEA.
  User: Valk
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%----------%>
<script src="<c:url value="/client/js/jquery.form.js"/>"></script>
<%----------%>

<div id="news-add-modal" class="modal fade modal-form-dialog" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="news.add"/></h4>
            </div>
            <div class="modal-body news-add-info">
                <form id="news-add-info__form" action="" method="post"
                    accept="application/zip,application/x-zip,application/x-zip-compressed">
                    <div class="input-block-wrapper news-add-info__date">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="news.date"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="newsDate" name="date"
                                   placeholder='<loc:message code="news.datetimeplaceholder"/>'
                                   autocomplete="off"
                                   class="form-control input-block-wrapper__input"/>
                        </div>
                        <div for="newsDate" hidden class="col-md-7 input-block-wrapper__error-wrapper">
                            <label for="newsDate" class="input-block-wrapper__input"><loc:message
                                    code="news.errordatetime"/></label>
                        </div>
                    </div>
                    <br/>
                    <br/>
                    <input id="uploadFile" class="file-choice__input" required type="file" name="file"/>
                    <input id="newsId" hidden name="id"/>
                    <input id="resource" hidden name="resource"/>
                </form>
                <textarea id="tinymce"></textarea>
                <button type="button" id="tinymce-btn">Button</button>
            </div>
            <div class="modal-footer">
                <div class="news-add-info__button-wrapper">
                    <button id="news-add-info__add-news" class="delete-order-info__button">
                        <loc:message code="news.add"/></button>
                    <button class="delete-order-info__button" data-dismiss="modal"
                            ><loc:message
                            code="submitorder.cancell"/></button>
                </div>
            </div>
        </div>
    </div>
</div>

