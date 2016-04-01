<%@page language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<html>
<head>
    <meta charset="utf-8"/>
    <!--[if lt IE 9]>
    <script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
    <title><loc:message code="admin.title"/></title>
    <meta name="keywords" content=""/>
    <meta name="description" content=""/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>

    <link href="<c:url value='/client/css/bootstrap.css'/>" rel="stylesheet" type="text/css"/>
    <link href="<c:url value='/client/css/style.css'/>" rel="stylesheet" type="text/css"/>
    <link href="<c:url value='/client/css/jquery.dataTables.min.css'/>" rel="stylesheet" type="text/css"/>

    <script type="text/javascript" src="/client/js/jquery.js"></script>
    <script type="text/javascript" src="/client/js/tab.js"></script>
    <script type="text/javascript" src="/client/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript">
        $(document).ready(function(){
            $('#usersTable').DataTable();
        });
    </script>


</head>
<body>

<div class="wrapper lk">

    <div class="container container_center full__height">

        <%@include file='../usermenu.jsp' %>

        <div class="main__content">

            <%@include file='../header.jsp' %>

            <%--<div class="col-md-12 main">--%>
            <div class="content__page">
                <c:set var="adminEnum" value="<%=me.exrates.model.enums.UserRole.ADMINISTRATOR%>"/>
                <c:set var="accountantEnum" value="<%=me.exrates.model.enums.UserRole.ACCOUNTANT%>"/>
                <c:set var="admin_userEnum" value="<%=me.exrates.model.enums.UserRole.ADMIN_USER%>"/>
                <ul class="nav nav-tabs">
                    <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                        <li class="active"><a data-toggle="tab" href="#panel1"><loc:message code="admin.users"/></a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAnyAuthority('${adminEnum}')">
                        <li><a data-toggle="tab" href="#panel2"><loc:message code="admin.admins"/></a></li>
                    </sec:authorize>
                    <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}')">
                        <li><a data-toggle="tab" href="#panel3"><loc:message code="admin.finance"/></a></li>
                    </sec:authorize>
                </ul>
                <div class="tab-content">
                    <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                        <div id="panel1" class="tab-pane fade in active">
                            <h3>
                                <b><loc:message code="admin.listOfUsers"/></b>
                            </h3>
                            <h1>

                            </h1>
                            <table id="usersTable" class="table table-hover table-bordered table-striped" border="1" cellpadding="8"
                                   cellspacing="0">
                                <thead style="border-style: none;">
                                <tr>
                                    <th><loc:message code="admin.user"/></th>
                                    <th><loc:message code="admin.email"/></th>
                                    <th><loc:message code="admin.registrationDate"/></th>
                                    <th><loc:message code="admin.role"/></th>
                                    <th><loc:message code="admin.status"/></th>
                                    <th><loc:message code="admin.balance"/></th>
                                </tr>
                                </thead>
                                <tbody>
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
                    <sec:authorize access="hasAnyAuthority('${adminEnum}')">
                        <div id="panel2" class="tab-pane fade">
                            <h3>
                                <b><loc:message code="admin.listOfAdmins"/></b>
                                <a href="/admin/addUser" class="btn btn-primary pull-right" id="admin-add-button"
                                   title=<loc:message code="admin.addUser"/>><loc:message code="admin.addUser"/></a>
                            </h3>
                            <h1>

                            </h1>
                            <table id="adminsTable" class="table table-hover table-bordered table-striped" border="1" cellpadding="8"
                                   cellspacing="0">
                                <thead style="border-style: none;">
                                <tr>
                                    <th><loc:message code="admin.user"/></th>
                                    <th><loc:message code="admin.email"/></th>
                                    <th><loc:message code="admin.registrationDate"/></th>
                                    <th><loc:message code="admin.role"/></th>
                                    <th><loc:message code="admin.status"/></th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${adminUsers}" var="user">
                                    <tr>
                                        <td>
                                            <a class="link" href="admin/editUser?id=${user.getId()}" name="edit-admin-id"
                                               id="edit-admin-id" title=<loc:message
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

                                    </tr>
                                </c:forEach>

                                </tbody>
                            </table>
                        </div>
                    </sec:authorize>
                    <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}')">
                        <div id="panel3" class="tab-pane fade">
                            <h3>
                                <a class="link" href="companywallet" ><loc:message code="admin.companyWallet"/></a>
                            </h3>
                        </div>
                    </sec:authorize>
                </div>
                <%--<%@include file='footer.jsp'%>--%>
            </div>
        </div>
    </div>
</div>
</body>
</html>
