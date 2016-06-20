/**
 * Created by Valk on 05.06.2016.
 */

function RightSiderClass() {
    if (RightSiderClass.__instance) {
        return RightSiderClass.__instance;
    } else if (this === window) {
        return new RightSiderClass(currentCurrencyPair);
    }
    RightSiderClass.__instance = this;
    /**/
    var that = this;
    var rightSiderId = "right-sider";
    /**/
    this.newsList = null;
    /*===========================================================*/
    (function init () {
        that.newsList = new NewsClass();
    })();
}
