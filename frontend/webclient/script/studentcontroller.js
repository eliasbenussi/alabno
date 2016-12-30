theapp.controller('studentController', function($scope) {
    $globals.student_scope = $scope;
    
    $scope.name = 'X';
    
    // #########################################################################
    // Show-Hide flags
    $scope.showhide = {};
    $scope.showhide.exercise_list = true;
    $scope.showhide.visualizer = false;
    
    $scope.showhide_view = function(viewname) {
        // hide everything
        for (var key in $scope.showhide) {
            if ($scope.showhide.hasOwnProperty(key)) {
                $scope.showhide[key] = false;
            }
        }

        // show the desired one
        $scope.showhide[viewname] = true;
    }
    
    // #########################################################################
    // Exercises list
    
    $scope.refresh_exercise_list = function() {
        var msgobj = {};
        msgobj.type = "std_refresh_list";
        msgobj.id = $globals.token;
        $globals.send(JSON.stringify(msgobj));
    }
    
    $scope.exercise_list = {};
    
    // Sends the message to request a single exercise
    $scope.request_exercise_view = function(title, stdno, hash) {
        var msgobj = {};
        msgobj.type = "std_retrieve_result";
        msgobj.id = $globals.token;
        msgobj.title = title;
        msgobj.student = stdno;
        msgobj.hash = hash;
        $globals.send(JSON.stringify(msgobj));
    }
  
});

$localstore.ready += 1;
$localstore.check_ready();