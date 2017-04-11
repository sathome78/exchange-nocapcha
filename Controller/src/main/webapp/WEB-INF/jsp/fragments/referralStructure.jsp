

<div class="form-group" style="text-align: right">
    <input id="refSearch" type="search" name="email" placeholder='E-mail'>
    <button id="refSearchButton" class="blue-box">Search</button>
    <button id="refSearchClearButton" class="blue-box">Clear</button>
</div>
<div id="ref" class="referral-table">
    <div class="table-wrp">
        <div class="table-body-wrp">
            <table class="table">
                <th class="user_name"><loc:message code="login.email" /></th>
                <th class="user-bonus"><loc:message code="btcWallet.amount" /></th>
                <th class="user-bonus"><loc:message code="admin.referral" /></th>
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
                    <div class="ref_center column-center">{%= refProfitFromUser %}</div>
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

<style>
    .column-left{ float: left; width: 33%; }
    .column-right{ float: right; width: 33%; }
    .column-center{ display: inline-block; width: 33%; }
    .ref_center {
        text-align: center;
    }

    .referral-table .table {
        width: 100%;
        max-width: 100%;
        margin-bottom: 0;
        font-size: 14px;
        font-family: 'Arimo', sans-serif;
        color: #333333;
    }
    .referral-table .table tbody th {
        text-transform: uppercase;
        background: #f2f3f5;
        border-width: 0;
        vertical-align: bottom;
    }
    .referral-table .table tbody th:nth-child(1) {
        padding-left: 10px;
    }
    .referral-table .table tbody th:nth-child(2) {
        text-align: center;
    }
    .referral-table .table tbody th:nth-child(3) {
        text-align: center;
    }
    .referral-table .angle {
        content: '';
        display: inline-block;
        position: relative;
        top: -2px;
        width: 8px;
        height: 8px;
        border: 1px solid #dfe1e5;
        border-right-width: 0;
        border-top-width: 0;
        margin-right: .5rem;
    }

    .referral-table .user_name {
        width: 23%;
    }
    .referral-table .user-bonus {
        width: 22%;
    }
    .reffil_ {
        display: flex;
        font-size: 14px;
        padding: 10px 10px;
    }
    .referral-table .list-view ul {
        padding: 0;
    }

    .referral-table .wrap-rows:nth-child(odd) {
        background: #fafafb;
    }
    .referral-table .badge {
        font-size: 14px;
        min-width: 30px;
        padding: 5px;
    }
    .referral-table .reff_button {
        text-align: right;
        padding-right: 32px;
    }
    .referral-table .pagination-wrp {
        margin-top: 20px;
    }

</style>