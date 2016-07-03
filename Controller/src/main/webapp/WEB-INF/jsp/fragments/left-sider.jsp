<%--
  Created by IntelliJ IDEA.
  User: Valk
  Date: 01.06.2016
  Time: 12:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="left-sider cols-md-2">
    <sec:authorize access="isAuthenticated()">
        <h4 class="h4_green"><loc:message code="usermenu.referral"/></h4>
        <hr class="under_h4">
        <div class="refferal clearfix">
            <div id="refferal-reference" class="refferal__reference"></div>
            <div>
                <button id="refferal-generate" class="refferal__button">
                    <loc:message code="refferal.generate"/>
                </button>
                <button id="refferal-copy" class="refferal__button">
                    <loc:message code="refferal.copy"/>
                </button>
            </div>
        </div>
        <h4 class="h4_green"><loc:message code="mywallets.abalance"/></h4>
        <hr class="under_h4">
        <table id="mywallets_table" class="table mywallets_table">
            <tbody>
            <tr>
                <th><loc:message code="mywallets.currency"/></th>
                <th><loc:message code="mywallets.amount"/></th>
            </tr>
            <script type="text/template" id="mywallets_table_row">
                <tr>
                    <td><@=currencyName@></td>
                    <td class="right"><@=activeBalance@></td>
                </tr>
            </script>
            </tbody>
        </table>
    </sec:authorize>
    <h4 class="h4_green"><loc:message code="currency.pairs"/></h4>
    <hr class="under_h4">
    <table id="currency_table" class="table currency_table">
        <tr>
            <th><loc:message code="currency.pair"/></th>
            <th><loc:message code="currency.rate"/></th>
        </tr>
        <script type="text/template" id="currency_table_row">
            <@var c = lastOrderRate == predLastOrderRate ? "black" : lastOrderRate < predLastOrderRate ? "red" :
            "green";@>
            <tr>
                <td><@=currencyPairName@></td>
                <td class="right <@=c@>"><@=lastOrderRate@></td>
            </tr>
        </script>
    </table>
</div>
