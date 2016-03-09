<%@page language="java"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
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

				<div class="title__page"><loc:message code="orders.title"/></div>

				<!-- begin chart__section 
				<div class="chart__section">
					<div class="diagramm__box">
						<img src="<c:url value='/client/img/grafik.png'/>" alt=""/>
					</div>
				</div>
				<!-- end chart__section -->
				<div>
					<c:if test="${msq ne ''}">
						<span style="color:red">${msg}</span><br><br>
					</c:if>
				</div>
	
				<!-- begin sell__buy -->
				<div class="sell__buy">
					<ul class="nav nav-tabs col-sm-offset-3 col-sm-7">
						<li class="active col-sm-6 col-xs-6">
							<a href="#tab__sell" data-toggle="tab">
								<loc:message code="orders.createordersell"/>
							</a>
						</li>
						<li class="col-sm-6 col-xs-6">
							<a href="#tab__buy" data-toggle="tab">
								<loc:message code="orders.createorderbuy"/>
							</a>
						</li>
					</ul>

					<div class="tab-content">
						<div class="tab-pane active" id="tab__sell">
							<!-- Start  withdraw__money -->
							<c:set var="submiturl" value="order/submit"/>
							<form:form class="form-horizontal withdraw__money" action="${submiturl}" method="post" modelAttribute="order">
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
										<c:set var="SELL" value="<%=me.exrates.model.enums.OperationType.SELL%>"/>
										<form:hidden path="operationType" value= "${SELL}" />
										<loc:message code="orders.submit" var="labelSubmit"></loc:message>
										<input type="submit" value="${labelSubmit}" class="btn btn-primary"/>
									</div>
								</div>
							</form:form>
							<!-- End  withdraw__money -->
						</div>

						<div class="tab-pane" id="tab__buy">
							<!-- Start  withdraw__money -->
							<form:form class="form-horizontal withdraw__money" action="${submiturl}" method="post" modelAttribute="order">
								<div class="form-group">
									<label class="col-sm-3 control-label" for="#"><loc:message code="orders.currencyforbuy" /></label>
									<div class="col-sm-7">
										<form:select path="currencyBuy" class="select form-control">
											<form:options items="${currList}" itemLabel="name" itemValue="id" />
										</form:select>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3 control-label" for="#"><loc:message code="orders.sum1"/></label>
									<div class="col-sm-7">
										<form:errors path="amountBuy" style="color:red"/>
										<span style="color:red">${notEnoughMoney}</span>
										<form:input path="amountBuy" class="form-control" placeholder="0.0"/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3 control-label" for="#"><loc:message code="orders.currencyforsale"/></label>
									<div class="col-sm-7">
										<form:select path="currencySell" class="select form-control">
											<form:options items="${currList}" itemLabel="name" itemValue="id" />
										</form:select>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3 control-label" for="#"><loc:message code="orders.sum2"/></label>
									<div class="col-sm-7">
										<form:errors path="amountSell" style="color:red" />
										<form:input path="amountSell" class="form-control" placeholder="0.0"/>
									</div>
								</div>
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
										<c:set var="BUY" value="<%=me.exrates.model.enums.OperationType.BUY%>"/>
										<form:hidden path="operationType" value="${BUY}" />
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

				<!-- begin orders__sell__buy -->
				<div class="orders__sell__buy">
					<div class="row">
						<div class="col-sm-6">
							<div class="orders__sell__buy__title">
								<loc:message code="orders.listtosell"/>
							</div>
	
							<table class="table">
								<thead>
								<tr>
									<th class="col-xs-4"><loc:message code="orders.currsell"/></th>
									<th class="col-xs-4"><loc:message code="orders.amountsell"/></th>
									<th class="col-xs-4"><loc:message code="orders.currbuy"/></th>
									<th class="col-xs-4"><loc:message code="orders.amountbuy"/></th>
									<td></td>
								</tr>
								</thead>
								<tbody>
								<c:forEach var="order" items="${orderMap.sell}">
								<tr>
									<td>
											${order.currencySellString}
									</td>
									<td>
										<fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountSell}"/>
									</td>
									<td>
											${order.currencyBuyString}
									</td>
									<td>
										<fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountBuy}"/>
									</td>
									<td><a href="orders/submitaccept?id=${order.id}"><loc:message code="orders.accept"/></a></td>
								</tr>
								</c:forEach>
								<tbody>
							</table>
						</div>

						<div class="col-sm-6">
							<div class="orders__sell__buy__title">
								<loc:message code="orders.listtobuy"/><br>
							</div>
							<table class="table">
								<thead>
								<tr>
									<th class="col-xs-4"><loc:message code="orders.currbuy"/></th>
									<th class="col-xs-4"><loc:message code="orders.amountbuy"/></th>
									<th class="col-xs-4"><loc:message code="orders.currsell"/></th>
									<th class="col-xs-4"><loc:message code="orders.amountsell"/></th>
									<td></td>
								</tr>
								</thead>
								<tbody>
								<c:forEach var="order" items="${orderMap.buy}">
								<tr>
									<td>
											${order.currencyBuyString}
									</td>
									<td>
										<fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountBuy}"/>
									</td>
									<td>
											${order.currencySellString}
									</td>
									<td>
										<fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountSell}"/>
									</td>
									<td><a href="orders/submitaccept?id=${order.id}"><loc:message code="orders.accept"/></a></td>
								</tr>
								</c:forEach>
								<tbody>
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

