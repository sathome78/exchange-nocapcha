<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-noty/2.3.7/packaged/jquery.noty.packaged.min.js"></script>

<c:set var="path" value="${fn:replace(pageContext.request.requestURI, '/WEB-INF/jsp', '')}"/>
<c:set var="path" value="${fn:replace(path, '.jsp', '')}"/>
<%--don't show entrance menu item in header for pages that contain it's own capcha because conflict occurs--%>
<sec:authorize access="isAuthenticated()" var="isAuth"/>
<c:set var="showEntrance" value="${
                                (path != '/login')
                                && (path != '/register')
                                && (path != '/forgotPassword')
                                && (path != '/login?error')}"/>
<header class="header">
    <div class="container">
        <div class="cols-md-2"><a href="/" class="logo"><img src="/client/img/Logo_blue.png" alt="Exrates Logo"></a>
        </div>
        <div class="cols-md-8">
            <ul class="nav header__nav">
                <sec:authorize access="isAuthenticated()">
                    <li id="menu-traiding"><a href="#" class="nav__link nav__link_active "><loc:message
                            code="dashboard.trading"/></a></li>
                    <li id="menu-mywallets"><a href="#" class="nav__link"><loc:message code="usermenu.mywallets"/></a>
                    </li>
                    <li id="menu-myhistory"><a href="#" class="nav__link"><loc:message code="usermenu.myorders"/></a>
                    </li>
                    <li id="menu-orders"><a href="#" class="nav__link"><loc:message code="usermenu.orders"/></a></li>
                </sec:authorize>
                <li><a href="<c:url value="http://support.exrates.me/" />" class="nav__link">
                    <loc:message code="dashboard.support"/></a>
                </li>
                <sec:authorize access="isAuthenticated()">
                    <li id="adminka-entry">
                        <c:set var="adminEnum" value="<%=me.exrates.model.enums.UserRole.ADMINISTRATOR%>"/>
                        <c:set var="accountantEnum" value="<%=me.exrates.model.enums.UserRole.ACCOUNTANT%>"/>
                        <c:set var="admin_userEnum" value="<%=me.exrates.model.enums.UserRole.ADMIN_USER%>"/>
                        <sec:authorize
                                access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                            <sec:authorize
                                    access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">

                                <a href="<c:url value='/admin'/>">
                                    <loc:message code="admin.title"/>
                                </a>

                            </sec:authorize>
                        </sec:authorize>
                    </li>

                    <li id="hello-my-friend"><a href="">
                        <loc:message code="dashboard.hello"/>
                        <strong><sec:authentication property="principal.username"/></strong></a>
                    </li>
                </sec:authorize>
            </ul>
        </div>
        <div class="cols-md-2">
            <ul class="padding0">
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
                                    <input name="username" type="email" placeholder=
                                        <loc:message code="dashboard.loginText"/>
                                            class="form_input">
                                    <input name="password" type="password" placeholder=
                                        <loc:message
                                                code="dashboard.passwordText"/> class="form_input">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                    <br/>
                                        <%--CAPTCHA--%>
                                    <div id="cpch-head-field" class="g-recaptcha"
                                         data-sitekey=${captchaProperties.get("captcha.key")}></div>
                                    <p class='cpch-error-message' style="color:red">${cpch}</p>
                                    <button type="submit" class="login_button"><loc:message
                                            code="dashboard.entrance"/></button>
                                    <a href="/forgotPassword" class="white forgot-password"><loc:message
                                            code="dashboard.forgotPassword"/></a>

                                    <div></div>
                                        <%--QR--%>
                                        <%--<div class="inline-block"><img src="/client/img/qr.jpg"></div>
                                        <span class="white margin-left20">To enter via QR code</span>--%>
                                    <sec:authorize access="! isAuthenticated()">
                                        <a href="/register" class="register"><loc:message code="dashboard.signUp"/></a>
                                    </sec:authorize>
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
                    <a id="language" class="dropdown-toggle focus-white nav__link" data-toggle="dropdown" href="#"
                       role="button" aria-haspopup="true" aria-expanded="false">
                        ${pageContext.response.locale.toString().toUpperCase()} <span class="caret"></span>
                    </a>
                    <ul class="dropdown-menu choose-language">
                        <li><a href="#" class="language">EN</a></li>
                        <li><a href="#" class="language">RU</a></li>
                        <li><a href="#" class="language">CH</a></li>
                        <li><a href="#" class="language">TH</a></li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</header>

<%--capcha--%>
<c:if test="${showEntrance && !isAuth}">
    <script type="text/javascript" src="<c:url value='/client/js/capchahead.js'/>"></script>
    <c:set value="${pageContext.response.locale}" var="locale"></c:set>
    <c:if test="${locale=='cn'}">
        <c:set value="zh-CN" var="locale"></c:set>
    </c:if>
    <script src="https://www.google.com/recaptcha/api.js?onload=onloadCallbackHead&render=explicit&hl=${locale}"
            async defer>
    </script>
</c:if>
