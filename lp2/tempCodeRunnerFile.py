
class Client:
    def __init__(self):
        self.sums = [0.0] * 40
        self.tcp_client = None

    @staticmethod
    def main():
        client = Client()