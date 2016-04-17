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
    <link href="<c:url value='/client/css/style-new.css'/>" rel="stylesheet">

    <script type="text/javascript" src="<c:url value='/client/js/dashboard.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/client/js/function.js'/>"></script>
    <%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/menuSwitcher.js'/>"></script>
    <%----------%>
</head>


<body>

<%@include file='header_new.jsp' %>

<main class="container orders_new transaction my_orders orders .container_footer_bottom my_wallets">
    <%@include file='exchange_info_new.jsp' %>
    <div class="row">
        <%@include file='usermenu_new.jsp' %>

        <div class="col-sm-9 content">
            <c:choose>
                <c:when test="${fn:length(transactions)==0}">
                    <loc:message code="transactions.absent"/>
                </c:when>
                <c:otherwise>
                    <%--ИСТОРИЯ ОПЕРАЦИЙ--%>
                    <h4><loc:message code="transactions.title"/></h4>
                    <table>
                        <tbody>
                        <tr>
                                <%--Дата--%>
                            <th><loc:message code="transaction.datetime"/></th>
                                <%--Тип--%>
                            <th><loc:message code="transaction.operationType"/></th>
                                <%--Валюта--%>
                            <th><loc:message code="transaction.currency"/></th>
                                <%--Сумма--%>
                            <th><loc:message code="transaction.amount"/></th>
                                <%--Валюта <br> покупки--%>
                            <th><loc:message code="transaction.currencyBuy"/></th>
                                <%--Сумма <br> покупки--%>
                            <th><loc:message code="transaction.amountBuy"/></th>
                                <%--Сумма <br> комиссии--%>
                            <th><loc:message code="transaction.commissionAmount"/></th>
                                <%--Платежная <br> система--%>
                            <th><loc:message code="transaction.merchant"/></th>
                            <th><loc:message code="transaction.status"/></th>
                        </tr>
                        <c:forEach var="transaction" items="${transactions}">
                            <tr>
                                    <%--2016-03-08 <br> 14:48:46--%>
                                <td>
                                    <fmt:parseDate value="${transaction.datetime}" var="parsedDate"
                                                   pattern="yyyy-MM-dd'T'HH:mm"/>
                                    <fmt:formatDate value="${parsedDate}" pattern="yyyy-MM-dd'<br/>'HH:mm"/>
                                </td>
                                <td>
                                        <%--Принятие <br> ордера--%>
                                    <c:choose>
                                        <c:when test="${transaction.operationType.type eq 1 or transaction.operationType.type eq 2}">
                                            <loc:message code="transaction.operationType${transaction.operationType}"/>
                                        </c:when>
                                        <c:when test="${transaction.orderStatus.status eq 2}">
                                            <loc:message code="transaction.operationTypeCreateOrder"/>
                                        </c:when>
                                        <c:when test="${transaction.orderStatus.status eq 3}">
                                            <loc:message code="transaction.operationTypeAcceptOrder"/>
                                        </c:when>
                                    </c:choose>
                                </td>
                                    <%--USD--%>
                                <td>
                                        ${transaction.currency}
                                </td>
                                    <%--Amount--%>
                                <td>
                                    <fmt:formatNumber value="${transaction.amount}" maxFractionDigits="9"/>
                                </td>
                                    <%--EUR--%>
                                <td>
                                        ${transaction.currencyBuy}
                                </td>
                                    <%--сумма--%>
                                <td>
                                    <fmt:formatNumber value="${transaction.amountBuy}" maxFractionDigits="9"/>
                                </td>
                                    <%--комиссия--%>
                                <td>
                                    <fmt:formatNumber value="${transaction.commissionAmount}" maxFractionDigits="9"/>
                                </td>
                                    <%--мерч имя--%>
                                <td>
                                    <c:if test="${transaction.orderStatus eq null}">
                                        ${transaction.merchant.name}
                                    </c:if>
                                </td>
                                <td>
                                    <c:if test="${transaction.status != null}">
                                        ${transaction.status}
                                    </c:if>
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
<%@include file='footer_new.jsp' %>
</body>
</html>

