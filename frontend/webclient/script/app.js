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
    
});

theapp.controller('studentController', function($scope) {
    $scope.name = 'X';
});

theapp.controller('professorController', function($scope) {
    // register callback to globals
    $globals.professor_scope = $scope;
    
    $scope.name = 'Y';
    
    $scope.new_exercise_clicked = function()
    {
        console.log('new exercise clicked');
        $scope.show_section('show_professor_new_exercise');
    };
    
    // #########################################################################
    // Show-hide flags
    $scope.show_professor_exercises = true;
    $scope.show_professor_new_exercise = false;
    $scope.show_student_result = false;
    $scope.show_hide_flags =
    [
        'show_professor_exercises',
        'show_professor_new_exercise',
        'show_student_result'
    ];
    
    // Show-hide-buttons
    $scope.show_section = function(to_show)
    {
        for (var i = 0; i < $scope.show_hide_flags.length; i++)
        {
            $scope[$scope.show_hide_flags[i]] = false;
        }
        $scope[to_show] = true;
    };
    
    $scope.entries = 
    [
        {}
    ];
    
    $scope.add_new_student_field = function()
    {
        $scope.entries.push({});
    };
    
    // #########################################################################
    // New job
    $scope.submit_new_exercise = function()
    {
        console.log("submit new exercise");
        console.log($scope.exercise_title);
        console.log($scope.exercise_type);
        console.log($scope.exercise_model);
        console.log($scope.entries);

        // get the array of strings of students gits
        var student_gits = [];
        for (var i = 0; i < $scope.entries.length; i++) {
            student_gits.push($scope.entries[i].git);
        }
        
        // send message to server
        var msgobj = {
            type: 'new_assignment',
            id: $globals.token,
            title: $scope.exercise_title,
            ex_type: $scope.exercise_type,
            model_git: $scope.exercise_model,
            students_git: student_gits
        };
        
        $globals.send(JSON.stringify(msgobj));
    };
    
    $scope.reset_submit = function() {
        console.log('Resetting submit');
        // clear out all data
        $scope.exercise_title = '';
        $scope.exercise_type = '';
        $scope.exercise_model = '';
        $scope.entries = [{}];
        
        // go back to the exercises view
        $scope.show_section('show_professor_exercises');
        console.log('Reset complete');
    };
    
    $scope.cancel_new_exercise = function()
    {
        $scope.reset_submit();
    };
    
    // #########################################################################
    // List of jobs
    
    // all_jobs contains objects of the type {title: "title", display: function(title), students: []}
    $scope.all_jobs = [];
    
    // get data for specific job and student
    $scope.get_data = function(title, studentid) {
        $scope.reset_result_postpro();
        console.log("Get data called with title [" + title + "], studentid [" + studentid + "]");
        var msgobj = {};
        msgobj.type = 'retrieve_result';
        msgobj.id = $globals.token;
        msgobj.title = title;
        msgobj.student = studentid;
        $globals.send(JSON.stringify(msgobj));
    };
    
    // Student results
    $scope.reset_result_postpro = function() {
        $scope.student_result = {};
        $scope.student_result.letter_score = 'NA';
        $scope.student_result.number_score = '0';
        $scope.student_result.annotations = 'NA';
        $scope.student_result.title = 'NA';
        $scope.student_result.student = 'NA'
    };
    $scope.reset_result_postpro();
;
});