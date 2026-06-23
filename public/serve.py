import http.server
import socketserver
import mimetypes

PORT = 8080
mimetypes.add_type('application/javascript', '.js')
mimetypes.add_type('text/css', '.css')
mimetypes.add_type('image/svg+xml', '.svg')

Handler = http.server.SimpleHTTPRequestHandler
with socketserver.TCPServer(("127.0.0.1", PORT), Handler) as httpd:
    print("Serving at http://127.0.0.1:8080")
    httpd.serve_forever()
