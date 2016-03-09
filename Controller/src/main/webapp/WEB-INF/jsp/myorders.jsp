<%@page language="java"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<!--[if lt IE 9]><script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
	<title><loc:message code="myorders.title"/></title>
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
					<div class="title__page">
						<form:form action="order/new">
							 <loc:message code="myorders.create" var="labelCreate"/>
				     		 <input type="submit" value="${labelCreate}" class="btn btn-primary" />
				     	</form:form>
					</div>
					<c:choose>

						<c:when test="${fn:length(orderMap.sell)==0 && fn:length(orderMap.buy)==0}">
							<loc:message code="myorders.noorders"/>
						</c:when>

					<c:otherwise>
					<div class="title__page"><loc:message code="myorders.title"/></div>
					<loc:message code="myorders.text"/><br><br><br>
					
					<div style="color:blue">
						<c:if test="${msg eq 'delete'}" >
							<br><loc:message code="myorders.deletesuccess"/><br><br>
						</c:if>
						<c:if test="${msg eq 'deletefailed'}" >
							<br><loc:message code="myorders.deletefailed"/><br><br>
						</c:if>
					</div>
					<c:if test="${fn:length(orderMap.sell) ne 0}">
					<div class="title__page"><loc:message code="myorders.sellorders"/></div>
					<!-- begin orders__sell__buy -->
					<div class="orders__sell__buy">
						<div class="row">
							<div class="col-sm-6">
							<table class="table">
								<tbody>
									<thead>
									  <tr>
										<th class="col-xs-4"><loc:message code="myorders.currsell"/></th>
										<th class="col-xs-4"><loc:message code="myorders.amountsell"/></th>
										<th class="col-xs-4"><loc:message code="myorders.currbuy"/></th>
										<th class="col-xs-4"><loc:message code="myorders.amountbuy"/></th>
										<th class="col-xs-4"><loc:message code="myorders.commission"/></th>
										<th class="col-xs-4"><loc:message code="myorders.amountwithcommission"/></th>
										<th class="col-xs-4"><loc:message code="myorders.datecreation"/></th>
										<th class="col-xs-4"><loc:message code="myorders.datefinal"/></th>
										<th class="col-xs-4"><loc:message code="myorders.status"/></th>
										<th class="col-xs-4"></th>
				   					 </tr>
				   					</thead>
								 <c:forEach var="myorder" items="${orderMap.sell}">
									<tr>
										<td>
											${myorder.currencySellString}
										</td>
										<td>
											<fmt:formatNumber type="number" maxFractionDigits="9" value="${myorder.amountSell}"/>
										</td>
										<td>
											${myorder.currencyBuyString}
										</td>
										<td>
											<fmt:formatNumber type="number" maxFractionDigits="9" value="${myorder.amountBuy}"/>
										</td>
										<td>
											<fmt:formatNumber type="number" maxFractionDigits="9" value="${myorder.commission}"/>%
										</td>
										<td>
											<fmt:formatNumber type="number" maxFractionDigits="9" value="${myorder.amountBuyWithCommission}"/>
										</td>
										<td>
											${myorder.dateCreation}
										</td>
										<td>
											<c:if test="${myorder.status.status eq 3}">
												${myorder.dateFinal}
											</c:if>
										</td>
										<td>
											${myorder.statusString}
										</td>
				     					<td>
				     						<c:if test="${(myorder.status.status eq 2)||(myorder.status.status eq 1)}">
				     							<a href="myorders/submitdelete?id=${myorder.id}"><loc:message code="myorders.delete"/></a>
				     						</c:if>  
				     					</td>
								</tr>
								</c:forEach>
							  </tbody>
							</table>
							</div>
				</div>
				</div>
				</c:if>
				<c:if test="${fn:length(orderMap.buy) ne 0}">
				<div class="title__page"><loc:message code="myorders.buyorders"/></div>
					<!-- begin orders__sell__buy -->
					<div class="orders__sell__buy">
						<div class="row">
							<div class="col-sm-6">
							<table class="table">
								<tbody>
									<thead>
									  <tr>
										<th class="col-xs-4"><loc:message code="myorders.currbuy"/></td>
										<th class="col-xs-4"><loc:message code="myorders.amountbuy"/></td>
										<th class="col-xs-4"><loc:message code="myorders.currsell"/></td>
										<th class="col-xs-4"><loc:message code="myorders.amountsell"/></td>
										<th class="col-xs-4"><loc:message code="myorders.commission"/></td>
										<th class="col-xs-4"><loc:message code="myorders.amountwithcommission"/></td>
										<th class="col-xs-4"><loc:message code="myorders.datecreation"/></td>
										<th class="col-xs-4"><loc:message code="myorders.datefinal"/></td>
										<th class="col-xs-4"><loc:message code="myorders.status"/></td>
										<th class="col-xs-4"></th>
				   					 </tr>
				   					</thead>
								 <c:forEach var="myorder" items="${orderMap.buy}">
									<tr>
										<td>
											${myorder.currencyBuyString}
										</td>
										<td>
											<fmt:formatNumber type="number" maxFractionDigits="9" value="${myorder.amountBuy}"/>
										</td>
										<td>
											${myorder.currencySellString}
										</td>
										<td>
											<fmt:formatNumber type="number" maxFractionDigits="9" value="${myorder.amountSell}"/>
										</td>
										<td>
											<fmt:formatNumber type="number" maxFractionDigits="9" value="${myorder.commission}"/>%
										</td>
										<td>
											<fmt:formatNumber type="number" maxFractionDigits="9" value="${myorder.amountBuyWithCommission}"/>
										</td>
										<td>
											${myorder.dateCreation}
										</td>
										<td>
											<c:if test="${myorder.status.status eq 3}">
												${myorder.dateFinal}
											</c:if>
										</td>
										<td>
											${myorder.statusString}
										</td>
				     					<td>
				     						<c:if test="${(myorder.status.status eq 2)||(myorder.status.status eq 1)}">
				     							<a href="myorders/submitdelete?id=${myorder.id}"><loc:message code="myorders.delete"/></a>
				     						</c:if>  
				     					</td>
								</tr>
								</c:forEach>
							  </tbody>
							</table>
							</div>
							</div>
							</div>
							</c:if>
							</c:otherwise>
							</c:choose>
				<!--#include file="footer__lk.shtml" -->
				<%@include file='footer.jsp'%>
				</div>
				</div>
			</div>

		</div>

	</div>

</div>
</div>

</body>
</html>
 




	
