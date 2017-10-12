<%@ taglib prefix="loc" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section id="2fa-options">
    <h4 class="h4_green">
        <loc:message code="message.2fa.title"/>
    </h4>
    <h4 class="under_h4_margin"></h4>
    <div class="container">
        <div class="row">
            <div class="col-sm-6 content">

                    <form method="post" action="/settings/2FaOptions/submit" id="2faSettings_form">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <table id="2fa-options-table" class="table">
                            <thead>
                            <tr>
                                <th></th>
                                <c:set var = "subscriptions" value = "${user2faOptions.get('subscriptions')}"/>
                                <c:forEach items="${user2faOptions.get('notificators')}" var="notificatorHead">
                                    <th>${notificatorHead}</th>
                                    <c:if test="${notificatorHead.needSubscribe}">
                                        <c:choose>
                                            <c:when test="${subscriptions.get(notificatorHead.code) == null
                                            or not subscriptions.get(notificatorHead.code).isConnected()}">
                                                <button id="subscribe_${notificatorHead}">
                                                    <loc:message code="notificator.conect"/></button>
                                            </c:when>
                                            <c:otherwise>
                                               <p>subscriptions.get(notificatorHead.code).getContactStr()</p>
                                                <button id="subscribe_${notificatorHead}">
                                                    <loc:message code="notificator.reconnect"/></button>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                </c:forEach>
                                <th>Disable</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:set var = "settings" value = "${user2faOptions.get('settings')}"/>
                            <c:forEach items="${user2faOptions.get('events')}" var="event">
                                    <tr>
                                        <td>${event}</td>
                                        <c:forEach items="${user2faOptions.get('notificators')}" var="notificator">
                                            <td><input type="radio" name="${event.code}" value="${notificator.code}"
                                                    <c:if test="${notificator.needSubscribe and (subscriptions.get(notificator.code) == null
                                                    or not subscriptions.get(notificator.code).isConnected())}">
                                                        disabled
                                                    </c:if>
                                                    <c:if test="${settings.get(event.code) != null
                                                    and settings.get(event.code).notificatorId == notificator.code}">CHECKED</c:if>>
                                            </td>
                                        </c:forEach>
                                            <td><input type="radio" name="${event.code}" value="0"
                                                    <c:if test="${settings.get(event.code) == null or settings.get(event.code).notificatorId == null}"> CHECKED</c:if>
                                                />
                                            </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        <button id="submitSessionOptionsButton" type="submit" class="blue-box">
                            <loc:message code="button.update"/></button>



                       <%-- <table id="notification-options-table" class="table">
                            <tbody>
                            <tr id="2fa_cell">
                                <td><loc:message code="message.2fa.via_email"/>:<c:if test="${global_use_2fa == false}">  \disabled!\</disabled></c:if></td>
                                <td><input type="checkbox" id="enable_2fa"
                                                name="enable_2fa"
                                        <c:if test="${global_use_2fa == false}">disabled</c:if>
                                        <c:if test="${enable_2fa}">checked</c:if> /><br>
                                </td>
                            </tr>
                            </tbody>
                        </table>&ndash;%&gt;
                        <div id="result" hidden></div>--%>

                    </form>

            </div>
        </div>
    </div>
</section>
