import os
import subprocess
import sys
import random
import datetime
import argparse

# ARG PARSER ###########################################################
parser = argparse.ArgumentParser()
    
parser.add_argument('--clean',
                    action='store_true',
                    default=False
                    )

args = parser.parse_args()

# UTILITY FUNCTIONS ####################################################

def datetime_past_seconds(time):
    now = datetime.datetime.now()
    difference = now - time
    return difference.total_seconds()

def clear_directory(target):
    old_threshold = 3600 # Delete files older than 1 hour
    randint = random.randint(0,100) # 1 possibility in 100 of performing a cleaning
    if randint == 0:
        print('Clearing directory {}'.format(target))
        dir_content = os.listdir(target)
        for a_file in dir_content:
            if a_file == 'manifest.txt':
                continue
            timestamp = os.path.getmtime(target + os.sep + a_file)
            time_created = datetime.datetime.fromtimestamp(timestamp)
            time_past = datetime_past_seconds(time_created)
            print('Time past for {} is {}'.format(a_file, time_past))
            if time_past > old_threshold:
                print('Removing {}'.format(target + os.sep + a_file))
                os.remove(target + os.sep + a_file)


# MAIN SCRIPT ##########################################################

# alabno/simple-haskell-marker
exec_dir = os.path.abspath(os.path.dirname(os.path.abspath(__file__)))
# alabno/simple-haskell-marker/training
training_dir = os.path.abspath(exec_dir + os.sep + 'training')

# clear old directory entries if necessary
if args.clean:
    clear_directory(training_dir)
    sys.exit(0)

# get list of items in training directory
dir_content = os.listdir(training_dir)

# sort the list
dir_content.sort()

# remove anything that isn't starting with train
new_dir = []
for a_dir in dir_content:
    stem, ext = os.path.splitext(a_dir)
    if stem[:5] == 'train' and ext == '.train':
        new_dir.append(a_dir)

# create the manifest file
manifest_path = exec_dir + os.sep + 'training' + os.sep + 'manifest.txt'
manifest_file = open(manifest_path, 'w')
stem, ext = os.path.splitext(new_dir[len(new_dir) - 1])
manifest_file.write(stem)
manifest_file.close()
