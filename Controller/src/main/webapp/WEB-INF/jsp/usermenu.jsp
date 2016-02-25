<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%> 
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
                <a href="<c:url value="/mywallets"/>" class="navabr__link"><loc:message code="usermenu.mywallets"/></a>
            </li>
            <li class="navabr__item">
                <a href="<c:url value="/orders"/>" class="navabr__link"><loc:message code="usermenu.orders"/></a>
            </li>
            <li class="navabr__item">
                <a href="<c:url value="/myorders"/>" class="navabr__link"><loc:message code="usermenu.myorders"/></a>
            </li>
            <li class="navabr__item">
                <a href="<c:url value="/merchants/input"/>" class="navabr__link"><loc:message code="usermenu.inputCredits"/></a>
            </li>
            <li class="navabr__item">
                <a href="<c:url value="/merchants/output"/>" class="navabr__link"><loc:message code="usermenu.outputCredits"/></a>
            </li>
            <li class="navabr__item">
                <a href="<c:url value="/transaction"/>" class="navabr__link"><loc:message code="usermenu.history"/></a>
            </li>
            <%--<li class="navabr__item">--%>
                <%--<a href="#" class="navabr__link"><loc:message code="usermenu.settings"/></a>--%>
            <%--</li>--%>
        </ul>
        <!-- end navbar -->


        <!-- begin sub__navbar  -->
        <ul class="sub__navbar ">
            <li class="navabr__item">
                <a href="#" class="navabr__link"><loc:message code="subusermenu.commissions"/></a>
            </li>
            <li class="navabr__item">
                <a href="#" class="navabr__link"><loc:message code="subusermenu.news"/></a>
            </li>
            <li class="navabr__item">
                <a href="#" class="navabr__link"><loc:message code="subusermenu.api"/></a>
            </li>
            <li class="navabr__item">
                <a href="#" class="navabr__link"><loc:message code="subusermenu.rules"/></a>
            </li>
            <li class="navabr__item">
                <a href="#" class="navabr__link"><loc:message code="subusermenu.faq"/></a>
            </li>
        </ul>
        <!-- end sub__navbar  -->
    </div>
</aside>