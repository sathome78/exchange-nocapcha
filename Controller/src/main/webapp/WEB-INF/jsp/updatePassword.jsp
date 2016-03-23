<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="loc" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
           uri="http://www.springframework.org/security/tags"%>
<%@ page session="false"%>
<html>
<head>
    <link href="<c:url value='/client/css/bootstrap.css'/>" rel="stylesheet" type="text/css"/>
    <link href="<c:url value='/client/css/style.css'/>" rel="stylesheet" type="text/css"/>

    <script type="text/javascript" src="<c:url value='/client/js/jquery.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/dropdown.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/modal.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/tab.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/chosen.jquery.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/test.js'/>"></script>

    <meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
    <title><loc:message code="dashboard.updatePasswordTitle"></loc:message></title>
</head>
<body>
<sec:authorize access="hasAnyAuthority('ROLE_CHANGE_PASSWORD')">
<div class="container container_center full__height">
    <div class="content__page">
        <h1>
            <loc:message code="dashboard.updatePasswordTitle"></loc:message>
        </h1>
        <div class="form-inline" >
                <div class="form-group">

                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    <label id="pass" ><loc:message code="admin.password" var="adminPassword"/></label>
                    <input type="password" name="pass" class="form-control" id="user-password"
                                placeholder="${adminPassword}" />

                    <label id="passConfirm"><loc:message code="admin.confirmpassword" var="adminConfirmpassword"/></label>
                    <input type="password" name="passConfirm" class="form-control" id="user-confirmpassword"
                                   placeholder="${adminConfirmpassword}" />

                    <loc:message code="admin.save" var="saveSubmit"></loc:message>
                    <button class="btn btn-primary" value="${saveSubmit}" type="submit" onclick="savePass()" id="saveButton">
                        ${saveSubmit}
                    </button>
               </div>
        </div>
    </div>
</div>
</sec:authorize>
</body>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script type="text/javascript">
    function savePass(){
        var pass = $("#user-password").val();
        var valid = pass == $("#user-confirmpassword").val();
        if(!valid) {
            $("#error").show();
            return;
        }
        var re = /((?=.*\d)(?=.*[a-zA-Z]).{8,20})/;
        if (pass == '' || !re.test(pass))
        {
            alert('Please enter a valid password.');
            return false;
        }
        $.ajax("/dashboard/updatePassword", {
            headers: {
                "X-CSRF-Token": $("input[name='_csrf']").val()
            },
            type: "POST",
            data: {password: pass},
            response: "json"
        }).done(function (response) {
            window.location = "/dashboard";
        }).fail(function (error) {

            console.log(error);
        });
    }
</script>
</html>