<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title><loc:message code="orderinfo.title"/></title>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/order/adminDeleteOrder.js'/>"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            var $ordertable = $('#order-info-table');
            if ($ordertable.length > 0) {
                $ordertable.DataTable();
            }
        })
    </script>

</head>


<body>

<%@include file='../fragments/header-simple.jsp' %>

<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-8 col-md-offset-1 content text-center admin-container">
            <c:choose>
                <c:when test="${fn:length(orders)==0}">
                    <div class="text-center">
                        <h4><loc:message code="orderinfo.searcherror"/></h4>
                    </div>
                </c:when>
                <c:otherwise>
                    <%--СПИСОК ИНВОЙСОВ--%>
                    <div class="text-center">
                        <h4><loc:message code="orderinfo.title"/></h4>
                    </div>

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
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="order" items="${orders}">
                            <tr onclick="getOrderDetailedInfo.call(this, ${order.id}, event)">
                                <td>${order.id}</td>
                                <td>${order.dateCreation.toLocalDate()}<br/>
                                        ${order.dateCreation.toLocalTime()}</td>
                                <td>${order.currencyPairName}</td>
                                <td>${order.orderTypeName}</td>
                                <td>${order.exrate}</td>
                                <td>${order.amountBase}</td>
                                <td>${order.orderCreatorEmail}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>

                </c:otherwise>
            </c:choose>
        </div>
    </div>
    <hr/>
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
