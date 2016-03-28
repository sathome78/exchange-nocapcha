+function initMenuItemsSwitcher() {
    var currentPathname = window.location.pathname;
    var className = '.side_menu-'+currentPathname.replace(/\//g,'-');
    $(className).addClass('active');
}();
