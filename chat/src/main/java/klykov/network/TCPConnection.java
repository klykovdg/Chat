package klykov.network;

import klykov.listener.Listener;

import java.io.*;
import java.net.*;

public class TCPConnection implements Connection {
    private Socket socket;
    private Listener listener;
    private BufferedWriter out;
    private BufferedReader in;

    public TCPConnection(Listener listener, String ip, int port) throws IOException {
        this(listener, new Socket(ip, port));
    }

    public TCPConnection(Listener listener, Socket socket) {
        this.listener = listener;
        this.socket = socket;
        serverUserCommunication();
    }

    public synchronized void sendString(String s) {
        try {
            out.write(s + "\n");
            out.flush();
        } catch (Exception e) {
            listener.onException(this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        listener.onDisconnection(this);
        try {
            socket.close();
            in.close();
            out.close();
        } catch (Exception e) {
            listener.onException(this, e);
        }
    }

    @Override
    public String toString() {
        return "Connection " + socket.getInetAddress() + ": " + socket.getPort();
    }

    private void serverUserCommunication() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            new Thread(new Communication(out, in, listener, this)).start();
        } catch (IOException e) {
            listener.onException(this, e);
        }
    }
}
