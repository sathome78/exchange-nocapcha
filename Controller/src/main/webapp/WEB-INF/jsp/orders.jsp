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

</head>

<body>

<%@include file='header_new.jsp' %>

<main class="container orders_new transaction my_orders orders">
    <%@include file='exchange_info_new.jsp' %>
    <div class="row">
        <%@include file='usermenu_new.jsp' %>

        <%--взял из старого - не понял для чего он... //TODO--%>
        <div>
            <c:if test="${msq ne ''}">
                <span style="color:red">${msg}</span><br><br>
            </c:if>
        </div>
        <%-- ... взял из старого - не понял для чего он--%>

        <div class="col-sm-9 content">
            <div class="buttons">
                <%--Создать ордер на продажу--%>
                <button class="active orderForm-toggler">
                    <loc:message code="orders.createordersell"/>
                </button>
                <%--Создать ордер на покупку--%>
                <button class="orderForm-toggler">
                    <loc:message code="orders.createorderbuy"/>
                </button>
            </div>

            <%--контейнер форм продажа - покупка--%>
            <div class="tab-content">
                <%--форма продажи--%>
                <div class="tab-pane active" id="tab__sell">
                    <c:set var="submiturl" value="order/submit"/>
                    <form:form class="form-horizontal withdraw__money" action="${submiturl}" method="post"
                               modelAttribute="order">
                        <%--Продаю--%>
                        <div>
                            <label><loc:message code="orders.currencyforsale"/></label>
                            <form:select path="currencySell" class="select form-control">
                                <form:options items="${currList}" itemLabel="name" itemValue="id"/>
                            </form:select>
                        </div>
                        <%--Сумма--%>
                        <div>
                            <form:input path="amountSell" class="form-control" placeholder="0.0"/>
                            <label><loc:message code="orders.sum1"/></label>
                            <form:errors class="form-input-error-message" path="amountSell" style="color:red"/>
                            <span>${notEnoughMoney}</span>
                        </div>
                        <%--Покупаю--%>
                        <div>
                            <label><loc:message code="orders.currencyforbuy"/></label>
                            <form:select path="currencyBuy" class="select form-control">
                                <form:options items="${currList}" itemLabel="name" itemValue="id"/>
                            </form:select>
                        </div>
                        <%--Сумма--%>
                        <div>
                            <form:input path="amountBuy" class="form-control" placeholder="0.0"/>
                            <label><loc:message code="orders.sum2"/></label>
                            <form:errors class="form-input-error-message" path="amountBuy" style="color:red"/>
                        </div>
                        <%--Комиссия с данной операции составит--%>
                        <p><loc:message code="orders.yourcommission"/>:
                            <span class="fee"><fmt:formatNumber type="number" maxFractionDigits="9"
                                                                value="${commission}"/>%
                            </span>
                        </p>
                        <%--Создать--%>
                        <c:set var="SELL" value="<%=me.exrates.model.enums.OperationType.SELL%>"/>
                        <form:hidden path="operationType" value="${SELL}"/>
                        <loc:message code="orders.submit" var="labelSubmit"/>
                        <button type="submit">${labelSubmit}</button>
                    </form:form>
                </div>

                <%--форма покупки--%>
                <div class="tab-pane" id="tab__buy">
                    <form:form class="form-horizontal withdraw__money" action="${submiturl}" method="post"
                               modelAttribute="order">
                        <%--Покупаю--%>
                        <div>
                            <label><loc:message code="orders.currencyforbuy"/></label>
                            <form:select path="currencyBuy" class="select form-control">
                                <form:options items="${currList}" itemLabel="name" itemValue="id"/>
                            </form:select>
                        </div>
                        <%--Сумма--%>
                        <div>
                            <form:input class="form-input-error-message" path="amountBuy" placeholder="0.0"/>
                            <label><loc:message code="orders.sum1"/></label>
                            <form:errors path="amountBuy" style="color:red"/>
                            <span>${notEnoughMoney}</span>
                        </div>
                        <%--Продаю--%>
                        <div>
                            <label><loc:message code="orders.currencyforsale"/></label>
                            <form:select path="currencySell" class="select form-control">
                                <form:options items="${currList}" itemLabel="name" itemValue="id"/>
                            </form:select>
                        </div>
                        <%--Сумма--%>
                        <div>
                            <form:input class="form-input-error-message" path="amountSell" placeholder="0.0"/>
                            <label><loc:message code="orders.sum2"/></label>
                            <form:errors path="amountSell" style="color:red"/>
                        </div>
                        <%--Комиссия с данной операции составит--%>
                        <p><loc:message code="orders.yourcommission"/>:
                            <span class="fee"><fmt:formatNumber type="number" maxFractionDigits="9"
                                                                value="${commission}"/>%
                            </span>
                        </p>
                        <%--Создать--%>
                        <c:set var="BUY" value="<%=me.exrates.model.enums.OperationType.BUY%>"/>
                        <form:hidden path="operationType" value="${BUY}"/>
                        <loc:message code="orders.submit" var="labelSubmit"/>
                        <button type="submit">${labelSubmit}</button>
                    </form:form>
                </div>
            </div>
            <hr>
            <div class="row">
                <%--СПИСОК ОРДЕРОВ НА ПРОДАЖУ--%>
                <div class="col-sm-6">
                    <p><loc:message code="orders.listtosell"/></p>
                    <table>
                        <tbody>
                        <%--Продаю	Сумма	Покупаю	Сумма--%>
                        <tr>
                            <th><loc:message code="orders.currsell"/></th>
                            <th><loc:message code="orders.amountsell"/></th>
                            <th><loc:message code="orders.currbuy"/></th>
                            <th><loc:message code="orders.amountbuy"/></th>
                            <th></th>
                        </tr>
                        <c:forEach var="order" items="${orderMap.sell}">
                            <tr>
                                <td> ${order.currencySellString} </td>
                                <td><fmt:formatNumber type="number" maxFractionDigits="9"
                                                      value="${order.amountSell}"/></td>
                                <td> ${order.currencyBuyString} </td>
                                <td><fmt:formatNumber type="number" maxFractionDigits="9"
                                                      value="${order.amountBuy}"/></td>
                                <td><a href="/orders/submitaccept?id=${order.id}"><loc:message
                                        code="orders.accept"/></a></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <!-- end col-sm-6 -->
                <%--СПИСОК ОРДЕРОВ НА ПОКУПКУ--%>
                <div class="col-sm-6">
                    <p><loc:message code="orders.listtobuy"/></p>
                    <table>
                        <tbody>
                        <%--Покупаю	Сумма	Продаю	Сумма--%>
                        <tr>
                            <th><loc:message code="orders.currbuy"/></th>
                            <th><loc:message code="orders.amountbuy"/></th>
                            <th><loc:message code="orders.currsell"/></th>
                            <th><loc:message code="orders.amountsell"/></th>
                            <th></th>
                        </tr>
                        <c:forEach var="order" items="${orderMap.buy}">
                            <tr>
                                <td> ${order.currencyBuyString} </td>
                                <td><fmt:formatNumber type="number" maxFractionDigits="9"
                                                      value="${order.amountBuy}"/></td>
                                <td> ${order.currencySellString} </td>
                                <td><fmt:formatNumber type="number" maxFractionDigits="9"
                                                      value="${order.amountSell}"/></td>
                                <td><a href="/orders/submitaccept?id=${order.id}"><loc:message code="orders.accept"/></a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
    <hr>
</main>
<%@include file='footer_new.jsp' %>
<%----------%>
<script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/menuSwitcher.js'/>"></script>
<%----------%>
</body>
</html>

