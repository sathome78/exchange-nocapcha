<%--
  Created by IntelliJ IDEA.
  User: Valk
  Date: 01.06.2016
  Time: 12:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="cols-md-2">
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
</div>
