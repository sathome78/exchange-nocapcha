<div id="dialog-refill-accept" class="modal fade form_full_width">
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
              <label for="initial-amount" class="input-block-wrapper__label"><loc:message code="admin.invoice.initialAmount"/></label>
            </div>
            <div class="col-md-8 input-block-wrapper__input-wrapper">
              <input id="initial-amount" readonly disabled class="input-block-wrapper__input" type="number" step="0.01">
            </div>
          </div>
          <div class="input-block-wrapper">
            <div class="col-md-4 input-block-wrapper__label-wrapper">
              <label for="actual-amount" class="input-block-wrapper__label"><loc:message code="admin.invoice.actualAmount"/></label>
            </div>
            <div class="col-md-4 input-block-wrapper__input-wrapper">
              <input id="actual-amount" class="input-block-wrapper__input" type="number" step="0.01">
            </div>
            <div class="col-md-4 input-block-wrapper__input-wrapper">
              <button type="button" id="computeCommission" class="btn btn-sm btn-danger"><loc:message code="admin.invoice.computeCommission"/></button>
            </div>
          </div>
          <div class="input-block-wrapper">
            <div class="col-md-4 input-block-wrapper__label-wrapper">
              <label for="new-commission" class="input-block-wrapper__label"><loc:message code="transaction.commissionAmount"/></label>
            </div>
            <div class="col-md-8 input-block-wrapper__input-wrapper">
              <input id="new-commission" readonly disabled class="input-block-wrapper__input" type="number" step="0.01">
            </div>
          </div>
          <input hidden id="transactionId" name="id">
          <input hidden id="actualPaymentSum" name="actualPaymentSum">
          <div class="table-button-block" style="white-space: nowrap; margin-top: 20px">
            <button id="confirm-button" class="blue-box" type="button"><loc:message code="admin.submit"/></button>
            <button id="cancel-acceptance" class="red-box" type="button"><loc:message code="admin.cancel"/></button>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>