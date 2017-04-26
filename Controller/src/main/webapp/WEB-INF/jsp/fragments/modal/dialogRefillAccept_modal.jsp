<script>
  var $dialog;
  $(function () {
    $dialog = $("#dialog-refill-accept");
    $(".credits-operation-enter__item").on("change input", function (event) {
      var $elem = $(event.currentTarget);
      var checkResult = checkField($elem);
      if (checkResult) {
        $('#confirm-button').prop('disabled', false);
      } else {
        $('#confirm-button').prop('disabled', true);
      }
    });
  });

  function checkAllFields() {
    result = true;
    [].forEach.call($dialog.find(".credits-operation-enter__item"), function (item) {
      result = checkField($(item)) && result;
    });
    if (result) {
      $('#confirm-button').prop('disabled', false);
    } else {
      $('#confirm-button').prop('disabled', true);
    }
  }

  function checkField($elem) {
    const NAME_REGEX = /^\d+\.?\d*$/;
    result = validateString($elem, NAME_REGEX, '', false);
    return result;
  }

</script>
<div id="dialog-refill-accept" class="modal fade merchant-output">
  <div style="font-size: 14px"
       class="modal-dialog modal-md">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title"><loc:message code="merchants.invoice.accept.modalTitle"/></h4>
      </div>
      <div class="modal-body">
        <div>
          <label class="control-label" for="initial-amount">
            <loc:message code="admin.invoice.initialAmount"/>
          </label>
          <span id="initial-amount" class="form-control"></span>
        </div>
        <div>
          <label class="control-label" for="actual-amount">
            <loc:message code="admin.invoice.actualAmount"/>
          </label>
          <input id="actual-amount"
                 class="form-control credits-operation-enter__item"
                 autofocus
                 type="text">
        </div>
      </div>
      <div class="modal-footer">
        <div id='request-money-operation-btns-wrapper'
             class="add__money__btns">
          <button id="confirm-button"
                  class="btn btn-primary btn-md" type="button">
            <loc:message code="merchants.continue"/>
          </button>
          <button class="btn btn-danger btn-md" type="button" data-dismiss="modal">
            <loc:message code="merchants.dismiss"/>
          </button>
        </div>
      </div>
    </div>
    <!-- /.modal-content -->
  </div>
  <!-- /.modal-dialog -->
</div>