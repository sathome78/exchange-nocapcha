<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>

<footer>
    <div class="container">
        <%--элемент потом скрыть до наполнения функциональностью //TODO --%>
        <div hidden class="row info">
            <div class="col-sm-3">
                <%--КАК ЭТО РАБОТАЕТ--%>
                <div class="header"><h5><loc:message code="dashboard.howItWork"/></h5></div>
                <div class="options">
                    <ul>
                        <%--Для начинающих
                        Купить валюту
                        Продать валюту
                        Торговля валютой--%>
                        <li><a href="#"><loc:message code="dashboard.forBeginners"/></a></li>
                        <li><a href="#"><loc:message code="dashboard.buyCurrency"/></a></li>
                        <li><a href="#"><loc:message code="dashboard.sellCurrency"/></a></li>
                        <li><a href="#"><loc:message code="dashboard.tradeByCurrency"/></a></li>
                    </ul>
                </div>
            </div>

            <div class="col-sm-3">
                <%--ДОКУМЕНТАЦИЯ--%>
                <div class="header"><h5><loc:message code="dashboard.documentation"/></h5></div>
                <div class="options">
                    <ul>
                        <li><a href="#"><loc:message code="dashboard.FAQ"/></a></li>
                        <li><a href="#"><loc:message code="dashboard.depositAndWithdraw"/></a></li>
                        <li><a href="#"><loc:message code="dashboard.feeForTransaction"/></a></li>
                        <li><a href="#"><loc:message code="dashboard.legalityAndSecurity"/>ь</a></li>
                        <li></li>
                    </ul>
                </div>

            </div>
            <div class="col-sm-3">
                <%--ИНСТРУМЕНТЫ--%>
                <div class="header"><h5><loc:message code="dashboard.tools"/></h5></div>
                <div class="options">
                    <ul>
                        <li><a href="#"><loc:message code="dashboard.tradeAPI"/></a></li>
                        <li><a href="#"><loc:message code="dashboard.mobileApp"/></a></li>
                    </ul>
                </div>
            </div>

            <div class="col-sm-3">
                <%--EXRATES.ME--%>
                <div class="header"><h5><loc:message code="dashboard.ourWeb"/></h5></div>
                <div class="options">
                    <ul>
                        <li><a href="#"><loc:message code="dashboard.aboutUs"/></a></li>
                        <li><a href="#"><loc:message code="dashboard.contactsAndSupport"/></a></li>
                        <li><a href="#"><loc:message code="dashboard.PressAboutUs"/></a></li>
                        <li></li>
                    </ul>
                </div>
            </div>
        </div>
        <br/>

        <div class="socials-icon-wrapper ">
            <a class="socials-icon-wrapper__icon" href="https://twitter.com/exratesme" target="_blank"><img
                    src="<c:url value='/client/img/twitter.png'/>" alt="TW"/></a>
            <a class="socials-icon-wrapper__icon" href="https://www.facebook.com/exrates.me" target="_blank"><img
                    src="<c:url value='/client/img/facebook.png'/>" alt="FB"/></a>
            <a class="socials-icon-wrapper__icon" href="https://edrcoin.com/ " target="_blank"><img
                    src="<c:url value='/client/img/edrcoin.png'/>" alt="FB"/></a>
            <a class="socials-icon-wrapper__icon" href="https://blockchain.mn/ " target="_blank"><img
                    src="<c:url value='/client/img/blockchain.png'/>" alt="FB"/></a>
        </div>
        <p><loc:message code="dashboard.allRightsReserved"/></p>
    </div>
</footer>