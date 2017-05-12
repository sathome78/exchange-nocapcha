<div id="merchants-input-center">
  <div hidden>
    <div id="bank-not-selected"><loc:message code="merchants.notSelected"/></div>
  </div>
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
                     value="${currency.name}"/>
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
                     data-min-amount="${minRefillSum}"
                     data-scale-of-amount="${scaleForCurrency}"
                     data-min-sum-noty-id="#min-sum-notification"
                     data-submit-button-id=".start-refill"/>
            </div>
            <div class="col-md-6 input-block-wrapper__label-wrapper">
              <div id="min-sum-notification" class="red"><loc:message code="merchants.input.minSum"/>
                <strong> ${currency.name} <span><fmt:formatNumber value="${minRefillSum}"
                                                                  pattern="###,##0.00######"/></span>
                </strong></div>
            </div>
          </div>
          <b hidden id="buttonMessage"><loc:message code="merchants.deposit"/></b>
          <div id="merchantList">
            <br>
            <c:forEach var="merchantCurrency" items="${merchantCurrencyData}">
              <c:forEach var="merchantImage" items="${merchantCurrency.listMerchantImage}">
                <div style=" width: 700px; min-height: 88px; border: 1px solid #d5d5d5; padding: 10px; border-radius: 10px">
                  <div style="float: left; height: 20px;  width: 208px; text-align: right; margin-right: 10px">
                    <img class="img-thumbnail" src="${merchantImage.image_path}"
                         style="width: 168px; height: 52px; margin-right: 35px"/>
                    <div style="float: left; height: 20px;  width: 208px; text-align: left; margin-right: 10px; padding-left: 10px">
                      <c:if test="${(merchantCurrency.minSum > 0) && (merchantCurrency.processType != \"INVOICE\")}">
                        <span><loc:message code="merchants.input.minSum"/></span>
                        <span>${merchantCurrency.minSum.stripTrailingZeros().toPlainString()}</span>
                      </c:if>
                    </div>
                  </div>
                  <c:choose>
                    <c:when test="${empty merchantCurrency.address}">
                      <button class="start-refill btn btn-primary btn-lg"
                              type="button"
                              data-currency-id="${currency.getId()}"
                              data-currency-name="${currency.getName()}"
                              data-merchant-id="${merchantCurrency.merchantId}"
                              data-merchant-name="${merchantCurrency.name}"
                              data-merchant-min-sum="${merchantCurrency.minSum}"
                              data-process_type="${merchantCurrency.processType}"
                              data-merchant-image-d="${merchantImage.id}"><loc:message code="merchants.deposit"/>
                      </button>
                    </c:when>
                    <c:otherwise>
                      <div style="display: inline-block; ">
                        <div>
                          <loc:message code="refill.messageAboutCurrentAddressSimple"/>
                          <div id="address-to-pay" style="font-size:16px">
                            ${merchantCurrency.address}
                          </div>
                          <div>
                            <img src='https://chart.googleapis.com/chart?chs=100x100&chld=L|2&cht=qr&chl=<c:out value="${merchantCurrency.address}"/>'/>
                          </div>
                          <button id=address-copy class="btn" style="padding: 0 20px"><loc:message
                                  code="refill.copy"/></button>
                          <button id=address-generate class="btn start-refill" style="padding: 0 20px"
                                  data-currency-id="${currency.getId()}"
                                  data-currency-name="${currency.getName()}"
                                  data-merchant-id="${merchantCurrency.merchantId}"
                                  data-merchant-name="${merchantCurrency.name}"
                                  data-merchant-min-sum="${merchantCurrency.minSum}"
                                  data-process_type="${merchantCurrency.processType}"
                                  data-merchant-image-d="${merchantImage.id}"><loc:message
                                  code="refill.generate"/></button>
                        </div>
                      </div>
                    </c:otherwise>
                  </c:choose>
                </div>
                <br>
              </c:forEach>
            </c:forEach>
          </div>
        </div>
      </div>
    </c:otherwise>
  </c:choose>
  <%@include file="modal/loading_modal.jsp" %>
  <%@include file="modal/dialogRefillCreation_modal.jsp" %>
  <%@include file="modal/dialogRefillDetailedParamsEnter_modal.jsp" %>
</div>
<c:if test="${not empty warningSingleAddress}">
  <div class="row inout-warning">
    <strong><loc:message code="${warningSingleAddress}"/></strong>
  </div>
</c:if>
