theapp.controller('mycontroller', function($scope) {

  $globals.top_scope = $scope;

  $scope.professor_button_clicked = function()
  {
    window.location.hash = 'professor';
  };

  $scope.student_button_clicked = function()
  {
    window.location.hash = 'student';
  };

  $scope.login_clicked = function()
  {
    console.log('got a login ' + $scope.username + ' # ' + $scope.password);

    // here I want to send 
    var login_msg_obj = {
      "type": "login",
      "username": $scope.username,
      "password": $scope.password
    };

    $globals.send(JSON.stringify(login_msg_obj));
  };

  $scope.login_enter = function(keyEvent) {
    if (keyEvent.which === 13) {
      $scope.login_clicked();
    }
  }

});
