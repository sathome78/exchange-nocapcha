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
    <title><loc:message code="orders.title"/></title>
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

    <script type="text/javascript" src="<c:url value='/client/js/dashboard.js'/>"></script>
    <%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/menuSwitcher.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/notyInit.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/submits/orderBeginAccept.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/calculateCreateOrderFormField.js'/>"></script>
    <%----------%>

</head>

<body>

<%@include file='header_new.jsp' %>

<main class="container orders_new transaction my_orders orders">
    <%@include file='exchange_info_new.jsp' %>
    <div class="row">
        <%@include file='usermenu_new.jsp' %>

        <div class="col-sm-9 content">
            <div class="tab-content">
                <c:set var="submitUrl" value="/order/submit"/>
                <%--BUY form - user can buy--%>
                <%--On this form displayed SELL orders--%>
                <div class="buy-form">
                    <%@include file="orderForBuyForm.jsp" %>
                </div>
                <%--SELL form - user can sell--%>
                <%--On this form displayed BUY orders--%>
                <div class="sell-form">
                    <%@include file="orderForSellForm.jsp" %>
                </div>
            </div>
            <hr>
            <div class="row">
                <%--LIST OF ORDERS SELL--%>
                <div class="col-sm-6">
                    <p><loc:message code="orders.listtosell"/></p>
                    <table>
                        <tbody>
                        <%--Rate	Currency1	Currency2	--%>
                        <tr>
                            <th><loc:message code="orders.exrate"/></th>
                            <th>${orderCreateDto.currencyPair.getCurrency1().getName()}</th>
                            <th>${orderCreateDto.currencyPair.getCurrency2().getName()}</th>
                            <th></th>
                        </tr>
                        <c:forEach var="order" items="${sellOrdersList}">
                            <tr>
                                <td><fmt:formatNumber type="number" maxFractionDigits="9"
                                                      value="${order.exrate}"/></td>
                                <td><fmt:formatNumber type="number" maxFractionDigits="9"
                                                      value="${order.amountBase}"/></td>
                                <td><fmt:formatNumber type="number" maxFractionDigits="9"
                                                      value="${order.amountConvert}"/></td>
                                <td>
                                    <button onclick="beginAcceptOrder(${order.id})"><loc:message
                                            code="orders.accept"/></button>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <%--LIST OF ORDERS BUY--%>
                <div class="col-sm-6">
                    <p><loc:message code="orders.listtobuy"/></p>
                    <table>
                        <tbody>
                        <%--Rate	Currency1	Currency2	--%>
                        <tr>
                            <th><loc:message code="orders.exrate"/></th>
                            <th>${orderCreateDto.currencyPair.getCurrency1().getName()}</th>
                            <th>${orderCreateDto.currencyPair.getCurrency2().getName()}</th>
                            <th></th>
                        </tr>
                        <c:forEach var="order" items="${buyOrdersList}">
                            <tr>
                                <td><fmt:formatNumber type="number" maxFractionDigits="9"
                                                      value="${order.exrate}"/></td>
                                <td><fmt:formatNumber type="number" maxFractionDigits="9"
                                                      value="${order.amountBase}"/></td>
                                <td><fmt:formatNumber type="number" maxFractionDigits="9"
                                                      value="${order.amountConvert}"/></td>
                                <td>
                                    <button onclick="beginAcceptOrder(${order.id})"><loc:message
                                            code="orders.accept"/></button>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
    <hr>
    <%--not used. might come useful for future (send startup message)--%>
    <span hidden id="errorNoty">${notEnoughMoney}</span>
</main>
<%@include file='footer_new.jsp' %>
<%@include file='finpassword.jsp' %>
</body>
</html>

