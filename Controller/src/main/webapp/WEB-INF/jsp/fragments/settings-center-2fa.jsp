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
                <c:if test="${enable_2fa != null}">
                    <form method="post" action="/settings/2FaOptions/submit" id="2faSettings_form">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <table id="notification-options-table" class="table">
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
                        </table>
                        <div id="result" hidden></div>
                        <button id="submitSessionOptionsButton" type="submit"
                                <c:if test="${global_use_2fa == false}">disabled</c:if> class="blue-box">
                            <loc:message code="button.update"/></button>
                    </form>
                </c:if>

            </div>
        </div>
    </div>
</section>