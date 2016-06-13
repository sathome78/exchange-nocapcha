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

    <script type="text/javascript" src="/client/js/jquery.dataTables.min.js"></script>
    <%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/menuSwitcher.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/notyInit.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminUsersDataTable.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminAdminsDataTable.js'/>"></script>
    <%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/order/adminDeleteOrder.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/changeRefSystemOptions.js'/>"></script>
    <%----------%>

</head>

<body>

<%@include file='../header_new.jsp' %>

<main class="container orders_new admin side_menu">
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
                <%--referral--%>
                <sec:authorize access="hasAnyAuthority('${adminEnum}')">
                    <button class="adminForm-toggler">
                        <loc:message code="admin.referral"/>
                    </button>
                </sec:authorize>
                <%--withdraw--%>
                <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}')">
                    <button onclick="javascript:window.location.href='/admin/withdrawal';" id="admin-withdraw-requests">
                        <loc:message code="admin.withdrawRequests"/></button>
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
                        </h4>

                        <div class="admin-add-functions-container clearfix">
                            <div id="admin-add-functions">
                                <button onclick="javascript:window.location.href='/admin/addUser';"
                                        class="admin-add-functions__item"><loc:message code="admin.addUser"/></button>
                                <button onclick="searchAndDeleteOrderByAdmin()"
                                        class="admin-add-functions__item"><loc:message
                                        code="deleteorder.title"/></button>
                            </div>
                        </div>
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

                            <p><a class="link" href="userswallets"><loc:message code="admin.usersWallet"/></a></p>
                        </h4>
                    </div>
                </sec:authorize>

                    <sec:authorize access="hasAnyAuthority('${adminEnum}')">
                        <div id="panel4 row" class="tab-pane">
                            <div class="col-sm-4">
                                <h4>
                                    <loc:message code="admin.referralLevels"/>
                                </h4>
                                <table class="col-sm-4 ref-lvl-table">
                                    <thead>
                                    <tr>
                                        <th>Level</th>
                                        <th>Percent</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${referralLevels}" var="level">
                                        <tr class="table-row" data-percent="${level.percent}" data-id="${level.id}" data-level="${level.level}" data-toggle="modal" data-target="#myModal">
                                            <td>
                                                    ${level.level}
                                            </td>
                                            <td id="_${level.level}">
                                                <span class="lvl-percent">${level.percent}</span>%
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            <div class="col-sm-4">
                               <h4>
                                   <loc:message code="admin.refCommonRoot"/>
                                   <c:choose>
                                       <c:when test="${commonRefRoot != null}">
                                           <span data-id="${commonRefRoot.id}" id="ref-root-info">(${commonRefRoot.email})</span>
                                       </c:when>
                                       <c:otherwise>
                                           <span id="current-ref-root">(<loc:message code="admin.refAbsentCommonRoot"/>)</span>
                                       </c:otherwise>
                                   </c:choose>
                               </h4>
                                <form id="edit-cmn-ref-root">
                                    <select name="ref-root">
                                        <c:forEach items="${admins}" var="admin">
                                            <option value="${admin.id}">${admin.email}</option>
                                        </c:forEach>
                                    </select>
                                    <button type="submit"><loc:message code="admin.refSubmitEditCommonRoot"/></button>
                                </form>
                            </div>
                        </div>
                    </sec:authorize>

            </div>
        </div>
    </div>
    <hr>
</main>

<%@include file='order_delete.jsp' %>

<div id="myModal" class="modal fade edit-ref-lvl-modal">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="admin.referralLevelEdit"/></h4>
            </div>
            <div class="modal-body">
                <form id="edit-ref-lvl-form">
                    <input class="" type="hidden" name="level">
                    <input type="hidden" name="id">
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.referralLevel"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input  name="" class="input-block-wrapper__input lvl-id" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.referralPercent"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input  name="percent" class="input-block-wrapper__input" type="text">
                        </div>
                    </div>
                    <button class="delete-order-info__button" type="submit"><loc:message code="admin.refSubmitEditCommonRoot"/></button>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>

