<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-noty/2.3.7/packaged/jquery.noty.packaged.min.js"></script>

<header>
    <c:set var="path" value="${fn:replace(pageContext.request.requestURI, '/WEB-INF/jsp', '')}"/>
    <c:set var="path" value="${fn:replace(path, '.jsp', '')}"/>
    <%--don't show entrance menu item in header for pages that contain it's own capcha because conflict occurs--%>
    <sec:authorize access="isAuthenticated()" var="isAuth"/>
    <c:set var="showEntrance" value="${
                                (path != '/login')
                                && (path != '/register')
                                && (path != '/forgotPassword')
                                && (path != '/login?error')}"/>
    <nav class="navbar">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                        aria-expanded="false" aria-controls="navbar">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="/"><img src="/client/img/logo.png" alt="Logo"></a>
            </div>

            <div id="navbar" class="collapse navbar-collapse">
                <ul class="nav navbar-nav">
                    <sec:authorize access="isAuthenticated()">
                        <%--ГЛАВНАЯ--%>
                        <li>
                            <a href="/" class="navabr__link active"><loc:message code="dashboard.general"/></a>
                        </li>
                        <%--НОВОСТИ--%>
                        <%--<li><a href="#"><loc:message code="dashboard.news"/></a></li>--%>
                        <%--ОБУЧЕНИЕ--%>
                        <%--<li><a href="#"><loc:message code="dashboard.training"/></a></li>--%>
                        <%--ЛИЧНЫЙ КАБИНЕТ--%>
                        <li>
                            <a href="<c:url value="/mywallets"/>" class="navabr__link"><loc:message
                                    code="dashboard.personalArea"/></a
                        </li>

                        <%--Добрый день--%>
                        <li><a href=""><loc:message code="dashboard.hello"/> <strong><sec:authentication
                                property="principal.username"/></strong></a></li>
                        <%--ВЫЙТИ--%>
                        <li>
                            <c:url value="/logout" var="logoutUrl"/>
                            <form action="${logoutUrl}" id="logoutForm" method="post">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <button type="submit" class="btn btn-link">
                                    <a>
                                        <h5><strong><loc:message code="dashboard.goOut"/></strong></h5>
                                    </a>
                                </button>
                            </form>
                        </li>
                    </sec:authorize>

                    <sec:authorize access="!isAuthenticated()">
                        <c:if test="${showEntrance}">
                            <%--ВОЙТИ--%>
                            <li class="margin-left">
                                <a href="#" data-toggle="modal" data-target="#myModal"><loc:message
                                        code="dashboard.entrance"/></a></li>
                        </c:if>
                        <%--РЕГИСТРАЦИЯ--%>
                        <li>
                            <a href="<c:url value="/register" />"><loc:message code="dashboard.signUp"/></a>
                        </li>
                    </sec:authorize>

                    <%--ПЕРЕКЛЮЧЕНИЕ ЯЗЫКОВ--%>
                    <li role="presentation" class="dropdown closed">
                        <a href="#" id="language" class="dropdown-toggle" data-toggle="dropdown"
                           aria-expanded="true">
                            ${pageContext.response.locale} <span class="caret"></span>
                        </a>
                        <ul class="dropdown-menu" id="languageUl">
                            <li><a class="lang__item" href="#">English</a></li>
                            <li><a class="lang__item" href="#">Русский</a></li>
                            <%--<li><a class="lang__item" href="#">Chinese</a></li>--%>
                        </ul>
                    </li>
                </ul>
            </div>
            <!--/.nav-collapse -->
        </div>
    </nav>
</header>

<!-- Modal SIGN IN -->
<sec:authorize access="!isAuthenticated()">
    <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="myModalLabel"><loc:message code="dashboard.entrance"/></h4>
                </div>
                <div class="modal-body modal-content__input-block-wrapper">
                    <div class="content modal-content__content-wrapper">
                        <c:url value="/login" var="loginUrl"/>
                        <form action="${loginUrl}" method="post">
                                <%--логин--%>
                            <input type="text" name="username" placeholder=<loc:message code="dashboard.loginText"/>>
                                <%--пароль--%>
                            <input type="password" name="password" placeholder=<loc:message
                                    code="dashboard.passwordText"/>>
                                <%--csrf--%>
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <br/>
                                <%--CAPCHA--%>
                            <div id="cpch-head-field" class="g-recaptcha"
                                 data-sitekey=${captchaProperties.key}></div>
                            <p class='cpch-error-message' style="color:red">${cpch}</p>
                                <%--войти--%>
                            <button type="submit" class="button_enter"><loc:message code="dashboard.entrance"/></button>
                                <%--Забыли пароль?--%>
                            <a class="button_forgot" href="/forgotPassword"><loc:message
                                    code="dashboard.forgotPassword"/></a>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</sec:authorize>

<%--capcha--%>
<c:if test="${showEntrance && !isAuth}">
    <script type="text/javascript" src="<c:url value='/client/js/capchahead.js'/>"></script>
    <script src="https://www.google.com/recaptcha/api.js?onload=onloadCallbackHead&render=explicit&hl=${pageContext.response.locale}"
            async defer>
    </script>
</c:if>
