import socket


class TcpClient:
    SERVER_PORT = 4444

    def __init__(self, ip_address, message_listener):
        self.server_ip = ip_address
        self.message_listener = message_listener
        self.out = None

    def send_message(self, message):
        try:
            if self.out is not None:
                self.out.write(message + '\n')
                self.out.flush()
        except Exception as e:
            print("TCP Client: Error sending message - ", e)

    def run(self):
        try:
            server_address = socket.gethostbyname(self.server_ip)
            with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
                sock.connect((server_address, self.SERVER_PORT))
                self.out = sock.makefile('w')
                in_stream = sock.makefile('r')
                while True:
                    server_message = in_stream.readline().strip()
                    if server_message and self.message_listener:
                        self.message_listener(server_message)
        except Exception as e:
            print("TCP Client: Error", e)
        