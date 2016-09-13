<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title><loc:message code="admin.usersWallet"/></title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>
    <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.0/themes/base/jquery-ui.css" rel='stylesheet' type='text/css'>
    <%@include file='admin/links_scripts.jsp' %>


    <script type="text/javascript" src="<c:url value='/client/js/download.js'/>"></script>
    <%----------%>
</head>

<body>

<%@include file='fragments/header-simple.jsp' %>

<main class="container my_wallets">
    <div class="row">
        <%@include file='admin/left_side_menu.jsp' %>

        <div class="col-md-8 col-md-offset-1 content admin-container">
            <div class="text-center">
                <h4>
                    <b><loc:message code="admin.usersWallet"/></b>
                </h4>
            </div>
            <div class="row">
                <button id="upload-users-wallets" class="blue-box pull-right" type="submit"><loc:message
                        code="wallets.download"/></button>
            </div>

            <c:forEach var="wallet" items="${usersWalletsSummaryList}">
                <div class="block">
                    <div class="currency">${wallet.currencyName}</div>
                    <p class="info-item info-item-title col-sm-12">
                        <loc:message code="wallets.amount"/>:
                            ${wallet.walletsAmount}
                    </p>

                    <p class="info-item col-sm-4">
                        <loc:message code="wallets.balance"/>:
                        <fmt:formatNumber type="number" maxFractionDigits="9" value="${wallet.balance}"/>
                    </p>

                    <p class="info-item next_item">
                        <loc:message code="wallets.average"/>:
                        <fmt:formatNumber type="number" maxFractionDigits="9" value="${wallet.balancePerWallet}"/>
                    </p>
                    <br/>

                    <p class="info-item col-sm-4">
                        <loc:message code="wallets.abalance"/>:
                        <fmt:formatNumber type="number" maxFractionDigits="9" value="${wallet.activeBalance}"/>
                    </p>

                    <p class="info-item next_item">
                        <loc:message code="wallets.average"/>:
                        <fmt:formatNumber type="number" maxFractionDigits="9" value="${wallet.activeBalancePerWallet}"/>
                    </p>
                    <br/>

                    <p class="info-item  col-sm-4">
                        <loc:message code="wallets.rbalance"/>:
                            ${wallet.reservedBalance}
                    </p>

                    <p class="info-item next_item">
                        <loc:message code="wallets.average"/>:
                        <fmt:formatNumber type="number" maxFractionDigits="9"
                                          value="${wallet.reservedBalancePerWallet}"/>
                    </p>
                    <br/>

                    <p class="info-item  col-sm-4">
                        <loc:message code="wallets.totalInputAmount"/>:
                        <fmt:formatNumber type="number" maxFractionDigits="9"
                                          value="${wallet.merchantAmountInput}"/>
                    </p>

                    <p class="info-item next_item">
                        <loc:message code="wallets.totalOutputAmount"/>:
                        <fmt:formatNumber type="number" maxFractionDigits="9"
                                          value="${wallet.merchantAmountOutput}"/>
                    </p>
                </div>
            </c:forEach>
        </div>
    </div>
    <hr>
</main>
<%@include file='fragments/footer.jsp' %>
<%@include file='admin/datePicker.jsp' %>
</body>
</html>

