package klykov.network;

import klykov.listener.Listener;
import klykov.listener.client.Client;

import java.io.*;

public class Communication implements Runnable {
    private String message;
    private BufferedWriter out;
    private BufferedReader in;
    private Listener listener;
    private Connection connection;

    public Communication(BufferedWriter out, BufferedReader in, Listener listener, Connection connection) {
        this.out = out;
        this.in = in;
        this.listener = listener;
        this.connection = connection;
    }

    //задача выполняет три вещи:
    public void run() {
        try {
            //1) listener (пользователь или сервер) выполняет предварительные действия
            listener.onConnectionReady(connection);
            //2) listener (пользователь или сервер) принимает сообщение от сервера или пользователя
            while (!Thread.currentThread().isInterrupted() && (message = in.readLine()) != null) {
                if (message.endsWith(Client.EXIT)) {
                    listener.onReceiveString(connection, message.replace(Client.EXIT, Client.DISCONNECTED));
                    connection.disconnect();
                    Thread.currentThread().interrupt();
                } else {
                    //3) listener отправляет сообщение пользователю или серверу
                    listener.onReceiveString(connection, message);
                }
            }
        } catch (Exception e) {
            listener.onReceiveString(connection, this + " " + Client.DISCONNECTED);
            listener.onException(connection, e);
            connection.disconnect();
            Thread.currentThread().interrupt();
        }
    }
}
