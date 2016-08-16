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

    <script src="<c:url value="/client/js/jquery_1.11.3.min.js"/>" type="text/javascript"></script>
    <script src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>" type="text/javascript"></script>

    <link rel="stylesheet" href="<c:url value="/client/css/font-awesome.min.css"/>">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style.css'/>" rel="stylesheet">
    <%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/tmpl.js'/>"></script>
    <%----%>
    <script type="text/javascript" src="<c:url value='/client/js/sockjs.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/app.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/globalPages/dashboard-init.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/siders/leftSider.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/siders/rightSider.js'/>"></script>
    <%----%>
    <script type="text/javascript" src="<c:url value='/client/js/trading/trading.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/mywallets/mywallets.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/history/history.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/myorders/myorders.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/inputOutput/inputOutput.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/myreferral/myreferral.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/mywallets/statements.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/order/orders.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/currencypair/currencyPairSelector.js'/>"></script>
    <%----%>
    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/notyInit.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/dashboard/chat.js'/>"></script>

    <%--TOOLS ... --%>
    <!-- Google Analytics-->
    <%--<%@include file="../tools/google.jsp"%>--%>
    <!-- Yandex.Metrika counter -->
    <%--<%@include file="../tools/yandex.jsp" %>--%>
    <%--ZOPIM CHAT--%>
    <%@include file="../tools/alexa.jsp" %>
    <%@include file="../tools/zopim.jsp" %>
    <%-- ... TOOLS--%>
</head>
<body>

<%@include file="../fragments/header.jsp" %>

<main class="container">
    <div class="row_big">
        <%@include file="../fragments/left-sider.jsp" %>
        <div class="cols-md-8 background_white">
            <div id="startup-page-id" class="center-dummy" style="height: 1px; visibility: hidden">
                <%--to keep panel when all pages are hidden--%>
                <%--and to keep startup page ID--%>
                ${startupPage}
            </div>
            <%@include file="../fragments/trading-center.jsp" %>
            <%@include file="../fragments/mywallets-center.jsp" %>
            <%@include file="../fragments/statement-center.jsp" %>
            <%@include file="../fragments/history-center.jsp" %>
            <%@include file="../fragments/orders-center.jsp" %>
        </div>
        <%@include file="../fragments/right-sider.jsp" %>
    </div>
</main>
<%@include file='../fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>

</body>
</html>
