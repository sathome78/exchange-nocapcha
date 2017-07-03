<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<div id="noty2fa_modal" class="modal fade order-info__modal modal-form-dialog" tabindex="-1" role="dialog">
<div class="modal-dialog">
    <div class="modal-content">
            <div class="modal-body">
                <div style="border: 1px solid rgba(0, 0, 0, 0.29); border-radius: 4px; margin: 0 5px; padding: 10px">
                    <h5 class="modal-title"><loc:message code="message.attention"/></h5>
                </div>
                <hr>
                <div class="well">
                    <div>
                        <loc:message code="message.2fa.text1"/>
                    </div>
                </div>
                <button id="decline_2fa" class="btn btn-default"><loc:message code="message.2fa.decline1"/></button>
                <button class="btn btn-default accept_2fa"><loc:message code="message.2fa.aggree.toSettings"/></button>
            </div>
    </div>
</div>
</div>

<div id="noty2fa_confirm_modal" class="modal fade order-info__modal modal-form-dialog" tabindex="-1" role="dialog">
<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-body">
            <div style="border: 1px solid rgba(0, 0, 0, 0.29); border-radius: 4px; margin: 0 5px; padding: 10px">
                <h5 class="modal-title"><loc:message code="message.attention"/></h5>
            </div>
            <hr>
            <div class="well">
                <div> <loc:message code="message.2fa.text2"/>
                </div>
            </div>
            <button id="decline_2fa_finally" class="btn btn-default"><loc:message code="message.2fa.accept"/></button>
            <button class="btn btn-default accept_2fa"><loc:message code="message.2fa.aggree.toSettings"/></button>

        </div>
    </div>
</div>
</div>
