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

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style-new.css'/>" rel="stylesheet">

    <link href="<c:url value='/client/css/jquery.dataTables.min.css'/>" rel="stylesheet" type="text/css"/>

    <%--<script type="text/javascript" src="/client/js/jquery.js"></script>--%>
    <%--<script type="text/javascript" src="/client/js/tab.js"></script>--%>
    <script type="text/javascript" src="/client/js/jquery.dataTables.min.js"></script>

</head>

<body>

<%@include file='../header_new.jsp' %>

<main class="container orders_new admin side_menu">
    <%@include file='../exchange_info_new.jsp' %>
    <div class="row">
        <%@include file='../usermenu_new.jsp' %>

        <div class="col-sm-9 content">
            <div class="buttons">
                <c:set var="adminEnum" value="<%=me.exrates.model.enums.UserRole.ADMINISTRATOR%>"/>
                <c:set var="accountantEnum" value="<%=me.exrates.model.enums.UserRole.ACCOUNTANT%>"/>
                <c:set var="admin_userEnum" value="<%=me.exrates.model.enums.UserRole.ADMIN_USER%>"/>
                <%--Пользователи--%>
                <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                    <button class="active adminForm-toggler">
                        <loc:message code="admin.users"/>
                    </button>
                </sec:authorize>
                <%--Администраторы--%>
                <sec:authorize access="hasAnyAuthority('${adminEnum}')">
                    <button class="adminForm-toggler">
                        <loc:message code="admin.admins"/>
                    </button>
                </sec:authorize>
                <%--Финансисты--%>
                <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}')">
                    <button class="adminForm-toggler">
                        <loc:message code="admin.finance"/>
                    </button>
                </sec:authorize>
            </div>

            <%--контейнер форм ролей пользователей--%>
            <div class="tab-content">
                <%--форма Пользователи--%>
                <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                    <div id="panel1" class="tab-pane active">
                        <h4>
                            <b><loc:message code="admin.listOfUsers"/></b>
                        </h4>
                        <hr/>

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

                <%--форма админы--%>
                <sec:authorize access="hasAnyAuthority('${adminEnum}')">
                    <div id="panel2" class="tab-pane">
                        <h4>
                            <b><loc:message code="admin.listOfAdmins"/></b>
                            <button onclick="javascript:window.location.href='/admin/addUser';" id="admin-add-button"><loc:message code="admin.addUser"/></button>
                        </h4>

                        <hr/>

                        <table id="adminsTable" class="admin-table table table-hover table-bordered table-striped"
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

                <%--форма финансисты--%>
                <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}')">
                    <div id="panel3" class="tab-pane">
                        <h4>
                            <p><a class="link" href="companywallet"><loc:message code="admin.companyWallet"/></a></p>
                        </h4>
                    </div>
                </sec:authorize>
            </div>
        </div>
    </div>
    <hr>
</main>
<%----------%>
<script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/menuSwitcher.js'/>"></script>

<script type="text/javascript" src="<c:url value='/client/js/dataTable/adminUsersDataTable.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/dataTable/adminAdminsDataTable.js'/>"></script>
<%----------%>
</body>
</html>

