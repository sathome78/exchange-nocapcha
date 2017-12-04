<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 28.11.2016
  Time: 10:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
<head>
    <title><loc:message code="admin.generalStats.title"/></title>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/reportAdmin.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/generalStats.js'/>"></script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>

        <div class="col-md-8 col-sm-offset-1 content admin-container">
            <div class="text-center"> <h4><loc:message code="admin.generalStats.title"/></h4></div>

            <div class="row text-center" style="margin: 20px">

                <div class="input-block-wrapper" >
                    <div class="col-md-2 input-block-wrapper__label-wrapper">
                        <label class="input-block-wrapper__label"><loc:message code="userwallets.startDate"/></label>
                    </div>
                    <div class="col-md-4 input-block-wrapper__input-wrapper">
                        <input id="datetimepicker_start" type="text" class="input-block-wrapper__input admin-form-input" name="startTime">
                    </div>
                    <div class="col-md-2 input-block-wrapper__label-wrapper">
                        <label class="input-block-wrapper__label"><loc:message code="userwallets.endDate"/></label>
                    </div>
                    <div class="col-md-4 input-block-wrapper__input-wrapper">
                        <input id="datetimepicker_end" type="text" class="input-block-wrapper__input admin-form-input" name="startTime">
                    </div>
                </div>






                <%--<input type="checkbox">--%>
            </div>

            <hr/>

            <div class="form_full_height_width col-md-8 col-md-offset-2">
                <div class="input-block-wrapper">
                    <div class="col-md-5 input-block-wrapper__label-wrapper">
                        <label class="input-block-wrapper__label"><loc:message code="admin.users.new"/></label>
                    </div>
                    <div class="col-md-5 input-block-wrapper__input-wrapper">
                        <span id="new-users-quantity">50</span>
                    </div>
                    <div class="col-md-2 input-block-wrapper__input-wrapper">
                        <button id="refresh-users" class="btn btn-sm btn-default pull-right" style="margin-bottom: 10px">
                            <span class="glyphicon glyphicon-refresh"></span>
                        </button>
                    </div>
                </div>


                <div class="input-block-wrapper">
                    <div class="col-md-5 input-block-wrapper__label-wrapper">
                        <label class="input-block-wrapper__label"><loc:message code="admin.generalStats.button.currencies"/></label>
                    </div>
                    <div class="col-md-7 input-block-wrapper__input-wrapper">
                        <button id="download-currencies-report" class="blue-box">
                            <loc:message code="admin.stats.download"/></button>
                    </div>
                </div>
                <div class="input-block-wrapper">
                    <div class="col-md-5 input-block-wrapper__label-wrapper">
                        <label class="input-block-wrapper__label"><loc:message code="admin.generalStats.button.currencyPairs"/></label>
                    </div>
                    <div class="col-md-7 input-block-wrapper__input-wrapper">
                        <button id="download-currency-pairs-report" class="blue-box">
                            <loc:message code="admin.stats.download"/></button>
                    </div>
                </div>

            </div>
        </div>





</main>

<%@include file='../fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
