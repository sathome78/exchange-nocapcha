<div class="modal fade" id="voucher_reedem_modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel"><loc:message code="voucher.code"/></h4>
            </div>
            <div class="modal-body modal-content__input-block-wrapper">
                <div class="content modal-content__content-wrapper">
                    <form id="voucher_code_form">
                        <loc:message
                                code="voucher.code"/>
                        <input id="code" type="text" name="code" autofocus />
                        <%--csrf--%>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <br/>
                        <%--отправить--%>
                        <button id="submit_code" type="button" class="button_enter">
                            <loc:message code="admin.submit"/></button>
                    </form>
                    <div id="voucher_result" hidden></div>
                </div>
            </div>
        </div>
    </div>
</div>
