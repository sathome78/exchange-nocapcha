<%@page language="java"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>  
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
	<title><loc:message code="orders.title"/></title>
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

					<div class="title__page"><loc:message code="submitorder.text"/>:</div>

	
						<div class="tab-content">
							<div class="tab-pane active">
								<!-- Start  withdraw__money -->
								<div class="form-horizontal withdraw__money">
									<div class="form-group">
										<label class="col-sm-3 control-label" for="#"><loc:message code="orders.currencyforsale" /></label>
										<div class="col-sm-7">
											<span class="form-control"><fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountSell}" />
											${currList.get(order.currencySell-1).getName()}</span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-3 control-label" for="#"><loc:message code="submitorder.buy"/></label>
										<div class="col-sm-7">
											<span class="form-control"><fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountBuy}"/> 
											${currList.get(order.currencyBuy-1).getName()}</span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-3 control-label" for="#"><loc:message code="submitorder.commission"/></label>
										<div class="col-sm-7">
											<c:set var="commissionValue" value="${order.amountBuy*commission/100}"/>
											<span class="form-control"><fmt:formatNumber type="number" maxFractionDigits="9" value="${commissionValue}"/>
											${currList.get(order.currencyBuy-1).getName()}</span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-3 control-label" for="#"><loc:message code="submitorder.sumwithcommission"/></label>
										<div class="col-sm-7">
											<span class="form-control"><fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountBuy-commissionValue}"/>
											 ${currList.get(order.currencyBuy-1).getName()}</span>
										</div>
									</div>
									<br>
									<div class="form-group">
										<div class="col-sm-offset-3 col-sm-6">
											<form:form action="create" modelAttribute="order" method="post">
									 			<form:hidden path="amountSell" value= "${order.amountSell}" />
									 			<form:hidden path="amountBuy" value= "${order.amountBuy}" />
									 			<form:hidden path="currencySell" value= "${order.currencySell}" />
									 			<form:hidden path="currencyBuy" value= "${order.currencyBuy}" />
												<loc:message code="submitorder.submit" var="labelSubmit"/>
											 	<input type="submit" value="${labelSubmit}" class="btn btn-primary"/>
									     	</form:form>

									     	<form:form action="edit" modelAttribute="order" method="post">
												 <form:hidden path="amountSell" value= "${order.amountSell}" />
									 			 <form:hidden path="amountBuy" value= "${order.amountBuy}" />
									 			 <form:hidden path="currencySell" value= "${order.currencySell}" />
									 			 <form:hidden path="currencyBuy" value= "${order.currencyBuy}" />
												 <loc:message code="submitorder.edit" var="labelEdit"/>
									     		 <input type="submit" value="${labelEdit}" class="btn btn-primary" />
									     	</form:form>
     	
									     	<c:url value="/orders" var="url"/>
											<form:form action="${url}">
												<loc:message code="submitorder.cancell" var="labelCancell"></loc:message>
									     		 <input type="submit" value="${labelCancell}" class="btn btn-primary"/>
											</form:form>
										</div>
									</div>
								</div>
								<!-- End  withdraw__money -->
							</div>
						
				</div>
					
				<!--#include file="footer__lk.shtml" -->
				<%@include file='footer.jsp'%>
				
			</div>

		</div>

	</div>

</div>

</body>
</html>
 
