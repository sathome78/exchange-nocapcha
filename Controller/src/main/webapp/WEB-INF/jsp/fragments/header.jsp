<%@include file="../tools/google_body.jsp"%>
<%@ page import="me.exrates.controller.AdminController"%>
<%@ page import="org.springframework.web.servlet.support.RequestContext" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%--CAPTCHA--%>
<%@ taglib prefix="botDetect" uri="botDetect" %>
<%--CAPTCHA--%>
<script type="text/javascript" src="/client/js/jquery.cookie.js"></script>
<script src="<c:url value="/client/js/jquery.noty.packaged.min.js"/>"></script>
<script src="<c:url value="/client/js/notifications/notifications.js"/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/login.js'/>"></script>

<!-- New design -->
<link rel="stylesheet" href="client/assets/css/main.min.css">
<link rel="stylesheet" href="client/assets/css/libs.min.css">

<script src="<c:url value="/client/assets/js/libs.min.js"/>"></script>
<script src="<c:url value="/client/assets/js/main.min.js"/>"></script>

<!-- Geetest-->
<script src="<c:url value="/client/assets/js/gt.js"/>"></script>
<!-- New design -->

<c:set var="path" value="${fn:replace(pageContext.request.requestURI, '/WEB-INF/jsp', '')}"/>
<c:set var="path" value="${fn:replace(path, '.jsp', '')}"/>
<%--don't show entrance menu item in header for pages that contain it's own capcha because conflict occurs--%>
<sec:authorize access="isAuthenticated()" var="isAuth"/>
<c:set var="showEntrance" value="${
                                (path != '/login')
                                && (path != '/register')
                                && (path != '/forgotPassword')
                                && (path != '/login?error')}"/>
