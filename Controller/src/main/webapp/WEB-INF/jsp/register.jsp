<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="registrationform" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title><loc:message code="register.title"/></title>

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='https://fonts.googleapis.com/css?family=Roboto:400,700,300' rel='stylesheet' type='text/css'>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
    <script src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>" type="text/javascript"></script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style-new.css'/>" rel="stylesheet">

    <script type="text/javascript" src="<c:url value="/client/js/function.js"/>"></script>

</head>


<body>

<%@include file='header_new.jsp' %>

<main class="container register">
    <hr>
    <div class="row">
        <div class="col-sm-4">
            <%--РЕГИСТРАЦИЯ НОВОГО ПОЛЬЗОВАТЕЛЯ--%>
            <h5><loc:message code="register.title"/></h5>

            <registrationform:form method="post" action="create" modelAttribute="user">
                <%--Логин--%>
                <loc:message code="register.nickname" var="login"/>
                <registrationform:input id="login" path="nickname" placeholder="${login}"
                                        required="required"/>
                <%--<registrationform:errors path="nickname" style="color:red" class="form-control"/>--%>
                <form:errors class="form-login-error-message" path="nickname" style="color:red"/>
                <span><loc:message code="register.loginLabel"/></span>
                <%--email--%>
                <loc:message code="register.email" var="email"/>
                <registrationform:input id="email" path="email" placeholder="${email}"
                                        required="required"/>
                <registrationform:errors path="email" style="color:red" class="form-control"/>
                <%--Пароль--%>
                <loc:message code="register.password" var="password"/>
                <registrationform:input id="pass" path="password" type="password" placeholder="${password}"
                                        required="required"/>
                <registrationform:errors path="password" style="color:red"/>
                <%--Повторите пароль--%>
                <loc:message code="register.repeatpassword" var="repassword"/>
                <registrationform:input id="repass" path="confirmPassword" type="password" placeholder="${repassword}"
                                        required="required"/>
                <span class='repass'><i class="fa fa-check"></i></span>
                <registrationform:errors path="confirmPassword" style="color:red"/>
                <br/>
                <br/>
                <%--CAPCHA--%>
                <div id="cpch-field" class="g-recaptcha" data-sitekey=${captchaProperties.key}></div>
                <p class='cpch-error-message' style="color:red">${cpch}</p>
                <br/>
                <%--ЗАРЕГИСТРИРОВАТЬСЯ--%>
                <button id="register_button" type="submit"><loc:message
                        code="register.submit"/></button>
            </registrationform:form>
        </div>
    </div>
</main>
<%@include file='footer_new.jsp' %>

<%----------%>
<script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
<%----------%>
<%--capcha--%>
<script type="text/javascript" src="<c:url value='/client/js/capcha.js'/>"></script>
<script src="https://www.google.com/recaptcha/api.js?onload=onloadCallback&render=explicit&hl=${pageContext.response.locale}"
        async defer>
</script>

</body>
</html>

