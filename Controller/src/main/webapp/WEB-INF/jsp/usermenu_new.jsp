<%--<%@ page contentType="text/html; charset=UTF-8" language="java" %>--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%--<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>--%>


<div class="side_menu col-sm-3"> <!-- Side Menu -->
    <ul>
        <li><a class="side_menu__item side_menu--merchants-input" href="<c:url value="/merchants/input"/>"><loc:message code="usermenu.inputCredits"/></a></li>
        <li><a class="side_menu__item side_menu--merchants-output" href="<c:url value="/merchants/output"/>"><loc:message code="usermenu.outputCredits"/></a></li>
        <li><a class="side_menu__item side_menu--transaction" href="<c:url value="/transaction"/>"><loc:message code="usermenu.history"/></a></li>
        <li><a class="side_menu__item side_menu--settings" href="<c:url value="/settings"/>" class="navabr__link"><loc:message code="usermenu.settings"/></a></li>

        <li>
            <c:set var="adminEnum" value="<%=me.exrates.model.enums.UserRole.ADMINISTRATOR%>"/>
            <c:set var="accountantEnum" value="<%=me.exrates.model.enums.UserRole.ACCOUNTANT%>"/>
            <c:set var="admin_userEnum" value="<%=me.exrates.model.enums.UserRole.ADMIN_USER%>"/>
            <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                <a class="side_menu__item side_menu--admin" href="<c:url value="/admin"/>" class="navabr__link"><loc:message code="admin.title"/></a>
            </sec:authorize>
        </li>

    </ul>
</div>
<!-- end Side Menu -->
