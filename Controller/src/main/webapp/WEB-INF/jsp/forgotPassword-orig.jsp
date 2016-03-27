<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="loc" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
           uri="http://www.springframework.org/security/tags"%>
<%@ page session="false"%>
<html>
<head>
    <link href="<c:url value='/client/css/bootstrap.css'/>" rel="stylesheet" type="text/css"/>
    <link href="<c:url value='/client/css/style.css'/>" rel="stylesheet" type="text/css"/>

    <script type="text/javascript" src="/client/js/jquery.js"></script>
    <script type="text/javascript" src="/client/js/tab.js"></script>

    <meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
    <title><loc:message code="dashboard.resetPasswordTitle"></loc:message></title>

</head>
<body>
<div class="container container_center full__height">
    <div class="content__page">
        <h1>
            <loc:message code="dashboard.resetPasswordTitle"></loc:message>
        </h1>
        <div >
            <br>
            <form:form class="form-inline" id="settings-user-form"
                action="forgotPassword/submit" method="post" modelAttribute="user">

                <div class="form-group">

                    <form:label path="email"><loc:message code="admin.email" var="adminEmail"/></form:label>
                    <form:input path="email" class="form-control" id="user-email"
                                placeholder="${adminEmail}" />

                    <loc:message code="dashboard.resetPasswordButton" var="saveSubmit"></loc:message>
                    <input class="btn btn-primary" value="${saveSubmit}" type="submit">
                </div>
            </form:form>


        </div>

        <br>
        <a href="<c:url value="/register" />">
            <loc:message code="register.submit"></loc:message>
        </a>
        <br>
        <a href="<c:url value="/login" />">
            <loc:message code="login.submit"></loc:message>
        </a>

    </div>
</div>

</body>

</html>