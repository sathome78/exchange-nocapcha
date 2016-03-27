<%@page language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<head>
    <meta charset="utf-8"/>
    <!--[if lt IE 9]>
    <script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
    <title><loc:message code="admin.title"/></title>
    <meta name="keywords" content=""/>
    <meta name="description" content=""/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>

    <link href="<c:url value='/client/css/bootstrap.css'/>" rel="stylesheet" type="text/css"/>
    <link href="<c:url value='/client/css/style.css'/>" rel="stylesheet" type="text/css"/>

    <script type="text/javascript" src="/client/js/jquery.js"></script>
    <script type="text/javascript" src="/client/js/tab.js"></script>
</head>
<body>

<div class="wrapper lk">

    <div class="container container_center full__height">

        <%@include file='usermenu.jsp' %>

        <div class="main__content">

            <%@include file='header.jsp' %>

            <div class="content__page">
                <ul class="nav nav-tabs">
                    <li class="active"><a data-toggle="tab" href="#panel1"><loc:message
                            code="admin.changePassword"/></a></li>
                    <li><a data-toggle="tab" href="#panel2"><loc:message code="admin.changeFinPassword"/></a></li>
                </ul>
                <div class="tab-content">
                    <div id="panel1" class="tab-pane fade in active">
                        <form:form class="form-horizontal" id="settings-user-form"
                                   action="/settings/changePassword/submit"
                                   method="post" modelAttribute="user">
                            <div>
                                <fieldset class="field-user">
                                    <div class="form-group user-name-group">
                                        <label for="user-name" class="col-md-4 control-label"><loc:message
                                                code="admin.login"/></label>

                                        <div class="col-md-8">
                                            <form:input path="id" type="hidden" class="form-control" id="user-id"/>
                                            <form:input path="role" type="hidden" class="form-control" id="user-role"/>
                                            <form:input path="status" type="hidden" class="form-control"
                                                        id="user-status"/>
                                            <form:input path="finpassword" type="hidden" class="form-control"
                                                        id="user-finpassword"/>
                                            <form:input path="nickname" class="form-control" id="user-name"
                                                        readonly="true"/>
                                        </div>
                                    </div>
                                    <div class="form-group user-email-group">
                                        <label for="user-email" class="col-md-4 control-label"><loc:message
                                                code="admin.email"/></label>

                                        <div class="col-md-8">
                                            <form:errors path="email" style="color:red"/>
                                            <form:input path="email" class="form-control" id="user-email"
                                                        readonly="true"/>
                                        </div>
                                    </div>
                                    <div class="form-group user-password-group">
                                        <label for="user-password" path="password"
                                               class="col-md-4 control-label"><loc:message
                                                code="admin.password"/></label>

                                        <div class="col-md-8">
                                            <form:errors path="password" style="color:red"/>
                                            <form:password path="password" class="form-control" id="user-password"/>
                                        </div>
                                    </div>
                                    <div class="form-group user-confirmpassword-group">
                                        <label for="user-confirmpassword" path="confirmpassword"
                                               class="col-md-4 control-label"><loc:message
                                                code="admin.confirmpassword"/></label>

                                        <div class="col-md-8">
                                            <form:errors path="confirmPassword" style="color:red"/>
                                            <form:password path="confirmPassword" class="form-control"
                                                           id="user-confirmpassword"/>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <div class="text-center">
                                            <loc:message code="admin.save" var="saveSubmit"></loc:message>
                                            <input class="btn btn-primary" value="${saveSubmit}" type="submit">

                                            <loc:message code="admin.cancel" var="cancelSubmit"></loc:message>
                                            <input onclick="javascript:window.location='/settings';"
                                                   class="btn btn-default" value="${cancelSubmit}" type="reset">
                                        </div>
                                    </div>
                                </fieldset>
                            </div>
                        </form:form>
                    </div>
                    <div id="panel2" class="tab-pane fade">
                        <form:form class="form-horizontal" id="settings-userFin-form"
                                   action="/settings/changeFinPassword/submit"
                                   method="post" modelAttribute="user">
                            <div>
                                <fieldset class="field-userFin">
                                    <div class="form-group userFin-name-group">
                                        <label for="userFin-name" class="col-md-4 control-label"><loc:message
                                                code="admin.login"/></label>

                                        <div class="col-md-8">
                                            <form:input path="id" type="hidden" class="form-control" id="userFin-id"/>
                                            <form:input path="role" type="hidden" class="form-control"
                                                        id="userFin-role"/>
                                            <form:input path="status" type="hidden" class="form-control"
                                                        id="userFin-status"/>
                                            <form:input path="nickname" class="form-control" id="userFin-name"
                                                        readonly="true"/>
                                        </div>
                                    </div>
                                    <div class="form-group userFin-email-group">
                                        <label for="userFin-email" class="col-md-4 control-label"><loc:message
                                                code="admin.email"/></label>

                                        <div class="col-md-8">
                                            <form:errors path="email" style="color:red"/>
                                            <form:input path="email" class="form-control" id="userFin-email"
                                                        readonly="true"/>
                                        </div>
                                    </div>
                                    <div class="form-group userFin-password-group">
                                        <label for="userFin-password" path="finpassword"
                                               class="col-md-4 control-label"><loc:message
                                                code="admin.finPassword"/></label>

                                        <div class="col-md-8">
                                            <form:errors path="finpassword" style="color:red"/>
                                            <form:password path="finpassword" class="form-control"
                                                           id="userFin-password"/>
                                        </div>
                                    </div>
                                    <div class="form-group userFin-confirmpassword-group">
                                        <label for="userFin-confirmpassword" path="confirmpassword"
                                               class="col-md-4 control-label"><loc:message
                                                code="admin.confirmpassword"/></label>

                                        <div class="col-md-8">
                                            <form:errors path="confirmPassword" style="color:red"/>
                                            <form:password path="confirmPassword" class="form-control"
                                                           id="userFin-confirmpassword"/>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <div class="text-center">
                                            <loc:message code="admin.save" var="saveSubmit"></loc:message>
                                            <input class="btn btn-primary" value="${saveSubmit}" type="submit">

                                            <loc:message code="admin.cancel" var="cancelSubmit"></loc:message>
                                            <input onclick="javascript:window.location='/settings';"
                                                   class="btn btn-default" value="${cancelSubmit}" type="reset">
                                        </div>
                                    </div>
                                </fieldset>
                            </div>
                        </form:form>
                    </div>
                    <a><loc:message
                            code="admin.changePasswordSendEmail"/></a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
