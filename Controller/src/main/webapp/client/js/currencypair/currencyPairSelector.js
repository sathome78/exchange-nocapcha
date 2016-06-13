/**
 * Created by Valk on 06.06.2016.
 */

function CurrencyPairSelectorClass(currencyPairSelectorId, currentCurrencyPair) {
    var that = this;
    this.$currencyPairSelector = $('#' + currencyPairSelectorId);
    this.currentCurrencyPair = currentCurrencyPair;

    this.init = function (onChangeHandler) {
        that.$currencyPairSelector.on('click', '.currency-pair-selector__menu-item', function (e) {
            var newCurrentCurrencyPairName = $(this).text().trim();
            var $item = $(this);
            syncCurrentParams(newCurrentCurrencyPairName, null, null, function(data){
                $item.siblings().removeClass('active');
                $item.addClass('active');
                that.currentCurrencyPair = $item.text();
                setButtonTitle();
                onChangeHandler(data.currencyPair);
            });
        });
        that.getAndShowCurrencySelector();
    };

    this.syncState = function(){
        syncCurrentParams(null, null, null, function(data){
            var $item = that.$currencyPairSelector.find('.currency-pair-selector__menu-item:contains('+data.currencyPair.name+')');
            $item.siblings().removeClass('active');
            $item.addClass('active');
            that.currentCurrencyPair = $item.text();
            setButtonTitle();
        });
    };

    this.getAndShowCurrencySelector = function () {
        var $currencyList = that.$currencyPairSelector;
        $currencyList.find('.currency-pair-selector__menu').remove();
        var url = '/dashboard/createPairSelectorMenu';
        $.ajax({
            url: url,
            type: 'GET',
            success: function (data) {
                if (!data) return;
                var $tmpl = that.$currencyPairSelector.find('.currency-pair-selector_row').html().replace(/@/g, '%');
                $currencyList.append(tmpl($tmpl, {data: data, currentCurrencyPair: that.currentCurrencyPair}));
                /**/
                setButtonTitle();
            }
        });
    };

    function setButtonTitle(){
        that.$currencyPairSelector.find('button:first-child').text(that.currentCurrencyPair).append('<span class="caret"></span>');
    }
}
