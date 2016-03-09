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
                  		 <div>
                  		 <table class="table">
                          <tbody>
							 <thead>
                               <tr>
                                   <th class="col-xs-4"><loc:message code="transaction.datetime"/></th>
                                   <th class="col-xs-4"><loc:message code="transaction.operationType"/></th>
                                   <th class="col-xs-4"><loc:message code="transaction.currency"/></th>
                                   <th class="col-xs-4"><loc:message code="transaction.amount"/></th>
                                   <th class="col-xs-4"><loc:message code="transaction.currencyBuy"/></th>
                                   <th class="col-xs-4"><loc:message code="transaction.amountBuy"/></th>
                                   <th class="col-xs-4"><loc:message code="transaction.commissionAmount"/></th>
                                   <th class="col-xs-4"><loc:message code="transaction.merchant"/></th>
                                </tr>
                               </thead>
                       			<c:forEach var="transaction" items="${transactions}">
                                 <tr>
                                      <td>
                                           ${transaction.datetime}
                                      </td>
                                      <td>
                                          <c:choose>
                                          	<c:when test="${transaction.operationType.type eq 1 or transaction.operationType.type eq 2}">
                                          		<loc:message code="transaction.operationType${transaction.operationType}"/>
                                          	</c:when>
                                          	<c:when test="${transaction.orderStatus.status eq 2}">
                                          		<loc:message code="transaction.operationTypeCreateOrder"/>
                                          	</c:when>
                                          	<c:when test="${transaction.orderStatus.status eq 3}">
                                          		<loc:message code="transaction.operationTypeAcceptOrder"/>
                                          	</c:when>
                                          </c:choose>
                                      </td>
                                      <td>
                                           ${transaction.currency}
                                       </td>
                                       <td>
                                          <fmt:formatNumber value="${transaction.amount}" maxFractionDigits="9"/>
                                       </td>
                                       <td>
                                       	   ${transaction.currencyBuy}                                 		
                                       </td>
                                       <td>
                                       	  <fmt:formatNumber value="${transaction.amountBuy}" maxFractionDigits="9"/>
                                       </td>
                                       <td>
                                          <fmt:formatNumber value="${transaction.commissionAmount}" maxFractionDigits="9"/>
                                        </td>
                                       <td>
                                           <c:if test="${transaction.orderStatus eq null}">
                                           		<loc:message code="transaction.${transaction.merchant.name}"/>
                                           </c:if>
                                       </td>
                                    </tr>
                                  </c:forEach>
                                  </tbody>
                               </table>
                           </div>
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
