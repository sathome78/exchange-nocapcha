<%@page language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%--<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>--%>

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

</head>
<body>

<div class="wrapper lk">

    <div class="container container_center full__height">

        <%@include file='../usermenu.jsp' %>

        <div class="main__content">

            <%@include file='../header.jsp' %>

            <div class="col-md-12 main">
                <div class="panel user-panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title"><loc:message code="admin.editUser"/></h3>
                    </div>
                    <div class="panel-body">

                       <form:form class="form-horizontal" id="user-edit-form" action="/admin/edituser/submit"
                                   method="post" modelAttribute="user">
                            <div>
                                <fieldset class="field-user">
                                    <div class="form-group user-name-group">
                                        <label for="user-name" class="col-md-4 control-label"><loc:message
                                                code="admin.login"/></label>
                                        <div class="col-md-8">
                                            <form:input path="id" type="hidden" class="form-control" id="user-id"/>
                                            <form:input path="nickname" class="form-control" id="user-name" readonly="true"/>
                                        </div>
                                    </div>
                                    <div class="form-group user-email-group">
                                        <label for="user-email" class="col-md-4 control-label"><loc:message
                                                code="admin.email"/></label>
                                        <div class="col-md-8">
                                            <form:errors path="email" style="color:red"/>
                                            <form:input path="email" class="form-control" id="user-email" readonly="true"/>
                                        </div>
                                    </div>
                                    <div class="form-group user-password-group">
                                        <label for="user-password" path="password" class="col-md-4 control-label"><loc:message
                                                code="admin.password"/></label>
                                        <div class="col-md-8">
                                            <form:errors path="password" style="color:red"/>
                                            <form:password path="password" class="form-control" id="user-password"/>
                                        </div>
                                    </div>
                                    <div class="form-group user-phone-group">
                                        <label for="user-phone" class="col-md-4 control-label"><loc:message
                                                code="admin.phone"/></label>
                                        <div class="col-md-8">
                                            <form:errors path="phone" style="color:red"/>
                                            <form:input path="phone" class="form-control" id="user-phone" />
                                        </div>
                                    </div>


                                    <div class="form-group user-role-group">
                                        <label for="user-role" class="col-md-4 control-label"><loc:message
                                                code="admin.role"/></label>
                                        <div class="col-md-8">
                                            <form:select path="role" id="user-role" class="form-control"
                                                         name="user-role" >
                                                <c:forEach items="${roleList}" var="role">
                                                    <option value="${role}" <c:if test="${role eq user.role}">SELECTED</c:if>>${role}</option>
                                                </c:forEach>
                                            </form:select>
                                        </div>
                                    </div>

                                    <div class="form-group user-status-group">
                                        <label for="user-status" class="col-md-4 control-label"><loc:message
                                                code="admin.status"/></label>
                                        <div class="col-md-8">
                                            <form:select path="status" id="user-status" class="form-control"
                                                         name="user-status">
                                                <c:forEach items="${statusList}" var="status">
                                                    <option value="${status}" <c:if test="${status eq user.status}">SELECTED</c:if>>${status}</option>
                                                    <%--<option>${status}</option>--%>
                                                </c:forEach>
                                            </form:select>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <div class="text-center">
                                            <loc:message code="admin.save" var="saveSubmit"></loc:message>
                                            <input class="btn btn-primary" value="${saveSubmit}" type="submit">

                                            <loc:message code="admin.cancel" var="cancelSubmit"></loc:message>
                                            <input onclick="javascript:window.location='/admin';"
                                                   class="btn btn-default" value="${cancelSubmit}" type="reset">
                                        </div>
                                    </div>
                                </fieldset>
                            </div>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
