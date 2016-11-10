import SimpleHTTPServer
import SocketServer
import traceback
import os
import sys
import pymysql.cursors
import subprocess

def get_pass():
    # alabno/simple-haskell-marker
    exec_dir = os.path.abspath(os.path.dirname(os.path.abspath(__file__)))
    
    # alabno/
    alabno_dir = os.path.abspath(exec_dir + os.sep + '..')
    
    # alabno/dbpass.txt
    pass_path = os.path.abspath(alabno_dir + os.sep + 'dbpass.txt')
    
    pass_file = open(pass_path, 'r')
    
    the_pass = ''
    for line in pass_file:
        the_pass = line
        break
    pass_file.close()
    
    return the_pass

class MysqlConn:
    def __init__(self):
        pass
        
    def dbconnect(self):
        try:
            the_password = get_pass()
            connection = pymysql.connect(host='tc.jstudios.ovh',
                                user='python',
                                password=the_password,
                                db='Automarker',
                                charset='utf8mb4',
                                cursorclass=pymysql.cursors.DictCursor)
            return connection
        except:
            print(traceback.format_exc())
            
    def query(self, sql, args=None):
        try:
            connection = self.dbconnect()
            with connection.cursor() as cursor:
                cursor.execute(sql, args)
                result = cursor.fetchall()
                return result
        except:
            print(traceback.format_exc())
            self.dbconnect()
            return None
    
    def execute(self, sql, args=None):
        try:
            connection = self.dbconnect()
            with connection.cursor() as cursor:
                cursor.execute(sql, args)
                connection.commit()
                return
        except:
            print(traceback.format_exc())
            self.dbconnect()
            return None
