<%--
  User: Valk
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%----------%>

<div id="order-info-modal" class="modal fade order-info__modal modal-form-dialog" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="orderinfo.info"/></h4>
            </div>
            <div class="modal-body order-info">
                <div class="input-block-wrapper clearfix">
                    <div class="col-md-5 input-block-wrapper__label-wrapper">
                        <label class="input-block-wrapper__label">
                            <loc:message code="orderinfo.id"/>
                        </label>
                    </div>
                    <div class="col-md-7 input-block-wrapper__input-wrapper">
                        <input id="order-info-id"
                               readonly="true"
                               autocomplete="off"
                               class="form-control input-block-wrapper__input"/>
                    </div>
                </div>
                <div class="input-block-wrapper clearfix">
                    <div class="col-md-5 input-block-wrapper__label-wrapper">
                        <label class="input-block-wrapper__label">
                            <loc:message code="orderinfo.status"/>
                        </label>
                    </div>
                    <div class="col-md-7 input-block-wrapper__input-wrapper">
                        <input id="order-info-orderStatusName"
                               readonly="true"
                               autocomplete="off"
                               class="form-control input-block-wrapper__input"/>
                    </div>
                </div>
                </br>
                <div class="input-block-wrapper clearfix">
                    <div class="col-md-5 input-block-wrapper__label-wrapper">
                        <label class="input-block-wrapper__label">
                            <loc:message code="orderinfo.currencypair"/>
                        </label>
                    </div>
                    <div class="col-md-7 input-block-wrapper__input-wrapper">
                        <input id="order-info-currencyPairName"
                               readonly="true"
                               autocomplete="off"
                               class="form-control input-block-wrapper__input"/>
                    </div>
                </div>
                <div class="input-block-wrapper clearfix">
                    <div class="col-md-5 input-block-wrapper__label-wrapper">
                        <label class="input-block-wrapper__label">
                            <loc:message code="ordersearch.type"/>
                        </label>
                    </div>
                    <div class="col-md-7 input-block-wrapper__input-wrapper">
                        <input id="order-info-orderTypeName"
                               readonly="true"
                               autocomplete="off"
                               class="form-control input-block-wrapper__input"/>
                    </div>
                </div>
                <div class="input-block-wrapper clearfix">
                    <div class="col-md-5 input-block-wrapper__label-wrapper">
                        <label class="input-block-wrapper__label">
                            <loc:message code="ordersearch.rate"/>
                        </label>
                    </div>
                    <div class="col-md-7 input-block-wrapper__input-wrapper">
                        <input id="order-info-exrate"
                               readonly="true"
                               autocomplete="off"
                               class="form-control input-block-wrapper__input"/>
                    </div>
                </div>
                <div class="input-block-wrapper clearfix">
                    <div class="col-md-5 input-block-wrapper__label-wrapper">
                        <label class="input-block-wrapper__label">
                            <loc:message code="orderinfo.baseamount"/>
                        </label>
                    </div>
                    <div class="col-md-7 input-block-wrapper__input-wrapper">
                        <input id="order-info-amountBase"
                               readonly="true"
                               autocomplete="off"
                               class="form-control input-block-wrapper__input"/>
                    </div>
                </div>
                <div class="input-block-wrapper clearfix">
                    <div class="col-md-5 input-block-wrapper__label-wrapper">
                        <label class="input-block-wrapper__label">
                            <loc:message code="orderinfo.convertamount"/>
                        </label>
                    </div>
                    <div class="col-md-7 input-block-wrapper__input-wrapper">
                        <input id="order-info-amountConvert"
                               readonly="true"
                               autocomplete="off"
                               class="form-control input-block-wrapper__input"/>
                    </div>
                </div>
                </br>
                <div class="input-block-wrapper clearfix">
                    <div class="col-md-5 input-block-wrapper__label-wrapper">
                        <label class="input-block-wrapper__label">
                            <loc:message code="orderinfo.createdate"/>
                        </label>
                    </div>
                    <div class="col-md-7 input-block-wrapper__input-wrapper">
                        <input id="order-info-dateCreation"
                               readonly="true"
                               autocomplete="off"
                               class="form-control input-block-wrapper__input"/>
                    </div>
                </div>
                <div class="input-block-wrapper clearfix">
                    <div class="col-md-5 input-block-wrapper__label-wrapper">
                        <label class="input-block-wrapper__label">
                            <loc:message code="orderinfo.acceptdate"/>
                        </label>
                    </div>
                    <div class="col-md-7 input-block-wrapper__input-wrapper">
                        <input id="order-info-dateAcception"
                               readonly="true"
                               autocomplete="off"
                               class="form-control input-block-wrapper__input"/>
                    </div>
                </div>
                <c:set var="adminEnum" value="<%=me.exrates.model.enums.UserRole.ADMINISTRATOR%>"/>
                <c:set var="accountantEnum" value="<%=me.exrates.model.enums.UserRole.ACCOUNTANT%>"/>
                <c:set var="admin_userEnum" value="<%=me.exrates.model.enums.UserRole.ADMIN_USER%>"/>
                <sec:authorize
                        access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                    </br>
                    <div class="input-block-wrapper clearfix">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label">
                                <loc:message code="orderinfo.creator"/>
                            </label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="order-info-orderCreatorEmail"
                                   readonly="true"
                                   autocomplete="off"
                                   class="form-control input-block-wrapper__input"/>
                        </div>
                    </div>
                    <div class="input-block-wrapper clearfix">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label">
                                <loc:message code="orderinfo.acceptor"/>
                            </label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="order-info-orderAcceptorEmail"
                                   readonly="true"
                                   autocomplete="off"
                                   class="form-control input-block-wrapper__input"/>
                        </div>
                    </div>
                    </br>
                    <div class="input-block-wrapper clearfix">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label">
                                <loc:message code="orderinfo.companycommission"/>
                            </label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="order-info-companyCommission"
                                   readonly="true"
                                   autocomplete="off"
                                   class="form-control input-block-wrapper__input"/>
                        </div>
                    </div>
                    </br>
                </sec:authorize>
            </div>
            <div class="modal-footer">
                <div class="order-info__button-wrapper">
                    <button class="order-info__button" data-dismiss="modal">
                        <loc:message code="orderinfo.ok"/>
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>

