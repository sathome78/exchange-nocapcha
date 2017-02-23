<script>
  function onSelectNewValue(select) {
    $('#commentText').val(select.value);
    $('#commentText').change();
  }
  function onChangeText() {
    if (!$('#commentText').val().trim()) {
      $("#createCommentConfirm").attr("disabled", true);
    } else {
      $("#createCommentConfirm").removeAttr("disabled");
    }
  }
</script>
<div class="modal fade comment" id="note-before-decline-modal">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h4 class="modal-title" id="user_info"></h4>
      </div>
      <div class="modal-body">
        <label for="phrase-template-list">
          <loc:message code="merchants.invoice.phrases.decline.cause"/>:
        </label>
        <textarea class="form-control" cols="40" rows="3"
                  id="commentText"
                  onkeyup="onChangeText()"
                  onchange="onChangeText()">
        </textarea>
        <%----%>
        <label for="phrase-template-list">
          <loc:message code="merchants.invoice.templatePhrases"/>
        </label>
        <select class="form-control"
                id="phrase-template-list"
                onchange="onSelectNewValue(this)">
        </select>
      </div>
      <div class="modal-footer">
        <div>
          <button class="btn btn-success" type="button" id="createCommentConfirm"
                  onmouseover="onChangeText()">
            <loc:message code="merchants.continue"/>
          </button>

          <button class="btn btn-default" type="button" id="createCommentCancel">
            <loc:message code="admin.cancel"/>
          </button>
        </div>
      </div>
    </div>
    <!-- /.modal-content -->
  </div>
  <!-- /.modal-dialog -->
</div>