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
  <link href="<c:url value="/client/css/ekko-lightbox.min.css"/>" rel="stylesheet">
  <script type="text/javascript" src="<c:url value="/client/js/ekko-lightbox.min.js"/>"></script>
</head>

<style>
  #invoice_requests td {
    padding: 5px 5px;
  }
</style>


<body>

<%@include file='../fragments/header-simple.jsp' %>

<main class="container">
  <div class="row">
    <%@include file='left_side_menu.jsp' %>
    <div class="col-md-10 content admin-container">
        <div class="row text-center">
            <div style="float: left; display: inline-block">
                <button id="invoice-requests-for-accept" class="myorders__button blue-box margin-box">
                    <loc:message code="admin.invoice.new"/></button>
                <button id="invoice-requests-accepted" class="myorders__button green-box margin-box">
                    <loc:message code="admin.invoice.accepted"/></button>
            </div>
        </div>


      <div class="text-center"><h4><loc:message code="transaction.titleInvoice"/></h4></div>
      <c:choose>
        <c:when test="${fn:length(invoiceRequests)==0}">
          <loc:message code="transactions.absent"/>
        </c:when>
        <c:otherwise>
          <table id="invoice_requests" class="table-striped">
            <thead>
            <tr>
              <th><loc:message code="transaction.id"/></th>
              <th><loc:message code="transaction.datetime"/></th>
              <th><loc:message code="transaction.user"/></th>
              <th><loc:message code="transaction.currency"/></th>
              <th><loc:message code="transaction.amount"/></th>
              <th><loc:message code="transaction.commissionAmount"/></th>
              <th><loc:message code="invoice.exratesBank"/></th>
              <th><loc:message code="invoice.payerBank"/></th>
              <th><loc:message code="transaction.acceptanceDatetime"/></th>
              <th><loc:message code="transaction.confirmation"/></th>
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
<div hidden id="prompt_acc_rqst" style="display: none"><loc:message code="merchants.invoice.promptAccept"/></div>
<div hidden id="prompt_decline_rqst"><loc:message code="merchants.invoice.promptDecline"/></div>
<span hidden id="acceptLocMessage"><loc:message code="merchants.invoice.accept"/></span>
<span hidden id="declineLocMessage"><loc:message code="merchants.invoice.decline"/></span>
<span hidden id="acceptedLocMessage"><loc:message code="merchants.invoice.accepted"/></span>
<span hidden id="declinedLocMessage"><loc:message code="merchants.invoice.declined"/></span>
<span hidden id="onConfirmationLocMessage"><loc:message code="merchants.invoice.onWaitingForUserConfirmation"/></span>
<span hidden id="revokedByUserLocMessage"><loc:message code="merchants.invoice.revokedByUser"/></span>
<span hidden id="timeOutExpiredLocMessage"><loc:message code="merchants.invoice.timeOutExpired"/></span>
<span hidden id="changeAmountLocMessage"><loc:message code="admin.invoice.changeAmount"/></span>
<span hidden id="cancelLocMessage"><loc:message code="admin.cancel"/></span>


<div id="acceptModal" class="modal fade form_full_width">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="merchants.invoice.accept.modalTitle"/></h4>
            </div>
            <div class="modal-body">
                <div class="row text-center">
                    <p style="font-size: 1.34rem"><loc:message code="merchants.invoice.promptAccept"/></p>
                </div>
                <form id="invoice-accept-form">
                    <div class="input-block-wrapper">
                        <div class="col-md-4 input-block-wrapper__label-wrapper">
                            <label for="initialAmount" class="input-block-wrapper__label"><loc:message code="admin.invoice.initialAmount"/></label>
                        </div>
                        <div class="col-md-8 input-block-wrapper__input-wrapper">
                            <input id="initialAmount" readonly class="input-block-wrapper__input" type="number" step="0.01">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-4 input-block-wrapper__label-wrapper">
                            <label for="actualAmount" class="input-block-wrapper__label"><loc:message code="admin.invoice.actualAmount"/></label>
                        </div>
                        <div class="col-md-5 input-block-wrapper__input-wrapper">
                            <input id="actualAmount" class="input-block-wrapper__input" type="number" step="0.01">
                        </div>
                        <div class="col-md-3 input-block-wrapper__input-wrapper">
                            <button type="button" id="changeAmount" class="btn btn-sm btn-danger"><loc:message code="admin.invoice.changeAmount"/></button>
                        </div>
                        <input hidden id="transactionId" name="id">
                        <input hidden id="actualPaymentSum" name="actualPaymentSum">
                    </div>
                    <div class="table-button-block" style="white-space: nowrap; margin-top: 20px">
                        <button id="submitAccept" class="blue-box" type="button"><loc:message code="admin.submit"/></button>
                        <button id="cancelAccept" class="red-box" type="button"><loc:message code="admin.cancel"/></button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@include file='../fragments/modal/enter_note_before_decline_modal.jsp' %>
<%@include file='../fragments/modal/invoice_info_modal.jsp' %>
<%@include file='../fragments/footer.jsp' %>
</body>
</html>


