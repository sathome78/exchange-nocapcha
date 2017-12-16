/**
 * Created by OLEG on 11.11.2016.
 */
function NotificationsClass() {
    if (NotificationsClass.__instance) {
        return NotificationsClass.__instance;
    } else if (this === window) {
        return new NotificationsClass();
    }
    NotificationsClass.__instance = this;
    /**/
    var that = this;

    var tableNotificationsId = 'notification-table';
    var notificationsTimeoutId;
    var refreshIntervalForNotifications = 30000 * REFRESH_INTERVAL_MULTIPLIER;

    var $notificationContainer;
    var $counter;

    this.getNotifications = function (refreshIfNeeded) {

        if (!windowIsActive) {
            clearTimeout(notificationsTimeoutId);
            notificationsTimeoutId = setTimeout(function () {
                that.getNotifications(true);
            }, refreshIntervalForNotifications);
            return;
        }

        var url = '/dashboard/notifications/' + tableNotificationsId + '?refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            headers: {
                "windowid": windowId
            },
            success: function (data) {
                if (!data) return;
                if (data.length == 0 || data[0].needRefresh) {
                    if (data.length == 0) {
                        handleAbsentMessages();
                    } else {
                        var unreadQuantity = 0;
                        var $tmpl = $('#notifications-row').html().replace(/@/g, '%');
                        clearNotificationsTable();
                        data.forEach(function (item) {
                            var $newItem = $(tmpl($tmpl, item));
                            if (!item.read) {
                                unreadQuantity++;
                            } else {
                                $($newItem).removeAttr('onclick');
                            }
                            $($notificationContainer).append($newItem);

                        });

                        if (unreadQuantity > 0) {
                            $($counter).text(unreadQuantity);
                            $($counter).show();
                        }
                    }

                }

                clearTimeout(notificationsTimeoutId);
                notificationsTimeoutId = setTimeout(function () {
                    that.getNotifications(true);
                }, refreshIntervalForNotifications);
            }
        });
    };

    /* TODO temporary disable
    (function init() {

        if ($('#notification-icon').length > 0) {
            $notificationContainer = $("#notifications-body");
            $counter = $('#unread-counter');
            unreadQuantity = 0;
            $($counter).hide();
            $('#notification-icon').find('.dropdown-menu').click(function (event) {
                event.stopPropagation();
            });
            $("#notifications-body-wrapper").mCustomScrollbar({
                theme: "dark",
                axis: "y",
                live: true
            });
            syncTableParams(tableNotificationsId, -1, function (data) {
                that.getNotifications();
            });
        }
    })();*/



}

function clearNotificationsTable() {
    var $table = $('#notifications-body');
    var $absent = $('#notifications-absent');
    var $script = $table.find('script');
    $table.empty();
    $table.append($absent);
    $table.append($script);
}



function markRead(element) {
    var count = $('#unread-counter').text();
    var idData = $(element).find('.notification-id').serialize();
    $.ajax({
        url: '/notifications/markRead',
        type: 'POST',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        data: idData,
        success: function () {
            $(element).removeClass('unread');
            $(element).addClass('read');
            $(element).removeAttr('onclick');
            $('#unread-counter').text(--count);
            if (count == 0) {
                $('#unread-counter').hide();
            }
        }
    })
}


function removeNotification(e, element) {
    var event = e || window.event;
    event.stopPropagation();
    var unreadCount = $('#unread-counter').text();
    var isRead = $(element).parent().hasClass('read');
    var idData = $(element).parent().find('.notification-id').serialize();
    $.ajax({
        url: '/notifications/remove',
        type: 'POST',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        data: idData,
        success: function () {
            $(element).parent().remove();
            if (!isRead) {
                $('#unread-counter').text(--unreadCount);
            }
            if (unreadCount == 0) {
                $('#unread-counter').hide();
            }
            if ($('.notification-item').length == 0) {
                handleAbsentMessages()
            }
        }
    })
}

function markReadAll() {
    var $counter = $('#unread-counter');
    $.ajax({
        url: '/notifications/markReadAll',
        type: 'POST',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        success: function () {
            $('.notification-item').removeClass('unread');
            $('.notification-item').addClass('read');
            $counter.text(0);
            $counter.hide();
        }
    })

}

function removeAllNotifications() {
    var $counter = $('#unread-counter');
    $.ajax({
        url: '/notifications/removeAll',
        type: 'POST',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        success: function () {
            $('.notification-item').remove();
            $counter.text(0);
            $counter.hide();
            handleAbsentMessages()
        }
    })
}

function handleAbsentMessages() {
    $('#notifications-absent').removeClass('invisible');
    $('#notifications-header').hide();
}