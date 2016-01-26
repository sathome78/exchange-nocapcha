<%@page language="java"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%> 
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>   
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>  
<head> 
<title><loc:message code="editorder.title" /></title>
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
<h2><loc:message code="editorder.text"/>:</h2>
	<form:form action="submitordertosell" method="post" modelAttribute="order">
			<loc:message code="orders.currencyforsale" />
			<form:select path="currencySell">
   			 	<form:options items="${currList}" itemLabel="name" itemValue="id" />
			</form:select>
			<p style="color:red">${notEnoughMoney}</p>
			<loc:message code="orders.sum1"/> <form:input path="amountSell"/> <form:errors path="amountSell" />
			 <br>
			<loc:message code="orders.currencyforbuy"/> 
			<form:select path="currencyBuy">
   			 	<form:options items="${currList}" itemLabel="name" itemValue="id" />
			</form:select>
			<br>
			<loc:message code="orders.sum2"/> <form:input path="amountBuy"/>
	 		  <form:errors path="amountBuy" />
			<br>
			<loc:message code="orders.yourcommission"/>: ${commission}%<br>
			<loc:message code="submitorder.change" var="labelSubmit"></loc:message>
     		 <input type="submit" value="${labelSubmit}" />
		</form:form>
	
</body>
</html>

