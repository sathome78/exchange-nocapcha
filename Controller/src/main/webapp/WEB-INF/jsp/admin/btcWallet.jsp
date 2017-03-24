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
    <title><loc:message code="admin.currencyLimits.title"/></title>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/admin-btcWallet/btcWallet.js'/>"></script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-8 col-md-offset-1 admin-container">
            <div class="text-center">
                <h4><loc:message code="btcWallet.title"/></h4>
                <div id="walletMenu" class="buttons">
                    <button class="active adminForm-toggler blue-box">
                        <loc:message code="btcWallet.history.title"/>
                    </button>
                    <button class="adminForm-toggler blue-box">
                        <loc:message code="btcWallet.send.title"/>
                    </button>
                </div>
            </div>



            <div class="tab-content">
                <div id="panel1" class="tab-pane active">
                    <h5><loc:message code="btcWallet.history.title"/></h5>
                    <table id="txHistory">
                        <thead>
                        <tr>
                            <th><loc:message code="btcWallet.history.time"/></th>
                            <th><loc:message code="btcWallet.history.txid"/></th>
                            <th><loc:message code="btcWallet.history.category"/></th>
                            <th><loc:message code="btcWallet.address"/></th>
                            <th><loc:message code="btcWallet.amount"/></th>
                            <th><loc:message code="btcWallet.history.fee"/></th>
                            <th><loc:message code="btcWallet.history.confirmations"/></th>
                        </tr>
                        </thead>
                    </table>

                </div>
                <div id="panel2" class="tab-pane">
                    <h1>talala</h1>
                </div>
            </div>



        </div>
</main>

<%@include file='../fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
