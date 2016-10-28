<div>
    <h4><loc:message code="merchants.outputTitle"/></h4>
    <c:if test="${error!=null}">
        <label class="alert-danger has-error">
            <loc:message code="${error}"/>
        </label>
    </c:if>
    <c:choose>
        <c:when test="${empty merchantCurrencyData}">
            <loc:message code="merchants.noWallet"/>
        </c:when>
        <c:otherwise>
            <div class="row">
                    <form:form id="payment" class="form-horizontal withdraw__money" name="payment" method="post"
                                      modelAttribute="payment" action="/merchants/payment/withdraw">
                        <div class="input-block-wrapper clearfix" >
                                <%--Currency to withdraw--%>
                            <div class="col-md-4 input-block-wrapper__label-wrapper" style="width:225px">
                                <label style="font-size: 15px" for="currencyFull" class="input-block-wrapper__label" ><loc:message code="merchants.currencyforoutput"/></label>
                            </div>

                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                <input id="currency" name="currency" hidden="true" value="${currency.getId()}" />
                                <input id="currencyName" name="currencyName" hidden="true" value="${currency.getName()}" />
                                <input class="form-control input-block-wrapper__input"
                                       style="float: left; width: auto" id="currencyFull" readonly="true" value="<c:out value='${wallet.name} ${wallet.activeBalance}'/>" />
                            </div>
                        </div>
                        <div class="input-block-wrapper clearfix" >
                            <div class="col-md-4 input-block-wrapper__label-wrapper" style="width:225px">
                                <label style="font-size: 15px" for="sum"><loc:message code="withdrawal.amount"/></label>
                            </div>
                            <div style="width: auto; " class="col-md-8 input-block-wrapper__input-wrapper">
                                <form:input class="form-control input-block-wrapper__input numericInputField"
                                          id="sum" path="sum" />
                            </div>
                            <div class="col-md-6 input-block-wrapper__label-wrapper">
                                <div id="min-sum-notification" class="red"><loc:message code="mercnahts.output.minSum"/>
                                    <strong> ${currency.name} <span><fmt:formatNumber value="${currency.minWithdrawSum}" pattern="###,##0.00######"/></span>
                                    </strong></div>
                            </div>
                        </div>

                        <b hidden id="buttonMessage"><loc:message code="merchants.withdraw" /></b>
                        <div id="merchantList">
                            <br>
                            <c:forEach var="merchantCurrency" items="${merchantCurrencyData}" >
                                <c:forEach var="merchantImage" items="${merchantCurrency.listMerchantImage}" >
                                    <div style=" width: 700px; height: 48px; ">
                                        <div style="float: left; width: 408px; text-align: right; margin-right: 10px; ">
                                            <img class="img-thumbnail" src="${merchantImage.image_path}" style="width: 168px; height: 52px"/>

                                        </div>
                                        <button style="position: relative; top: 50%; -webkit-transform: translateY(-50%); -ms-transform: translateY(-50%); transform: translateY(-50%);" type="button" value="${merchantCurrency.merchantId}:${merchantCurrency.name}:${merchantCurrency.minSum}:${merchantImage.id}"  name="assertOutputPay"
                                                onclick="finPassCheck('myModal', submitMerchantsOutput)" class="btn btn-primary btn-lg"><loc:message code="merchants.withdraw"/></button>
                                    </div>
                                    <br>
                                </c:forEach>
                            </c:forEach>
                        </div>
                        <form:hidden path="operationType"/>
                        <form:hidden id="destination" path="destination"/>
                    </form:form>
            </div>
        </c:otherwise>
    </c:choose>
    <span hidden id="min-withdraw-sum">${currency.minWithdrawSum}</span>
</div>

<div class="modal fade" id="finPassModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel"><loc:message code="admin.finPassword"/></h4>
            </div>
            <div class="modal-body modal-content__input-block-wrapper">
                <div class="content modal-content__content-wrapper">
                    <c:url value="/checkfinpass" var="loginUrl"/>
                    <form id="submitFinPassForm" action="${loginUrl}" method="post" modelAttribute="user">
                        <%--логин--%>
                        <sec:authentication
                                property="principal.username" var="username"/>
                        <input type="text" readonly name="email" value="${username}"/>
                        <%--пароль--%>
                        <loc:message
                                code="admin.finPassword" var="finpassPlaceholder"/>
                        <input type="password" name="finpassword" placeholder="${finpassPlaceholder}"/>
                        <%--csrf--%>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <br/>
                        <%--отправить--%>
                        <button onclick="finPassCheck()" type="button" data-dismiss="modal" class="button_enter">
                            <loc:message code="admin.submitfinpassword"/></button>
                        <%--Забыли пароль?--%>
                        <a style="display:none" class="button_forgot" href="/forgotPassword"><loc:message
                                code="dashboard.forgotPassword"/></a>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<%--MODAL ... --%>
<div class="modal fade merchant-output" id="myModal">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title"><loc:message code="merchants.outputTitle"/></h4>
            </div>
            <div class="modal-body">
                <label class="alert-danger merchantError"><loc:message code="merchants.notAvaliablePayment"/></label>

                <div class="paymentInfo">
                    <p><loc:message code="merchants.modalOutputHeader"/></p>
                    <p><loc:message code="merchants.modalOutputCommission"/></p>
                    <p><loc:message code="merchants.modalOutputFinalSum"/></p>
                </div>
                <div class="wallet_input">
                    <label class="control-label" for="walletUid">
                        <loc:message code="merchants.modalOutputWallet"/>
                    </label>
                    <input class="form-control" autofocus="autofocus" name="walletUid" type="text" id="walletUid">
                </div>
            </div>
            <div class="modal-footer">
                <div class="add__money__btns request_money_operation_btn">
                    <button class="modal-button" type="button" data-dismiss="modal">
                        <loc:message code="merchants.dismiss"/>
                    </button>
                    <button class="modal-button" type="button" id="outputPaymentProcess" name="paymentOutput">
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
<%--... MODAL--%>