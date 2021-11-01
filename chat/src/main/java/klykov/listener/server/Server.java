package klykov.listener.server;

import klykov.aids.*;
import klykov.listener.Listener;
import klykov.listener.client.Client;
import klykov.network.*;

import java.io.*;
import java.net.*;
import java.util.*;

import static klykov.admin.Admin.ADMIN;

public class Server implements Listener {
    private List<Connection> connections = new ArrayList<>();
    //для метода checkWhoSent
    private Connection againMe = null;
    private String message;
    private File chatLog = new File(CHAT_LOG);
    private FileWriter chatWriter;
    public final static String CHAT_LOG = "chat.log";

    public void startServer() {
        Address address = new Address();
        checkExistence(chatLog);

        try (ServerSocket serverSocket = new ServerSocket(address.getPort())) {
            while (true) {
                new TCPConnection(this, serverSocket.accept());
            }
        } catch (IOException e) {
            ADMIN.log(e);
        } finally {
            try {
                chatWriter.close();
                ADMIN.close();
            } catch (IOException e) {
                ADMIN.log(e);
            }
        }
    }

    @Override
    public synchronized void onConnectionReady(Connection connection) {
        connections.add(connection);
    }

    @Override
    public synchronized void onReceiveString(Connection connection, String value) {
        sendStringToAllConnections(connection, value);
    }

    @Override
    public synchronized void onDisconnection(Connection connection) {
        connections.remove(connection);
    }

    @Override
    public synchronized void onException(Connection connection, Exception e) {
        ADMIN.log(e, connection);
    }

    private void sendStringToAllConnections(Connection connection, String s) {
        message = checkWhoSent(connection, s);
        try {
            chatWriter.write(message + "\n");
            chatWriter.flush();
        } catch (IOException e) {
            ADMIN.log(e);
        }
        connections.forEach(v -> {
            if (!v.equals(connection)) {
                v.sendString(message);
            }
        });
    }

    //метод отслеживает, кто из пользователей отправляет сообщение.
    //Елси два (и более) сообщения подряд приходят от одного пользователя,
    //метод удаляет имя, чтобы избежать дублирования
    private String checkWhoSent(Connection connection, String s) {
        if (againMe == null || !againMe.equals(connection)) {
            againMe = connection;
        } else {
            if (!s.endsWith(Client.CONNECTED) && !s.endsWith(Client.DISCONNECTED)) {
                return s.substring(s.indexOf(")") + 2);
            }
        }
        return s.replaceFirst("\\) ", ")\n");
    }

    private void checkExistence(File chatLog) {
        try {
            if (!chatLog.exists()) {
                chatLog.createNewFile();
            }
            chatWriter = new FileWriter(chatLog, true);
        } catch (IOException e) {
            ADMIN.log(e);
        }
    }
}