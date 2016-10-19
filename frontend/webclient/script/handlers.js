var $handlers = {};

$handlers.handle_login_success = function(msgobj) {
    $globals.token = msgobj.id;
};

$handlers.handle_login_failure = function(msgobj) {
    alert('Login failed');
};