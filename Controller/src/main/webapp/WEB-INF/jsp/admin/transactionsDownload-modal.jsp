<div id="transactions_download_modal" class="modal fade delete-order-info__modal" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="admin.user.transactions.downloadHistory"/></h4>
            </div>
            <div class="modal-body delete-order-info">
                <form id="transactions_history_download_form" class="form_auto_height" method="get">
                    <div class="delete-order-info__item error error-search"><loc:message
                            code="admin.user.transactions.downloadHistory.choosePeriod"/>
                        <span>
                            <input id="trans_download_start" type="text" name="startDate">
                            <input id="trans_download_end" type="text" name="endDate">
                        </span>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <div class="delete-order-info__button-wrapper">
                    <button class="delete-order-info__button" id="download_trans_history_button" data-dismiss="modal"><loc:message
                            code="admin.user.transactions.downloadHistory"/></button>
                    <button class="delete-order-info__button" data-dismiss="modal"><loc:message
                            code="submitorder.cancell"/></button>
                </div>
            </div>
        </div>
    </div>
</div>