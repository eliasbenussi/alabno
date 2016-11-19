import flask
import traceback
import os
import sys
import pymysql.cursors
import subprocess
import datetime
import imp


# alabno/frontend
exec_dir = os.path.abspath(os.path.dirname(os.path.abspath(__file__)))

pdf_path_dir = os.path.abspath(exec_dir + '/..')

upload_path_dir = os.path.abspath(exec_dir + '/../uploads')
subprocess.call('mkdir {}'.format(upload_path_dir), shell=True)

# import mysqldb.py
mysqldb_dir = exec_dir + os.sep + '..' + os.sep + 'simple-haskell-marker'
sys.path.append(mysqldb_dir)
import mysqldb


def datetime_past_seconds(time):
    now = datetime.datetime.now()
    difference = now - time
    return difference.total_seconds()
    

def get_pdf_path(db, token):
    sql = 'SELECT path, created FROM PdfPaths WHERE token=%s'
    results = db.query(sql, [token])
    print('results')
    print(results)
    if results is None:
        print('No path given token {} found'.format(token))
        return None
    try:
        final_path = results[0]['path']
        final_timestamp = results[0]['created']
        seconds_past = datetime_past_seconds(final_timestamp)
        print('seconds past {}'.format(seconds_past))
        if seconds_past > (60 * 20): # expiration is after 1 hour
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
    print('results')
    print(results)
    if results is None:
        print('No path given token {} found'.format(token))
        return None
    try:
        final = results[0]['path']
        print('final is ')
        print(final)
        return final
    except:
        print(traceback.format_exc())
        return None

db = mysqldb.MysqlConn()

app = flask.Flask(__name__)

secure = False
if len(sys.argv) >= 2 and sys.argv[1] == 'https':
    secure = True

# SSL context
context = ('selfsigned.crt', 'decserver.key')

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
    return buff

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
    if secure:
        app.run(host='0.0.0.0', port=4443, debug=False, ssl_context=context)
    else:
        app.run(host='0.0.0.0', port=8000, debug=False)
