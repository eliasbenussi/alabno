import os
import sys
import subprocess
import argparse
import binascii
import json

import microservices
import clonerutils

################################################################################
# INFORMATION
#
# Exit code = 0     Update successful, microservices run
# Exit code = 1     Error
# Exit code = 34    No update found, no errors
#

# ##############################################################################
# CONFIGURATION

# alabno/infrastructure
exec_dir = os.path.abspath(os.path.dirname(os.path.abspath(__file__)))
# alabno
alabno_dir = os.path.abspath(exec_dir + os.sep + '..')
# alabno/tmp
tmp_dir = os.path.abspath(alabno_dir + os.sep + 'tmp')

max_clone_depth = '1'

jobmanager_path = os.path.abspath(alabno_dir + os.sep + 'infrastructure' + os.sep + 'infrastructure' + os.sep + 'JobManager')

# ##############################################################################

# read the command line arguments

parser = argparse.ArgumentParser()

parser.add_argument('--extype',
                    required=True
                    )

parser.add_argument('--exname',
                    required=True
                    )

parser.add_argument('--studentidx',
                    required=True
                    )

args = parser.parse_args()

# ##############################################################################

student_dir = os.path.abspath(tmp_dir + os.sep + '{}/student{}'.format(args.exname, args.studentidx))

# Discover url.txt and last.txt
url_file_path = os.path.abspath(student_dir + os.sep + 'url.txt')
if not os.path.isfile(url_file_path):
    print(url_file_path + ' does not exist. Aborting...')
    sys.exit(1)
url_file = open(url_file_path, 'r')
the_url = url_file.read()
url_file.close()

last_file_path = os.path.abspath(student_dir + os.sep + 'last.txt')
if not os.path.isfile(last_file_path):
    print(last_file_path + ' does not exist. Aborting...')
    sys.exit(1)
last_file = open(last_file_path, 'r')
the_last = last_file.read()
last_file.close()

# Clone into a commitX directory
os.chdir(student_dir)
cmd = 'timeout 60 git clone {} {} --depth {}'.format(the_url, 'commitX', max_clone_depth)
code = subprocess.call(cmd, shell=True)
if code != 0:
    print('Cloning was not successful. Return code: {}'.format(code))
    cmd = 'rm -rf {}'.format(os.path.abspath(student_dir + os.sep + 'commitX'))
    subprocess.call(cmd, shell=True)
    sys.exit(1)

# get the commit hash
commithash = clonerutils.discover_commit_hash(os.path.abspath(student_dir + os.sep + 'commitX'))
if commithash == the_last:
    # there was no change, cleanup
    cmd = 'rm -rf {}'.format(os.path.abspath(student_dir + os.sep + 'commitX'))
    subprocess.call(cmd, shell=True)
    sys.exit(34)

# update the last commit hash file
last_file = open(last_file_path, 'w')
last_file.write(commithash)
last_file.close()

# rename commitX
student_commit_dir = os.path.abspath(student_dir + os.sep + 'commit' + commithash)
cmd = 'mv {} {}'.format(os.path.abspath(student_dir + os.sep + 'commitX'), student_commit_dir)
subprocess.call(cmd, shell=True)

# create the configuration JSON file for the JobManager
student_in_directory = student_commit_dir
student_out_directory = student_in_directory + '_out'
cmd = 'mkdir {}'.format(student_out_directory)
subprocess.call(cmd, shell=True)

jsonobj = {
    'input_directory': student_in_directory,
    'type': args.extype,
    'additional_config': '',
    'output_directory': student_out_directory,
    'services': microservices.microservices,
    'model_directory': os.path.abspath(tmp_dir + os.sep + args.exname + os.sep + 'model')
}

print('Json is {}'.format(json.dumps(jsonobj)))
json_config_file_path = os.path.abspath(student_dir + os.sep + 'config' + os.sep + 'jobmanager.json')
json_config_file = open(json_config_file_path, 'w')
json_config_file.write(json.dumps(jsonobj))
json_config_file.write('\n')
json_config_file.close()

cmd = '{} < {}'.format(jobmanager_path, json_config_file_path)
subprocess.call(cmd, shell=True)

# postprocessor arguments
language = args.extype
postpro_input_json_paths = []
postpro_output_json_path = os.path.abspath(student_out_directory + os.sep + 'postpro.json')

# Get the list of microservice output jsons
for item in os.listdir(student_out_directory):
    full_path = os.path.abspath(student_out_directory + os.sep + item)
    if len(full_path) >= 12 and full_path[-12:] == '_output.json':
        postpro_input_json_paths.append(full_path)

# call the postprocessor
postprocessor_jar_path = os.path.abspath(alabno_dir + os.sep + 'backend/postprocessor/target/postprocessor-1.0-SNAPSHOT-jar-with-dependencies.jar')
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
print('#FINALOUTPUTS={}'.format(postpro_output_json_path))
sys.exit(0)
