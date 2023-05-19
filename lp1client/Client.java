package sockets.caso1.lp2;

class Client {

    private final double[] sums = new double[40];
    private TCPClient tcpClient;

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    void start() {
        new Thread(
                () -> {
                    tcpClient = new TCPClient("127.0.0.1", this::receiveFromClient);
                    tcpClient.run();
                }
        ).start();

    }

    void receiveFromClient(String input) {
        System.out.println("Mensaje recibido: " + input);
        if (input.trim().contains("enviar")) {
            String[] parts = input.split(" ");

            String polynomialExpression = parts[1];
            double a = Double.parseDouble(parts[2]);
            double b = Double.parseDouble(parts[3]);
            int n = Integer.parseInt(parts[4]);

            System.out.printf("Variables: a=%f b=%f n=%d\n\n", a, b, n);
            process(polynomialExpression, a, b, n);
        }
    }

    void sendToClient(String message) {
        if (tcpClient != null) {
            tcpClient.sendMessage(message);
        }
    }

    void process(String polynomial, double a, double b, int n) {
        RiemannSum riemannSum = new RiemannSum(polynomial, a, b, n);
        int T = 6;
        int n_i = n / T;
        double delta_x_i = (b - a) / T;
        Thread[] threads = new Thread[T];

        for (int i = 0; i < T; i++) {
            double start = a + i * delta_x_i;
            double end = a + (i + 1) * delta_x_i;
            int numIntervals = n_i;

            if (i == T - 1) {
                end = b;
                numIntervals = n - n_i * (T - 1);
            }

            threads[i] = new RiemannSumThread(riemannSum.terms, riemannSum.coefficients, riemannSum.exponents, start, end, numIntervals, i);
            threads[i].start();
        }

        for (int i = 0; i < T; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException ex) {
                System.out.println("Error: " + ex);
            }
        }

        double partialSum = 0.0;

        for (int i = 0; i < T; i++) {
            partialSum += sums[i];
        }

        System.out.println("\nResultado del cliente: " + partialSum);
        sendToClient("Resultado " + partialSum);
    }

    public class RiemannSumThread extends Thread {
        private final RiemannSum riemannSum;
        private final double start;
        private final double end;
        private final int numIntervals;
        private final int id;

        RiemannSumThread(String[] terms, String[] coefficients, String[] exponents, double start, double end, int numIntervals, int id) {
            riemannSum = new RiemannSum(terms, coefficients, exponents, start, end, numIntervals);
            this.start = start;
            this.end = end;
            this.numIntervals = numIntervals;
            this.id = id;
        }

        public void run() {
            sums[id] = riemannSum.calculate();
            System.out.printf("Hilo (%d) | a=%.4f - b=%.4f - n=%d - area=%f\n", id+1, start, end, numIntervals, sums[id]);
        }
    }
}
