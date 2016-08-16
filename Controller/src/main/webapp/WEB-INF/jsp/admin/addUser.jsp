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
    <link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>

    <script src="<c:url value="/client/js/jquery_1.11.3.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>" type="text/javascript"></script>

    <link rel="stylesheet" href="<c:url value="/client/css/font-awesome.min.css"/>">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style-new.css'/>" rel="stylesheet">

    <link href="<c:url value='/client/css/jquery.dataTables.min.css'/>" rel="stylesheet" type="text/css"/>

    <script type="text/javascript" src="/client/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            $('#usersTable').DataTable();
        });
    </script>
    <%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/menuSwitcher.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminTransactionsDataTable.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminWalletsDataTable.js'/>"></script>
    <%----------%>
    <%@include file="../tools/alexa.jsp" %>

</head>

<body>

<%@include file='../header_new.jsp' %>

<main class="container orders_new admin side_menu">
    <div class="row">
        <%@include file='../usermenu_new.jsp' %>
        <%--<div class="col-sm-6 content">--%>
        <div class="content">
            <%--форма редактирование пользователя--%>
            <div class="col-sm-6 content">
                <h4>
                    <b><loc:message code="admin.addUser"/></b>
                </h4>
                <hr/>
                <div class="panel-body">
                    <form:form class="form-horizontal" id="user-add-form" action="/admin/adduser/submit" method="post" modelAttribute="user">
                        <div>
                            <fieldset class="field-user">

                                <div class="input-block-wrapper">
                                    <div class="col-md-4 input-block-wrapper__label-wrapper">
                                        <label for="user-name" class="input-block-wrapper__label"><loc:message
                                                code="admin.login"/></label>
                                    </div>
                                    <div class="col-md-8 input-block-wrapper__input-wrapper">
                                        <form:input path="nickname" class="input-block-wrapper__input" id="user-name" required="required"/>
                                        <form:errors path="nickname" class="input-block-wrapper__input" style="color:red"/>
                                    </div>
                                </div>
                                <div class="input-block-wrapper">
                                    <div class="col-md-4 input-block-wrapper__label-wrapper">
                                        <label for="user-email" class="input-block-wrapper__label"><loc:message
                                                code="admin.email"/></label>
                                    </div>
                                    <div class="col-md-8 input-block-wrapper__input-wrapper">
                                        <form:input path="email" class="input-block-wrapper__input" id="user-email" required="required" />
                                        <form:errors path="email" class="input-block-wrapper__input" style="color:red"/>
                                    </div>
                                </div>
                                <div class="input-block-wrapper">
                                    <div class="col-md-4 input-block-wrapper__label-wrapper">
                                        <label for="user-password" path="password"
                                               class="input-block-wrapper__label"><loc:message
                                                code="admin.password"/></label>
                                    </div>
                                    <div class="col-md-8 input-block-wrapper__input-wrapper">
                                        <form:input path="password" type="password" class="input-block-wrapper__input" id="user-password" required="required"/>
                                        <form:errors path="password" class="input-block-wrapper__input" style="color:red"/>
                                    </div>
                                </div>
                                <div class="input-block-wrapper">
                                    <div class="col-md-4 input-block-wrapper__label-wrapper">
                                        <label for="user-phone" class="input-block-wrapper__label"><loc:message
                                                code="admin.phone"/></label>
                                    </div>
                                    <div class="col-md-8 input-block-wrapper__input-wrapper">
                                        <form:input path="phone" class="input-block-wrapper__input" id="user-phone" />
                                        <form:errors path="phone" class="input-block-wrapper__input" style="color:red"/>
                                    </div>
                                </div>

                                <div class="input-block-wrapper">
                                    <div class="col-md-4 input-block-wrapper__label-wrapper">
                                        <label for="user-role" class="input-block-wrapper__label"><loc:message
                                                code="admin.role"/></label>
                                    </div>
                                    <div class="col-md-8 input-block-wrapper__input-wrapper">
                                        <form:select path="role" id="user-role" class="input-block-wrapper__input" name="user-role">
                                            <c:forEach items="${roleList}" var="role">
                                                <option value="${role}">${role}</option>
                                            </c:forEach>
                                        </form:select>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <div>
                                        <loc:message code="admin.save" var="saveSubmit"></loc:message>
                                        <button type="submit">${saveSubmit}</button>

                                        <loc:message code="admin.cancel" var="cancelSubmit"></loc:message>
                                        <button type="reset"
                                                onclick="javascript:window.location='/admin';">${cancelSubmit}</button>
                                    </div>
                                </div>
                            </fieldset>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
    <hr>
</main>
</body>
</html>

