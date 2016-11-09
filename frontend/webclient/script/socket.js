var $globals = {};

var ws_address = location.protocol == 'https:' ?
                 'wss' + ws_address_stem + '4444' :
                 'ws' + ws_address_stem + '8686';

$globals.socket = new WebSocket(ws_address);

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
    } else if (msgobj.type == 'alert') {
        $handlers.handle_alert(msgobj);
    } else if (msgobj.type == 'job_sent') {
        $handlers.handle_job_sent(msgobj);
    } else if (msgobj.type == 'job_list') {
        $handlers.handle_job_list(msgobj);
    } else if (msgobj.type == 'job_group') {
        $handlers.handle_job_group(msgobj);
    } else if (msgobj.type == 'postpro_result') {
        $handlers.handle_postpro_result(msgobj);
    } else if (msgobj.type == 'annotated_file') {
        $handlers.handle_annotated_file(msgobj);
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
