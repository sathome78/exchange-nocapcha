<%--<%@ page contentType="text/html; charset=UTF-8" language="java" %>--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<div class="exchange_info"> <!-- Exchange info -->
    <ul>
        <li id="lastOrder">
            <span>
                <loc:message code="dashboard.lastOrder"/>
            </span>
            <span>
                <fmt:formatNumber type="number" maxFractionDigits="9" value="${lastOrder.getAmountBuy()}"/>
                ${lastOrderCurrency}
            </span>
        </li>
        <li id="priceStart">
            <span>
                <loc:message code="dashboard.priceStart"/>
            </span>
            <span>
                <fmt:formatNumber type="number" maxFractionDigits="9" value="${lastOrder.getAmountBuy()}"/>
                ${lastOrderCurrency}
            </span>
        </li>
        <li id="priceEnd">
            <span>
                <loc:message code="dashboard.priceEnd"/>
            </span>
            <span>
                <fmt:formatNumber type="number" maxFractionDigits="9" value="${lastOrder.getAmountBuy()}"/>
                ${lastOrderCurrency}
            </span>
        </li>
        <li id="sumAmountBuyClosed">
            <span>
                <loc:message code="dashboard.volume"/>
            </span>
            <span>
                <fmt:formatNumber type="number" maxFractionDigits="9" value="${sumAmountBuyClosed}"/>
                ${currencyPair.getCurrency1().getName()}
            </span>
        </li>
        <li id="sumAmountSellClosed">
            <span></span>
            <span>
                <fmt:formatNumber type="number" maxFractionDigits="9" value="${sumAmountSellClosed}"/>
                ${currencyPair.getCurrency2().getName()}
            </span></li>

        <li id="pair-selector">
            <div>${currencyPair.getName()}</div>
            <span class="caret"></span>
            <div class="pair-selector__menu">
                <c:forEach var="curr" items="${currencyPairs}">
                    <c:choose>
                        <c:when test="${curr.getName()==currencyPair.getName()}">
                            <div class="pair-selector__menu-item active" selected>${curr.getName()}
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="pair-selector__menu-item">${curr.getName()}</div>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </div>
        </li>
        <div id="pair-selector-arrow"></div>
    </ul>
</div>