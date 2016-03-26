<%@page language="java"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>  
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<!--[if lt IE 9]><script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
	<title><loc:message code="orders.ordersell" /></title>
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
					<c:set var="SELL" value="<%=me.exrates.model.enums.OperationType.SELL%>"/>
					<c:set var="BUY" value="<%=me.exrates.model.enums.OperationType.BUY%>"/>
					<c:if test="${order.operationType  eq SELL}" >
						<div class="title__page"><loc:message code="orders.ordersell"/></div>
					</c:if>
					<c:if test="${order.operationType  eq BUY}" >
						<div class="title__page"><loc:message code="orders.orderbuy"/></div>
					</c:if>

			
					<!-- begin sell__buy -->
					<div class="sell__buy">
						<div class="tab-content">
							<div class="tab-pane active">
								<!-- Start  withdraw__money -->
								<form:form class="form-horizontal withdraw__money" action="submit" method="post" modelAttribute="order">
								  <c:if test="${order.operationType  eq SELL}">
									<div class="form-group">
										<label class="col-sm-3 control-label" for="#"><loc:message code="orders.currencyforsale" /></label>
										<div class="col-sm-7">
											<form:select path="currencySell" class="select form-control">
   			 									<form:options items="${currList}" itemLabel="name" itemValue="id" />
											</form:select>
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-3 control-label" for="#"><loc:message code="orders.sum1"/></label>
										<div class="col-sm-7">
											<form:errors path="amountSell" style="color:red"/> 
											<span style="color:red">${notEnoughMoney}</span>
											<form:input path="amountSell" class="form-control" placeholder="0.0"/> 
										</div>
									</div>
								  </c:if>
									<div class="form-group">
										<label class="col-sm-3 control-label" for="#"><loc:message code="orders.currencyforbuy"/></label>
										<div class="col-sm-7">
											<form:select path="currencyBuy" class="select form-control">
   			 									<form:options items="${currList}" itemLabel="name" itemValue="id" />
											</form:select>
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-3 control-label" for="#"><loc:message code="orders.sum2"/></label>
										<div class="col-sm-7">
											<form:errors path="amountBuy" style="color:red" />
											<form:input path="amountBuy" class="form-control" placeholder="0.0"/>
										</div>
									</div>
								  <c:if test="${order.operationType  eq BUY}">
									<div class="form-group">
										<label class="col-sm-3 control-label" for="#"><loc:message code="orders.currencyforsale" /></label>
										<div class="col-sm-7">
											<form:select path="currencySell" class="select form-control">
   			 									<form:options items="${currList}" itemLabel="name" itemValue="id" />
											</form:select>
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-3 control-label" for="#"><loc:message code="orders.sum1"/></label>
										<div class="col-sm-7">
											<form:errors path="amountSell" style="color:red"/> 
											<span style="color:red">${notEnoughMoney}</span>
											<form:input path="amountSell" class="form-control" placeholder="0.0"/> 
										</div>
									</div>
								  </c:if>
									<div class="form-group">
										<label class="col-sm-3 control-label" for="#">
											<loc:message code="orders.yourcommission"/>: 
										</label>
										<div class="col-sm-7">
											<fmt:formatNumber type="number" maxFractionDigits="9" value="${commission}"/>%
										</div>
									</div>
									<br>
									<div class="form-group">
										<div class="col-sm-offset-3 col-sm-6">
											<form:hidden path="operationType" value="${order.operationType}" />
											<loc:message code="orders.submit" var="labelSubmit"></loc:message>
     									    <input type="submit" value="${labelSubmit}" class="btn btn-primary"/>
										</div>
									</div>
								</form:form>
								<!-- End  withdraw__money -->
							</div>
						</div>
					</div>
					<!-- end sell__buy -->


				</div>
					
				<!--#include file="footer__lk.shtml" -->
				<%@include file='footer.jsp'%>
				
			</div>

		</div>

	</div>


</body>
</html>
 
