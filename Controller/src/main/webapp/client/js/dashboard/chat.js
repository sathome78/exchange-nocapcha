
var ws; //web-socket connection

function connect(chatLang) {
    if (ws!=null) {
        ws.close();
    }
    ws = new SockJS('/chat-' + chatLang);
    ws.onmessage = function (message) {
        const messageObj = JSON.parse(message['data']);
        if ($.inArray("removed", Object.keys(messageObj)) >= 0) {
            removeMessageFromChatHistory(messageObj.id);
        } else {
            appendNewMessage(messageObj);
        }

    };
}

function toJson(a) {
    var o = {};
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
}

function formatNewMessage(o) {
    var deletionButton = '';
    if ($("a[href='/2a8fy7b07dxe44']").length > 0) {
        deletionButton = '<button class="btn btn-sm btn-danger pull-right" onclick="deleteMessage.call(this, event)">' +
            '<span class="glyphicon glyphicon-remove"></span></button>';
    }
    var dateTime = o['time'].split(/[\s.]+/);
    var previousDate = $('.chat_message .message_date').last().text();
    var hidden = previousDate === dateTime[0] ? ' invisible' : '';
    return '<div class="chat_message">' + deletionButton +
        '<p class="message_date text-center ' + hidden + '">' + dateTime[0] + '</p><span class="message_id invisible">' + o['id'] +
        '</span> <span class="user_id invisible">' + o['userId'] + '</span> <p class="nickname">' + o['nickname']  +
         '<span class="message_time text-muted">' + dateTime[1] + '</span>' +
        '</p> <p class="message"><span class="message_body">'  + o['body']  + '</span></p></div>' ;

}

function appendNewMessage(messageObj) {
    const newMessage = formatNewMessage(messageObj);

    $('#chat .mCSB_container').append(newMessage);

     scrollChat();

    $('#new_mess').find('input[name="body"]').val('');
}

function loadChatHistory(lang) {
    $.ajax('/chat/history', {
        method: 'GET',
        data: 'lang=' + lang
    }).done(function (data) {
        for (var i = data.length - 1; i >=0; i--) {
            $('#chat .mCSB_container').append(formatNewMessage(data[i]));
        }
        scrollChat();
    }).fail(function(e){
        console.log(e)
    })
}

function changeChatLocale(lang) {
    $('#chat .mCSB_container').empty();
    $('#new_mess').find('input[name="lang"]').val(lang);
    connect(lang);
    loadChatHistory(lang);
}


$(function () {

    initScrollbar();

    $('.chat-locales>a:first-child').click(); // Connect to websocket and load chat history

    $('#new_mess').submit(function (e) {
        e.preventDefault();
        const o = toJson($(this).serializeArray());
        $.ajax('/chat/new-message', {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            method: 'POST',
            data: $(this).serializeArray()
        }).done(function (e) {
            /*NOP*/
        }).fail(function (e) {
            console.log(e);
            const error = JSON.parse(e['responseText']);
            alert(error['errorInfo']);
        });
    })
});

function initScrollbar() {
    $("#chat").mCustomScrollbar({
        theme:"dark",
        axis:"y",
        live: true
    });
}

function scrollChat() {
    $('#chat').mCustomScrollbar("scrollTo", "bottom", {
        scrollInertia:0
    });
}



function deleteMessage(event) {
    var $chat_message = $(this).parent();


    var message = {
        id: parseInt($chat_message.find('.message_id').text()),
        userId: parseInt($chat_message.find('.user_id').text()),
        body: $chat_message.find('.message_body').text(),
        nickname: $chat_message.find('.nickname').text(),
        lang: $('#new_mess').find('input[name="lang"]').val()
    };
    $.ajax('/2a8fy7b07dxe44/chat/deleteMessage', {
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        method: 'POST',
        data: message,
        dataType: "text"
    }).done(function () {
      //
    }).fail(function(e){
        console.log(e)
    })
}

function removeMessageFromChatHistory(id) {
    var $messageDiv =$('.chat_message').filter(function (index) {
        return parseInt($(this).find('.message_id').text()) === id;
    });
    var $prevMessageDiv = $messageDiv.prev();
    var $nextMessageDiv = $messageDiv.next();

    if ($prevMessageDiv.find('.message_date').text() != $nextMessageDiv.find('.message_date').text()) {
        $nextMessageDiv.find('.message_date').removeClass('invisible');
    }
    $messageDiv.remove();
}