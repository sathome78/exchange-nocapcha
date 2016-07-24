

<tbody>
<tr>
    <th class="col-4 center blue-white"><loc:message code="inputoutput.datetime"/></th>
    <th class="col-2 center blue-white"><loc:message code="inputoutput.currency"/></th>
    <th class="col-2 right blue-white"><loc:message code="inputoutput.amount"/></th>
    <th class="col-2 right blue-white"><loc:message code="inputoutput.commissionAmount"/></th>
    <th class="col-2 center blue-white"><loc:message code="inputoutput.merchant"/></th>
    <th class="col-2 right blue-white"><loc:message code="inputoutput.operationtype"/></th>
    <th class="col-4 center blue-white"><loc:message code="inputoutput.order"/></th>
    <th class="col-2 center blue-white"><loc:message code="inputoutput.status"/></th>
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
        <td class="center"></td>
    </tr>
</script>
</tbody>

