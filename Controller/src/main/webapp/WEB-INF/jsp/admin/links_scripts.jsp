<%--
  Created by IntelliJ IDEA.
  User: ogolv
  Date: 27.07.2016
  Time: 13:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>

<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link href='https://fonts.googleapis.com/css?family=Roboto:400,700,300' rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
<link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
<link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
<link href="<c:url value='/client/css/style.css'/>" rel="stylesheet">
<link rel="stylesheet" type="text/css" href="//cdn.datatables.net/1.10.12/css/jquery.dataTables.css">

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
<script src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>" type="text/javascript"></script>

<%--<link href="<c:url value='/client/css/jquery.dataTables.min.css'/>" rel="stylesheet">

<script type="text/javascript" src="<c:url value='/client/js/jquery.dataTables.min.js'/>"></script>--%>

<script type="text/javascript" src="<c:url value='/client/js/function.js'/>"></script>


<script type="text/javascript" charset="utf8" src="//cdn.datatables.net/1.10.12/js/jquery.dataTables.js"></script>


<%----------%>
<script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/menuSwitcher.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/notyInit.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/submits/invoiceSubmitAccept.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/dataTable/adminInvoiceDataTable.js'/>"></script>
<%----------%>

<script type="text/javascript" src="<c:url value='/client/js/dataTable/adminUsersDataTable.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/dataTable/adminAdminsDataTable.js'/>"></script>
<%----------%>
<script type="text/javascript" src="<c:url value='/client/js/order/adminDeleteOrder.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/changeRefSystemOptions.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/loc-direction.js'/>"></script>
<%@include file="../tools/alexa.jsp" %>
<%@include file="../tools/yandex.jsp" %>