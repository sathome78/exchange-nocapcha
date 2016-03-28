<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title><loc:message code="admin.title"/></title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon" />

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='https://fonts.googleapis.com/css?family=Roboto:400,700,300' rel='stylesheet' type='text/css'>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
    <script src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>" type="text/javascript"></script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style-new.css'/>" rel="stylesheet">

</head>

<body>

<%@include file='header_new.jsp' %>

<main class="container orders_new transaction my_orders orders">
    <%@include file='exchange_info_new.jsp' %>
    <div class="row">
        <%@include file='usermenu_new.jsp' %>

        <%--взял из старого - не понял для чего он... //TODO--%>
        <div>
            <c:if test="${msq ne ''}">
                <span style="color:red">${msg}</span><br><br>
            </c:if>
        </div>
        <%-- ... взял из старого - не понял для чего он--%>

        <div class="col-sm-9 content">
            <div class="buttons">
                <%--Создать ордер на продажу--%>
                <button class="active orderForm-toggler">
                    <loc:message code="admin.changePassword"/>
                </button>
                <%--Создать ордер на покупку--%>
                <button class="orderForm-toggler">
                    <loc:message code="admin.changeFinPassword"/>
                </button>
            </div>

            <%--контейнер форм продажа - покупка--%>
            <div class="tab-content">
                <%--Изменить пароль--%>
                <div class="tab-pane active" id="tab__sell">
                    <form:form class="form-horizontal" id="settings-user-form"
                               action="/settings/changePassword/submit"
                               method="post" modelAttribute="user">
                        <%--Логин--%>
                        <div>
                            <label for="user-name"><loc:message code="admin.login"/></label>
                            <form:input path="id" type="hidden" class="form-control" id="user-id"/>
                            <form:input path="role" type="hidden" class="form-control" id="user-role"/>
                            <form:input path="status" type="hidden" class="form-control"
                                        id="user-status"/>
                            <form:input path="finpassword" type="hidden" class="form-control"
                                        id="user-finpassword"/>
                            <form:input path="nickname" class="form-control" id="user-name"
                                        readonly="true"/>
                        </div>
                        <%--e-mail--%>
                        <div>
                            <label for="user-email"><loc:message code="admin.email"/></label>
                            <form:errors path="email" style="color:red"/>
                            <form:input path="email" class="form-control" id="user-email"
                                        readonly="true"/>
                        </div>
                        <%--пароль--%>
                        <div>
                            <label for="user-password" path="password"><loc:message code="admin.password"/></label>

                            <div class="input-field-wrapper">
                                <form:password path="password" class="form-control" id="user-password"/>
                                <form:errors path="password" style="color:red"/>
                            </div>
                        </div>
                        <%--повтор пароль--%>
                        <div>
                            <label for="user-confirmpassword" path="confirmpassword"><loc:message
                                    code="admin.confirmpassword"/></label>

                            <div class="input-field-wrapper">
                                <form:password path="confirmPassword" class="form-control"
                                               id="user-confirmpassword"/>
                                <form:errors path="confirmPassword" style="color:red"/>
                            </div>
                        </div>
                        <%--будет отправлено письмо--%>
                        <h4><loc:message code="admin.changePasswordSendEmail"/></h4>
                        <%--Кнопки--%>
                        <button class="confirm-button" type="submit"><loc:message code="admin.save"/></button>
                        <button class="confirm-button" type="reset" onclick="javascript:window.location='/settings';">
                            <loc:message code="admin.cancel"/></button>
                    </form:form>
                </div>

                <%--Изменить фин пароль--%>
                <div class="tab-pane" id="tab__buy">
                    <form:form class="form-horizontal" id="settings-userFin-form"
                               action="/settings/changeFinPassword/submit"
                               method="post" modelAttribute="user">
                        <%--Логин--%>
                        <div>
                            <label for="userFin-name"><loc:message code="admin.login"/></label>
                            <form:input path="id" type="hidden" class="form-control" id="userFin-id"/>
                            <form:input path="role" type="hidden" class="form-control"
                                        id="userFin-role"/>
                            <form:input path="status" type="hidden" class="form-control"
                                        id="userFin-status"/>
                            <form:input path="nickname" class="form-control" id="userFin-name"
                                        readonly="true"/>
                        </div>
                        <%--e-mail--%>
                        <div>
                            <label for="userFin-email"><loc:message code="admin.email"/></label>
                            <form:errors path="email" style="color:red"/>
                            <form:input path="email" class="form-control" id="userFin-email"
                                        readonly="true"/>
                        </div>
                        <%--пароль--%>
                        <div>
                            <label for="userFin-password" path="finpassword"><loc:message
                                    code="admin.finPassword"/></label>

                            <div class="input-field-wrapper">
                                <form:password path="finpassword" class="form-control"
                                               id="userFin-password"/>
                                <form:errors path="finpassword" style="color:red"/>
                            </div>
                        </div>
                        <%--повтор пароль--%>
                        <div>
                            <label for="userFin-confirmpassword" path="confirmpassword"><loc:message
                                    code="admin.confirmpassword"/></label>

                            <div class="input-field-wrapper">
                                <form:password path="confirmPassword" class="form-control"
                                               id="userFin-confirmpassword"/>
                                <form:errors path="confirmPassword" style="color:red"/>
                            </div>
                        </div>
                        <%--будет отправлено письмо--%>
                        <h4><loc:message code="admin.changePasswordSendEmail"/></h4>
                        <%--Кнопки--%>
                        <button class="confirm-button" type="submit"><loc:message code="admin.save"/></button>
                        <button class="confirm-button" type="reset" onclick="javascript:window.location='/settings';">
                            <loc:message code="admin.cancel"/></button>
                    </form:form>
                </div>
            </div>
            <hr>
        </div>
    </div>
</main>
<%@include file='footer_new.jsp' %>
<%----------%>
<script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/menuSwitcher.js'/>"></script>
<%----------%>
</body>
</html>

