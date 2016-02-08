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
                <paymentForm:select path="currency">
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
                    <paymentForm:option value="ymoney">
                        <loc:message code="merchants.yandexmoney"/>
                    </paymentForm:option>
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
                <paymentForm:input path="sum"/>
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