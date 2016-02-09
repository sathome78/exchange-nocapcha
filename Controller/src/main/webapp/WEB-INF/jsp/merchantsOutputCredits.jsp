<%@page language="java"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="paymentForm"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title><loc:message code="merchants.merchants"/></title>
    <style type="text/css">
        .error {
            color: #ff0000;
        }

        .errorblock {
            color: #000;
            background-color: #ffEEEE;
            border: 3px solid #ff0000;
            padding: 8px;
            margin: 16px;
        }
    </style>
    <script src="<c:url value="/client/js/jquery.js"/>"></script>
    <script src="<c:url value="/client/js/main.js"/>"></script>

</head>
<body>
<%--@elvariable id="_csrf" type="org.springframework.security.web.csrf.CsrfAuthenticationStrategy.SaveOnAccessCsrfToken"--%>
<%--@elvariable id="currencies" type="java.util.List"--%>
<%@include file='header.jsp'%><br>
<c:url value="/merchants/yandexmoney/payment/prepare" var="url"/>
<paymentForm:form action="${url}" method="post" modelAttribute="payment"  acceptCharset="UTF-8">

    <paymentForm:errors path="*" cssClass="errorblock" element="div" />
    <table>
        <tr>
            <td>
                <loc:message code="merchants.currency"/>
            </td>
            <td>
                <paymentForm:select path="currency" onchange="loadMeansOfPayment()">
                    <paymentForm:options items="${currencies}" itemLabel="name" itemValue="id" />
                </paymentForm:select>
            </td>
            <td>
                <paymentForm:errors path="currency" cssClass="error"/>
            </td>
        </tr>
        <tr>
            <td>
                <loc:message code="merchants.meansOfPayment"/>
            </td>
            <td>
                <paymentForm:select path="meansOfPayment">

                </paymentForm:select>
            </td>
            <td>
                <paymentForm:errors path="meansOfPayment"/>
            </td>
        </tr>
        <tr>
            <td>
                <loc:message code="merchants.sum"/>
            </td>
            <td>
                <paymentForm:input id="abc" path="sum"/>
            </td>
            <td>
                <paymentForm:errors path="sum"/>
            </td>
        </tr>
        <tr>
            <td>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <input type="submit">
            </td>
        </tr>
    </table>
</paymentForm:form>
</body>
</html>








<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <!--[if lt IE 9]><script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
    <title></title>
    <meta name="keywords" content="" />
    <meta name="description" content="" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <link href="<c:url value="/client/css/bootstrap.css"/>" rel="stylesheet" type="text/css" />
    <link href="<c:url value="/client/css/chosen.css"/>" rel="stylesheet" type="text/css" />
    <link href="<c:url value="/client/css/style.css"/>" rel="stylesheet" type="text/css" />

    <script type="text/javascript" src="<c:url value="/client/js/jquery.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/dropdown.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/modal.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/chosen.jquery.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/function.js"/>"></script>

</head>

<body>

<div class="wrapper lk">


    <div class="container container_center full__height">

        <!--#include file="sidebar__lk.shtml" -->

        <div class="main__content">

            <!--#include file="header__lk.shtml" -->

            <div class="content__page">

                <div class="title__page">Ввод средств</div>

                <!-- Start  withdraw__money -->
                <form class="form-horizontal withdraw__money" id="" action="/">
                    <div class="form-group">
                        <label class="col-sm-3 control-label" for="#">Валюта к выводу</label>
                        <div class="col-sm-8">
                            <select name="" id="" class="select form-control">
                                <option value="">Item1</option>
                                <option value="">Item2</option>
                                <option value="">Item3</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label" for="#">Платежная система</label>
                        <div class="col-sm-8">
                            <select name="" id="" class="select form-control">
                                <option value="">Item1</option>
                                <option value="">Item2</option>
                                <option value="">Item3</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-3 col-sm-8">
                            <input class="form-control" type="text" id="#" placeholder="Количество">
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-3 col-sm-6">
                            <button type="button" class="btn btn-primary">Пополнить</button>
                        </div>
                    </div>
                </form>
                <!-- End  withdraw__money -->

                <div class="add__money col-sm-offset-3 col-sm-8">
                    <div class="add__money__title">Ввод средств</div>
                    <div class="add__money__description">
                        Вы вводите 100 USD через платежную систему QIWI. Комиссия биржи составит — 1 USD.
                    </div>
                    <div class="add__money__btns">
                        <button type="button" class="btn btn-primary">Продолжить</button>
                        <button type="button" class="btn btn-warning">Отменить</button>
                    </div>
                </div>

            </div>

            <!--#include file="footer__lk.shtml" -->

        </div>

    </div>

</div>


</body>
</html>