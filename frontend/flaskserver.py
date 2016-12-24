import flask
import traceback
import os
import sys
import pymysql.cursors
import subprocess
import datetime
import imp
import mimetypes


# alabno/frontend
exec_dir = os.path.abspath(os.path.dirname(os.path.abspath(__file__)))

pdf_path_dir = os.path.abspath(exec_dir + '/..')

upload_path_dir = os.path.abspath(exec_dir + '/../uploads')
subprocess.call('mkdir {}'.format(upload_path_dir), shell=True)

# import mysqldb.py
mysqldb_dir = exec_dir + os.sep + '..' + os.sep + 'simple-haskell-marker'
sys.path.append(mysqldb_dir)
import mysqldb

# UTILITY FUNCTIONS ####################################################

def datetime_past_seconds(time):
    now = datetime.datetime.now()
    difference = now - time
    return difference.total_seconds()
    

def get_pdf_path(db, token):
    sql = 'SELECT path, created FROM PdfPaths WHERE token=%s'
    results = db.query(sql, [token])
    if results is None:
        print('No path given token {} found'.format(token))
        return None
    try:
        final_path = results[0]['path']
        final_timestamp = results[0]['created']
        seconds_past = datetime_past_seconds(final_timestamp)
        if seconds_past > (60 * 20): # expiration is after 20 minutes
            sql = 'DELETE FROM `PdfPaths` WHERE `token`=%s'
            db.execute(sql, [token])
            return None
        return final_path
    except:
        print(traceback.format_exc())
        return None
    
def get_upload_path(db, token):
    sql = 'SELECT path FROM UploadPaths WHERE token=%s'
    results = db.query(sql, [token])
    if results is None:
        print('No path given token {} found'.format(token))
        return None
    try:
        final = results[0]['path']
        return final
    except:
        print(traceback.format_exc())
        return None

def get_mime_string(path):
    guess = mimetypes.guess_type(path)
    if isinstance(guess, basestring):
        return guess
    else:
        return 'application/octet-stream'

# INITIALIZE DB AND SERVER #############################################

db = mysqldb.MysqlConn()

app = flask.Flask(__name__)

secure = False
if len(sys.argv) >= 2 and sys.argv[1] == 'https':
    secure = True

# SSL context
context = (exec_dir+'/selfsigned.crt', exec_dir+'/decserver.key')

# ROUTES ###############################################################

@app.route("/upload/<token>", methods=['POST'])
def upload_file(token):
    # get the path from database
    filepath = get_upload_path(db, token)
    f = request.files['file']
    f.save(upload_path_dir + os.sep + filepath)
    return 'File uploaded'

@app.route("/result/<token>")
def download_result(token):
    # get the path from database
    filepath = get_pdf_path(db, token)
    thefile = open(pdf_path_dir + os.sep + filepath, 'r')
    buff = thefile.read()
    thefile.close()
    download_name = os.path.basename(filepath)
    stem, ext = os.path.splitext(filepath)
    if ext == '.html' or ext == '.htm':
        return buff
    else:
        return flask.Response(buff, mimetype=get_mime_string(filepath), headers={"Content-Disposition": "attachment;filename={}".format(download_name)})

@app.route("/")
def serve_index():
    try:
        buff = flask.send_file('webclient/index.html')
        return buff
    except:
        print(traceback.format_exc())
        return 'error'

@app.route("/<path:filepath>")
def serve_file(filepath):
    try:
        if '..' in filepath:
            flask.abort(404)
        buff = flask.send_file('webclient/' + filepath)
        return buff
    except:
        print(traceback.format_exc())
        return 'error'

if __name__ == "__main__":
    while True:
        try:
            if secure:
                app.run(host='0.0.0.0', port=4443, debug=False, ssl_context=context)
            else:
                app.run(host='0.0.0.0', port=8000, debug=False)
        except:
            print(traceback.format_exc())