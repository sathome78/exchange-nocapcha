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
    <title><loc:message code="orders.title"/></title>
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

    <%--<link href="<c:url value='/client/css/jquery.dataTables.min.css'/>" rel="stylesheet" type="text/css"/>--%>
    <script type="text/javascript" src="/client/js/tab.js"></script>
    <script type="text/javascript" src="/client/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript">
        $(document).ready(function(){
            $('#usersTable').DataTable();
        });
    </script>

</head>

<body>

<%@include file='../header_new.jsp' %>

<main class="container orders_new transaction my_orders orders">
    <%@include file='../exchange_info_new.jsp' %>
    <div class="row">
        <%@include file='../usermenu_new.jsp' %>

        <div class="col-sm-9 content">
            <div class="buttons">
                <c:set var="adminEnum" value="<%=me.exrates.model.enums.UserRole.ADMINISTRATOR%>"/>
                <c:set var="accountantEnum" value="<%=me.exrates.model.enums.UserRole.ACCOUNTANT%>"/>
                <c:set var="admin_userEnum" value="<%=me.exrates.model.enums.UserRole.ADMIN_USER%>"/>
                <ul class="nav nav-tabs">
                    <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}')">
                        <li class="active"><a data-toggle="tab" href="#panel0"><loc:message code="admin.withdrawRequests"/></a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                        <li><a data-toggle="tab" href="#panel1"><loc:message code="admin.users"/></a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAnyAuthority('${adminEnum}')">
                        <li><a data-toggle="tab" href="#panel2"><loc:message code="admin.admins"/></a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}')">
                        <li><a data-toggle="tab" href="#panel3"><loc:message code="admin.finance"/></a></li>
                    </sec:authorize>
                </ul>
                <div class="tab-content">
                    <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}')">
                        <div id="panel0" class="tab-pane fade in active">
                            <h3>
                                <a class="link" href="/admin/withdrawal" ><loc:message code="admin.withdrawRequests"/></a>
                            </h3>
                        </div>
                    </sec:authorize>
                    <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                        <div id="panel1" class="tab-pane fade">
                            <h3>
                                <b><loc:message code="admin.listOfUsers"/></b>
                            </h3>
                        </div>
                    </sec:authorize>
                    <%--Пользователи--%>
                    <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                        <button class="active orderForm-toggler">
                            <loc:message code="admin.users"/>
                        </button>
                    </sec:authorize>
                    <%--Администраторы--%>
                    <sec:authorize access="hasAnyAuthority('${adminEnum}')">
                        <button class="orderForm-toggler">
                            <loc:message code="admin.admins"/>
                        </button>
                    </sec:authorize>
                    <%--Финансисты--%>
                    <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}')">
                        <button class="orderForm-toggler">
                            <loc:message code="admin.finance"/>
                        </button>
                    </sec:authorize>
                </div>

                <%--контейнер форм ролей пользователей--%>
                <div class="tab-content">
                    <%--форма Пользователи--%>
                    <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                        <div class="tab-pane active" id="tab__sell">
                            <h4>
                                <b><loc:message code="admin.listOfUsers"/></b>
                            </h4>
                            <hr/>

                            <table id="adminsTable" class="table table-hover table-bordered table-striped" border="1" cellpadding="8">
                            <tbody>

                            <tr>
                                <th><loc:message code="admin.user"/></th>
                                <th><loc:message code="admin.email"/></th>
                                <th><loc:message code="admin.registrationDate"/></th>
                                <th><loc:message code="admin.role"/></th>
                                <th><loc:message code="admin.status"/></th>
                                <th><loc:message code="admin.balance"/></th>
                            </tr>
                            <c:forEach items="${userUsers}" var="user">
                                <tr>
                                    <td>
                                        <a class="link" href="admin/editUser?id=${user.getId()}" name="edit-user-id"
                                           id="edit-user-id" title=<loc:message
                                                code="admin.editUser"/>>${user.getNickname()}</a>
                                    </td>
                                    <td>
                                        <option>${user.getEmail()}</option>
                                    </td>
                                    <td>
                                        <option>${user.getRegdate()}</option>
                                    </td>
                                    <td>
                                        <option>${user.getRole()}</option>
                                    </td>
                                    <td>
                                        <option>${user.getStatus()}</option>
                                    </td>
                                    <td>
                                        <option>${user.getStatus()}</option>
                                    </td>

                                </tr>
                            </c:forEach>
                            </tbody>
                            </table>

                        </div>
                    </sec:authorize>

                    <%--форма покупки--%>
                    <div class="tab-pane" id="tab__buy">

                    </div>
                </div>
                <hr>
                <!-- end row -->
            </div>
            <!-- end col-sm-9 content -->
        </div>
</main>
<%@include file='../footer_new.jsp' %>
<%----------%>
<script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/menuSwitcher.js'/>"></script>
<%----------%>
</body>
</html>

