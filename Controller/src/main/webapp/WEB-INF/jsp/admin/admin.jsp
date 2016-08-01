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


    <%@include file='links_scripts.jsp' %>

</head>

<body>

<%@include file='../fragments/header-simple.jsp' %>

<main class="container orders_new admin side_menu">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <c:set var="adminEnum" value="<%=me.exrates.model.enums.UserRole.ADMINISTRATOR%>"/>
        <c:set var="accountantEnum" value="<%=me.exrates.model.enums.UserRole.ACCOUNTANT%>"/>
        <c:set var="admin_userEnum" value="<%=me.exrates.model.enums.UserRole.ADMIN_USER%>"/>

        <div class="col-sm-9 content">

            <%--контейнер форм ролей пользователей--%>
            <div class=" col-md-8 col-md-offset-2 tab-content admin-container">
                <%--форма Пользователи--%>
                <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                    <div id="panel1" class="tab-pane active">
                        <div class="text-center">
                        <h4>
                            <b><loc:message code="admin.listOfUsers"/></b>
                        </h4>
                        </div>
                        <table id="usersTable" class="admin-table table table-hover table-bordered table-striped"
                               style="width: 100%;">
                            <thead>
                            <tr>
                                <th><loc:message code="admin.user"/></th>
                                <th><loc:message code="admin.email"/></th>
                                <th><loc:message code="admin.registrationDate"/></th>
                                <th><loc:message code="admin.role"/></th>
                                <th><loc:message code="admin.status"/></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </sec:authorize>

            </div>
        </div>
    </div>
    <hr>
</main>

</body>
</html>

