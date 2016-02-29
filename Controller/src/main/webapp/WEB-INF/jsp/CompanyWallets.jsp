<%@page language="java"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
							<c:forEach var="wallet" items="${companyWalletList}">
								<div>
									<table class="table">
										<tr>
											<td colspan=4 style="text-align:left; font-weight:bold">${wallet.currency.name}</td>
										</tr>
										<tr>
											<td nowrap  width=200 style="text-align:left">
												<loc:message code="mywallets.balance"/>:
												<fmt:formatNumber type="number" maxFractionDigits="9" value="${wallet.balance}"/>
										</td>
										</tr>
										<tr>
											<td colspan=4 style="text-align:left">
												<loc:message code="mywallets.commissionbalance"/>:
												<fmt:formatNumber type="number" maxFractionDigits="9" value="${wallet.commissionBalance}"/>
											</td>
										</tr>
									</table>
								</div>
							</c:forEach>
						</div>
				<!--#include file="footer__lk.shtml" -->
					<%@include file='footer.jsp'%>
			</div>

		</div>

	</div>

</body>
</html>
 