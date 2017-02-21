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
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
  <script type="text/javascript" src="<c:url value="/client/js/main.js"/>"></script>
  <title><loc:message code="transaction.titleBitcoin"/></title>
  <%@include file='links_scripts.jsp' %>
</head>


<body>

<%@include file='../fragments/header-simple.jsp' %>

<main class="container">
  <div class="row">
    <%@include file='left_side_menu.jsp' %>
    <div class="col-md-10 content admin-container">
      <div class="text-center"><h4><loc:message code="transaction.titleBitcoin"/></h4></div>
      <table id="invoice_requests">
        <thead>
        <tr>
          <%--Дата--%>
          <th><loc:message code="transaction.datetime"/></th>
          <%--Номер--%>
          <th><loc:message code="transaction.id"/></th>
          <%--Email--%>
          <th><loc:message code="transaction.initiatorEmail"/></th>
          <%--Сумма--%>
          <th><loc:message code="transaction.amount"/></th>
          <%--Сумма <br> комиссии--%>
          <th><loc:message code="transaction.commissionAmount"/></th>
          <%--Сумма <br> комиссии--%>
          <th><loc:message code="transaction.acceptanceDatetime"/></th>
          <%--Hash--%>
          <th>Hash</th>
          <%--Manual amount--%>
          <th>Manual amount</th>
          <%--Confirmation/Acceptance user--%>
          <th><loc:message code="transaction.acceptanceUser"/></th>

        </tr>
        </thead>
        <tbody>

        <c:forEach var="maptransaction" items="${bitcoinRequests}">
          <tr>
            <td style="white-space: nowrap;">
                ${maptransaction.datetime.toLocalDate()}<br/>
                ${maptransaction.datetime.toLocalTime()}
            </td>
              <%--Transaction id--%>
            <td>
                ${maptransaction.invoiceId}
            </td>
              <%--Transaction email--%>
            <td>
              <a href="<c:url value='/2a8fy7b07dxe44/userInfo'>
                                    <c:param name="id" value="${maptransaction.userId}"/>
                                    </c:url>">
                  ${maptransaction.userEmail}</a>
            </td>
              <%--Amount--%>
            <td>
              <fmt:formatNumber value="${maptransaction.amount}" maxFractionDigits="9"/>
            </td>
              <%--комиссия--%>
            <td>
              <fmt:formatNumber value="${maptransaction.commissionAmount}" maxFractionDigits="9"/>
            </td>
            <c:choose>
              <c:when test="${maptransaction.provided}">
                <%--Acceptance date--%>
                <td>
                    ${maptransaction.acceptanceTime.toLocalDate()}<br/>
                    ${maptransaction.acceptanceTime.toLocalTime()}
                </td>
                <%--Hash--%>
                <td>
                  <input readonly value="${maptransaction.hash}" style="width: 130px"
                         class="form-control input-block-wrapper__input">
                </td>
                <%--Manual amount--%>
                <td>
                  <fmt:formatNumber value="${maptransaction.amount+maptransaction.commissionAmount}"
                                    maxFractionDigits="9"/>
                </td>
                <%--Подтвердить--%>
                <td>
                  <c:choose>
                    <c:when test="${maptransaction.acceptanceUserId == null}">by service</c:when>
                    <c:otherwise>${maptransaction.acceptanceUserEmail}</c:otherwise>
                  </c:choose>
                </td>
              </c:when>
              <c:otherwise>
                <%--Acceptance date--%>
                <td>
                  -
                </td>
                <%--Hash--%>
                <td>
                  <input id="bitcoin_hash${maptransaction.invoiceId}" value="${maptransaction.hash}"
                         style="width: 130px" class="form-control input-block-wrapper__input">
                </td>
                <%--Manual amount--%>
                <td>
                  <input id="manual_amount${maptransaction.invoiceId}"
                         value="${maptransaction.amount+maptransaction.commissionAmount}" style="width: 130px"
                         maxlength="9" class="form-control input-block-wrapper__input numericInputField">
                </td>
                <%--Подтвердить--%>
                <td>
                  <button class="acceptbtn" type="submit"
                          onclick="submitAcceptBitcoin(${maptransaction.invoiceId})"><loc:message
                          code="transaction.accept"/></button>
                </td>
              </c:otherwise>
            </c:choose>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
  </div>
  <hr/>
</main>
<div id="prompt_acc_rqst" style="display: none">
  <loc:message code="merchants.promptWithdrawRequestAccept"/>
</div>
<%@include file='../fragments/footer.jsp' %>
</body>
</html>

