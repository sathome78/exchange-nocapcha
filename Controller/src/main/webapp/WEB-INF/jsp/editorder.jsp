<%@page language="java"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%> 
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>   

<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<!--[if lt IE 9]><script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
	<title><loc:message code="editorder.text"/></title>
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

					<div class="title__page"><loc:message code="editorder.text"/></div>

	
						<div class="tab-content">
							<div class="tab-pane active" id="tab__sell">
								<!-- Start  withdraw__money -->
								<form:form class="form-horizontal withdraw__money" action="submit" method="post" modelAttribute="order">
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
											<form:input path="amountSell" class="form-control"/> 
											<form:errors path="amountSell" /> 
											<span style="color:red">${notEnoughMoney}</span>
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-3 control-label" for="#"><loc:message code="orders.currencyforbuy"/> </label>
										<div class="col-sm-7">
											<form:select path="currencyBuy" class="select form-control">
   			 									<form:options items="${currList}" itemLabel="name" itemValue="id" />
											</form:select>
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-3 control-label" for="#"><loc:message code="orders.sum2"/></label>
										<div class="col-sm-7">
											<form:input path="amountBuy" class="form-control"/>
	 		 								<form:errors path="amountBuy" />
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-3 control-label" for="#"><loc:message code="orders.yourcommission"/></label>
										<div class="col-sm-7">
											${commission}%
										</div>
									</div>
									<br>
									<div class="form-group">
										<div class="col-sm-offset-3 col-sm-6">
											<loc:message code="submitorder.change" var="labelSubmit"></loc:message>
     										 <input type="submit" value="${labelSubmit}" class="btn btn-primary"/>
										</div>
									</div>
								</form:form>
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
 
