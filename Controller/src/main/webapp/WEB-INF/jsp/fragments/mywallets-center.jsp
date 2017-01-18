<%--
  User: Valk
--%>
<div id="balance-page" data-menuitemid="menu-mywallets" class="balance center-frame-container hidden">
    <h4 class="h4_green"><loc:message code="mywallets.title"/></h4>
    <table id="balance-table" class="balance__table">
        <tbody>
        <tr>
            <th class="left blue-white"><loc:message code="mywallets.currency"/></th>
            <th class="right blue-white"><loc:message code="mywallets.balance"/></th>
            <th class="right blue-white"><loc:message code="mywallets.onconfirmation"/></th>
            <th class="right blue-white"><loc:message code="mywallets.rbalance"/></th>
            <th class="right blue-white"><loc:message code="mywallets.reservedonorders"/></th>
            <th class="right blue-white"><loc:message code="mywallets.reservedonwithdraw"/></th>
            <th class="right blue-white"></th>
        </tr>
        <script type="text/template" id="balance-table_row">
            <tr class="balance-table__row">
                <td class="left blue-white"><@=currencyName@></td>
                <td class="right"><@=activeBalance@></td>
                <td class="right"><@=onConfirmation@><div class="on-confirmation-detail">
                    <@=
                    (function(){
                    if((+onConfirmationCount)==1){
                        return '('+onConfirmationStage+'/4)';
                    }else if((+onConfirmationCount)>1){
                        return '<span class="glyphicon glyphicon-search mywallet-item-detail" data-walletid='+id+'></span>';
                    }
                    })()
                    @></div></td>
                <td class="right"><@=reservedBalance@></td>
                <td class="right"><@=reservedByOrders@></td>
                <td class="right"><@=reservedByMerchant@></td>
                <td class="table-button-block table-button-block--wallets">
                    <form class="table-button-block__form" action="/merchants/input" target="_blank">
                        <loc:message code="mywallets.input" var="inputButton"/>
                        <input type="text" hidden value=<@=currencyName@> name="currency" >
                        <button class="table-button-block__button btn btn-success" type="submit">${inputButton}</button>
                    </form>
                    <form class="table-button-block__form" action="<c:url value="/merchants/output"/>" target="_blank">
                        <loc:message code="mywallets.output" var="outputButton"/>
                        <input type="text" hidden value=<@=currencyName@> name="currency" >
                        <button class="table-button-block__button btn btn-danger" type="submit">${outputButton}</button>
                    </form>
                    <form class="table-button-block__form" action="<c:url value="#"/>">
                        <loc:message code="mywallets.history" var="historyButton"/>
                        <@=
                        '<button data-walletid='+id+' class="wallet-mystatement-button table-button-block__button btn btn-primary" type="submit">${historyButton}</button>'
                        @>
                    </form>
                    <form class="table-button-block__form" action="<c:url value="/transfer"/>">
                        <loc:message code="mywallets.transfer" var="transferButton"/>
                        <input type="text" hidden value=<@=currencyName@> name="currencyName" >
                        <button class="table-button-block__button btn btn-info" type="submit">${transferButton}</button>
                    </form>
                </td>
            </tr>
        </script>
        </tbody>
    </table>
</div>



