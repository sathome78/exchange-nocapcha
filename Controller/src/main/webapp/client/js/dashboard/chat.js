
var ws; //web-socket connection

function connect(chatLang) {
    if (ws!=null) {
        ws.close();
    }
    ws = new SockJS('/chat-' + chatLang);
    ws.onmessage = function (message) {
        appendNewMessage(message['data']);
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
    return '<p class="nickname">' + o['nickname']  + '</p>' +
           '<p class="message"><span class="message_body">'  + o['body']      + '</span></p>' ;

}

function appendNewMessage(messageObj) {
    const newMessage = formatNewMessage(JSON.parse(messageObj));
    $('#chat').append(newMessage);
    adjustScrollbar();
    scrollChat();

    $('#new_mess').find('input[name="body"]').val('');
}

function loadChatHistory(lang) {
    $.ajax('/chat/history', {
        method: 'GET',
        data: 'lang=' + lang
    }).done(function (data) {
        for (var i = data.length - 1; i >=0; i--) {
            $('#chat').append(formatNewMessage(data[i]));
        }
        scrollChat();
    }).fail(function(e){
        console.log(e)
    })
}

function changeChatLocale(lang) {
    $('#chat').empty();
    $('#new_mess').find('input[name="lang"]').val(lang);
    connect(lang);
    loadChatHistory(lang);
}


$(function () {

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

function adjustScrollbar() {

    // $("#chat").mCustomScrollbar("update") does not work in any combinations

    $("#chat").mCustomScrollbar("destroy");
   $("#chat").mCustomScrollbar({
        theme:"dark",
        axis:"y"
    });
}

function scrollChat() {
    $('#chat').mCustomScrollbar("scrollTo", "bottom",{
        scrollInertia:0
    });
}