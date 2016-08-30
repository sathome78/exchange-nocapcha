<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 30.08.2016
  Time: 15:24
  To change this template use File | Settings | File Templates.
--%>
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
    <title>User sessions</title>
    <%@include file='links_scripts.jsp' %>
</head>


<body>

<%@include file='../fragments/header-simple.jsp' %>

<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-8 col-md-offset-1 content admin-container">
            <div class="text-center"><h4><loc:message code="transaction.titleInvoice"/></h4></div>
            <c:choose>
                <c:when test="${fn:length(userSessions)==0}">
                    <loc:message code="transactions.absent"/>
                </c:when>
                <c:otherwise>
                    <table id="user_sessions">
                        <thead>
                        <tr>
                                <%--Дата--%>
                            <th></th>
                                <%--Пользователь--%>
                            <th></th>
                                <%--Валюта--%>
                            <th></th>
                                <%--Сумма--%>
                            <th></th>
                                <%--Сумма <br> комиссии--%>
                            <th><loc:message code="transaction.commissionAmount"/></th>
                                <%--Дата обработки заявки--%>
                            <th><loc:message code="transaction.acceptanceDatetime"/></th>

                                <%--Confirmation--%>
                            <th><loc:message code="transaction.confirmation"/></th>

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
                                    <a href="<c:url value='/admin/userInfo'>
                                    <c:param name="id" value="${invoiceRequest.userId}"/>
                                    </c:url>">${invoiceRequest.userEmail}</a>
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
                                    <c:choose>
                                        <c:when test="${invoiceRequest.acceptanceTime == null}">
                                            _
                                        </c:when>
                                        <c:otherwise>
                                            ${invoiceRequest.acceptanceTime.toLocalDate()}<br/>
                                            ${invoiceRequest.acceptanceTime.toLocalTime()}
                                        </c:otherwise>
                                    </c:choose>

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
                                    <c:choose>
                                        <c:when test="${not empty invoiceRequest.acceptanceUserEmail}">
                                            <a href="<c:url value='/admin/userInfo'>
                                            <c:param name="id" value="${invoiceRequest.acceptanceUserId}"/>
                                            </c:url>">${invoiceRequest.acceptanceUserEmail}</a>
                                        </c:when>
                                        <c:otherwise>
                                            _
                                        </c:otherwise>
                                    </c:choose>

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
<%@include file='../fragments/footer.jsp' %>
</body>
</html>

