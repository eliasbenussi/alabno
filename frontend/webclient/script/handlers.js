var $handlers = {};

$handlers.handle_login_success = function(msgobj) {
    $globals.token = msgobj.id;
};

$handlers.handle_login_failure = function(msgobj) {
    alert('Login failed');
};

$handlers.handle_alert = function(msgobj) {
    if (!msgobj.message) {
        console.log("alert message: no message field found");
    }
    
    alert(msgobj.message);
};

$handlers.handle_job_sent = function(msgobj) {
    console.log('handlers: resetting');
    $globals.professor_scope.reset_submit();
    $globals.professor_scope.$apply();
};