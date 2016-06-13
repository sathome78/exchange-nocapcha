<%@page language="java"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <!--[if lt IE 9]><script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
    <title><loc:message code="admin.withdrawRequests"/></title>
    <meta name="keywords" content="" />
    <meta name="description" content="" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <link href="<c:url value='/client/css/bootstrap.css'/>" rel="stylesheet" type="text/css" />
    <link href="<c:url value='/client/css/chosen.css'/>" rel="stylesheet" type="text/css" />
    <link href="<c:url value='/client/css/style-old.css'/>" rel="stylesheet" type="text/css" />
    <link href="<c:url value='/client/css/jquery.dataTables.min.css'/>" rel="stylesheet" type="text/css"/>

    <script type="text/javascript" src="<c:url value='/client/js/jquery.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/withdrawal.js'/>"></script>
    <script type="text/javascript" src="/client/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="<c:url value='/client/js/chosen.jquery.min.js'/>"></script>
</head>

<body>
<div class="wrapper lk" style="max-width: 1300px">


    <div class="container container_center full__height" style="max-width: 1300px">

        <!--#include file="sidebar__lk.shtml" -->
        <%@include file='usermenu.jsp'%>

        <div class="main__content">
            <!--#include file="header__lk.shtml" -->
            <%@include file='header.jsp'%>
            <div class="content__page" style="height: 100% !important; overflow: scroll;">
                <table id="withdrawalTable" class="table table-responsive table-hover table-bordered table-striped" border="1" cellpadding="8"
                       cellspacing="0">
                    <thead style="border-style: none;">
                    <tr>
                        <th><loc:message code="withdrawal.requestDatetime"/></th>
                        <th><loc:message code="withdrawal.user"/></th>
                        <th><loc:message code="withdrawal.amount"/></th>
                        <th><loc:message code="withdrawal.currency"/></th>
                        <th><loc:message code="withdrawal.commission"/></th>
                        <th><loc:message code="withdrawal.merchant"/></th>
                        <th><loc:message code="withdrawal.wallet"/></th>
                        <th><loc:message code="withdrawal.acceptanceDatetime"/></th>
                        <th><loc:message code="withdrawal.acceptanceUser"/></th>
                        <th><loc:message code="withdrawal.status"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${requests}" var="requests">
                        <tr class="id_${requests.transaction.id}">
                            <td>
                                    ${requests.transaction.datetime}
                                <%--<fmt:parseDate value="${requests.transaction.datetime}" var="parsedDate"--%>
                                               <%--pattern="yyyy-MM-dd'T'HH:mm:ss"/>--%>
                                <%--<fmt:formatDate value="${parsedDate}" pattern="yyyy-MM-dd'<br/>'HH:mm:ss"/>--%>
                            </td>
                            <td>
                                ${requests.userEmail}
                            </td>
                            <td>
                                <fmt:formatNumber value="${requests.transaction.amount}" maxFractionDigits="9"/>
                            </td>
                            <td>
                                ${requests.transaction.currency.name}
                            </td>
                            <td>
                                <fmt:formatNumber value="${requests.transaction.commissionAmount}" maxFractionDigits="9"/>
                            </td>
                            <td>
                                ${requests.transaction.merchant.name}
                            </td>
                            <td>
                                ${requests.wallet}
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty requests.acceptance}">
                                        ${requests.acceptance}
                                        <%--<fmt:parseDate value="${requests.acceptance}" var="parsedDate"--%>
                                                       <%--pattern="yyyy-MM-dd'T'HH:mm:ss"/>--%>
                                        <%--<fmt:formatDate value="${parsedDate}" pattern="yyyy-MM-dd'<br/>'HH:mm:ss"/>--%>
                                    </c:when>
                                    <c:otherwise>
                                        _
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty requests.processedBy}">
                                        ${requests.processedBy}
                                    </c:when>
                                    <c:otherwise>
                                        _
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${requests.transaction.provided}">
                                        <loc:message code="merchants.withdrawRequestAccepted"/>
                                    </c:when>
                                    <c:otherwise>
                                        <form class="accept_withdrawal_rqst">
                                            <input type="hidden" name="requestId" value="${requests.transaction.id}">
                                            <button type="submit" class="btn btn-link">
                                                <loc:message code="merchants.withdrawRequestAccept"/>
                                            </button>
                                        </form>
                                        <form class="decline_withdrawal_rqst">
                                            <input type="hidden" name="requestId" value="${requests.transaction.id}">
                                            <button type="submit" class="btn btn-link">
                                                <loc:message code="merchants.withdrawRequestDecline"/>
                                            </button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>

                    </c:forEach>
                    </tbody>
                </table>
            </div>
            <!--#include file="footer__lk.shtml" -->
            <%@include file='footer.jsp'%>
        </div>
    </div>
</div>
<div id="accepted">
    <loc:message code="merchants.withdrawRequestAccepted"/>
</div>
<div id="prompt_acc_rqst" style="display: none">
    <loc:message code="merchants.promptWithdrawRequestAccept"/>
</div>
<div id="prompt_dec_rqst" style="display: none">
    <loc:message code="merchants.promptWithdrawRequestDecline"/>
</div>
</body>
</html>
