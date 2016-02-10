<%@page language="java"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%> 
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

					<div class="title__page"><loc:message code="myorders.title"/></div>
					<loc:message code="myorders.text"/><br><br><br>
					<!-- begin orders__sell__buy -->
					<div class="orders__sell__buy">
						<div class="row">
							<div class="col-sm-6">
								<div class="orders__sell__buy__title" style="text-align:left">
									<loc:message code="myorders.sellorders"/>
								</div>
								<div style="color:blue">
									<c:if test="${msg eq 'delete'}" >
										<br><loc:message code="myorders.deletesuccess"/><br>
									</c:if>
									<c:if test="${msg eq 'deletefailed'}" >
										<br><loc:message code="myorders.deletefailed"/><br>
									</c:if>
									<c:if test="${msg eq 'edit'}" >
										<br><loc:message code="myorders.editsuccess"/><br>
									</c:if>
									<c:if test="${msg eq 'editfailed'}" >
										<br><loc:message code="myorders.editfailed"/><br>
									</c:if>
								</div>

							<table class="table">
								<tbody>
									<thead>
									  <tr>
										<th class="col-xs-4"><loc:message code="myorders.currsell"/></td>
										<th class="col-xs-4"><loc:message code="myorders.amountsell"/></td>
										<th class="col-xs-4"><loc:message code="myorders.currbuy"/></td>
										<th class="col-xs-4"><loc:message code="myorders.amountbuy"/></td>
										<th class="col-xs-4"><loc:message code="myorders.commission"/></td>
										<th class="col-xs-4"><loc:message code="myorders.amountwithcommission"/></td>
										<th class="col-xs-4"><loc:message code="myorders.datecreation"/></td>
										<th class="col-xs-4"><loc:message code="myorders.datefinal"/></td>
										<th class="col-xs-4"><loc:message code="myorders.status"/></td>
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
											${myorder.commission}
										</td>
										<td>
											<fmt:formatNumber type="number" maxFractionDigits="9" value="${myorder.amountBuyWithCommission}"/>
										</td>
										<td>
											${myorder.dateCreation}
										</td>
										<td>
											${myorder.dateFinal}
										</td>
										<td>
											${myorder.statusString}
										</td>
				     					<td>
				     						<c:if test="${(myorder.status.status eq 2)||(myorder.status.status eq 1)}">
				     							<a href="myorders/delete?id=${myorder.id}"><loc:message code="myorders.delete"/></a>
				     						</c:if>  
				     					</td>
								</tr>
								</c:forEach>
							  </tbody>
							</table>
							</div>
				
				</div>
					
				<!--#include file="footer__lk.shtml" -->
				<%@include file='footer.jsp'%>
				
			</div>

		</div>

	</div>

</div>
</div>

</body>
</html>
 




	
