<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%> 
<%--<!DOCTYPE html>--%>
<%--<html>--%>
<%--<head>--%>
  <%--<title></title>--%>
 <%--</head>--%>
<%--<body>--%>
    <%--<div style="margin-top: 10px;">--%>
    <%--<p><a href="mywallets" ><loc:message code="usermenu.accounts" /></a>--%>
    <%--<p><a href="orders" ><loc:message code="usermenu.orders" /></a>--%>
    <%--<p><a href="merchants/input" ><loc:message code="usermenu.enter" /></a>--%>
    <%--<p><a href="/" ><loc:message code="usermenu.history" /></a>--%>
    <%--<p><a href="/" ><loc:message code="usermenu.settings" /></a>--%>
    <%--</div>--%>

<%--</body>--%>
<%--</html>--%>

<aside class="sidebar">

    <!-- begin Logo block -->
    <div class="header__logo">
        <a href="<c:url value="/"/>">
            <img src="<c:url value="/client/img/logo.png"/>" alt=""/>
        </a>
    </div>
    <!-- end Logo block -->

    <div class="sidebar__wrapper">
        <!-- begin navbar -->
        <ul class="navbar">
            <li class="navabr__item">
                <a href="<c:url value="/mywallets"/>"
                   class="navabr__link">Мои счета
                </a>
            </li>
            <li class="navabr__item">
                <a href="#" class="navabr__link">Ордера</a>
            </li>
            <li class="navabr__item">
                <a href="<c:url value="/merchants/input"/>" class="navabr__link">Ввод средств</a>
            </li>
            <li class="navabr__item">
                <a href="<c:url value="/merchants/output"/>" class="navabr__link">Вывод средств</a>
            </li>
            <li class="navabr__item">
                <a href="#" class="navabr__link">История операций</a>
            </li>
            <li class="navabr__item">
                <a href="#" class="navabr__link">Настройки</a>
            </li>
        </ul>
        <!-- end navbar -->


        <!-- begin sub__navbar  -->
        <ul class="sub__navbar ">
            <li class="navabr__item">
                <a href="#" class="navabr__link">Комиссии</a>
            </li>
            <li class="navabr__item">
                <a href="#" class="navabr__link">Новости</a>
            </li>
            <li class="navabr__item">
                <a href="#" class="navabr__link">API</a>
            </li>
            <li class="navabr__item">
                <a href="#" class="navabr__link">Правила</a>
            </li>
            <li class="navabr__item">
                <a href="#" class="navabr__link">FAQ</a>
            </li>
        </ul>
        <!-- end sub__navbar  -->
    </div>
</aside>