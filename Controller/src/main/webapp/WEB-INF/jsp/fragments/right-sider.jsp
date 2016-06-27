<%--
  User: Valk
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%----%>
<script type="text/javascript" src="<c:url value='/client/js/news/news.js'/>"></script>
<%----%>
<div id="right-sider" class="cols-md-2">
    <%--CHAT TODO REMOVE TO SEPARATE jsp--%>

    <div>
        <h4 class="h4_green"><loc:message code="dashboard.onlinechat"/></h4>
        <div class="chat-locales">
            <a href="javascript:void(0)" onclick="changeChatLocale('en')">EN</a>
            <a href="javascript:void(0)" onclick="changeChatLocale('ru')">RU</a>
            <a href="javascript:void(0)" onclick="changeChatLocale('cn')">CN</a>
        </div>
    </div>

    <hr class="under_h4">
    <div id="chat" class="chat">
    </div>
    <sec:authorize access="isAuthenticated()">
        <form id="new_mess" method="POST">
            <input type="text" name="body" class="message_text" placeholder='<loc:message code="dashboard.onlinechatenter"/>'>
            <input type="hidden" name="lang" value="EN"/>
            <button class="send_button" type="submit"><loc:message code="dashboard.onlinechatsend"/></button>
        </form>
    </sec:authorize>

    <%--NEWS LIST--%>
    <%@include file="right-sider-news-list.jsp" %>
</div>
<script src="//cdnjs.cloudflare.com/ajax/libs/jquery-scrollTo/2.1.0/jquery.scrollTo.min.js"></script>
