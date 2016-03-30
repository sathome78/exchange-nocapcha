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
    <title><loc:message code="mywallets.title"/></title>

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

</head>


<body>

<%@include file='header_new.jsp' %>

<main class="container orders_new transaction my_orders orders .container_footer_bottom my_wallets">
    <%@include file='exchange_info_new.jsp' %>
    <div class="row">
        <%--изменить активный элемент //TODO --%>
        <%@include file='usermenu_new.jsp' %>

        <div class="col-sm-9 content">
            <%--список счетов--%>
            <c:choose>
                <c:when test="${fn:length(walletList)==0}">
                    <loc:message code="merchants.noWallet"/>
                </c:when>
                <c:otherwise>
                    <c:if test="${error!=null}">
                        <label class="alert-danger"><loc:message code="${error}"/></label>
                    </c:if>
                    <c:if test="${message!=null}">
                        <label class="alert-success"><loc:message
                                arguments="${sumCurrency},${commissionPercent},${finalAmount}"
                                code="${message}"/></label>
                    </c:if>
                    <c:forEach var="wallet" items="${walletList}">
                        <div class="block">
                                <%--RUB--%>
                            <div class="currency">${wallet.name}</div>
                                <%--Активны й баланс:   0--%>
                            <p class="margin_top">
                                <loc:message code="mywallets.abalance"/>:
                                <fmt:formatNumber type="number" maxFractionDigits="9" value="${wallet.activeBalance}"/>
                            </p>

                            <div class="buttons1">
                                    <%--Пополнить--%>
                                <form class="form" action="<c:url value="/merchants/input"/>">
                                    <loc:message code="mywallets.input" var="inputButton"/>
                                    <button type="submit">${inputButton}</button>
                                </form>
                                    <%--Вывести--%>
                                <form class="form"  action="<c:url value="/merchants/output"/>">
                                    <loc:message code="mywallets.output" var="outputButton"/>
                                    <button type="submit">${outputButton}</button>
                                </form>
                                    <%--Создать ордер--%>
                                <form class="form"  action="order/new">
                                    <loc:message code="mywallets.createorder" var="createorderButton"/>
                                    <button type="submit">${createorderButton}</button>
                                </form>
                            </div>
                            <hr>
                                <%--Зарезервировано: 100--%>
                            <p>
                                <loc:message code="mywallets.rbalance"/>:
                                <fmt:formatNumber type="number" maxFractionDigits="9"
                                                  value="${wallet.reservedBalance}"/>
                            </p>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>


        </div>
    </div>
</main>
<%@include file='footer_new.jsp' %>

<%----------%>
<script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
<%----------%>
</body>
</html>

