import sys
import subprocess
import os
import json
import traceback
from Marker import Marker

# function to exit with an error message and dump the error message in the output
# json
def exit_with_error(msg, output_json_path):
    jsonobj = {
        'score': 0,
        'annotations': [],
        'error': msg
        }
    jsonstr = json.dumps(jsonobj)
    
    print('Error: {}'.format(msg))
    
    if os.path.isfile(output_json_path):
        print('Output json exists.')
    else:
        try:
            output_json_file = open(output_json_path, 'w')
            output_json_file.write(jsonstr)
            output_json_file.close()
        except:
            print('Could not open file {} for writing'.format(output_json_path))
    
    sys.exit(1)

# calculate classpaths
# exec_dir = alabno/backend/overlapping-block-marker
exec_dir = os.path.abspath(os.path.dirname(os.path.abspath(__file__)))
# alabno_dir = alabno
alabno_dir = os.path.abspath(exec_dir + os.sep + '..' + os.sep + '..')

# read arguments
# 0 scriptname
# 1 input json path
# 2 output json path

if len(sys.argv) != 3:
    print('Usage: ./OverlappingBlockMarker <input.json> <output.json>')
    sys.exit(1)

input_json_path = sys.argv[1]
output_json_path = os.path.abspath(sys.argv[2])

# read the input json
input_json_file = open(input_json_path, 'r')
input_json_string = input_json_file.read()
input_json_obj = None
try:
    input_json_obj = json.loads(input_json_string)
except:
    exit_with_error('Could not parse input JSON', output_json_path)

try:
    input_directory = os.path.abspath(input_json_obj['input_directory'])
except:
    exit_with_error('Invalid JSON: could not find key input_directory', output_json_path)

print('Read input directory {}'.format(input_directory))

# chdir into alabno
os.chdir(alabno_dir)

# walk the input directory to find haskell files
# Note: this piece of code binds the marker to haskell files.
# It can be easily extended to look for other types of files.
haskell_file_paths = []

for root, dirs, files in os.walk(input_directory, topdown=False):
    for name in files:
        filename = os.path.relpath(os.path.join(root,name))
        stem, ext = os.path.splitext(filename)
        if ext == '.hs':
            _path = os.path.abspath(filename)
            haskell_file_paths.append(_path)

# read the manifest file and get the training file

# alabno/backend/overlapping-block-marker/training/manifest.txt
training_path = None
try:
    manifest_path = exec_dir + os.sep + 'training' + os.sep + 'manifest.txt'
    manifest_file = open(manifest_path, 'r')
    for line in manifest_file:
        training_path = line.replace('\n', '')
        break
    manifest_file.close()
except:
    print(traceback.format_exc())
    exit_with_error('Error when loading the manifest file for the Overlapping Block Marker training data', output_json_path)

# alabno/overlapping-block-marker/training/trainXXXXX.train
training_path = os.path.abspath(exec_dir + os.sep + 'training' + os.sep + training_path + '.train')
# Check that it exists
if not os.path.isfile(training_path):
    exit_with_error('The training file {} does not exist'.format(training_path), output_json_path)

# Initialise Marker and mark
marker = Marker(training_path, haskell_file_paths)
marker.mark()
marker.write_output(output_json_path)
