import os
import subprocess
import sys

# alabno/simple-haskell-marker
exec_dir = os.path.abspath(os.path.dirname(os.path.abspath(__file__)))

# get list of items in training directory
dir_content = os.listdir(exec_dir + os.sep + 'training')

# sort the list
dir_content.sort()

# remove anything that isn't starting with train
new_dir = []
for a_dir in dir_content:
    print(a_dir)
    stem, ext = os.path.splitext(a_dir)
    if stem[:5] == 'train' and ext == '.train':
        new_dir.append(a_dir)

# create the manifest file
manifest_path = exec_dir + os.sep + 'training' + os.sep + 'manifest.txt'
manifest_file = open(manifest_path, 'w')
stem, ext = os.path.splitext(new_dir[len(new_dir) - 1])
manifest_file.write(stem)
manifest_file.close()
