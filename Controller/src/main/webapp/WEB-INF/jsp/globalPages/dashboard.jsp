<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>Exrates</title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="interkassa-verification" content="c4deb5425361141d96dd48d235b6fc4a"/>

    <link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>
    <script src="<c:url value="/client/js/polyfill/polyfill.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/client/js/jquery_1.11.3.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>" type="text/javascript"></script>
    <script src="<c:url value="/client/js/jquery-ui.js"/>" type="text/javascript"></script>
    <script src="<c:url value="/client/js/datatables.js"/>" type="text/javascript"></script>

    <%--<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">--%>
    <link rel="stylesheet" href="<c:url value="/client/css/font-awesome.min.css"/>">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/jquery-ui.css'/>" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value="/client/css/refTable.css"/>">
    <link rel="stylesheet" href="<c:url value="/client/css/jquery.datetimepicker.css"/>">
    <link rel="stylesheet" href="<c:url value="/client/css/jquery.onoff.css"/>">
    <link rel="stylesheet" href="<c:url value="/client/css/jqueryDatatables.css"/>">
    <script type="text/javascript" src="<c:url value='/client/js/jquery.datetimepicker.js'/>"></script>
    <%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/tmpl.js'/>"></script>
    <%----%>
    <script type="text/javascript" src="<c:url value='/client/js/sockjs114.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/stomp.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/lib/numeral/numbro.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/app.js'/>"></script>
    <%----%>
    <script type="text/javascript" src="<c:url value='/client/js/history/history.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/inputOutput/refillConfirmationDialog.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/currencypair/currencyPairSelector.js'/>"></script>
    <%----%>
    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/notyInit.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/loc-direction.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/moment-with-locales.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/jquery.tmpl.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/jquery.twbsPagination.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/lib/jquery.onoff.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/order/orderRoleFilter.js'/>"></script>
    <%-- <script src="<c:url value='/client/js/lib/survey/survey.jquery.min.js'/>"></script>--%>
    <%----%>
    <script type="text/javascript" src="<c:url value='/client/js/kinetic.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/jquery.final-countdown.js'/>"></script>

    <link href="<c:url value='/client/css/action-buttons.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/timer.css'/>" rel="stylesheet">
    <%@include file="../tools/newCapchaScripts.jsp" %>


</head>
<body>

<%@include file="../fragments/header.jsp" %>
<main class="container">

    <%@include file="../fragments/alerts.jsp" %>
    <input id="noty2fa" hidden value='${notify2fa}'/>
    <c:if test="${firstLogin}">
        <input id="info_new" hidden value='${firstLogin}'/>
    </c:if>
    <input id="preferedCurrencyPairName" hidden value='${preferedCurrencyPairName}'/>
    <div class="row_big">
        <div class="cols-md-8 background_white">
            <div id="startup-page-id" class="center-dummy" style="height: 1px; visibility: hidden">
                <%--to keep panel when all pages are hidden--%>
                <%--and to keep startup page ID--%>
                ${startupPage}
            </div>
            <div id="startup-subPage-id" class="center-dummy" style="height: 1px; visibility: hidden">
                <%--to keep panel when all pages are hidden--%>
                <%--and to keep startup page ID--%>
                ${startupSubPage}
            </div>
        </div>
    </div>
</main>
<%@include file="../fragments/modal/G2fa_noty_modal.jsp" %>
<%@include file="../fragments/modal/g2fa_login_warning_modal.jsp" %>
<span hidden id="successNoty">${successNoty}</span>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="session">${sessionId}</span>
<c:if test="${successRegister}"><span hidden id="successRegister"></span></c:if>
<input type="hidden" class="s_csrf" name="${_csrf.parameterName}" value="${_csrf.token}"/>

</body>
</html>
