<%--
  User: Valk
--%>
<div id="order-delete-confirm__modal" class="modal fade modal-form-dialog" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="deleteorder.text"/></h4>
            </div>
            <div class="modal-body order-delete-confirm">
                <div class="clearfix">
                    <form id="order-delete-confirm__form" action="" method="post">
                        <div class="input-block-wrapper clearfix">
                            <div class="col-md-5 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="orders.type"/>
                                </label>
                            </div>
                            <div class="col-md-7 input-block-wrapper__input-wrapper">
                                <input id="operationTypeName" name="operationTypeName"
                                       readonly="true"
                                       autocomplete="off"
                                       class="form-control input-block-wrapper__input"/>
                            </div>
                        </div>
                        <div class="input-block-wrapper clearfix">
                            <div class="col-md-5 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="orders.currencypair"/>
                                </label>
                            </div>
                            <div class="col-md-7 input-block-wrapper__input-wrapper">
                                <input id="currencyPairName" name="currencyPairName"
                                       readonly="true"
                                       autocomplete="off"
                                       class="form-control input-block-wrapper__input"/>
                            </div>
                        </div>
                        <div class="input-block-wrapper clearfix">
                            <div class="col-md-5 input-block-wrapper__label-wrapper">
                                <label id="amountSellLabel" class="input-block-wrapper__label">
                                    <loc:message code="orders.amount"/>
                                </label>
                            </div>
                            <div class="col-md-7 input-block-wrapper__input-wrapper">
                                <input id="amount" name="amount"
                                       readonly="true"
                                       class="form-control input-block-wrapper__input numericInputField"/>
                            </div>
                        </div>
                        <div class="input-block-wrapper clearfix">
                            <div class="col-md-5 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="order.rate"/>
                                </label>
                            </div>
                            <div class="col-md-7 input-block-wrapper__input-wrapper">
                                <input id="exrate" name="exrate"
                                       readonly="true"
                                       class="form-control input-block-wrapper__input numericInputField"/>
                            </div>
                        </div>
                        <div class="input-block-wrapper clearfix">
                            <div class="col-md-5 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="order.total"/>
                                </label>
                            </div>
                            <div class="col-md-7 input-block-wrapper__input-wrapper">
                                <input id="total" name="total"
                                       readonly="true"
                                       class="form-control input-block-wrapper__input numericInputField"/>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <div class="modal-footer">
                <div class="order-delete-confirm__button-wrapper">
                    <button id="order-delete-confirm__submit" class="order-delete-confirm__button">
                        <loc:message code="deleteorder.submit"/>
                    </button>
                    <button class="order-delete-confirm__button" data-dismiss="modal">
                        <loc:message code="submitorder.cancell"/>
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
