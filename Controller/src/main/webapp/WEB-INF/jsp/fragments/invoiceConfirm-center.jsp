<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 06.02.2017
  Time: 17:56
  To change this template use File | Settings | File Templates.
--%>
<div>
  <h4><loc:message code="merchants.invoiceConfirm.title"/></h4>
  <c:choose>
    <c:when test="${not empty error}">
      <label class="alert-danger has-error">
        <loc:message code="${error}"/>
      </label>
    </c:when>
    <c:otherwise>
      <c:set var="needToConfirm" value='${invoiceRequest.invoiceRequestStatus == "CREATED_USER"
                                            ||invoiceRequest.invoiceRequestStatus == "DECLINED_ADMIN"}'/>
      <c:set var="selected" value="selected"/>
      <c:set var="readonly" value="readonly"/>
      <c:set var="disabled" value="disabled"/>
      <c:set var="readonlyIfConfirmed" value="${confirmed ? readonly : ''}"/>
      <span hidden>${needToConfirm}</span>

      <div class="row">
        <div class="input-block-wrapper clearfix">
          <div id="creditsOperationInfo" class="well col-md-6 col-md-offset-3 ">
            <div class="text-center">
              <h5><loc:message code="merchants.invoiceDetails.paymentDetails"/></h5>
            </div>
            <table class="table">
              <tbody>
              <tr>
                <td><loc:message code="transaction.amount"/></td>
                <td><fmt:formatNumber value="${invoiceRequest.transaction.amount}" pattern="###,##0.00######"/>
                    ${invoiceRequest.transaction.currency.name}</td>
              </tr>
              <tr>
                <td><loc:message code="merchants.invoiceDetails.bankName"/></td>
                <td>${invoiceRequest.invoiceBank.name}</td>
              </tr>
              <tr>
                <td><loc:message code="merchants.invoiceDetails.bankAccount"/></td>
                <td>${invoiceRequest.invoiceBank.accountNumber}</td>
              </tr>
              <tr>
                <td><loc:message code="merchants.invoiceDetails.bankRecipient"/></td>
                <td>${invoiceRequest.invoiceBank.recipient}</td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>


        <form id="confirmationForm" action="<c:url value="/merchants/invoice/payment/confirm"/>" method="post">
          <input type="hidden" name="invoiceId" id="invoiceId" value="${invoiceRequest.transaction.id}" <c:out
                  value="${readonlyIfConfirmed}"/>>
          <input type="hidden" name="payerBankName" id="payerBankName" value="${invoiceRequest.payerBankName}" <c:out
                  value="${readonlyIfConfirmed}"/>>
          <div class="input-block-wrapper clearfix">
            <div class="col-md-3 input-block-wrapper__label-wrapper">
              <label for="bankSelect" class="input-block-wrapper__label">
                <loc:message code="merchants.invoiceConfirm.bankFrom"/>*</label>
            </div>
            <div class="col-md-8 ">
              <select class="form-control input-block-wrapper__input" id="bankSelect" <c:out
                      value="${confirmed ? disabled : ''}"/>>
                <option value="-1"><loc:message code="merchants.notSelected"/></option>
                <c:forEach items="${bankNames}" var="bank" varStatus="counter">
                  <option <c:out value="${bank.equals(invoiceRequest.payerBankName) ? selected : ''}"/>
                          value="${counter.count}">${bank}</option>
                </c:forEach>
                <option <c:out value="${not empty otherBank ? selected : ''}"/> value="0"><loc:message
                        code="merchants.invoice.otherBank"/></option>
              </select>
            </div>
          </div>
          <div class="input-block-wrapper clearfix">
            <div class="col-md-3 input-block-wrapper__label-wrapper">
              <label for="otherBank" class="input-block-wrapper__label"></label>
            </div>
            <div class="col-md-8 ">
              <input class="form-control input-block-wrapper__input" type="text" id="otherBank" value="${otherBank}"
                <c:out value="${readonlyIfConfirmed}"/>>
            </div>
            <div id="bankNameError" class="col-md-11 input-block-wrapper__error-wrapper">
              <p class="red"><loc:message code="merchants.error.bankNameInLatin"/></p>
            </div>
          </div>
          <div class="input-block-wrapper clearfix">
            <div class="col-md-3 input-block-wrapper__label-wrapper">
              <label for="userAccount" class="input-block-wrapper__label">
                <loc:message code="merchants.invoiceConfirm.userAccount"/>*</label>
            </div>
            <div class="col-md-8 ">
              <input class="form-control input-block-wrapper__input" type="text" id="userAccount"
                     name="userAccount" value="${invoiceRequest.payerAccount}" <c:out value="${readonlyIfConfirmed}"/>>
            </div>
            <div id="userAccountError" class="col-md-11 input-block-wrapper__error-wrapper">
              <p class="red"><loc:message code="merchants.error.accountDigitsOnly"/></p>
            </div>
          </div>
          <div class="input-block-wrapper clearfix">
            <div class="col-md-3 input-block-wrapper__label-wrapper">
              <label for="userFullName" class="input-block-wrapper__label">
                <loc:message code="merchants.invoiceDetails.userFullName"/>*</label>
            </div>
            <div class="col-md-8 ">
              <input class="form-control input-block-wrapper__input" type="text" id="userFullName"
                     name="userFullName" value="${invoiceRequest.userFullName}" <c:out value="${readonlyIfConfirmed}"/>>
            </div>
            <div id="userFullNameError" class="col-md-11 input-block-wrapper__error-wrapper">
              <p class="red"><loc:message code="merchants.error.fullNameInLatin"/></p>
            </div>
          </div>
          <div class="input-block-wrapper clearfix">
            <div class="col-md-3 input-block-wrapper__label-wrapper">
              <label for="remark" class="input-block-wrapper__label">
                <loc:message code="merchants.invoiceDetails.remark"/></label>
            </div>
            <div class="col-md-8">
              <textarea id="remark" class="form-control textarea non-resize" name="remark" <c:out
                      value="${readonlyIfConfirmed}"/> >${invoiceRequest.remark}</textarea>
            </div>
          </div>
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

          <c:choose>
            <c:when test="${needToConfirm}">
              <c:choose>
                <c:when test="${revoke}">
                  <div class="col-md-4 input-block-wrapper">
                    <button id="invoiceCancelInvoice" class="btn btn-primary btn-lg" type="button"><loc:message
                            code="merchants.invoice.revoke.submit"/></button>
                  </div>
                </c:when>
                <c:otherwise>
                  <div class="col-md-4 input-block-wrapper">
                    <button id="invoiceSubmit" class="btn btn-primary btn-lg" type="submit"><loc:message
                            code="admin.submit"/></button>
                    <button id="invoiceCancel" class="btn btn-danger btn-lg" type="button"><loc:message
                            code="admin.cancel"/></button>
                  </div>
                </c:otherwise>
              </c:choose>
            </c:when>
            <c:otherwise>
              <div class="col-md-4 input-block-wrapper">
                <button id="invoiceReturn" class="btn btn-primary btn-lg" type="button"><loc:message
                        code="admin.return"/></button>
              </div>
            </c:otherwise>
          </c:choose>
        </form>

      </div>
    </c:otherwise>
  </c:choose>
</div>
