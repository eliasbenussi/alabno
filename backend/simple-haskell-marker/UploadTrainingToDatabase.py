#!/usr/bin/env python

import SimpleHTTPServer
import SocketServer
import traceback
import os
import sys
import pymysql.cursors
import subprocess

import mysqldb

db = mysqldb.MysqlConn()

# read system arguments
if len(sys.argv) != 2:
    print('Expecting an argument: filename')

input_path = sys.argv[1]

input_file = open(input_path, 'r')

for line in input_file:
    try:
        splitted = line.replace('\n', '').split('\t')
        name = splitted[0]
        text = splitted[1]
        
        sql = 'INSERT INTO `HaskellTraining`(`name`, `text`) VALUES (%s,%s)'
        values = [name, text]
        
        db.execute(sql, values)
    except:
        continue