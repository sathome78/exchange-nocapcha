<%@page language="java"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>  
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>  
<head> 
<title><loc:message code="submitorder.title" /></title>
<style>  
body {  
 font-size: 20px;  
 color: teal;  
 font-family: Calibri;  
}  
  
td {  
 font-size: 15px;  
 color: black;  
 width: 100px;  
 height: 22px;  
 text-align: left;  
}  
  
.heading {  
 font-size: 18px;  
 color: white;  
 font: bold;  
 background-color: orange;  
 border: thick;  
}  
</style>  
</head>  
<body>  
<table width=100%>
	<tr>
		<td colspan=2><%@include file='header.jsp'%></td>
	</tr>
	<tr>
	<td><%@include file='usermenu.jsp'%></td>
	<td>
		<h2><loc:message code="submitorder.text"/>:</h2>
		
		<loc:message code="orders.currencyforsale" />: 
		<fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountSell}"/>
		${currList.get(order.currencySell-1).getName()}
		<br>
		
		<loc:message code="submitorder.buy"/>: ${order.amountBuy} ${currList.get(order.currencyBuy-1).getName()}<br>
		
		<c:set var="commissionValue" value="${order.amountBuy*commission/100}"/>
		<loc:message code="submitorder.commission"/>: ${commissionValue} ${currList.get(order.currencyBuy-1).getName()}<br>
	
		<loc:message code="submitorder.sumwithcommission"/>: ${order.amountBuy-commissionValue} ${currList.get(order.currencyBuy-1).getName()}<br>
		
		<form:form action="create" modelAttribute="order" method="post">
 			<form:hidden path="amountSell" value= "${order.amountSell}" />
 			<form:hidden path="amountBuy" value= "${order.amountBuy}" />
 			<form:hidden path="currencySell" value= "${order.currencySell}" />
 			<form:hidden path="currencyBuy" value= "${order.currencyBuy}" />
			<loc:message code="submitorder.submit" var="labelSubmit"/>
		 	<input type="submit" value="${labelSubmit}" /><br>
     	</form:form>

     	<form:form action="edit" modelAttribute="order" method="post">
			 <form:hidden path="amountSell" value= "${order.amountSell}" />
 			 <form:hidden path="amountBuy" value= "${order.amountBuy}" />
 			 <form:hidden path="currencySell" value= "${order.currencySell}" />
 			 <form:hidden path="currencyBuy" value= "${order.currencyBuy}" />
			 <loc:message code="submitorder.edit" var="labelEdit"/>
     		 <input type="submit" value="${labelEdit}" />
     	</form:form>
     	
     	<c:url value="/orders" var="url"/>
		<form:form action="${url}">
			<loc:message code="submitorder.cancell" var="labelCancell"></loc:message>
     		 <input type="submit" value="${labelCancell}" />
		</form:form>
     </td>
     </tr>
 </table>
</body>
</html>

