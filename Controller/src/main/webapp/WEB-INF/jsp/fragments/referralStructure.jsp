<button onclick="ShowHide(${user.id})"></button>
<div hidden="" class="reffil_${user.id}">
    <div  class="list-view">
        <ul></ul>
    </div>
</div>



<script id="refTemplate" type="text/html">
    <div class="row_aff" id="reff_{%= refId %}" style="color:#333333; padding-left:10px;">
        <div class="reffil_">
            <div class="user_name" style="padding-left:0px;">
                <span class="angle"></span>{%= email %}
            {%if firstRefLevelCount>0 %}
            <span id="span_{%= id %}" class="fa-stack" style="cursor:pointer; font-size: 10px;"  onclick="ShowHide({%= refId %})" >
                    <i class="fa fa-info fa-stack-1x"></i><i class="fa fa-circle-thin fa-stack-2x"></i>
                </span>
            {%/if%}
            </div>
            <div class="user-bonus">{%= refProfitFromUser %}</div>
            <div class="chield-butt"> <div class="reff_button">
                <span class="badge badge-clear">{%= firstRefLevelCount %}</span>
            </div>
            </div>
        </div>
    </div>
    <div hidden="" class="reffil_{%= refId %}">
        <div id="test" class="list-view"><ul></ul>
        </div>
    </div>
</script>