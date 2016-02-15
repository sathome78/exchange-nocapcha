<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Неверный логин или пароль</title>
</head>
<body>
<b><font color="red">Login failed</font></b>
<br/><a href="<c:url value="/login"/>">Назад</a>
</body>
</html>