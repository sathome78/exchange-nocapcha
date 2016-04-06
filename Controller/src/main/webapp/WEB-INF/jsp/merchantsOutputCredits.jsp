<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="paymentForm" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title><loc:message code="merchants.outputTitle"/></title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon" />

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='https://fonts.googleapis.com/css?family=Roboto:400,700,300' rel='stylesheet' type='text/css'>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
    <script src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>" type="text/javascript"></script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style-new.css'/>" rel="stylesheet">

    <script type="text/javascript" src="<c:url value="/client/js/main.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/function.js"/>"></script>

</head>


<body>

<%@include file='header_new.jsp' %>

<main class="container orders_new output">
    <%@include file='exchange_info_new.jsp' %>
    <div class="row">
        <%@include file='usermenu_new.jsp' %>

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
                        <div class="col-sm-9">
                            <paymentForm:form id="payment" class="form-horizontal withdraw__money" name="payment" method="post"
                                              modelAttribute="payment" action="/merchants/payment/withdraw">
                                <div>
                                        <%--Валюта к вводу--%>
                                    <label><loc:message code="merchants.currencyforoutput"/> </label>
                                    <paymentForm:select path="currency" class="select currency-for-output-select">
                                        <paymentForm:options items="${wallets}" itemLabel="fullName" itemValue="currencyId"/>
                                    </paymentForm:select>
                                </div>
                                <div>
                                        <%--Способ оплаты--%>
                                    <label for="merchant"><loc:message code="merchants.meansOfPayment"/></label>
                                    <paymentForm:select id="merchant" path="merchant"/>
                                </div>
                                <div>
                                    <label><loc:message code="merchants.sum"/></label>
                                    <paymentForm:input class="form-control" pattern="/\d*\.\d{1,2}/" placeholder="0.0"
                                                       id="sum" path="sum"/>
                                </div>
                                <paymentForm:hidden path="operationType"/>
                                <paymentForm:hidden id="destination" path="destination"/>
                                <%--Создать(Вывести)--%>
                                <button onclick="finPassCheck('myModal', submitMerchantsOutput)" type="button" id="assertOutputPay"
                                        class="btn btn-primary">
                                    <loc:message code="merchants.withdraw"/>
                                </button>
                            </paymentForm:form>
                        </div>
                        <div class="col-sm-3"></div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    <hr/>
</main>

<%@include file='footer_new.jsp' %>
<%@include file='finpassword.jsp' %>

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
                    <button class="modal-button" type="button" data-dismiss="modal"><loc:message code="merchants.continue"/></button>
                </div>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<%--... MODAL--%>


<%----------%>
<script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/menuSwitcher.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/submits/merchantsSubmitOutput.js'/>"></script>
<%----------%>
</body>
</html>

