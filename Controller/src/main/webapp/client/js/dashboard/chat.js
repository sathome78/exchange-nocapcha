function appendNewMessage(messageObj) {
    const newMessage = '<p class="nickname">' + messageObj['from']    + '</p>' +
                       '<p class="message">'  + messageObj['message'] + '</p>' ;
    $('#chat')
        .append(newMessage)
        .scrollTo('max');
}

$(function () {
    var ws = new WebSocket('ws://localhost:8080/chat');

    ws.onmessage = function (data) {
        alert(JSON.stringify(data))
    }

    ws.send("blabla");

});



