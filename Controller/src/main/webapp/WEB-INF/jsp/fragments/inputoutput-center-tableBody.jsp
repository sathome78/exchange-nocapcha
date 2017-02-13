

<tbody>
<tr>
    <th class="col-4 center blue-white"><loc:message code="inputoutput.datetime"/></th>
    <th class="col-2 center blue-white"><loc:message code="inputoutput.currency"/></th>
    <th class="col-2 right blue-white"><loc:message code="inputoutput.amount"/></th>
    <th class="col-2 right blue-white"><loc:message code="inputoutput.commissionAmount"/></th>
    <th class="col-2 center blue-white"><loc:message code="inputoutput.merchant"/></th>
    <th class="col-1 right blue-white"><loc:message code="inputoutput.operationtype"/></th>
    <th class="col-3 center blue-white"><loc:message code="inputoutput.order"/></th>
    <th class="col-2 center blue-white"><loc:message code="inputoutput.status"/></th>
    <th class="col-3 center blue-white"></th>
</tr>
<script type="text/template" id="${table_row_id}">
    <tr>
        <td class="center blue-white"><@=datetime@></td>
        <td class="center"><@=currencyName@></td>
        <td class="right"><@=amount@></td>
        <td class="right"><@=commissionAmount@></td>
        <td class="center"><@=merchantName@></td>
        <td class="right"><@=operationType@></td>
        <td class="center"><@=transactionId@></td>
        <td class="center"><@=transactionProvided@></td>
        <td class="center table-button-block">
            <form class="table-button-block__form" action="<c:url value="/merchants/invoice/payment/confirmation"/>">
                <input type="text" hidden value=<@=transactionId@>  name="transactionId" >
            <@=(function() {
                if (confirmationRequired) {
                    return '<button type="submit" style="font-size: 1.1rem;" class="wallet-mystatement-button table-button-block__button btn btn-primary">
                <loc:message code="merchants.invoice.confirm" /></button>';
                } else if (merchantName === 'Invoice') {
                    return '<button type="submit" style="font-size: 1.1rem;" class="wallet-mystatement-button table-button-block__button btn btn-info">
                <loc:message code="merchants.invoice.viewConfirm" /></button>'
                }
            })()

            @>
            </form></td>
    </tr>
</script>
</tbody>

