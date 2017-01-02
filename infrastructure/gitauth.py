import os
import getpass
import subprocess
import urllib

# alabno/infrastructure/
exec_dir = os.path.abspath(os.path.dirname(os.path.abspath(__file__)))
# alabno/
alabno_dir = os.path.abspath(exec_dir + os.sep + '..')

conf_file_name = 'gitauth.txt'
conf_file_path = alabno_dir + os.sep + 'config' + os.sep + conf_file_name

def ask_authentication(force=False):
    # If forcing, remove existing configuration first
    if force:
        cmd = 'rm {}'.format(conf_file_path)
        subprocess.call(cmd, shell=True)
    
    # Check if conf file exists. If yes, exit
    if os.path.isfile(conf_file_path):
        return
    
    username = ''
    password = ''
    print('Please insert your gitlab authentication details. These will be used to clone the student repositories and model answers. If they are left blank, only publicly accessible repositories can be cloned.')
    print('Username:')
    username = raw_input()
    # Need to use URL encoding
    username = urllib.quote(username)
    password = getpass.getpass()
    password = urllib.quote(password)
    
    if username == '':
        return
    
    conf_file = open(conf_file_path, 'w')
    conf_file.write(username)
    conf_file.write('\n')
    conf_file.write(password)
    conf_file.close()

def get_auth_string():
    if os.path.isfile(conf_file_path):
        conf_file = open(conf_file_path, 'r')
        content = conf_file.read()
        conf_file.close()
        words = content.split('\n')
        if words and (len(words) == 2):
            username = words[0]
            password = words[1]
            return username + ':' + password
    else:
        return ''
    
def format_git_url(giturl):
    words = giturl.split('https://', 1)
    if words and (len(words) == 2):
        return 'https://' + get_auth_string() + '@' + words[1]
    return giturl
