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
<link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>
<%@include file='links_scripts.jsp' %>
<link rel="stylesheet" href="<c:url value="/client/css/font-awesome.min.css"/>">
<link href="<c:url value="/client/css/ekko-lightbox.min.css"/>" rel="stylesheet">
<%----------%>
<script type="text/javascript" src="<c:url value="/client/js/ekko-lightbox.min.js"/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/dataTable/adminWalletsDataTable.js'/>"></script>
<link rel="stylesheet" href="<c:url value="/client/css/jquery-ui.css"/>">
<script type="text/javascript" src="<c:url value='/client/js/jquery-ui.js'/>"></script>
<link rel="stylesheet" href="<c:url value="/client/css/jquery.datetimepicker.css"/>">
<script type="text/javascript" src="<c:url value='/client/js/jquery.datetimepicker.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/moment-with-locales.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/dataTable/adminTransactionsDataTable.js'/>"></script>




 <%----------%>
    <%----------%>
 <%@include file="../tools/alexa.jsp" %>

</head>

<body>

<%@include file='../fragments/header-simple.jsp' %>

<main class="container orders_new admin side_menu">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-8 col-md-offset-1 content admin-container">
            <div class="buttons">
                <c:set var="adminEnum" value="<%=me.exrates.model.enums.UserRole.ADMINISTRATOR%>"/>
                <c:set var="accountantEnum" value="<%=me.exrates.model.enums.UserRole.ACCOUNTANT%>"/>
                <c:set var="admin_userEnum" value="<%=me.exrates.model.enums.UserRole.ADMIN_USER%>"/>
                <%--Редактирование пользователя--%>
                <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                    <button class="active adminForm-toggler blue-box">
                        <loc:message code="admin.user"/>
                    </button>
                </sec:authorize>
                <%--Список транзакций--%>
                <sec:authorize access="hasAnyAuthority('${adminEnum}')">
                    <button class="adminForm-toggler blue-box">
                        <loc:message code="admin.transactions"/>
                    </button>
                </sec:authorize>
                <%--Список кошельков--%>
                <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}')">
                    <button class="adminForm-toggler blue-box">
                        <loc:message code="admin.wallets"/>
                    </button>
                </sec:authorize>

            <%--Current user and email--%>
            <div>
                <h5><b>
                ${user.nickname}, ${user.email}
            </div>
            <%--контейнер для данных пользователей--%>
            <div class="tab-content">
                <%--форма редактирование пользователя--%>
                <div id="panel1" class="tab-pane active">
                    <div class="col-md-8 content">
                        <div class="text-center"> <h4>
                            <b><loc:message code="admin.editUser"/></b>
                        </h4></div>

                        <div class="panel-body">

                            <form:form class="form-horizontal" id="user-edit-form" action="/admin/edituser/submit"
                                       method="post" modelAttribute="user">
                                <div>
                                    <fieldset class="field-user">
                                        <div class="input-block-wrapper">
                                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                                <label for="user-name" class="input-block-wrapper__label"><loc:message
                                                        code="admin.login"/></label>
                                            </div>

                                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                                <form:input path="id" type="hidden" class="input-block-wrapper__input"
                                                            id="user-id"/>
                                                <form:input path="nickname" class="input-block-wrapper__input admin-form-input"
                                                            id="user-name"
                                                            readonly="true"/>
                                            </div>
                                        </div>
                                        <div class="input-block-wrapper">
                                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                                <label for="user-email" class="input-block-wrapper__label"><loc:message
                                                        code="admin.email"/></label>
                                            </div>
                                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                                <form:input path="email" class="input-block-wrapper__input admin-form-input"
                                                            id="user-email"
                                                            readonly="true"/>
                                                <form:errors path="email" class="input-block-wrapper__input"
                                                             style="color:red"/>
                                            </div>
                                        </div>

                                        <div class="input-block-wrapper">
                                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                                <label for="user-password" path="password"
                                                       class="input-block-wrapper__label"><loc:message
                                                        code="admin.password"/></label>
                                            </div>
                                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                                <form:password path="password" class="input-block-wrapper__input admin-form-input"
                                                               id="user-password"/>
                                                <form:errors path="password" class="input-block-wrapper__input"
                                                             style="color:red"/>
                                            </div>
                                        </div>

                                        <div class="input-block-wrapper">
                                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                                <label for="user-phone" class="input-block-wrapper__label"><loc:message
                                                        code="admin.phone"/></label>
                                            </div>
                                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                                <form:input path="phone" class="input-block-wrapper__input admin-form-input"
                                                            id="user-phone"/>
                                                <form:errors path="phone" class="input-block-wrapper__input"
                                                             style="color:red"/>
                                            </div>
                                        </div>

                                        <div class="input-block-wrapper">
                                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                                <label for="user-role" class="input-block-wrapper__label"><loc:message
                                                        code="admin.role"/></label>
                                            </div>
                                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                                <form:select path="role" id="user-role"
                                                             class="input-block-wrapper__input admin-form-input"
                                                             name="user-role">
                                                    <c:forEach items="${roleList}" var="role">
                                                        <option value="${role}"
                                                                <c:if test="${role eq user.role}">SELECTED</c:if>>${role}</option>
                                                    </c:forEach>
                                                </form:select>
                                            </div>
                                        </div>

                                        <div class="input-block-wrapper">
                                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                                <label for="user-status" class="input-block-wrapper__label"><loc:message
                                                        code="admin.status"/></label>
                                            </div>
                                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                                <form:select path="userStatus" id="user-status"
                                                             class="input-block-wrapper__input admin-form-input"
                                                             name="user-status">
                                                    <c:forEach items="${statusList}" var="status">
                                                        <option value="${status}"
                                                                <c:if test="${status eq user.status}">SELECTED</c:if>>${status}</option>
                                                    </c:forEach>
                                                </form:select>
                                            </div>
                                        </div>

                                        <div class="input-block-wrapper">
                                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                                <label for="user-name" class="input-block-wrapper__label"><loc:message
                                                        code="register.sponsor"/></label>
                                            </div>

                                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                                <form:input path="parentEmail" readonly="true" class="input-block-wrapper__input admin-form-input"
                                                            id="parentEmail"/>
                                            </div>
                                        </div>
                                        <div class="admin-submit-group">
                                            <div>
                                                <loc:message code="admin.save" var="saveSubmit"></loc:message>
                                                <button class="blue-box" type="submit">${saveSubmit}</button>

                                                <loc:message code="admin.cancel" var="cancelSubmit"></loc:message>
                                                <button class="blue-box" type="reset"
                                                        onclick="javascript:window.location='/admin';">${cancelSubmit}</button>
                                            </div>
                                        </div>
                                    </fieldset>
                                </div>
                            </form:form>
                            <c:choose>
                                <c:when test="${userFiles.size() != 0}">
                                    <h4><loc:message code="admin.yourFiles"/></h4>
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
                    <div class="col-md-12 content">
                        <%--ИСТОРИЯ ОПЕРАЦИЙ--%>
                            <div class="text-center"><h4><loc:message code="transactions.title"/></h4></div>
                        <div id="transaction-filter">
                            <form id="transaction-search-form" method="get">
                                <%--STATUS--%>
                                    <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        Status
                                    </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <select id="status" class="input-block-wrapper__input admin-form-input" name="status">
                                        <option value="ALL">ALL</option>
                                        <c:forEach items="${transactionStatuses}" var="transactionStatus">
                                            <option value="${transactionStatus}">${transactionStatus}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                    </div>
                                    <%--TYPE--%>
                                    <div class="input-block-wrapper">
                                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                                            <label class="input-block-wrapper__label">
                                                Type
                                            </label>
                                        </div>
                                        <div class="col-md-9 input-block-wrapper__input-wrapper">
                                            <select multiple id="type" class="input-block-wrapper__input admin-form-input" name="type">
                                                <option value="ALL">ALL</option>
                                                <c:forEach items="${transactionTypes}" var="type">
                                                    <option value="${type}"><span>${type}</span>
                                                </c:forEach>
                                            </select>
                                        </div>

                                    </div>
                                    <%--MERCHANT--%>
                                    <div class="input-block-wrapper">
                                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                                            <label class="input-block-wrapper__label">
                                                Merchant
                                            </label>
                                        </div>
                                        <div class="col-md-9 input-block-wrapper__input-wrapper">
                                            <select multiple id="merchant" class="input-block-wrapper__input admin-form-input" name="merchant">
                                                <option value="ALL">ALL</option>
                                                <c:forEach items="${merchants}" var="merchant">
                                                <option value="${merchant.name}"><span>${merchant.name}</span>
                                                    </c:forEach>
                                            </select>

                                        </div>

                                    </div>
                                    <%--TIME--%>
                                    <div class="input-block-wrapper">
                                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                                            <label class="input-block-wrapper__label">
                                                Date
                                            </label>
                                        </div>
                                        <div class="col-md-9 input-block-wrapper__input-wrapper">
                                            <input id="datetimepicker_start" type="text" name="startDate">
                                            <input id="datetimepicker_end" type="text" name="endDate">
                                        </div>

                                    </div>
                                    <%--AMOUNT--%>
                                    <div class="input-block-wrapper">
                                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                                            <label class="input-block-wrapper__label">
                                                Amount
                                            </label>
                                        </div>
                                        <div class="col-md-9 input-block-wrapper__input-wrapper">
                                            <input type="hidden" id="max-amount" value="${maxAmount}">
                                            <input type="text" readonly id="amount" name="amount">
                                            <div id="amount-slider"></div>

                                        </div>

                                    </div>
                                    <%--COMMISSION_AMOUNT--%>
                                    <div class="input-block-wrapper">
                                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                                            <label class="input-block-wrapper__label">
                                                Commission amount
                                            </label>
                                        </div>
                                        <div class="col-md-9 input-block-wrapper__input-wrapper">
                                            <input type="hidden" id="max-commission-amount" value="${maxCommissionAmount}">
                                            <input type="text" readonly id="commission-amount" name="commissionAmount">
                                            <div id="commission-slider"></div>
                                        </div>

                                    </div>
                                <button id="filter-apply">Apply filter</button>

                            </form>

                        </div>


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
                    <div class="col-md-8 content">
                        <%--СПИСОК СЧЕТОВ--%>
                        <div class="text-center"><h4><loc:message code="admin.wallets"/></h4></div>

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
<div hidden id="prompt_delete_rqst">
    <loc:message code="admin.promptDeleteUserFiles"/>
</div>
<%@include file='order-modals.jsp' %>

<%@include file='../fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>

</body>
</html>

