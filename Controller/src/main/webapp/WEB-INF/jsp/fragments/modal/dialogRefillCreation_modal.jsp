<div class="modal fade merchant-input" id="dialog-refill_creation">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" id="assertInputPayment" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title"><loc:message code="merchants.inputTitle"/></h4>
            </div>
            <div class="modal-body">
                <label class="alert-danger merchantError"><loc:message code="merchants.notAvaliablePayment"/></label>
                <div class="paymentInfo">
                    <p><loc:message code="merchants.modalInputHeader"/></p>
                    <p><loc:message code="merchants.modalInputCommission"/></p>
                    <p><loc:message code="merchants.modalInputFinalSum"/></p>
                    <p><loc:message code="merchants.warn"/></p>
                </div>
                <c:if test="${not empty warningCodeTimeout}">
                    <div class="timeoutWarning">
                        <strong><loc:message code="${warningCodeTimeout}"/></strong>
                    </div>
                </c:if>
                <div class="timeoutWarning">
                    <c:if test="${not empty warningCodeTimeout}">
                            <strong><loc:message code="${warningCodeTimeout}"/></strong>
                    </c:if>
                </div>

                <div class="paymentQR">

                </div>
            </div>
            <div class="modal-footer">
                <div class="add__money__btns request_money_operation_btn">
                    <button class="modal-button" type="button" id="inputPaymentProcess" ><loc:message code="merchants.continue"/></button>
                    <button class="modal-button" type="button" data-dismiss="modal"><loc:message code="merchants.dismiss"/></button>
                </div>
                <div class="response_money_operation_btn">
                    <button class="modal-button" type="button" data-dismiss="modal"><loc:message code="merchants.close"/></button>
                </div>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
