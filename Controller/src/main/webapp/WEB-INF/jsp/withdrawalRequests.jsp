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
    <%@include file='admin/links_scripts.jsp'%>
    <script type="text/javascript" src="<c:url value='/client/js/withdrawal.js'/>"></script>



</head>

<body>
<%@include file='fragments/header-simple.jsp'%>
<main class="container">
    <div class="row">
        <%@include file='admin/left_side_menu.jsp' %>
        <div class="col-md-10 content text-center admin-container">
            <h4><loc:message code="admin.withdrawRequests"/></h4>
            <table id="withdrawalTable"> <%--class="table table-responsive table-hover table-bordered" border="1" cellpadding="8"
                   cellspacing="0">--%>
                <thead>
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
                                ${requests.transaction.datetime.toLocalDate()}<br/>
                                ${requests.transaction.datetime.toLocalTime()}
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
                                ${requests.merchantImage.image_name}

                        </td>
                        <td>
                                ${requests.wallet}
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty requests.acceptance}">
                                    ${requests.acceptance.toLocalDate()}<br/>
                                    ${requests.acceptance.toLocalTime()}
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
    </div>
</main>
<div id="accepted" style="display: none">
    <loc:message code="merchants.withdrawRequestAccepted"/>
</div>
<div id="prompt_acc_rqst" style="display: none">
    <loc:message code="merchants.promptWithdrawRequestAccept"/>
</div>
<div id="prompt_dec_rqst" style="display: none">
    <loc:message code="merchants.promptWithdrawRequestDecline"/>
</div>


<%--


<div class="wrapper lk" style="max-width: 1300px">


    <div class="container container_center full__height" style="max-width: 1300px">

        <!--#include file="sidebar__lk.shtml" -->
        <%@include file='usermenu.jsp'%>

        <div class="main__content">
            <!--#include file="header__lk.shtml" -->
            <div class="content__page">

            </div>
            <!--#include file="footer__lk.shtml" -->
            <%@include file='footer.jsp'%>
        </div>
    </div>
</div>
--%>

</body>
</html>
