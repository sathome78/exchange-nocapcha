<div id="sms_connect_modal" class="modal fade order-info__modal modal-form-dialog" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-body">
                <div id="sms_connect_block" hidden>
                    <div style="border: 1px solid rgba(0, 0, 0, 0.29); border-radius: 4px; margin: 0 5px; padding: 10px">
                        <h5 class="modal-title"><loc:message code="message.sms.ConnectYourPhone"/></h5>
                    </div>
                    <hr>
                    <h5><loc:message code="message.sms.connectInstr"/></h5>
                    <div><loc:message code="message.telegram.messagePrice"/>:<span id="sms_mssg_price"></span> USD
                        <loc:message code="message.sms.forOneMessage"/></div>
                    <label for="sms_number_input"><loc:message code="message.sms.phoneNumber"/></label>
                    <input id="sms_number_input"/>
                    <button id='sms_check_number' class="btn btn-default"><loc:message code="message.sms.numberCheck"/></button>
                    <br>
                    <div id="sms_instruction" hidden><loc:message code="message.sms.afterCheckInstruction"/></div>
                    <button id='sms_connect_button' disabled class="btn btn-default"><loc:message code="message.sms.connect"/></button>
                    <button class="btn btn-default" type="button" data-dismiss="modal"><loc:message code="admin.cancel"/></button>
                    <button id='sms_enter_code_button' class="btn btn-default"><loc:message code="message.sms.enterCode"/></button>
                </div>

                <div id="sms_code_block" hidden>
                    <div style="border: 1px solid rgba(0, 0, 0, 0.29); border-radius: 4px; margin: 0 5px; padding: 10px">
                        <h5 class="modal-title"><loc:message code="message.sms.EnterCodeFromSms"/></h5>
                    </div>
                    <hr>
                    <label for="sms_code_input"><loc:message code="message.sms.code"/></label>
                    <input id="sms_code_input"/>
                    <br>
                    <button id='sms_send_code_button' disabled class="btn btn-default"><loc:message code="orderinfo.ok"/></button>
                    <button class="btn btn-default" type="button" data-dismiss="modal"><loc:message code="admin.cancel"/></button>
                </div>

                <div id="sms_info_block" hidden>
                    <div style="border: 1px solid rgba(0, 0, 0, 0.29); border-radius: 4px; margin: 0 5px; padding: 10px">
                        <h5 class="modal-title"><loc:message code="message.sms.ConnectedNumber"/></h5>
                    </div>
                    <hr>
                    <div><loc:message code="message.sms.phoneNumber"/>: <span id="sms_number"></span></div>
                    <div><loc:message code="message.sms.forOneMessage"/>: <span id="sms_price"></span>USD</div>
                    <br>
                    <button class="btn btn-default" type="button" data-dismiss="modal"><loc:message code="admin.cancel"/></button>
                </div>
                <p id="phone_error" style="color: red"></p>
            </div>
        </div>
    </div>
</div>