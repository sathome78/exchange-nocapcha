<%--<%@ page contentType="text/html; charset=UTF-8" language="java" %>--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<div class="exchange_info"> <!-- Exchange info -->
    <ul>
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

        <%--элемент отсутсвует в новом интерфейсе  //TODO --%>
        <%--отключен до выяснения функциональности--%>
        <%--<li class="reveal">
            <a href="#">
                BTC/USD <span class="caret"></span>
            </a>
            &lt;%&ndash;
            id="currency" нельзя использовать - конфликтует с main.js (ловит его по id вместо нужного)
            если включать <li hidden class="reveal">, то продумать замену <ul class="" id="currency">
            &ndash;%&gt;
            <ul class="" id="curr-ency">
                <li><a href="#">BTC/USD</a></li>
                <li><a href="#">BTC/USD</a></li>
            </ul>
        </li>--%>
        <%-- ... непонятный элемент - в старой форме отсутствует  --%>
    </ul>
</div>