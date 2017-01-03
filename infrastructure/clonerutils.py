import os
import subprocess

def discover_commit_hash(target):
    target = os.path.abspath(target)
    previous_directory = os.path.abspath(os.getcwd())
    
    os.chdir(target)
    
    cmd = 'git rev-parse --verify HEAD'
    
    output = ''
    
    proc = subprocess.Popen(cmd, stdout=subprocess.PIPE, shell=True)
    for line in iter(proc.stdout.readline,''):
        output = line
        break
    
    if len(output) > 0 and output[-1] == '\n':
        output = output[:-1]
    
    os.chdir(previous_directory)
    
    return output