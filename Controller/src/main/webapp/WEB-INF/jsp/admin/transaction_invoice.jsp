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
    <%@include file='links_scripts.jsp' %>
</head>


<body>

<%@include file='../fragments/header-simple.jsp' %>

<main class="container orders_new transaction my_orders orders .container_footer_bottom my_wallets">
    <%--<%@include file='../exchange_info_new.jsp' %>--%>
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-8 col-md-offset-1 content text-center">
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

