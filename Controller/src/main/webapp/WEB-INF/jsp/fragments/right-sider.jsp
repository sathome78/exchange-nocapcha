<%--
  User: Valk
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%----%>
<script type="text/javascript" src="<c:url value='/client/js/news/news.js'/>"></script>
<%----%>
<div id="right-sider" class="cols-md-2">
    <%--CHAT TODO REMOVE TO SEPARATE jsp--%>
    <h4 class="h4_green"><loc:message code="dashboard.onlinechat"/></h4>
    <hr class="under_h4">
    <div id="chat" class="chat">
        <p class="nickname">Никнейм_Пользователя</p>

        <p class="message">Какое-нибудь сообщение, например - Всем привет!</p>

        <p class="nickname nickname_active">Ваше_имя_в_чате</p>

        <p class="message">И тебе привет</p>

        <p class="nickname">Никнейм_Пользователя</p>

        <p class="message">Как дела?</p>

        <p class="nickname">Никнейм_2</p>

        <p class="message">продайте yacoin кто-нибудь по 116</p>

        <p class="nickname">Никнейм_3</p>

        <p class="message">Какое-нибудь сообщение, например - Всем привет!</p>

        <p class="nickname">Никнейм_4</p>

        <p class="message">Какое-нибудь сообщение, например - Всем привет!</p>

        <p class="nickname">Никнейм_5</p>

        <p class="message">Какое-нибудь сообщение, например - Всем привет!</p>

        <p class="nickname">Никнейм_6</p>

        <p class="message">Какое-нибудь сообщение, например - Всем привет!</p>
    </div>
    <input type="text" class="message_text" placeholder='<loc:message code="dashboard.onlinechatenter"/>'>
    <button class="send_button"><loc:message code="dashboard.onlinechatsend"/></button>
    <%--NEWS LIST--%>
    <%@include file="right-sider-news-list.jsp" %>
</div>
