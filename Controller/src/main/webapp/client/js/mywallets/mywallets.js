/**
 * Created by Valk on 04.06.2016.
 */

function MyWalletsClass() {
    if (MyWalletsClass.__instance) {
        return MyWalletsClass.__instance;
    } else if (this === window) {
        return new MyWalletsClass();
    }
    MyWalletsClass.__instance = this;
    /**/
    var that = this;
    /**/
    var timeOutIdForMyWalletsData;
    var refreshIntervalForMyWalletsData = 5000 * REFRESH_INTERVAL_MULTIPLIER;
    /**/
    var showLog = false;
    /**/
    var $balanceContainer = $('#balance-page');

    this.getAndShowMyWalletsData = function (refreshIfNeeded) {
        if ($balanceContainer.hasClass('hidden') || !windowIsActive) {
            clearTimeout(timeOutIdForMyWalletsData);
            timeOutIdForMyWalletsData = setTimeout(function () {
                that.getAndShowMyWalletsData(true);
            }, refreshIntervalForMyWalletsData);
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowMyWalletsData');
        }
        var $balanceTable = $('#balance-table').find('tbody');
        var url = '/dashboard/myWalletsData' + '?refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            headers: {
                "windowid": windowId
            },
            success: function (data) {
                if (!data) return;
                if (data.length == 0 || data[0].needRefresh) {
                    hideConfirmationDetailTooltip();
                    var $tmpl = $('#balance-table_row').html().replace(/@/g, '%');
                    $balanceTable.find('tr').has('td').remove();
                    data.forEach(function (e) {
                        $balanceTable.append(tmpl($tmpl, e));
                    });
                    blink($balanceTable.find('td:not(:first-child)'));
                }
                clearTimeout(timeOutIdForMyWalletsData);
                timeOutIdForMyWalletsData = setTimeout(function () {
                    that.getAndShowMyWalletsData(true);
                }, refreshIntervalForMyWalletsData);
            }
        });
    };
    /*=====================================================*/
    (function init() {
        that.getAndShowMyWalletsData();
        $('#balance-table').on('mouseleave', function (e) {
            hideConfirmationDetailTooltip();
        });

        $('#balance-table').on('click', '.mywallet-item-detail', function (e) {
            hideConfirmationDetailTooltip();
            var walletId = $(this).data('walletid');
            getMyWalletConfirmationDetail(walletId, $(this));
        })
    })();

    function getMyWalletConfirmationDetail(walletId, $detailButton) {
        var url = '/dashboard/myWalletsConfirmationDetail?walletId=' + walletId;
        $.ajax({
            url: url,
            type: 'GET',
            success: function (data) {
                var $tooltip = getConfirmationDetailTooltip(data);
                $tooltip.css({
                    left: $detailButton.offset().left + parseInt($detailButton.css('width')),
                    top: $detailButton.offset().top + parseInt($detailButton.css('height'))
                });
                $('html').append($tooltip);
            }
        });
    }

    function getConfirmationDetailTooltip(data) {
        var html = '<div id="mywallet-detail-tooltip" class="mywallet-detail-tooltip"  style="position: absolute; z-index: 1">';
        data.forEach(function (e) {
            html += '<div>' + e.total + ' (' + e.stage + '/4)' + '</div>';
        });
        html += '</div>';
        return $(html)
            .on('click', function () {
                $(this).remove();
            });
    }

    function hideConfirmationDetailTooltip(){
        $('#mywallet-detail-tooltip').remove();
    }
}