<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 20.09.2016
  Time: 13:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
