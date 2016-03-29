<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
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

    <script type="text/javascript" src="<c:url value='/client/js/dashboard.js'/>"></script>
    <script type="text/javascript" src="<c:url value='https://www.google.com/jsapi'/>"></script>
    <script type="text/javascript">
        google.load("visualization", "1", {"packages": ["corechart"]});
    </script>

</head>


<body>

<%@include file='header_new.jsp' %>

<main class="container orders_new transaction my_orders orders">
    <%@include file='exchange_info_new.jsp' %>
    <div class="row">
        <%--изменить активный элемент //TODO --%>
        <%@include file='usermenu_new.jsp' %>

        <div class="col-sm-9 content">
            <div class="row">
                <div class="col-sm-9">
                    <%--Подтвердите создание ордера с такими данными	--%>
                    <h4><loc:message code="submitorder.text"/>:</h4>
                    <c:set var="SELL" value="<%=me.exrates.model.enums.OperationType.SELL%>"/>
                    <c:set var="BUY" value="<%=me.exrates.model.enums.OperationType.BUY%>"/>
                    <hr>

                    <%--форма--%>
                    <div class="row">
                        <div class="col-sm-14">
                            <div>
                                <%--Продаю--%>
                                <c:if test="${order.operationType  eq SELL}">
                                    <div class="clearfix">
                                        <label class="submit-order-label"><loc:message code="orders.currencyforsale"/></label>
                                        <fmt:formatNumber type="number" maxFractionDigits="9"
                                                          value="${order.amountSell}"
                                                          var='amountSell'/>
                                        <input  class="submit-order-input" readonly="true"
                                               value="${amountSell}&nbsp;${currList.get(order.currencySell-1).getName()}"/>
                                    </div>
                                </c:if>
                                <%--Покупаю--%>
                                <div>
                                    <label class="submit-order-label"><loc:message code="submitorder.buy"/></label>
                                    <fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountBuy}"
                                                      var='amountBuy'/>
                                    <input class="submit-order-input" readonly="true"
                                           value="${amountBuy}&nbsp;${currList.get(order.currencyBuy-1).getName()}"/>
                                </div>
                                <%--Продаю--%>
                                <c:if test="${order.operationType  eq BUY}">
                                    <div>
                                        <label class="submit-order-label"><loc:message code="orders.currencyforsale"/></label>
                                        <fmt:formatNumber type="number" maxFractionDigits="9"
                                                          value="${order.amountSell}"
                                                          var='amountSell'/>
                                        <input class="submit-order-input" readonly="true"
                                               value="${amountSell}&nbsp;${currList.get(order.currencySell-1).getName()}"/>
                                    </div>
                                </c:if>
                                <%--Комиссия--%>
                                <div>
                                    <div>
                                        <c:set var="commissionValue" value="${order.amountBuy*commission/100}"/>
                                        <label class="submit-order-label"><loc:message code="submitorder.commission"/></label>
                                        <fmt:formatNumber type="number" maxFractionDigits="9"
                                                          value="${commissionValue}"
                                                var="cv"/>
                                        <input class="submit-order-input" readonly="true"
                                               value="${commissionValue}&nbsp;${currList.get(order.currencyBuy-1).getName()}"/>
                                    </div>

                                </div>
                                <%--Сумма с комиссией--%>
                                <div>
                                    <div>
                                        <label class="submit-order-label"><loc:message code="submitorder.sumwithcommission"/></label>
                                        <fmt:formatNumber type="number" maxFractionDigits="9"
                                                          value="${order.amountBuy-commissionValue}"
                                                          var='valueWithCommission'/>
                                        <input class="submit-order-input" readonly="true"
                                               value="${valueWithCommission}&nbsp;${currList.get(order.currencyBuy-1).getName()}"/>
                                    </div>
                                </div>
                                    <div class="submit-order-button-container">
                                    <form:form class ="submit-order-form" action="create" modelAttribute="order" method="post">
                                        <form:hidden path="amountSell" value="${order.amountSell}"/>
                                        <form:hidden path="amountBuy" value="${order.amountBuy}"/>
                                        <form:hidden path="currencySell" value="${order.currencySell}"/>
                                        <form:hidden path="currencyBuy" value="${order.currencyBuy}"/>
                                        <form:hidden path="operationType" value="${order.operationType}"/>
                                        <loc:message code="submitorder.submit" var="labelSubmit"/>
                                        <button type="submit">${labelSubmit}</button>
                                    </form:form>

                                    <form:form class ="submit-order-form" action="edit" modelAttribute="order" method="post">
                                        <form:hidden path="amountSell" value="${order.amountSell}"/>
                                        <form:hidden path="amountBuy" value="${order.amountBuy}"/>
                                        <form:hidden path="currencySell" value="${order.currencySell}"/>
                                        <form:hidden path="currencyBuy" value="${order.currencyBuy}"/>
                                        <form:hidden path="operationType" value="${order.operationType}"/>
                                        <loc:message code="submitorder.edit" var="labelEdit"/>
                                        <button type="submit">${labelEdit}</button>
                                    </form:form>

                                    <c:url value="/orders" var="url"/>
                                    <form:form class ="submit-order-form" action="${url}">
                                        <loc:message code="submitorder.cancell" var="labelCancell"></loc:message>
                                        <button type="submit">${labelCancell}</button>
                                    </form:form>

                                </div>
                            </div>
                        </div>
                        <div class="col-sm-3"></div>
                    </div>
                </div>
            </div>
</main>
<%@include file='footer_new.jsp' %>
<%----------%>
<script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>

<%----------%>

</body>
</html>

