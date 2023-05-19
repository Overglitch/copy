from tcpclient import TcpClient
from riemann_sum import RiemannSum, RiemannSumThread


class Client:
    def __init__(self):
        self.sums = [0.0] * 40
        self.tcp_client = None

    @staticmethod
    def main():
        client = Client()
        client.start()

    def start(self):
        self.tcp_client = TcpClient("192.168.1.14", self.receive_from_client)
        self.tcp_client.run()

    def receive_from_client(self, input_client):
        print("Mensaje recibido:", input_client)
        if "enviar" in input_client.strip():
            parts = input_client.split(" ")
            polynomial_expression = parts[1]
            a = float(parts[2])
            b = float(parts[3])
            n = int(parts[4])

            print(f"Variables: a={a} b={b} n={n}\n")
            self.process(polynomial_expression, a, b, n)

    def send_to_client(self, message):
        if self.tcp_client:
            self.tcp_client.send_message(message)

    def process(self, polynomial, a, b, n):
        riemann_sum = RiemannSum(a, b, n, polynomial)
        num_threads = 6
        n_i = n / num_threads
        delta_x_i = (b - a) / num_threads
        threads = []

        for i in range(num_threads):
            start = a + i * delta_x_i
            end = a + (i + 1) * delta_x_i
            num_intervals = n_i

            if i == num_threads - 1:
                end = b
                num_intervals = n - n_i * (num_threads - 1)

            thread = RiemannSumThread(riemann_sum.terms, riemann_sum.coefficients, riemann_sum.exponents,
                                      start, end, num_intervals, i, self.sums)
            thread.start()
            threads.append(thread)

        for thread in threads:
            thread.join()

        partial_sum = sum(self.sums)

        print("\nResultado del cliente:", partial_sum)
        self.send_to_client("Resultado " + str(partial_sum))


if __name__ == "__main__":
    Client.main()