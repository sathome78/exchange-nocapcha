/**
 * Created by Valk on 20.06.2016.
 */
function SettingsClass() {
    if (SettingsClass.__instance) {
        return SettingsClass.__instance;
    } else if (this === window) {
        return new SettingsClass(currentCurrencyPair);
    }
    SettingsClass.__instance = this;
    /**/
    var that = this;
    /**/
    var showLog = false;
    /**/
    this.tabIdx = 0;

    /*===========================================================*/
    (function init() {
        that.tabIdx = $('#tabIdx').text();
        if (!that.tabIdx) {
            that.tabIdx = 0;
        }
        setActiveSwitcher();
        switchPassTab();
        /**/
        $('.orderForm-toggler').on('click', function(e){
            that.tabIdx = $(this).index();
            setActiveSwitcher();
            switchPassTab();
        });
    })();

    function setActiveSwitcher(){
        $('.orderForm-toggler').removeClass('active');
        $('.orderForm-toggler:eq('+that.tabIdx+')').addClass('active');
    }

    function switchPassTab(){
        var tabId = $('.orderForm-toggler.active').data('tabid');
        $('#'+tabId).siblings().removeClass('active');
        $('#'+tabId).addClass('active');
        blink($('#passwords-changing').find('[for="user-password"]'));
        blink($('#passwords-changing').find('[for="userFin-password"]'));
    }

}