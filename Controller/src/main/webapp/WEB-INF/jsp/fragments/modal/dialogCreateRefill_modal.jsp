<div id="dialog-refill-accept" class="modal fade merchant-output">
    <div style="font-size: 14px"
         class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title"><loc:message code="merchants.invoice.accept.modalTitle"/></h4>
            </div>
            <div class="modal-body">
                <%--CURRENCY--%>
                <div class="input-block-wrapper">
                    <div class="col-md-3 input-block-wrapper__label-wrapper">
                        <label class="input-block-wrapper__label">
                            <loc:message code="transaction.currency" />
                        </label>
                    </div>
                    <div class="col-md-9 ">
                        <ul class="checkbox-grid">
                            <c:forEach items="${cryptoCurrencies}" var="currency">
                                <li><input type="checkbox" name="currencyIds" value="${currency.currencyId}"><span>${currency.currencyName}</span></li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
                    <div>
                        <label class="control-label" for="email">
                            <loc:message code="admin.email"/>
                        </label>
                        <input id="email"
                               class="form-control credits-operation-enter__item"
                               autofocus
                               type="text">
                    </div>
                <div>
                    <label class="control-label" for="amount">
                        <loc:message code="admin.invoice.actualAmount"/>
                    </label>
                    <input id="amount"
                           class="form-control credits-operation-enter__item"
                           autofocus
                           type="text">
                </div>
                <div>
                    <label class="control-label" for="merchant_transaction_id">
                        <loc:message code="refill.merchantTransactionId"/>
                    </label>
                    <input id="merchant_transaction_id"
                           class="form-control credits-operation-enter__item"
                           autofocus
                           type="text">
                </div>
                <hr>
                <div>
                    <label class="control-label" for="remark">
                        <loc:message code="merchants.invoiceDetails.remark"/>
                    </label>
                    <textarea id="remark"
                              class="form-control credits-operation-enter__item">
          </textarea>
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