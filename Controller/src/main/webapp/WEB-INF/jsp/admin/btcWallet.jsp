<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 23.09.2016
  Time: 12:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<html>
<head>
    <title><loc:message code="btcWallet.title"/></title>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/admin-btcWallet/btcWallet.js'/>"></script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<c:set var="admin_manageBtcWallet" value="<%=AdminAuthority.MANAGE_BTC_CORE_WALLET%>"/>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-8 col-md-offset-1 admin-container">
            <div class="text-center">
                <h4><loc:message code="btcWallet.title"/></h4>
            </div>
            <div class="row text-center" style="font-size: 1.4rem">
                <p class="green"><strong><loc:message code="btcWallet.balance"/>
                    <span id="current-btc-balance">${walletInfo.balance}</span> BTC</strong></p>
                <p class="lightblue"><strong><loc:message code="btcWallet.unconfirmedBalance"/>
                    <span id="current-btc-unconfirmed-balance">${walletInfo.unconfirmedBalance}</span> BTC</strong></p>
            </div>
            <sec:authorize access="hasAuthority('${admin_manageBtcWallet}')">
            <div id="walletMenu" class="buttons">
                <button class="active adminForm-toggler blue-box">
                    <loc:message code="btcWallet.history.title"/>
                </button>

                    <button class="adminForm-toggler blue-box">
                        <loc:message code="btcWallet.send.title"/>
                    </button>
            </div>
            </sec:authorize>

            <div class="tab-content">
                <div id="panel1" class="tab-pane active">
                    <div class="text-center"><h4><loc:message code="btcWallet.history.title"/></h4></div>
                    <table id="txHistory">
                        <thead>
                        <tr>
                            <th><loc:message code="btcWallet.history.time"/></th>
                            <th><loc:message code="btcWallet.history.txid"/></th>
                            <th><loc:message code="btcWallet.history.category"/></th>
                            <th><loc:message code="btcWallet.address"/></th>
                            <th><loc:message code="btcWallet.history.amount"/></th>
                            <th><loc:message code="btcWallet.history.fee"/></th>
                            <th><loc:message code="btcWallet.history.confirmations"/></th>
                            <th></th>
                            <th></th>
                        </tr>
                        </thead>
                    </table>
                </div>
                <sec:authorize access="hasAuthority('${admin_manageBtcWallet}')">
                <div id="panel2" class="tab-pane">
                    <div class="text-center"><h4><loc:message code="btcWallet.send.title"/></h4></div>
                    <div class="col-md-8 col-md-offset-2">
                        <form id="send-btc-form" class="form_full_width">

                            <div>
                                <div style="float: right"><button id="addPayment" class="text-center btn btn-default">
                                    <span style="font-size: 2.5rem; margin: 0" class="fa fa-plus"></span></button></div>
                                <div class="col-md-11">
                                    <div id="payments">
                                        <div id="payment_0" class="btcWalletPayment">
                                            <div class="input-block-wrapper">
                                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                                    <label class=" input-block-wrapper__label"><loc:message code="btcWallet.address"/></label>
                                                </div>
                                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                                    <input id="address_0" name="address" class="input-address input-block-wrapper__input admin-form-input"/>
                                                </div>
                                            </div>
                                            <div class="input-block-wrapper">
                                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label"><loc:message code="btcWallet.amount"/></label>
                                                </div>
                                                <div class="col-md-5 input-block-wrapper__input-wrapper">
                                                    <input id="amount_0" name="amount" type="number" class="input-amount input-block-wrapper__input admin-form-input"/>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div id="fee-div" class="input-block-wrapper">
                                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                                            <label for="input-fee" class="input-block-wrapper__label pull-right"><loc:message code="btcWallet.fee"/></label>
                                        </div>
                                        <div class="col-md-3 input-block-wrapper__input-wrapper">
                                            <input id="input-fee" readonly disabled type="number" class="input-block-wrapper__input admin-form-input"/>
                                        </div>
                                    </div>

                                </div>


                            </div>
                            <div class="input-block-wrapper">
                                <button id="submit-btc" class="delete-order-info__button blue-box"
                                        type="button"><loc:message code="admin.submit"/></button>
                                <button id="reset-btc" class="delete-order-info__button blue-box"
                                        type="button"><loc:message code="admin.reset"/></button>
                            </div>
                        </form>
                    </div>
                </div>
                </sec:authorize>
            </div>
        </div>
</main>

<sec:authorize access="hasAuthority('${admin_manageBtcWallet}')">
<div id="password-modal" class="modal fade">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="btcWallet.password.title"/></h4>
            </div>
            <div class="modal-body">
                <p><loc:message code="btcWallet.password.prompt"/></p>
                <form id="password-form" class="form_full_width form_auto_height">
                    <div class="input-block-wrapper">
                        <div class="col-md-12 input-block-wrapper__input-wrapper">
                            <input name="password" class="input-block-wrapper__input admin-form-input" type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <button id="submit-wallet-pass" class="delete-order-info__button blue-box" type="button"><loc:message code="admin.submit"/></button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<div id="payment-confirm-modal" class="modal fade">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="btcWallet.payment.dialog.title"/></h4>
            </div>
            <div class="modal-body">
                <p id="btc-confirm-prompt"><loc:message code="btcWallet.payment.prompt"/></p>
            </div>
            <div class="modal-footer">
                <div class="delete-order-info__button-wrapper">
                    <button id="confirm-btc-submit" class="delete-order-info__button">
                        <loc:message code="admin.submit"/></button>
                    <button class="delete-order-info__button" data-dismiss="modal">
                        <loc:message code="admin.cancel"/></button>
                </div>
            </div>
        </div>
    </div>
</div>

</sec:authorize>







<span hidden id="confirmBtcMessage"><loc:message code="btcWallet.payment.prompt"/></span>

<%@include file='../fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
