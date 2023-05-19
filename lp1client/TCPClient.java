package sockets.caso1.lp2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient {
    private final String serverIP;
    public static final int SERVER_PORT = 4444;
    private final OnMessageReceived messageListener;

    private PrintWriter out;

    public TCPClient(String ipAddress, OnMessageReceived listener) {
        serverIP = ipAddress;
        messageListener = listener;
    }

    public void sendMessage(String message) {
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }

    public void run() {
        try {
            InetAddress serverAddress = InetAddress.getByName(serverIP);
            try (Socket socket = new Socket(serverAddress, SERVER_PORT)) {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (true) {
                    String serverMessage = in.readLine();
                    if (serverMessage != null && messageListener != null) {
                        messageListener.messageReceived(serverMessage);
                    }
                }
            } catch (Exception e) {
                System.out.println("TCP Client: Error " + e);
            }
        } catch (Exception e) {
            System.out.println("TCP Client: Error " + e);
        }
    }

    public interface OnMessageReceived {
        void messageReceived(String message);
    }
}
