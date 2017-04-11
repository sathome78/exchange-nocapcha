<div id="merchants-input-center">
  <%--Deposit--%>
  <h4><loc:message code="merchants.inputTitle"/></h4>
  <label class="alert-danger has-error">
    <c:if test="${not empty error}">
      <loc:message code="${error}"/>
    </c:if>
  </label>
  <c:choose>
    <c:when test="${empty merchantCurrencyData}">
      <p class="red noMerchants"><loc:message code="merchant.operationNotAvailable"/></p>
    </c:when>
    <c:otherwise>
      <div class="row">
        <div hidden id="operationType">${payment.operationType}</div>
        <div class="form-horizontal refill__money">
          <div class="input-block-wrapper clearfix">
            <div class="col-md-4 input-block-wrapper__label-wrapper" style="width:225px">
              <label style="font-size: 15px" for="currencyName" class="input-block-wrapper__label"><loc:message
                      code="merchants.inputCurrency"/></label>
            </div>
            <div class="col-md-8 input-block-wrapper__input-wrapper">
              <input id="currencyName"
                     style="float: left; width: auto"
                     class="form-control input-block-wrapper__input"
                     readonly
                     value="${currencyName}"/>
            </div>

          </div>
          <div class="input-block-wrapper clearfix">
            <div class="col-md-4 input-block-wrapper__label-wrapper" style="width:225px">
              <label style="font-size: 15px" for="sum"><loc:message code="withdrawal.amount"/></label>
            </div>
            <div style="width: auto; " class="col-md-8 input-block-wrapper__input-wrapper">
              <input id="sum"
                     class="form-control input-block-wrapper__input numericInputField"
                     data-currency-name="${currency.name}"
                     data-max-amount="${balance}" <%--для USER_TRANSFER другое значение: ищи #maxForTransfer--%>
                     data-min-amount="${minWithdrawSum}"
                     data-min-sum-noty-id="#min-sum-notification"
                     data-submit-button-id="#button"/>
            </div>
            <div class="col-md-6 input-block-wrapper__label-wrapper">
              <div id="min-sum-notification" class="red"><loc:message code="merchants.input.minSum"/>
                <strong> ${currencyName} <span><fmt:formatNumber value="${minAmount}"
                                                                 pattern="###,##0.00######"/></span>
                </strong></div>
            </div>
          </div>
          <b hidden id="buttonMessage"><loc:message code="merchants.deposit"/></b>
          <div id="merchantList">
            <br>
            <c:forEach var="merchantCurrency" items="${merchantCurrencyData}">
              <c:forEach var="merchantImage" items="${merchantCurrency.listMerchantImage}">
                <div style=" width: 700px; height: 48px; ">
                  <div style="float: left; width: 408px; text-align: right; margin-right: 10px; ">
                    <img class="img-thumbnail" src="${merchantImage.image_path}" style="width: 168px; height: 52px"/>

                  </div>
                  <button style="position: relative; top: 50%; -webkit-transform: translateY(-50%); -ms-transform: translateY(-50%); transform: translateY(-50%);"
                          class="refill-withdraw btn btn-primary btn-lg"
                          type="button"
                          data-currency-id="${currency.getId()}"
                          data-currency-name="${currency.getName()}"
                          data-merchant-id="${merchantCurrency.merchantId}"
                          data-merchant-name="${merchantCurrency.name}"
                          data-merchant-min-sum="${merchantCurrency.minSum}"
                          data-simple-invoice="${merchantCurrency.simpleInvoice}"
                          data-merchant-image-d="${merchantImage.id}"><loc:message code="merchants.deposit"/></button>
                </div>
                <br>
              </c:forEach>
            </c:forEach>
          </div>
        </div>
      </div>
    </c:otherwise>
  </c:choose>
    <%@include file="modal/check_fin_pass_modal.jsp" %>
    <%@include file="modal/dialogRefillCreation_modal.jsp" %>
</div>
<c:if test="${not empty warningSingleAddress}">
  <div class="row inout-warning">
    <strong><loc:message code="${warningSingleAddress}"/></strong>
  </div>
</c:if>
<%--MODAL ... --%>
<%--
<div class="modal fade merchant-input" id="myModal">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" id="assertInputPayment" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title"><loc:message code="merchants.inputTitle"/></h4>
            </div>
            <div class="modal-body">
                <label class="alert-danger merchantError"><loc:message code="merchants.notAvaliablePayment"/></label>
                <div class="paymentInfo">
                    <p><loc:message code="merchants.modalInputHeader"/></p>
                    <p><loc:message code="merchants.modalInputCommission"/></p>
                    <p><loc:message code="merchants.modalInputFinalSum"/></p>
                    <p><loc:message code="merchants.warn"/></p>
                </div>
                <c:if test="${not empty warningCodeTimeout}">
                    <div class="timeoutWarning">
                        <strong><loc:message code="${warningCodeTimeout}"/></strong>
                    </div>
                </c:if>
                <div class="timeoutWarning">

                </div>

                <div class="paymentQR">

                </div>
            </div>
            <div class="modal-footer">
                <div class="add__money__btns request_money_operation_btn">
                    <input type="hidden" id="mrcht-waiting" value="<loc:message code="merchants.waiting"/>">
                    <input type="hidden" id="mrcht-ready" value="<loc:message code="merchants.continue"/>">

                    <button class="modal-button" type="button" data-dismiss="modal"><loc:message code="merchants.dismiss"/></button>
                    <button class="modal-button" type="button" id="inputPaymentProcess" ><loc:message code="merchants.continue"/></button>
                </div>
                <div class="response_money_operation_btn">
                    <button class="modal-button" type="button" data-dismiss="modal"><loc:message code="merchants.close"/></button>
                </div>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
--%>


<%--... MODAL--%>