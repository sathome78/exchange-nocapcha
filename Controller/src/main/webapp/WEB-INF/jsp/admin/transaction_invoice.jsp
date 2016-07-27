<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title><loc:message code="transactions.title"/></title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='https://fonts.googleapis.com/css?family=Roboto:400,700,300' rel='stylesheet' type='text/css'>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
    <script src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>" type="text/javascript"></script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/jquery.dataTables.min.css'/>" rel="stylesheet">

    <%--<script type="text/javascript" src="<c:url value='/client/js/jquery.dataTables.min.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/client/js/function.js'/>"></script>--%>
    <link rel="stylesheet" type="text/css" href="//cdn.datatables.net/1.10.12/css/jquery.dataTables.css">

    <script type="text/javascript" charset="utf8" src="//cdn.datatables.net/1.10.12/js/jquery.dataTables.js"></script>



<%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/menuSwitcher.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/notyInit.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/submits/invoiceSubmitAccept.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminInvoiceDataTable.js'/>"></script>
    <%----------%>
</head>


<body>

<%@include file='../fragments/header-simple.jsp' %>

<main class="container orders_new transaction my_orders orders .container_footer_bottom my_wallets">
    <%--<%@include file='../exchange_info_new.jsp' %>--%>
    <div class="row text-center">
        <div class="col-md-8 col-md-offset-2 content">
            <c:choose>
                <c:when test="${fn:length(invoiceRequests)==0}">
                    <loc:message code="transactions.absent"/>
                </c:when>
                <c:otherwise>
                    <%--СПИСОК ИНВОЙСОВ--%>
                    <h4><loc:message code="transaction.titleInvoice"/></h4>

                    <table id="invoice_requests">
                        <thead>
                        <tr>
                                <%--Дата--%>
                            <th><loc:message code="transaction.datetime"/></th>
                                <%--Пользователь--%>
                            <th><loc:message code="transaction.user"/></th>
                                <%--Валюта--%>
                            <th><loc:message code="transaction.currency"/></th>
                                <%--Сумма--%>
                            <th><loc:message code="transaction.amount"/></th>
                                <%--Сумма <br> комиссии--%>
                            <th><loc:message code="transaction.commissionAmount"/></th>
                                <%--Дата обработки заявки--%>
                            <th><loc:message code="transaction.acceptanceDatetime"/></th>

                                <%--Confirmation--%>
                            <th><loc:message code="transaction.сonfirmation"/></th>

                                <%--Пользователь, обработавший заявку--%>
                            <th><loc:message code="transaction.acceptanceUser"/></th>


                        </tr>
                        </thead>
                        <tbody>

                        <c:forEach var="invoiceRequest" items="${invoiceRequests}">
                            <tr>
                                <td style="white-space: nowrap;">
                                        ${invoiceRequest.transaction.datetime.toLocalDate()}<br/>
                                        ${invoiceRequest.transaction.datetime.toLocalTime()}
                                </td>
                                <td><%--User--%>
                                        ${invoiceRequest.userEmail}
                                </td>
                                    <%--USD--%>
                                <td>
                                        ${invoiceRequest.transaction.currency.getName()}
                                </td>
                                    <%--Amount--%>
                                <td>
                                    <fmt:formatNumber value="${invoiceRequest.transaction.amount}" maxFractionDigits="9"/>
                                </td>
                                    <%--комиссия--%>
                                <td>
                                    <fmt:formatNumber value="${invoiceRequest.transaction.commissionAmount}" maxFractionDigits="9"/>
                                </td>
                                <td>
                                        ${invoiceRequest.acceptanceTime.toLocalDate()}<br/>
                                        ${invoiceRequest.acceptanceTime.toLocalTime()}
                                </td>
                                    <%--Подтвердить--%>
                                <td>
                                    <c:choose>
                                    <c:when test="${invoiceRequest.acceptanceTime == null}">
                                        <button class="acceptbtn" type="submit"
                                                onclick="submitAcceptInvoice(${invoiceRequest.transaction.id})"><loc:message
                                                code="transaction.accept"/></button>
                                    </c:when>
                                    <c:otherwise>

                                        <loc:message code="transaction.provided"/>
                                    </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                        ${invoiceRequest.acceptanceUserEmail}
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>

                </c:otherwise>
            </c:choose>
        </div>
    </div>
    <hr/>
</main>
<%--<%@include file='../footer_new.jsp' %>--%>
</body>
</html>

