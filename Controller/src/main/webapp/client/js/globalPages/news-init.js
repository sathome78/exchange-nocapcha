/**
 * Created by Valk on 18.06.2016.
 */
var leftSider;
var rightSider;
/**/
/*it's need to distinguish different windows (tab) of the browser*/
var windowId = Math.floor((Math.random()) * 10000).toString(36) + Math.floor((Math.random()) * 10000).toString(36);
/*it's need to prevent ajax request if window (tab) is not active*/
var windowIsActive = true;
/*for testing*/
var REFRESH_INTERVAL_MULTIPLIER = 1;

$(function newsInit() {
    try {
        /*FOR LEFT-SIDER ...*/
        leftSider = new LeftSiderClass();
        /*...FOR LEFT-SIDER*/

        /*FOR RIGHT-SIDER ...*/
        rightSider = new RightSiderClass();
        /*...FOR RIGHT-SIDER*/
    } catch (e) {
        /*it's need for ignoring error from old interface*/
    }
});
