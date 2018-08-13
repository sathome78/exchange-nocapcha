<section id="nickname-changing">
    <h4 class="h4_green">
        <loc:message code="admin.changeNicknameTitle"/>
    </h4>
    <h4 class="under_h4_margin"></h4>
    <div class="container">
        <div class="row">
            <div class="col-sm-6 content">
                <form:form class="form-horizontal" id="settings-user-form"
                           action="/settings/changeNickname/submit"
                           method="post" modelAttribute="user">
                    <form:input path="id" type="hidden" class="form-control" id="user-id"/>
                    <form:input path="role" type="hidden" class="form-control" id="user-role"/>
                    <form:input path="userStatus" type="hidden" class="form-control"
                                id="user-status"/>
                    <%--nickname--%>
                    <div class="input-block-wrapper clearfix">
                        <loc:message code="register.nickname" var="login"/>
                        <div class="col-md-11 input-block-wrapper__input-wrapper">
                            <form:input id="login" path="nickname"
                                        placeholder="${login}"
                                        class="form-control input-block-wrapper__input"/>
                        </div>
                        <div class="col-md-11 input-block-wrapper__error-wrapper">
                            <form:errors path="nickname" class="input-block-wrapper__input"/>
                        </div>
                    </div>
                    <div class="confirm-button-wrapper">
                        <button class="confirm-button" type="submit"><loc:message
                                code="admin.save"/></button>
                    </div>
                </form:form>
            </div>
        </div>
    </div>
</section>