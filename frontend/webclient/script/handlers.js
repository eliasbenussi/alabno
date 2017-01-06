var $handlers = {};

$handlers.handle_login_success = function(msgobj) {
  // get user type
  var usertype = msgobj.usertype;
  if (usertype == "s") {
      window.location.hash = 'student';
      $globals.usertype = "s";
  } else if (usertype == "p") {
      window.location.hash = 'professor';
      $globals.usertype = "p";
  } else if (usertype == "a") {
      window.location.hash = 'professor';
      $globals.is_admin = true;
      $globals.usertype = "p";
  } else {
      console.error("Unrecognized user type " + usertype);
  }
  
  if ($globals.usertype == "s") {
    if (!$globals.student_scope) {
        console.error("student scope not loaded yet. Delaying...");
        setTimeout(function() { $handlers.handle_login_success(msgobj) }, 500);
        return;
    }
      
    var token = msgobj.id;

    var username = $globals.top_scope.username;
    $globals.token = token;
    $globals.student_scope.username = username;
    
    // Store into local storage
    $localstore.save_username(username);
    $localstore.save_token(token);
    
    $globals.top_scope.logged_in_flag = true;
    $globals.top_scope.$apply();
    $globals.student_scope.$apply();
  } else {
    if (!$globals.professor_scope) {
        console.error("professor scope not loaded yet. Delaying...");
        setTimeout(function() { $handlers.handle_login_success(msgobj) }, 500);
        return;
    }
      
    var token = msgobj.id;

    var username = $globals.top_scope.username;
    $globals.token = token;
    
    // Store into local storage
    $localstore.save_username(username);
    $localstore.save_token(token);
    
    $globals.top_scope.logged_in_flag = true;
    $globals.top_scope.$apply();
  }

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

$handlers.handle_job_list = function(msgobj) {
    if ($globals.usertype == "s") {
        return;
    }
    
    if (!$globals.professor_scope) {
        console.error("professor scope not loaded yet. Delaying...");
        setTimeout(function() { $handlers.handle_login_success(msgobj) }, 500);
        return;
    }
    
  var jobs = msgobj.jobs;

  if (!jobs) {
    console.log("Error, job_list.job is null");
    return;
  }

  if (!(jobs.constructor === Array)) {
    console.log("Error, job_list.job is not an Array");
    return;
  }

  // Write to model
  $globals.professor_scope.all_jobs = [];

  for (var i = 0; i < jobs.length; i++) {
    var in_job = jobs[i];
    var in_job_title = in_job.title;
    var in_job_status = in_job.status;
    var local = in_job.local;
    
    var color = "green";
    var glyphicon = "ok";
    if (in_job_status == "pending") {
      color = "blue";
      glyphicon = "time";
    } else if (in_job_status == "error") {
      color = "red";
      glyphicon = "warning-sign";
    } else if (in_job_status == "processing") {
      color = "orange";
      glyphicon = "cog";
    }

    var a_job = {};
    a_job.title = in_job_title;
    a_job.status = in_job_status;
    a_job.local = local;
    a_job.color = color;
    a_job.glyphicon = glyphicon;
    a_job.displayed = false;
    a_job.display = function(title) {
      // TODO : HIDING NOT WORKING PROPERLY
      a_job.displayed = !a_job.displayed;
      if (!a_job.displayed) {
        a_job.students = []
      } else {
        var msgobj = {};
        msgobj.type = "get_job";
        msgobj.id = $globals.token;
        msgobj.title = title;
        $globals.send(JSON.stringify(msgobj));
      }
    };
    a_job.students = [];

    $globals.professor_scope.all_jobs.push(a_job);
  }

  $globals.professor_scope.$apply();
};

$handlers.handle_job_group = function(msgobj) {
  var title = msgobj.title;
  var group = msgobj.group;

  // check that group is valid
  if (!group) {
    console.log("Error, group is null");
  }
  if (!(group.constructor === Array)) {
    console.log("Error, group is not an array");
  }

  // The student array has objects with field id
  var final_array = [];
  for (var i = 0; i < group.length; i++) {
    var groupitem = group[i];
    var studentid = groupitem.idx;
    var studentlogin = groupitem.uname;
    var a_student_obj = {};
    a_student_obj.id = studentid;
    a_student_obj.uname = studentlogin;
    final_array.push(a_student_obj);
  }

  // Find the job with the correct title
  for (var i = 0; i < $globals.professor_scope.all_jobs.length; i++) {
    var a_job = $globals.professor_scope.all_jobs[i];

    if (a_job.title == title) {
      // add the students array to this job
      $globals.professor_scope.all_jobs[i].students = final_array;
    }
  }

  $globals.professor_scope.$apply();
};

$handlers.handle_postpro_result = function(msgobj) {
  var title = msgobj.title;
  var student = msgobj.student;
  var data = msgobj.data;

  var dataobj = JSON.parse(data);
  $globals.professor_scope.student_result.letter_score = dataobj.letter_score;
  $globals.professor_scope.student_result.number_score = dataobj.number_score;
  $globals.professor_scope.student_result.annotations = dataobj.annotations;
  $globals.professor_scope.student_result.title = title;
  $globals.professor_scope.student_result.student = student;

  // change view
  $globals.professor_scope.show_section('show_student_result');

  // apply
  $globals.professor_scope.$apply();
};

$handlers.handle_annotated_file = function(msgobj) {
    
    if ($globals.usertype == 's') {
        
        $globals.student_scope.annotated_files = msgobj;
        $globals.student_scope.showhide_view('visualizer');
        $globals.student_scope.$apply();
        
    } else {

        $globals.professor_scope.annotated_files = [];

        $globals.professor_scope.student_exercise_type = msgobj.exercise_type;
        
        $globals.professor_scope.student_exercise_mark = msgobj.mark;
        
        var files = msgobj.files;

        for (var i = 0; i < files.length; i++) {
        var file = {};
        file.filename = files[i].filename;

        file.displayed = false;
        file.display = function(f) {
            f.displayed = !f.displayed;
        };

        var data_list = [];

        var data_list_length = files[i].data.length;
        for (var j = 0; j < data_list_length; j++) {
            var data_entry = {};
            data_entry.no = files[i].data[j].no;
            data_entry.content = files[i].data[j].content;
            data_entry.annotation = files[i].data[j].annotation;
            data_entry.show_icons = false;
            data_entry.show_icons_unlocked = true;
            data_entry.show_editor = false;
            data_list.push(data_entry);
        }
        file.data = data_list;
        $globals.professor_scope.annotated_files.push(file);
        }

        // change view
        $globals.professor_scope.show_section('show_annotated_file');

        // apply
        $globals.professor_scope.$apply();
    }
};

$handlers.handle_type_list = function(msgobj) {
    if ($globals.usertype == "s") {
        return;
    }
    
    if (!$globals.professor_scope) {
        console.error("professor scope not loaded yet. Delaying...");
        setTimeout(function() { $handlers.handle_login_success(msgobj) }, 500);
        return;
    }
    
    var data = msgobj.data;
    $globals.professor_scope.valid_exercise_types = [];
    for (var i = 0; i < data.length; i++) {
        $globals.professor_scope.valid_exercise_types.push(data[i]);
    }
    
    $globals.professor_scope.$apply();
};

$handlers.handle_std_ex_list = function(msgobj) {
    $globals.student_scope.exercise_list = msgobj;
    $globals.student_scope.$apply();
};

$handlers.handle_commits = function(msgobj) {
    $globals.professor_scope.commits = msgobj.data;
    $globals.professor_scope.show_section('show_commits');
    $globals.professor_scope.$apply();
};

$handlers.handle_status_info = function(msgobj) {
    color = msgobj.color;

    // set msg alert type
    if (color === 'black') {
      msgobj.alert_type = 'info';
    } else if (color === 'green') {
      msgobj.alert_type ='success';
    } else if (color === 'yellow') {
      msgobj.alert_type = 'warning';
    } else if (color === 'red') {
      msgobj.alert_type = 'danger';
    }

    var checker = msgobj;

    $globals.top_scope.statusinformation.unshift(msgobj);
    // only keep latest 3 messages
    $globals.top_scope.statusinformation = $globals.top_scope.statusinformation.slice(0,3);

    setTimeout(function(){
        for (var i = 0; i < $globals.top_scope.statusinformation.length; i++) {
            if (checker === $globals.top_scope.statusinformation[i]) {
                $globals.top_scope.statusinformation.splice(i, 1);
            }
        }
        $globals.top_scope.$apply();
    }, msgobj.timeout*1000);

    $globals.top_scope.$apply();
};
