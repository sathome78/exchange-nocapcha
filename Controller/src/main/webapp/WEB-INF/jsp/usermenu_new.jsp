<%@ page import="me.exrates.controller.AdminController" %><%--<%@ page contentType="text/html; charset=UTF-8" language="java" %>--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%--<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>--%>


<div class="side_menu col-sm-3"> <!-- Side Menu -->
    <ul>
        <li>
            <sec:authorize access="<%=AdminController.adminAnyAuthority%>">
                <a class="side_menu__item side_menu--admin" href="<c:url value="/2a8fy7b07dxe44"/>" class="navabr__link"><loc:message code="admin.title"/></a>
            </sec:authorize>
        </li>

    </ul>
</div>
<!-- end Side Menu -->
