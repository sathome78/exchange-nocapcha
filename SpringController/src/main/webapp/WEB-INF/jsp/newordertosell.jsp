<%@page language="java"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>  
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>  
<head> 
<title><loc:message code="orders.ordersell" /></title>
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
<h2><loc:message code="orders.ordersell"/>:</h2>
<form:form action="newordertosell" method="post" modelAttribute="order">
			<loc:message code="orders.currencyforsale" /><br>
			<form:select path="curr1" id="curr1">
			  <c:forEach items="${currList}" var="currency">
			       <option value="${currency.getId()}">${currency.getName()}</option>
			  </c:forEach>
			</form:select><br>
			<loc:message code="orders.sum1"/> <input type="text" name="sum1"><br>
			<loc:message code="orders.currencyforbuy"/><br>
			<select name="curr2" id="curr2">
				<c:forEach items="${currList}" var="currency">
			       <option value="${currency.getId()}">${currency.getName()}</option>
			    </c:forEach>
			</select><br>
			<loc:message code="orders.kurs"/> <form:input path="kurs"/><br>
			<loc:message code="orders.yourcommission"/>: ${commission}%<br>
			<loc:message code="orders.submit" var="labelSubmit"></loc:message>
     		 <input type="submit" value="${labelSubmit}" />
		</form:form>
	
</body>
</html>

