<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title><loc:message code="dashboard.aboutUs"/></title>

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='https://fonts.googleapis.com/css?family=Roboto:400,700,300' rel='stylesheet' type='text/css'>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style-new.css'/>" rel="stylesheet">

    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/menuSwitcher.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/notyInit.js'/>"></script>

    <script src="/client/js/news/addNewsVariant.js" type="text/javascript"></script>

</head>


<body>

<%@include file='../header_new.jsp' %>

<style type="text/css">
    .news__item {
        background: rgba(255, 255, 255, 0.2);
        padding: 10px 0;
        margin: 0 0 15px 0;
    }

    .news__title, .news__title:hover {
        font-size: 22px;
        text-decoration: none;
        color: white;
        line-height: 16px;
    }

    .news__brief {
        font-size: 14px;
        color: #fdeeff;
        margin: 0 0 10px 0;
    }

    .news__date,
    .news_more,
    .news_more:hover {
        display: inline-block;
        background: #602422;
        padding: 5px 20px;
        margin: 0 10px 0 0;
        text-decoration: none;
        color: white;
    }

    .news__admin-section {
        background: rgba(255, 252, 28, 0.20);
        color: white;
        margin: 10px 0 0 0;
        padding: 3px 0;
        text-align: center;
    }

    .news__admin-section-button {
        float: right;
        background: #602422;
        padding: 3px 20px;
        margin: 0 10px 0 0;
        border: none;
    }


</style>

<main class="container register">
    <c:set var="adminEnum" value="<%=me.exrates.model.enums.UserRole.ADMINISTRATOR%>"/>
    <c:set var="accountantEnum" value="<%=me.exrates.model.enums.UserRole.ACCOUNTANT%>"/>
    <c:set var="admin_userEnum" value="<%=me.exrates.model.enums.UserRole.ADMIN_USER%>"/>
    <div class="row">
        <div>
            <h4><loc:message code="dashboard.news"/></h4>
            <br/>
            <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                <div class="news__admin-section clearfix">
                    Эта панель видна только админу
                    <button class="news__admin-section-button" onclick="addNews()">Добавить новость</button>
                </div>
            </sec:authorize>
            <br/>
            <c:forEach var="news" items="${newsList}">
                <div class="news__item">
                    <div class="clearfix">
                        <div class="news__body-wrapper col-sm-8">
                            <a class="news__title"
                               href="/news/${news.resource}${news.id}/newstopic.html">${news.title}</a>

                            <div class="news__brief">${news.brief}</div>
                            <div class="news__date">${news.date}</div>
                            <a class="news_more" href="/news/${news.resource}${news.id}/newstopic.html"><loc:message
                                    code="news.more"/></a>
                        </div>
                        <div class="news__img-wrapper col-sm-4">
                            <img src="/client/img/more.png" alt=""/>
                        </div>
                    </div>
                    <sec:authorize access="hasAnyAuthority('${adminEnum}', '${accountantEnum}', '${admin_userEnum}')">
                        <div class="news__admin-section clearfix">
                            Эта панель видна только админу
                            <button class="news__admin-section-button" onclick="deleteNews(${news.id})">Удалить</button>
                            <button class="news__admin-section-button"
                                    onclick="addNewsVariant(${news.id}, '${news.resource}')">Добавить версию
                            </button>
                        </div>
                    </sec:authorize>
                </div>
            </c:forEach>
            <input id="news_resource" hidden value="${news.resource}"/>
        </div>
    </div>
</main>
<%@include file='../footer_new.jsp' %>
<%@include file='news_add.jsp' %>

<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>

</body>
</html>

