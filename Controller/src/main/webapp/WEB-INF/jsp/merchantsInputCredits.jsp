<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="paymentForm" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title><loc:message code="merchants.inputTitle"></loc:message></title>
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
            <%--Deposit--%>
            <h4><loc:message code="merchants.inputTitle"/></h4>

            <label class="alert-danger has-error">
                <c:if test="${not empty error}">
                    <loc:message code="${error}"/>
                </c:if>
            </label>
            <div class="row">
                <div class="col-sm-9">
                    <paymentForm:form class="form-horizontal withdraw__money" id="payment" name="payment" method="post"
                                      modelAttribute="payment" action="">
                        <div>
                                <%--Currency to deposit--%>
                            <label>
                                <loc:message code="merchants.inputCurrency"/>
                            </label>
                                    <select name="currency" id="currency" class="select currency-for-output-select">
                                        <c:forEach items="${currencies}" var="currency">
                                            <option value="${currency.id}">${currency.name}</option>
                                        </c:forEach>
                                    </select>
                        </div>
                        <div>
                                <%--Payment method--%>
                            <label for="merchant"><loc:message
                                    code="merchants.meansOfPayment"/></label>
                            <paymentForm:select id="merchant" path="merchant"/>
                        </div>
                        <div>
                            <label for="sum"><loc:message code="merchants.sum"/></label>
                            <paymentForm:input class="form-control" placeholder="0.0"
                                               id="sum" path="sum"/>
                        </div>
                        <button type="button" data-toggle="modal" id="assertInputPay" name="assertInputPay"
                                data-target="#myModal"
                                class="btn btn-primary"><loc:message code="merchants.deposit"/></button>
                        <paymentForm:hidden path="operationType"/>
                    </paymentForm:form>
                </div>
                <div class="col-sm-3"></div>
            </div>
        </div>
    </div>
    <hr>
</main>

<%@include file='footer_new.jsp' %>

<%--MODAL ... --%>
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
            </div>
            <div class="modal-footer">
                <div class="add__money__btns request_money_operation_btn">
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


<%--... MODAL--%>


<%----------%>
<script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/menuSwitcher.js'/>"></script>
<%----------%>
</body>
</html>

