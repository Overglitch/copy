import java.util.Scanner;

public class Server {

    private TCPServer tcpServer;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    void start() {
        new Thread(() -> {
            tcpServer = new TCPServer(message -> {
                synchronized (this) {
                    receiveFromServer(message);
                }
            });
            tcpServer.run();
        }).start();

        System.out.println("Suma de Riemann de un polinomio");
        System.out.print("Ejemplo: enviar -7x^1+8x^-2+14x^3-7 5 10 10000\n");

        listening();

        tcpServer.stopServer();
        System.exit(0);

    }

    int clientCount = 0;
    double[] clientResponses = new double[20];
    double totalSum = 0;

    void receiveFromServer(String message) {
        if (message != null && !message.equals("")) {
            if (message.trim().contains("Resultado")) {
                String[] parts = message.split("\\s+");
                double data = Double.parseDouble(parts[1]);
                if (data > 0) {
                    clientResponses[clientCount] = data;
                    System.out.println("Cliente [" + clientCount + "] = " + clientResponses[clientCount]);
                    System.out.println("Numero de clientes: " + this.tcpServer.clientCount);
                    clientCount++;

                    if (clientCount == this.tcpServer.clientCount) {
                        for (int i = 0; i < clientCount; i++) {
                            System.out.println("Cliente [" + i + "] = " + clientResponses[i]);
                            totalSum += clientResponses[i];
                        }
                        System.out.println("\nArea total = " + totalSum);
                        clientCount = 0;
                        totalSum = 0;
                    }
                }
            }
        }
    }

    void sendToServer(String message) {
        if (message != null) {
            if (message.trim().contains("enviar")) {
                if (tcpServer != null) {
                    tcpServer.sendMessageToTCPServer(message);
                }
            } else {
                System.out.println("El mensaje no contiene la palabra 'enviar'");
            }
        }
    }
    void listening() {
        Scanner scanner = new Scanner(System.in);
        String command;

        do {
            command = scanner.nextLine();
            sendToServer(command);
        } while (!command.equals("salir"));
    }
}
