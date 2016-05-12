<%--
  Created by IntelliJ IDEA.
  User: Valk
  Date: 11.05.2016
  Time: 19:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script type="text/javascript" src="<c:url value='/client/js/bootstrap-datetimepicker.min.js'/>"></script>
<link href="<c:url value='/client/css/bootstrap-datetimepicker.css'/>" rel="stylesheet" type="text/css"/>

<%--<script type="text/javascript">
    $(function () {
        $('#orderDate').datetimepicker({
            format: 'yyyy-mm-dd hh:ii', autoclose: true,
            todayBtn: true
        });
    });
</script>--%>

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

<div id="order-delete-modal--search" class="modal fade delete-order-info__modal" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="ordersearch.title"/></h4>
            </div>
            <div class="modal-body delete-order-info">
                <form id="delete-order-info__form" action="#">
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="ordersearch.currencypair"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <select id="currencyPair" class="input-block-wrapper__input" name="currencyPair">
                                <c:forEach items="${currencyPairList}" var="currencyPair">
                                    <option value="${currencyPair.id}">${currencyPair.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="ordersearch.type"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <%--<div class="input-block-wrapper__inner-label">${orderCreateDto.currencyPair.getCurrency2().getName()}</div>--%>
                            <select id="orderType" class="input-block-wrapper__input" name="orderType">
                                <option value="SELL">SELL</option>
                                <option value="BUY">BUY</option>
                            </select>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="ordersearch.date"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="orderDate" name="orderDate" placeholder="<loc:message code="ordersearch.dateplaceholder"/>"
                                   class="form-control input-block-wrapper__input"/>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="ordersearch.rate"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <%--<div class="input-block-wrapper__inner-label">${orderCreateDto.currencyPair.getCurrency2().getName()}</div>--%>
                            <input id="orderRate" name="orderRate" class="input-block-wrapper__input"/>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message
                                    code="ordersearch.volume"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <%--<div class="input-block-wrapper__inner-label">${orderCreateDto.currencyPair.getCurrency2().getName()}</div>--%>
                            <input id="orderVolume" name="orderVolume" class="input-block-wrapper__input"/>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <div class="delete-order-info__button-wrapper">
                    <button id="delete-order-info__search" class="delete-order-info__button"
                            onclick="searchOrder()"><loc:message
                            code="ordersearch.submit"/></button>
                    <button class="delete-order-info__button" data-dismiss="modal"
                            ><loc:message
                            code="submitorder.cancell"/></button>
                </div>
            </div>
        </div>
    </div>
</div>
