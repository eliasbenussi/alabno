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
encrypted_conf_file_path = os.path.abspath(conf_file_path + '.gpg')

def ask_authentication(force=False):
    # If forcing, remove existing configuration first
    if force:
        cmd = 'rm {}'.format(encrypted_conf_file_path)
        subprocess.call(cmd, shell=True)
    
    # Check if conf file exists. If yes, exit
    if os.path.isfile(encrypted_conf_file_path):
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
    
    # encrypt file using gpg
    print('The configuration file will now encrypted and stored. Please enter a password to encrypt and decrypt the configuration file')
    cmd = 'gpg -c {}'.format(conf_file_path)
    code = subprocess.call(cmd, shell=True)
    if code != 0:
        print('Error, gpg encryption tool returned code {}'.format(code))
    
    # remove the clear configuration file
    cmd = 'rm -rf {}'.format(conf_file_path)
    subprocess.call(cmd, shell=True)

def get_auth_string():
    if os.path.isfile(encrypted_conf_file_path):
        passphrase = os.environ['ALABNOGITAUTHPASS']
        if not passphrase:
            print('cannot decode gitauth file: no valid passphrase')
            return ''
        cmd = ['gpg', '--passphrase', passphrase, encrypted_conf_file_path]
        code = subprocess.call(cmd, shell=False)
        if code != 0:
            print('could not decode gitauth file: wrong passphrase!')
            return ''
        
        conf_file = open(conf_file_path, 'r')
        content = conf_file.read()
        conf_file.close()
        words = content.split('\n')
        result = ''
        if words and (len(words) == 2):
            username = words[0]
            password = words[1]
            result = username + ':' + password
        
        # remove clear file
        cmd = 'rm -rf {}'.format(conf_file_path)
        subprocess.call(cmd, shell=True)
        
        return result
    else:
        return ''

# The authentication password for the file is set in the environment
# variable at server startup. Therefore the variable containing the password
# for decryption will be only visible by the subprocesses.
def set_auth_passphrase():
    if os.path.isfile(encrypted_conf_file_path):
        valid = False
        while not valid:
            print('Please enter the decryption key of the git authorization configuration file')
            a_pass = getpass.getpass()
            cmd = ['gpg', '--passphrase', a_pass, encrypted_conf_file_path]
            code = subprocess.call(cmd, shell=False)
            valid = code == 0
            if code != 0:
                print('Password incorrect. Please retry')
        os.environ['ALABNOGITAUTHPASS'] = a_pass
        # remove clear file
        cmd = 'rm -rf {}'.format(conf_file_path)
        subprocess.call(cmd, shell=True)
    
def format_git_url(giturl):
    authstring = get_auth_string()
    if (not authstring) or (authstring == ''):
        return giturl
    
    words = giturl.split('https://', 1)
    if words and (len(words) == 2):
        return 'https://' + authstring + '@' + words[1]
    return giturl
