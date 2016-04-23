<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>Exrates</title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="interkassa-verification" content="c4deb5425361141d96dd48d235b6fc4a" />
    <link href='https://fonts.googleapis.com/css?family=Roboto:400,700,300' rel='stylesheet' type='text/css'>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
    <script src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>" type="text/javascript"></script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style-new.css'/>" rel="stylesheet">

    <script type="text/javascript" src="<c:url value='/client/js/dashboard.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/chart/areaChart.js'/>"></script>
    <%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/notyInit.js'/>"></script>
    <%----------%>
    <!-- Google Charts ... -->
    <script type="text/javascript" src="<c:url value='https://www.gstatic.com/charts/loader.js'/>"></script>
    <script type="text/javascript">
        google.charts.load('current', {'packages': ['corechart']});
    </script>
    <!-- ... Google Charts -->
    <!-- Google Analytics ... -->
    <script async src='//www.google-analytics.com/analytics.js'></script>
    <script>
        window.ga = window.ga || function () {
                    (ga.q = ga.q || []).push(arguments)
                };
        ga.l = +new Date;
        ga('create', 'UA-75711135-1', 'auto');
        ga('send', 'pageview');
    </script>
    <!-- ... Google Analytics -->
    <%--Chat--%>
    <script type="text/javascript">
        window.$zopim || (function (d, s) {
            var z = $zopim = function (c) {
                z._.push(c)
            }, $ = z.s =
                    d.createElement(s), e = d.getElementsByTagName(s)[0];
            z.set = function (o) {
                z.set.
                        _.push(o)
            };
            z._ = [];
            z.set._ = [];
            $.async = !0;
            $.setAttribute("charset", "utf-8");
            $.src = "//v2.zopim.com/?3n4rzwKe0WvQGt1TDMpL8gvMRIUvgCjX";
            z.t = +new Date;
            $.
                    type = "text/javascript";
            e.parentNode.insertBefore($, e)
        })(document, "script");
    </script>

</head>
<body>

<%@include file="header_new.jsp" %>
</div>

