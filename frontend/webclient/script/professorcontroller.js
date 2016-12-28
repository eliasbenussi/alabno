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
  $scope.show_final_result_tabs = false;
  $scope.show_student_result = false;
  $scope.show_annotated_file = false;
  $scope.show_hide_flags =
    [
    'show_professor_exercises',
    'show_professor_new_exercise',
    'show_final_result_tabs',
    'show_student_result',
    'show_annotated_file'
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

  //Show multiple sections (same as above, without hiding everything else)
  $scope.show_sections = function() {
    for (var i = 0; i < $scope.show_hide_flags.length; i++) {
      $scope[$scope.show_hide_flags[i]] = false;
    }
    for (var j = 0; j < arguments.length; j++) {
      $scope[arguments[j]] = true;
    }
  };

  $scope.entries = [{}];

  $scope.add_new_student_field = function()
  {
    $scope.entries.push({});
  };

  // #########################################################################
  // New job
  
  // list of valid exercises types
  $scope.valid_exercise_types = [];
  
  $scope.submit_new_exercise = function()
  {
    // get the array of strings of students gits
    var student_gits = [];
    for (var i = 0; i < $scope.entries.length; i++) {
      if ($scope.entries[i].git && $scope.entries[i].git != '') {
          var sobj = {};
          sobj.git = $scope.entries[i].git + ".git";
          sobj.uname = $scope.entries[i].uname;
          student_gits.push(sobj);
      }
    }

    // send message to server
    var msgobj = {
      type: 'new_assignment',
      id: $globals.token,
      title: $scope.exercise_title,
      ex_type: $scope.exercise_type,
      model_git: $scope.exercise_model + ".git",
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
  $scope.get_data = function(job_title, student_id, subtype) {
    $scope.current_job_title = job_title;
    $scope.current_student_id = student_id;
    if (subtype == 'postprocessor') {
      $scope.reset_result_postpro();
    } else if (subtype == 'annotated') {
      $scope.reset_annotated_result();
    }

    console.log("Get data called with title [" + $scope.current_job_title + "], studentid [" + $scope.current_studentid + "]");
    var msgobj = {};
    msgobj.type = 'retrieve_result';
    msgobj.subtype = subtype;
    msgobj.id = $globals.token;
    msgobj.title = $scope.current_job_title;
    msgobj.student = $scope.current_student_id;
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


  // Annotated files
  $scope.reset_annotated_result = function() {
    $scope.annotated_files = [];
  };
  
  // ###########################################################################
  // marking feedback
  
  // false: show mark read only
  // true: show mark editor
  $scope.show_mark_editor = false;
  // exercise type of the exercise being shown
  $scope.student_exercise_type = "";
  // ng-bound to the mark shown, and to the list selector in mark editor
  $scope.student_exercise_mark = "";
  
  // shows the mark editor
  $scope.edit_mark = function() {
    $scope.show_mark_editor = true;
  };
  
  // sends a modified mark to the backend
  $scope.submit_mark = function() {
    console.log("Now mark is " + $scope.student_exercise_mark);
    
    // Send all the available filenames of the exercise, because the mark
    // concerns all files belonging to the exercise, and it's each updater's
    // job to filter out unwanted files if necessary. Each updater receives
    // source code and name of file, so it is easy for backend to identify
    // which are the wanted files
    for (var i = 0; i < $scope.annotated_files.length; i++) {
        var the_annotated_file = $scope.annotated_files[i];
        
        var msgobj = {};
        msgobj.type = 'markfeedback';
        msgobj.id = $globals.token;
        msgobj.filename = the_annotated_file.filename;
        msgobj.exercise_type = $scope.student_exercise_type;
        msgobj.mark = $scope.student_exercise_mark;
        $globals.send(JSON.stringify(msgobj));
    }

    $scope.show_mark_editor = false;
  };
  
  // ###################################################################
  // Feedback management and annotation editing
  
  // editlist: list of old annotations that need to be changed/removed
  $scope.editing_file = null;

  $scope.editing_data_entry = null;
  
  $scope.editing_lineno = 0;
  
  $scope.feedback_sent = '';
  
  // The source code being used
  // each element has lineno and text
  $scope.editing_source_cache = "";
  
  $scope.show_edit_buttons = function(value) {
    var annotated_files = $scope.annotated_files;
    for (var i = 0; i < annotated_files.length; i++) {
      var file = annotated_files[i];
      var filedata = file.data;
      for (var j = 0; j < filedata.length; j++) {
        var data_entry = filedata[j];
        data_entry.show_icons_unlocked = value;
      }
    }
  }
  
  $scope.add_feedback_annotation = function(filename, lineno, text, oldannotation, data_entry) {
      if (!$scope.editing_file) {
          $scope.editing_data_entry = data_entry;
          $scope.editing_file = filename;
          $scope.editing_data_entry.editing_annotation = oldannotation;
          $scope.editing_data_entry.editing_ann_type = "semantic";
          $scope.editing_lineno = lineno;
          $scope.editing_source_cache = text;
          console.log('opening editor...')
          data_entry.show_editor = true;
          // hide buttons from all other lines
          $scope.show_edit_buttons(false);
      } else {
          // check that it's the same filename
          if (filename != $scope.editing_file) {
              alert('You cannot add a source line from a different file');
              return;
          }
      }
  }
  
  $scope.delete_feedback_annotation = function(filename, lineno, oldannotation, data_entry) {
      var msgobj = {};
      msgobj.type = 'feedback';
      msgobj.id = $globals.token;
      msgobj.filename = filename;
      msgobj.ann_type = "ok";
      msgobj.annotation = "ok";
      msgobj.lineno = lineno;
      
      data_entry.annotation = "";

      $globals.send(JSON.stringify(msgobj));
      
      // show buttons in all lines
      $scope.show_edit_buttons(true);
  }
  
  $scope.submit_feedback_annotation = function() {
      var msgobj = {};
      msgobj.type = 'feedback';
      msgobj.id = $globals.token;
      msgobj.filename = $scope.editing_file;
      msgobj.ann_type = $scope.editing_data_entry.editing_ann_type;
      msgobj.annotation = $scope.editing_data_entry.editing_annotation;
      msgobj.lineno = $scope.editing_lineno;
      
      $scope.editing_data_entry.annotation = $scope.editing_data_entry.editing_annotation;
      
      $globals.send(JSON.stringify(msgobj));

      $scope.successful_feedback = true;
      $scope.feedback_sent = "Sent";
  }
  
  $scope.feedback_clear = function() {
      $scope.editing_data_entry.show_editor = false;
      $scope.editing_file = null;
      $scope.editing_data_entry.editing_annotation = "";
      $scope.editing_data_entry.editing_ann_type = "";
      $scope.editing_data_entry = null;
      $scope.editing_source_cache = "";
      $scope.editing_lineno = 0;
      $scope.feedback_sent = '';
      $scope.successful_feedback = false;

      // show buttons in all lines
      $scope.show_edit_buttons(true);
  }

});


$localstore.ready += 1;
$localstore.check_ready();