<c:set var="showRegistration" value="${(path != '/register')}"/>
<header class="header">
    <div class="container">
        <div class="cols-md-2"><a href="/" class="logo"><img src="/client/img/Logo_blue.png" alt="Exrates Logo"></a>
        </div>
        <div class="cols-md-8">
            <ul class="nav header__nav">
                <li>
                    <a class="nav__link" href="<c:url value='/ico_dashboard'/>">
                        ICO
                    </a>
                </li>
                <sec:authorize access="isAuthenticated()">
                    <li id="menu-traiding"><a href="#" class="nav__link nav__link_active "><loc:message
                            code="dashboard.trading"/></a></li>
                    <li id="menu-mywallets"><a href="#" class="nav__link"><loc:message code="usermenu.mywallets"/></a>
                    </li>
                    <li id="menu-myhistory"><a href="#" class="nav__link"><loc:message code="usermenu.myorders"/></a>
                    </li>
                    <li id="menu-orders"><a href="#" class="nav__link"><loc:message code="usermenu.orders"/></a></li>
                </sec:authorize>
                <li><a href="<c:url value="http://support.exrates.me/" />" target="_blank" class="nav__link">
                    <loc:message code="dashboard.support"/></a>
                </li>

                <sec:authorize access="isAuthenticated()">
                    <li id="adminka-entry">
                            <sec:authorize access="<%=AdminController.adminAnyAuthority%>">
                                <a class="nav__link" href="<c:url value='/2a8fy7b07dxe44'/>">
                                    <loc:message code="admin.title"/>
                                </a>
                            </sec:authorize>
                        <sec:authorize access="<%=AdminController.traderAuthority%>">
                            <a class="nav__link" href="<c:url value='/2a8fy7b07dxe44/removeOrder'/>">
                                <loc:message code="manageOrder.title"/>
                            </a>
                        </sec:authorize>
                        <sec:authorize access="<%=AdminController.botAuthority%>">
                            <a class="nav__link" href="<c:url value='/2a8fy7b07dxe44/autoTrading'/>">
                                <loc:message code="admin.title"/>
                            </a>
                        </sec:authorize>
                    </li>
                </sec:authorize>


                    <li>
                        <a href="https://play.google.com/store/apps/details?id=lk.exrates.me" target="_blank"
                           class="nav__link"><img src="/client/img/android-solid.png" height="20" width="20"></a>
                    </li>
                    <li>
                        <a href="https://itunes.apple.com/ua/app/exratesme/id1163197277" target="_blank"
                           class="nav__link"><img src="/client/img/apple-solid.png" height="20" width="20"></a>
                    </li>
                    <sec:authorize access="isAuthenticated()">
                        <li id="hello-my-friend"><a class="nav__link" href="">

                            <strong><sec:authentication property="principal.username"/></strong></a>
                        </li>
                    </sec:authorize>

                <ul class="padding0 pull-right">
                    <sec:authorize access="! isAuthenticated()">
                        <c:if test="${showEntrance}">

                            <li role="presentation" class="dropdown paddingtop10 open-li">
                                <a class="dropdown-toggle nav__link focus-white" data-toggle="dropdown" href="#"
                                   role="button"
                                   aria-haspopup="true" aria-expanded="false">
                                    <loc:message code="dashboard.entrance"/> <span class="caret"></span>
                                </a>
                                <div class="dropdown-menu">
                                    <form action="/login" class="dropdown-menu__form" method="post">
                                        <input id="login__name" name="username" type="email" placeholder=
                                            <loc:message code="dashboard.loginText"/>
                                                class="form_input">
                                        <input id="login__password" name="password" type="password" placeholder=
                                            <loc:message
                                                    code="dashboard.passwordText"/> class="form_input">
                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                        <br/>
                                        <c:if test="${captchaType==\"RECAPTCHA\"}">
                                            <%--CAPTCHA GOOGLE--%>
                                            <div id="cpch-head-field" class="g-recaptcha"
                                                 data-sitekey=${captchaProperties.get("captcha.key")}></div>
                                            <p class='cpch-error-message' style="color:red">${cpch}</p>
                                        </c:if>
                                        <c:if test="${captchaType==\"BOTDETECT\"}">
                                            <%--CAPTCHA BotDetect--%>
                                            <div class="validationDiv">
                                                <botDetect:captcha id="headerRegCaptcha" userInputID="captchaCode"/>
                                                <input name="captchaCode" type="text" id="captchaCode"/>
                                                <input type="hidden" name="captchaId" value="headerRegCaptcha"/>
                                            </div>
                                        </c:if>
                                        <input type="hidden" name="captchaType" value="${captchaType}"/>
                                            <%----%>
                                        <button id="login_button" type="submit" class="login_button"><loc:message
                                                code="dashboard.entrance"/></button>
                                        <a href="/forgotPassword" class="white forgot-password"><loc:message
                                                code="dashboard.forgotPassword"/></a>

                                        <div></div>
                                            <%--QR--%>
                                        <%--TODO temporary disable--%>
                                            <%--<div class="col-sm-8 col-sm-offset-2 text-center"><span id="login-qr"></span></div>
                                        <div class="col-sm-12 text-center" style="margin-top: 5px"><span class="white"><loc:message code="dashboard.qrLogin.login"/></span></div>--%>
                                    </form>
                                    <sec:authorize access="isAuthenticated()">
                                        <form action="/logout" class="dropdown-menu__logout-form" method="post">
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                            <button type="submit" class="register">
                                                <strong><loc:message code="dashboard.goOut"/></strong>
                                            </button>
                                        </form>
                                    </sec:authorize>
                                </div>
                            </li>
                        </c:if>
                        <li class="pull-left paddingtop10"> <a id="login_link" data-fancybox href="#login" class="focus-white nav__link"><loc:message code="dashboard.loginText"/></a></li>
                        <%--<a id="login_link" data-fancybox href="#login" class="demo-bar-item">login</a>--%>
                    </sec:authorize>
                </ul>
            </ul>
        </div>
        <div class="cols-md-2 right_header_nav">

            <ul class="padding0">
                <sec:authorize access="! isAuthenticated()">
                    <c:if test="${showRegistration}">
                        <li class="pull-left paddingtop10"> <a id="regT" data-fancybox href="#registration" class="focus-white nav__link"><loc:message code="dashboard.signUp"/></a></li>
                    </c:if>
                    <%--<a id="regT" data-fancybox href="#registration" class="demo-bar-item">registration</a>--%>
                </sec:authorize>
                <sec:authorize access="isAuthenticated()">
                    <li class="">
                        <form action="/logout" class="dropdown-menu__logout-form" method="post">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button type="submit" class="logout-button">
                                <loc:message code="dashboard.goOut"/>
                            </button>
                        </form>
                    </li>
                </sec:authorize>

                <li role="presentation" class="dropdown paddingtop10 open-language">
                    <%String lang = (new RequestContext(request)).getLocale().getLanguage();%>
                    <c:set var="lang" value="<%=me.exrates.controller.DashboardController.convertLanguageNameToMenuFormat(lang)%>"/>
                    <a id="language" class="dropdown-toggle focus-white nav__link" data-toggle="dropdown" href="#"
                       role="button" aria-haspopup="true" aria-expanded="false">
                        ${fn:toUpperCase(lang)} <span class="caret"></span>
                    </a>
                    <ul class="dropdown-menu choose-language">
                        <li><a href="#" class="language">EN</a></li>
                        <li><a href="#" class="language">RU</a></li>
                        <li><a href="#" class="language">CH</a></li>
                        <li><a href="#" class="language">ID</a></li>
                        <li><a id="pin_2fa_login_hides" data-fancybox href="#pin_2fa_login" class="popup__bottom-link">2fa Login</a></li>
                        <!--
                        <li><a href="#" class="language">AR</a></li>
                        -->
                    </ul>

                </li>
                <sec:authorize access="isAuthenticated()">
                    <li class="settings-menu-item">
                        <a href="<c:url value="/settings"/>">
                            <span class="glyphicon glyphicon-cog nav__link"></span>
                        </a>
                    </li>
                    <%--<li>
                        <%@include file="../fragments/notification-header.jsp" %>
                    </li>--%>
                </sec:authorize>

            </ul>
        </div>
    </div>

    <!-- Fancybox -->
    <input id="login_error" hidden value='${loginErr}'/>
    <div id="login" class="popup">
        <div class="popup__inner">
            <div class="popup__caption">Log in</div>
            <c:if test="${not empty loginErr}">
                <div class='field__error' style="text-align: center">
                        ${loginErr}
                </div>
            </c:if>

            <form id="login_form" action="/login" class="form" method="post">
                <input type="hidden"  class="csrfC" name="_csrf" value="${_csrf.token}"/>
                <div class="field">
                    <div class="field__label">Email</div>
                    <input id="auth_email" class="field__input" type="email" name="username" placeholder="Email" required>
                </div>
                <div class="field">
                    <div class="field__label">Password</div>
                    <div class="field__pwd-show / js-show-pwd"></div>
                    <input id="auth_pass" class="field__input / js-pwd" type="password" name="password" placeholder="Password" required>
                </div>

                <input id="log_geetest_challenge" type="hidden" name="geetest_challenge">
                <input id="log_geetest_validate" type="hidden" name="geetest_validate">
                <input id="log_geetest_seccode" type="hidden" name="geetest_seccode">

                <div class="field field--btn__new">
                    <input id="login_submit" class="btn__new btn__new--form" value="Authorise me" readonly disabled>
                </div>
            </form>

            <div class="popup__bottom-links-row">
                <a id="go_to_register" class="popup__bottom-link">Go to registration form</a>
                <a id="forgot_pwd" class="popup__bottom-link">Forgot password?</a>
                <a id="forgot_pwd_hide" data-fancybox href="#pwd_restore" class="popup__bottom-link" style="display: none">Forgot password?</a>
            </div>
        </div>
    </div>

    <input id="restore_error" hidden value='${recoveryError}'/>
    <div id="pwd_restore" class="popup">
        <div class="popup__inner">
            <div class="popup__caption">Forgot password?</div>
            <c:if test="${not empty recoveryError}">
                <div class='field__error' style="text-align: center">
                        ${recoveryError}
                </div>
            </c:if>

            <form id="pwd_restore_form" class="form" method="post">
                <div class="field">
                    <div class="field__label">Email</div>
                    <c:choose>
                        <c:when test="${not empty recoveryError}">
                            <input id="email_pwd_restore" class="field__input" type="email" name="email" placeholder="Email" value="${userEmail}" required>
                        </c:when>
                        <c:otherwise>
                            <input id="email_pwd_restore" class="field__input" type="email" name="email" placeholder="Email" required>
                        </c:otherwise>
                    </c:choose>
                    <div id="email_pwd_restore_wrong" class='field__error' style="display:none">
                        Wrong email
                    </div>
                    <div id="email_pwd_restore_notExist" class='field__error' style="display:none">
                        <loc:message code="login.notExists.email"/>
                    </div>
                </div>

                <div class="field field--btn__new">
                    <input id="pwd_restore_submit" class="btn__new btn__new--form" type="submit" value="Reset password" disabled>
                </div>
            </form>

            <div class="popup__bottom-links-row">
                <a id="back_login" class="popup__bottom-link popup__bottom-link--back"><loc:message code="login.button.backToLogin"/></a>
            </div>
        </div>
    </div>

    <%--PIN | START--%>
        <a id="pin_2fa_login_hide" data-fancybox href="#pin_2fa_login" class="popup__bottom-link" style="display: none">2fa Login</a>

    <c:if test="${pinNeed != null}">
        <script>
            $("document").ready(function() {
                setTimeout(function() {
                    $('#pin_2fa_login_hide').trigger('click');
                    alert("KY-KY");
                },10);
            });
        </script>
    </c:if>

        <c:url value="/login" var="loginUrl"/>
        <div id="pin_2fa_login" class="popup">
            <div class="popup__inner">
                <hr>
                <h4 class=""><loc:message
                        code="message.pin_code"/></h4>
                <h5 id="res">${pinNeed}</h5>
                <hr>
                <c:if test="${not empty pinError}">
                    <div class='field__error' style="text-align: center">
                            ${pinError}
                    </div>
                </c:if>
                <form id="pin_2fa_login_form" action="${loginUrl}" method="post" class="form">
                    <div class="field">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <loc:message code="message.pin_code" var="pin"/>
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label">${pin}</label>
                        </div>
                        <div class="col-md-9 input-block-wrapper__input-wrapper">
                            <input id="pin" name="l_pin" type="text" placeholder="${pin}" class="form-control input-block-wrapper__input"/>
                        </div>
                        <div id="email_pwd_restore_wrong" class='field__error' style="display:none">
                            Wrong email
                        </div>
                    </div>

                    <a id="send_pin_again" class="btn btn-link" style="margin-left: 80px;"><loc:message code="login.pin.sendagain"/></a>

                    <div id="send_pin_res""></div>
                    <br>

                    <div class="field field--btn__new">
                        <input id="pin_2fa_login_submit" class="btn__new btn__new--form" type="submit" value='<loc:message code="login.submit"/>'/>
                    </div>
                </form>
            </div>
        </div>
    <%--PIN | END--%>

    <div id="registration" class="popup">
        <div class="popup__inner">
            <div class="popup__caption">Registration</div>

            <form id="create_me" class="form" method="post">
                <input id="csrfC" type="hidden"  class="csrfC" name="_csrf"/>
                <%--<div class="field">--%>
                    <%--<div class="field__label">Nickname</div>--%>
                    <%--<input id="nickname" class="field__input" type="text" name="nickname" placeholder="Nickname" required>--%>
                    <%--<div id="nickname_exists" class='field__error' style="display:none">--%>
                        <%--Nichname exists--%>
                    <%--</div>--%>
                    <%--<div id="nichname_wrong" class='field__error' style="display:none">--%>
                        <%--Wrong nichname--%>
                    <%--</div>--%>
                <%--</div>--%>
                <div class="field">
                    <div class="field__label">Email</div>
                    <input id="email" class="field__input" type="email" name="email" placeholder="Email" required>
                    <div id="reg__email_exists" class='field__error' style="display:none">
                        <loc:message code="register.emailExists"/>
                    </div>
                    <div id="reg__email_wrong" class='field__error' style="display:none">
                        <loc:message code="register.emailWrong"/>
                    </div>
                    <div id="reg__email_regex" class='field__error' style="display:none">
                        <loc:message code="register.emailValidation"/>
                    </div>
                    <div id="reg__email_reequired" class='field__error' style="display:none">
                        <loc:message code="register.emailIsRequired"/>
                    </div>
                </div>

                <div class="field field--btn__new">
                    <input id="reg_submit" class="btn__new btn__new--form" type="submit" value="Create an account" disabled>
                </div>

                <div class="popup__bottom">
                    <div class="popup__privacy">
                        <input id="privacy__checked" class="privacy__checkbox" type="checkbox" />
                        I agree to exrates
                        <a href="/termsAndConditions" class="popup__bottom-link" target="_blank">Terms of Use</a>
                    </div>
                    <div class="popup__bottom-row">Already have an account? <a id="go_login" class="popup__bottom-link">Log in</a></div>
                </div>
            </form>
        </div>
    </div>

    <a id="pwd_unverifiedUser_hide" data-fancybox href="#pwd_unverifiedUser" class="popup__bottom-link" style="display: none"><loc:message code="register.unconfirmedUser"/></a>

    <input id="unverifiedUser_error" hidden value='${unconfirmedUser}'/>
    <div id="pwd_unverifiedUser" class="popup">
        <div class="popup__inner">
            <c:if test="${not empty unconfirmedUser}">
                <div class='field__error' style="text-align: center">
                        ${unconfirmedUser}
                </div>
            </c:if>

            <form id="pwd_unverifiedUser_form" class="form" method="post">
                <input type="hidden"  class="csrfC" name="_csrf" value="${_csrf.token}"/>
                <input id="unconfirmedUserEmail" name="unconfirmedUserEmail" hidden value='${unconfirmedUserEmail}'>

                <div class="field">${unconfirmedUserMessage}</div>

                <div class="field field--btn__new">
                    <input id="pwd_unverifiedUser_submit" class="btn__new--form" type="submit" value='<loc:message code="register.button.sendAgain"/>'>
                </div>
            </form>

            <div class="popup__bottom-links-row">
                <a id="back_login_from_unverifiedUser_error" class="popup__bottom-link popup__bottom-link--back"><loc:message code="login.button.backToLogin"/></a>
            </div>
        </div>
    </div>

    <a data-fancybox id="confirm-success" href="#confirm" class="demo-bar-item" style="display: none">Confirm</a>
    <div id="confirm" class="popup">
        <div class="popup__inner">
            <div class="popup__caption">Confirm the email</div>

            <div class="popup__text">
                We sended the confirmation link to<br>
                <a id="confirm_email" href="" class="popup__text-link"></a>
            </div>
            <div class="popup__text">
                Please check your email and follow instructions.
            </div>

            <div class="popup__hr"></div>

            <div class="popup__bottom">
                <div class="popup__bottom-row">If you haven't received the email, do the following:</div>
                <div class="popup__bottom-row">
                    Check spam or other folders.<br>
                    Set email address whitelist.<br>
                    Check the mail client works normally.
                </div>
            </div>
        </div>
    </div>

    <%--Geetest--%>
    <a data-fancybox id="geetest_confirm" href="#getest" class="demo-bar-item" style="display: none">Geetest</a>
    <div id="getest" class="popup">
        <div class="popup__inner">
            <div>
                <label>Verification</label>

                <div id="captcha_mssg" class="popup__text" style="display: none">
                    Resolve the captcha please<br>
                </div>

                <div class="popup__hr"></div>

                <div id="captcha1">
                    <div id="wait1" class="popup__text">
                        Loading verification code...<br>
                    </div>
                </div>
            </div>
            <br>
        </div>
    </div>

</header>

<%--capcha--%>
<c:if test="${showEntrance && !isAuth && captchaType==\"RECAPTCHA\"}">
    <script type="text/javascript" src="<c:url value='/client/js/capchahead.js'/>"></script>
    <c:set value="${pageContext.response.locale}" var="locale"></c:set>
    <c:if test="${locale=='cn'}">
        <c:set value="zh-CN" var="locale"></c:set>
    </c:if>
    <script src="https://www.google.com/recaptcha/api.js?onload=onloadCallbackHead&render=explicit&hl=${locale}"
            async defer>
    </script>
</c:if>
