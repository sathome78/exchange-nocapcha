/**
 * Created by Valk on 18.06.2016.
 */
var leftSider;
var rightSider;

$(function newsInit() {
    try {
        /*FOR LEFT-SIDER ...*/
        leftSider = new LeftSiderClass();
        $('#currency_table_wrapper').mCustomScrollbar({
            theme: "dark",
            axis: "yx",
            live: true

        });
        /*...FOR LEFT-SIDER*/
        $('#news_table_wrapper').mCustomScrollbar({
            theme: "dark",
            axis: "yx",
            live: true
        });
        /*FOR RIGHT-SIDER ...*/
        rightSider = new RightSiderClass();
        /*...FOR RIGHT-SIDER*/
    } catch (e) {
        /*it's need for ignoring error from old interface*/
    }
});
