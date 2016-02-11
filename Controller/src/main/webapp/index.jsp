<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%--<!DOCTYPE html>--%>
<%--<html lang="en">--%>
<%--<head>--%>
    <%--<meta charset="utf-8">--%>
    <%--<meta http-equiv="X-UA-Compatible" content="IE=edge">--%>
    <%--<meta name="viewport" content="width=device-width, initial-scale=1.0">--%>
    <%--<meta name="description" content="">--%>
    <%--<meta name="author" content="">--%>
 <%----%>
    <%--<title>Main page</title>--%>
 <%----%>
   <%--</head>--%>
 <%----%>
<%--<body>--%>
 <%----%>
<%--<div>--%>
 <%--<%@include file='WEB-INF/jsp/header.jsp'%><br>--%>
    <%--<div style="margin-top: 20px;">--%>
        <%--<h1>Welcome!</h1>--%>
        <%--<p>--%>
            <%--It's main page!--%>
        <%--</p>--%>
        <%----%>
    <%--</div>--%>
 <%----%>
    <%--<div>--%>
        <%--<%@include file='WEB-INF/jsp/footer.jsp'%>--%>
    <%--</div>--%>
 <%----%>
<%--</div>--%>
<%--</body>--%>
<%--</html>--%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <!--[if lt IE 9]><script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
    <title></title>
    <meta name="keywords" content="" />
    <meta name="description" content="" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <link href="<c:url value="/client/css/bootstrap.css"/>" rel="stylesheet" type="text/css" />
    <link href="<c:url value="/client/css/style.css"/>" rel="stylesheet" type="text/css" />

    <script type="text/javascript" src="<c:url value="/client/js/jquery.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/dropdown.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/modal.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/function.js"/>"></script>

</head>

<body>

