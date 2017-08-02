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
        <div class="col-md-6 col-md-offset-2 content admin-container">
            <div class="text-center"><h4><loc:message code="admin.autoTrading.title"/></h4></div>


            <div id="autotradingMenu" class="buttons">
                <button class="active adminForm-toggler blue-box">
                    <loc:message code="admin.autoTrading.settings.roles"/>
                </button>
                <button class="adminForm-toggler blue-box">
                    <loc:message code="admin.autoTrading.settings.bot"/>
                </button>
            </div>
            <div class="tab-content">
                <div id="panel1" class="tab-pane active">
                    <div class="col-sm-6">
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
                <div id="panel2" class="tab-pane">
                    <div class="col-md-8">
                        <div class="text-center"><h4><loc:message code="admin.autoTrading.settings.bot"/></h4></div>
                        <div>
                            <c:choose>
                                <c:when test="${not empty bot}">
                                    <form id="bot-settings-form" class="form_full_height_width">
                                        <div class="input-block-wrapper">
                                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                <label for="nickname" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.bot.nickname"/></label>
                                            </div>
                                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                <input id="nickname" class="input-block-wrapper__input admin-form-input" value="<c:out value="${botUser.nickname}"/>" readonly>
                                            </div>
                                        </div>
                                        <div class="input-block-wrapper">
                                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                <label for="email" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.bot.email"/></label>
                                            </div>
                                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                <input id="email" class="input-block-wrapper__input admin-form-input" value="<c:out value="${botUser.email}"/>" readonly>
                                            </div>
                                        </div>
                                        <div class="input-block-wrapper">
                                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                <label for="enabled" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.bot.status"/></label>
                                            </div>
                                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                <input id="enabled" type="checkbox" name="isEnabled" <c:out value="${bot.isEnabled ? 'checked' : ''}"/> class="input-block-wrapper__input">
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
                                </c:when>
                                <c:otherwise>
                                    <p><loc:message code="admin.autoTrading.bot.notCreated"/> </p>

                                </c:otherwise>
                            </c:choose>
                        </div>






                    </div>
                </div>

            </div>



        </div>
</main>
<div id="editCommissionModal" class="modal fade">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="admin.editCommission"/></h4>
            </div>
            <div class="modal-body">
                <%--<form id="edit-commission-form" class="form_full_width form_auto_height">
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

<div id="editMerchantCommissionModal" class="modal fade">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="admin.editCommission"/></h4>
            </div>
            <div class="modal-body">
                <form id="edit-merchantCommission-form" class="form_full_width form_auto_height">
                    <input type="hidden" name="merchantId" >
                    <input type="hidden" name="currencyId" >
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="merchantName" class="input-block-wrapper__label"><loc:message code="withdrawal.merchant"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="merchantName" class="input-block-wrapper__input" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="currencyName" class="input-block-wrapper__label"><loc:message code="withdrawal.currency"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="currencyName" class="input-block-wrapper__input" readonly type="text">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.merchantsCommissions.input"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input  name="inputValue" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.merchantsCommissions.output"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input  name="outputValue" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.merchantsCommissions.minFixed"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input  name="minFixedAmount" class="input-block-wrapper__input" type="number">
                        </div>
                    </div>
                    <button id="submitMerchantCommission" class="blue-box admin-form-submit" type="submit"><loc:message code="admin.refSubmitEditCommonRoot"/></button>
                </form>
            </div>
        </div>
    </div>
</div>

<%@include file='../fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
