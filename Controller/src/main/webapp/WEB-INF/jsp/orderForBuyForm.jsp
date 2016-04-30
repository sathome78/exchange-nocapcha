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

    <form:hidden path="currencyPair"/>
    <form:hidden path="walletIdCurrencyBase"/>
    <form:hidden path="walletIdCurrencyConvert"/>
    <form:hidden path="orderId"/>
    <form:hidden path="userId"/>
    <form:hidden path="status"/>
    <%----%>
    <c:set var="BUY" value="<%=me.exrates.model.enums.OperationType.BUY%>"/>
    <c:set var="amount" value="0"/>
    <c:set var="rate" value="0"/>
    <%--operationType is set if we transfered to form the orders from dashbourd.
    It's necessary operationType to be set, to correct fill one of two forms (BUY or SELL) by values from page the dashboard  --%>
    <c:if test="${orderCreateDto.operationType==BUY}">
        <c:set var="amount" value="${orderCreateDto.amount}"/>
        <c:set var="rate" value="${orderCreateDto.exchangeRate}"/>
    </c:if>
    <form:hidden path="operationType" value="${BUY}"/>
    <%--Active balance--%>
    <c:if test="${hideBalance == null && hideBalance != 'true'}">
        <div class="input-block-wrapper">
            <div class="col-md-5 input-block-wrapper__label-wrapper">
                <label class="input-block-wrapper__label"><loc:message
                        code="mywallets.abalance"/></label>
            </div>
            <div class="col-md-7 input-block-wrapper__input-wrapper">
                <form:input id="currencyConvertBalance" path="currencyConvertBalance" class="input-block-wrapper__input"
                            readonly="true"/>
                <div class="input-block-wrapper__inner-label">${orderCreateDto.currencyPair.getCurrency2().getName()}</div>
            </div>
        </div>
    </c:if>
    <%--amount--%>
    <div class="input-block-wrapper">
        <div class="col-md-5 input-block-wrapper__label-wrapper">
            <label class="input-block-wrapper__label"><loc:message code="orders.amountbuy"/></label>
        </div>
        <div class="col-md-7 input-block-wrapper__input-wrapper">
            <form:input id="amountBuy" path="amount" class="input-block-wrapper__input numericInputField"
                        value="${amount}"
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
            <form:input id="exchangeRateBuy" path="exchangeRate" class="input-block-wrapper__input numericInputField"
                        value="${rate}"
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
            <form:hidden path="comissionId"/>
            <form:hidden id="comissionForBuyId" path="comissionForBuyId"/>
            <form:hidden id="comissionForBuyRate" path="comissionForBuyRate"/>
            <form:hidden path="comissionForSellId"/>
            <form:hidden path="comissionForSellRate"/>
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
</form:form>
<hr/>


<div class="formButtonWrapper">
    <%--Create--%>
    <div id="submitOrderBuySellWrapper">
        <c:if test="${formVariant != 'acceptOrder' && formVariant != 'deleteOrder'}">
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
        </c:if>
        <c:if test="${formVariant == 'acceptOrder'}">
            <loc:message code="acceptorder.submit" var="labelSubmit"/>
            <button id="submitOrderSell" onclick="finPassCheck(${orderCreateDto.orderId}, submitAcceptOrder)"
                    type="submit">${labelSubmit}</button>
        </c:if>
        <c:if test="${formVariant == 'deleteOrder'}">
            <loc:message code="deleteorder.submit" var="labelSubmit"/>
            <button id="submitOrderBuy" onclick="$('#createBuyOrderForm').submit()"
                    type="submit">${labelSubmit}</button>
        </c:if>
    </div>
    <%--Edit--%>
    <c:if test="${editUrl != null}">
        <form:form id="editOrderBuySellWrapper" action="${editUrl}" modelAttribute="orderCreateDto" method="post">
            <form:hidden path="currencyPair"/>
            <form:hidden path="operationType"/>
            <form:hidden path="walletIdCurrencyBase"/>
            <form:hidden path="walletIdCurrencyConvert"/>
            <form:hidden path="currencyBaseBalance"/>
            <form:hidden path="currencyConvertBalance"/>
            <form:hidden path="comissionForBuyId"/>
            <form:hidden path="comissionForBuyRate"/>
            <form:hidden path="comissionForSellId"/>
            <form:hidden path="comissionForSellRate"/>
            <form:hidden path="exchangeRate"/>
            <form:hidden path="amount"/>
            <form:hidden path="total"/>
            <form:hidden path="comission"/>
            <%--<form:input hidden="totalWithComission" path="comission"/>--%>
            <form:hidden path="comission"/>
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



