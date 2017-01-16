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
    <title><loc:message code="admin.merchantAccess"/></title>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/dataTable/adminMerchantAccessDataTable.js'/>"></script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-6 col-md-offset-2 admin-container">
            <div class="text-center"><h4><loc:message code="admin.merchantAccess"/></h4></div>
<hr/>
                    <div class="col-sm-8 col-sm-offset-2">

                        <table id="merchant-options-table">
                            <thead>
                            <tr>
                                <th><loc:message code="withdrawal.merchant"/> </th>
                                <th><loc:message code="withdrawal.currency"/> </th>
                                <th><loc:message code="transaction.operationTypeINPUT"/></th>
                                <th><loc:message code="transaction.operationTypeOUTPUT"/></th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${merchantCurrencies}" var="merchantCurrency">
                                <tr data-merchantid="${merchantCurrency.merchantId}"
                                    data-currencyid="${merchantCurrency.currencyId}">
                                    <td>${merchantCurrency.merchantName}</td>
                                    <td>${merchantCurrency.currencyName}</td>
                                    <td data-operationtype="INPUT">
                                        <c:choose>
                                            <c:when test="${merchantCurrency.refillBlocked}">
                                                <i class="fa fa-lock red" aria-hidden="true"></i>
                                            </c:when>
                                            <c:otherwise>
                                                <i class="fa fa-unlock" aria-hidden="true"></i>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td data-operationtype="OUTPUT">
                                        <c:choose>
                                            <c:when test="${merchantCurrency.withdrawBlocked}">
                                                <i class="fa fa-lock red" aria-hidden="true"></i>
                                            </c:when>
                                            <c:otherwise>
                                                <i class="fa fa-unlock" aria-hidden="true"></i>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                </tr>
                            </c:forEach>
                            </tbody>

                        </table>

                        <sec:authorize access="hasAuthority('${adminEnum}')">
                            <hr/>
                            <div class="row text-center">
                                <button id="block-all-input" class="red-box"><loc:message code="admin.blockAllInput" /></button>
                                <button id="block-all-output" class="red-box"><loc:message code="admin.blockAllOutput" /></button>
                            </div>
                            <div class="row text-center">
                                <button id="unblock-all-input" class="blue-box"><loc:message code="admin.unblockAllInput" /></button>
                                <button id="unblock-all-output" class="blue-box"><loc:message code="admin.unblockAllOutput" /></button>
                            </div>
                        </sec:authorize>
                    </div>
            </div>
        </div>
</main>
<div hidden id="prompt-toggle-block">
    <loc:message code="admin.toggleBlockPrompt" />
</div>
<div hidden id="prompt-toggle-block-all">
    <loc:message code="admin.blockAllPrompt" />
</div>
<%@include file='../fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
