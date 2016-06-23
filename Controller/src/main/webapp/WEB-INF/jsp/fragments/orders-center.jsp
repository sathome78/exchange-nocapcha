<%--
  User: Valk
--%>

<div id="orders" class="orders center-frame-container hidden">
    <%----%>
    <div id="orders-currency-pair-selector" class="currency-pair-selector dropdown">
        <%@include file="currencyPairSelector.jsp" %>
    </div>
    <h4 class="h4_green"><loc:message code="orders.title"/></h4>
    <%----%>
    </br>
    </br>
    <h4 class="h4_green"><loc:message code="orders.sellorders"/></h4>
    </br>
    <ul class="pager balance__table orders__pager">
        <li class="previous orders-sell-table__backward"><a href="#"><loc:message code="table.backward"/></a></li>
        <div class="orders-sell-table__page" style="display:inline-block"></div>
        <li class="next orders-sell-table__forward"><a href="#"><loc:message code="table.forward"/></a></li>
    </ul>
    <table id="orders-sell-table" class="balance__table orders__table">
        <c:set value="orders-sell-table_row" var="table_row_id"/>
        <%@include file="orders-center-tableBody.jsp" %>
    </table>
    </br>
    <h4 class="h4_green"><loc:message code="orders.buyorders"/></h4>
    </br>
    <ul class="pager balance__table orders__pager">
        <li class="previous orders-buy-table__backward"><a href="#"><loc:message code="table.backward"/></a></li>
        <div class="orders-buy-table__page" style="display:inline-block"></div>
        <li class="next orders-buy-table__forward"><a href="#"><loc:message code="table.forward"/></a></li>
    </ul>
    <table id="orders-buy-table" class="balance__table orders__table orders-buy-table">
        <c:set value="orders-buy-table_row" var="table_row_id"/>
        <%@include file="orders-center-tableBody.jsp" %>
    </table>

</div>
<%--MODAL--%>
<%@include file="modal/order_delete_confirm_modal.jsp" %>
<%--#order-delete-confirm__modal--%>

