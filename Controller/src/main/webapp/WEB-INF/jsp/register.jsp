<%@page language="java"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="registrationform"%>  
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>  
<head> 
<title><loc:message code="register.title" /></title>
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
<%@include file='header.jsp'%><br>
 <b><loc:message code="register.form" /></b>   
   
  <div>  
   <registrationform:form method="post" action="create" modelAttribute="user">  
    <table>  
     <tr>  
      <td><loc:message code="register.nickname" /></td>  
      <td><registrationform:input path="nickname" /></td> 
      <td><registrationform:errors path="nickname" /></td>  
     </tr>  
     <tr>  
      <td><loc:message code="register.email" /></td>  
      <td><registrationform:input path="email" /></td>  
      <td><registrationform:errors path="email" /></td>  
     </tr>  
     <tr>  
      <td><loc:message code="register.password" /></td>  
      <td><registrationform:input path="password" /></td>  
      <td><registrationform:errors path="password" /></td>  
     </tr> 
    
     <tr>  
      <td><loc:message code="register.repeatpassword" /></td>  
      <td><registrationform:input path="confirmPassword" /></td>  
      <td><registrationform:errors path="confirmPassword" /></td>  
     </tr> 
     
     <tr>  
      <td><loc:message code="register.readrules" /></td>  
      <td><registrationform:checkbox path="readRules" value="ReadRulesOk" /></td>  
      <td><registrationform:errors path="readRules" /></td>  
     </tr> 
   
     <tr>  
      <td> </td>  
      <td>
     	 <loc:message code="register.submit" var="labelSubmit"></loc:message>
     	 <input type="submit" value="${labelSubmit}" />
      </td>  
      <td></td>
     </tr>  
   </table>  
   </registrationform:form>  
  </div>  

</body>  
</html>  


