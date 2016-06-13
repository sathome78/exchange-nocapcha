<%--
  User: Valk
--%>

<div id="myorders" class="myorders center-frame-container hidden">
    <%----%>
    <div id="myorders-currency-pair-selector" class="currency-pair-selector dropdown">
        <%@include file="currencyPairSelector.jsp" %>
    </div>
        <h4 class="h4_green"><loc:message code="myorders.title"/></h4>
    <div>
        <button id="myorders-button-deal" class="myorders__button green-box margin-box"><loc:message code="myorders.deal"/></button>
        <button id="myorders-button-cancelled" class="myorders__button red-box margin-box"><loc:message code="myorders.cancelled"/></button>
    </div>
    <%----%>

    </br>
    </br>
    <h4 class="h4_green"><loc:message code="myorders.sellorders"/></h4>
    </br>
    <table id="myorders-sell-table" class="balance__table myorders__table">
        <c:set value="myorders-sell-table_row" var="table_row_id"/>
        <%@include file="myorders-center-tableBody.jsp" %>
    </table>
    </br>
    <h4 class="h4_green"><loc:message code="myorders.buyorders"/></h4>
    </br>
    <table id="myorders-buy-table" class="balance__table myorders__table">
        <c:set value="myorders-buy-table_row" var="table_row_id"/>
        <%@include file="myorders-center-tableBody.jsp" %>
    </table>
</div>
