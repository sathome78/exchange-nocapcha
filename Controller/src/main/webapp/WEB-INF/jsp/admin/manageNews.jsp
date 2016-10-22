<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 22.10.2016
  Time: 16:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Manage news</title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript">
        $(function () {
            $.ajax("/news/findAllNewsVariants", {
                type: 'GET',
                success: function (data) {
                    console.log(data);
                },
                error: function (err) {
                    console.log(err);
                }
            })
        })
    </script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>

<main class="container orders_new admin side_menu">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
    </div>
</main>

</body>
</html>
