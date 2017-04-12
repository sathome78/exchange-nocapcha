<script>
  function onSelectNewValue(select) {
    var bankId = $('#bankId').val();
    var $bankInfoOption = $(select).find("option[value=" + bankId + "]");
    var $infoWrapper = $(select).closest("#credits-operation-info");
    $infoWrapper.find("#bankName").html($bankInfoOption.data("bank-name"));
    $infoWrapper.find("#bankCode").html($bankInfoOption.data("bank-code"));
  }
</script>
<div class="modal fade merchant-output" id="dialog-withdraw-detailed-params-enter">
  <h4><loc:message code="merchants.invoiceDetails.title"/></h4>
  <c:choose>
    <c:when test="${not empty error}">
      <label class="alert-danger has-error">
        <loc:message code="${error}"/>
      </label>
    </c:when>
    <c:otherwise>
      <div class="row">
        <div class="input-block-wrapper clearfix">
          <div id="credits-operation-info" class="well col-md-6 col-md-offset-3 ">
            <div class="text-center">
              <h5><loc:message code="merchants.invoiceDetails.paymentDetails"/></h5>
            </div>
            <table class="table">
              <tbody>
              <tr>
                <td><loc:message code="transaction.amount"/></td>
                <td><fmt:formatNumber value="${creditsOperation.amount}"
                                      pattern="###,##0.00######"/> ${creditsOperation.currency.name}</td>
              </tr>
              <c:if test="${not empty additionMessage}">
                <tr>
                  <td colspan="2">${additionMessage}</td>
                </tr>
              </c:if>
              <tr>
                <td><loc:message code="transaction.commission"/></td>
                <td><fmt:formatNumber value="${creditsOperation.commission.value}" pattern="###,##0.0#######"/></td>
              </tr>
              <tr>
                <td><loc:message code="transaction.commissionAmount"/></td>
                <td><fmt:formatNumber value="${creditsOperation.commissionAmount}"
                                      pattern="###,##0.0#######"/> ${creditsOperation.currency.name}</td>
              </tr>
              <tr>
                <td><loc:message code="dashboard.amountwithcommission"/></td>
                <td><fmt:formatNumber value="${creditsOperation.amount.add(creditsOperation.commissionAmount)}"
                                      pattern="###,##0.0#######"/> ${creditsOperation.currency.name}</td>
              </tr>
              <tr>
                <td><loc:message code="merchants.invoiceDetails.bankName"/></td>
                <td>
                  <div id="bankName"></div>
                </td>
              </tr>
              <tr>
                <td><loc:message code="merchants.invoiceDetails.bankAccount"/></td>
                <td>
                  <div id="bankAccount"></div>
                </td>
              </tr>
              <tr>
                <td><loc:message code="merchants.invoiceDetails.bankRecipient"/></td>
                <td>
                  <div id="bankRecipient"></div>
                </td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div>
          <div class="input-block-wrapper clearfix">
            <div class="col-md-3 input-block-wrapper__label-wrapper">
              <label for="bankId" class="input-block-wrapper__label">
                <loc:message code="merchants.invoiceDetails.bank"/>*</label>
            </div>
            <div class="col-md-8 ">
              <select id="bankId"
                      class="form-control input-block-wrapper__input"
                      onchange="onSelectNewValue(this)">
                <c:forEach items="${invoiceBanks}" var="bank">
                  <option value="${bank.id}"
                          data-bank-id="${bank.id}"
                          data-bank-code="${bank.code}">
                      ${bank.name}
                  </option>
                </c:forEach>
              </select>
            </div>
          </div>
          <div class="input-block-wrapper clearfix">
            <div class="col-md-3 input-block-wrapper__label-wrapper">
              <label for="userFullName" class="input-block-wrapper__label">
                <loc:message code="merchants.invoiceDetails.userFullName"/>*</label>
            </div>
            <div class="col-md-8 ">
              <input id="userFullName"
                     class="form-control input-block-wrapper__input"
                     type="text">
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
                            <textarea id="remark"
                                      class="form-control textarea non-resize" name="remark"></textarea>
            </div>
          </div>
          <div class="col-md-4 input-block-wrapper">
            <button id="invoiceSubmit" class="btn btn-primary btn-lg"><loc:message
                    code="admin.submit"/></button>
            <button id="invoiceCancel" class="btn btn-danger btn-lg" data-dismiss="modal"><loc:message
                    code="admin.cancel"/></button>
          </div>
        </div>
      </div>
    </c:otherwise>
  </c:choose>
</div>