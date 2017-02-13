<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>

<footer>
    <div class="container">
        <div class="socials-icon-wrapper ">
            <a class="socials-icon-wrapper__icon" href="https://twitter.com/exratesme" target="_blank"><img
                    src="<c:url value='/client/img/twitter.png'/>" alt="TW"/></a>
            <a class="socials-icon-wrapper__icon" href="https://www.facebook.com/exrates.me" target="_blank"><img
                    src="<c:url value='/client/img/facebook.png'/>" alt="FB"/></a>

        </div>
        <div class="row">
            <span class="footer_link"><loc:message code="dashboard.allRightsReserved"/></span>
            <span class="footer_link"><a href="<c:url value="/termsAndConditions"/>"><loc:message code="dashboard.terms"/></a></span>
            <span class="footer_link"><a href="<c:url value="/privacyPolicy"/>"><loc:message code="dashboard.privacy"/></a></span>
            <span class="footer_link"><a href="<c:url value="/contacts"/>"><loc:message code="dashboard.contactsAndSupport"/></a></span>
            <span class="footer_link"><a href="<c:url value="/aboutUs"/>"><loc:message code="dashboard.aboutUs"/></a></span>
        </div>
    </div>
</footer>