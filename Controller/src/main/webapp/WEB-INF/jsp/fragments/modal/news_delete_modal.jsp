<%--
  Created by IntelliJ IDEA.
  User: Valk
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%----------%>
<script src="<c:url value="/client/js/jquery.form.js"/>"></script>
<%----------%>

<div id="news-delete-modal" class="modal fade modal-form-dialog" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="news.deletevariant"/></h4>
            </div>
            <div class="modal-body news-delete-info">
                <form id="news-delete-info__form" action="" method="post">
                    <div class="input-block-wrapper news-delete-info__date">
                        <div>
                            <label class="input-block-wrapper__input">
                                <input class="input-block-wrapper__radio"type="radio" name="removingType" value="news"/>
                                <loc:message code="news.news"/>
                            </label>
                        </div>
                        <div>
                            <label class="input-block-wrapper__input">
                                <input class="input-block-wrapper__radio" type="radio" name="removingType" value="variant"/>
                                <loc:message code="news.variant"/>
                            </label>
                        </div>
                    </div>
                    <input id="delete-newsId" hidden name="id"/>
                    <input id="delete-newsVariant" hidden name="variant"/>
                    <input id="delete-resource" hidden name="resource"/>
                </form>
            </div>
            <div class="modal-footer">
                <div class="news-delete-info__button-wrapper">
                    <button id="news-delete-info__delete-news" class="delete-order-info__button">
                        <loc:message code="news.deletenews"/></button>
                    <button class="delete-order-info__button" data-dismiss="modal"
                            ><loc:message
                            code="submitorder.cancell"/></button>
                </div>
            </div>
        </div>
    </div>
</div>

