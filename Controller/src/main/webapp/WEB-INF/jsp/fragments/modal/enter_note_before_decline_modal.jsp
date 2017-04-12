<script>
  $(function () {
    $('#note-before-decline-modal').on('shown.bs.modal', function (e) {
      var $textArea = $(e.currentTarget).closest("#note-before-decline-modal").find("#commentText");
      $textArea.val($textArea.val().trim());
    })
  });
  function onSelectNewValue(select) {
    var $textArea = $(select).closest("#note-before-decline-modal").find("#commentText");
    $textArea.val(select.value);
    $textArea.change();
  }

  function onChangeText(elem) {
    var $textArea = $(elem).closest("#note-before-decline-modal").find("#commentText");
    var $button = $(elem).closest("#note-before-decline-modal").find("#createCommentConfirm");
    if (!$textArea.val().trim()) {
      $button.attr("disabled", true);
    } else {
      $button.removeAttr("disabled");
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
          <loc:message code="admin.userLanguage"/>:
        </label>
        <input class="form-control"
               id="user-language"
               readonly>
        </input>
        <br>
        <%----%>
        <label for="phrase-template-list">
          <loc:message code="merchants.invoice.phrases.decline.cause"/>:
        </label>
        <textarea class="form-control" cols="40" rows="3"
                  id="commentText"
                  onkeyup="onChangeText(this)"
                  onchange="onChangeText(this)">
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
                  onmouseover="onChangeText(this)">
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