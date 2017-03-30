<%@ page import="me.exrates.model.enums.invoice.InvoiceOperationPermission" %>
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
    <%@include file='links_scripts.jsp' %>
    <link rel="stylesheet" href="<c:url value="/client/css/font-awesome.min.css"/>">

    <%----------%>
    <link rel="stylesheet" href="<c:url value="/client/css/jquery-ui.css"/>">
    <script type="text/javascript" src="<c:url value='/client/js/jquery-ui.js'/>"></script>
    <link rel="stylesheet" href="<c:url value="/client/css/jquery.datetimepicker.css"/>">
    <script type="text/javascript" src="<c:url value='/client/js/jquery.datetimepicker.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/moment-with-locales.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminTransactionsDataTable.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/downloadTransactions.js'/>"></script>


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
                    <div class="col-md-12 content">
                        <%--ИСТОРИЯ ОПЕРАЦИЙ--%>
                        <div class="text-center"><h4><loc:message code="transactions.title"/></h4></div>

                            <%--Current user and email--%>
                            <div>
                                <h5><b>
                                    ${user.nickname}, ${user.email}
                            </div>

                            <form id="transactions_history_download_form" class="form_auto_height" method="get">
                                <div class="delete-order-info__item error error-search"><loc:message
                                        code="admin.user.transactions.downloadHistory.choosePeriod"/>
                                    <span>
                                        <input id="trans_download_start" type="text" name="startDate">
                                        <input id="trans_download_end" type="text" name="endDate">
                                    </span>
                                </div>
                            </form>
                        <button data-toggle="collapse" class="blue-box" id="download_trans_history_action" style="margin: 10px 0;">
                            <loc:message code="admin.user.transactions.downloadHistory"/></button>

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
                                <th>Source ID</th>
                            </tr>
                            </thead>
                        </table>
                    </div>

    </div>
    <hr>
</main>
<input path="id" type="hidden"
            class="input-block-wrapper__input"
            id="user-id" value="${user.id}"/>

<%@include file='../fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>


</body>
</html>

