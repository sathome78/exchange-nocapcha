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
    <title><loc:message code="admin.autoTrading.title"/></title>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/admin-autotrading/autotrading.js'/>"></script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>


            <div class="col-md-8 col-md-offset-1 content admin-container">
                <div class="text-center"><h4><loc:message code="admin.autoTrading.title"/></h4>

            <div id="autotradingMenu" class="buttons">
                <button class="active adminForm-toggler blue-box">
                    <loc:message code="admin.autoTrading.settings.bot"/>
                </button>
                <button class="adminForm-toggler blue-box">
                    <loc:message code="admin.autoTrading.settings.bot.trading"/>
                </button>
                <button class="adminForm-toggler blue-box">
                    <loc:message code="admin.autoTrading.settings.roles"/>
                </button>
            </div>
                </div>
            <div class="tab-content">

                <div id="panel1" class="tab-pane active">
                    <div class="col-md-8 col-md-offset-2">
                        <div class="text-center"><h4><loc:message code="admin.autoTrading.settings.bot"/></h4></div>
                        <div>
                            <c:choose>
                                <c:when test="${not empty bot}">
                                    <div class="row">
                                        <form id="bot-settings-form" class="form_full_height_width">
                                            <input id="bot-id" hidden value="<c:out value="${bot.id}"/>" readonly>
                                            <input id="bot-user-id" hidden value="<c:out value="${bot.userId}"/>" readonly>
                                            <div class="input-block-wrapper">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label"><loc:message code="admin.autoTrading.bot.email"/></label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">

                                                    <a class="input-block-wrapper__input admin-form-input text-1_5"
                                                       href="<c:url value="/2a8fy7b07dxe44/userInfo">
                                                   <c:param name = "id" value = "${bot.userId}"/></c:url>">${botUser.email}</a>
                                                </div>
                                            </div>
                                            <div class="input-block-wrapper">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label for="bot-enabled-box" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.bot.status"/></label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper blue-switch">
                                                    <input id="bot-enabled-box" type="checkbox" name="isEnabled" <c:out value="${bot.isEnabled ? 'checked' : ''}"/> class="input-block-wrapper__input">
                                                </div>
                                            </div>
                                            <div class="input-block-wrapper">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label for="timeout" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.bot.timeout"/></label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <input id="timeout" type="number" name="acceptDelayInSeconds" value="<c:out value="${bot.acceptDelayInSeconds}"/>"
                                                           class="input-block-wrapper__input admin-form-input">
                                                </div>
                                            </div>

                                            <button id="submitBotSettings" class="blue-box"><loc:message code="admin.submit"/></button>
                                        </form>
                                    </div>

                                </c:when>
                                <c:otherwise>
                                    <div class="row text-center">
                                        <p class="text-1_5 red"><loc:message code="admin.autoTrading.bot.notCreated"/> </p>
                                    </div>
                                    <form id="bot-creation-form" class="form_full_height_width">
                                        <div class="input-block-wrapper">
                                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                <label for="nickname" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.bot.nickname"/></label>
                                            </div>
                                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                <input id="nickname" name="nickname" class="input-block-wrapper__input admin-form-input">
                                            </div>
                                        </div>
                                        <div class="input-block-wrapper">
                                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                <label for="email" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.bot.email"/></label>
                                            </div>
                                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                <input id="email" type="email" name="email" class="input-block-wrapper__input admin-form-input">
                                            </div>
                                        </div>
                                        <div class="input-block-wrapper">
                                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                <label for="password" class="input-block-wrapper__label"><loc:message code="admin.password"/></label>
                                            </div>
                                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                <input id="password" type="password" name="password" class="input-block-wrapper__input admin-form-input">
                                            </div>
                                        </div>

                                        <button id="submitNewBot" class="blue-box"><loc:message code="admin.submit"/></button>
                                    </form>

                                </c:otherwise>
                            </c:choose>
                        </div>






                    </div>
                </div>
                <div id="panel2" class="tab-pane">
                    <div class="col-md-8 col-md-offset-2">
                        <div class="text-center"><h4><loc:message code="admin.autoTrading.settings.bot.trading"/></h4></div>
                        <div>
                            <c:choose>
                                <c:when test="${not empty bot}">
                                    <div class="row">
                                        <table id="launch-settings-table">
                                            <thead>
                                            <tr>
                                                <th><loc:message code="currency.pair"/></th>
                                                <th><loc:message code="admin.autoTrading.bot.status"/> </th>
                                                <th><loc:message code="admin.autoTrading.bot.launchInterval"/></th>
                                                <th><loc:message code="admin.autoTrading.bot.createTimeout"/></th>
                                                <th><loc:message code="admin.autoTrading.bot.quantityPerSeq"/></th>
                                                <th></th>
                                            </tr>
                                            </thead>
                                        </table>

                                    </div>

                                   <%-- <div class="row">
                                        <select id="currencyPair" class="input-block-wrapper__input admin-form-input">
                                            <c:forEach items="${currencyPairs}" var="currencyPair">
                                                <option value="${currencyPair.id}">${currencyPair.name}</option>
                                            </c:forEach>
                                        </select>

                                        <form id="launch-settings-form">
                                            <div class="input-block-wrapper">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label for="bot-pair-enabled-box" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.bot.status"/></label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper blue-switch">
                                                    <input id="bot-pair-enabled-box" type="checkbox" name="isEnabled" <c:out value="${bot.isEnabled ? 'checked' : ''}"/> class="input-block-wrapper__input">
                                                </div>
                                            </div>

                                        </form>

                                    </div>
                                    <div class="row">
                                        <select id="orderType" class="input-block-wrapper__input admin-form-input">
                                            <c:forEach items="${orderTypes}" var="orderType">
                                                <option value="${orderType.type}">${orderType.name()}</option>
                                            </c:forEach>
                                            <form id="trading-settings-form">

                                            </form>
                                        </select>

                                    </div>--%>
                                </c:when>
                                <c:otherwise>
                                    <div class="row text-center">
                                        <p class="text-1_5 red"><loc:message code="admin.autoTrading.bot.notCreated"/> </p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
                <div id="panel3" class="tab-pane">
                    <div class="col-md-6 col-md-offset-3">
                        <div class="text-center"><h4><loc:message code="admin.autoTrading.settings.roles"/></h4></div>


                        <hr/>

                        <table id="roles-table">
                            <thead>
                            <tr>
                                <th><loc:message code="admin.autoTrading.role"/> </th>
                                <th><loc:message code="admin.autoTrading.sameRoleOnly"/></th>
                                <th><loc:message code="admin.autoTrading.botAccept"/></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>



        </div>
