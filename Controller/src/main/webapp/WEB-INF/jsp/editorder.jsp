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
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style-new.css'/>" rel="stylesheet">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
    <script src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>" type="text/javascript"></script>
    <%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <%----------%>

</head>


<body>

<%@include file='header_new.jsp' %>

<main class="container orders_new transaction my_orders orders">
    <%@include file='exchange_info_new.jsp' %>
    <div class="row">
        <%@include file='usermenu_new.jsp' %>

        <div class="col-sm-9 content">
            <%--ВНЕСИТЕ ИЗМЕНЕНИЯ--%>
            <h4><loc:message code="editorder.text"/></h4>
            <c:set var="SELL" value="<%=me.exrates.model.enums.OperationType.SELL%>"/>
            <c:set var="BUY" value="<%=me.exrates.model.enums.OperationType.BUY%>"/>
            <hr>

            <%--форма--%>
            <div class="row">
                <div class="col-sm-9">
                    <form:form class="form-horizontal withdraw__money" action="submit" method="post" modelAttribute="order">
                        <%--Продаю--%>
                        <c:if test="${order.operationType  eq SELL}">
                            <div>
                                <label><loc:message code="orders.currencyforsale"/></label>
                                <form:select path="currencySell">
                                    <form:options items="${currList}" itemLabel="name" itemValue="id"/>
                                </form:select>
                            </div>
                            <div class="clearfix">
                                <form:input path="amountSell" placeholder="0.0"/>
                                <label><loc:message code="orders.sum1"/></label>
                                <form:errors class="form-input-error-message" path="amountSell" style="color:red"/>
                                <span>${notEnoughMoney}</span>
                            </div>
                        </c:if>
                        <%--Покупаю--%>
                        <div>
                            <label><loc:message code="orders.currencyforbuy"/></label>
                            <form:select path="currencyBuy" class="select form-control">
                                <form:options items="${currList}" itemLabel="name" itemValue="id"/>
                            </form:select>
                        </div>
                        <div>
                            <form:input path="amountBuy" class="form-control" placeholder="0.0"/>
                            <label><loc:message code="orders.sum2"/></label>
                            <form:errors class="form-input-error-message" path="amountBuy" style="color:red"/>
                        </div>
                        <%--Продаю--%>
                        <c:if test="${order.operationType  eq BUY}">
                            <div>
                                <label><loc:message code="orders.currencyforsale"/></label>
                                <form:select path="currencySell">
                                    <form:options items="${currList}" itemLabel="name" itemValue="id"/>
                                </form:select>
                            </div>
                            <div>
                                <form:input path="amountSell" class="form-control" placeholder="0.0"/>
                                <label><loc:message code="orders.sum1"/></label>
                                <form:errors class="form-input-error-message" path="amountSell" style="color:red"/>
                                <span>${notEnoughMoney}</span>
                            </div>
                        </c:if>
                        <%--Комиссия с данной операции составит:--%>
                        <p><loc:message code="orders.yourcommission"/>:
                            <span class="fee"><fmt:formatNumber type="number" maxFractionDigits="9"
                                                                value="${commission}"/>%</span>
                        </p>
                        <%--Создать--%>
                        <form:hidden path="operationType" value="${order.operationType}"/>
                        <loc:message code="submitorder.change" var="labelSubmit"/>
                        <button type="submit">${labelSubmit}</button>
                    </form:form>
                </div>
                <div class="col-sm-3"></div>
            </div>
        </div>
    </div>
</main>
<%@include file='footer_new.jsp' %>
</body>
</html>

