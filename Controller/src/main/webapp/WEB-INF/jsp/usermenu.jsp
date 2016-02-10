<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%> 
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<aside class="sidebar">

	<!-- begin Logo block -->
	<div class="header__logo">
		<a href="<c:url value='/'/>"><img src="<c:url value='/client/img/logo.png' />" alt="Home"/></a>
	</div>
	<!-- end Logo block -->
<sec:authorize access="isAuthenticated()">
<div class="sidebar__wrapper">
	<!-- begin navbar -->
	<ul class="navbar">
		<li class="navabr__item">
			<a href="<c:url value='/mywallets'/>" class="navabr__link"><loc:message code="usermenu.accounts" /></a>
		</li>
		<li class="navabr__item">
			<a href="<c:url value='/orders'/>" class="navabr__link"><loc:message code="usermenu.orders" /></a>
		</li>
		<li class="navabr__item">
			<a href="merchants" class="navabr__link"><loc:message code="usermenu.enter" /></a>
		</li>
		<li class="navabr__item">
			<a href="<c:url value='/myorders'/>" class="navabr__link"><loc:message code="usermenu.history" /></a>
		</li>
		<li class="navabr__item">
			<a href="/" class="navabr__link"><loc:message code="usermenu.settings" /></a>
		</li>
	</ul>
	<!-- end navbar -->
	

	<!-- begin sub__navbar  
	<ul class="sub__navbar ">
		<li class="navabr__item">
			<a href="<c:url value='/order/sell/new'/>" class="navabr__link"><loc:message code="orders.createordersell"/></a>
		</li>
	</ul>
	 end sub__navbar  -->
</div>
</sec:authorize>
</aside>
 
