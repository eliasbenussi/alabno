import flask
import traceback
import os
import sys
import pymysql.cursors

class MysqlConn:
    def __init__(self):
        self.connection = None
        
    def dbconnect(self):
        try:
            self.connection = pymysql.connect(host='tc.jstudios.ovh',
                                user='python',
                                password='python',
                                db='Automarker',
                                charset='utf8mb4',
                                cursorclass=pymysql.cursors.DictCursor)
        except:
            print(traceback.format_exc())
            
    def query(self, sql):
        try:
            self.dbconnect()
            with self.connection.cursor() as cursor:
                cursor.execute(sql)
                result = cursor.fetchall()
                return result
        except:
            print(traceback.format_exc())
            self.dbconnect()
            return None
    
    def execute(self, sql):
        try:
            self.dbconnect()
            with self.connection.cursor() as cursor:
                cursor.execute(sql)
                self.connection.commit()
                return
        except:
            print(traceback.format_exc())
            self.dbconnect()
            return None

def get_pdf_path(db, token):
    sql = 'SELECT path FROM PdfPaths WHERE token="{}"'.format(token)
    results = db.query(sql)
    print('results')
    print(results)
    if results is None:
        print('No path given token {} found'.format(token))
        return None
    try:
        final = results[0]['path']
        print('final is ')
        print(final)
        # now delete the entry
        sql = 'DELETE FROM `PdfPaths` WHERE `token`="{}"'.format(token)
        db.execute(sql)
        return final
    except:
        print(traceback.format_exc())
        return None

db = MysqlConn()

app = flask.Flask(__name__)

exec_dir = os.path.abspath(os.path.dirname(os.path.abspath(__file__)))

pdf_path_dir = os.path.abspath(exec_dir + '/..')

secure = False
if len(sys.argv) >= 2 and sys.argv[1] == 'https':
    secure = True

# SSL context
context = ('selfsigned.crt', 'decserver.key')

@app.route("/upload/<token>")
def upload_file(token):
    return 'post with token {}'.format(token)

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
