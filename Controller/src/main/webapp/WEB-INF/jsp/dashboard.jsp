<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<html>
<head>
    <meta charset="utf-8" />
    <!--[if lt IE 9]><script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
    <title></title>
    <meta name="keywords" content="" />
    <meta name="description" content="" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <link href="<c:url value='/client/css/bootstrap.css'/>" rel="stylesheet" type="text/css" />
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.css'/>" rel="stylesheet" type="text/css" />
    <link href="<c:url value='/client/css/style.css'/>" rel="stylesheet" type="text/css" />

    <script type="text/javascript" src="<c:url value='/client/js/jquery.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/dropdown.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/modal.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/tab.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/chosen.jquery.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/function.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/dashboard.js'/>"></script>
    <script type="text/javascript" src="<c:url value='https://www.google.com/jsapi'/>"></script>
    <script type="text/javascript">
        google.load("visualization", "1", {"packages":["corechart"]});
    </script>

</head>

<body>

<div class="wrapper market">

    <header class="header">
        <div class="container container_center">

            <!-- begin Logo block -->
            <div class="header__logo">
                <a href="/"><img src="<c:url value='/client/img/logo.png'/>" alt=""/></a>
            </div>
            <!-- end Logo block -->

            <!-- begin Right block -->
            <div class="header__flip">
                <div class="dropdown lang__select">
                    <a data-toggle="dropdown" href="#">ru</a><i class="glyphicon-chevron-down"></i>
                    <ul class="dropdown-menu">
                        <li><a href="#">ru</a></li>
                        <li><a href="#">en</a></li>
                    </ul>
                </div>
            </div>
            <!-- end Right block -->

            <!-- begin header__menu -->
            <div class="header__menu">
                <sec:authorize access="!isAuthenticated()">
                    <!-- begin header__login__form -->
                    <div class="header__login__form">
                        <c:url value="/login" var="loginUrl" />
                        <form action="${loginUrl}" method="post">
                            <input class="login__field" type="text" name="username" placeholder=<loc:message code="dashboard.loginText"/>>
                            <input class="password__field" type="password" name="password" placeholder=<loc:message code="dashboard.passwordText"/>>
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button class="btn"><loc:message code="dashboard.entrance"/></button>

                        </form>
                        <div class="header__login__form__link">
                            <a href="<c:url value="/register" />"><loc:message code="dashboard.signUp"/></a>

                            <a href="<c:url value="/forgotPassword"/>"><loc:message code="dashboard.forgotPassword"/></a>
                            <%--<a href="#"><loc:message code="dashboard.forgotPassword"/></a>--%>
                        </div>
                    </div>
                    <!-- end header__login__form -->
                </sec:authorize>

                <sec:authorize access="isAuthenticated()">
                    <div class="header__flip">
                        <a href="<c:url value="/mywallets"/>">
                                <span style="color:#eee"><loc:message code="dashboard.hello"/> <strong><sec:authentication property="principal.username" /></strong>
                                </span>
                        </a>
                        <c:url value="/logout" var="logoutUrl" />
                        <form action="${logoutUrl}" id="logoutForm" method="post">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            <button type="submit" class="btn btn-link"><loc:message code="dashboard.goOut"/></button>
                        </form>
                    </div>
                </sec:authorize>

                <!-- begin navbar main__menu -->
                <ul class="navbar main__menu">
                    <li class="navabr__item">
                        <a href="#" class="navabr__link active"><loc:message code="dashboard.general"/></a>
                    </li>
                    <li class="navabr__item">
                        <a href="<c:url value="/mywallets"/>" class="navabr__link"><loc:message code="dashboard.personalArea"/></a>
                    </li>
                </ul>
                <!-- end navbar main__menu -->

            </div>
            <!-- end header__menu -->
        </div>
    </header><!-- .header-->

    <!-- begin order__history -->


    <section id="" class="order__history">
        <div class="container container_center">
            <div class="dropdown order__history__instrument">
                <form:form action="dashboard" method="get" modelAttribute="currencyPair" name="formTest">
                    <form:select path="name" onchange="submit()" id="currencyPair-select" class="form-control" name="currencyPair-select">
                        <c:forEach var="currencyPair" items="${currencyPairs}">
                            <form:option type="submit" id="currencyPair" value="${currencyPair.getName()}">${currencyPair.getName()}</form:option>
                        </c:forEach>
                    </form:select>
                </form:form>
            </div>
            <ul class="order__history__item">
                <li><span><loc:message code="dashboard.lastOrder"/></span>
                    <fmt:formatNumber type="number" maxFractionDigits="9" value="${lastOrder.getAmountBuy()}"/>
                    ${lastOrderCurrency}</li>
                <li><span><loc:message code="dashboard.priceStart"/></span>
                    <fmt:formatNumber type="number" maxFractionDigits="9" value="${lastOrder.getAmountBuy()}"/>
                    ${lastOrderCurrency}</span></li>
                <li><span><loc:message code="dashboard.priceEnd"/></span>
                    <fmt:formatNumber type="number" maxFractionDigits="9" value="${lastOrder.getAmountBuy()}"/>
                    ${lastOrderCurrency}</span></li>
                <li><span><loc:message code="dashboard.volume"/></span>
                    <fmt:formatNumber type="number" maxFractionDigits="9" value="${sumAmountBuyClosed}"/>
                    ${currencyPair.getCurrency1().getName()}</span></li>
                <li><span>
                    <fmt:formatNumber type="number" maxFractionDigits="9" value="${sumAmountSellClosed}"/>
                    ${currencyPair.getCurrency2().getName()}</span></li>
            </ul>
        </div>
    </section>
    <!-- end order__history -->

    <!-- begin quotes__news__section -->
    <section class="quotes__news__section">
        <div class="container container_center">

            <!-- begin chart__section -->
            <div class="chart__section">
                <div class="chart__section__title"><a id="chartPair"></a> </div>
                    <span style="color:red">${msg}</span><br><br>
                    <c:if test="${not empty sumAmountBuyClosed}">
                        <div id='chart_div'></div>
                    </c:if>
            </div>
            <!-- end chart__section -->

        </div>
    </section>
    <!-- end quotes__news__section -->

    <main class="main__content">

        <div class="container container_center">

            <!-- Start  buy__sell__btc -->
            <div class="buy__sell__btc">

                <form:form action="order/submit" method="post" modelAttribute="order" name="formBuy">

                    <div class="form-horizontal col-sm-6">
                        <div class="buy__sell__btc__title"><loc:message code="dashboard.BUY"/> ${currencyPair.getCurrency1().getName()}</div>
                        <div class="buy__sell__btc__thead">
                            <div class="col-sm-4"><loc:message code="dashboard.yourBalance"/><br>
                                    <%--${balanceCurrency1}--%>
                                <fmt:formatNumber type="number" maxFractionDigits="9" value="${balanceCurrency1}"/>
                                    ${currencyPair.getCurrency2().getName()}
                            </div>
                            <div class="col-sm-8"><loc:message code="dashboard.lowestPrice"/><br>
                                    <%--${minPrice}--%>
                                <fmt:formatNumber type="number" maxFractionDigits="9" value="${minPrice}"/>
                                    ${currencyPair.getCurrency2().getName()}
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label" for="#"><loc:message code="dashboard.amount"/> ${currencyPair.getCurrency1().getName()}:</label>
                            <div class="col-sm-8">
                                    <%--<input class="form-control" type="text" value="0">--%>
                                <form:errors path="amountBuy" style="color:red" />
                                <form:input path="amountBuy" class="form-control" id="amountBuyForm1" placeholder="0"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label" for="#"><loc:message code="dashboard.priceFor"/> ${currencyPair.getCurrency1().getName()}:</label>
                            <div class="col-sm-8 btc__currency">
                                    <%--<input class="form-control" type="text" value="0">--%>
                                <form:errors path="amountSell" style="color:red" />
                                <input class="form-control" id="amountSellForm1" placeholder="0"/>
                                <form:input type="hidden" path="amountSell" class="form-control" id="sumSellForm1" placeholder="0"/>

                                <i>${currencyPair.getCurrency2().getName()}</i>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label" for="#"><loc:message code="dashboard.total"/></label>
                            <div class="col-sm-8 btc__currency">
                                <div class="item-form">
                                    <b id="sumBuyWithCommission"></b> ${currencyPair.getCurrency2().getName()}
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label" for="#"><loc:message code="dashboard.fee"/></label>
                            <div class="col-sm-8 btc__currency">
                                <div class="item-form">
                                    <b id="buyCommission"></b> ${currencyPair.getCurrency1().getName()}
                                </div>

                            </div>
                        </div>
                        <div class="form-group">
                            <div class="buy__sell__btn">
                                <button type="button" class="btn btn-primary" name="calculateBuy"><loc:message code="dashboard.calculate"/></button>
                                <c:set var="BUY" value="<%=me.exrates.model.enums.OperationType.BUY%>"/>
                                <form:hidden path="operationType" value= "${BUY}" />
                                <form:hidden path="currencySell" value= "${currencyPair.getCurrency2().getId()}" />
                                <form:hidden path="currencyBuy" value= "${currencyPair.getCurrency1().getId()}" />
                                <button class="btn btn-buy" type="submit"><loc:message code="dashboard.buy"/> ${currencyPair.getCurrency1().getName()}</button>

                            </div>
                        </div>
                    </div>
                </form:form>

                <form:form action="order/submit" method="post" modelAttribute="order">
                    <div class="form-horizontal col-sm-6">
                        <div class="buy__sell__btc__title"><loc:message code="dashboard.SELL"/> ${currencyPair.getCurrency1().getName()}</div>
                        <div class="buy__sell__btc__thead">
                            <div class="col-sm-4"><loc:message code="dashboard.yourBalance"/> <br>
                                    <%--${balanceCurrency2}--%>
                                <fmt:formatNumber type="number" maxFractionDigits="9" value="${balanceCurrency2}"/>
                                    ${currencyPair.getCurrency1().getName()}
                            </div>
                            <div class="col-sm-8"><loc:message code="dashboard.highestPrice"/> <br>
                                    <%--${maxPrice} --%>
                                <fmt:formatNumber type="number" maxFractionDigits="9" value="${maxPrice}"/>
                                    ${currencyPair.getCurrency2().getName()}
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label" for="#"><loc:message code="dashboard.amount"/> ${currencyPair.getCurrency1().getName()}:</label>
                            <div class="col-sm-8">
                                <form:errors path="amountSell" style="color:red" />
                                <form:input path="amountSell" class="form-control" id="amountSellForm2" placeholder="0"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label" for="#"><loc:message code="dashboard.priceFor"/> ${currencyPair.getCurrency1().getName()}:</label>
                            <div class="col-sm-8 btc__currency">
                                <%--<form:errors path="amountBuy" style="color:red" />--%>
                                <input class="form-control" id="amountBuyForm2" placeholder="0"/>
                                <form:input type="hidden" path="amountBuy" class="form-control" id="sumBuyForm2" placeholder="0"/>
                                <i>${currencyPair.getCurrency2().getName()}</i>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label" for="#"><loc:message code="dashboard.total"/></label>
                            <div class="col-sm-8 btc__currency">
                                <div class="item-form">
                                    <b id="sumSellWithCommission"></b> ${currencyPair.getCurrency2().getName()}
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-4 control-label" for="#"><loc:message code="dashboard.fee"/></label>
                            <div class="col-sm-8 btc__currency">
                                <div class="item-form">
                                    <b id="sellCommission"></b> ${currencyPair.getCurrency2().getName()}
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="buy__sell__btn">
                                <button type="button" name="calculateSell" class="btn btn-primary"><loc:message code="dashboard.calculate"/></button>
                                <c:set var="SELL" value="<%=me.exrates.model.enums.OperationType.SELL%>"/>
                                <form:hidden path="operationType" value= "${SELL}" />
                                <form:hidden path="currencySell" value= "${currencyPair.getCurrency1().getId()}" />
                                <form:hidden path="currencyBuy" value= "${currencyPair.getCurrency2().getId()}" />
                                <button class="btn btn-sell" type="submit" ><loc:message code="dashboard.sell"/> ${currencyPair.getCurrency1().getName()}</button>
                            </div>
                        </div>
                    </div>
                </form:form>

                <!-- End  buy__sell__btc -->

                <!-- begin btc__orders -->
                <div class="btc__orders">
                    <div class="col-sm-6">
                        <div class="btc__orders__title"><loc:message code="dashboard.buyOrders"/></div>
                        <div class="btc__orders__ammount">Всего:
                            <%--${sumAmountBuy} --%>
                            <fmt:formatNumber type="number" maxFractionDigits="9" value="${sumAmountBuy}"/>
                            ${currencyPair.getCurrency1().getName()}
                        </div>
                        <div class="btc__orders__table__wrapper custom__scrollbar">
                            <table class="table">
                                <thead>
                                <tr>
                                    <th>Цена</th>
                                    <th>${currencyPair.getCurrency1().getName()}</th>
                                    <th>${currencyPair.getCurrency2().getName()}</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="order" items="${ordersBuy}">
                                    <tr>
                                        <td>
                                                <%--${order.amountSell/order.amountBuy}--%>
                                            <fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountSell/order.amountBuy}"/>
                                        </td>
                                        <td>
                                                <%--${order.amountBuy}--%>
                                            <fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountBuy}"/>
                                        </td>
                                        <td>
                                                <%--${order.amountSell}--%>
                                            <fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountSell}"/>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="col-sm-6">
                        <div class="btc__orders__title"><loc:message code="dashboard.sellOrders"/></div>
                        <div class="btc__orders__ammount">Всего:
                            <%--${sumAmountSell} --%>
                            <fmt:formatNumber type="number" maxFractionDigits="9" value="${sumAmountSell}"/>
                            ${currencyPair.getCurrency2().getName()}
                        </div>
                        <div class="btc__orders__table__wrapper custom__scrollbar">
                            <table class="table">
                                <thead>
                                <tr>
                                    <th>Цена</th>
                                    <th>${currencyPair.getCurrency1().getName()}</th>
                                    <th>${currencyPair.getCurrency2().getName()}</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="order" items="${ordersSell}">
                                    <tr>
                                        <td>
                                                <%--${order.amountBuy/order.amountSell}--%>
                                            <fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountBuy/order.amountSell}"/>
                                        </td>
                                        <td>
                                                <%--${order.amountSell}--%>
                                            <fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountSell}"/>
                                        </td>
                                        <td>
                                                <%--${order.amountBuy}--%>
                                            <fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountBuy}"/>
                                        </td>
                                    </tr>
                                </c:forEach>

                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <!-- end btc__orders -->

            </div>

        </div>
    </main>


</div>

<jsp:include page="footer_dashboard.jsp"/>

</body>
</html>