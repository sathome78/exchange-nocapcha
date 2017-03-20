

<tbody>
<tr>
    <th class="col-2 center blue-white"><loc:message code="inputoutput.datetime"/></th>
    <th class="col-1 center blue-white"><loc:message code="inputoutput.currency"/></th>
    <th class="col-08 right blue-white"><loc:message code="inputoutput.amount"/></th>
    <th class="col-06 right blue-white"><loc:message code="inputoutput.commissionAmount"/></th>
    <th class="col-1 center blue-white"><loc:message code="inputoutput.merchant"/></th>
    <th class="col-1 right blue-white"><loc:message code="inputoutput.operationtype"/></th>
    <th class="col-2 center blue-white"><loc:message code="inputoutput.order"/></th>
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
        <td class="center"><@=id@></td>
        <td class="center"><@=summaryStatus@></td>
        <td class="center table-button-block" style="text-align: right;">
            <@=getButtonsSet(id, sourceType, buttons, "inputoutput-table", false)@>
    </tr>
</script>
</tbody>

