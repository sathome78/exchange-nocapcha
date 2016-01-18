<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
 
    <title>Main page</title>
 
   </head>
 
<body>
 
<div>
 <%@include file='WEB-INF/jsp/header.jsp'%><br>
    <div style="margin-top: 20px;">
        <h1>Welcome!</h1>
        <p>
            It's main page!
        </p>
        
    </div>
 
    <div>
        <%@include file='WEB-INF/jsp/footer.jsp'%>
    </div>
 
</div>
</body>
</html>