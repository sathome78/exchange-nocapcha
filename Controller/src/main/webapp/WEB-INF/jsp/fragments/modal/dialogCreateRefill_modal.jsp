<div id="dialog-refill-create" class="modal fade merchant-output">
    <div style="font-size: 14px"
         class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title"><loc:message code="message.create.refill.request"/></h4>
            </div>
            <div class="modal-body">
                <div class="input-block-wrapper">
                    <div class="col-md-6 input-block-wrapper__label-wrapper">
                        <label class="control-label" for="rc_currency_select">
                            <loc:message code="transaction.currency" />
                        </label>
                    </div>
                    <select name="currency" id="rc_currency_select" class="input-block-wrapper__input admin-form-input">
                        <c:forEach items="${cryptoCurrencies}" var="currency">
                            <option value="${currency.currencyId}"}>${currency.currencyName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="input-block-wrapper">
                    <div class="col-md-6 input-block-wrapper__label-wrapper">
                        <label class="control-label" for="rc_email">
                            <loc:message code="admin.email"/>
                        </label>
                    </div>
                    <input id="rc_email"
                           class="form-control rc_item credits-operation-enter__item"
                           autofocus
                           type="text">
                </div>
                <div class="input-block-wrapper">
                    <div class="col-md-6 input-block-wrapper__label-wrapper">
                        <label class="control-label" for="rc_amount">
                            <loc:message code="admin.invoice.actualAmount"/>
                        </label>
                    </div>
                    <input id="rc_amount"
                           class="form-control rc_item credits-operation-enter__item"
                           autofocus
                           type="text">
                </div>
                <div class="input-block-wrapper">
                    <div class="col-md-6 input-block-wrapper__label-wrapper">
                        <label class="control-label" for="rc_address">
                            <loc:message code="refill.address"/>
                        </label>
                    </div>
                    <input id="rc_address"
                           class="form-control rc_item credits-operation-enter__item"
                           autofocus
                           type="text">
                </div>
                <div class="input-block-wrapper">
                    <div class="col-md-6 input-block-wrapper__label-wrapper">
                        <label class="control-label" for="rc_merchant_transaction_id">
                            <loc:message code="refill.merchantTransactionId"/>
                        </label>
                    </div>
                    <input id="rc_merchant_transaction_id"
                           class="form-control credits-operation-enter__item"
                           autofocus
                           type="text">

                </div>
            </div>
            <div class="modal-footer">
                <div id='request-money-operation-btns-wrapper'
                     class="add__money__btns">
                    <button disabled id="refill_create_button"
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