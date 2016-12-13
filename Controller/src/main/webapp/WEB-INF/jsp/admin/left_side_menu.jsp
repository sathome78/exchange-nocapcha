<%@ page import="me.exrates.model.enums.AdminAuthority" %><%--
  Created by IntelliJ IDEA.
  User: ogolv
  Date: 27.07.2016
  Time: 11:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<script type="text/javascript">
    $(function () {
        var title = $('title').text().trim();
        var $menuItem = $('.sidebar > ul li').filter(function (index) {

            return ($(this).text().trim() === title) ||
                ($(this).find('ul li').filter(function () {
                    return ($(this).text().trim() === title);
                }).length > 0);

        });
        $menuItem.children('a').wrapInner('<strong></strong>');
    })
</script>

<div id="admin_side_menu" class="col-md-2">
    <c:set var="adminEnum" value="<%=me.exrates.model.enums.UserRole.ADMINISTRATOR%>"/>
    <c:set var="accountantEnum" value="<%=me.exrates.model.enums.UserRole.ACCOUNTANT%>"/>
    <c:set var="admin_userEnum" value="<%=me.exrates.model.enums.UserRole.ADMIN_USER%>"/>
    <c:set var="admin_processWithdraw" value="<%=AdminAuthority.PROCESS_WITHDRAW%>"/>
    <c:set var="admin_processInvoice" value="<%=AdminAuthority.PROCESS_INVOICE%>"/>
    <c:set var="admin_deleteOrder" value="<%=AdminAuthority.DELETE_ORDER%>"/>
    <c:set var="admin_commentUser" value="<%=AdminAuthority.COMMENT_USER%>"/>
    <c:set var="admin_manageSessions" value="<%=AdminAuthority.MANAGE_SESSIONS%>"/>
    <c:set var="admin_currencyLimits" value="<%=AdminAuthority.SET_CURRENCY_LIMIT%>"/>
    <c:set var="admin_manageAccess" value="<%=AdminAuthority.MANAGE_ACCESS%>"/>
<div class="sidebar">
    <ul>
        <li>
            <%--Пользователи--%>
            <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                <a href="<c:url value='/admin/users'/>"><loc:message code="admin.users"/></a>
            </sec:authorize>
        </li>


        <li>
            <%--Администраторы--%>
            <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                <a href="<c:url value='/admin/administrators'/>"><loc:message code="admin.admins"/></a>
            </sec:authorize>
        </li>


        <li>
            <%--Заявки на пополнение валюты--%>
            <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                <a href="<c:url value='/admin/invoiceConfirmation'/>"><loc:message code="transaction.titleInvoice"/></a>
            </sec:authorize>
        </li>

        <li>
            <%--Заявки на пополнение Bitcoin--%>
            <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                <a href="<c:url value='/admin/bitcoinConfirmation'/>"><loc:message code="transaction.titleBitcoin"/></a>
            </sec:authorize>
        </li>

        <li>
            <%--withdraw--%>
            <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                <a href="<c:url value='/admin/withdrawal'/>"><loc:message code="admin.withdrawRequests"/></a>
            </sec:authorize>
        </li>
        <%--Удаление ордера--%>
        <li>

            <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                <a href="<c:url value='/admin/removeOrder'/>"><loc:message code="deleteorder.title"/></a>
            </sec:authorize>
        </li>


        <li>
            <%--Финансисты--%>
            <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                <a href="#finMenu"  data-toggle="collapse"><loc:message code="admin.finance"/><i class="fa fa-caret-down"></i></a>
                <div class="collapse" id="finMenu">
                    <ul>
                        <li><a href="<c:url value='/companywallet'/>"><loc:message code="admin.companyWallet"/></a></li>
                        <li><a href="<c:url value='/userswallets'/>"><loc:message code="admin.usersWallet"/></a></li>
                        <li><a href="<c:url value='/admin/editCurrencyLimits'/>"><loc:message code="admin.currencyLimits.title"/></a></li>
                        <li><a href="<c:url value='/admin/commissions'/>"><loc:message code="admin.commissions"/></a></li>
                    </ul>
                </div>

            </sec:authorize>
        </li>


        <li>
            <%--referral--%>
            <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                <a href="<c:url value='/admin/referral'/>"><loc:message code="admin.referral"/></a>
            </sec:authorize>
        </li>

        <li>
            <%--referral--%>
            <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                <a href="<c:url value='/admin/sessionControl'/>"><loc:message code="admin.sessionControl"/></a>
            </sec:authorize>
        </li>




    </ul>
</div>
</div>
