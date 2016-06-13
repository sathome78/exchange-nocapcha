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
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='https://fonts.googleapis.com/css?family=Roboto:400,700,300' rel='stylesheet' type='text/css'>


    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
    <script src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>" type="text/javascript"></script>

    <link href="<c:url value="/client/css/ekko-lightbox.min.css"/>" rel="stylesheet">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style-new.css'/>" rel="stylesheet">
    <%----------%>
    <script type="text/javascript" src="<c:url value="/client/js/ekko-lightbox.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/menuSwitcher.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/notyInit.js'/>"></script>
    <%----------%>

</head>

<body>

<%@include file='header_new.jsp' %>

<main class="container orders_new transaction my_orders orders">
    <div class="row">
        <%@include file='usermenu_new.jsp' %>

        <div>
            <c:if test="${msq ne ''}">
                <span style="color:red">${msg}</span><br><br>
            </c:if>
        </div>

        <div class="col-sm-9 content">
            <% String tabIdx = request.getParameter("tabIdx");%>
            <% if (tabIdx == null) {tabIdx=String.valueOf(request.getAttribute("tabIdx"));}  %>
            <% if (tabIdx == "null") {tabIdx="1";}  %>
            <div class="buttons">
                <%--изменить пароль--%>
                <% if (tabIdx==null || "1".equals(tabIdx)) {%>
                <button class="orderForm-toggler active">
                        <%} else {%>
                    <button class="orderForm-toggler">
                        <%}%>
                        <loc:message code="admin.changePassword"/>
                    </button>

                    <%--изменить фин пароль--%>
                        <% if ("2".equals(tabIdx)) {%>
                    <button class="orderForm-toggler active">
                            <%} else {%>
                        <button class="orderForm-toggler">
                            <%}%>
                            <loc:message code="admin.changeFinPassword"/>
                        </button>
            </div>

            <div class="tab-content">
                <%--Изменить пароль--%>
                <div class="tab-pane active" id="tab__sell">
                    <form:form class="form-horizontal" id="settings-user-form"
                               action="/settings/changePassword/submit"
                               method="post" modelAttribute="user">
                        <%--Логин--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                <label for="user-name" class="input-block-wrapper__label"><loc:message
                                        code="admin.login"/></label>
                            </div>
                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                <form:input path="id" type="hidden" class="form-control" id="user-id"/>
                                <form:input path="role" type="hidden" class="form-control" id="user-role"/>
                                <form:input path="status" type="hidden" class="form-control"
                                            id="user-status"/>
                                <form:input path="finpassword" type="hidden" class="form-control"
                                            id="user-finpassword"/>
                                <form:input path="nickname" class="input-block-wrapper__input" id="user-name"
                                            readonly="true"/>
                            </div>
                        </div>
                        <%--e-mail--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                <label for="user-email" class="input-block-wrapper__label"><loc:message
                                        code="admin.email"/></label>
                            </div>
                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                <form:input path="email" class="input-block-wrapper__input"
                                            id="user-email" readonly="true"/>
                                <form:errors path="email" class="input-block-wrapper__input"
                                             style="color:red"/>
                            </div>
                        </div>
                        <%--пароль--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                <label for="user-password" path="password"
                                       class="input-block-wrapper__label"><loc:message
                                        code="admin.password"/></label>
                            </div>
                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                <form:password path="password" class="input-block-wrapper__input"
                                               id="user-password"/>
                                <form:errors path="password" class="input-block-wrapper__input"
                                             style="color:red"/>
                            </div>
                        </div>
                        <%--повтор пароль--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                <label for="user-confirmpassword" path="confirmpassword"
                                       class="input-block-wrapper__label"><loc:message
                                        code="admin.confirmpassword"/></label>
                            </div>
                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                <form:password path="confirmPassword" class="input-block-wrapper__input"
                                               id="user-confirmpassword"/>
                                <form:errors path="confirmPassword" class="input-block-wrapper__input"
                                             style="color:red"/>
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
                        <div class="input-block-wrapper">
                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                <label for="userFin-name" class="input-block-wrapper__label"><loc:message
                                        code="admin.login"/></label>
                            </div>
                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                <form:input path="id" type="hidden" class="form-control" id="userFin-id"/>
                                <form:input path="role" type="hidden" class="form-control"
                                            id="userFin-role"/>
                                <form:input path="status" type="hidden" class="form-control"
                                            id="userFin-status"/>
                                <form:input path="nickname" class="input-block-wrapper__input" id="userFin-name"
                                            readonly="true"/>
                            </div>
                        </div>
                        <%--e-mail--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                <label for="userFin-email" class="input-block-wrapper__label"><loc:message
                                        code="admin.email"/></label>
                            </div>
                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                <form:input path="email" class="input-block-wrapper__input"
                                            id="userFin-email" readonly="true"/>
                                <form:errors path="email" class="input-block-wrapper__input"
                                             style="color:red"/>
                            </div>
                        </div>
                        <%--пароль--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                <label for="userFin-password" path="finpassword"
                                       class="input-block-wrapper__label"><loc:message
                                        code="admin.finPassword"/></label>
                            </div>
                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                <form:password path="finpassword" class="input-block-wrapper__input"
                                               id="userFin-password"/>
                                <form:errors path="finpassword" class="input-block-wrapper__input"
                                             style="color:red"/>
                            </div>
                        </div>
                        <%--повтор пароль--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                <label for="userFin-confirmpassword" path="confirmpassword"
                                       class="input-block-wrapper__label"><loc:message
                                        code="admin.confirmpassword"/></label>
                            </div>
                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                <form:password path="confirmFinPassword" class="input-block-wrapper__input"
                                               id="userFin-confirmpassword"/>
                                <form:errors path="confirmFinPassword" class="input-block-wrapper__input"
                                             style="color:red"/>
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
                <c:choose>
                    <c:when test="${userFiles.size() != 0}">
                        <h4><loc:message code="admin.yourFiles"/></h4>
                        <div class="row usr_doc_row">
                            <div class="col-md-offset-0 col-md-10">
                                <c:forEach var="image" items="${userFiles}">
                                    <a href="${image.path}" data-toggle="lightbox" class="col-sm-4">
                                        <img src="${image.path}" class="img-responsive">
                                    </a>
                                </c:forEach>
                            </div>
                        </div>
                    </c:when>
                </c:choose>
                <c:choose>
                    <c:when test="${userFiles.size() < 3}">
                        <h4><loc:message code="admin.uploadFiles"/></h4>
                        <form method="post" id="upload" action="/settings/uploadFile" accept="image/x-png, image/jpeg, image/jpg" enctype="multipart/form-data" class="form-horizontal">

                            <c:forEach var="i" varStatus="vs" begin="1" end="${3 - userFiles.size()}">

                                <c:choose>

                                    <c:when test="${i == 1}">
                                        <input required type="file"name="file"/>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="file" name="file"/>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button type="submit" class="confirm-button"><loc:message code="admin.upload"/></button>

                        </form>
                        <%--</div>--%>
                    </c:when>
                </c:choose>
            </div>
        </div>
    </div>
    <hr/>
</main>
<%@include file='fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>

