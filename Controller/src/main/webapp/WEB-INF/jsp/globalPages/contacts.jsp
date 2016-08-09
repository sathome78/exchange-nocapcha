<%--
  Created by IntelliJ IDEA.
  User: ogolv
  Date: 09.08.2016
  Time: 8:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title><loc:message code="dashboard.contactsAndSupport"/></title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="interkassa-verification" content="c4deb5425361141d96dd48d235b6fc4a"/>
    <link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>

    <script src="<c:url value="/client/js/jquery_1.11.3.min.js"/>" type="text/javascript"></script>
    <link rel="stylesheet" href="<c:url value="/client/css/font-awesome.min.css"/>">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style.css'/>" rel="stylesheet">

    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/notyInit.js'/>"></script>
</head>
<body>
<%@include file="../fragments/header-simple.jsp" %>
<main class="container">
    <div class="row">
        <h3><loc:message code="dashboard.contactsAndSupport"/></h3>
        <hr/>

        <div class="col-md-6 col-md-offset-3 content">
           <div class="text-center"></div>
            <div class="panel-body">
                <form action="/sendFeedback" id="feedback-form" method="post">
                    <div class="input-block-wrapper">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label for="sender-name" class="input-block-wrapper__label"><loc:message
                                    code="register.nickname"/></label>
                        </div>

                        <div class="col-md-9 input-block-wrapper__input-wrapper">
                            <input name="name" class="input-block-wrapper__input full-width"
                                   id="sender-name"/>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label for="sender-email" class="input-block-wrapper__label"><loc:message
                                    code="login.email"/></label>
                        </div>
                        <div class="col-md-9 input-block-wrapper__input-wrapper">
                            <input name="email" class="input-block-wrapper__input full-width"
                                   id="sender-email"/>
                        </div>
                    </div>

                    <div class="input-block-wrapper">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label for="message-text" class="input-block-wrapper__label"><loc:message
                                    code="admin.phone"/></label>
                        </div>
                        <div class="col-md-9">
                            <textarea class="textarea non-resize"
                                   id="message-text" name="text" form="feedback-form"></textarea>

                        </div>
                    </div>
                    <br/>
                    <div class="input-block-wrapper">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">

                        </div>
                        <div class="col-md-9">
                            <c:if test="${captchaType==\"RECAPTCHA\"}">
                                <%--CAPTCHA GOOGLE--%>
                                <div id="cpch-head-field" class="g-recaptcha"
                                     data-sitekey=${captchaProperties.get("captcha.key")}></div>
                                <p class='cpch-error-message' style="color:red">${cpch}</p>
                            </c:if>
                            <c:if test="${captchaType==\"BOTDETECT\"}">
                                <%--CAPTCHA BotDetect--%>
                                <div class="validationDiv">
                                    <botDetect:captcha id="feedbackMessageCaptcha" userInputID="captchaCode"/>
                                    <input name="captchaCode" type="text" id="captchaCode"/>
                                    <input type="hidden" name="captchaId" value="feedbackMessageCaptcha"/>
                                </div>
                            </c:if>
                            <input type="hidden" name="captchaType" value="${captchaType}"/>


                        </div>
                    </div>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>


                    <%----%>
                    <div class="paddingtop10">
                        <button type="submit" class="blue-box"><loc:message
                                code="dashboard.entrance"/></button>
                    </div>




                </form>

        </div>
    </div>
        <hr/>
        <div>
            <p><loc:message code="contacts.contactPhone"/> </p>
            <loc:message code="contacts.address"/>
        </div>

    </div>
</main>
<span hidden id="errorNoty">${cpch}</span>
<span hidden id="successNoty">${successNoty}</span>
<%@include file='../fragments/footer.jsp' %>
</body>
</html>
