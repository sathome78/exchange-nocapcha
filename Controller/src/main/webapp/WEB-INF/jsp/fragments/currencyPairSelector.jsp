<%--
  User: Valk
--%>
<button id="dLabel" class='currency-pair-selector__button blue-box' type="button" data-toggle="dropdown"
        aria-haspopup="true"
        aria-expanded="false">
    <loc:message code="currency.pairs"/>
    <span class="caret"></span>
</button>

<script type="text/template" class="currency-pair-selector_row">
    <ul class="dropdown-menu currency-pair-selector__menu" aria-labelledby="dLabel">
        <@for(var i=0; i<data.length; i++){@>
        <li class="currency-pair-selector__menu-item <@=data[i]===currentCurrencyPair ? 'active':''@>">
            <a href="#"><@=data[i]@></a>
        </li>
        <@}@>
    </ul>
</script>

