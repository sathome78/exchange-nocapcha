<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 06.02.2017
  Time: 17:56
  To change this template use File | Settings | File Templates.
--%>
<div>
    <h4><loc:message code="merchants.invoiceConfirm.title"/></h4>
    <c:choose>
        <c:when test="${not empty error}">
            <label class="alert-danger has-error">
                <loc:message code="${error}"/>
            </label>
        </c:when>
        <c:otherwise>

            <div class="row">
                <div class="input-block-wrapper clearfix">
                    <div id="creditsOperationInfo" class="well col-md-6 col-md-offset-3 ">
                        <div class="text-center">
                            <h5><loc:message code="merchants.invoiceDetails.paymentDetails" /></h5>
                        </div>
                        <table class="table">
                            <tbody>
                            <tr>
                                <td><loc:message code="merchants.withdrawDetails.inputFinalSum"/></td>
                                <td><fmt:formatNumber value="${creditsOperation.amount.add(creditsOperation.commissionAmount)}"
                                                      pattern="###,##0.0#######"/> ${creditsOperation.currency.name}</td>
                            </tr>
                            <tr>
                                <td><loc:message code="transaction.commission"/></td>
                                <td><fmt:formatNumber value="${creditsOperation.commission.value}" pattern="###,##0.0#######"/></td>
                            </tr>
                            <tr>
                                <td><loc:message code="transaction.commissionAmount"/></td>
                                <td><fmt:formatNumber value="${creditsOperation.commissionAmount}" pattern="###,##0.0#######"/> ${creditsOperation.currency.name}</td>
                            </tr>

                            <tr>
                                <td><loc:message code="merchants.withdrawDetails.outputFinalSum"/> </td>
                                <td><fmt:formatNumber value="${creditsOperation.amount}" pattern="###,##0.00######"/> ${creditsOperation.currency.name}</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>


                <form id="confirmationForm" action="<c:url value="/merchants/invoice/withdraw/submit"/>" method="post">
                    <input type="hidden" name="invoiceId" id="invoiceId" value="${invoiceRequest.transaction.id}">
                    <input type="hidden" name="payerBankName" id="payerBankName" value="${invoiceRequest.payerBankName}">
                    <div class="input-block-wrapper clearfix">
                        <div class="col-md-3 input-block-wrapper__label-wrapper" >
                            <label for="bankSelect" class="input-block-wrapper__label" >
                                <loc:message code="merchants.invoiceConfirm.bankFrom"/>*</label>
                        </div>
                        <div class="col-md-8 " >
                            <select class="form-control input-block-wrapper__input" id="bankSelect">
                                <option value="-1"><loc:message code="merchants.notSelected"/></option>
                                <c:forEach items="${banks}" var="bank">
                                    <option  value="${bank.code}">${bank.name}</option>
                                </c:forEach>
                                <option value="0"><loc:message code="merchants.invoice.otherBank"/></option>
                            </select>
                        </div>
                    </div>
                    <div class="input-block-wrapper clearfix">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label for="otherBank" class="input-block-wrapper__label" ></label>
                        </div>
                        <div class="col-md-8 " >
                            <input class="form-control input-block-wrapper__input" type="text" id="otherBank" >
                        </div>
                        <div id="bankNameError" class="col-md-11 input-block-wrapper__error-wrapper">
                            <p class="red"><loc:message code="merchants.error.bankNameInLatin" /></p>
                        </div>
                    </div>
                    <div class="input-block-wrapper clearfix">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label for="bankCode" class="input-block-wrapper__label" ><loc:message code="invoice.bankCode" /></label>
                        </div>
                        <div class="col-md-8 " >
                            <input class="form-control input-block-wrapper__input" type="text" id="bankCode" name="payerBankCode" >
                        </div>
                        <div id="bankCodeError" class="col-md-11 input-block-wrapper__error-wrapper">
                            <p class="red"><loc:message code="invoice.bankCode.error" /></p>
                        </div>
                    </div>
                    <div class="input-block-wrapper clearfix">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label for="userAccount" class="input-block-wrapper__label" >
                                <loc:message code="merchants.invoiceConfirm.userAccount"/>*</label>
                        </div>
                        <div class="col-md-8 " >
                            <input class="form-control input-block-wrapper__input" type="text" id="userAccount"
                                   name="userAccount" >
                        </div>
                        <div id="userAccountError" class="col-md-11 input-block-wrapper__error-wrapper">
                            <p class="red"><loc:message code="merchants.error.accountDigitsOnly" /></p>
                        </div>
                    </div>
                    <div class="input-block-wrapper clearfix">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label for="userFullName" class="input-block-wrapper__label" >
                                <loc:message code="merchants.invoiceDetails.userFullName"/>*</label>
                        </div>
                        <div class="col-md-8 " >
                            <input class="form-control input-block-wrapper__input" type="text" id="userFullName"
                                   name="userFullName">
                        </div>
                        <div id="userFullNameError" class="col-md-11 input-block-wrapper__error-wrapper">
                            <p class="red"><loc:message code="merchants.error.fullNameInLatin" /></p>
                        </div>
                    </div>
                    <div class="input-block-wrapper clearfix">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label for="remark" class="input-block-wrapper__label" >
                                <loc:message code="merchants.invoiceDetails.remark"/></label>
                        </div>
                        <div class="col-md-8">
                            <textarea id="remark" class="form-control textarea non-resize" name="remark"></textarea>
                        </div>
                    </div>


                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                        <div class="col-md-4 input-block-wrapper">
                            <button id="invoiceSubmit" class="btn btn-primary btn-lg" type="submit"><loc:message code="admin.submit" /> </button>
                            <button id="invoiceCancel" class="btn btn-danger btn-lg" type="button"><loc:message code="admin.cancel" /> </button>

                        </div>
                </form>

            </div>
        </c:otherwise>
    </c:choose>
</div>
