<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<link href="https://fonts.googleapis.com/css?family=Roboto:700,600,500,400,200,100" rel="stylesheet" type="text/css">
<link href="<c:url value='/client/css/new-ieo.css'/>" rel="stylesheet">

<script type="text/javascript" src="<c:url value='/client/js/ieo.js'/>"></script>

<div class="ieo">
    <div class="ieo-half-wr">
        <div class="left-part">
            <div class="text-wr">
                <h1 class="ieo-title"><loc:message code="ieo.title"/></h1>
                <p class="ieo-description"><loc:message code="ieo.text.part1"/></p>
                <p class="ieo-description"><loc:message code="ieo.text.part2"/></p>
                <div class="participation">
                    <p class="participation-title"><loc:message code="ieo.block.buttons.title"/></p>
                    <div class="participation-item-wr">
                        <div class="participation-item">
							<span class="icon">
								<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="19" height="18" viewBox="0 0 19 18"><defs><path id="wtrja" d="M284.615 461.62a9 9 0 1 1 12.728 12.728 9 9 0 0 1-12.728-12.728z"/><path id="wtrjc" d="M284.99 465.146a1 1 0 0 1 1.414 0l4.95 4.95a1 1 0 1 1-1.415 1.414l-4.95-4.95a1 1 0 0 1 0-1.414z"/><path id="wtrjd" d="M289.94 470.096l9.192-9.192a1 1 0 0 1 1.414 1.414l-9.192 9.192a1 1 0 0 1-1.415-1.414z"/><clipPath id="wtrjb"><use fill="#fff" xlink:href="#wtrja"/></clipPath></defs><g><g transform="translate(-282 -459)"><use fill="#fff" fill-opacity="0" stroke="#d1d1d1" stroke-miterlimit="50" stroke-width="4" clip-path="url(&quot;#wtrjb&quot;)" xlink:href="#wtrja"/></g><g transform="translate(-282 -459)"><use fill="#1b5ff8" xlink:href="#wtrjc"/></g><g transform="translate(-282 -459)"><use fill="#1b5ff8" xlink:href="#wtrjd"/></g></g></svg>
							</span>
                            <p class="participation-description"><loc:message code="ieo.block.buttons.button1"/></p>
                        </div>
                        <div class="participation-item">
							<span class="icon">
								<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="19" height="18" viewBox="0 0 19 18"><defs><path id="wtrja" d="M284.615 461.62a9 9 0 1 1 12.728 12.728 9 9 0 0 1-12.728-12.728z"/><path id="wtrjc" d="M284.99 465.146a1 1 0 0 1 1.414 0l4.95 4.95a1 1 0 1 1-1.415 1.414l-4.95-4.95a1 1 0 0 1 0-1.414z"/><path id="wtrjd" d="M289.94 470.096l9.192-9.192a1 1 0 0 1 1.414 1.414l-9.192 9.192a1 1 0 0 1-1.415-1.414z"/><clipPath id="wtrjb"><use fill="#fff" xlink:href="#wtrja"/></clipPath></defs><g><g transform="translate(-282 -459)"><use fill="#fff" fill-opacity="0" stroke="#d1d1d1" stroke-miterlimit="50" stroke-width="4" clip-path="url(&quot;#wtrjb&quot;)" xlink:href="#wtrja"/></g><g transform="translate(-282 -459)"><use fill="#1b5ff8" xlink:href="#wtrjc"/></g><g transform="translate(-282 -459)"><use fill="#1b5ff8" xlink:href="#wtrjd"/></g></g></svg>
							</span>
                            <p class="participation-description"><loc:message code="ieo.block.buttons.button2"/></p>
                        </div>
                        <div class="participation-item">
							<span class="icon">
								<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="19" height="18" viewBox="0 0 19 18"><defs><path id="wtrja" d="M284.615 461.62a9 9 0 1 1 12.728 12.728 9 9 0 0 1-12.728-12.728z"/><path id="wtrjc" d="M284.99 465.146a1 1 0 0 1 1.414 0l4.95 4.95a1 1 0 1 1-1.415 1.414l-4.95-4.95a1 1 0 0 1 0-1.414z"/><path id="wtrjd" d="M289.94 470.096l9.192-9.192a1 1 0 0 1 1.414 1.414l-9.192 9.192a1 1 0 0 1-1.415-1.414z"/><clipPath id="wtrjb"><use fill="#fff" xlink:href="#wtrja"/></clipPath></defs><g><g transform="translate(-282 -459)"><use fill="#fff" fill-opacity="0" stroke="#d1d1d1" stroke-miterlimit="50" stroke-width="4" clip-path="url(&quot;#wtrjb&quot;)" xlink:href="#wtrja"/></g><g transform="translate(-282 -459)"><use fill="#1b5ff8" xlink:href="#wtrjc"/></g><g transform="translate(-282 -459)"><use fill="#1b5ff8" xlink:href="#wtrjd"/></g></g></svg>
							</span>
                            <p class="participation-description"><loc:message code="ieo.block.buttons.button3"/></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="right-part">
            <img src="/client/img/ieo.png" alt="">
        </div>
    </div>
    <div class="subscribe-form-wr">
        <div class="subscribe-title">
            <h5><loc:message code="ieo.form.subscribe.title"/></h5>
        </div>
        <form id="subscribe-form-id" action="" class="subscribe-form">
            <input type="text" placeholder="<loc:message code="ieo.form.subscribe.input"/>" id="ieo-email" name="email">
            <button class="subscribe-btn" id="subscribe-btn-id" type="button" onclick="subscribeOnInitialExchangeOfferings()" disabled="true">
                <span><loc:message code="ieo.form.subscribe.button"/></span>
            </button>
        </form>
    </div>
</div>