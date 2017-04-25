<%--
  User: Valk
--%>

<div id="myorders" data-submenuitemid="myhistory-button-orders" class="myorders center-frame-container hidden">
    <%----%>
    <div id="myorders-currency-pair-selector" class="currency-pair-selector dropdown">
        <c:set value="true" var="showAllPairsEnabled"/>
        <%@include file="currencyPairSelector.jsp" %>
    </div>
    <h4 class="h4_green"><loc:message code="orders.title"/></h4>

    <div>
        <button id="myorders-button-deal" class="myorders__button green-box margin-box"><loc:message
                code="myorders.deal"/></button>
        <button id="myorders-button-mine" class="myorders__button green-box margin-box"><loc:message
                code="myorders.title"/></button>
        <button id="myorders-button-accepted" class="myorders__button green-box margin-box"><loc:message
                code="myorders.accepted"/></button>
        <button id="myorders-button-cancelled" class="myorders__button red-box margin-box"><loc:message
                code="myorders.cancelled"/></button>
    </div>
    <%----%>

    </br>
    </br>
    <h4 class="h4_green"><loc:message code="myorders.sellorders"/></h4>
    </br>
    <ul class="pager balance__table myorders__pager">
        <li class="previous myorders-sell-table__backward"><a href="#"><loc:message code="table.backward"/></a></li>
        <div class="myorders-sell-table__page" style="display:inline-block"></div>
        <li class="next myorders-sell-table__forward"><a href="#"><loc:message code="table.forward"/></a></li>
    </ul>
    <table id="myorders-sell-table" class="balance__table myorders__table">
        <c:set value="myorders-sell-table_row" var="table_row_id"/>
        <%@include file="myorders-center-tableBody.jsp" %>
    </table>

    </br>
    <h4 class="h4_green"><loc:message code="myorders.buyorders"/></h4>
    </br>
    <ul class="pager balance__table myorders__pager">
        <li class="previous myorders-buy-table__backward"><a href="#"><loc:message code="table.backward"/></a></li>
        <div class="myorders-buy-table__page" style="display:inline-block"></div>
        <li class="next myorders-buy-table__forward"><a href="#"><loc:message code="table.forward"/></a></li>
    </ul>
    <table id="myorders-buy-table" class="balance__table myorders__table">
        <c:set value="myorders-buy-table_row" var="table_row_id"/>
        <%@include file="myorders-center-tableBody.jsp" %>
    </table>


    <%--    <br>
        <h4 class="h4_green"><loc:message code="myorders.stoporders"/></h4>
        <br>
        <ul class="pager balance__table myorders__pager">
            <li class="previous myorders-buy-table__backward"><a href="#"><loc:message code="table.backward"/></a></li>
            <div class="myorders-buy-table__page" style="display:inline-block"></div>
            <li class="next myorders-buy-table__forward"><a href="#"><loc:message code="table.forward"/></a></li>
        </ul>
        <table id="myorders-buy-table" class="balance__table myorders__table">
            <c:set value="myorders-buy-table_row" var="table_row_id"/>
            <%@include file="myStopOrders-center-tableBody.jsp" %>
        </table>--%>



</div>
