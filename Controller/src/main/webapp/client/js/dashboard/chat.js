
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
           '<p class="message">'  + o['body']      + '</p>' ;

}

function appendNewMessage(messageObj) {
    const newMessage = formatNewMessage(JSON.parse(messageObj));
    $('#chat')
        .append(newMessage)
        .scrollTo('max');
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

    connect('en');

    loadChatHistory('EN');

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
