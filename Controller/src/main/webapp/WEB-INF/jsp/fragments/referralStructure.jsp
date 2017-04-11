

<div class="form-group" style="text-align: right">
    <input id="refSearch" type="search" name="email" placeholder='E-mail'>
    <button id="refSearchButton" disabled class="blue-box"><loc:message code="currency.search"/></button>
    <button id="refSearchClearButton" class="blue-box"><loc:message code="admin.reset"/></button>
</div>
<div hidden id="level-outer"><loc:message code="admin.referralLevel" />: <span id="level"></span></div>
<div id="ref" class="referral-table">
    <div class="table-wrp">
        <div class="table-body-wrp">
            <table class="table">
                <th class="user_name"><loc:message code="login.email"/></th>
                <th class="user-bonus"><loc:message code="btcWallet.history.amount"/></th>
                <th class="user-bonus"><loc:message code="admin.referrals"/></th>
            </table >
            <div class="wrap-rows reffil_${user.id}">

            </div>
        </div>
    </div>
</div>
<ul id="pagination-demo" class="pagination-sm"></ul>


<script id="refTemplate" type="text/html">
    <div class="row_aff" id="reff_{%= refId %}" style="color:#333333; padding-left:10px;">
        <div class="reffil_">
            <div class="column-left">{%= email %}
                {%if firstRefLevelCount>0 %}
                    <span id="span_{%= id %}" class="fa-stack ref-Show" style="cursor:pointer; font-size: 10px;"  onclick="ShowHide({%= refId %})" >
                        <i class="fa fa-info fa-stack-1x"></i><i class="fa fa-circle-thin fa-stack-2x"></i>
                    </span>
                {%/if%}
            </div>
            <div class="ref_center column-center">
                {%if refProfitFromUser == 0  %}
                     0
                {%else%}
                {%each(i, dto) referralProfitDtoList %}
                      <span>{%= dto.amount %} {%= dto.currencyName %} </span><br>
                {%/each%}
                {%/if%}
            </div>
            <div class="ref_center column-right">
                <span class="badge badge-clear">{%= firstRefLevelCount %}</span>
            </div>
        </div>
        <div hidden="" class="reffil_{%= refId %}">
            <div id="test" class="list-view">
                <ul></ul>
            </div>
        </div>
    </div>
</script>
