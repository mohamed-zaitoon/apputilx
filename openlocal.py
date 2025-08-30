#!/usr/bin/env python3
import http.server
import socketserver
import webbrowser
import sys
import os

def main():
    # Require at least a port
    if len(sys.argv) < 2:
        print("Usage: openlocal <port>")
        sys.exit(1)

    port = int(sys.argv[1])
    # Use current directory if no folder given
    folder = sys.argv[2] if len(sys.argv) >= 3 else os.getcwd()

    if not os.path.isdir(folder):
        print(f"Folder not found: {folder}")
        sys.exit(1)

    os.chdir(folder)

    handler = http.server.SimpleHTTPRequestHandler

    with socketserver.TCPServer(("", port), handler) as httpd:
        url = f"http://localhost:{port}"
        print(f"Serving {folder} at {url}")
        webbrowser.open(url)
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            print("\nServer stopped.")

if __name__ == "__main__":
    main()