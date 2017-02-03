<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 03.02.2017
  Time: 9:49
  To change this template use File | Settings | File Templates.
--%>

<div>
    <h4><loc:message code="merchants.invoiceDetails.title"/></h4>
    <c:choose>
        <c:when test="${not empty error}">
            <label class="alert-danger has-error">
                <loc:message code="${error}"/>
            </label>
    </c:when>
        <c:otherwise>
                    <%--Deposit--%>
                <div class="row">
                    <div class="input-block-wrapper clearfix">
                        <div id="creditsOperationInfo" class="well col-md-8 col-md-offset-3 ">
                            <div class="text-center">
                                <h5><loc:message code="merchants.invoiceDetails.paymentDetails" /></h5>
                            </div>
                            <table class="table">
                                <tbody>
                                <tr>
                                    <td><loc:message code="transaction.amount"/> </td>
                                    <td>${creditsOperation.amount} ${creditsOperation.currency.name}</td>
                                </tr>
                                <tr>
                                    <td><loc:message code="transaction.commission"/></td>
                                    <td>${creditsOperation.commission.value}</td>
                                </tr>
                                <tr>
                                    <td><loc:message code="transaction.commissionAmount"/></td>
                                    <td>${creditsOperation.commissionAmount} ${creditsOperation.currency.name}</td>
                                </tr>
                                <tr>
                                    <td>Bank details</td>
                                    <td><div id="bankDetails"></div></td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>


                <form action="<c:url value="/merchants/invoice/payment/prepare"/>" method="post">
                    <div class="input-block-wrapper clearfix">
                        <div class="col-md-3 input-block-wrapper__label-wrapper" >
                            <label for="bankId" class="input-block-wrapper__label" >
                                <loc:message code="merchants.invoiceDetails.bank"/></label>
                        </div>
                        <div class="col-md-8 " >
                            <select class="form-control input-block-wrapper__input" id="bankId" name="bankId">
                                <option value="-1">NOT_SELECTED</option>
                                <c:forEach items="${invoiceBanks}" var="bank">
                                    <option value="${bank.id}">${bank.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="input-block-wrapper clearfix">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label for="userAccount" class="input-block-wrapper__label" >
                                    <loc:message code="merchants.invoiceDetails.userAccount"/></label>
                            </div>
                            <div class="col-md-8 " >
                                <input class="form-control input-block-wrapper__input" type="text" id="userAccount" name="userAccount">
                            </div>
                        </div>
                        <div class="input-block-wrapper clearfix">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label for="remark" class="input-block-wrapper__label" >
                                    <loc:message code="merchants.invoiceDetails.remark"/></label>
                            </div>
                            <div class="col-md-8 " >
                                <textarea id="remark" class="form-control textarea non-resize" name="remark" ></textarea>
                            </div>
                        </div>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

                    <div class="col-md-4 input-block-wrapper">
                        <button class="btn btn-primary btn-lg" type="submit">Submit</button>
                        <button class="btn btn-danger btn-lg" type="button">Cancel</button>
                    </div>

                </form>

                </div>
            <div hidden id="bankInfo">
                <p data-bankid="-1">Not selected</p>
                <c:forEach items="${invoiceBanks}" var="bank">
                    <p data-bankid="${bank.id}">
                        <span>${bank.name}</span><br/>
                        <span>${bank.accountNumber}</span><br/>
                        <span>${bank.recipient}</span><br/>
                    </p>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
