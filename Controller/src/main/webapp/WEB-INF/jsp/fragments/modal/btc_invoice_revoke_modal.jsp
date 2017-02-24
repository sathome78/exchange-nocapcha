<div class="modal fade comment" id="btc-invoice-revoke-modal">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h4 class="modal-title" id="user_info"></h4>
      </div>
      <div class="modal-body">
        <input type="text" hidden id="invoiceId"/>
        <label for="address-to-pay">
          <loc:message code="merchants.btc_invoice.addressToPay"/>:
        </label>
        <input class="form-control"
               id="address-to-pay"
               readonly>
        </input>
      </div>
      <div class="modal-footer">
        <div>
          <button class="btn btn-success" type="button" id="btcInvoiceRevokeConfirm">
            <loc:message code="merchants.continue"/>
          </button>

          <button class="btn btn-default" type="button" data-dismiss="modal">
            <loc:message code="admin.cancel"/>
          </button>
        </div>
      </div>
    </div>
    <!-- /.modal-content -->
  </div>
  <!-- /.modal-dialog -->
</div>