<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 20.10.2016
  Time: 17:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>To be redirected</title>
    <script src="<c:url value="/client/js/jquery_1.11.3.min.js"/>" type="text/javascript"></script>

    <script type="text/javascript">
        $(function () {
            const NO_ACTION = 'javascript:void(0);';
            const PERFECT = 2;
            const YANDEX_KASSA = 1;
            const PRIVAT24 = 9;
            const INTERKASSA = 10;

            $.fn.serializeObject = function()
            {
                var o = {};
                var a = this.serializeArray();
                $.each(a, function() {
                    if (o[this.name] !== undefined) {
                        if (!o[this.name].push) {
                            o[this.name] = [o[this.name]];
                        }
                        o[this.name].push(this.value || '');
                    } else {
                        o[this.name] = this.value || '';
                    }
                });
                return o;

            };

            function resetFormAction(merchant,form) {
                var formAction = {
                    perfectDeposit:'https://perfectmoney.is/api/step1.asp',
                    yandex_kassa:'http://din24.net/index.php?route=acc/success/order',
                    privat24:'https://api.privatbank.ua/p24api/ishop',
                    interkassa:'https://sci.interkassa.com/'
                };
                switch (Number(merchant)) {
                    case PERFECT:
                        form.attr('action', formAction.perfectDeposit);
                        break;
                    case YANDEX_KASSA:
                        form.attr('action', formAction.yandex_kassa);
                        break;
                    case PRIVAT24:
                        form.attr('action', formAction.privat24);
                        break;
                    case INTERKASSA:
                        form.attr('action', formAction.interkassa);
                        break;
                    default:
                        form.attr('action', NO_ACTION);
                }
            }
            function resetPaymentFormData(form,callback) {

                $.ajax('/api/payments/preparePostPayment', {
                    headers: {
                        'Exrates-Rest-Token': $("#auth-token").text()
                    },
                    type: 'POST',
                    contentType: 'application/json',
                    dataType: 'json',
                    data: JSON.stringify($(form).serializeObject())
                }).done(function (response) {
                    var inputsHTML = '';
                    $new_form = $("<form></form>");
                    $.each(response, function (key) {
                        $new_form.append('<input type="hidden" name="' + key + '" value="' + response[key] + '">');
                    });
                    var targetCurrentHTML = $new_form.html();
                    var targetNewHTML = targetCurrentHTML + inputsHTML;
                    $(form).html(targetNewHTML);
                    callback();
                }).fail(function (error) {
                    console.log(error);
                });
            }

            var paymentForm = $('#paymentForm');
            var targetMerchant = $('#merchant').val();
            resetFormAction(targetMerchant, paymentForm);
            resetPaymentFormData(paymentForm,function(){
                paymentForm.submit();
            });
        });



    </script>
</head>
<body>
<form id="paymentForm" method="post">
    <input type="hidden" id="currency" name="currency" value="${currency}" />
    <input type="hidden" id="sum" name="sum" value="${amount}" />
    <input type="hidden" id="merchant" name="merchant" value="${merchant}" />
</form>
<span hidden id="auth-token">${authToken}</span>
</body>
</html>