</main>

<div id="editSettingsModal" class="modal fade">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span>&times;</span></button>
                <h4 class="modal-title">talala<%--<loc:message code="admin.editCommission"/>--%></h4>
            </div>
            <div class="modal-body">
                <ul class="nav nav-tabs">
                    <li class="active"><a data-toggle="tab" href="#home">Home</a></li>
                    <li><a data-toggle="tab" href="#menu1">Menu 1</a></li>
                    <li><a data-toggle="tab" href="#menu2">Menu 2</a></li>
                </ul>
                <div class="tab-content">
                    <div id="home" class="tab-pane fade in active">
                        <h3>HOME</h3>
                        <p>Some content.</p>
                    </div>
                    <div id="menu1" class="tab-pane fade">
                        <h3>Menu 1</h3>
                        <p>Some content in menu 1.</p>
                    </div>
                    <div id="menu2" class="tab-pane fade">
                        <h3>Menu 2</h3>
                        <p>Some content in menu 2.</p>
                    </div>
                </div>



                <%--<form id="edit-settings-form" class="form_full_width form_auto_height">
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="userRole" class="input-block-wrapper__label"><loc:message code="admin.role"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="userRole" name="userRole" class="input-block-wrapper__input" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="operationType" class="input-block-wrapper__label"><loc:message code="admin.commissions.operationType"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="operationType" name="operationType" class="input-block-wrapper__input" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="commissionValue" class="input-block-wrapper__label"><loc:message code="admin.commissions.value"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="commissionValue" name="commissionValue" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <button id="submitCommission" class="blue-box admin-form-submit" type="submit"><loc:message code="admin.refSubmitEditCommonRoot"/></button>
                </form>--%>
            </div>
        </div>
    </div>
</div>

<%@include file='../fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
