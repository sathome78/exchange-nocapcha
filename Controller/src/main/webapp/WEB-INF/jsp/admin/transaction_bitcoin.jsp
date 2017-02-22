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

<style>
  #btc_invoice_requests th{
    padding: 5px 10px;
    font-size: 11px;
    font-weight: 600;
    text-align: center;
  }
  #btc_invoice_requests td{
    padding: 5px 10px;
    font-size: 11px;
    text-align: center;
  }

  .address-ref{
    cursor: pointer;
  }

</style>

<main class="container">
  <div class="row">
    <%@include file='left_side_menu.jsp' %>
    <div class="col-md-10 content admin-container">
      <div class="text-center"><h4><loc:message code="transaction.titleBitcoin"/></h4></div>
      <table id="btc_invoice_requests"  class="table-striped">
        <thead>
        <tr>
          <th><loc:message code="transaction.datetime"/></th>
          <th><loc:message code="transaction.id"/></th>
          <th><loc:message code="transaction.initiatorEmail"/></th>
          <th><loc:message code="transaction.status"/></th>
          <th><loc:message code="transaction.amount"/></th>
          <th><loc:message code="transaction.commissionAmount"/></th>
          <th><loc:message code="transaction.acceptanceDatetime"/></th>
          <th>Hash</th>
          <th>Manual amount</th>
          <th><loc:message code="transaction.confirmation"/></th>
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
            <td>
              <button class="address-ref"
                    onclick="alert('${maptransaction.address}')">
                ${maptransaction.invoiceId}
              </button>
            </td>
            <td>
              <a href="<c:url value='/2a8fy7b07dxe44/userInfo'>
                                    <c:param name="id" value="${maptransaction.userId}"/>
                                    </c:url>">
                  ${maptransaction.userEmail}</a>
            </td>
            <td>
                ${maptransaction.pendingPaymentStatus}
            </td>
            <td>
              <fmt:formatNumber value="${maptransaction.amount}" maxFractionDigits="9"/>
            </td>
            <td>
              <fmt:formatNumber value="${maptransaction.commissionAmount}" maxFractionDigits="9"/>
            </td>
            <td>
                ${maptransaction.acceptanceTime.toLocalDate()}<br/>
                ${maptransaction.acceptanceTime.toLocalTime()}
            </td>
            <td>
              <input id="bitcoin_hash${maptransaction.invoiceId}"
                    <c:if test="${maptransaction.hash!=null && !''.equals(maptransaction.hash)}">
                      <c:out value="readonly"/>
                      </c:if>
                     value="${maptransaction.hash}"
                     style="width: 130px" class="form-control input-block-wrapper__input">
            </td>
            <td>
              <input id="manual_amount${maptransaction.invoiceId}"
                     <c:if test="${maptransaction.hash!=null && !''.equals(maptransaction.hash)}">
                        <c:out value="readonly"/>
                      </c:if>
                     value="${maptransaction.amount+maptransaction.commissionAmount}" style="width: 130px"
                     maxlength="9" class="form-control input-block-wrapper__input numericInputField">
            </td>
            <td>
              ${maptransaction.confirmation}
            </td>
            <td>
              <c:choose>
                <c:when test="${maptransaction.provided}">
                  <c:choose>
                    <c:when test="${maptransaction.acceptanceUserId == null}">by service</c:when>
                    <c:otherwise>${maptransaction.acceptanceUserEmail}</c:otherwise>
                  </c:choose>
                </c:when>
                <c:otherwise>
                  <button class="acceptbtn" type="submit"
                          onclick="submitAcceptBitcoin(${maptransaction.invoiceId})"><loc:message
                          code="transaction.accept"/></button>
                </c:otherwise>
              </c:choose>
            </td>
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

