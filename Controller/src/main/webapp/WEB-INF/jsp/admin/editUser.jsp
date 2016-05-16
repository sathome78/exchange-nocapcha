<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html lang="en">
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
    <link href="<c:url value="/client/css/ekko-lightbox.min.css"/>" rel="stylesheet">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style-new.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/jquery.dataTables.min.css'/>" rel="stylesheet" type="text/css"/>

    <%----------%>
    <script type="text/javascript" src="<c:url value="/client/js/ekko-lightbox.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/jquery.dataTables.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/menuSwitcher.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminTransactionsDataTable.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminWalletsDataTable.js'/>"></script>
    <%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/order/adminDeleteOrder.js'/>"></script>
    <%----------%>

</head>

<body>

<%@include file='../header_new.jsp' %>

<main class="container orders_new admin side_menu">
    <%@include file='../exchange_info_new.jsp' %>
    <div class="row">
        <%@include file='../usermenu_new.jsp' %>
        <%--<div class="col-sm-6 content">--%>
        <div class="content">
            <div class="buttons">
                <c:set var="adminEnum" value="<%=me.exrates.model.enums.UserRole.ADMINISTRATOR%>"/>
                <c:set var="accountantEnum" value="<%=me.exrates.model.enums.UserRole.ACCOUNTANT%>"/>
                <c:set var="admin_userEnum" value="<%=me.exrates.model.enums.UserRole.ADMIN_USER%>"/>
                <%--Редактирование пользователя--%>
                <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                    <button class="active adminForm-toggler">
                        <loc:message code="admin.user"/>
                    </button>
                </sec:authorize>
                <%--Список транзакций--%>
                <sec:authorize access="hasAnyAuthority('${adminEnum}')">
                    <button class="adminForm-toggler">
                        <loc:message code="admin.transactions"/>
                    </button>
                </sec:authorize>
                <%--Список кошельков--%>
                <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}')">
                    <button class="adminForm-toggler">
                        <loc:message code="admin.wallets"/>
                    </button>
                </sec:authorize>
            </div>
            <%--контейнер для данных пользователей--%>
            <div class="tab-content">
                <%--форма редактирование пользователя--%>
                <div id="panel1" class="tab-pane active">
                    <div class="col-sm-6 content">
                        <h4>
                            <b><loc:message code="admin.editUser"/></b>
                        </h4>
                        <hr/>

                        <div class="panel-body">

                            <form:form class="form-horizontal" id="user-edit-form" action="/admin/edituser/submit"
                                       method="post" modelAttribute="user">
                                <div>
                                    <fieldset class="field-user">
                                        <div class="input-block-wrapper">
                                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                <label for="user-name" class="input-block-wrapper__label"><loc:message
                                                        code="admin.login"/></label>
                                            </div>
                                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                <form:input path="id" type="hidden" class="input-block-wrapper__input"
                                                            id="user-id"/>
                                                <form:input path="nickname" class="input-block-wrapper__input"
                                                            id="user-name"
                                                            readonly="true"/>
                                            </div>
                                        </div>
                                        <div class="input-block-wrapper">
                                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                <label for="user-email" class="input-block-wrapper__label"><loc:message
                                                        code="admin.email"/></label>
                                            </div>
                                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                <form:input path="email" class="input-block-wrapper__input"
                                                            id="user-email"
                                                            readonly="true"/>
                                                <form:errors path="email" class="input-block-wrapper__input"
                                                             style="color:red"/>
                                            </div>
                                        </div>

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

                                        <div class="input-block-wrapper">
                                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                <label for="user-phone" class="input-block-wrapper__label"><loc:message
                                                        code="admin.phone"/></label>
                                            </div>
                                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                <form:input path="phone" class="input-block-wrapper__input"
                                                            id="user-phone"/>
                                                <form:errors path="phone" class="input-block-wrapper__input"
                                                             style="color:red"/>
                                            </div>
                                        </div>

                                        <div class="input-block-wrapper">
                                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                <label for="user-role" class="input-block-wrapper__label"><loc:message
                                                        code="admin.role"/></label>
                                            </div>
                                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                <form:select path="role" id="user-role"
                                                             class="input-block-wrapper__input"
                                                             name="user-role">
                                                    <c:forEach items="${roleList}" var="role">
                                                        <option value="${role}"
                                                                <c:if test="${role eq user.role}">SELECTED</c:if>>${role}</option>
                                                    </c:forEach>
                                                </form:select>
                                            </div>
                                        </div>

                                        <div class="input-block-wrapper">
                                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                <label for="user-status" class="input-block-wrapper__label"><loc:message
                                                        code="admin.status"/></label>
                                            </div>
                                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                <form:select path="status" id="user-status"
                                                             class="input-block-wrapper__input"
                                                             name="user-status">
                                                    <c:forEach items="${statusList}" var="status">
                                                        <option value="${status}"
                                                                <c:if test="${status eq user.status}">SELECTED</c:if>>${status}</option>
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
                            <h4><loc:message code="admin.yourFiles"/></h4>
                            <c:choose>
                                <c:when test="${userFiles.size() != 0}">
                                    <div class="row usr_doc_row">
                                        <div class="col-md-offset-0 col-md-10">
                                            <c:forEach var="image" items="${userFiles}">
                                            <div id="_${image.id}">
                                                <a href="${image.path}" class="col-sm-4" data-title="<form class='delete_img'>
                                                        <input type='hidden' name='id' value='${image.id}'/>
                                                        <input type='hidden' name='path' value='${image.path}'/>
                                                        <input type='hidden' name='userId' value='${image.userId}'/>
                                                        <button id='apr_delete' type='submit' class='btn-md btn-danger'><loc:message code='admin.modal.delete'/></button>
                                                        </form>" data-toggle="lightbox">
                                                    <img src="${image.path}" class="img-responsive">
                                                </a>
                                            </div>
                                            </c:forEach>
                                            </div>
                                    </div>
                                </c:when>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <%--форма список транзакций--%>
                <div id="panel2" class="tab-pane">
                    <div class="col-sm-8 content">
                        <%--ИСТОРИЯ ОПЕРАЦИЙ--%>
                        <h4><loc:message code="transactions.title"/></h4>
                        <hr>

                        <table id="transactionsTable"
                               class="admin-table table table-hover table-bordered table-striped"
                               style="width:100%">
                            <thead>
                            <tr>
                                <%--Дата--%>
                                <th><loc:message code="transaction.datetime"/></th>
                                <th></th>
                                <%--Тип--%>
                                <th><loc:message code="transaction.operationType"/></th>
                                <%--Статус--%>
                                <th><loc:message code="orderstatus.name"/></th>
                                <%--Валюта--%>
                                <th><loc:message code="transaction.currency"/></th>
                                <%--Сумма--%>
                                <th><loc:message code="transaction.amount"/></th>
                                <%--Сумма <br> комиссии--%>
                                <th><loc:message code="transaction.commissionAmount"/></th>
                                <%--Платежная <br> система--%>
                                <th><loc:message code="transaction.merchant"/></th>
                                <th><loc:message code="transaction.order"/></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>

                <%--форма список кошельков--%>
                <div id="panel3" class="tab-pane">
                    <%--<div class="container orders_new transaction my_orders orders .container_footer_bottom my_wallets">--%>
                    <%--<div class="row">--%>
                    <div class="col-sm-6 content">
                        <%--СПИСОК СЧЕТОВ--%>
                        <h4><loc:message code="admin.wallets"/></h4>
                        <hr>
                        <table id="walletsTable"
                               class="admin-table table table-hover table-bordered table-striped"
                               style="width:100%">
                            <thead>
                            <tr>
                                <%--RUB--%>
                                <th></th>
                                <%--Активный баланс>--%>
                                <th><loc:message code="mywallets.abalance"/></th>
                                <%--Резерв--%>
                                <th><loc:message code="mywallets.rbalance"/></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>

            </div>
        </div>
    </div>
    <hr>
</main>
<div id="prompt_delete_rqst">
    <loc:message code="admin.promptDeleteUserFiles"/>
</div>

<%@include file='order_delete.jsp' %>


</body>
</html>

