var theapp = angular.module('myapp', ['ngRoute']);

theapp.config(['$routeProvider', function($routeProvider) {

  $routeProvider.

    when('/student', {
      templateUrl: 'student.htm',
      controller: 'studentController'
    }).

  when('/professor', {
    templateUrl: 'professor.htm',
    controller: 'professorController'
  }).

  otherwise({
    redirectTo: '/professor'
  });

}]);
