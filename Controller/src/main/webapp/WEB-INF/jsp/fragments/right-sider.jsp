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
    <div class="chat-locales">
        <a href="javascript:void(0)" onclick="changeChatLocale('ru')">RU</a>
        <a href="javascript:void(0)" onclick="changeChatLocale('en')">EN</a>
        <a href="javascript:void(0)" onclick="changeChatLocale('cn')">CN</a>
    </div>
    <hr class="under_h4">
    <div id="chat" class="chat">

    </div>
    <sec:authorize access="isAuthenticated()">
        <form id="new_mess" action="/chat/new-message" method="POST">
            <input type="text" name="body" class="message_text" placeholder='<loc:message code="dashboard.onlinechatenter"/>'>
            <input type="hidden" name="lang" value="EN"/>
            <button class="send_button" type="submit"><loc:message code="dashboard.onlinechatsend"/></button>
        </form>
    </sec:authorize>


    <h4 class="h4_green"><loc:message code="news.title"/></h4>
    <hr class="under_h4">
    <div class="news">
        <h5 class="news_title">Заголовок новости</h5>
        <p>Краткий текст новости в несколько строчек + ссылка <a href="#">yandex.ru</a></p>

        <h5 class="news_title">Заголовок новости в несколько строк</h5>
        <p>Краткий текст новости в несколько строчек</p>

        <h5 class="news_title">Заголовок новости в несколько строк</h5>
        <p>Краткий текст новости в несколько строчек + какая-нибудь ссылка <a href="#">google.com</a> и ещё немного текста</p>

        <h5 class="news_title">Заголовок новости</h5>
        <p>Краткий текст новости в несколько строчек</p>

        <h5 class="news_title">Заголовок новости в несколько строк</h5>
        <p>Краткий текст новости в несколько строчек + ссылка <a href="#"> yandex.ru</a></p>
    </div>
    <input type="text" class="message_text" placeholder='<loc:message code="dashboard.onlinechatenter"/>'>
    <button class="send_button"><loc:message code="dashboard.onlinechatsend"/></button>
    <%--NEWS LIST--%>
    <%@include file="right-sider-news-list.jsp" %>
</div>
<script src="//cdnjs.cloudflare.com/ajax/libs/jquery-scrollTo/2.1.0/jquery.scrollTo.min.js"></script>
