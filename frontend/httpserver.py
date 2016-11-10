import SimpleHTTPServer
import SocketServer
import BaseHTTPServer
import ssl
import os
import sys

if len(sys.argv) > 1 and sys.argv[1] == 'https':
    exec_dir = os.path.abspath(os.path.dirname(os.path.abspath(__file__)))

    https = BaseHTTPServer.HTTPServer(('', 4443), SimpleHTTPServer.SimpleHTTPRequestHandler)
    https.socket = ssl.wrap_socket (https.socket, certfile=(exec_dir + os.sep + 'server.pem'), server_side=True)
    https.serve_forever()
    
else:
    PORT = 8000

    Handler = SimpleHTTPServer.SimpleHTTPRequestHandler

    httpd = SocketServer.TCPServer(("", PORT), Handler)

    print "serving at port", PORT
    httpd.serve_forever()
