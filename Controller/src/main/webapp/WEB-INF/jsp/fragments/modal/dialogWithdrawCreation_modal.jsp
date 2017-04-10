<div class="modal fade merchant-output" id="dialog-withdraw-creation">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title"><loc:message code="merchants.outputTitle"/></h4>
            </div>
            <div class="modal-body">
                <label class="alert-danger merchantError"><loc:message code="merchants.notAvaliablePayment"/></label>
                <div class="paymentInfo">
                    <p><loc:message code="merchants.modalOutputHeader"/></p>
                    <p><loc:message code="merchants.modalOutputCommission"/></p>
                    <p><loc:message code="merchants.modalOutputFinalSum"/></p>
                </div>
                <div class="wallet_input">
                    <label class="control-label" for="walletUid">
                        <loc:message code="merchants.modalOutputWallet"/>
                    </label>
                    <input class="form-control" autofocus name="walletUid" type="text" id="walletUid">
                </div>
            </div>
            <div class="modal-footer">
                <div class="add__money__btns request_money_operation_btn">
                    <button class="modal-button" type="button" data-dismiss="modal">
                        <loc:message code="merchants.dismiss"/>
                    </button>
                    <button class="modal-button" type="button" id="outputPaymentProcess" name="paymentOutput">
                        <loc:message code="merchants.continue"/>
                    </button>
                </div>
                <div class="response_money_operation_btn">
                    <button class="modal-button" type="button" data-dismiss="modal"><loc:message code="merchants.close"/></button>
                </div>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>