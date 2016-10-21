import os
import sys
import subprocess
import argparse
import binascii
import json

import microservices

# #########################################################################
# CONFIGURATION

home_directory = os.path.abspath(os.getcwd());
temporary_directory = home_directory + os.sep + 'tmp' + os.sep

max_clone_depth = '50'

jobmanager_path = os.path.abspath(home_directory + os.sep + 'infrastructure' + os.sep + 'infrastructure' + os.sep + 'JobManager')

print(microservices.microservices)

# #########################################################################

# read the command line arguments
print(sys.argv)

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

parser.add_argument('--services')

parser.add_argument('--students')

args = parser.parse_args()

print(args.extype)
print(args.model)
print(args.services)
print(args.students)
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

def get_student_out_directory(base, student_number):
    return get_student_directory(base, student_number) + os.sep + 'commitX_out' + os.sep

# create temporary base directory
base_directory = ''
for i in range(500):
    random_hash = get_random_hash()
    candidate_directory = temporary_directory + os.sep + random_hash
    if not directory_exists(candidate_directory):
        base_directory = candidate_directory
        break

if base_directory == '':
    print('could not create a temporary base directory')
    sys.exit(1)

os.makedirs(base_directory)

# create temporary directory structure with:, all inside the base directory
#model
#studentX/config
#studentX/commitX
#studentX/commitX_out

for i in range(len(the_students_gits)):
    os.makedirs(get_student_directory(base_directory, i))
    os.makedirs(get_student_config_directory(base_directory, i))
    #os.makedirs(get_student_commit_directory(base_directory, i))
    os.makedirs(get_student_out_directory(base_directory, i))

# clone the model answer
if args.model and args.model != '':
    os.chdir(base_directory)
    cmd = 'git clone {} {} --depth {}'.format(args.model, 'model', max_clone_depth)
    subprocess.call(cmd, shell=True)

os.chdir(home_directory)

# clone the student git repos
for i in range(len(the_students_gits)):
    os.chdir(get_student_directory(base_directory, i))
    cmd = 'git clone {} {} --depth {}'.format(the_students_gits[i], 'commitX', max_clone_depth)
    subprocess.call(cmd, shell=True)
    os.chdir(home_directory)

# create the configuration JSON file for the JobManager
for i in range(len(the_students_gits)):
    jsonobj = {
        'input_directory': get_student_commit_directory(base_directory, i),
        'type': args.extype,
        'additional_config': '',
        'output_directory': get_student_out_directory(base_directory, i),
        'services': microservices.microservices
    }
    print('Json is {}'.format(json.dumps(jsonobj)))
    json_config_file_path = get_student_config_directory(base_directory, i) + os.sep + 'jobmanager.json'
    json_config_file = open(json_config_file_path, 'w')
    json_config_file.write(json.dumps(jsonobj))
    json_config_file.write('\n')
    json_config_file.close()
    
    cmd = 'infrastructure/infrastructure/JobManager < {}'.format(json_config_file_path)
    subprocess.call(cmd, shell=True)
    
