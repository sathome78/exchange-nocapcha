<%--
  Created by IntelliJ IDEA.
  User: Valk
  Date: 13.04.16
  Time: 23:05
  To change this template use File | Settings | File Templates.
--%>
<form:form id="createBuyOrderForm" action="${submitUrl}"
           method="post"
           modelAttribute="orderCreateDto">
    <h4>
        <loc:message code="orders.currencybuy"/>&nbsp;
            ${orderCreateDto.currencyPair.getCurrency1().getName()}&nbsp;
        <loc:message code="orders.for"/>&nbsp;
            ${orderCreateDto.currencyPair.getCurrency2().getName()}
    </h4>

    <c:set var="pattern" value="\d+(.\d)*"/>

    <form:input hidden="true" path="currencyPair" value="${orderCreateDto.currencyPair}"/>
    <form:input hidden="true" path="walletIdCurrency1" value="${orderCreateDto.walletIdCurrency1}"/>
    <form:input hidden="true" path="walletIdCurrency2" value="${orderCreateDto.walletIdCurrency2}"/>

    <%--Active balance--%>
    <div class="input-block-wrapper">
        <div class="col-md-5 input-block-wrapper__label-wrapper">
            <label class="input-block-wrapper__label"><loc:message
                    code="mywallets.abalance"/></label>
        </div>
        <div class="col-md-7 input-block-wrapper__input-wrapper">
            <fmt:formatNumber type="number" maxFractionDigits="9"
                              value="${orderCreateDto.balance2}" var="balance2" groupingUsed="false"/>
            <form:input id="balance2" path="balance2" class="input-block-wrapper__input" value="${balance2}"
                        readonly="true"/>
            <div class="input-block-wrapper__inner-label">${orderCreateDto.currencyPair.getCurrency2().getName()}</div>
        </div>
    </div>
    <%--amount--%>
    <div class="input-block-wrapper">
        <div class="col-md-5 input-block-wrapper__label-wrapper">
            <label class="input-block-wrapper__label"><loc:message code="orders.amountbuy"/></label>
        </div>
        <div class="col-md-7 input-block-wrapper__input-wrapper">
            <form:input id="amountBuy" pattern="${pattern}" path="amount" class="input-block-wrapper__input"
                        placeholder="0.0"
                        readonly="${disableEdit}"/>
            <div class="input-block-wrapper__inner-label">${orderCreateDto.currencyPair.getCurrency1().getName()}</div>
        </div>
        <div class="col-md-7 input-block-wrapper__error-wrapper">
            <form:errors path="amount" class="input-block-wrapper__input"/>
        </div>
    </div>
    <%--rate--%>
    <div class="input-block-wrapper">
        <div class="col-md-5 input-block-wrapper__label-wrapper">
            <label class="input-block-wrapper__label"><loc:message code="order.rate"/></label>
        </div>
        <div class="col-md-7 input-block-wrapper__input-wrapper">
            <form:input id="exchangeRateBuy" pattern="${pattern}" path="exchangeRate" class="input-block-wrapper__input"
                        placeholder="0.0"
                        readonly="${disableEdit}"/>

            <div class="input-block-wrapper__inner-label">${orderCreateDto.currencyPair.getCurrency2().getName()}</div>
        </div>
        <div class="col-md-7 input-block-wrapper__error-wrapper">
            <form:errors path="exchangeRate" class="input-block-wrapper__input"/>
        </div>
    </div>
    <%--total--%>
    <div class="input-block-wrapper">
        <div class="col-md-5 input-block-wrapper__label-wrapper">
            <label class="input-block-wrapper__label"><loc:message code="order.total"/></label>
        </div>
        <div class="col-md-7 input-block-wrapper__input-wrapper">
            <form:input id="totalForBuy" path="total" class="input-block-wrapper__input" readonly="true"/>

            <div class="input-block-wrapper__inner-label">${orderCreateDto.currencyPair.getCurrency2().getName()}</div>
        </div>
    </div>
    <%--comission--%>
    <div class="input-block-wrapper">
        <div class="col-md-5 input-block-wrapper__label-wrapper">
            <label class="input-block-wrapper__label"><loc:message code="order.commission"/></label>
        </div>
        <div class="col-md-7 input-block-wrapper__input-wrapper">
            <form:input hidden="true" id="comissionForBuy" path="comissionForBuy"
                        value="${orderCreateDto.comissionForBuy}"/>
            <form:input hidden="true" path="comissionForSell"
                        value="${orderCreateDto.comissionForSell}"/>
            <form:input id="calculatedComissionForBuy" path="comission" class="input-block-wrapper__input"
                        readonly="true"/>

            <div class="input-block-wrapper__inner-label">${orderCreateDto.currencyPair.getCurrency2().getName()}</div>
        </div>
    </div>
    <%--total with comission--%>
    <div class="input-block-wrapper">
        <div class="col-md-5 input-block-wrapper__label-wrapper">
            <label class="input-block-wrapper__label"><loc:message
                    code="order.amountwithcommission"/></label>
        </div>
        <div class="col-md-7 input-block-wrapper__input-wrapper">
            <form:input id="totalWithComissionForBuy" path="totalWithComission" class="input-block-wrapper__input"
                        readonly="true"/>

            <div class="input-block-wrapper__inner-label">${orderCreateDto.currencyPair.getCurrency2().getName()}</div>
        </div>
    </div>
    <c:set var="BUY" value="<%=me.exrates.model.enums.OperationType.BUY%>"/>
    <form:hidden path="operationType" value="${BUY}"/>
