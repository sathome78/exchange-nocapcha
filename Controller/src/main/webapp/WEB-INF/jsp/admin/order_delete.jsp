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
    <title><loc:message code="orderinfo.title"/></title>
    <meta name="keywords" content="" />
    <meta name="description" content="" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <%@include file='links_scripts.jsp'%>
    <link rel="stylesheet" href="<c:url value="/client/css/jquery.datetimepicker.css"/>">
    <script type="text/javascript" src="<c:url value='/client/js/jquery.datetimepicker.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/moment-with-locales.min.js'/>"></script>
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
                            <label class="input-block-wrapper__label">
                                <loc:message code="ordersearch.date" />
                            </label>
                        </div>
                        <div class="col-md-9 input-block-wrapper__input-wrapper">
                            <input id="datetimepicker_start" type="text" name="orderDateFrom">
                            <input id="datetimepicker_end" type="text" name="orderDateTo">
                        </div>
                        <div for="datetimepicker_start" hidden class="col-md-7 input-block-wrapper__error-wrapper">
                            <label for="datetimepicker_start" class="input-block-wrapper__input"><loc:message
                                    code="ordersearch.errordatetime"/></label>
                        </div>
                        <div for="datetimepicker_end" hidden class="col-md-7 input-block-wrapper__error-wrapper">
                            <label for="datetimepicker_end" class="input-block-wrapper__input"><loc:message
                                    code="ordersearch.errordatetime"/></label>
                        </div>

                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="ordersearch.rate"/></label>
                        </div>
                        <div class="col-md-9 input-block-wrapper__input-wrapper">
                            <input type="number" id="orderRateFrom" name="orderRateFrom" placeholder="0.0"/>
                            <input type="number" id="orderRateTo" name="orderRateTo" placeholder="0.0"/>
                        </div>
                        <div for="orderRateFrom" hidden class="col-md-7 input-block-wrapper__error-wrapper">
                            <label for="orderRateFrom" class="input-block-wrapper__input"><loc:message
                                    code="ordersearch.errornumber"/></label>
                        </div>
                        <div for="orderRateTo" hidden class="col-md-7 input-block-wrapper__error-wrapper">
                            <label for="orderRateTo" class="input-block-wrapper__input"><loc:message
                                    code="ordersearch.errornumber"/></label>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="ordersearch.volume"/></label>
                        </div>
                        <div class="col-md-9 input-block-wrapper__input-wrapper">
                            <input type="number" id="orderVolumeFrom" name="orderVolumeFrom"
                                   placeholder="0.0"/>
                            <input type="number" id="orderVolumeTo" name="orderVolumeTo"
                                   placeholder="0.0"/>
                        </div>
                        <div for="orderVolumeFrom" hidden class="col-md-7 input-block-wrapper__error-wrapper">
                            <label for="orderVolumeFrom" class="input-block-wrapper__input"><loc:message
                                    code="ordersearch.errornumber"/></label>
                        </div>
                        <div for="orderVolumeTo" hidden class="col-md-7 input-block-wrapper__error-wrapper">
                            <label for="orderVolumeTo" class="input-block-wrapper__input"><loc:message
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
                    <div class="input-block-wrapper">
                        <div class="col-md-3 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="orderinfo.acceptor"/></label>
                        </div>
                        <div class="col-md-9 input-block-wrapper__input-wrapper">
                            <input id="acceptorEmail" name="acceptor" class="input-block-wrapper__input admin-form-input"
                                   placeholder="user@user.com"/>
                        </div>
                        <div for="acceptorEmail" hidden class="col-md-7 input-block-wrapper__error-wrapper">
                            <label for="acceptorEmail" class="input-block-wrapper__input"><loc:message
                                    code="ordersearch.erroremail"/></label>
                        </div>
                    </div>
                    <div class="delete-order-info__button-wrapper">
                        <button id="delete-order-info__search" class="delete-order-info__button blue-box"
                                type="button"><loc:message code="ordersearch.submit"/></button>

                    </div>

                </form>

            </div>
            <div class="row">
                <table id="order-info-table">
                    <thead>
                    <tr>
                        <th><loc:message code="orderinfo.id"/></th>
                        <th><loc:message code="orderinfo.createdate"/></th>
                        <th><loc:message code="orderinfo.currencypair"/></th>
                        <th><loc:message code="orders.type"/></th>
                        <th><loc:message code="orderinfo.rate"/></th>
                        <th><loc:message code="orderinfo.baseamount"/></th>
                        <th><loc:message code="orderinfo.creator"/></th>
                        <th><loc:message code="orderinfo.status"/></th>
                    </tr>
                    </thead>
                </table>
            </div>

        </div>
        </div>

    </div>
</main>

<div id="order-delete-modal" class="modal fade delete-order-info__modal" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="orderinfo.title"/></h4>
            </div>
            <div class="modal-body delete-order-info">
                <div class="delete-order-info__item" id="id"><loc:message code="orderinfo.id"/><span></span></div>
                <div class="delete-order-info__item" id="orderStatusName"><loc:message
                        code="orderinfo.status"/><span></span></div>
                </br>
                </br>
                <div class="delete-order-info__item" id="currencyPairName"><span></span></div>
                <div class="delete-order-info__item" id="orderTypeName"><span></span></div>
                <div class="delete-order-info__item" id="exrate"><loc:message code="orderinfo.rate"/><span></span></div>
                <div class="delete-order-info__item" id="amountBase"><loc:message
                        code="orderinfo.baseamount"/><span></span></div>
                <div class="delete-order-info__item" id="amountConvert"><loc:message
                        code="orderinfo.convertamount"/><span></span></div>
                </br>
                </br>
                <div class="delete-order-info__item" id="dateCreation"><loc:message
                        code="orderinfo.createdate"/><span></span></div>
                <div class="delete-order-info__item" id="dateAcception"><loc:message
                        code="orderinfo.acceptdate"/><span></span></div>
                </br>
                <div class="delete-order-info__item" id="orderCreatorEmail"><loc:message
                        code="orderinfo.creator"/><span></span></div>
                <div class="delete-order-info__item" id="orderAcceptorEmail"><loc:message
                        code="orderinfo.acceptor"/><span></span></div>
                </br>
                </br>
                <div class="delete-order-info__item" id="companyCommission"><loc:message
                        code="orderinfo.companycommission"/><span></span></div>
                </br>
            </div>
            <div class="modal-footer">
                <div class="delete-order-info__button-wrapper">
                    <button id="delete-order-info__delete" class="delete-order-info__button"
                    ><loc:message
                            code="deleteorder.submit"/></button>
                    <button class="delete-order-info__button" data-dismiss="modal"
                    ><loc:message
                            code="submitorder.cancell"/></button>
                </div>
            </div>
        </div>
    </div>
</div>

<div id="order-delete-modal--result-info" class="modal fade delete-order-info__modal" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><%--<loc:message code="orderinfo.title"/>--%></h4>
            </div>
            <div class="modal-body delete-order-info">
                <div class="delete-order-info__item success"><loc:message
                        code="orderinfo.deletedcount"/><span></span></div>
                <div class="delete-order-info__item error error-delete"><loc:message
                        code="orderinfo.deleteerror"/><span></span></div>

                <div class="delete-order-info__item error error-search"><loc:message
                        code="orderinfo.searcherror"/><span></span></div>
            </div>
            <div class="modal-footer">
                <div class="delete-order-info__button-wrapper">
                    <button class="delete-order-info__button" data-dismiss="modal"
                    ><loc:message
                            code="submitorder.cancell"/></button>
                </div>
            </div>
        </div>
    </div>
</div>

<%@include file='../fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>

