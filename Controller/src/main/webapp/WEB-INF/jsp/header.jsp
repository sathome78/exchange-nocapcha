<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
 
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
 
    <title></title>
 
    
</head>
 
<body>
 

    <div style="margin-top: 10px;">
    <sec:authorize access="!isAuthenticated()">
            <p><a href="<c:url value="/login" />" role="button">Login</a></p>
            <p><a href="<c:url value="/register" />" role="button">Registration</a></p>
        </sec:authorize>
        <sec:authorize access="isAuthenticated()">
            You are authorize as <sec:authentication property="principal.username" /><br/>
          	<c:url value="/logout" var="logoutUrl" />
			<form action="${logoutUrl}" method="post">
         		 <input type="submit" value="Logout" /> 
         		 <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			</form>  
         </sec:authorize>
    </div>

</body>
</html>