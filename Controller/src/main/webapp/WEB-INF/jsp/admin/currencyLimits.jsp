<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 23.09.2016
  Time: 12:30
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
    <title><loc:message code="admin.currencyLimits.title"/></title>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminCurrenciesDataTable.js'/>"></script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-8 col-md-offset-2 admin-container">
            <div class="text-center"><h4><loc:message code="admin.currencyLimits.title"/></h4></div>

            <sec:authorize access="hasAnyAuthority('${adminEnum}')">
                <div id="panel4 row" class="tab-pane">
                    <div class="col-sm-6 text-center">
                        <h5>
                            <loc:message code="admin.currencyLimits.table"/>
                        </h5>
                        <table id="currency-limits-table">
                            <thead>
                            <tr>
                                <th><loc:message code="admin.currencyLimits.name"/> </th>
                                <th><loc:message code="admin.currencyLimits.description"/></th>
                                <th><loc:message code="admin.currencyLimits.minLimit"/></th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${currencies}" var="currency">
                                <tr data-id="${currency.id}">
                                    <td>
                                            ${currency.name}
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${currency.description == null}">
                                                -
                                            </c:when>
                                            <c:otherwise>
                                                ${currency.description}
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <span hidden class="minLimitUnformatted">${currency.minWithdrawSum}</span>
                                        <c:choose>
                                            <c:when test="${currency.minWithdrawSum == null}">
                                                -
                                            </c:when>
                                            <c:otherwise>
                                        <span class="minLimitFormatted">
                                                <fmt:formatNumber value="${currency.minWithdrawSum}" pattern="###,##0.00########"/>
                                            </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

            </sec:authorize>

        </div>
    </div>
</main>
<div id="editLimitModal" class="modal fade">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="admin.currencyLimits.modalTitle"/></h4>
            </div>
            <div class="modal-body">
                <form id="edit-currency-limit-form">
                    <input type="hidden" name="currencyId" >
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="withdrawal.currency"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="currency-name" class="input-block-wrapper__input" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.currencyLimits.minLimit"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input  name="minAmount" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <button id="submitNewLimit" class="blue-box admin-form-submit" type="submit"><loc:message code="admin.refSubmitEditCommonRoot"/></button>
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
