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
    <title><loc:message code="mywallets.title"/></title>
    <meta name="keywords" content="" />
    <meta name="description" content="" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <link href="<c:url value='/client/css/bootstrap.css'/>" rel="stylesheet" type="text/css" />
    <link href="<c:url value='/client/css/chosen.css'/>" rel="stylesheet" type="text/css" />
    <link href="<c:url value='/client/css/style.css'/>" rel="stylesheet" type="text/css" />

    <script type="text/javascript" src="<c:url value='/client/js/jquery.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/dropdown.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/tab.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/modal.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/chosen.jquery.min.js'/>"></script>
</head>

<body>
<div class="wrapper lk">


    <div class="container container_center full__height">

        <!--#include file="sidebar__lk.shtml" -->
        <%@include file='usermenu.jsp'%>

        <div class="main__content">

            <!--#include file="header__lk.shtml" -->
            <%@include file='header.jsp'%>
            <div class="content__page">
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

                    <%--&lt;%&ndash;<td><div class=".btn-group-vertical" >&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<button type="button" class="btn btn-primary">Apple</button>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<button type="button" class="btn btn-primary">Samsung</button>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;</div></td>&ndash;%&gt;--%>

                    <%--<c:forEach items="${withdrawalRequests}" var="requests">--%>
                    <%--<tr>--%>
                    <%--<td>--%>
                    <%--${requests.transaction.datetime}--%>
                    <%--</td>--%>
                    <%--<td>--%>
                    <%--${requests.userEmail}--%>
                    <%--</td>--%>
                    <%--<td>--%>
                    <%--${requests.transaction.amount}--%>
                    <%--</td>--%>
                    <%--<td>--%>
                    <%--${requests.transaction.amount}--%>
                    <%--</td>--%>
                    <%--<td>--%>
                    <%--${requests.transaction.currency.name}--%>
                    <%--</td>--%>
                    <%--<td>--%>
                    <%--${requests.transaction.merchant.name}--%>
                    <%--</td>--%>
                    <%--<td>--%>
                    <%--${requests.wallet}--%>
                    <%--</td>--%>
                    <%--<td>--%>
                    <%--<c:if test="${not empty requests.acceptance}">--%>
                    <%--${requests.acceptance}--%>
                    <%--</c:if>--%>
                    <%--<c:otherwise>--%>
                    <%--_--%>
                    <%--</c:otherwise>--%>
                    <%--</td>--%>
                    <%--<td>--%>
                    <%--<c:if test="${not empty requests.processedBy}">--%>
                    <%--${requests.processedBy}--%>
                    <%--</c:if>--%>
                    <%--<c:otherwise>--%>
                    <%--_--%>
                    <%--</c:otherwise>--%>
                    <%--</td>--%>
                    <%--</tr>--%>
                    <%--</c:forEach>--%>
                    </tbody>
                </table>
            </div>
            <!--#include file="footer__lk.shtml" -->
            <%@include file='footer.jsp'%>
        </div>

    </div>

</div>

</body>
</html>
