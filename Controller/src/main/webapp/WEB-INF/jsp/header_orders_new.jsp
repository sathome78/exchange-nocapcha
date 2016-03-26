<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>


<header>
    <nav class="navbar">
        <%--пока не понял--%>
        <a href="#" class="mobile__menu__toggle glyphicon-align-justify"></a>

        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                        aria-expanded="false" aria-controls="navbar">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a href="<c:url value="/"/>">
                    <img src="<c:url value="/client/img/logo.png"/>" alt=""/>
                </a>
            </div>
            <div id="navbar" class="collapse navbar-collapse">
                <ul class="nav navbar-nav">
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
                    <%--ПЕРЕКЛЮЧЕНИЕ ЯЗЫКОВ--%>
                    <li role="presentation" class="dropdown closed">
                        <a href="#" id="language" class="dropdown-toggle" data-toggle="dropdown"
                           aria-expanded="true">
                            ${pageContext.response.locale} <span class="caret"></span>
                        </a>
                        <ul class="dropdown-menu" id="languageUl">
                            <li><a class="lang__item" href="#">English</a></li>
                            <li><a class="lang__item" href="#">Русский</a></li>
                            <li><a class="lang__item" href="#">Chinese</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
            <!--/.nav-collapse -->
        </div>
    </nav>
</header>
