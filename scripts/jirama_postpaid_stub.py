import json
from http.server import BaseHTTPRequestHandler, HTTPServer


class JiramaStub(BaseHTTPRequestHandler):
    def do_POST(self):
        if self.path != "/jderest/orchestrator/ORCH_WS_1_3_ClientPostpaid":
            self.send_error(404)
            return

        try:
            length = int(self.headers.get("Content-Length", "0"))
            request = json.loads(self.rfile.read(length))
        except ValueError:
            self.send_error(400)
            return

        if not isinstance(request, dict):
            self.send_error(400)
            return

        if request.get("FactureReference") != "DEMO-INVOICE-005":
            self.send_error(404)
            return

        body = json.dumps({
            "NumeroClient": 12345678,
            "Compte": "00123456789",
        }).encode("utf-8")

        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)


HTTPServer(("127.0.0.1", 8090), JiramaStub).serve_forever()