<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title><loc:message code="transaction.titleInvoice"/></title>
    <%@include file='links_scripts.jsp' %>
</head>


<body>

<%@include file='../fragments/header-simple.jsp' %>

<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-10 content admin-container">
            <div class="text-center"><h4><loc:message code="transaction.titleInvoice"/></h4></div>
            <c:choose>
                <c:when test="${fn:length(invoiceRequests)==0}">
                    <loc:message code="transactions.absent"/>
                </c:when>
                <c:otherwise>
                    <%--СПИСОК ИНВОЙСОВ--%>
                    <table id="invoice_requests">
                        <thead>
                        <tr>
                            <th><loc:message code="transaction.id"/></th>
                                <%--Дата--%>
                            <th><loc:message code="transaction.datetime"/></th>
                                <%--Пользователь--%>
                            <th><loc:message code="transaction.user"/></th>
                                <%--Валюта--%>
                            <th><loc:message code="transaction.currency"/></th>
                                <%--Сумма--%>
                            <th><loc:message code="transaction.amount"/></th>
                                <%--Сумма <br> комиссии--%>
                            <th><loc:message code="transaction.commissionAmount"/></th>

                            <th><loc:message code="invoice.exratesBank"/></th>

                            <th><loc:message code="invoice.payeeBank"/></th>

                                <%--Дата обработки заявки--%>
                            <th><loc:message code="transaction.acceptanceDatetime"/></th>



                                <%--Confirmation--%>
                            <th><loc:message code="transaction.confirmation"/></th>

                                <%--Пользователь, обработавший заявку--%>
                            <th><loc:message code="transaction.acceptanceUser"/></th>


                        </tr>
                        </thead>
                    </table>

                </c:otherwise>
            </c:choose>
        </div>
    </div>
    <hr/>
</main>
<div id="prompt_acc_rqst" style="display: none">
    <loc:message code="merchants.promptWithdrawRequestAccept"/>
</div>
<span hidden id="confirmButtonLocMessage"><loc:message code="transaction.accept"/></span>
<span hidden id="confirmedLocMessage"><loc:message code="transaction.provided"/></span>
<%@include file='../fragments/modal/invoice_info_modal.jsp' %>
<%@include file='../fragments/footer.jsp' %>
</body>
</html>

