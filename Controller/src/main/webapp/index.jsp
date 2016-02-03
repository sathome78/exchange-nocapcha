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
 <table>
	<tr>
		<td colspan=2><%@include file='WEB-INF/jsp/header.jsp'%></td>
	</tr>
	<tr>
		<td><%@include file='WEB-INF/jsp/usermenu.jsp'%></td>
		<td>
		<div style="margin-left: 20px;">
        <h1>Welcome!</h1>
        <p>
            It's main page!
        </p>
        
    </div>
		 </td>
	<tr>
		<td colspan=2 align=center><%@include file='WEB-INF/jsp/footer.jsp'%></td>
	</tr>
</table>

</body>
</html>