<main class="container">
    <div class="exchange_data"> <!-- Exchange currencies and graphic -->
        <ul class="large-pair-selector exchange">
            <c:forEach var="curr" items="${currencyPairs}" begin="0" end="3">
                <c:choose>
                    <c:when test="${curr.getName()==currencyPair.getName()}">
                        <li class="exchange__pair active" selected><a class="active"
                                                                      href="?name=${curr.getName()}">${curr.getName()}</a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="exchange__pair"><a
                                href="?name=${curr.getName()}">${curr.getName()}</a></li>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
            <li id="other_pairs"><a href="#"><loc:message code="dashboard.otherpairs"/> <span class="caret"></span></a>
                <ul>
                    <c:forEach var="curr" items="${currencyPairs}" begin="4">
                        <c:choose>
                            <c:when test="${curr.getName()==currencyPair.getName()}">
                                <li class="exchange__pair active" selected><a class="active"
                                                                              href="?name=${curr.getName()}">${curr.getName()}</a>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <li class="exchange__pair"><a
                                        href="?name=${curr.getName()}">${curr.getName()}</a></li>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </ul>
            </li>
        </ul>
        <div class="graphic"> <!-- graphic -->
            <c:if test="${not empty sumAmountBuyClosed}">
                <div id='chart_div'></div>
            </c:if>
            <img id='graphic' src="/client/img/graphic.png" alt="Graphic">
        </div>
    </div>

    <%@include file='exchange_info_new.jsp' %>

    <div class="buy_sell row"> <!-- BUY or SELL BTC -->
        <div class="buy col-sm-4">
            <%--купить--%>
            <h3><loc:message code="dashboard.BUY"/> ${currencyPair.getCurrency1().getName()}</h3>
            <hr class="display_at_small_width">
            <div class="row add_margin">
                <%--ваши средства--%>
                <div class="col-xs-6"><loc:message code="dashboard.yourBalance"/><br>
                    <fmt:formatNumber type="number" maxFractionDigits="9" value="${balanceCurrency1}"/>
                    ${currencyPair.getCurrency1().getName()}
                </div>
                <%--мин цена--%>
                <div class="col-xs-6"><loc:message code="dashboard.lowestPrice"/><br>
                    <%--${minPrice}--%>
                    <fmt:formatNumber type="number" maxFractionDigits="9" value="${minPrice}"/>
                    ${currencyPair.getCurrency2().getName()}
                </div>
            </div>
            <%--<form:form action="order/submit" method="post" modelAttribute="order" name="formBuy">--%>
            <form:form action="/orders" method="post" modelAttribute="exOrder" name="formBuy">
                <%--количество--%>
                <label class="col1"><loc:message
                        code="dashboard.amount"/> ${currencyPair.getCurrency1().getName()}:</label>
                <form:errors path="amountBase" style="color:red"/>
                <form:input class="col2 numericInputField" path="amountBase" type="text" id="amountBuyForm1" placeholder="0"/>
                <%--цена за--%>
                <label class="col1"><loc:message
                        code="dashboard.priceFor"/> ${currencyPair.getCurrency1().getName()}:</label>
                <form:input path="exRate" type="text" class="col2 numericInputField" id="amountSellForm1" placeholder="0"/>
                <%--всего--%>
                <span class="col1"><loc:message code="dashboard.total"/></span>
                <span class="col2"><b id="sumBuyWithCommission"></b> ${currencyPair.getCurrency2().getName()}</span>
                <%--комисия--%>
                <span class="col1"><loc:message code="dashboard.fee"/></span>
                <span class="col2"><b id="buyCommission"></b> ${currencyPair.getCurrency2().getName()}</span>

                <div class="row">
                    <div class="col-xs-6">
                            <%--Подсчитать--%>
                        <button class="calculate" type="button" name="calculateBuy"><loc:message
                                code="dashboard.calculate"/></button>
                    </div>
                    <c:set var="BUY" value="<%=me.exrates.model.enums.OperationType.BUY%>"/>
                    <form:hidden path="operationType" value="${BUY}"/>
                    <div class="col-xs-6">
                            <%--Купить--%>
                        <button class="buy" type="submit"><loc:message
                                code="dashboard.buy"/> ${currencyPair.getCurrency1().getName()}</button>
                    </div>
                </div>
            </form:form>
        </div>
        <!-- End BUY BTC-->

        <div class="col-sm-4 big_logo"></div>

        <div class="sell col-sm-4">
            <%--Продать--%>
            <h3><loc:message code="dashboard.SELL"/> ${currencyPair.getCurrency1().getName()}</h3>
            <hr class="display_at_small_width">
            <div class="row add_margin">
                <%--Ваши средства--%>
                <div class="col-xs-6"><loc:message code="dashboard.yourBalance"/> <br>
                    <fmt:formatNumber type="number" maxFractionDigits="9" value="${balanceCurrency2}"/>
                    ${currencyPair.getCurrency2().getName()}
                </div>
                <%--Мин. Цена--%>
                <div class="col-xs-6"><loc:message code="dashboard.highestPrice"/> <br>
                    <%--${maxPrice} --%>
                    <fmt:formatNumber type="number" maxFractionDigits="9" value="${maxPrice}"/>
                    ${currencyPair.getCurrency2().getName()}
                </div>
            </div>
            <%--<form:form action="order/submit" method="post" modelAttribute="order">--%>
            <form:form action="/orders" method="post" modelAttribute="exOrder">
                <%--Количество--%>
                <form:errors path="amountBase" style="color:red"/>
                <form:input type="text" class="col2 numericInputField" path="amountBase" id="amountSellForm2" placeholder="0.0"/>
                <label class="col1"><loc:message
                        code="dashboard.amount"/> ${currencyPair.getCurrency1().getName()}:</label>
                <%--Цена за--%>
                <form:input type="text" path="exRate" class="col2 numericInputField" id="amountBuyForm2" placeholder="0"/>
                <label class="col1"><loc:message
                        code="dashboard.priceFor"/> ${currencyPair.getCurrency1().getName()}:</label>
                <%--ВСЕГО--%>
                <span class="col1"><loc:message code="dashboard.total"/></span>
                    <span class="col2"><b
                            id="sumSellWithCommission"></b> ${currencyPair.getCurrency2().getName()}</span>
                <%--Комиссия--%>
                <span class="col1"><loc:message code="dashboard.fee"/></span>
                <span class="col2"><b id="sellCommission"></b> ${currencyPair.getCurrency2().getName()}</span>

                <div class="row">
                    <div class="col-xs-6">
                            <%--Подсчитать--%>
                        <button class="calculate" type="button" name="calculateSell"><loc:message
                                code="dashboard.calculate"/></button>
                    </div>
                    <c:set var="SELL" value="<%=me.exrates.model.enums.OperationType.SELL%>"/>
                    <form:hidden path="operationType" value="${SELL}"/>
                    <div class="col-xs-6">
                            <%--Купить--%>
                        <button class="buy" type="submit"><loc:message
                                code="dashboard.sell"/> ${currencyPair.getCurrency1().getName()}</button>
                    </div>
                </div>
            </form:form>
        </div>
        <!-- End SELL BTC -->
    </div>
    <!-- End BUY or SELL BTC -->

    <div class="row margin_top orders"> <!-- ORDERS -->
        <div class="col-sm-4">
            <%--ОРДЕРА НА ПРОДАЖУ--%>
            <h3 class=""><loc:message code="dashboard.buyOrders"/></h3>
            <hr class="display_at_small_width">
            <%--Всего--%>
            <p>
                <loc:message code="dashboard.total"/>
                <fmt:formatNumber type="number" maxFractionDigits="9" value="${sumAmountSell}"/>
                ${currencyPair.getCurrency1().getName()}
            </p>
        </div>
        <div class="col-sm-4"></div>
        <div class="col-sm-4 align-right">
            <%--ОРДЕРА НА ПОКУПКУ--%>
            <h3 class=""><loc:message code="dashboard.sellOrders"/></h3>
            <hr class="display_at_small_width">
            <p>
                <loc:message code="dashboard.total"/>
                <fmt:formatNumber type="number" maxFractionDigits="9" value="${sumAmountBuy}"/>
                ${currencyPair.getCurrency1().getName()}
            </p>
        </div>
    </div>
    <!-- end Orders -->
    <div class="row"> <!-- Tables -->
        <div class="col-xs-6 table1 mCustomScrollbar" data-mcs-theme="dark">
            <table>
                <tr>
                    <th><loc:message code="dashboard.orderPrice"/></th>
                    <th>${currencyPair.getCurrency1().getName()}</th>
                    <th>${currencyPair.getCurrency2().getName()}</th>
                </tr>
                <c:forEach var="order" items="${ordersSell}">
                    <tr>
                        <td>
                            <fmt:formatNumber type="number" maxFractionDigits="9"
                                              value="${order.getExrate()}"/>
                        </td>
                        <td>
                            <fmt:formatNumber type="number" maxFractionDigits="9" value="${order.getAmountBase()}"/>
                        </td>
                        <td>
                            <fmt:formatNumber type="number" maxFractionDigits="9" value="${order.getAmountConvert()}"/>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
        <div class="col-xs-6 table1 mCustomScrollbar" data-mcs-theme="dark">
            <table>
                <tr>
                    <th><loc:message code="dashboard.orderPrice"/></th>
                    <th>${currencyPair.getCurrency1().getName()}</th>
                    <th>${currencyPair.getCurrency2().getName()}</th>
                </tr>
                <c:forEach var="order" items="${ordersBuy}">
                    <tr>
                        <td>
                            <fmt:formatNumber type="number" maxFractionDigits="9"
                                              value="${order.getExrate()}"/>
                        </td>
                        <td>
                            <fmt:formatNumber type="number" maxFractionDigits="9" value="${order.getAmountBase()}"/>
                        </td>
                        <td>
                            <fmt:formatNumber type="number" maxFractionDigits="9" value="${order.getAmountConvert()}"/>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
    <!-- end Tables -->
</main>

<%@include file='footer_new.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>

<script>
    (function (i, s, o, g, r, a, m) {
        i['GoogleAnalyticsObject'] = r;
        i[r] = i[r] || function () {
                    (i[r].q = i[r].q || []).push(arguments)
                }, i[r].l = 1 * new Date();
        a = s.createElement(o),
                m = s.getElementsByTagName(o)[0];
        a.async = 1;
        a.src = g;
        m.parentNode.insertBefore(a, m)
    })(window, document, 'script', '//www.google-analytics.com/analytics.js', 'ga');

    ga('create', 'UA-75711135-1', 'auto');
    ga('send', 'pageview');
</script>

</body>
</html>
