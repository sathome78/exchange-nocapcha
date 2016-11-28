<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 28.11.2016
  Time: 10:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<html>
<head>
    <title><loc:message code="admin.commissions"/></title>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminCommissionsDataTable.js'/>"></script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-6 col-md-offset-2 admin-container">
            <div class="text-center"><h4><loc:message code="admin.commissions"/></h4></div>

            <sec:authorize access="hasAnyAuthority('${adminEnum}')">
            <div class="buttons">
                <button class="active adminForm-toggler blue-box">
                    <loc:message code="admin.stockExchangeCommissions"/>
                </button>
                <button class="adminForm-toggler blue-box">
                    <loc:message code="admin.merchantsCommissions"/>
                </button>
            </div>
            <div class="tab-content">
                <div id="panel1" class="tab-pane active">
                    <div class="col-sm-6">
                        <div class="text-center"><h4><loc:message code="admin.stockExchangeCommissions"/></h4></div>

                        <table id="commissions-table">
                            <thead>
                            <tr>
                                <th><loc:message code="admin.commissions.operationType"/> </th>
                                <th><loc:message code="admin.commissions.value"/></th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${commissions}" var="commission">
                                <tr data-id="${commission.id}">
                                    <td>${commission.operationType}</td>
                                    <td>
                                        <span hidden class="commissionUnformatted">${commission.value}</span>
                                        <span class="commissionFormatted">
                                                <fmt:formatNumber value="${commission.value}" pattern="###,##0.00########"/>
                                            </span>
                                    </td>

                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div id="panel2" class="tab-pane">
                    <div class="col-sm-6">
                        <div class="text-center"><h4><loc:message code="admin.merchantsCommissions"/></h4></div>

                        <table id="merchant-commissions-table">
                            <thead>
                            <tr>
                                <th><loc:message code="withdrawal.merchant"/> </th>
                                <th><loc:message code="withdrawal.currency"/> </th>
                                <th><loc:message code="withdrawal.commission"/></th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${merchantCurrencies}" var="merchantCurrency">
                                <tr data-merchantid="${merchantCurrency.merchantId}"
                                    data-currencyid="${merchantCurrency.currencyId}">
                                    <td>${merchantCurrency.merchantName}</td>
                                    <td>${merchantCurrency.currencyName}</td>
                                    <td>
                                        <span hidden class="merchantCommissionUnformatted">${merchantCurrency.commission}</span>
                                        <span class="merchantCommissionFormatted">
                                                <fmt:formatNumber value="${merchantCurrency.commission}" pattern="###,##0.00########"/>
                                            </span>
                                    </td>

                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>

            </div>



            </sec:authorize>
        </div>
</main>
<div id="editCommissionModal" class="modal fade">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="admin.editCommission"/></h4>
            </div>
            <div class="modal-body">
                <form id="edit-commission-form">
                    <input type="hidden" name="commissionId" >
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.commissions.operationType"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="operation-type" class="input-block-wrapper__input" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.commissions.value"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input  name="commissionValue" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <button id="submitCommission" class="blue-box admin-form-submit" type="submit"><loc:message code="admin.refSubmitEditCommonRoot"/></button>
                </form>
            </div>
        </div>
    </div>
</div>

<div id="editMerchantCommissionModal" class="modal fade">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="admin.editCommission"/></h4>
            </div>
            <div class="modal-body">
                <form id="edit-merchantCommission-form">
                    <input type="hidden" name="merchantId" >
                    <input type="hidden" name="currencyId" >
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="merchantName" class="input-block-wrapper__label"><loc:message code="withdrawal.merchant"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="merchantName" class="input-block-wrapper__input" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="currencyName" class="input-block-wrapper__label"><loc:message code="withdrawal.currency"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="currencyName" class="input-block-wrapper__input" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.commissions.value"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input  name="commissionValue" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <button id="submitMerchantCommission" class="blue-box admin-form-submit" type="submit"><loc:message code="admin.refSubmitEditCommonRoot"/></button>
                </form>
            </div>
        </div>
    </div>
</div>

<%@include file='../fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
