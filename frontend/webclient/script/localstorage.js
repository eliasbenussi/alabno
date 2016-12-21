if (typeof(Storage) == "undefined") {
    alert("Your browser does not support HTML5 Local Storage. Some features will not behave correctly");
}

var $localstore = {};

$localstore.save_username = function(username) {
    localStorage.username = username;
};

$localstore.save_token = function(token) {
    localStorage.token = token;
};

$localstore.get_username = function() {
    return localStorage['username'];
};

$localstore.get_token = function() {
    return localStorage['token'];
};

// Number of controllers loaded
$localstore.ready = 0;
$localstore.number_controllers = 3;
// Checks for all controllers to be ready, and send token validation message
// to attempt restoring user session
$localstore.check_ready = function() {
    console.log("checking ready... " + $localstore.ready);
    
    if ($localstore.ready == $localstore.number_controllers) {
        var username = $localstore.get_username();
        var token = $localstore.get_token();
        
        if (!username || !token) {
            return;
        }
        
        $globals.top_scope.username = username;
        
        var msgobj = {};
        msgobj.type = 'validatetoken';
        msgobj.username = username;
        msgobj.token = token;
        $globals.send(JSON.stringify(msgobj));
    }
}
