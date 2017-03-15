<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 18.01.2017
  Time: 9:34
  To change this template use File | Settings | File Templates.
--%>
<div>
    <h4><loc:message code="wallets.transferTitle"/></h4>

    <label class="alert-danger has-error">
        <c:if test="${not empty error}">
            <loc:message code="${error}"/>
        </c:if>
    </label>
    <div class="row inout-warning">
        <strong><loc:message code="transfer.warning"/></strong>
    </div>
    <div class="row">
        <form class="form-horizontal withdraw__money" id="payment" method="post" action="">
            <div class="input-block-wrapper clearfix">
                <div class="col-md-4 input-block-wrapper__label-wrapper" style="width:225px">
                    <label style="font-size: 15px" for="currencyName" class="input-block-wrapper__label" ><loc:message code="wallets.transferCurrency"/></label>
                </div>
                <div class="col-md-8 input-block-wrapper__input-wrapper" >
                    <input id="currencyId" name="currencyId" hidden="true" value="${currency.id}" />
                    <input id="currencyName" name="currencyName" hidden="true" value="${currency.name}" />
                    <input style="float: left; width: auto"  class="form-control input-block-wrapper__input"
                           id="currencyFull" readonly="true" value="${currency.name} ${wallet.activeBalance}" />
                </div>

            </div>

            <div class="input-block-wrapper clearfix">
                <div class="col-md-4 input-block-wrapper__label-wrapper" style="width:225px">
                    <label style="font-size: 15px" for="sum"><loc:message code="withdrawal.amount"/></label>
                </div>
                <div style="width: auto; " class="col-md-8 input-block-wrapper__input-wrapper">
                    <input class="form-control input-block-wrapper__input numericInputField"
                                id="sum" name="amount" />
                </div>
                <div class="col-md-6 input-block-wrapper__label-wrapper">
                    <div id="min-sum-notification" class="red"><loc:message code="merchants.transfer.minSum"/>
                        <strong> ${currency.name} <span><fmt:formatNumber value="${minAmount}" pattern="###,##0.00######"/></span>
                        </strong></div>
                </div>
            </div>
            <input hidden id="walletId" name="walletId" value="${wallet.id}" />
            <input hidden id="nickname" name="nickname" />
            <input hidden id="operationType" value="USER_TRANSFER" />

            <div class="col-md-4 input-block-wrapper">
                <button id="transferButton" type="button" class="btn btn-primary btn-lg">
                    <loc:message code="mywallets.transfer"/></button>
            </div>

        </form>
    </div>
    <span hidden id="maxForTransfer">${maxForTransfer}</span>
    <span hidden id="minAmount">${minAmount}</span>
</div>

<%@include file="modal/check_fin_pass_modal.jsp"%>
<div class="modal fade merchant-output" id="transferModal">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title"><loc:message code="wallets.transferTitle"/></h4>
            </div>
            <div class="modal-body">
                <label class="alert-danger merchantError"><loc:message code="merchants.notAvaliablePayment"/></label>
                <div class="paymentInfo">
                    <p><loc:message code="wallets.modalTransferHeader"/></p>
                    <p><loc:message code="wallets.modalTransferCommission"/></p>
                    <p><loc:message code="wallets.modalTransferFinalSum"/></p>
                </div>
                <div class="nickname_input">
                    <label class="control-label" for="nicknameInput">
                        <loc:message code="transfer.nickname"/>
                    </label>
                    <input class="form-control" autofocus="autofocus" type="text" id="nicknameInput">
                </div>
            </div>
            <div class="modal-footer">
                <div class="add__money__btns request_money_operation_btn">
                    <button class="modal-button" type="button" data-dismiss="modal">
                        <loc:message code="merchants.dismiss"/>
                    </button>
                    <button class="modal-button" type="button" id="transferProcess">
                        <loc:message code="merchants.continue"/>
                    </button>
                </div>
                <div class="response_money_operation_btn">
                    <button class="modal-button" type="button" data-dismiss="modal"><loc:message code="merchants.close"/></button>
                </div>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
