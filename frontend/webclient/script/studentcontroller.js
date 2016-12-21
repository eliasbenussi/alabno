theapp.controller('studentController', function($scope) {
    $scope.name = 'X';
  
    // ###########################################################################
    // Controller ready
    $scope.init = function() {
      $localstore.ready += 1;
      $localstore.check_ready();
    }
    $scope.init();
  
});
