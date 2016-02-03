<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%> 
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
<head>
  <title></title>
 </head>
<body>
     <sec:authorize access="isAuthenticated()">
	    <div style="margin-top: 10px;">
		    <p><a href="<c:url value='/mywallets'/>"><loc:message code="usermenu.accounts" /></a>
		    <p><a href="<c:url value='/orders'/>"><loc:message code="usermenu.orders" /></a>
		    <p><a href=""><loc:message code="usermenu.enter" /></a>
		    <p><a href="<c:url value='/myorders'/>"><loc:message code="usermenu.history" /></a>
		    <p><a href="/"><loc:message code="usermenu.settings" /></a>
	    </div>
	</sec:authorize>
</body>
</html>