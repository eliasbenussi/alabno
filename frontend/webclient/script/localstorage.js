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