<div class="wrapper">

    <sec:authorize access="!isAuthenticated()">
        <%--<p><a href="<c:url value="/login" />" role="button">Login</a></p>--%>
        <%--<p><a href="<c:url value="/register" />" role="button">Registration</a></p>--%>
        <header class="header">
            <div class="container container_center">

                <!-- begin Logo block -->
                <div class="header__logo">
                    <a href="<c:url value="/"/>"><img src="<c:url value="/client/img/logo.png"/>" alt=""/></a>
                </div>
                <!-- end Logo block -->

                <!-- begin Right block -->
                <div class="header__flip">
                    <a href="<c:url value="/register" />">
                        <button  class="btn btn-danger">Регистрация</button>
                    </a>
                    <a href="<c:url value="/login" />" class="login__link">ВХОД</a>
                    <div class="dropdown lang__select">
                        <a data-toggle="dropdown" href="#">ru</a><i class="glyphicon-chevron-down"></i>
                        <ul class="dropdown-menu">
                            <li><a href="#">ru</a></li>
                            <li><a href="#">en</a></li>
                        </ul>
                    </div>

                </div>
                <!-- end Right block -->

            </div>
        </header><!-- .header-->
    </sec:authorize>
    <sec:authorize access="isAuthenticated()">
        <header class="header">
            <div class="container container_center">

                <!-- begin Logo block -->
                <div class="header__logo">
                    <a href="<c:url value="/"/>"><img src="<c:url value="/client/img/logo.png"/>" alt=""/></a>
                </div>
                <!-- end Logo block -->

                <!-- begin Right block -->
                <div class="header__flip">
                    <span style="color:#eee">Добрый день! <strong><sec:authentication property="principal.username" /></strong></span>
                    <c:url value="/logout" var="logoutUrl" />
                    <form action="${logoutUrl}" id="logoutForm" method="post">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    </form>
                    <a href="#" id="logout" class="login__link">
                        Выйти
                    </a>

                    <div class="dropdown lang__select">
                        <a data-toggle="dropdown" href="#">ru</a><i class="glyphicon-chevron-down"></i>
                        <ul class="dropdown-menu">
                            <li><a href="#">ru</a></li>
                            <li><a href="#">en</a></li>
                        </ul>
                    </div>

                </div>
                <!-- end Right block -->

            </div>
        </header><!-- .header-->
    </sec:authorize>



    <main class="main__content">

        <!-- begin order__history -->
        <section id="" class="order__history">
            <div class="container container_center">
                <div class="dropdown order__history__instrument">
                    <a data-toggle="dropdown" class="btn btn-default" href="#">BTC/USD <span class="glyphicon-chevron-down"></span></a>
                    <ul class="dropdown-menu">
                        <li><a href="#">BTC/USD</a></li>
                        <li><a href="#">BTC/USD</a></li>
                    </ul>
                </div>
                <ul class="order__history__item">
                    <li><span>Последняя сделка:</span> <span>456 USD</span></li>
                    <li><span>Цена открытия:</span> <span>450 USD</span></li>
                    <li><span>Цена закрытия:</span> <span>470 USD</span></li>
                    <li><span>Объем:</span> <span>1000 BTC</span></li>
                    <li><span>35000 USD</span></li>
                </ul>
            </div>
        </section>
        <!-- end order__history -->

        <!-- begin quotes__news__section -->
        <section id="" class="quotes__news__section">
            <div class="container container_center">

                <!-- begin quotes -->
                <div class="quotes">
                    <div class="quotes__title">КОТИРОВКИ</div>
                    <div class="row">
                        <div class="col-xs-3">
                            <ul class="quotes__list">
                                <li><a href="#">BTC/USD 404.544</a></li>
                                <li><a href="#">BTC/USD 404.544</a></li>
                                <li><a href="#">BTC/USD 404.544</a></li>
                                <li><a href="#">BTC/USD 404.544</a></li>
                            </ul>
                        </div>
                        <div class="col-xs-3">
                            <ul class="quotes__list">
                                <li><a href="#">BTC/USD 404.544</a></li>
                                <li><a href="#">BTC/USD 404.544</a></li>
                                <li><a href="#">BTC/USD 404.544</a></li>
                                <li><a href="#">BTC/USD 404.544</a></li>
                            </ul>
                        </div>
                        <div class="col-xs-3">
                            <ul class="quotes__list">
                                <li><a href="#">BTC/USD 404.544</a></li>
                                <li><a href="#">BTC/USD 404.544</a></li>
                                <li><a href="#">BTC/USD 404.544</a></li>
                                <li><a href="#">BTC/USD 404.544</a></li>
                            </ul>
                        </div>
                        <div class="col-xs-3">
                            <ul class="quotes__list">
                                <li><a href="#">BTC/USD 404.544</a></li>
                                <li><a href="#">BTC/USD 404.544</a></li>
                                <li><a href="#">BTC/USD 404.544</a></li>
                                <li><a href="#">BTC/USD 404.544</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
                <!-- end quotes -->

                <!-- begin news -->
                <div class="news">
                    <div class="news__title">НОВОСТИ</div>
                    <ul class="news__list">
                        <li>22.01 <a href="#">DRCoin бьет рекорды продаж!</a></li>
                        <li>20.01 <a href="#">Мировые цены на нефть...</a></li>
                        <li>09.01 <a href="#">Будущее за криптовалютой</a></li>
                        <li>22.01 <a href="#">DRCoin бьет рекорды продаж!</a></li>
                        <li>20.01 <a href="#">Мировые цены на нефть подняли..</a></li>
                        <li>09.01 <a href="#">Будущее за криптовалютой</a></li>
                        <li>09.01 <a href="#">Будущее за криптовалютой нового..</a></li>
                    </ul>
                </div>
                <!-- end news -->

            </div>
        </section>
        <!-- end quotes__news__section -->

        <!-- begin chart__section -->
        <section id="" class="chart__section">
            <div class="container container_center">
                <div class="chart__section__title"><strong>LTC/RUR</strong> (14 ДЕК—14 ЯНВ. 2016)</div>
                <div class="diagramm__box">
                    <img src="<c:url value="/client/img/grafik.png"/>" alt=""/>
                </div>
            </div>
        </section>
        <!-- end chart__section -->

        <!-- begin bitcoint__price__section -->
        <section id="" class="bitcoint__price__section">
            <div class="container container_center">
                <div class="row bitcoint__price">
                    <div class="col-xs-2">
                        <div class="bitcoint__price__title">ЦЕНА БИТКОИНА</div>
                        <div class="bitcoint__price__currency">356 USD</div>
                    </div>
                    <div class="col-xs-2">
                        <div class="bitcoint__price__title">ЦЕНА БИТКОИНА</div>
                        <div class="bitcoint__price__currency">327 EUR</div>
                    </div>
                    <div class="col-xs-2">
                        <div class="bitcoint__price__title">ПОЛЬЗОВАТЕЛИ</div>
                        <div class="bitcoint__price__currency">560,877</div>
                    </div>
                    <div class="col-xs-2">
                        <div class="bitcoint__price__title">ДОБЫТО БИТКОИНОВ</div>
                        <div class="bitcoint__price__currency">549,447</div>
                    </div>
                    <div class="col-xs-2">
                        <div class="bitcoint__price__title">СКОРОСТЬ ПУЛА</div>
                        <div class="bitcoint__price__currency">7.10 PH/S</div>
                    </div>
                    <div class="col-xs-2">
                        <div class="bitcoint__price__title">СКОРОСТЬ ПУЛА</div>
                        <div class="bitcoint__price__currency">7.10 PH/S</div>
                    </div>
                </div>

                <div class="bitcoint__price__headline">ЛУЧШАЯ БИРЖА ОБМЕНА ВАЛЮТ</div>
                <div class="bitcoint__price__subtitle">для торговли валютой и криптовалютой в режиме реального времени</div>

                <!-- begin bitcoint__tools -->
                <div class="row bitcoint__tools">
                    <div class="col-xs-6">
                        <div class="bitcoint__tools__item">
                            <img src="<c:url value="/client/img/money.png"/>" alt=""/>
                            <div>
                                <div class="bitcoint__tools__title">ПОКУПКА ВАЛЮТЫ</div>
                                <div class="bitcoint__tools__subtitle">Покупайте валюту за доллары США или евро с помощью платежной карты или банковского перевода.</div>
                            </div>
                        </div>
                    </div>
                    <div class="col-xs-6">
                        <div class="bitcoint__tools__item">
                            <img src="<c:url value="/client/img/rate.png"/>" alt=""/>
                            <div>
                                <div class="bitcoint__tools__title">ТОРГОВЛЯ ВАЛЮТОЙ</div>
                                <div class="bitcoint__tools__subtitle">Зарабатывайте на колебаниях цены валюты. Выводите средства в долларах США или евро.</div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- end bitcoint__tools -->

            </div>
        </section>
        <!-- end bitcoint__price__section -->

        <!-- begin video__section -->
        <section id="" class="video__section video__wrapper">
            <div class="container container_center">
                <a href="#video__about" data-toggle="modal">
                    <img class="play" src="<c:url value="/client/img/play.png"/>" alt=""/>
                </a>
            </div>
            <!-- Modal Video -->
            <div class="modal fade" id="video__about" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-lg">
                    <div class="video__wrapper">
                        <iframe width="560" height="315" src="https://www.youtube.com/embed/nNwPuaHF-jo" frameborder="0" allowfullscreen></iframe>
                    </div>
                </div>
            </div>
        </section>
        <!-- end video__section -->

        <!-- begin registr__section -->
        <section id="" class="registr__section">
            <div class="container container_center">
                <div class="registr__title">EXRATE — БИРЖА ОБМЕНА ВАЛЮТ</div>
                <div class="row registr__point">
                    <div class="col-xs-4">
                        <img src="<c:url value="/client/img/more.png"/>" alt=""/>
                        <div class="registr__description">МНОЖЕСТВО ТОРГОВЫХ ПАР</div>
                    </div>
                    <div class="col-xs-4">
                        <img src="<c:url value="/client/img/24hours.png"/>" alt=""/>
                        <div class="registr__description">ПРОФЕССИОНАЛЬНАЯ ПОДДЕРЖКА 24/7</div>
                    </div>
                    <div class="col-xs-4">
                        <img src="<c:url value="/client/img/people.png"/>" alt=""/>
                        <div class="registr__description">НАМ ДОВЕРЯЮТ БОЛЕЕ 22 МЛН.</div>
                    </div>
                </div>

                <div class="registr__btn">
                    <a href="<c:url value="/register" />">
                        <button class="btn btn-success">Зарегистрироваться</button>
                    </a>
                </div>

                <div class="registr__pay__systems">
                    <img src="<c:url value="/client/img/pay_sistems.png"/>" alt=""/>
                </div>

            </div>
        </section>
        <!-- end registr__section -->

    </main><!-- .content -->

