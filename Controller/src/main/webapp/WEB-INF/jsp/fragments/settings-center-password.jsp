<%--
  User: Valk
--%>
<section id="passwords-changing">
    <h4 class="h4_green">
        <loc:message code="admin.changePasswordTitle"/>
    </h4>
    <h4 class="under_h4_margin"></h4>
    <div class="container">
        <div class="row">
            <div class="col-sm-8 content">
                <div class="tab-content">
                    <%--change main passwod--%>
                        <div <%--class="tab-pane"--%> id="tab__mainpass">
                            <form:form class="form-horizontal" id="settings-user-form"
                                       action="/settings/changePassword/submit"
                                       method="post" modelAttribute="user">
                                <form:input path="id" type="hidden" class="form-control" id="user-id"/>
                                <div class="input-block-wrapper clearfix">
                                    <div class="col-md-4 input-block-wrapper__label-wrapper">
                                        <label for="user-name" class="input-block-wrapper__label">
                                            <loc:message code="admin.login"/>
                                        </label>
                                    </div>
                                    <div class="col-md-8 input-block-wrapper__input-wrapper">
                                        <form:input path="nickname" class="form-control input-block-wrapper__input"
                                                    id="user-name" readonly="true"/>
                                    </div>
                                </div>
                                <div class="input-block-wrapper clearfix">
                                    <div class="col-md-4 input-block-wrapper__label-wrapper">
                                        <label for="user-email" class="input-block-wrapper__label">
                                            <loc:message code="admin.email"/>
                                        </label>
                                    </div>
                                    <div class="col-md-8 input-block-wrapper__input-wrapper">
                                        <form:input path="email" class="form-control input-block-wrapper__input"
                                                    id="user-email" readonly="true"/>
                                        <form:errors path="email" class="form-control input-block-wrapper__input"
                                                     style="color:red"/>
                                    </div>
                                </div>
                                <loc:message code="user.settings.label.oldPassword" var="oldPassword"/>
                                <div class="input-block-wrapper clearfix">
                                    <div class="col-md-4 input-block-wrapper__label-wrapper">
                                        <label for="user-password" path="password" class="input-block-wrapper__label">
                                            ${oldPassword}
                                        </label>
                                    </div>
                                    <div class="col-md-8 input-block-wrapper__input-wrapper">
                                        <form:password path="password" class="form-control input-block-wrapper__input"
                                                       id="user-password" placeholder="${oldPassword}"/>
                                    </div>
                                    <div class="col-md-12 input-block-wrapper__error-wrapper">
                                        <form:errors path="password" class="input-block-wrapper__input"/>
                                    </div>
                                </div>
                                <loc:message code="user.settings.label.newPassword" var="newPassword"/>
                                <div class="input-block-wrapper clearfix">
                                    <div class="col-md-4 input-block-wrapper__label-wrapper">
                                        <label for="user-confirmpassword" path="confirmpassword" class="input-block-wrapper__label">
                                            ${newPassword}
                                        </label>
                                    </div>
                                    <div class="col-md-8 input-block-wrapper__input-wrapper">
                                        <form:password path="confirmPassword" class="form-control input-block-wrapper__input"
                                                       id="user-confirmpassword" placeholder="${newPassword}"/>
                                    </div>
                                    <div class="col-md-12 input-block-wrapper__error-wrapper">
                                        <form:errors path="confirmPassword" class="input-block-wrapper__input"/>
                                    </div>
                                    <div id="new_password_wrong" class='field__error' style="display:none">
                                        Password cannot be less than 8 and more than 20 characters long and should contain of letters (a-z), numbers (0-9) and/or any combination of @*%!#^!&$<> characters
                                    </div>
                                    <div id="new_password_required" class='field__error' style="display:none">
                                        Password is required
                                    </div>
                                </div>
                                <loc:message code="user.settings.label.confirmNewPassword" var="confirmNewPassword"/>
                                <div class="input-block-wrapper clearfix">
                                    <div class="col-md-4 input-block-wrapper__label-wrapper">
                                        <label class="input-block-wrapper__label">
                                            ${confirmNewPassword}
                                        </label>
                                    </div>
                                    <div class="col-md-7 input-block-wrapper__input-wrapper">
                                        <input id="confirmNewPassword" type="password" class="form-control input-block-wrapper__input" placeholder="${confirmNewPassword}" required/>
                                    </div>
                                    <span class="col-md-1 repass green"><i class="glyphicon glyphicon-ok"></i></span>
                                    <span class="col-md-1 repass-error red"><i class="glyphicon glyphicon-remove"></i></span>
                                </div>
                                <div class="confirm-button-wrapper" style="text-align: center;">
                                    <button id="change-password-button" class="btn btn-primary btn-block" disabled>
                                        <loc:message code="admin.save"/>
                                    </button>
                                </div>
                            </form:form>
                        </div>
                </div>
            </div>
        </div>

    </div>
</section>
