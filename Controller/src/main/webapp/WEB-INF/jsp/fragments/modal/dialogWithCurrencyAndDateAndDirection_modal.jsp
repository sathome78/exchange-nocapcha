<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Valk
  Date: 18.05.2016
  Time: 10:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%----------%>
<%--<script src="<c:url value="/client/js/jquery-ui.js"/>"></script>
<script src="<c:url value="/client/js/datepicker-local/datepicker-ru.js"/>"></script>
<script src="<c:url value="/client/js/datepicker-local/datepicker-zh-CN.js"/>"></script>
<script>
    $(function () {
        $("#report-dialog-currency-date-direction-dialog").find(".datepicker").datepicker({
            dateFormat: "yy-mm-dd"
        });
        var datePickerLocale = "";
        var currentLocale = $('#language').text().trim().toLowerCase();
        if (currentLocale === 'ru') {
            datePickerLocale = currentLocale;
        } else if (currentLocale === 'cn') {
            datePickerLocale = 'zh_CN'
        }
        $.datepicker.setDefaults( $.datepicker.regional[ datePickerLocale ] );
    });
</script>--%>
<%----------%>

<div id="report-dialog-currency-date-direction-dialog" class="modal fade delete-order-info__modal form_full_height_width" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="wallets.download"/></h4>
            </div>
            <div class="modal-body delete-order-info">
                <form action="#">
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="userwallets.startDate"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="start-date" name="startDate"
                                   placeholder="<loc:message code="ordersearch.datelaceholder"/>"
                                   class="form-control input-block-wrapper__input datepicker"/>
                        </div>
                        <div for="start-date" hidden class="col-md-7 input-block-wrapper__error-wrapper" >
                            <label for="start-date" class="input-block-wrapper__input"><loc:message code="ordersearch.errordate"/></label>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="userwallets.endDate"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <input id="end-date" name="endDate"
                                   placeholder="<loc:message code="ordersearch.datelaceholder"/>"
                                   class="form-control input-block-wrapper__input datepicker"/>
                        </div>
                        <div for="end-date" hidden class="col-md-7 input-block-wrapper__error-wrapper" >
                            <label for="end-date" class="input-block-wrapper__input"><loc:message code="ordersearch.errordate"/></label>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="userwallets.currencies"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <select id="currencies" name="currencyList"
                                   class="form-control input-block-wrapper__input">
                                <c:forEach var="currency" items="${usersCurrencyPermittedList}">
                                    <option value="${currency}">${currency}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="userwallets.direction"/></label>
                        </div>
                        <div class="col-md-7 input-block-wrapper__input-wrapper">
                            <select id="direction" name="direction"
                                    class="form-control input-block-wrapper__input">
                                <c:forEach var="direction" items="${operationDirectionList}">
                                    <option value="${direction}">${direction}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <div class="delete-order-info__button-wrapper">
                    <button id="delete-order-info__search" class="delete-order-info__button"
                            onclick="makeReport()"><loc:message
                            code="wallets.download"/></button>
                    <button class="delete-order-info__button" data-dismiss="modal"
                            ><loc:message
                            code="submitorder.cancell"/></button>
                </div>
            </div>
        </div>
    </div>
</div>

