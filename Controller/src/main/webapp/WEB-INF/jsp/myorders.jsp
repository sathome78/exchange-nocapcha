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
    <title><loc:message code="orders.title"/></title>

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
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>

</head>


<body>

<%@include file='header_new.jsp' %>

<main class="container orders_new transaction my_orders">
    <%@include file='exchange_info_new.jsp' %>
    <div class="row">
        <%--изменить активный элемент //TODO --%>
        <%@include file='usermenu_new.jsp' %>

        <div class="col-sm-9 content">
            <%--Создать ордер--%>
            <form:form action="order/new">
                <loc:message code="myorders.create" var="labelCreate"/>
                <button type="submit">${labelCreate}</button>
            </form:form>
            <c:choose>

                <c:when test="${fn:length(orderMap.sell)==0 && fn:length(orderMap.buy)==0}">
                    <loc:message code="myorders.noorders"/>
                </c:when>
                <c:otherwise>
                    <%--Здесь можно просмотреть и удалить свои ордера--%>
                    <p><loc:message code="myorders.text"/></p>
                    <%--ОРДЕРА НА ПРОДАЖУ--%>
                    <c:if test="${fn:length(orderMap.sell) ne 0}">
                        <h4><loc:message code="myorders.sellorders"/></h4>
                        <hr>
                        <table>
                            <tbody>
                            <tr>
                                    <%--Продаю--%>
                                <th><loc:message code="myorders.currsell"/></th>
                                    <%--Сумма--%>
                                <th><loc:message code="myorders.amountsell"/></th>
                                    <%--Покупаю--%>
                                <th><loc:message code="myorders.currbuy"/></th>
                                    <%--Сумма (без <br> комиссии)--%>
                                <th><loc:message code="myorders.amountbuy"/></th>
                                    <%--Комиссия %--%>
                                <th><loc:message code="myorders.commission"/></th>
                                    <%--Сумма <br> с комиссией--%>
                                <th><loc:message code="myorders.amountwithcommission"/></th>
                                    <%--Дата <br> создания--%>
                                <th><loc:message code="myorders.datecreation"/></th>
                                    <%--Дата <br> исполнения--%>
                                <th><loc:message code="myorders.datefinal"/></th>
                                    <%--Статус--%>
                                <th><loc:message code="myorders.status"/></th>
                            </tr>
                            <c:forEach var="myorder" items="${orderMap.sell}">
                                <tr>
                                        <%--RUB--%>
                                    <td> ${myorder.currencySellString} </td>
                                        <%--сумма--%>
                                    <td><fmt:formatNumber type="number" maxFractionDigits="9"
                                                          value="${myorder.amountSell}"/></td>
                                        <%--USD--%>
                                    <td> ${myorder.currencyBuyString} </td>
                                        <%--сумма--%>
                                    <td><fmt:formatNumber type="number" maxFractionDigits="9"
                                                          value="${myorder.amountBuy}"/></td>
                                        <%--комиссия--%>
                                    <td><fmt:formatNumber type="number" maxFractionDigits="9"
                                                          value="${myorder.commission}"/>%
                                    </td>
                                        <%--с комиссией--%>
                                    <td><fmt:formatNumber type="number" maxFractionDigits="9"
                                                          value="${myorder.amountBuyWithCommission}"/></td>
                                        <%--2016-03-08 14:48:46--%>
                                    <td> ${myorder.dateCreation} </td>
                                    <td><c:if
                                            test="${myorder.status.status eq 3}">                                                            ${myorder.dateFinal}                                                        </c:if></td>
                                    <td> ${myorder.statusString} </td>
                                    <td><c:if test="${(myorder.status.status eq 2)||(myorder.status.status eq 1)}"> <a
                                            href="myorders/submitdelete?id=${myorder.id}"><loc:message
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
                                <th class="col-xs-4"><loc:message code="myorders.currbuy"/></th>
                                <th class="col-xs-4"><loc:message code="myorders.amountbuy"/></th>
                                <th class="col-xs-4"><loc:message code="myorders.currsell"/></th>
                                <th class="col-xs-4"><loc:message code="myorders.amountsell"/></th>
                                <th class="col-xs-4"><loc:message code="myorders.commission"/></th>
                                <th class="col-xs-4"><loc:message code="myorders.amountwithcommission"/></th>
                                <th class="col-xs-4"><loc:message code="myorders.datecreation"/></th>
                                <th class="col-xs-4"><loc:message code="myorders.datefinal"/></th>
                                <th class="col-xs-4"><loc:message code="myorders.status"/></th>
                            </tr>
                            <c:forEach var="myorder" items="${orderMap.buy}">
                                <tr>
                                    <td> ${myorder.currencyBuyString} </td>
                                    <td><fmt:formatNumber type="number" maxFractionDigits="9"
                                                          value="${myorder.amountBuy}"/></td>
                                    <td> ${myorder.currencySellString} </td>
                                    <td><fmt:formatNumber type="number" maxFractionDigits="9"
                                                          value="${myorder.amountSell}"/></td>
                                    <td><fmt:formatNumber type="number" maxFractionDigits="9"
                                                          value="${myorder.commission}"/>%
                                    </td>
                                    <td><fmt:formatNumber type="number" maxFractionDigits="9"
                                                          value="${myorder.amountBuyWithCommission}"/></td>
                                    <td> ${myorder.dateCreation} </td>
                                    <td><c:if
                                            test="${myorder.status.status eq 3}">                                        ${myorder.dateFinal}                                    </c:if></td>
                                    <td> ${myorder.statusString} </td>
                                    <td>
                                        <c:if test="${(myorder.status.status eq 2)||(myorder.status.status eq 1)}">
                                            <a href="myorders/submitdelete?id=${myorder.id}"><loc:message
                                                    code="myorders.delete"/></a>
                                        </c:if></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</main>
<%@include file='footer_new.jsp' %>

<%----------%>
<script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
<%----------%>
</body>
</html>