</form:form>
<hr/>


<div class="formButtonWrapper">
    <%--Create--%>
    <div id="submitOrderBuySellWrapper">
        <c:if test="${submitUrl != null}">
            <loc:message code="orders.submit" var="labelSubmit"/>
            <button id="submitOrderBuy" onclick="$('#createBuyOrderForm').submit()"
                    type="submit">${labelSubmit}</button>
        </c:if>
        <c:if test="${submitUrl == null}">
            <loc:message code="orders.submit" var="labelSubmit"/>
            <button id="submitOrderBuy" onclick="finPassCheck(0, submitCreateOrder, 'BUY', event)"
                    type="submit">${labelSubmit}</button>
        </c:if>
    </div>
    <%--Edit--%>
    <c:if test="${editUrl != null}">
        <form:form id="editOrderBuySellWrapper" action="${editUrl}" modelAttribute="orderCreateDto" method="post">
            <form:input hidden="true" path="currencyPair" value="${orderCreateDto.currencyPair}"/>
            <form:input hidden="true" path="walletIdCurrency1" value="${orderCreateDto.walletIdCurrency1}"/>
            <form:input hidden="true" path="walletIdCurrency2" value="${orderCreateDto.walletIdCurrency2}"/>
            <form:input hidden="true" path="balance1" value="${orderCreateDto.balance1}"/>
            <form:input hidden="true" path="balance2" value="${orderCreateDto.balance2}"/>
            <form:input hidden="true" path="comissionForBuy" value="${orderCreateDto.comissionForBuy}"/>
            <form:input hidden="true" path="comissionForSell" value="${orderCreateDto.comissionForSell}"/>
            <form:input hidden="true" path="operationType" value="${orderCreateDto.operationType}"/>
            <form:input hidden="true" path="exchangeRate" value="${orderCreateDto.exchangeRate}"/>
            <form:input hidden="true" path="amount" value="${orderCreateDto.amount}"/>
            <form:input hidden="true" path="total" value="${orderCreateDto.total}"/>
            <form:input hidden="true" path="comission" value="${orderCreateDto.comission}"/>
            <form:input hidden="totalWithComission" path="comission" value="${orderCreateDto.totalWithComission}"/>
            <loc:message code="submitorder.edit" var="labelEdit"/>
            <button type="submit" id="editOrderBuySell" type="button">${labelEdit}</button>
        </form:form>
    </c:if>
    <%--Cancel--%>
    <c:if test="${cancelUrl != null}">
        <div id="cancelOrderBuySellWrapper">
            <loc:message code="submitorder.cancell" var="labelCancell"></loc:message>
            <button id="cancelOrderBuySell" type="button"
                    onclick="window.location.href='${cancelUrl}'">${labelCancell}</button>
        </div>
    </c:if>
</div>



