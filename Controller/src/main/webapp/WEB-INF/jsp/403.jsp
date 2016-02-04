<%@ page language="java" contentType="text/html; charset=UTF-8"
 pageEncoding="UTF-8"%>
 <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<title>Access Denied</title>
</head>
<body>
<h2>You do not have permission to access this page!
</h2>
 <c:url value="/logout" var="logoutUrl" />
 <form action="${logoutUrl}" method="post">
          <input type="submit" value="Sign in as different user" /> 
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
</form>   
</body>
</html>