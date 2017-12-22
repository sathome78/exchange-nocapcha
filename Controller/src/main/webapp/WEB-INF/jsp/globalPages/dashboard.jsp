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

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
    <%--<link rel="stylesheet" href="<c:url value="/client/css/font-awesome.min.css"/>">--%>
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/jquery-ui.css'/>" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value="/client/css/refTable.css"/>">
    <link rel="stylesheet" href="<c:url value="/client/css/jquery.datetimepicker.css"/>">
    <link rel="stylesheet" href="<c:url value="/client/css/jquery.onoff.css"/>">
    <script type="text/javascript" src="<c:url value='/client/js/jquery.datetimepicker.js'/>"></script>
    <%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/tmpl.js'/>"></script>
    <%----%>
    <script src="https://cdn.jsdelivr.net/sockjs/1/sockjs.min.js"></script>
    <script type="text/javascript" src="<c:url value='/client/js/stomp.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/lib/numeral/numbro.min.js'/>"></script>
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
    <script type="text/javascript" src="<c:url value='/client/js/loc-direction.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/moment-with-locales.min.js'/>"></script>
    <script src='//cdn.tinymce.com/4/tinymce.min.js'></script>
    <script type="text/javascript" src="<c:url value='/client/js/historyRefStr.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/jquery.tmpl.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/jquery.twbsPagination.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/lib/jquery.onoff.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/order/orderRoleFilter.js'/>"></script>
   <%-- <script src="<c:url value='/client/js/lib/survey/survey.jquery.min.js'/>"></script>--%>
    <!-- Amcharts Charts ... -->
    <script src="/client/js/chart-amcharts/amcharts.js" type="text/javascript"></script>
    <script src="/client/js/chart-amcharts/serial.js" type="text/javascript"></script>
    <script src="/client/js/chart-amcharts/amstock.js" type="text/javascript"></script>
    <script src="/client/js/chart-amcharts/chartInit.js" type="text/javascript"></script>
    <script src="/client/js/chart-amcharts/stockChart.js" type="text/javascript"></script>
    <!-- ... Amcharts Charts -->
    <script type="text/javascript" src="<c:url value='/client/js/news/news.js'/>"></script>
    <%----%>

    <link href="<c:url value='/client/css/action-buttons.css'/>" rel="stylesheet">

    <%--TOOLS ... --%>
    <!-- Google Analytics-->
    <%--<%@include file="../tools/google.jsp"%>--%>
    <!-- Yandex.Metrika counter -->
    <%--<%@include file="../tools/yandex.jsp" %>--%>
    <%--ZOPIM CHAT--%>
    <%@include file="../tools/alexa.jsp" %>
    <%@include file="../tools/ga.jsp" %>
    <%--<%@include file="../tools/yandex.jsp" %>--%>
    <%@include file="../tools/zopim.jsp" %>
    <%-- ... TOOLS--%>
</head>
<body>

<%@include file="../fragments/header.jsp" %>

<main class="container">

    <div id="alerts_container">
        <div class="alert alert-warning" id="tech_alert" style="display: none">
            <h4 id="tech_alert_text"></h4>
        </div>

        <div class="alert alert-warning" id="upd_alert" style="display: none; padding-bottom: 0">
            <h4 id="upd_alert_text"></h4>

            <div class="countdown countdown-container container" style="margin-left: 80%">

                <div class="clock row">
                    <h6 style="margin-left: 1%"><loc:message code="message.alert.timerTimeTo"/>:</h6>
                    <!-- hours -->
                    <div class="clock-item clock-hours countdown-time-value col-sm-6 col-md-3">
                        <div class="wrap">
                            <div class="inner">
                                <div id="canvas_hours" class="clock-canvas"></div>
                                <div class="text">
                                    <p class="val">0</p>
                                    <p class="type-hours type-time"><loc:message code="message.alert.hours"/></p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- minutes -->
                    <div class="clock-item clock-minutes countdown-time-value col-sm-6 col-md-3">
                        <div class="wrap">
                            <div class="inner">
                                <div id="canvas_minutes" class="clock-canvas"></div>
                                <div class="text">
                                    <p class="val">0</p>
                                    <p class="type-minutes type-time"><loc:message code="message.alert.minutes"/></p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- seconds -->
                    <div class="clock-item clock-seconds countdown-time-value col-sm-6 col-md-3">
                        <div class="wrap">
                            <div class="inner">
                                <div id="canvas_seconds" class="clock-canvas"></div>
                                <div class="text">
                                    <p class="val">0</p>
                                    <p class="type-seconds type-time"><loc:message code="message.alert.seconds"/></p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>


    </div>
    <input id="noty2fa" hidden value='${notify2fa}'/>
    <div class="row_big">
        <%@include file="../fragments/left-sider.jsp" %>
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
<%@include file="../fragments/modal/poll_invite_modal.jsp" %>
<%@include file="../fragments/modal/2fa_noty_modals.jsp" %>
<span hidden id="successNoty">${successNoty}</span>
<span hidden id="session">${sessionId}</span>
<input type="hidden" class="s_csrf" name="${_csrf.parameterName}" value="${_csrf.token}"/>

</body>
</html>

<script type="text/javascript" src="<c:url value='/client/js/kinetic.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/jquery.final-countdown.js'/>"></script>
<style>
    .countdown-container {
        position: sticky;
        top: 50%;
        -webkit-transform: translateY(-50%);
        -moz-transform: translateY(-50%);
        transform: translateY(-50%);
    }

    .clock-item {
        width: 5%; !important;
    }
    .clock-item .inner {
        height: 0px;
        padding-bottom: 100%;
        position: relative;
        width: 100%;
    }
    .clock-canvas {
        background-color: rgba(255, 255, 255, .1);
        border-radius: 50%;
        height: 0px;
        padding-bottom: 100%;
    }
    .text {
        color: #fff;
        font-size: 30px;
        font-weight: bold;
        margin-top: -30px;
        position: absolute;
      /*  top: 50%;*/
        text-align: center;
        text-shadow: 1px 1px 1px rgba(0, 0, 0, 1);
        width: 100%;
    }
    .text .val {
        font-size: 16px;
    }
    .text .type-time {
        font-size: 8px;
    }
    @media (min-width: 768px) and (max-width: 991px) {
        .clock-item {
            margin-bottom: 30px;
        }
    }
    @media (max-width: 767px) {
        .clock-item {
            margin: 0px 30px 30px 30px;
        }
    }
</style>
