<div class="col-sm-9 content">
    <h4><loc:message code="merchants.outputTitle"/></h4>
    <c:if test="${error!=null}">
        <label class="alert-danger has-error">
            <loc:message code="${error}"/>
        </label>
    </c:if>
    <c:choose>
        <c:when test="${empty wallets}">
            <loc:message code="merchants.noWallet"/>
        </c:when>
        <c:otherwise>
            <div class="row">
                <div class="col-sm-9" style="margin-bottom: 15px;">
                    <form:form id="payment" class="form-horizontal withdraw__money" name="payment" method="post"
                                      modelAttribute="payment" action="/merchants/payment/withdraw">
                        <div class="input-block-wrapper clearfix" >
                                <%--Currency to withdraw--%>
                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                <label style="font-size: 15px" class="input-block-wrapper__label" ><loc:message code="merchants.currencyforoutput"/></label>
                            </div>
                            <div class="col-md-8 input-block-wrapper__input-wrapper" style="margin-bottom: 15px; height: auto">
                                <select name="currency" id="currency" class="form-control">
                                    <c:forEach items="${wallets}" var="wallet">
                                        <option data-currency="${wallet.name}" value='<c:out value="${wallet.currencyId}"/>'<c:if test="${wallet.currencyId eq currentCurrency.getId()}">SELECTED</c:if>>
                                            <c:out value="${wallet.name}"/>
                                            <c:out value="${wallet.activeBalance}"/>
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-4 input-block-wrapper__label-wrapper" >
                            <label for="merchant" style="font-size: 15px" class="input-block-wrapper__label" ><loc:message code="merchants.meansOfPayment"/></label>
                        </div>
                        <div class="col-md-8 input-block-wrapper__input-wrapper" style="margin-bottom: 15px; height: auto">
                            <form:select id="merchant" path="merchant" class="form-control"/>
                        </div>

                        <div class="col-md-4 input-block-wrapper__label-wrapper" >
                            <label style="font-size: 15px"><loc:message code="merchants.sum"/></label>
                        </div>
                        <div class="col-md-8 input-block-wrapper__input-wrapper" style="margin-bottom: 15px; height: auto">
                            <form:input class="form-control input-block-wrapper__input numericInputField"
                                        id="sum" path="sum"/>
                        </div>
                        <div style="text-align: center;">
                            <form:hidden path="operationType"/>
                            <form:hidden id="destination" path="destination"/>
                                <%--Withdraw--%>
                            <div class="col-md-8 input-block-wrapper__input-wrapper" style="height: auto; float: right">
                                <button onclick="finPassCheck('myModal', submitMerchantsOutput)" type="button" id="assertOutputPay"
                                <%--<button type="button" id="assertOutputPay" data-toggle="modal" data-target="#myModal"--%>
                                        class="btn btn-primary">
                                    <loc:message code="merchants.withdraw"/>
                                </button>
                            </div>

                        </div>
                    </form:form>
                </div>
                <div class="col-sm-3"></div>
            </div>
        </c:otherwise>
    </c:choose>
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