</div><!-- .wrapper -->

<footer class="footer">
    <div class="container container_center">
        <div class="row footer__menu">
            <div class="col-xs-3">
                <div class="footer__title">КАК ЭТО РАБОТАЕТ</div>
                <ul>
                    <li class="footer__menu__item"><a class="footer__menu__link" href="#">Для начинающих</a></li>
                    <li class="footer__menu__item"><a class="footer__menu__link" href="#">Купить валюту</a></li>
                    <li class="footer__menu__item"><a class="footer__menu__link" href="#">Продать валюту</a></li>
                    <li class="footer__menu__item"><a class="footer__menu__link" href="#">Торговля валютой</a></li>
                </ul>
            </div>
            <div class="col-xs-3">
                <div class="footer__title">ДОКУМЕНТАЦИЯ</div>
                <ul>
                    <li class="footer__menu__item"><a class="footer__menu__link" href="#">FAQ</a></li>
                    <li class="footer__menu__item"><a class="footer__menu__link" href="#">Пополнение и вывод</a></li>
                    <li class="footer__menu__item"><a class="footer__menu__link" href="#">Комиссия за транзакцию</a></li>
                    <li class="footer__menu__item"><a class="footer__menu__link" href="#">Легальность и безопасность</a></li>
                </ul>
            </div>
            <div class="col-xs-3">
                <div class="footer__title">ИНСТРУМЕНТЫ</div>
                <ul>
                    <li class="footer__menu__item"><a class="footer__menu__link" href="#">Торговое API</a></li>
                    <li class="footer__menu__item"><a class="footer__menu__link" href="#">Mobile App</a></li>
                </ul>
            </div>
            <div class="col-xs-3">
                <div class="footer__title">EXRATES.ME</div>
                <ul>
                    <li class="footer__menu__item"><a class="footer__menu__link" href="#">О нас</a></li>
                    <li class="footer__menu__item"><a class="footer__menu__link" href="#">Контакты и поддержка</a></li>
                    <li class="footer__menu__item"><a class="footer__menu__link" href="#">Пресса о нас</a></li>
                </ul>
            </div>
        </div>
        <div class="row text-center footer__copyright">
            © 2016 EXRATES LTD Торговые марки принадлежат их соответствующим владельцам. Все права защищены
        </div>
    </div>
</footer><!-- .footer -->

</body>
</html>