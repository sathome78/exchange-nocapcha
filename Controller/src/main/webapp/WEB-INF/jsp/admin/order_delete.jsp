<%--
  Created by IntelliJ IDEA.
  User: Valk
  Date: 11.05.2016
  Time: 19:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <!--[if lt IE 9]><script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
    <title><loc:message code="ordersearch.title"/></title>
    <meta name="keywords" content="" />
    <meta name="description" content="" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <%@include file='links_scripts.jsp'%>
    <script type="text/javascript" src="<c:url value='/client/js/order/adminDeleteOrder.js'/>"></script>
</head>

<body>
<%@include file='../fragments/header-simple.jsp'%>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="row">
        <div class="col-md-6 col-md-offset-2 content admin-container">
            <div id="order-search">
                <div class="text-center">
                    <h4 class="modal-title"><loc:message code="ordersearch.title"/></h4>
                </div>

                <form id="delete-order-info__form" action="/admin/searchorders" method="get">
                    <div class="input-block-wrapper">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="ordersearch.currencypair"/></label>
                        </div>
                        <div class="col-md-9 input-block-wrapper__input-wrapper">
                            <select id="currencyPair" class="input-block-wrapper__input admin-form-input" name="currencyPair">
                                    <option value="-1">ANY</option>
                                <c:forEach items="${currencyPairList}" var="currencyPair">
                                    <option value="${currencyPair.id}">${currencyPair.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="ordersearch.type"/></label>
                        </div>
                        <div class="col-md-9 input-block-wrapper__input-wrapper">
                            <%--<div class="input-block-wrapper__inner-label">${orderCreateDto.currencyPair.getCurrency2().getName()}</div>--%>
                            <select id="orderType" class="input-block-wrapper__input admin-form-input" name="orderType">
                                <option value="ANY">ANY</option>
                                <option value="SELL">SELL</option>
                                <option value="BUY">BUY</option>
                            </select>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="ordersearch.date"/></label>
                        </div>
                        <div class="col-md-4 input-block-wrapper__input-wrapper">
                            <input id="orderDateFrom" name="orderDateFrom"
                                   placeholder="<loc:message code="ordersearch.datetimeplaceholder"/>"
                                   class="form-control input-block-wrapper__input"/>
                        </div>
                        <div class="col-md-5 input-block-wrapper__input-wrapper">
                            <input id="orderDateTo" name="orderDateTo"
                                   placeholder="<loc:message code="ordersearch.datetimeplaceholder"/>"
                                   class="form-control input-block-wrapper__input"/>
                        </div>
                        <div for="orderDateFrom" hidden class="col-md-6 input-block-wrapper__error-wrapper">
                            <label for="orderDateFrom" class="input-block-wrapper__input"><loc:message
                                    code="ordersearch.errordatetime"/></label>
                        </div>
                        <div for="orderDateTo" hidden class="col-md-6 input-block-wrapper__error-wrapper">
                            <label for="orderDateTo" class="input-block-wrapper__input"><loc:message
                                    code="ordersearch.errordatetime"/></label>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="ordersearch.rate"/></label>
                        </div>
                        <div class="col-md-9 input-block-wrapper__input-wrapper">
                            <input id="orderRate" name="orderRate" class="input-block-wrapper__input admin-form-input"
                                   placeholder="0.0"/>
                        </div>
                        <div for="orderRate" hidden class="col-md-7 input-block-wrapper__error-wrapper">
                            <label for="orderRate" class="input-block-wrapper__input"><loc:message
                                    code="ordersearch.errornumber"/></label>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="ordersearch.volume"/></label>
                        </div>
                        <div class="col-md-9 input-block-wrapper__input-wrapper">
                            <input id="orderVolume" name="orderVolume" class="input-block-wrapper__input admin-form-input"
                                   placeholder="0.0"/>
                        </div>
                        <div for="orderVolume" hidden class="col-md-7 input-block-wrapper__error-wrapper">
                            <label for="orderVolume" class="input-block-wrapper__input"><loc:message
                                    code="ordersearch.errornumber"/></label>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="orderinfo.creator"/></label>
                        </div>
                        <div class="col-md-9 input-block-wrapper__input-wrapper">
                            <input id="creatorEmail" name="creator" class="input-block-wrapper__input admin-form-input"
                                   placeholder="user@user.com"/>
                        </div>
                        <div for="creatorEmail" hidden class="col-md-7 input-block-wrapper__error-wrapper">
                            <label for="creatorEmail" class="input-block-wrapper__input"><loc:message
                                    code="ordersearch.erroremail"/></label>
                        </div>
                    </div>
                    <div class="delete-order-info__button-wrapper">
                        <button id="delete-order-info__search" class="delete-order-info__button blue-box"
                                type="button" onclick="searchOrder()"><loc:message
                                code="ordersearch.submit"/></button>

                    </div>

                </form>

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

