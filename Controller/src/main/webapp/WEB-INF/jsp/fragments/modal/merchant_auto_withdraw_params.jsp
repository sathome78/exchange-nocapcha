<div id="withdraw-auto-params" class="modal fade">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="merchant.withdrawAutoParams"/></h4>
            </div>
            <div class="modal-body">
                <form id="withdraw-auto-params-form" class="form_full_width form_auto_height">
                    <input type="hidden" name="merchantId" >
                    <input type="hidden" name="currencyId" >
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="auto-enabled" class="input-block-wrapper__label"><loc:message code="merchant.withdrawAuto"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="auto-enabled" name="withdrawAutoEnabled" class="input-block-wrapper__input" type="checkbox" value="true">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="auto-delay" class="input-block-wrapper__label"><loc:message code="merchant.withdrawAutoDelay"/></label>
                        </div>
                        <span class="input-block-wrapper__input-wrapper">
                            <input id="auto-delay" name="withdrawAutoDelaySeconds" class="input-block-wrapper__input" type="number">
                        </span>
                        <span class="input-block-wrapper__input-wrapper">
                            <select class="input-block-wrapper__input" id="timeUnit">
                            </select>
                        </span>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="auto-threshold" class="input-block-wrapper__label"><loc:message code="merchant.withdrawAutoThresholdExt"/></label>
                        </div>
                        <div class="input-block-wrapper__input-wrapper">
                            <input id="auto-threshold" name="withdrawAutoThresholdAmount" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <button id="submitAutoWithdrawParams" class="blue-box admin-form-submit" type="submit"><loc:message code="admin.refSubmitEditCommonRoot"/></button>
                </form>
            </div>
        </div>
    </div>
</div>