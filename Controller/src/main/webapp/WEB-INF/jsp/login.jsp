<%@page language="java" contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="registrationform"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <!--[if lt IE 9]><script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
    <title></title>
    <meta name="keywords" content="" />
    <meta name="description" content="" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <link href="<c:url value="/client/css/bootstrap.css"/>" rel="stylesheet" type="text/css" />
    <link href="<c:url value="/client/css/chosen.css"/>" rel="stylesheet" type="text/css" />
    <link href="<c:url value="/client/css/style.css"/>" rel="stylesheet" type="text/css" />

    <script type="text/javascript" src="<c:url value="/client/js/jquery.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/dropdown.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/modal.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/chosen.jquery.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/function.js"/>"></script>

</head>

<body>

<div class="wrapper login_page">


    <div class="container container_center full__height">

        <div class="main__content full__height">

            <div class="content__page full__height">
                <div class="registration__form form">
                <c:if test="${not empty error}">
                        <div class="error">${error}</div>
                    </c:if>
                <c:if test="${not empty msg}">
                        <div class="msg">${msg}</div>
                </c:if>
                <c:url value="/login" var="loginUrl" />
                <form action="${loginUrl}" method="post">
                    <h2><loc:message code="login.title"/></h2>
                    <input type="text" class="form-control" name="username" placeholder="<loc:message code="login.email"/>" required autofocus>
                    <input type="password" class="form-control" name="password" placeholder="<loc:message code="login.password"/>" required>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button class="form-control" type="submit"><loc:message code="login.submit"/></button>
                </form>
                    </div>
            </div>
        </div>
        </div>
    </div>
</body>
</html>