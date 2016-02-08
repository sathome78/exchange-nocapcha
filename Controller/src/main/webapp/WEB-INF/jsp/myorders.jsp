<%@page language="java"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>  
<head> 
<title><loc:message code="myorders.title"/></title>
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
<table>
	<tr>
		<td colspan=2><%@include file='header.jsp'%></td>
	</tr>
	<tr>
	<td><%@include file='usermenu.jsp'%></td>
	<td>
		<c:if test="${orderMap.sell.size() eq 0}"><loc:message code="myorders.noorders"/></c:if>
		<c:if test="${orderMap.sell.size() ne 0}">
			<h2><loc:message code="myorders.text"/></h2><br>
			<h3><loc:message code="myorders.sellorders"/>:</h3>
			<c:if test="${msg eq 'delete'}" >
				<br><loc:message code="myorders.deletesuccess"/>
			</c:if>
			<c:if test="${msg eq 'deletefailed'}" >
				<br><loc:message code="myorders.deletefailed"/>
			</c:if>
			<c:if test="${msg eq 'edit'}" >
				<br><loc:message code="myorders.editsuccess"/>
			</c:if>
			<c:if test="${msg eq 'editfailed'}" >
				<br><loc:message code="myorders.editfailed"/>
			</c:if>
			<p>
				<table border=1>
					<tr>
						<td><loc:message code="myorders.currsell"/></td>
						<td><loc:message code="myorders.amountsell"/></td>
						<td><loc:message code="myorders.currbuy"/></td>
						<td><loc:message code="myorders.amountbuy"/></td>
						<td><loc:message code="myorders.commission"/></td>
						<td><loc:message code="myorders.amountwithcommission"/></td>
						<td><loc:message code="myorders.datecreation"/></td>
						<td><loc:message code="myorders.datefinal"/></td>
						<td><loc:message code="myorders.status"/></td>
						<td></td>

					</tr>
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
				</table>
				</p>
			
</c:if>

	 </td>
	<tr>
		<td colspan=2 align=center><%@include file='footer.jsp'%></td>
	</tr>
</table>
</body>  
</html>  


