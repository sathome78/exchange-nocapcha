<%@page language="java"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <!--[if lt IE 9]><script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
    <title><loc:message code="transactions.title"/></title>
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
    <script type="text/javascript" src="<c:url value='/client/js/function.js'/>"></script>

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
               <c:choose>
                   <c:when test="${fn:length(transactions)==0}">
                       <loc:message code="transactions.absent"/>
                   </c:when>
                   <c:otherwise>
                       <c:forEach var="transaction" items="${transactions}">
                           <div>
                               <table class="table">
                                   <tr>
                                       <td><loc:message code="transaction.id"/>:</td>
                                       <td><loc:message code="transaction.walletId"/></td>
                                       <td><loc:message code="transaction.currency"/></td>
                                       <td><loc:message code="transaction.amount"/></td>
                                       <td><loc:message code="transaction.transactionType"/></td>
                                       <td><loc:message code="transaction.date"/></td>
                                       <td><loc:message code="transaction.commission"/></td>
                                   </tr>
                                   <tr>
                                       <td>
                                           <label>${transaction.id}</label>
                                       </td>
                                       <td>
                                           <label>${transaction.walletId}</label>
                                       </td>
                                       <td>
                                           <label>${transaction.currency}</label>
                                       </td>
                                       <td>
                                           <label>${transaction.amount}</label>
                                       </td>
                                       <td>
                                           <label>${transaction.operationType}</label>
                                       </td>
                                       <td>
                                           <label>${transaction.date}</label>
                                       </td>
                                       <td>
                                           <label>${transaction.commission}</label>
                                       </td>
                                   </tr>
                               </table>
                           </div>
                       </c:forEach>
                   </c:otherwise>
               </c:choose>
            </div>

            <!--#include file="footer__lk.shtml" -->
            <%@include file='footer.jsp'%>
        </div>

    </div>

</div>

</body>
</html>
