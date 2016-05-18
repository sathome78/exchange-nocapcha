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
    <title><loc:message code="myorders.title"/></title>
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

<main class="container orders_new transaction my_orders">
    <%@include file='exchange_info_new.jsp' %>
    <div class="row">
        <%@include file='usermenu_new.jsp' %>

        <div class="col-sm-9 content">
            <c:choose>
                <c:when test="${fn:length(orderMap.sell)==0 && fn:length(orderMap.buy)==0}">
                    <loc:message code="myorders.noorders"/>
                </c:when>
                <c:otherwise>
                    <%--Здесь можно просмотреть и удалить свои ордера--%>
                    <p><loc:message code="myorders.text"/></p>
                    <%--SELL ORDERS--%>
                    <c:if test="${fn:length(orderMap.sell) ne 0}">
                        <h4><loc:message code="myorders.sellorders"/></h4>
                        <hr>
                        <table>
                            <tbody>
                            <tr>
                                <th></th>
                                    <%--Sell--%>
                                <th class="allign-center"><loc:message code="myorders.currsell"/></th>
                                    <%--Buy --%>
                                <th class="allign-center"><loc:message code="myorders.currbuy"/></th>
                                    <%--Comission rate %--%>
                                <th class="allign-center"><loc:message code="myorders.commission"/></th>
                                    <%--Amount with commission--%>
                                <th class="allign-center"><loc:message code="myorders.amountwithcommission"/></th>
                                    <%--creation date--%>
                                <th class="allign-center"><loc:message code="myorders.datecreation"/></th>
                                    <%--accepted date--%>
                                <th class="allign-center"><loc:message code="myorders.datefinal"/></th>
                                    <%--status--%>
                                <th class="allign-center"><loc:message code="myorders.status"/></th>
                            </tr>
                            <c:forEach var="myorder" items="${orderMap.sell}">
                                <tr>
                                        <%--BTC/USD--%>
                                    <td> ${myorder.getCurrencyPairName()} </td>
                                        <%--amount BTC --%>
                                    <td class="allign-center"><fmt:formatNumber type="number" maxFractionDigits="9"
                                                          value="${myorder.getAmountBase()}"/></td>
                                        <%--amount USD--%>
                                    <td class="allign-center"><fmt:formatNumber type="number" maxFractionDigits="9"
                                                          value="${myorder.getAmountConvert()}"/></td>
                                        <%--commission--%>
                                    <td class="allign-center"><fmt:formatNumber type="number" maxFractionDigits="9"
                                                          value="${myorder.getCommissionFixedAmount()}"/>
                                    </td>
                                        <%--with commission--%>
                                    <td class="allign-center"><fmt:formatNumber type="number" maxFractionDigits="9"
                                                          value="${myorder.getAmountConvert().add(myorder.getCommissionFixedAmount().negate())}"/></td>
                                        <%--2016-03-08 14:48:46--%>
                                    <td class="allign-center">
                                        <fmt:parseDate value="${myorder.getDateCreation()}" var="parsedDate"
                                                       pattern="yyyy-MM-dd'T'HH:mm"/>
                                        <fmt:formatDate value="${parsedDate}" pattern="yyyy-MM-dd'<br/>'HH:mm"/>
                                    </td>
                                    <td class="allign-center">
                                        <c:if test="${myorder.status.status eq 3}">
                                            <fmt:parseDate value="${myorder.getDateAcception()}" var="parsedDate"
                                                           pattern="yyyy-MM-dd'T'HH:mm"/>
                                            <fmt:formatDate value="${parsedDate}" pattern="yyyy-MM-dd'<br/>'HH:mm"/>
                                        </c:if>
                                    </td>
                                    <td class="allign-center">${myorder.getStatusString()}
                                    </td>
                                    <td><c:if test="${(myorder.status.status eq 2)||(myorder.status.status eq 1)}">
                                        <a class="button_delete_order"
                                            href="/myorders/submitdelete?id=${myorder.id}"><loc:message
                                            code="myorders.delete"/></a> </c:if></td>
                                </tr>
                            </c:forEach>

                            </tbody>
                        </table>
                    </c:if>
                    <c:if test="${fn:length(orderMap.buy) ne 0}">
                        <h4><loc:message code="myorders.buyorders"/></h4>
                        <hr>
                        <table>
                            <tbody>
                            <tr>
                                <th></th>
                                <th class="allign-center"><loc:message code="myorders.currbuy"/></th>
                                <th class="allign-center"><loc:message code="myorders.currsell"/></th>
                                <th class="allign-center"><loc:message code="myorders.commission"/></th>
                                <th class="allign-center"><loc:message code="myorders.amountwithcommission"/></th>
                                <th class="allign-center"><loc:message code="myorders.datecreation"/></th>
                                <th class="allign-center"><loc:message code="myorders.datefinal"/></th>
                                <th class="allign-center"><loc:message code="myorders.status"/></th>
                            </tr>
                            <c:forEach var="myorder" items="${orderMap.buy}">
                                <tr>
                                        <%--BTC/USD--%>
                                    <td> ${myorder.getCurrencyPairName()} </td>
                                        <%--amount BTC --%>
                                    <td class="allign-center"><fmt:formatNumber type="number" maxFractionDigits="9"
                                                          value="${myorder.getAmountBase()}"/></td>
                                        <%--amount USD--%>
                                    <td class="allign-center"><fmt:formatNumber type="number" maxFractionDigits="9"
                                                          value="${myorder.getAmountConvert()}"/></td>
                                        <%--commission--%>
                                    <td class="allign-center"><fmt:formatNumber type="number" maxFractionDigits="9"
                                                          value="${myorder.getCommissionFixedAmount()}"/>
                                    </td>
                                        <%--with commission--%>
                                    <td class="allign-center"><fmt:formatNumber type="number" maxFractionDigits="9"
                                                          value="${myorder.getAmountConvert().add(myorder.getCommissionFixedAmount())}"/></td>
                                        <%--2016-03-08 14:48:46--%>
                                    <td class="allign-center">
                                        <fmt:parseDate value="${myorder.getDateCreation()}" var="parsedDate"
                                                       pattern="yyyy-MM-dd'T'HH:mm"/>
                                        <fmt:formatDate value="${parsedDate}" pattern="yyyy-MM-dd'<br/>'HH:mm"/>
                                    </td>
                                    <td class="allign-center">
                                        <c:if test="${myorder.status.status eq 3}">
                                            <fmt:parseDate value="${myorder.getDateAcception()}" var="parsedDate"
                                                           pattern="yyyy-MM-dd'T'HH:mm"/>
                                            <fmt:formatDate value="${parsedDate}" pattern="yyyy-MM-dd'<br/>'HH:mm"/>
                                        </c:if>
                                    </td>
                                    <td class="allign-center">${myorder.getStatusString()}
                                    </td>
                                    <td><c:if test="${(myorder.status.status eq 2)||(myorder.status.status eq 1)}">
                                        <a class="button_delete_order"
                                            href="/myorders/submitdelete?id=${myorder.id}"><loc:message
                                            code="myorders.delete"/></a> </c:if></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    <hr/>
</main>
<%@include file='footer_new.jsp' %>
</body>
</html>

