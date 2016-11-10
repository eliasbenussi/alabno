import SimpleHTTPServer
import SocketServer
import traceback
import os
import sys
import pymysql.cursors
import subprocess

class MysqlConn:
    def __init__(self):
        pass
        
    def dbconnect(self):
        try:
            connection = pymysql.connect(host='tc.jstudios.ovh',
                                user='python',
                                password='python',
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
