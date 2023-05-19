import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    protected int clientCount = 0;
    public static final int SERVER_PORT = 7777;
    private final OnMessageReceived messageListener;
    private final TCPServerThread[] connectedClients = new TCPServerThread[10];
    public TCPServer(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
    }
    public OnMessageReceived getMessageListener() {
        return this.messageListener;
    }

    public void sendMessageToTCPServer(String message) {
        if (clientCount == 0) {
            System.out.println("No hay clientes conectados");
            System.exit(0);
        }
        String[] parts = message.split(" ");
        String command = parts[0];
        String polynomial = parts[1];
        double a = Double.parseDouble(parts[2]);
        double b = Double.parseDouble(parts[3]);
        int n = Integer.parseInt(parts[4]);
        double interval = (b - a) / clientCount;
        int numIntervals = n / clientCount;

        for (int i = 1; i <= clientCount; i++) {
            String start = Double.toString(a + (i - 1) * interval);
            String end = Double.toString(a + i * interval);
            String num = Integer.toString(numIntervals);

            if (i == clientCount) {
                end = Double.toString(b);
                num = Integer.toString(n - (clientCount - 1) * numIntervals);
            }

            String newMessage = command + " " + polynomial + " " + start + " " + end + " " + num;

            connectedClients[i].sendMessage(newMessage);
            System.out.println("Enviado al Cliente " + i);
        }
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)){
            while (true) {
                Socket client = serverSocket.accept();
                clientCount++;
                System.out.println("Clientes totales: " + clientCount);
                connectedClients[clientCount] = new TCPServerThread(client, this, clientCount, connectedClients);
                connectedClients[clientCount].start();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void stopServer() {
        for (int i = 1; i <= clientCount; i++) {
            connectedClients[i].stopClient();
        }
    }

    public interface OnMessageReceived {
        void messageReceived(String message);
    }
}
