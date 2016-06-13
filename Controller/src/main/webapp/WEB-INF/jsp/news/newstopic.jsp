<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>

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

</head>


<body>

<%@include file='../header_new.jsp' %>

<style type="text/css">
    .newstopic__title, .newstopic__title:hover {
        font-size: 22px;
        text-decoration: none;
        color: white;
        line-height: 16px;
    }

    .newstopic__content {
        background: rgba(255, 255, 255, 0.2);
        padding: 10px 0;
    }

    .newstopic__date {
        float: right;
        background: #602422;
        padding: 5px 20px;
        margin: 0 10px 0 0;
        text-decoration: none;
        color: white;
    }
</style>

<main class="container register">
    <div class="row">
        <div>
            <h4><loc:message code="dashboard.news"/></h4>

            <div class=" newstopic">
                <div class="news">
                    <div class="newstopic__title">${newstopic.title}</div>
                    <div class="newstopic__date">${news.date}</div>
                    <div class="newstopic__content">${news.content}</div>
                </div>
            </div>
        </div>
    </div>
</main>
<%@include file='../fragments/footer.jsp' %>

<script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/menuSwitcher.js'/>"></script>

</body>
</html>

