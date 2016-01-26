<%@page language="java"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="registrationform"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Title</title>
</head>
<body>
<%@include file='header.jsp'%><br>
<form action="<c:url value='/merchants/yandexmoney/payment/prepare'/>" method="post">
    <select name="currency">
        <c:forEach var="wallet" items="${userWallets}">
            <option value="${wallet.id}">${wallet.name}</option>
        </c:forEach>
    </select>
    <select name="meansOfPayment">
        <option value="ymoney">Yandex Money</option>
    </select>
    <input type="text" name="sum" required>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <input type="submit">
</form>
</body>
</html>
