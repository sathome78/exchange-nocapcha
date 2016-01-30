<%@page language="java"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="paymentForm"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Title</title>
</head>
<body>
<%@include file='header.jsp'%><br>
<c:url value="/merchants/yandexmoney/payment/process" var="submitUrl" />
<paymentForm:form method="post" action="${submitUrl}" modelAttribute="payment">
    <loc:message code="merchants.amountToBeCredited"/> : <input type="text" name="amount" value="${paymentPrepareData.amount}" readonly="true"/>
    <loc:message code="mechants.commission"/> : <input type="text" name="commission" value="${paymentPrepareData.commission}" readonly="true"/>
    <loc:message code="merchants.sumToPay"/> : <input type="text" value="${paymentPrepareData.sumToPay}">
    <input type="submit" value="<loc:message code='mechants.submitPayment'/>">
    <a href="<c:url value='/merchants'/>"><loc:message code="mechants.cancelPayment"/></a>
</paymentForm:form>
</body>
</html>
