<%@page language="java"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <!--[if lt IE 9]><script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
    <title><loc:message code="admin.withdrawRequests"/></title>
    <meta name="keywords" content="" />
    <meta name="description" content="" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <%@include file='admin/links_scripts.jsp'%>
    <script type="text/javascript" src="<c:url value='/client/js/withdrawal.js'/>"></script>

</head>

<body>
<%@include file='fragments/header-simple.jsp'%>
<main class="container">
    <div class="row">
        <%@include file='admin/left_side_menu.jsp' %>
        <div class="col-md-8 col-sm-offset-1 content text-center admin-container">
            <h4><loc:message code="admin.withdrawRequests"/></h4>
            <table id="withdrawalTable">
                <thead>
                <tr>
                    <th><loc:message code="transaction.id"/></th>
                    <th><loc:message code="withdrawal.requestDatetime"/></th>
                    <th><loc:message code="withdrawal.user"/></th>
                    <th><loc:message code="withdrawal.amount"/></th>
                    <th><loc:message code="withdrawal.currency"/></th>
                    <th><loc:message code="withdrawal.commission"/></th>
                    <th><loc:message code="withdrawal.merchant"/></th>
                    <th><loc:message code="withdrawal.acceptanceDatetime"/></th>
                    <th><loc:message code="withdrawal.acceptanceUser"/></th>
                </tr>
                </thead>
                <%--<tbody>
                <c:forEach items="${requests}" var="request">
                    <tr class="id_${request.transaction.id}">
                        <td>
                            <button class="request_id_button" onclick="viewRequestInfo(this)"> ${request.transaction.id}</button>

                        </td>
                        <td>
                                ${request.transaction.datetime.toLocalDate()}<br/>
                                ${request.transaction.datetime.toLocalTime()}
                         </td>
                        <td>
                            <a href="<c:url value='/2a8fy7b07dxe44/userInfo'>
                            <c:param name="id" value="${request.userId}"/>
                            </c:url>">${request.userEmail}</a>
                            <c:if test="${not empty request.userFullName}">
                                <br/>
                                ${request.userFullName}
                            </c:if>
                        </td>
                        <td>
                            <fmt:formatNumber value="${request.transaction.amount}" maxFractionDigits="9"/>
                        </td>
                        <td>
                                ${request.transaction.currency.name}
                        </td>
                        <td>
                            <fmt:formatNumber value="${request.transaction.commissionAmount}" maxFractionDigits="9"/>
                        </td>
                        <td>
                                ${request.transaction.merchant.name}
                                ${request.merchantImage.image_name}
                                <c:if test="${not empty request.recipientBankName}">
                                    <br/>
                                    ${request.recipientBankName}
                                </c:if>
                                <c:if test="${not empty request.recipientBankCode}">
                                    ${request.recipientBankCode}
                                </c:if>

                        </td>
                        <td>
                                ${request.wallet}
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${request.acceptance.isAfter(request.transaction.datetime)}">
                                    ${request.acceptance.toLocalDate()}<br/>
                                    ${request.acceptance.toLocalTime()}
                                 </c:when>
                                <c:otherwise>
                                    _
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty request.processedBy}">
                                    <a href="<c:url value='/2a8fy7b07dxe44/userInfo'>
                                    <c:param name="id" value="${request.processedById}"/>
                                    </c:url>">${request.processedBy}</a>
                                </c:when>
                                <c:otherwise>
                                    _
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${request.status.type == 2}">
                                    <loc:message code="merchants.withdrawRequestAccepted"/>
                                </c:when>
                                <c:when test="${request.status.type == 3}">
                                    <loc:message code="merchants.WithdrawRequestDecline"/>
                                </c:when>
                                <c:otherwise>
                                    <form class="accept_withdrawal_rqst">
                                        <input type="hidden" name="requestId" value="${request.transaction.id}">
                                        <button type="submit" class="btn btn-link">
                                            <loc:message code="merchants.withdrawRequestAccept"/>
                                        </button>
                                    </form>
                                    <form class="decline_withdrawal_rqst">
                                        <input type="hidden" name="requestId" value="${request.transaction.id}">
                                        <button type="submit" class="btn btn-link">
                                            <loc:message code="merchants.withdrawRequestDecline"/>
                                        </button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td hidden>${request.remark}</td>
                        <td hidden>${request.recipientBankName} ${request.recipientBankCode}</td>
                        <td hidden>${request.userFullName}</td>
                    </tr>

                </c:forEach>
                </tbody>--%>
            </table>
        </div>
    </div>

</main>
<div id="acceptRequestMessage" style="display: none">
    <loc:message code="merchants.withdrawRequestAccept"/>
</div>
<div id="declineRequestMessage" style="display: none">
    <loc:message code="merchants.withdrawRequestDecline"/>
</div>

<div id="accepted" style="display: none">
    <loc:message code="merchants.withdrawRequestAccepted"/>
</div>
<div id="declined" style="display: none">
    <loc:message code="merchants.WithdrawRequestDecline"/>
</div>
<div id="prompt_acc_rqst" style="display: none">
    <loc:message code="merchants.promptWithdrawRequestAccept"/>
</div>
<div id="prompt_dec_rqst" style="display: none">
    <loc:message code="merchants.promptWithdrawRequestDecline"/>
</div>
<div id="prompt_send_message_rqst" style="display: none">
    <loc:message code="admin.promptSendMessageRequestAccept"/>
</div>
<%--MODAL ... --%>
<div class="modal fade comment" id="myModal">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">

                <h4 class="modal-title" id="user_info"></h4>
            </div>
            <div class="modal-body">
                <p><loc:message code="admin.comment"/>:<Br>
                    <textarea class="form-control" cols="40" rows="3" id="commentText"></textarea>
                <p><input style="vertical-align: bottom" id="sendMessageCheckbox" type="checkbox">
                    <loc:message code="admin.sendMessage"/>
                <p><span id="checkMessage" style="color: #FF0000; " hidden><loc:message code="admin.checkLanguage"/></span>

            </div>
            <div class="modal-footer">
                <div>
                    <button class="modal-button" type="button" id="createCommentConfirm">
                        <loc:message code="merchants.continue"/>
                    </button>

                    <button class="modal-button" type="button" id="createCommentCancel" data-dismiss="modal">
                        <loc:message code="merchants.close"/>
                    </button>
                </div>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<%--... MODAL--%>


<%@include file='fragments/modal/withdraw_info_modal.jsp' %>
<%@include file='fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
