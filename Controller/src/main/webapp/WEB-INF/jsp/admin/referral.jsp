<%--
  Created by IntelliJ IDEA.
  User: ogolv
  Date: 27.07.2016
  Time: 13:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<html>
<head>
    <title><loc:message code="admin.referralLevels"/></title>
    <%@include file='links_scripts.jsp' %>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<main class="container orders_new transaction my_orders orders .container_footer_bottom my_wallets">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-8 col-md-offset-1">
            <sec:authorize access="hasAnyAuthority('${adminEnum}')">
                <div id="panel4 row" class="tab-pane">
                    <div class="col-sm-4">
                        <h4>
                            <loc:message code="admin.referralLevels"/>
                        </h4>
                        <table class="col-sm-4 ref-lvl-table">
                            <thead>
                            <tr>
                                <th>Level</th>
                                <th>Percent</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${referralLevels}" var="level">
                                <tr class="table-row" data-percent="${level.percent}" data-id="${level.id}" data-level="${level.level}" data-toggle="modal" data-target="#myModal">
                                    <td>
                                            ${level.level}
                                    </td>
                                    <td id="_${level.level}">
                                        <span class="lvl-percent">${level.percent}</span>%
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div class="col-sm-4">
                        <h4>
                            <loc:message code="admin.refCommonRoot"/>
                            <c:choose>
                                <c:when test="${commonRefRoot != null}">
                                    <span data-id="${commonRefRoot.id}" id="ref-root-info">(${commonRefRoot.email})</span>
                                </c:when>
                                <c:otherwise>
                                    <span id="current-ref-root">(<loc:message code="admin.refAbsentCommonRoot"/>)</span>
                                </c:otherwise>
                            </c:choose>
                        </h4>
                        <form id="edit-cmn-ref-root">
                            <select name="ref-root">
                                <c:forEach items="${admins}" var="admin">
                                    <option value="${admin.id}">${admin.email}</option>
                                </c:forEach>
                            </select>
                            <button type="submit"><loc:message code="admin.refSubmitEditCommonRoot"/></button>
                        </form>
                    </div>
                </div>
            </sec:authorize>

        </div>
    </div>
</main>



</body>
</html>
