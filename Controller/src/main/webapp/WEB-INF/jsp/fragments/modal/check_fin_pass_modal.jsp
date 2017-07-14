<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 18.01.2017
  Time: 11:02
  To change this template use File | Settings | File Templates.
--%>
<div class="modal fade" id="finPassModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel"><loc:message code="admin.finPassword"/></h4>
      </div>
      <div class="modal-body modal-content__input-block-wrapper">
        <div class="content modal-content__content-wrapper">
          <form id="submitFinPassForm">
            <%--логин--%>
            <sec:authentication
                    property="principal.username" var="username"/>
            <input type="text" readonly name="email" value="${username}"/>
            <%--пароль--%>
            <loc:message
                    code="admin.finPassword" var="finpassPlaceholder"/>
            <input id="finpassword" type="password" name="finpassword" autofocus placeholder="${finpassPlaceholder}"/>
            <%--csrf--%>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <br/>
            <%--отправить--%>
            <button id="check-fin-password-button" type="button" class="button_enter">
              <loc:message code="admin.submitfinpassword"/></button>
            <%--Забыли пароль?--%>
            <a style="display:none" class="button_forgot" href="/forgotPassword"><loc:message
                    code="dashboard.forgotPassword"/></a>
          </form>

        </div>
      </div>
    </div>
  </div>
</div>

