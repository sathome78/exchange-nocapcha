/**
 * Created by Valk on 06.06.2016.
 */

function CurrencyPairSelectorClass(currencyPairSelectorId, currentCurrencyPair) {
    var that = this;
    this.$currencyPairSelector = $('#' + currencyPairSelectorId);
    this.currentCurrencyPair = currentCurrencyPair;

    this.init = function (onChangeHandler) {
        that.$currencyPairSelector.on('click', '.currency-pair-selector__menu-item', function (e) {
            e.preventDefault();
            var $item = $(this);
            var showAllPairs;
            if (that.$currencyPairSelector.find('.currency-pair-selector__menu-item').hasClass('all-pairs-item')) {
                showAllPairs = $item.hasClass('all-pairs-item');
            } else {
                showAllPairs = null;
            }
            trading.clearOrdersCreationForm();
            trading.resetOrdersListForAccept();
            var newCurrentCurrencyPairName = showAllPairs ? null : $(this).text().trim();
            syncCurrentParams(newCurrentCurrencyPairName, null, null, showAllPairs, function (data) {
                $item.siblings().removeClass('active');
                $item.addClass('active');
                that.currentCurrencyPair = $item.text();
                setButtonTitle();
                onChangeHandler(data.currencyPair);
            });
        });
        that.getAndShowCurrencySelector();
    };

    this.syncState = function (callback) {
        syncCurrentParams(null, null, null, null, function (data) {
            var $item;
            if (data.showAllPairs && that.$currencyPairSelector.find('.currency-pair-selector__menu-item').hasClass('all-pairs-item')) {
                $item = that.$currencyPairSelector.find('.currency-pair-selector__menu-item.all-pairs-item');
            } else {
                $item = that.$currencyPairSelector.find('.currency-pair-selector__menu-item:contains(' + data.currencyPair.name + ')');
            }
            $item.siblings().removeClass('active');
            $item.addClass('active');
            that.currentCurrencyPair = $item.text();
            var pairHasChanged = setButtonTitle();
            if (callback) {
                callback(pairHasChanged);
            }
        });
    };

    this.getAndShowCurrencySelector = function () {
        var $currencyList = that.$currencyPairSelector;
        $currencyList.find('.currency-pair-selector__menu').remove();
        var $template = $('.selectors_template');
        var url = '/dashboard/createPairSelectorMenu';
        $.ajax({
            url: url,
            type: 'GET',
            success: function (data) {
                if (!data) return;
                /**/
                 var $tmpl1 = $template.html().replace(/@/g, '%');
                $currencyList.append(tmpl($tmpl1, {keys : Object.keys(data), data: Object.values(data), currentCurrencyPair: that.currentCurrencyPair}));
                setButtonTitle();
            }
        });
    };

    function setButtonTitle() {
        var $li = $(document.getElementById(that.currentCurrencyPair));
        console.log("refresh");
        var market = $li.data("market");
        var newText = $li.text();
        $('#' + market).text(newText).append('<span class="caret"></span>');

      /*  var prevTitle = that.$currencyPairSelector.find('button:first-child').text();
        that.$currencyPairSelector.find('button:first-child').text(that.currentCurrencyPair).append('<span class="caret"></span>');
      */  return /*newText != that.$currencyPairSelector.find('button:first-child').text()*/ false;
    }
}
