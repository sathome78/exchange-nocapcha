<div class="modal fade merchant-input" id="dialog-refill-creation">
  <div style="font-size: 14px"
       class="modal-dialog modal-md">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" id="assertInputPayment" data-dismiss="modal"
                aria-hidden="true">&times;</button>
        <h4 class="modal-title"><loc:message code="merchants.inputTitle"/></h4>
      </div>
      <div class="modal-body">
        <div id="amount-info-wrapper">
          <label class="alert-danger merchantError"><loc:message code="merchants.notAvaliablePayment"/></label>
          <div class="paymentInfo">
            <p><loc:message code="merchants.modalInputHeader"/></p>
            <p><loc:message code="merchants.modalInputCommissionMerchant"/></p>
            <p><loc:message code="merchants.modalInputCommission"/></p>
            <p><loc:message code="merchants.modalInputFinalSum"/></p>
          </div>
          <div id="merchant-commission-warning">
            <hr>
            <div class="red">
              <loc:message code="merchant.commission.warning"/>
            </div>
            <hr>
          </div>
        </div>
        <div class="timeoutWarning">
          <c:forEach var="warningCode" items="${warningCodeList}">
            <div><strong><loc:message code="${warningCode}"/></strong></div>
          </c:forEach>
        </div>

        <div id="message"
             style="width: 100%; border-radius: 5px; display: block; padding: 5px; background: rgba(111, 111, 111, 0.83); color: white">
        </div>
        <div id="payment-qr">
        </div>
        <div></div>
       <%-- <button id="address-copy" class="btn address-copy" hidden><loc:message code="refill.copy"/></button>--%>
      </div>
      <div class="modal-footer">
        <div id='request-money-operation-btns-wrapper'
             class="add__money__btns">
          <button id="continue-btn"
                  class="btn btn-primary btn-md" type="button"><loc:message code="merchants.continue"/></button>
          <button class="btn btn-danger btn-md" type="button" data-dismiss="modal"><loc:message
                  code="merchants.dismiss"/></button>
        </div>
        <div id="response-money-operation-btns-wrapper">
          <button class="btn btn-danger btn-md" type="button" data-dismiss="modal"><loc:message
                  code="merchants.close"/></button>
        </div>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div>
<!-- /.modal -->
