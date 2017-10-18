<div id="telegram_connect_modal" class="modal fade order-info__modal modal-form-dialog" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-body">
                <div style="border: 1px solid rgba(0, 0, 0, 0.29); border-radius: 4px; margin: 0 5px; padding: 10px">
                    <h5 class="modal-title"><loc:message code="message.attention"/></h5>
                </div>
                <hr>
                <h5 id="telegram_reconnect_block" hidden>
                    <loc:message code="message.telegram.reconnectInstr"/>
                </h5>
                <div id="telegram_connect_block">
                    <h5><loc:message code="message.telegram.connectInstr"/></h5>
                    <div><loc:message code="message.telegram.subscriptionPrice"/>:<span id="telegram_subscr_price"></span> USD</div>
                    <div><loc:message code="message.telegram.messagePrice"/>:<span id="telegram_mssg_price"></span> USD</div>
                </div>
                <div class="code" style="font-size: 14px" hidden><loc:message code="action.button.SHOW_CODE_BUTTON"/>: <span id="telegram_code"></span></div>
                <button id='telegram_pay_button' class="btn btn-default"><loc:message code="telegram.payAndGetTheSubscriptionCode"/></button>
                <button class="btn btn-default" type="button" data-dismiss="modal"><loc:message code="admin.cancel"/></button>
            </div>
        </div>
    </div>
</div>