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
  // Display tabs with selection of view to show results in

  $scope.final_result_select_group = function(job_title, student_id) {
    console.log("Showing tabs for final results");
    $scope.show_sections('show_final_result_tabs', 'show_professor_exercises');
    $scope.current_job_title = job_title;
    $scope.current_student_id = student_id;
  };

  // #########################################################################
  // List of jobs

  // all_jobs contains objects of the type {title: "title", display: function(title), students: []}
  $scope.all_jobs = [];

  // get data for specific job and student
  $scope.get_data = function(subtype) {
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
        data_entry.show_icons = value;
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
          # hide buttons from all other lines
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
      
      # show buttons in all lines
      $scope.show_edit_buttons(true);
  }

});