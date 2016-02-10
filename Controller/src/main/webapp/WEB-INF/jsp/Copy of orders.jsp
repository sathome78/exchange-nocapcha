
 
<table>
	<tr>
		<td colspan=2><%@include file='header.jsp'%></td>
	</tr>
	<tr>
	<td><%@include file='usermenu.jsp'%></td>
	<td nowrap style="padding-left:30px">
		<h4><a href="order/sell/new"><loc:message code="orders.createordersell"/></a>
		<br>
		<a href="newordertobuy"><loc:message code="orders.createorderbuy"/></a></h4>		 
		<br><br><br>
		<loc:message code="orders.listtosell"/><br>
		<c:if test="${msq ne ''}">
		${msg}
		</c:if>
		<p>
				<table border=1>
					<tr>
						<td><loc:message code="orders.currsell"/></td>
						<td><loc:message code="orders.amountsell"/></td>
						<td><loc:message code="orders.currbuy"/></td>
						<td><loc:message code="orders.amountbuy"/></td>
						<td><loc:message code="orders.commission"/></td>
						<td><loc:message code="orders.amountwithcommission"/></td>
						<td><loc:message code="orders.datecreation"/></td>
						<td></td>
					</tr>
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
						<td>
							${order.commission*order.amountSell/100}
						</td>
						<td>
							<fmt:formatNumber type="number" maxFractionDigits="9" value="${order.amountSellWithCommission}"/>
						</td>
						<td>
							${order.dateCreation}
						</td>
	   					<td><a href="orders/sell/accept?id=${order.id}"><loc:message code="orders.accept"/></a></td>  
					</tr>
				</c:forEach>
				</table>
				</p>
		<loc:message code="orders.listtobuy"/>
	</td>
	<tr>
		<td colspan=2 align=center><%@include file='footer.jsp'%></td>
	</tr>
</table>
</body>  
</html>  
