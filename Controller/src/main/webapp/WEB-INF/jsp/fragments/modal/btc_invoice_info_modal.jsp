<div class="modal fade comment" id="btc-invoice-info-modal">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h4 class="modal-title" id="user_info"></h4>
      </div>
      <div class="modal-body">
        <input type="text" hidden id="invoiceId"/>
        <%----%>
        <label for="address-to-pay">
          <loc:message code="merchants.btc_invoice.addressToPay"/>:
        </label>
        <input class="form-control"
               id="address-to-pay"
               readonly>
        </input>
        <%----%>
        <label for="btc-transaction">
          <loc:message code="merchants.btc_invoice."/>:
        </label>
        <input class="form-control"
               id="btc-transaction"
               readonly>
        </input>
      </div>
      <div class="modal-footer">
        <div>
          <button class="btn btn-info" type="button" data-dismiss="modal">
            <loc:message code="admin.return"/>
          </button>
        </div>
      </div>
    </div>
    <!-- /.modal-content -->
  </div>
  <!-- /.modal-dialog -->
</div>