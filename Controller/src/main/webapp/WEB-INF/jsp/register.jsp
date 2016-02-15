<%@page language="java" contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="registrationform"%>  
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%--<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">--%>
<%--<html>  --%>
<%--<head> --%>
<%--<title><loc:message code="register.title" /></title>--%>
<%--<style>  --%>
<%--body {  --%>
 <%--font-size: 20px;  --%>
 <%--color: teal;  --%>
 <%--font-family: Calibri;  --%>
<%--}  --%>
  <%----%>
<%--td {  --%>
 <%--font-size: 15px;  --%>
 <%--color: black;  --%>
 <%--width: 100px;  --%>
 <%--height: 22px;  --%>
 <%--text-align: left;  --%>
<%--}  --%>
  <%----%>
<%--.heading {  --%>
 <%--font-size: 18px;  --%>
 <%--color: white;  --%>
 <%--font: bold;  --%>
 <%--background-color: orange;  --%>
 <%--border: thick;  --%>
<%--}  --%>
<%--</style>  --%>
<%--</head>  --%>
<%--<body>  --%>
 <%--<b><loc:message code="register.form" /></b>--%>
   <%----%>
  <%--<div>  --%>
   <%--<registrationform:form method="post" action="create" modelAttribute="user">  --%>
    <%--<table>  --%>
     <%--<tr>  --%>
      <%--<td><loc:message code="register.nickname" /></td>  --%>
      <%--<td><registrationform:input path="nickname" /></td> --%>
      <%--<td><registrationform:errors path="nickname" /></td>  --%>
     <%--</tr>  --%>
     <%--<tr>  --%>
      <%--<td><loc:message code="register.email" /></td>  --%>
      <%--<td><registrationform:input path="email" /></td>  --%>
      <%--<td><registrationform:errors path="email" /></td>  --%>
     <%--</tr>  --%>
     <%--<tr>  --%>
      <%--<td><loc:message code="register.password" /></td>  --%>
      <%--<td><registrationform:input path="password" type="password"/></td>  --%>
      <%--<td><registrationform:errors path="password" type="password"/></td>  --%>
     <%--</tr> --%>
    <%----%>
     <%--<tr>  --%>
      <%--<td><loc:message code="register.repeatpassword" /></td>  --%>
      <%--<td><registrationform:input path="confirmPassword" type="password" /></td>--%>
      <%--<td><registrationform:errors path="confirmPassword" /></td>  --%>
     <%--</tr> --%>
     <%----%>
     <%--<tr>  --%>
      <%--<td><loc:message code="register.readrules" /></td>  --%>
      <%--<td><registrationform:checkbox path="readRules" value="ReadRulesOk" /></td>  --%>
      <%--<td><registrationform:errors path="readRules" /></td>  --%>
     <%--</tr> --%>
   <%----%>
     <%--<tr>  --%>
      <%--<td> </td>  --%>
      <%--<td>--%>
     	 <%--<loc:message code="register.submit" var="labelSubmit"></loc:message>--%>
     	 <%--<input type="submit" value="${labelSubmit}" />--%>
      <%--</td>  --%>
      <%--<td></td>--%>
     <%--</tr>  --%>
   <%--</table>  --%>
   <%--</registrationform:form>  --%>
  <%--</div>  --%>

<%--</body>  --%>
<%--</html>--%>



<!DOCTYPE html>
<html lang="en">
<head>
 <meta charset="utf-8" />
 <!--[if lt IE 9]><script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
 <title></title>
 <meta name="keywords" content="" />
 <meta name="description" content="" />
 <meta name="viewport" content="width=device-width, initial-scale=1.0" />

 <link href="<c:url value="/client/css/bootstrap.css"/>" rel="stylesheet" type="text/css" />
 <link href="<c:url value="/client/css/chosen.css"/>" rel="stylesheet" type="text/css" />
 <link href="<c:url value="/client/css/style.css"/>" rel="stylesheet" type="text/css" />

 <script type="text/javascript" src="<c:url value="/client/js/jquery.js"/>"></script>
 <script type="text/javascript" src="<c:url value="/client/js/dropdown.js"/>"></script>
 <script type="text/javascript" src="<c:url value="/client/js/modal.js"/>"></script>
 <script type="text/javascript" src="<c:url value="/client/js/chosen.jquery.min.js"/>"></script>
 <script type="text/javascript" src="<c:url value="/client/js/function.js"/>"></script>

</head>

<body>

<div class="wrapper login_page">


 <div class="container container_center full__height">

  <div class="main__content full__height">

   <div class="content__page full__height">

    <!-- begin registration__form -->

    <div class="registration__form form">
<registrationform:form method="post" action="create" modelAttribute="user">
     <legend>Регистрация</legend>
     <div class="row">

      <registrationform:input path="nickname"  class="form-control" placeholder="<loc:message code=\"register.nickname\" />" required="required" />
      <registrationform:errors path="nickname" />
     <%--<input type="text" class="form-control" placeholder="Имя*" required>--%>
     </div>
     <div class="row">
      <label>Используется как логин для входа, изменению не подлежит.</label>
      <registrationform:input path="email" class="form-control" required="required"/>
      <registrationform:errors path="email" />
     </div>
     <div class="row">
      <registrationform:input path="password" type="password" class="form-control" required="required"/>
      <registrationform:errors path="password" />
     </div>
     <div class="row">
      <registrationform:input path="confirmPassword" type="password" class="form-control"  required="required" />
      <registrationform:errors path="confirmPassword" />
     </div>
     <div class="row offerta">
      <registrationform:checkbox path="readRules" id="1" value="ReadRulesOk" required="required"  />
      <registrationform:errors path="readRules" />
      <label for="1"><a href="#">С правилами</a> ознакомлен.</label>
     </div>
     <div class="row">
      <button type="button" class="btn btn-primary">Зарегистрироваться</button>
     </div>
    </div>
    <!-- end registration__form -->
 </registrationform:form>
   </div>

  </div>

 </div>

</div>


</body>
</html>
