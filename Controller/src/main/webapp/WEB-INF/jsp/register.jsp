<%@page language="java" contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="registrationform"%>  
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
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
 <%--<link href="<c:url value="/client/css/style.css"/>" rel="stylesheet" type="text/css" />--%>

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
                        <legend><loc:message code="register.title"/></legend>
                        <div class="row">
                            <loc:message code="register.nickname" var="login"/>
                            <registrationform:input path="nickname"  class="form-control" placeholder="${login}" required="required" />
                            <registrationform:errors path="nickname" class="form-control"  />
                        </div>
                        <div class="row">
                            <label for="email"><loc:message code="register.loginLabel"/></label>
                            <loc:message code="register.email" var="email"/>
                            <registrationform:input id="email" path="email" class="form-control" placeholder="${email}" required="required"/>
                            <registrationform:errors path="email" class="form-control" />
                        </div>
                        <div class="row">
                            <loc:message code="register.password" var="password"/>
                            <registrationform:input path="password" type="password" placeholder="${password}" class="form-control" required="required"/>
                            <registrationform:errors path="password" class="form-control" />
                        </div>
                        <div class="row">
                            <loc:message code="register.repeatpassword" var="repassword"/>
                            <registrationform:input path="confirmPassword" type="password" placeholder="${repassword}" class="form-control"  required="required" />
                            <registrationform:errors path="confirmPassword" class="form-control" />
                        </div>
                        <div class="row offerta">
                            <registrationform:checkbox path="readRules" id="1" value="ReadRulesOk" required="required"  />
                            <registrationform:errors path="readRules" class="form-control" />
                            <label for="1"><a href="#"><loc:message code="register.readrules"/></a></label>
                        </div>
                        <div class="row">
                            <button type="submit" class="btn btn-primary"><loc:message code="register.submit"/></button>
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