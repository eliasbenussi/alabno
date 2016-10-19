var $globals = {};

$globals.socket = new WebSocket('ws://localhost:8686');

$globals.socket.onopen = function() {
    console.log('Connection opened');
};

$globals.socket.onmessage = function(message) {
    console.log(message.data);
    
    var msgobj = undefined;
    
    try {
        msgobj = JSON.parse(message.data);
    } catch (err) {
        console.log(err);
        return;
    }
    
    if (!msgobj.type) {
        console.log("message is missing type info");
    }
    
    if (msgobj.type == 'login_success') {
        $handlers.handle_login_success(msgobj);
    } else if (msgobj.type == 'login_fail') {
        $handlers.handle_login_failure(msgobj);
    } else {
        console.log("message type not recognized: " + msgobj.type);
    }
};

$globals.socket.onclose = function() {
    console.log('Connection closed');
};

$globals.socket.onerror = function() {
    console.log('WS error');
};

$globals.send = function(msg) {
    try {
        $globals.socket.send(msg);
    } catch (err) {
        console.log('tried to send ' + msg + ' but connection is not open yet');
    }
};