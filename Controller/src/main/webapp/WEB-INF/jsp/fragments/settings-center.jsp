<div id="settings-page" class="settings">
    <c:if test="${sectionid == null || sectionid.equals('passwords-changing')}">
        <%@include file="settings-center-password.jsp" %>
    </c:if>
    <hr/>
    <c:if test="${sectionid == null || sectionid.equals('files-upload')}">
        <%@include file="settings-center-filesupload.jsp" %>
    </c:if>
    <hr/>
</div>

