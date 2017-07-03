<div id="settings-page" class="settings">
    <c:if test="${sectionid == null || sectionid.equals('passwords-changing')}">
        <%@include file="settings-center-password.jsp" %>
    </c:if>
    <hr/>
    <c:if test="${sectionid == null || sectionid.equals('files-upload')}">
        <%@include file="settings-center-filesupload.jsp" %>
    </c:if>
    <hr/>
    <c:if test="${sectionid == null || sectionid.equals('notification-options')}">
        <%@include file="settings-center-notifications.jsp" %>
    </c:if>
    <hr/>
    <c:if test="${sectionid == null || sectionid.equals('session-options')}">
        <%@include file="settings-center-session.jsp" %>
    </c:if>
    <c:if test="${sectionid == null || sectionid.equals('2fa-options')}">
        <%@include file="settings-center-2fa.jsp" %>
    </c:if>
    <hr/>
</div>

