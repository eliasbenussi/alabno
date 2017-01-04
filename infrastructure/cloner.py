import os
import sys
import subprocess
import argparse
import binascii
import json

import microservices
import clonerutils

# #########################################################################
# CONFIGURATION

home_directory = os.path.abspath(os.getcwd());
temporary_directory = home_directory + os.sep + 'tmp' + os.sep

max_clone_depth = '1'

jobmanager_path = os.path.abspath(home_directory + os.sep + 'infrastructure' + os.sep + 'infrastructure' + os.sep + 'JobManager')

# #########################################################################

# read the command line arguments

if len(sys.argv) < 4:
    print('Error, expecting more arguments!')
    sys.exit(1)

parser = argparse.ArgumentParser()

parser.add_argument('--extype',
                    required=True
                    )

parser.add_argument('--model',
                    required=True
                    )

parser.add_argument('--exname',
                    required=True
                    )

parser.add_argument('--students')

args = parser.parse_args()

splitted = args.students.split(' ')
the_students_gits = []
for s in splitted:
    if s:
        the_students_gits.append(s)

# utility functions
def directory_exists(path):
    return os.path.isdir(path) or os.path.exists(path)

def get_random_hash():
    return binascii.hexlify(os.urandom(16))

def get_model_directory(base):
    return base + os.sep + 'model' + os.sep

def get_student_directory(base, student_number):
    return base + os.sep + 'student' + str(student_number) + os.sep

def get_student_config_directory(base, student_number):
    return get_student_directory(base, student_number) + os.sep + 'config' + os.sep

def get_student_commit_directory(base, student_number):
    return get_student_directory(base, student_number) + os.sep + 'commitX' + os.sep

def makedirs(dirpath):
    cmd = 'mkdir {}'.format(dirpath)
    subprocess.call(cmd, shell=True)

# create temporary base directory
base_directory = temporary_directory + os.sep + args.exname
if base_directory == '':
    print('could not create a temporary base directory')
    sys.exit(1)
code = subprocess.call('mkdir {}'.format(base_directory), shell=True)


# create temporary directory structure with:, all inside the base directory
#model
#studentX/config
#studentX/commitX
#studentX/commitX_out

for i in range(len(the_students_gits)):
    makedirs(get_student_directory(base_directory, i))
    makedirs(get_student_config_directory(base_directory, i))

# clone the model answer
if args.model and args.model != '':
    os.chdir(base_directory)
    cmd = 'timeout 60 git clone {} {} --depth {}'.format(args.model, 'model', max_clone_depth)
    code = subprocess.call(cmd, shell=True)
    if code != 0:
        print('Cloning of the model answer at {} failed. Aborting...'.format(args.model))
        sys.exit(1)

os.chdir(home_directory)

student_commit_directories = []

# clone the student git repos
for i in range(len(the_students_gits)):
    student_base_directory = get_student_directory(base_directory, i)
    
    os.chdir(student_base_directory)
    
    # first, clone into a commitX directory
    cmd = 'timeout 60 git clone {} {} --depth {}'.format(the_students_gits[i], 'commitX', max_clone_depth)
    code = subprocess.call(cmd, shell=True)
    if code != 0:
        print('Cloning of student repository at {} failed. Aborting...'.format(the_students_gits[i]))
        sys.exit(1)
    
    # Save the git url into title/studentX/url.txt
    url_file_path = os.path.abspath(student_base_directory + os.sep + 'url.txt')
    url_file = open(url_file_path, 'w')
    url_file.write(the_students_gits[i])
    url_file.close()

    # now, enter the commitX directory to discover the commit hash
    commithash = clonerutils.discover_commit_hash(get_student_commit_directory(base_directory, i))
    
    # Save the latest commit hash into title/studentX/last.txt
    hash_file_path = os.path.abspath(student_base_directory + os.sep + 'last.txt')
    hash_file = open(hash_file_path, 'w')
    hash_file.write(commithash)
    hash_file.close()
    
    # rename it with the hash
    cmd = 'mv {} {}'.format(os.path.abspath(student_base_directory + os.sep + 'commitX'), os.path.abspath(student_base_directory + os.sep + 'commit' + commithash))
    subprocess.call(cmd, shell=True)
    
    student_commit_dir = os.path.abspath(student_base_directory + os.sep + 'commit' + commithash)
    os.makedirs(student_commit_dir + '_out')
    student_commit_directories.append(os.path.abspath(student_commit_dir))
    
    os.chdir(home_directory)

postpro_all_outputs = []

# create the configuration JSON file for the JobManager
for i in range(len(the_students_gits)):
    student_in_directory = student_commit_directories[i]
    student_out_directory = student_in_directory + '_out'
    
    jsonobj = {
        'input_directory': student_in_directory,
        'type': args.extype,
        'additional_config': '',
        'output_directory': student_out_directory,
        'services': microservices.microservices
    }

    if args.model and args.model != '':
        jsonobj['model_directory'] = get_model_directory(base_directory)
    
    print('Json is {}'.format(json.dumps(jsonobj)))
    json_config_file_path = get_student_config_directory(base_directory, i) + os.sep + 'jobmanager.json'
    json_config_file = open(json_config_file_path, 'w')
    json_config_file.write(json.dumps(jsonobj))
    json_config_file.write('\n')
    json_config_file.close()
    
    cmd = 'infrastructure/infrastructure/JobManager < {}'.format(json_config_file_path)
    subprocess.call(cmd, shell=True)
    
    # postprocessor arguments
    language = args.extype
    postpro_input_json_paths = []
    postpro_output_json_path = os.path.abspath(student_out_directory + os.sep + 'postpro.json')
    postpro_all_outputs.append(os.path.abspath(postpro_output_json_path))
    
    # Get the list of microservice output jsons
    for item in os.listdir(student_out_directory):
        full_path = os.path.abspath(student_out_directory + os.sep + item)
        if len(full_path) >= 12 and full_path[-12:] == '_output.json':
            postpro_input_json_paths.append(full_path)
    
    # call the postprocessor
    postprocessor_jar_path = os.path.abspath(home_directory + os.sep + 'backend/postprocessor/target/postprocessor-1.0-SNAPSHOT-jar-with-dependencies.jar')
    cmd = 'java -cp {} postprocessor.PostProcessor {} {} {}'.format(
        postprocessor_jar_path,
        language,
        ' '.join(postpro_input_json_paths),
        postpro_output_json_path
        )
    
    code = subprocess.call(cmd, shell=True)
    if code != 0:
        print('cmd [{}] failed with code {}'.format(cmd, code))

# Special stdout line that will be read by the caller process
print('#FINALOUTPUTS={}'.format(' '.join(postpro_all_outputs)))
