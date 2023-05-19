package sockets.caso3.lp1;

import java.io.*;
import java.net.Socket;

public class TCPServerThread extends Thread {

    private final Socket client;
    private final TCPServer tcpServer;
    public int clientID;
    public PrintWriter out;
    public BufferedReader in;
    TCPServerThread[] connectedClients;

    public TCPServerThread(Socket client, TCPServer tcpServer, int clientID, TCPServerThread[] connectedClients) {
        this.client = client;
        this.tcpServer = tcpServer;
        this.clientID = clientID;
        this.connectedClients = connectedClients;
    }

    public void run() {
        try {
            try {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                TCPServer.OnMessageReceived messageListener = tcpServer.getMessageListener();
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                while (true) {
                    String message = in.readLine();
                    if (message != null && messageListener != null) {
                        messageListener.messageReceived(message);
                    }
                }
            } catch (Exception e) {
                System.out.println("TCP Server: Error " + e);
            } finally {
                client.close();
            }

        } catch (Exception e) {
            System.out.println("TCP Server: Error " + e);
        }
    }

    public void sendMessage(String message) {
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }

    public void stopClient() {
        try {
            client.close();
        } catch (Exception e) {
            System.out.println("TCP Server: Error " + e);
        }
    }
}
