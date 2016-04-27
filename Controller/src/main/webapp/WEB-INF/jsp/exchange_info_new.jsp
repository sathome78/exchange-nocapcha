<%--<%@ page contentType="text/html; charset=UTF-8" language="java" %>--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<div class="exchange_info"> <!-- Exchange info -->
    <ul>
        <li id="lastOrderAmountBase">
            <span>
                <loc:message code="dashboard.lastOrder"/>
            </span>
            <span>0.0</span>
        </li>
        <li id="firstOrderRate">
            <span>
                <loc:message code="dashboard.priceStart"/>
            </span>
            <span>0.0</span>
        </li>
        <li id="lastOrderRate">
            <span>
                <loc:message code="dashboard.priceEnd"/>
            </span>
            <span>0.0</span>
        </li>
        <li id="sumBase">
            <span>
                <loc:message code="dashboard.volume"/>
            </span>
            <span>0.0</span>
        </li>
        <li id="sumConvert">
            <span></span>
            <span>0.0</span>
        </li>

        <li id="pair-selector">
            <div>${currencyPair.getName()}</div>
            <span class="caret"></span>
            <div class="pair-selector__menu">
                <%--items created in createPairSelectorMenu()--%>
            </div>
        </li>
        <div id="pair-selector-arrow"></div>
    </ul>
</div>